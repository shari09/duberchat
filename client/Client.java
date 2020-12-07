package client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.entities.payload.ClientInfo;
import common.entities.payload.ClientRequestStatus;
import common.entities.payload.Login;
import common.entities.payload.NewUser;
import common.entities.payload.Payload;

/**
 * The client side of the chat program.
 * <p>
 * Created on 2020.12.04.
 * @author Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Client {
  Socket socket;
  ObjectOutputStream output;
  boolean running = true;
  ObjectInputStream input;
  InputStream rawInput;

  public void run() {
    System.out.println("Trying to connect...");
    try {
      this.socket = new Socket("127.0.0.1", 5000);
      this.rawInput = this.socket.getInputStream();
      this.input = new ObjectInputStream(this.rawInput);

      this.output = new ObjectOutputStream(this.socket.getOutputStream());
    } catch (Exception e) {
      System.out.println("Failed to connect to server");
    }

    System.out.println("Connected to server");
    Login user = new Login(1, "shari09", "123456");
    Login user2 = new Login(1, "shari09", "12346");

    try {
      this.output.writeObject(user);
      this.output.flush();
      this.output.writeObject(user2);
      this.output.flush();
      System.out.println("Payload sent");
    } catch (Exception e) {
      System.out.println("error sending obj to server");
      e.printStackTrace();
    }

    
    while (running) {
      try {
        if (this.rawInput.available() > 0) {
          Payload payload = (Payload)this.input.readObject();
          System.out.println("Response received");
          System.out.println(payload.toString());
          switch (payload.getType()) {
            case CLIENT_REQUEST_STATUS:
              System.out.println(((ClientRequestStatus)payload).hasError());
              break;
            case CLIENT_INFO:
              System.out.println(((ClientInfo)payload).getToken());
              break;
            default:
              break;
          
          }
          // running = false;
        }
      } catch (Exception e) {
        System.out.println("Failed to receive response from server");
        e.printStackTrace();
      }
    }
    
    try {
      this.socket.close();
      this.input.close();
      this.output.close();
    } catch (Exception e) {
      System.out.println("Failed to close sockets");
    }

  }

  public static void main(String[] args) {
    new Client().run();
  }
}
