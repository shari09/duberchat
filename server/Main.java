package server;

import server.services.SocketService;

public class Main {
  public static void main(String[] args) {
    new SocketService().run();
  }
}
