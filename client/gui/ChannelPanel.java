package client.gui;

import java.util.concurrent.ConcurrentSkipListSet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
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

import client.entities.ClientSocket;
import client.resources.GlobalClient;
import common.entities.payload.PayloadType;
import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.PrivateChannelMetadata;
import common.entities.GroupChannelMetadata;

@SuppressWarnings("serial")
public class ChannelPanel extends JPanel {
  private final String channelId;
  private final ClientSocket clientSocket;

  public ChannelPanel(String channelId, ClientSocket clientSocket) {
    super();
    
    this.channelId = channelId;
    this.clientSocket = clientSocket;

    this.setBackground(Color.PINK);

    this.setVisible(true);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  public synchronized String getChannelTitle() {
    String title = "";
    ChannelMetadata channelMetadata = GlobalClient.clientData.getChannelByChannelId(this.channelId);
    
    if (channelMetadata instanceof PrivateChannelMetadata) {
      PrivateChannelMetadata pcMeta = ((PrivateChannelMetadata)channelMetadata);
      title = GlobalClient.clientData.getOtherUserInPrivateChannel(pcMeta).getUsername();

    } else if (channelMetadata instanceof GroupChannelMetadata) {
      title = ((GroupChannelMetadata)channelMetadata).getChannelName();
    }

    return title;
  }
}
