package MindReader;

import info.monitorenter.gui.chart.ITrace2D;

import java.io.IOException;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

//TODO Migrate prototype code

/**
 * Binary File IO Object
 * 
 * <p>Handles reading from mindware .mw files, parsing them into individual
 * channels that can be read from.
 * 
 * @author Christopher Curreri
 */
public class BinaryIO implements FileIO {
    private FileInputStream source;          // input file stream
    private int headerLength;                // length of the header
    private int startOfData;                 // start location of data
    private ByteBuffer header;               // copy of just the header
    private ArrayList<ChannelInfo> channels; // ArrayList for channel info
    
    BinaryIO(){
        this.source = null;
        this.headerLength = -1;
        this.startOfData = -1;
        this.header = null;
        this.channels = new ArrayList<ChannelInfo>();
    }
    
    public void open(String path) throws IOException {
        this.source = new FileInputStream(path);
        
        //TODO fill & read header here
    }

    public void close() throws IOException {
        this.source.close();
    }

    public void read(ITrace2D channel, int id, int start, int length,
            int frequency) throws IOException {
        // TODO Auto-generated method stub

    }

    public ArrayList<ChannelInfo> getChannels() {
        return this.channels;
    }

}
