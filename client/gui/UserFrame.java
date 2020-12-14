package client.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import common.entities.ClientData;
import common.entities.payload.PayloadType;
import common.entities.payload.ServerBroadcast;
import common.entities.Constants;
import client.entities.ClientSocket;
import client.entities.ClientSocketListener;

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

    try {
      this.setIconImage(ImageIO.read(new File(Constants.ICON_PATH)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    this.clientSocket = clientSocket;
    this.clientSocket.addListener(this);
  }

  @Override
  public abstract void clientDataUpdated(ClientData updatedClientData);

  @Override
  public abstract void serverBroadcastReceived(ServerBroadcast broadcast);

  @Override
  public synchronized void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {
    if (successful) {
      for (int i = 0; i < this.getSuccessNotifTypes().length; i++) {
        if (this.getSuccessNotifTypes()[i] == payloadType) {
          JOptionPane.showMessageDialog(
            this,
            notifMessage,
            "Success",
            JOptionPane.PLAIN_MESSAGE
          );
          return;
        }
      }
    } else {
      for (int i = 0; i < this.getErrorNotifTypes().length; i++) {
        if (this.getErrorNotifTypes()[i] == payloadType) {
          JOptionPane.showMessageDialog(
            this,
            notifMessage,
            "Error",
            JOptionPane.ERROR_MESSAGE
          );
          return;
        }
      }
    }
  }

  @Override
  public void dispose() {
    this.clientSocket.removeListener(this);
    super.dispose();
  }

  public abstract PayloadType[] getSuccessNotifTypes();

  public abstract PayloadType[] getErrorNotifTypes();

  public ClientSocket getClientSocket() {
    return this.clientSocket;
  }

}
