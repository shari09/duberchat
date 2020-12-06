package server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import server.entities.*;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UsersService {
  private final String USERS_FILE_PATH = "clientdata/data/users.ser";
  private final String USERS_ID_MAPPING_PATH = "clientdata/data/username-to-uid-mapping.ser";
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, String> usernameToUid;
  private int numNewEntries = 0;
  private int bufferEntriesNum = 1;

  @SuppressWarnings("unchecked")
  public UsersService() {    
    try {
      File usersFile = new File(USERS_FILE_PATH);
      File idMappingFile = new File(USERS_ID_MAPPING_PATH);
      if (!usersFile.exists()) {
        System.out.println("Created new save file");
        this.users = new ConcurrentHashMap<>();
        this.usernameToUid = new ConcurrentHashMap<>();
        this.save();
        return;
      }
      //read in userId to userData mapping
      FileInputStream fileIn = new FileInputStream(usersFile);
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      this.users = (ConcurrentHashMap<String, User>)(objIn.readObject());
      fileIn.close();
      objIn.close();

      //read in username to user id mapping
      fileIn = new FileInputStream(idMappingFile);
      objIn = new ObjectInputStream(fileIn);
      this.usernameToUid = (ConcurrentHashMap<String, String>)(objIn.readObject());
      fileIn.close();
      objIn.close();
    } catch (Exception e) {
      System.out.println("Error loading the data");
    }
  }

  public synchronized void save() {
    try {
      //save userId to userData mapping
      FileOutputStream fileOut = new FileOutputStream(this.USERS_FILE_PATH);
      ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
      objOut.writeObject(this.users);
      fileOut.close();
      objOut.close();
      //save username to userId mapping
      fileOut = new FileOutputStream(this.USERS_ID_MAPPING_PATH);
      objOut = new ObjectOutputStream(fileOut);
      objOut.writeObject(this.usernameToUid);
      objOut.close();
      fileOut.close();
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private boolean usernameTaken(String username) {
    return this.usernameToUid.containsKey(username);
  }

  public Token authenticate(String username, String password) {
    if (this.usernameToUid.containsKey(username)) {
      String userId = this.usernameToUid.get(username);
      if (this.users.get(userId).hasPassword(password)) {
        return new Token();
      }
      return null;
    }
    return null;
  }

  public synchronized Token add(String username, String password, String description) {
    if (this.usernameTaken(username)) {
      return null;
    }
    this.numNewEntries++;
    User user = new User(username, password, description);
    this.users.put(user.getUserId(), user);
    this.usernameToUid.put(username, user.getUserId());
    if (this.numNewEntries >= this.bufferEntriesNum) {
      this.save();
    }
    return new Token();
  }

}
