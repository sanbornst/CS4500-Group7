package MindReader;

import java.util.ArrayList;
// TODO: download + import CSVReader class
import java.io.IOException;
import java.io.FileReader;

/**
 * IBI File IO Object
 * 
 * Handles reading the IBI file and calculating the data based
 * on the columns in the tab-delimited text file
 * 
 * @author Samantha Sanborn
 */
class IbiIO implements FileIO {
  // parameters
  private int[] data;
  private Point2D[] points;
  private int startDelay;
  private CSVReader source;
  private ArrayList<ChannelInfo> channels;

  // Constructor
  IbiIO() {
    channels = new ArrayList<ChannelInfo>();
    // add the IBI channel and nothing else
  }
	
  public void open(String path) throws IOException {
    source = new CSVReader(new FileReader(path), '\t');
    getData();
  }

  public void close() throws IOException {
    
  }
	
  public void read(ITrace2D channel, int id, int start, int length, int frequency) throws IOException {
    int end = start + length;
    for (int i = 0; i < points.length; i++) {
  	  if (points[i].x >= start && points[i].x <= end) {
  	    channel.addPoint(points[i]);
  	  } else if (points[i].x > end) {
  	    break;
  	  }
    }
  }
  
  public ArrayList<ChannelInfo> getChannels() {
    return this.channels;
  }
	
  private void getData() {
    String[] line;
    // read the first line
    if ((line = source.readNext()) == null) {
    	// we have an issue
    }
    ArrayList<Integer>[] cols = new ArrayList<Integer>[line.length];
    // read the columns in the file
    while ((line = source.readNext()) != null) {
      // break the loop if the line is entirely blank
      
      for (int i = 0; i < line.length; i++) {
        if (!line[i].equals("")) {
          cols[i].add(Integer.valueOf(line[i]);
        }
      }
    }
  	columnsToData(cols);
  }
  
  private void columnsToData(ArrayList<Integer>[] cols) {
    ArrayList<Integer> dataArray = new ArrayList<Integer>();
    // the first number in column 1 is the delay before the first beat
    startDelay = cols[0].get(0);
    int colFirst, colLast; // holders for calculating the full IBI at starts and ends of columns
    for (int k = 0; k < cols.length; k++) {
      // make sure not to count the first number in column 1
      if (k > 0) {
        colFirst = cols[k].get(0);
        dataArray.add(colFirst + colLast);
      }
      // starting from i = 1, go to i < (cols[k].size() - 1)
      for (int i = 1; i < (cols[k].size() - 1); i++) {
        dataArray.add(cols[k].get(i));
      }
      // set the last point in the column to colLast for the next iteration
      colLast = cols[k].get(cols[k].size() - 1);
    }
    data = dataArray.toArray();
    dataToPoints();
  }

  private void dataToPoints() {
    int sum = startDelay;
    points = new Point2D[data.length];
    for (int i = 0; i < data.length; i++) {
      points[i] = new Point2D(sum, data[i]);
      sum += data[i];
    }
  }
}