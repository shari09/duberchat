package server.entities;

public class Event implements Comparable<Event> {
  private int priority;
  private EventType type;
  
  public Event(EventType type, int priority) {
    this.type = type;
    this.priority = priority;
  }

  public int compareTo(Event other) {
    return this.priority - other.getPriority();
  }

  public int getPriority() {
    return this.priority;
  }

  public EventType getType() {
    return this.type;
  }
}
