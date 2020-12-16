package server.gui;

import java.io.ObjectOutputStream;

import common.entities.payload.server_to_client.ServerBroadcast;
import server.entities.EventType;
import server.entities.LogType;
import server.services.GlobalServices;
import server.services.CommunicationService;

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
        CommunicationService.send(out, new ServerBroadcast(this.getMessageText()));
        String userId = GlobalServices.clientConnections.getUserId(out);
        String username = GlobalServices.users.getUsername(userId);
        try {
          GlobalServices.clientConnections.getSocket(userId).close();
          CommunicationService.log(
            String.format("Disconnecting %s:%s", username, userId), 
            LogType.SUCCESS
          );
        } catch (Exception e) {
          CommunicationService.log(
            String.format("Disconnecting %s:%s", username, userId), 
            LogType.CLIENT_ERROR
          );
        }
    }
    this.setMessageText(DisconnectPanel.PROMPT);
  }
  
}
