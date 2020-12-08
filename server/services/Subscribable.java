package server.services;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.1
 */
public interface Subscribable {
  public void activate();
  public void onEvent(Object emitter);
}
