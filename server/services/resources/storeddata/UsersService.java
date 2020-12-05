package server.services.resources.storeddata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import server.entities.UserData;
import server.entities.Token;

public class UsersService {
  private final String FILE_PATH = "/clientdata/data/users.ser";
  private ConcurrentHashMap<String, UserData> users;
  private int numNewEntries = 0;
  private int bufferEntriesNum = 2;

  public UsersService() {    
    try {
      File file = new File(FILE_PATH);
      if (!file.exists()) {
        this.users = new ConcurrentHashMap<>();
        this.save();
        return;
      }

      FileInputStream fileIn = new FileInputStream(file);
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      this.users = (ConcurrentHashMap<String, UserData>)(objIn.readObject());

      fileIn.close();
      objIn.close();
    } catch (Exception e) {
      System.out.println("Error loading the data");
    }
  }

  public void save() {
    try {
      FileOutputStream fileOut = new FileOutputStream(this.FILE_PATH);
      // ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
      // objOut.writeObject(users);
      // objOut.close();
      // fileOut.close();
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println("SKDJFl");
      System.out.println(e.getMessage());
    }
  }

  public boolean usernameTaken(String username) {
    return this.users.containsKey(username);
  }

  public Token authenticate(String username, String password) {
    if (this.users.containsKey(username)) {
      if (this.users.get(username).hasPassword(password)) {
        return new Token();
      }
      return null;
    }
    return null;
  }

  public Token add(String username, String password) {
    this.numNewEntries++;
    users.put(username, new UserData(username, password));
    if (this.numNewEntries >= this.bufferEntriesNum) {
      this.save();
    }
    return new Token();
  }
}
