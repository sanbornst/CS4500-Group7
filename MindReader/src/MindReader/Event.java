package MindReader;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Event object
 * 
 * Holds and returns information about recorded events (and possibly
 * user-created notes if they are implemented).
 * 
 * @author Samantha Sanborn
 */
class Event {
  
  // parameters
  String type;
  String name;
  Date timestamp;
  
  // Constructor
  Event(String type, String name, Date time) {
    this.type = type;
    this.name = name;
    this.timestamp = time;
  }
  
  /**
   * Uses the time from the beginning date/time to calculate the x position
   * of an event and returns a point
   * 
   * @param beginning timestamp of beginning of data
   */
  public TracePointString eventToPoint(Date beginning) {
    TracePointString point = new TracePointString(this.timestamp.getTime() - beginning.getTime(),
                                                  0,
                                                  this.getToolTip());
    return point;
  }
  
  /**
   * Returns a string describing the event, formatted for use as a tooltip
   * 
   * @return tooltip text
   */
  public String getToolTip() {
    SimpleDateFormat outFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS aa");
    String tip = "<html>Event type: " + this.type + "<br />" +
      "Timestamp: " + outFormat.format(this.timestamp) + "<br />" +
      "Event name: " + this.name + "</html>";
    return tip;
  }
}