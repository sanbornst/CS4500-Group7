package MindReader;

import info.monitorenter.gui.chart.ITrace2D;

import java.io.IOException;
import java.util.ArrayList;

/**
 * File IO Interface
 * 
 * Interface for various file sources of data (binary, ibi, event, etc.)
 * 
 * @author Christopher Curreri
 */
public interface FileIO {
    /**
     * Opens the given file
     * 
     * @param path file path to open.
     * 
     * @throws IOException
     */
    public void open(String path) throws IOException;

    /**
     * Closes the file
     * 
     * @throws IOException
     */
    public void close() throws IOException;

    /**
     * Reads data from the file into a trace
     * 
     * @param channel The jchart2d trace to put data in
     * @param id The channel ID to read (unique to that file)
     * @param start the time (in ms from 0) to start reading from
     * @param end the time (in ms) to stop reading at
     * @param freq the number of consecutive data points to group together
     * 
     * @throws IOException
     */
    public void read(ITrace2D channel, int id, long start, long end, int freq) throws IOException;

    /**
     * Reads data from the file into a trace
     * 
     * @param channel The jchart2d trace to put data in
     * @param id The channel ID to read (unique to that file)
     * @param start the time (in ms from 0) to start reading from
     * @param end the time (in ms) to stop reading at
     * @param freq the number of consecutive data points to group together
     * @param offset value to offset all data points by
     * 
     * @throws IOException
     */
    public void read(ITrace2D channel, int id, long start, long end, int freq, long offset) throws IOException;
    
    /**
     * Gets channel information from the file
     * 
     * @return arrayList containing channelInfo
     */
    public ArrayList<ChannelInfo> getChannels() throws IOException;
    
    /**
     * Gets the end time of the file
     * 
     * @return the time (in ms) of the last entry in the file
     */
    public long getEndTime() throws IOException;
}
