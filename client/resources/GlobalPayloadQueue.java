package client.resources;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.Payload;

public class GlobalPayloadQueue {
  public static PriorityBlockingQueue<Payload> queue = new PriorityBlockingQueue<Payload>();

  public static void sendPayload(Payload payloadToSend) {
    GlobalPayloadQueue.queue.add(payloadToSend);
  }
}
