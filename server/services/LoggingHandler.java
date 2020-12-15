package server.services;

import server.entities.EventType;
import server.entities.Log;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoggingHandler implements Subscribable {

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_LOG, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    GlobalServices.logging.addLog((Log)emitter);
  }
  
}
