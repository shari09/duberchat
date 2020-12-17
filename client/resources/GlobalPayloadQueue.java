package client.resources;

import java.util.concurrent.PriorityBlockingQueue;

import common.entities.payload.Payload;

/**
 * A queue that stores the payloads to send from client to server.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalPayloadQueue {
  /** A priority to store payloads. */
  public static PriorityBlockingQueue<Payload> queue = new PriorityBlockingQueue<Payload>();

  /**
   * Enqueues the payload into the payload queue.
   * @param payloadToSend The payload to be enqueued.
   */
  public static void enqueuePayload(Payload payloadToSend) {
    GlobalPayloadQueue.queue.add(payloadToSend);
  }
}
