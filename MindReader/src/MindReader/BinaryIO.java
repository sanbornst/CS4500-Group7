package MindReader;

import info.monitorenter.gui.chart.ITrace2D;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Binary File IO Object
 * 
 * <p>Handles reading from mindware .mw files, parsing them into individual
 * channels that can be read from.
 * 
 * @author Christopher Curreri
 */
public class BinaryIO implements FileIO {
    //TODO Migrate prototype code
    
    @Override
    public void open(String path) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void read(ITrace2D channel, int id, int start, int length,
            int frequency) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public ArrayList<ChannelInfo> getChannels() {
        // TODO Auto-generated method stub
        return null;
    }

}
