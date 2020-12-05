package server;

import java.util.concurrent.PriorityBlockingQueue;

public class Test {
  PriorityBlockingQueue<Integer> q = new PriorityBlockingQueue<>();
  Thread thread = new Thread(new Inner());
  void initiate() {
    q.add(1);
    q.add(2);
    q.add(3);
    q.add(4);
    q.add(5);
    q.add(6);
    q.add(7);
    q.add(8);
    q.add(9);
    q.add(10);
  }
  
  void run() {
    for (int i = 20; i < 50; i++) {
      q.add(i);
      System.out.println("run");
      if (!thread.isAlive()) {
        thread.start();
      }
      
    }
  }
  
  public static void main(String[] args) {
    Test test = new Test();
    test.initiate();
    test.run();
  }

  class Inner implements Runnable {
    public void run() {
      while (!q.isEmpty()) {
        // try {
        //   Thread.sleep(30);
        // } catch (Exception e) {}
        
        System.out.println(q.poll());
      }
    }
  }
}
