package server.entities;

/**
 * An event used for {@link EventQueueService}.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Event implements Comparable<Event> {
  private int priority;
  private EventType type;
  private Object emitter;
  
  /**
   * Creates a new event.
   * @param type          the event type
   * @param priority      the event priority/importance
   * @param emitter       the emitter/source of event
   * @see                 EventType
   */
  public Event(EventType type, int priority, Object emitter) {
    this.type = type;
    this.priority = priority;
    this.emitter = emitter;
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

  public Object getEmitter() {
    return this.emitter;
  }
}
