package client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.entities.payload.NewUser;
import common.entities.payload.Payload;

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
    NewUser user = new NewUser("shari09", "123456", 1);
    NewUser user2 = new NewUser("shari09", "123456", 1);

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
