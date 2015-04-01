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
  String type;
  String name;
  Date timestamp;
  
  // Constructor
  Event(String type, String name, Date time) {
    this.type = type;
    this.name = name;
    this.timestamp = time;
  }
  
  // add methods for returning modified trace points
  public TracePointString eventToPoint(Date beginning) {
    TracePointString point = new TracePointString(this.timestamp.getTime() - beginning.getTime(),
                                                  0,
                                                  this.getToolTip());
    return point;
  }
  
  public String getToolTip() {
    SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss.SSS aa");
    String tip = "<html>Event type: " + this.type + "<br />" +
      "Timestamp: " + outFormat.format(this.timestamp) + "<br />" +
      "Event name: " + this.name + "</html>";
    return tip;
  }
}