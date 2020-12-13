package client.gui;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import common.entities.payload.FriendRequestToServer;
import common.entities.GroupChannelMetadata;
import common.entities.payload.PayloadType;

import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;

/**
 * The frame to display the GUI for the client.
 * Detects user activity by key and mouse motion events.
 * <p>
 * Created on 2020.12.12.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public abstract class UserFrame extends JFrame implements ClientSocketListener {

  private ClientSocket clientSocket;

  public UserFrame(String title, ClientSocket clientSocket) {
    super(title);
    
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    this.clientSocket = clientSocket;
    this.clientSocket.addListener(this);
  }

  @Override
  public abstract void clientDataUpdated(ClientData updatedClientData);

  @Override
  public abstract void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  );

  @Override
  public void dispose() {
    this.clientSocket.removeListener(this);
    super.dispose();
  }

  public ClientSocket getClientSocket() {
    return this.clientSocket;
  }

}
