package server.resources;

import server.services.EventQueueService;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalEventQueue {
  public static EventQueueService queue = new EventQueueService();
}
