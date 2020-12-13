package server.gui;

import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import server.entities.Client;
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
    super("Connected Users");
    this.users = new ConcurrentHashMap<>();
    this.activate();
  }

  private void addClient(Client client) {
    String username = GlobalServices.users.getUsername(client.getUserId());
    JButton user = new JButton(username);
    this.users.put(client.getUserId(), user);
    super.addEntry(user, new JPanel());
  }

  private void removeClient(Object emitter) {
    String userId;
    if (emitter instanceof ObjectOutputStream) {
      ObjectOutputStream toClient = (ObjectOutputStream) emitter;
      userId = GlobalServices.clientConnections.getUserId(toClient);
    } else {
      userId = (String) emitter;
    }
    super.removeEntry(this.users.get(userId));
    this.users.remove(userId);
  }



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
    GlobalServices.serverEventQueue.subscribe(EventType.AUTHENTICATED_CLIENT, this);
    GlobalServices.serverEventQueue.subscribe(EventType.CLIENT_DISCONNECTED, this);
  }

  @Override
  public void onEvent(Object emitter, EventType eventType) {
    switch(eventType) {
      case AUTHENTICATED_CLIENT:
        this.addClient((Client)emitter);
        break;
      case CLIENT_DISCONNECTED:
        this.removeClient(emitter);
      default:
        break;
    }
  }
  

  
}
