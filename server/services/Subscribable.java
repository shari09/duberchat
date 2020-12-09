package server.services;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Subscribable {
  /**
   * Subscribes to its target events.
   */
  public void activate();
  /**
   * This function is called once the event it subscribed to has happened.
   * @param emitter    the object source that emitted this event
   */
  public void onEvent(Object emitter);
}
