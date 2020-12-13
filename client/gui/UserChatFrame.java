package client.gui;

import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JTabbedPane;

import client.entities.ClientSocket;
import common.entities.payload.PayloadType;
import common.entities.payload.ServerBroadcast;
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
public class UserChatFrame extends UserFrame {
  private static final Dimension PREFERRED_DIMENSION = new Dimension(800, 600);

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.REQUEST_ATTACHMENT,
    PayloadType.REMOVE_PARTICIPANT,
    PayloadType.BLACKLIST_USER,
    PayloadType.LEAVE_CHANNEL,
    PayloadType.TRANSFER_OWNERSHIP
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.MESSAGE_TO_SERVER,
    PayloadType.EDIT_MESSAGE,
    PayloadType.REMOVE_MESSAGE,
    PayloadType.REQUEST_MESSAGES,
    PayloadType.REQUEST_ATTACHMENT,
    PayloadType.ADD_PARTICIPANT,
    PayloadType.REMOVE_PARTICIPANT,
    PayloadType.BLACKLIST_USER,
    PayloadType.LEAVE_CHANNEL,
    PayloadType.TRANSFER_OWNERSHIP
  };

  private JTabbedPane tabbedPane;

  public UserChatFrame(String title, ClientSocket clientSocket) {
    super(title, clientSocket);
    
    this.setSize(UserChatFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserChatFrame.PREFERRED_DIMENSION);
    this.setResizable(true);

    this.tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    this.getContentPane().add(this.tabbedPane);
  }

  @Override
  public PayloadType[] getSuccessNotifTypes() {
    return UserChatFrame.SUCCESS_NOTIF_TYPES;
  }

  @Override
  public PayloadType[] getErrorNotifTypes() {
    return UserChatFrame.ERROR_NOTIF_TYPES;
  }

  @Override
  public void clientDataUpdated(ClientData updatedClientData) {
    for(int i = 0; i < this.tabbedPane.getTabCount(); i++) {
      Component comp = this.tabbedPane.getComponentAt(i);
      if (comp instanceof ChannelPanel) {
        ChannelPanel panel = ((ChannelPanel)comp);
        this.tabbedPane.setTitleAt(i, panel.getChannelTitle());
        panel.syncClientData();
        System.out.println("data synced for channel " + panel.getChannelTitle());
      }
    }
    this.repaint();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }

  public void addChannel(String channelId) {
    ChannelPanel panel = new ChannelPanel(
      channelId,
      this.getClientSocket()
    );
    this.tabbedPane.addTab(panel.getChannelTitle(), panel);
    this.requestFocus();
  }

  public boolean hasChannelTab(String channelId) {
    for(int i = 0; i < this.tabbedPane.getTabCount(); i++) {
      Component comp = this.tabbedPane.getComponentAt(i);
      if (comp instanceof ChannelPanel) {
        ChannelPanel panel = ((ChannelPanel)comp);
        if (panel.getChannelId().equals(channelId)) {
          return true;
        }
      }
    }
    return false;
  }

}
