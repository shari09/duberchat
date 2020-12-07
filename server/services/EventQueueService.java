package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import server.entities.Event;
import server.entities.EventType;

/**
 * [insert description]
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

  public void emitEvent(EventType event, int priority, Object emitter) {
    this.queue.add(new Event(event, priority, emitter));
    synchronized(this.thread) {
      this.thread.notify();
    }
  }

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
          ArrayList<Subscribable> subscribers = EventQueueService.this.eventSubscribers.get(event.getType());
          if (subscribers != null) {
            for (int i = 0; i < subscribers.size(); i++) {
              pool.execute(new OnEvent(event.getEmitter(), subscribers.get(i)));
            }
          }
        }
        //inactive until a new event comes
        synchronized(EventQueueService.this.thread) {
          try {
            EventQueueService.this.thread.wait();
          } catch (Exception e) {
            System.out.println("Unable to pause event queue");
            e.printStackTrace();
          }
        }  
      }
          
    }
    
  }

  private class OnEvent implements Runnable {
    private Object emitter;
    private Subscribable subscriber;
    public OnEvent(Object emitter, Subscribable subscriber) {
      this.emitter = emitter;
      this.subscriber = subscriber;
    }

    public void run() {
      this.subscriber.onEvent(this.emitter);
    }
  }

  
}
