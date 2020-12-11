package client;

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

import common.entities.ChannelMetadata;
import common.entities.ClientData;

/**
 * The frame to display the GUI for the client.
 * <p>
 * Created on 2020.12.05.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserChatFrame extends JFrame {

  private ClientSocket clientSocket;
  private ConcurrentSkipListSet<ChannelMetadata> channels;
  private ChannelMetadata currentChannel;

  public UserChatFrame(String title, ClientSocket clientSocket) {
    super(title);
  }

}
