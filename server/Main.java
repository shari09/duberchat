package server;

import server.services.SocketService;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Main {
  public static void main(String[] args) {
    new SocketService().run();
  }
}
