package client.gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import common.entities.Constants;
import common.entities.payload.PayloadType;
import common.gui.Theme;

/**
 * The super frame for all client gui frames after a socket connection has been made.
 * <p>
 * Created on 2020.12.12.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public abstract class UserFrame extends JFrame implements ClientSocketListener {

  private ClientSocket clientSocket;

  public UserFrame(ClientSocket clientSocket) {
    super(Theme.APPLICATION_NAME);

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
  public void clientRequestStatusReceived(
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
            JOptionPane.PLAIN_MESSAGE,
            ClientGUIFactory.getDialogSuccessIcon(30, 30)
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
            JOptionPane.ERROR_MESSAGE,
            ClientGUIFactory.getDialogErrorIcon(30, 30)
          );
          return;
        }
      }
    }
    if (payloadType == PayloadType.KEEP_ALIVE) {
      this.dispose();
      System.exit(0); // manual EXIT_ON_CLOSE lol
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
