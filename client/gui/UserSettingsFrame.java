package client.gui;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.security.acl.Group;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Container;
import java.awt.CardLayout;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.jws.soap.SOAPBinding.Use;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

import client.entities.ClientSocket;
import client.entities.ClientSocketListener;
import client.resources.GlobalClient;
import common.entities.ChannelMetadata;
import common.entities.ClientData;
import common.entities.payload.PayloadType;

@SuppressWarnings("serial")
public class UserSettingsFrame extends JFrame implements ActionListener,
                                                         ClientSocketListener{

  private static final Dimension PREFERRED_DIMENSION = new Dimension(800, 600);

  private ClientSocket clientSocket;

  private JLabel usernameLabel;
  private JButton updateUsernameButton;

  private JButton updatePasswordButton;

  private JLabel descriptionLabel;
  private JButton updateDescriptionButton;

  private JLabel statusLabel;
  private JButton updateStatusButton;

  public UserSettingsFrame(String title, ClientSocket clientSocket) {
    super(title);
    
    this.clientSocket = clientSocket;
    this.clientSocket.addListener(this);

    this.setSize(UserSettingsFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserSettingsFrame.PREFERRED_DIMENSION);

    this.setResizable(true);

    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

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

  private void updateSettingFields(ClientData updatedClientData) {
    this.usernameLabel.setText("Username: " + updatedClientData.getUsername());
    this.descriptionLabel.setText("Description: " + updatedClientData.getDescription());
    this.statusLabel.setText("Status: ");
  }
}
