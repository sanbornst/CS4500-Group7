package MindReader;

import info.monitorenter.gui.chart.ITrace2D;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.nio.ByteBuffer;

/**
 * Binary File IO Object
 * 
 * Handles reading from mindware .mw files, parsing them into individual
 * channels that can be read from.
 * 
 * @author Christopher Curreri
 */
public class BinaryIO implements FileIO {
    private RandomAccessFile source;          // input file stream
    private int startOfData;                  // length of the header
    private float scanRate;                   // sample rate of file
    private ArrayList<ChannelInfo> channels;  // arrayList for channel info
    
    // CONSTANTS
    private static final int dataSize = 2;
    
    BinaryIO(){
        this.source = null;
        this.startOfData = -1;
        this.scanRate = -1;
        this.channels = new ArrayList<ChannelInfo>();
    }
    
    /**
     * reads file header information
     * 
     * @throws IOException
     */
    private void readHeader() throws IOException{
        byte[] firstBytes = new byte[8];
        ByteBuffer bb;
        int headerLength = 0;
        
        System.out.println("Reading Header...");
        
        // read the first eight bytes from file and store in a ByteBuffer
        this.source.read(firstBytes, 0, 8);
        bb = ByteBuffer.wrap(firstBytes);
        
        // pull two ints out of the byte buffer (8 bytes)
        int byte1 = bb.getInt();
        int byte2 = bb.getInt();
        
        // verify file is a MindWare file (byte2 = byte1 - 4)
        if (byte2 == byte1 - 4){
            // valid file, store header length and continue
            headerLength = byte1;
            this.startOfData = headerLength + 4;
            System.out.println("   Header Length: " + headerLength);
        } else {
            // invalid file, throw exception
            throw new IOException("Not a MindWare File!");
        }
        
        // get the header information from stream (less the first eight bytes)
        byte[] header = new byte[headerLength - 8];
        this.source.read(header, 0, headerLength - 8);
        bb = ByteBuffer.wrap(header);
        
        // length of raw channel list
        int channelNameLength = bb.getInt();
        
        // ignore the raw channel list (information is located later)
        bb.position(bb.position() + channelNameLength);
        
        // ignore the hardware configuration information as it is unimportant
        bb.position(bb.position() + 4);
        
        // get number of channels
        int numChannels = bb.getInt();
        
        System.out.println("   Number of channels: " + numChannels);

        int channelLength = 0;
        
        // get all the channel information
        for (int i = 0; i < numChannels; i++){
            // get the channel information
            channelLength = readChannel(i, bb.duplicate());
            
            // ignore channelLength bytes from main buffer
            bb.position(bb.position() + channelLength);
        }
        
        // get the scan rate for the file
        this.scanRate = bb.getFloat();
        System.out.println("   Scan rate: " + this.scanRate);
    }
    
    /**
     * reads a single channel's worth of information and adds it
     * to the list of channels.
     * 
     * @param id the id to set to the channel
     * @param bb the ByteBuffer to read from
     * 
     * @return the number of bytes read
     */
    private int readChannel(int id, ByteBuffer bb){
        String name = null;
        byte[] bName = null;
        int nameLength = -1;
        
        // Get the channel name length
        nameLength = bb.getInt();
        bName = new byte[nameLength];
        
        // Get the bytes that make up the channel name
        bb.get(bName, 0, nameLength);
        
        // Attempt to convert them into an ASCII string
        try{
            name = new String(bName, "US-ASCII").trim();
        } catch (Exception e) {
            // TODO: Meaningful error here
        }
        
        // Pull the rest of the channel data
        // TODO: Put useful channel data into ChannelInfo class
        bb.getFloat(); // upper limit
        bb.getFloat(); // lower limit
        bb.getFloat(); // range
        bb.getShort(); // polarity
        bb.getFloat(); // gain
        bb.getShort(); // coupling
        bb.getShort(); // hardware config
        bb.getFloat(); // scale multiplier
        bb.getFloat(); // scale offset
        
        System.out.println("      Channel Name: \"" + name + "\"");
        
        // add Channel to list
        this.channels.add(new ChannelInfo(id, ChannelType.BINARY, name));
        
        // length bytes + name bytes + rest bytes
        return 4 + nameLength + 30;
    }
    
    /**
     * opens the file, verifies that it is a MindWare file, and reads
     * all of the necessary header information
     * 
     * @param path the path to the input file
     * 
     * @throws IOException
     */
    public void open(String path) throws IOException {
        // ensure file isn't already open
        if (this.source != null){ throw new IOException("File already open!"); }
        
        // attempt to get a file handle
        this.source = new RandomAccessFile(path, "r");
        
        
        // verify and read the header
        readHeader();
    }
    
    /**
     * closes the currently open file
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        if (this.source == null){ throw new IOException("Open file first!"); }
        
        this.source.close();
    }

    /**
     * sequentially reads channel data from the file
     * 
     * @param channel the ITRace2D instance to read data into
     * @param id the id of the channel to read from
     * @param start the position to start reading from
     * @param length how many entries to read
     * @param freq how many points to average together
     * 
     * @throws IOException
     */
    public void read(ITrace2D channel, int id, int start, int length, int freq) throws IOException {
        
        ByteBuffer bb;
        double point;
        double x;
        
        System.out.println("   Reading from channel " + id);
        System.out.println("           Start: " + start + "ms");
        System.out.println("          Length: " + length + "ms");
        System.out.println("      Data/Point: " + freq);
        
        // move the file position to the end of the header
        this.source.getChannel().position(this.startOfData + start * this.channels.size());
        
        byte[] data = new byte[2];
        
        // for each row
        for(int i = 0; i < length / freq; i++){
            
            // average data together (rolling average)
            point = 0;
            for (int n = 0; n < freq; n++){
                // skip data for channels before the one we want
                this.source.skipBytes(BinaryIO.dataSize * id);
                
                // get the value out of the file
                this.source.read(data, 0, BinaryIO.dataSize);
                bb = ByteBuffer.wrap(data);
                
                // update average
                point = (bb.getShort() + n * point) / (n + 1);

                // skip data for channels after the one we want
                this.source.skipBytes(BinaryIO.dataSize * (this.channels.size() - (id + 1)));
            }
            
            // adjust point by channel scale
            // TODO: Don't hard-code this... use channel specific scale
            point = point * 0.000305;
            
            // adjust x value to account for averaging (if any)
            x = i - (freq - 1) / 2;
            
            // add point to trace
            channel.addPoint(x, point);
        }
        
        System.out.println("   Done reading data...");
    }

    /**
     * gets all of the channel information
     * 
     * @throws IOEXception
     */
    public ArrayList<ChannelInfo> getChannels() throws IOException {
        if (this.source == null){ throw new IOException("Open file first!"); }
        
        return this.channels;
    }
    
    /**
     * gets the scan rate of the file
     * 
     * @return the sampling rate of the file
     * 
     * @throws IOException
     */
    public float getScanRate() throws IOException {
        if (this.source == null){ throw new IOException("Open file first!"); }
        
        return this.scanRate;
    }

}
