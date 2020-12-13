package server.gui;


import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import server.entities.EventType;
import server.entities.User;
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

  private void addUser(User user) {
    String username = user.getUsername();
    JButton tab = super.addEntry(username, new JPanel());
    this.users.put(user.getId(), tab);
  }

  private void loadUsers() {
    ConcurrentHashMap<String, User> users = GlobalServices.users.getAllUsers();
    for (User user: users.values()) {
      this.addUser(user);
    }
  }

  // private void removeClient(Object emitter) {
  //   String userId;
  //   if (emitter instanceof ObjectOutputStream) {
  //     ObjectOutputStream toClient = (ObjectOutputStream) emitter;
  //     userId = GlobalServices.clientConnections.getUserId(toClient);
  //   } else {
  //     userId = (String) emitter;
  //   }
  //   super.removeEntry(this.users.get(userId));
  //   this.users.remove(userId);
  // }



  // @Override
  // public void actionPerformed(ActionEvent e) {
  //   JButton button = (JButton)e.getSource();
  //   // button.setBackground(bg);

  //   GlobalServerServices.guiEventQueue.emitEvent(
  //     EventType.SELECT_ENTRY, 
  //     2, 
  //     super.getContent(button)
  //   );

  // }

  @Override
  public void activate() {
    GlobalServices.serverEventQueue.subscribe(EventType.NEW_USER, this);
    // GlobalServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    this.addUser((User)emitter);
  }
  

  
}
