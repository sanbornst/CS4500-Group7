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
}