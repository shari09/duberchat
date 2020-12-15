package server.gui;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;

import common.entities.UserMetadata;
import server.entities.EventType;
import server.services.GlobalServices;
import server.services.Subscribable;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class UsersEntriesPanel extends EntriesPanel implements Subscribable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private ConcurrentHashMap<String, JButton> users;


  public UsersEntriesPanel() {
    super("All Users");
    this.users = new ConcurrentHashMap<>();
    this.loadUsers();

    this.activate();
  }

  private void addUser(UserMetadata user) {
    String username = user.getUsername();
    JButton tab = super.addEntry(username, new UserInfoPanel(user));
    this.users.put(user.getUserId(), tab);
  }

  private void loadUsers() {
    ArrayList<UserMetadata> users = GlobalServices.users.getAllUsers();
    for (UserMetadata user: users) {
      this.addUser(user);
    }
  }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_USER, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    this.addUser((UserMetadata)emitter);
  }
  

  
}
