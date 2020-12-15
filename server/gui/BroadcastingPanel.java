package server.gui;

import java.io.ObjectOutputStream;

import common.entities.payload.server_to_client.ServerBroadcast;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.PayloadService;

/**
 * 
 * <p>
 * Created on 2020.12.13.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class BroadcastingPanel extends AdminPanel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String PROMPT = "Message to broadcast...";

  public BroadcastingPanel() {
    super("Broadcast", "Broadcasting (Connected users)", EventType.BROADCAST);
    this.setMessageText(BroadcastingPanel.PROMPT);
  }

  @Override
  public void activate() {
    super.activate();
    GlobalServices.guiEventQueue.subscribe(EventType.BROADCAST, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    super.onEvent(emitter, eventType);
    switch (eventType) {
      case BROADCAST:
        this.broadcastMsg();
        break;
      default:
        break;
    }
  }


  private void broadcastMsg() {
    for (ObjectOutputStream out: this.getSelectedUsersOut()) {
        PayloadService.send(out, new ServerBroadcast(this.getMessageText()));
    }
    this.setMessageText(BroadcastingPanel.PROMPT);
  }
  
}
