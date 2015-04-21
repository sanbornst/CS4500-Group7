package MindReader;

import com.opencsv.*;
import info.monitorenter.gui.chart.ITrace2D;

import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.IErrorBarPolicy;
import info.monitorenter.gui.chart.IErrorBarPainter;
import info.monitorenter.gui.chart.errorbars.ErrorBarPolicyAbsoluteSummation;
import info.monitorenter.gui.chart.errorbars.ErrorBarPainterLine;
import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Event file IO object
 * 
 * Handles reading data from event files (.txt) and translating
 * it into a usable channel
 * 
 * @author Samantha Sanborn
 */
public class EventIO implements FileIO {
  
  // parameters
  private ArrayList<Event> events;
  private CSVReader source;
  private ArrayList<ChannelInfo> channels;
  
  // constructor
  public EventIO() {
    // set everything to null
    events = null;
    source = null;
    channels = null;
  }
  
  /**
   * Opens a file from a path if there is not already an open file,
   * verifies it is an event file, and fetches the events
   * 
   * @param path file path
   * 
   * @throws IOException
   */
  public void open(String path) throws IOException {
    if (source == null) {
      source = new CSVReader(new FileReader(path), '\t');
      events = new ArrayList<Event>();
      channels = new ArrayList<ChannelInfo>();
      channels.add(new ChannelInfo(0, ChannelType.EVENT, "Events"));
      // check the first line to ensure this file is an event file & get events
      getEvents();
    } else {
      throw new IOException("Already an open file.");
    }
  }
  
  /**
   * Closes the currently open event file and resets all the data
   *
   * @throws IOException
   */
  public void close() throws IOException {
    if (source != null) {
      source.close();
      source = null;
      events = null;
      channels = null;
    } else {
      throw new IOException("No open file.");
    }
  }
  
  /**
   * Places the event data within the given range onto the given trace
   * 
   * @param channel the trace to place events on
   * @param id channel id (unused)
   * @param start start point of data requested
   * @param end end point of data requested
   * @param freq frequency of data requested (unused)
   * 
   * @throws IOException
   */
  public void read(ITrace2D channel, int id, long start, long end, int freq) throws IOException {
    read(channel, id, start, end, freq, 0);
  }
  
  /**
   * Places the event data within the given range onto the given trace
   * 
   * @param channel the trace to place events on
   * @param id channel id (unused)
   * @param start start point of data requested
   * @param end end point of data requested
   * @param freq frequency of data requested (unused)
   * @param offset y offset of data
   * 
   * @throws IOException
   */
  public void read(ITrace2D channel, int id, long start, long end, int freq, long offset) throws IOException {
    // get the starting date in ms
    long fileStart = events.get(0).timestamp.getTime();
    TracePointString p = null;
    for (Event e : events) {
      if (e.timestamp.getTime() - fileStart >= start && e.timestamp.getTime() - fileStart <= end) { // point is in requested section
        p = e.eventToPoint(new Date(fileStart));
        p.setLocation(p.getX(), p.getY() + offset);
        channel.addPoint(p);
      }
    }
    
    // set up the trace to look like an event channel (error bars specifically)?
    if (!channel.showsErrorBars()) {
      IErrorBarPolicy errors = new ErrorBarPolicyAbsoluteSummation(5, 5);
      errors.setShowNegativeYErrors(true);
      errors.setShowPositiveYErrors(true);
      IErrorBarPainter errorPainter = new ErrorBarPainterLine();
      errors.setErrorBarPainter(errorPainter);
      channel.setErrorBarPolicy(errors);
    }    
    
  }
  
  /**
   * Returns the info for the event channel
   * 
   * @throws IOException
   */
  public ArrayList<ChannelInfo> getChannels() throws IOException {
    return channels;
  }
  
  /**
   * Reads the tab-delimited file and extracts event information
   * 
   * @throws IOException
   */
  private void getEvents() throws IOException {
    // variables
    String[] line;
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS aa");
    // check to ensure this is an event file
    line = source.readNext();
    if (line == null) {
      throw new IOException("File is empty.");
    } else if (!line[0].equals("Event Type") || !line[1].equals("Name") || !line[2].equals("Date") || !line[3].equals("Time")) {
      throw new IOException("File is not a recognized event file.");
    }
    // loop through all the lines with data
    while ((line = source.readNext()) != null) {
      if (line[0].equals("")) {
        // end of data
        break;
      } else if (line.length < 4) {
        // too few parameters - error
      }
      try {
        events.add(new Event(line[0], // event type
                             line[1], // event name
                             df.parse(line[2] + " " + line[3]))); // timestamp
      } catch (ParseException p) {
        throw new IOException("Problem with event date formatting.");
      }
      
    }
  }
  
  /**
   * Gets the time of the last event in the file (if there are events in the file)
   */
  public long getEndTime() {
    if (events.size() > 0) {
      return (long) (events.get(events.size() - 1).eventToPoint(events.get(0).timestamp).getX());
    } else {
      return 0;
    }
  }

@Override
public boolean isOpen() {
    return source != null;
}

}