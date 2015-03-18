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
    private RandomAccessFile source;                  // input file stream
    private int startOfData;                          // length of the header
    private long dataLength;                          // the size of the data segment
    private float scanRate;                           // sample rate of file
    private ArrayList<ExtendedChannelInfo> channels;  // arrayList for channel info
    
    // CONSTANTS
    // The size of each data point in bytes
    private static final int intSize = 4;  // the size of an int in the file
    private static final int dataSize = 2; // the size of each data point (short)
    
    // Constructor
    BinaryIO(){
        this.source = null;
        this.startOfData = -1;
        this.dataLength = -1;
        this.scanRate = -1;
        this.channels = new ArrayList<ExtendedChannelInfo>();
    }
    
    /**
     * reads file header information
     * 
     * @throws IOException
     */
    private void readHeader() throws IOException{
        // to hold first two bytes of the file
        byte[] firstBytes = new byte[BinaryIO.intSize * 2];
        int byte1 = 0; int byte2 = 0;
        // byte buffer to hold raw binary data
        ByteBuffer bb;
        // hold various pieces of file/channel info
        int headerLength = 0;
        int numChannels = 0;
        int channelLength = 0;
        int channelNameLength = 0;
        
        System.out.println("Reading Header...");
        
        // read the first eight bytes from file and store in a ByteBuffer
        this.source.read(firstBytes, 0, BinaryIO.intSize * 2);
        bb = ByteBuffer.wrap(firstBytes);
        
        // pull two ints out of the byte buffer (8 bytes)
        byte1 = bb.getInt(); byte2 = bb.getInt();
        
        // verify file is a MindWare file (byte2 = byte1 - 4)
        if (byte2 == byte1 - BinaryIO.intSize){
            // valid file, store header length and continue
            headerLength = byte1;
            
            // now have enough info to figure out where data segment starts
            // so set that now
            this.startOfData = headerLength + BinaryIO.intSize;
            
            System.out.println("   Header Length: " + headerLength + " bytes");
        } else {
            // invalid file, throw exception
            throw new IOException("Not a MindWare File!");
        }
        
        // get the header information from stream (less the first eight bytes)
        byte[] header = new byte[headerLength - BinaryIO.intSize * 2];
        this.source.read(header, 0, headerLength - BinaryIO.intSize * 2);
        bb = ByteBuffer.wrap(header);
        
        // get the length of raw channel list
        channelNameLength = bb.getInt();
        
        // ignore the raw channel list (information is located later)
        bb.position(bb.position() + channelNameLength);
        
        // ignore the hardware configuration information as it is unimportant
        bb.position(bb.position() + BinaryIO.intSize);
        
        // get number of channels
        numChannels = bb.getInt();
        
        System.out.println("   # of channels: " + numChannels);
        
        // we now have enough information to figure out how big the data segment is, so do it.
        this.dataLength = (this.source.getChannel().size() - headerLength) / (numChannels * BinaryIO.dataSize);
        System.out.println("     Data Length: " + this.dataLength + " points/channel");
        
        // get all the channel information
        for (int i = 0; i < numChannels; i++){
            // get the channel information
            channelLength = readChannel(i, bb.duplicate());
            
            // ignore channelLength bytes from main buffer
            bb.position(bb.position() + channelLength);
        }
        
        // get the scan rate for the file
        this.scanRate = bb.getFloat();
        
        System.out.println("   Scan rate: " + this.scanRate + " points/second");
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
        float scale = -1;
        float offset = 0;
        
        // Get the channel name length
        nameLength = bb.getInt();
        bName = new byte[nameLength];
        
        // Get the bytes that make up the channel name
        bb.get(bName, 0, nameLength);
        
        // Attempt to convert them into an ASCII string
        try{
            name = new String(bName, "US-ASCII").trim();
        } catch (Exception e) {
            System.out.println("Unable to convert channel name to string.");
        }
        
        // Pull the rest of the channel data
        bb.getFloat();          // upper limit
        bb.getFloat();          // lower limit
        bb.getFloat();          // range
        bb.getShort();          // polarity
        bb.getFloat();          // gain
        bb.getShort();          // coupling
        bb.getShort();          // hardware config
        scale = bb.getFloat();  // scale multiplier
        offset = bb.getFloat(); // scale offset
        // Total Size of this segment = 30 bytes
        
        System.out.println("      Channel Name: \"" + name + "\"");
        System.out.println("             Scale: " + scale);
        System.out.println("            Offset: " + offset);
        
        // add Channel to list
        this.channels.add(new ExtendedChannelInfo(id, ChannelType.BINARY, name, scale, offset));
        
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
        if (this.source != null){ throw new IOException("File already open."); }
        
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
        if (this.source == null){ throw new IOException("No file to close."); }
        
        // close the file handle
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
    public void read(ITrace2D trace, int id, long start, long end, int freq) throws IOException {
        ByteBuffer bb;
        double point;
        double x;
        ExtendedChannelInfo channel = this.channels.get(id);
        
        // convert start/end/range to # points instead of ms
        long startPoint = (long) Math.floor(start / this.scanRate * 1000);
          long endPoint = (long) Math.floor(end / this.scanRate * 1000);
        
        // if frequency is less than 1, assume 1
        if (freq < 1){ freq = 1; }
        
        // Dump data from channel being read
        System.out.println("   Reading from channel " + id);
        System.out.println("           Start: " + start + "ms");
        System.out.println("             End: " + end + "ms");
        System.out.println("      Data/Point: " + freq);
        
        // move the file position to the start of the data segment
        long startByte = this.startOfData + start * this.channels.size() * BinaryIO.dataSize;
        this.source.getChannel().position(startByte);
        
        // create a byte[] to hold a single data point
        byte[] data = new byte[BinaryIO.dataSize];
        
        // for each chunk of data to average together
        for(long i = 0 ; i < (endPoint - startPoint) / freq; i++){
            // average data together (rolling average)
            
            // start with a new point value
            point = 0;
            
            // for each point in the current chunk
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
            
            // adjust point by channel scale and offset
            point = point * channel.getScale() + channel.getOffset();
            
            // adjust x value to account for averaging (if any)
            // (initial start point) + (last point in chunk) - (half the size of chunk)
            x = start + i - (freq - 1) / 2;
            
            // add point to trace
            trace.addPoint(x, point);
        }
        
        System.out.println("   Done reading data...");
    }

    /**
     * gets all of the channel information
     * 
     * @throws IOException
     */
    public ArrayList<ChannelInfo> getChannels() throws IOException {
        if (this.source == null){ throw new IOException("No channel data. Open file first."); }
        
        ArrayList<ChannelInfo> results = new ArrayList<ChannelInfo>();
        
        // loop through extended channel information and reduce to
        // normal ChannelInfo instances
        for (ExtendedChannelInfo e : this.channels){
            results.add(e.toChannelInfo());
        }
        
        return results;
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

    public long getEndTime() throws IOException {
        if (this.dataLength == -1){ throw new IOException("Open file first!"); }
        
        return this.dataLength;
    }
    
    
    // private extension to ChannelInfo to store extra channel information
    private class ExtendedChannelInfo extends ChannelInfo{
        private float scale;
        private float offset;
        
        ExtendedChannelInfo(int id, ChannelType type, String name, float scale, float offset){
            super(id, type, name);
            this.scale = scale;
            this.offset = offset;
        }
        
        float getScale(){ return this.scale; }
        float getOffset(){ return this.offset; }
        
        ChannelInfo toChannelInfo(){
            return new ChannelInfo(this.getId(), this.getType(), this.getName());
        }
    }
}
