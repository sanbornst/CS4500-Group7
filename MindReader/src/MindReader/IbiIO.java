package MindReader;

import com.opencsv.*;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.TracePoint2D;

import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.geom.Point2D;
import java.awt.Point;

/**
 * IBI File IO Object
 * 
 * Handles reading the IBI file and calculating the data based
 * on the columns in the tab-delimited text file
 * 
 * @author Samantha Sanborn
 */
public class IbiIO implements FileIO {
  // parameters
  private ArrayList<Integer> data;
  private Point2D[] points;
  private long startDelay;
  private long endDelay;
  private CSVReader source;
  private ArrayList<ChannelInfo> channels;

  /**
   * IbiIO constructor
   */
  public IbiIO() {
    data = null;
    points = null;
    startDelay = 0;
    endDelay = 0;
    source = null;
  }
 
  /**
   * Opens a given file path
   * 
   * @param path file path to open
   * 
   * @throws IOException
   */
  public void open(String path) throws IOException {
    if (source == null) { // make sure there's no open file
      // source is a CSVReader with a tab delimiter
      source = new CSVReader(new FileReader(path), '\t');
      channels = new ArrayList<ChannelInfo>();
      channels.add(new ChannelInfo(0, ChannelType.IBI, "IBI"));
      getData();
    } else {
      // throw an error
      throw new IOException("Already an open file.");
    }
  }

  /**
   * Closes the open file (if there is one)
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    if (source != null) {
      source.close();
      data = null;
      points = null;
      startDelay = 0;
      endDelay = 0;
      source = null;
      channels = null;
    } else {
      throw new IOException("No open file.");
    }
  }
 
  /**
   * Reads the data in the open file and plots it, starting from the start point
   * and ending at the end point (id, and frequency are unimportant to IBI reading)
   * 
   * @param channel the trace to plot to
   * @param id the channel id
   * @param start starting point for the requested data
   * @param end ending point for the requested data
   * @param frequency the frequency of read data
   * @param offset y offset of the data
   * 
   * @throws IOException
   */
  public void read(ITrace2D channel, int id, long start, long end, int freq) throws IOException {
    read(channel, id, start, end, freq, 0);
  }
  
  /**
   * Reads the data in the open file and plots it, starting from the start point
   * and ending at the end point (id, and frequency are unimportant to IBI reading)
   * 
   * @param channel the trace to plot to
   * @param id the channel id
   * @param start starting point for the requested data
   * @param end ending point for the requested data
   * @param frequency the frequency of read data
   * @param offset y offset of the data
   * 
   * @throws IOException
   */
  public void read(ITrace2D channel, int id, long start, long end, int freq, long offset) throws IOException {
    for (int i = 0; i < points.length; i++) { // each point
     if (points[i].getX() >= start && points[i].getX() <= end) { // if point is between start and end
       channel.addPoint(new TracePoint2D(Utils.msToSeconds((long) points[i].getX()), points[i].getY() + offset)); // add the point to the channel
     } else if (points[i].getX() > end) { // as soon as we get past the end of the data (since points is in order)
       break;
     }
    }
  }
  
  /**
   * Returns a list of channels in the file
   * 
   * @throws IOException
   */
  public ArrayList<ChannelInfo> getChannels() throws IOException {
    return this.channels;
  }
 
  /**
   * Gets the raw data from a tab-delimited IBI file
   * 
   * @throws IOException
   */
  private void getData() throws IOException {
    String[] line;
    boolean lineHasData = false;
    int numCols = 0;
    // read the first line
    if ((line = source.readNext()) == null) {
     throw new IOException("No data in IBI file.");
    } else {
      for (int i = 0; i < line.length; i++) {
        if (!line[i].equals("")) {
          numCols++;
        }
      }
    }
    // create a 2D array with columns of unknown length (this is ugly, Java is ugly, I'm trying to figure out something better)
    ArrayList<ArrayList<Integer>> cols = new ArrayList<ArrayList<Integer>>(numCols);
    for (int i = 0; i < numCols; i++) {
      cols.add(i, new ArrayList<Integer>());
    }
    // read the columns in the file
    //int count = 0; // temporary troubleshooting loop count
    while ((line = source.readNext()) != null) {
      //count++;
      lineHasData = false; // use to break if the line has no data
      for (int i = 0; i < line.length; i++) {
        if (!line[i].equals("")) {
          cols.get(i).add(Integer.valueOf(line[i]));
          lineHasData = lineHasData || true;
        } else {
          lineHasData = lineHasData || false;
        }
      }
        
      if (lineHasData == false) {
        break; // break the loop if we've gone a whole line without any real data
      }
    }
   columnsToData(cols);
  }
  
  /**
   * Converts the raw data (multiple columns) into usable IBI data
   * 
   * @param cols the array containing the data from the file
   */
  private void columnsToData(ArrayList<ArrayList<Integer>> cols) {
    ArrayList<Integer> dataArray = new ArrayList<Integer>();
    // the first number in column 1 is the delay before the first beat
    startDelay = cols.get(0).get(0);
    int colFirst, colLast = 0; // holders for calculating the full IBI at starts and ends of columns
    for (int k = 0; k < cols.size(); k++) {
      // make sure not to count the first number in column 1
      if (k > 0) {
        colFirst = cols.get(k).get(0);
        dataArray.add(colFirst + colLast);
      }
      // starting from i = 1, go to i < (cols[k].size() - 1)
      for (int i = 1; i < (cols.get(k).size() - 1); i++) {
        dataArray.add(cols.get(k).get(i));
      }
      // set the last point in the column to colLast for the next iteration
      colLast = cols.get(k).get(cols.get(k).size() - 1);
    }
    endDelay = colLast;
    data = dataArray;
    dataToPoints();
  }

  /**
   * Converts the list of IBI data to plottable points
   */
  private void dataToPoints() {
    long sum = startDelay;
    points = new Point2D[data.size()];
    for (int i = 0; i < data.size(); i++) {
      sum += data.get(i);
      points[i] = new Point((int)sum, data.get(i));
    }
  }
  
  /**
   * Returns the ending time of the IBI file
   * 
   * @throws IOException
   */
  public long getEndTime() {
    if (points.length > 0) {
      double end = points[points.length - 1].getX() + endDelay;
      return (long) end;
    } else {
      return 0;
    }
  }

@Override
public boolean isOpen() {
    return source != null;
}

}