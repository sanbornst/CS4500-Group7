package MindReader;

import com.opencsv.*;
import info.monitorenter.gui.chart.ITrace2D;

import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class EventIO implements FileIO {
  private ArrayList<Event> events;
  private CSVReader source;
  private ArrayList<ChannelInfo> channels;
  
  public EventIO() {
    events = new ArrayList<Event>();
    channels = new ArrayList<ChannelInfo>();
    channels.add(new ChannelInfo(0, ChannelType.EVENT, "Events"));
  }
  
  public void open(String path) throws IOException {
    if (source == null) {
      source = new CSVReader(new FileReader(path), '\t');
      // check the first line to ensure this file is an event file & get events
      getEvents();
    } else {
      throw new IOException("Already an open file.");
    }
  }
  
  public void close() throws IOException {
    if (source != null) {
      source.close();
      source = null;
    } else {
      throw new IOException("No open file.");
    }
  }
  
  // read
  public void read(ITrace2D channel, int id, long start, long end, int freq) throws IOException {
    // get the starting date in ms
    long fileStart = events.get(0).timestamp.getTime();
    for (Event e : events) {
      if (e.timestamp.getTime() - fileStart >= start && e.timestamp.getTime() - fileStart <= end) { // point is in requested section
        channel.addPoint(e.eventToPoint(new Date(fileStart)));
      }
    }
  }
  
  public ArrayList<ChannelInfo> getChannels() throws IOException {
    return channels;
  }
  
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
  
  public long getEndTime() {
    return 0;
  }
  
  public ArrayList<Event> getEventsForTest() {
    return events;
  }
}