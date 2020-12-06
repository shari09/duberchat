package server.resources;

import server.services.EventQueue;

public class GlobalEventQueue {
  public static EventQueue queue = new EventQueue();
}
