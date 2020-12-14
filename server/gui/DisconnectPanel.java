package server.gui;

import java.io.ObjectOutputStream;

import common.entities.payload.ServerBroadcast;
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
public class DisconnectPanel extends AdminPanel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String PROMPT = "Disconnection message...";

  public DisconnectPanel() {
    super("Disconnect", "Disconnecting (Connected users)", EventType.DISCONNECT);
    this.setMessageText(DisconnectPanel.PROMPT);
  }

  @Override
  public void activate() {
    super.activate();
    GlobalServices.guiEventQueue.subscribe(EventType.DISCONNECT, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    super.onEvent(emitter, eventType);
    switch (eventType) {
      case DISCONNECT:
        this.disconnectUsers();
        break;
      default:
        break;
    }
  }


  private void disconnectUsers() {
    for (ObjectOutputStream out: this.getSelectedUsersOut()) {
        PayloadService.send(out, new ServerBroadcast(this.getMessageText()));
        String userId = GlobalServices.clientConnections.getUserId(out);
        String username = GlobalServices.users.getUsername(userId);
        try {
          GlobalServices.clientConnections.getSocket(userId).close();
          GlobalServices.serverEventQueue.emitEvent(
            EventType.NEW_LOG, 
            1,
            String.format("[CONNECTION] Disconnected %s:%s", username, userId)
          );
        } catch (Exception e) {
          GlobalServices.serverEventQueue.emitEvent(
            EventType.NEW_LOG, 
            1,
            String.format("[CONNECTION] Failed to disconnect %s:%s", username, userId)
          );
        }
    }
    this.setMessageText(DisconnectPanel.PROMPT);
  }
  
}
