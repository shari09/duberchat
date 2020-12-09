package client;

/**
 * [description]
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientThread extends Thread {
  private ClientSocket socket;

  public ClientThread(ClientSocket socket) {
    super(socket);
    this.socket = socket;
  }

  public synchronized ClientSocket getSocket() {
    return this.socket;
  }
  
}
