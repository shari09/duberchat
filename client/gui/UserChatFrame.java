package client.gui;

import java.util.concurrent.ConcurrentSkipListSet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import common.entities.payload.PayloadType;
import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.GroupChannelMetadata;
import common.entities.PrivateChannelMetadata;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.05.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserChatFrame extends UserFrame {
  private static final int PREFERRED_WIDTH = 800;
  private static final int PREFERRED_HEIGHT = 600;

  private JTabbedPane tabbedPane;

  public UserChatFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);

    this.tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    this.setPreferredSize(
      new Dimension(
        UserChatFrame.PREFERRED_WIDTH,
        UserChatFrame.PREFERRED_HEIGHT
      )
    );
    this.setResizable(true);
    this.setVisible(true);
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {

  }

  @Override
  public void clientRequestStatusReceived(
    PayloadType payloadType, 
    boolean successful,
    String notifMessage
  ) {

  }

  public void addChannel(String channelId) {
    ChannelPanel panel = new ChannelPanel(
      channelId,
      this.getClientSocket()
    );
    this.tabbedPane.insertTab(
      panel.getChannelTitle(),
      null,
      panel,
      null,
      0
    );
    this.setVisible(true);
    this.requestFocus();
  }

}
