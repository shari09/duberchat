package client;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ClientData;

public class GlobalClient {

  public static ConcurrentHashMap<Socket, ClientData> clientsInfo = new ConcurrentHashMap<Socket, ClientData>();
  
  public static boolean hasClientData(Socket socket) {
    return GlobalClient.clientsInfo.containsKey(socket);
  }
}
