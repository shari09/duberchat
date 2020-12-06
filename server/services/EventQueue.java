package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import server.entities.Event;
import server.entities.EventType;

public class EventQueue {
  private PriorityBlockingQueue<Event> queue;
  private HashMap<EventType, ArrayList<Runnable>> eventSubscribers;
  private Thread thread;
  public EventQueue() {
    this.queue = new PriorityBlockingQueue<>();
    this.eventSubscribers = new HashMap<>();
    this.thread = new Thread(new QueueLoop());
    this.thread.start();
  }

  public void addEvent(EventType event, int priority) {
    this.queue.add(new Event(event, priority));
    synchronized(this.thread) {
      this.thread.notify();
    }
  }

  public void subscribe(EventType type, Runnable subscriber) {
    if (this.eventSubscribers.containsKey(type)) {
      this.eventSubscribers.get(type).add(subscriber);
      return;
    }
    ArrayList<Runnable> subscribers = new ArrayList<>();
    subscribers.add(subscriber);
    this.eventSubscribers.put(type, subscribers);
  }

  class QueueLoop implements Runnable {
    private final int MAX_THREAD_COUNT = 10;
    ExecutorService pool = Executors.newFixedThreadPool(this.MAX_THREAD_COUNT);
    
    public void run() {
      //loop through all the events and call their subscribers
      while (true) {
        while (!EventQueue.this.queue.isEmpty()) {
          Event event = EventQueue.this.queue.poll();
          ArrayList<Runnable> subscribers = EventQueue.this.eventSubscribers.get(event.getType());
          for (int i = 0; i < subscribers.size(); i++) {
            pool.execute(subscribers.get(i));
          }
        }
        //inactive until a new event comes
        synchronized(EventQueue.this.thread) {
          try {
            EventQueue.this.thread.wait();
          } catch (Exception e) {
            System.out.println("Unable to pause event queue");
            e.printStackTrace();
          }
        }  
      }
          
    }
    
  }

  
}
