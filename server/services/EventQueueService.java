package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import server.entities.Event;
import server.entities.EventType;
import server.entities.LogType;

/**
 * Creates an event queue that provides methods
 * such as emitting/subscribing to events.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.1.0
 * @since 1.0.0
 */

public class EventQueueService {
  private PriorityBlockingQueue<Event> queue;
  private HashMap<EventType, ArrayList<Subscribable>> eventSubscribers;
  private Thread thread;
  public EventQueueService() {
    this.queue = new PriorityBlockingQueue<>();
    this.eventSubscribers = new HashMap<>();
    this.thread = new Thread(new QueueLoop());
    this.thread.start();
  }

  /**
   * Emits an event given a type, importance/priority, and the emitter/source.
   * @param event       the event type
   * @param priority    the priority
   * @param emitter     the emitter/source
   */
  public void emitEvent(EventType event, int priority, Object emitter) {
    this.queue.add(new Event(event, priority, emitter));
    synchronized(this.thread) {
      this.thread.notify();
    }
  }

  /**
   * Subscribes to an event.
   * Once an object subscribes to an event, their {@code onEvent(source, eventType)}
   * method will be called whenever something emits a event of the corresponding type.
   * @param type            the event type
   * @param subscriber      the object subscribing to the event
   * @see                   Subscribable
   */
  public void subscribe(EventType type, Subscribable subscriber) {
    if (this.eventSubscribers.containsKey(type)) {
      this.eventSubscribers.get(type).add(subscriber);
      return;
    }
    ArrayList<Subscribable> subscribers = new ArrayList<>();
    subscribers.add(subscriber);
    this.eventSubscribers.put(type, subscribers);
  }

  private class QueueLoop implements Runnable {
    private final int MAX_THREAD_COUNT = 10;
    ExecutorService pool = Executors.newFixedThreadPool(this.MAX_THREAD_COUNT);
    
    public void run() {
      //loop through all the events and call their subscribers
      while (true) {
        while (!EventQueueService.this.queue.isEmpty()) {
          Event event = EventQueueService.this.queue.poll();
          ArrayList<Subscribable> subscribers = EventQueueService
                                                .this
                                                .eventSubscribers
                                                .get(event.getType());
          if (subscribers != null) {
            for (int i = 0; i < subscribers.size(); i++) {
              pool.execute(new EventHandler(
                event.getEmitter(), 
                subscribers.get(i), 
                event.getType()
              ));
            }
          }
        }
        //inactive until a new event comes
        synchronized(EventQueueService.this.thread) {
          try {
            if (EventQueueService.this.queue.isEmpty()) {
              EventQueueService.this.thread.wait();
            }
          } catch (Exception e) {
            CommunicationService.log(String.format(
              "Unable to pause event queue: %s \n%s", 
              e.getMessage(),
              CommunicationService.getStackTrace(e)
            ), LogType.SERVER_ERROR);
          }
        }  
      }
          
    }
    
  }

  /**
   * A {@code Runnable} inner class used to call the {@code Subscribable}'s
   * {@code onEvent()} with corresponding information.
   */
  private class EventHandler implements Runnable {
    private Object emitter;
    private Subscribable subscriber;
    private EventType type;
    public EventHandler(Object emitter, Subscribable subscriber, EventType type) {
      this.emitter = emitter;
      this.subscriber = subscriber;
      this.type = type;
    }

    @Override
    public void run() {
      this.subscriber.onEvent(this.emitter, this.type);
    }
  }

  
}
