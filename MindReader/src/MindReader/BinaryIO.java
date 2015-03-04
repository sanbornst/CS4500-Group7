package MindReader;

import info.monitorenter.gui.chart.ITrace2D;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.nio.ByteBuffer;

//TODO Migrate prototype code (in progress...)

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
    private int headerLength;                 // length of the header
    private float scanRate;                   // sample rate of file
    private ArrayList<ChannelInfo> channels;  // arrayList for channel info
    
    BinaryIO(){
        this.source = null;
        this.headerLength = -1;
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
            this.headerLength = byte1;
            System.out.println("   Header Length: " + this.headerLength);
        } else {
            // invalid file, throw exception
            throw new IOException("Not a MindWare File!");
        }
        
        // get the header information from stream (less the first eight bytes)
        byte[] header = new byte[this.headerLength - 8];
        this.source.read(header, 0, this.headerLength - 8);
        bb = ByteBuffer.wrap(header);
        
        // length of raw channel list
        int channelNameLength = bb.getInt();
        
        // ignore the raw channel list (information is located later)
        bb.position(bb.position() + channelNameLength);
        
        // ignore the hardware configuration information as it is unimportant
        // TODO: See if there's anything useful in here
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
     * 
     * @throws IOException
     */
    public void read(ITrace2D channel, int id, int start, int length,
            int frequency) throws IOException {
        // TODO Auto-generated method stub

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
