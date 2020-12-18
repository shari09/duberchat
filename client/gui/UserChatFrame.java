package client.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.entities.ClientSocket;
import client.services.ChannelServices;
import common.entities.payload.PayloadType;
import common.entities.payload.server_to_client.ServerBroadcast;
import common.gui.Theme;

/**
 * The frame containing a list of the user's opened channels.
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class UserChatFrame extends UserFrame implements ChangeListener {
  private static final Dimension PREFERRED_DIMENSION = new Dimension(900, 600);

  private static final PayloadType[] SUCCESS_NOTIF_TYPES = new PayloadType[] {
    PayloadType.EDIT_MESSAGE,
    PayloadType.REMOVE_MESSAGE,
    PayloadType.REQUEST_ATTACHMENT
  };
  private static final PayloadType[] ERROR_NOTIF_TYPES = new PayloadType[] {
    PayloadType.MESSAGE_TO_SERVER,
    PayloadType.EDIT_MESSAGE,
    PayloadType.REMOVE_MESSAGE,
    PayloadType.REQUEST_MESSAGES,
    PayloadType.REQUEST_ATTACHMENT
  };

  private JTabbedPane tabbedPane;

  public UserChatFrame(ClientSocket clientSocket) {
    super(clientSocket);

    this.setVisible(false);
    this.setTitle("Chat Window");
    
    this.setSize(UserChatFrame.PREFERRED_DIMENSION);
    this.setPreferredSize(UserChatFrame.PREFERRED_DIMENSION);
    this.setMinimumSize(UserChatFrame.PREFERRED_DIMENSION);
    this.setResizable(true);

    this.tabbedPane = ClientGUIFactory.getTabbedPane(Theme.getBoldFont(15));
    this.tabbedPane.setTabPlacement(JTabbedPane.LEFT);
    this.tabbedPane.addChangeListener(this);
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
  public void clientDataUpdated() {
    this.syncTabs();
    this.repaint();
  }

  @Override
  public void serverBroadcastReceived(ServerBroadcast broadcast) {
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (this.tabbedPane.getSelectedComponent() instanceof ChannelPanel) {
      this.scrollToBottom((ChannelPanel)this.tabbedPane.getSelectedComponent());
    }
  }

  public void addChannel(String channelId) {
    ChannelPanel panel = new ChannelPanel(
      channelId,
      this.getClientSocket()
    );
    this.tabbedPane.addTab(ChannelServices.getChannelTitle(channelId), panel);
    this.tabbedPane.setSelectedComponent(panel);
    this.syncTabs();
    this.requestFocus();
    this.scrollToBottom(panel);
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

  private void scrollToBottom(ChannelPanel panel) {
    if (panel.getMessagesList().getModel().getSize() > 0) {
      panel.getMessagesList().ensureIndexIsVisible(panel.getMessagesList().getModel().getSize()-1);
    }
  }

  private void syncTabs() {
    for(int i = 0; i < this.tabbedPane.getTabCount(); i++) {
      Component comp = this.tabbedPane.getComponentAt(i);
      if (comp instanceof ChannelPanel) {
        ChannelPanel panel = ((ChannelPanel)comp);
        this.tabbedPane.setTitleAt(i, ChannelServices.getChannelTitle(panel.getChannelId()));
        panel.syncClientData();
      }
    }
    this.repaint();
  }
  

}
