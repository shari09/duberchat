package server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.Token;
import server.entities.User;
import server.resources.TempData;


/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.1.0
 * @since 1.0.0
 */

public class UsersService {
  private final String USERS_FILE_PATH = "clientdata/data/users.ser";
  private final String USERNAME_MAPPING_PATH = "clientdata/data/username-to-uid-mapping.ser";
  private final String USERS_ID_MAPPING_PATH = "clientdata/data/uid-to-username-mapping.ser";
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, String> usernameToUid;
  private ConcurrentHashMap<String, String> uidToUsername;
  private int numChanges = 0;
  private int bufferEntriesNum = 1;
  
  public UsersService() {    
    try {
      File usersFile = new File(this.USERS_FILE_PATH);
      if (!usersFile.exists()) {
        System.out.println("Created new save files");
        this.users = new ConcurrentHashMap<>();
        this.usernameToUid = new ConcurrentHashMap<>();
        this.uidToUsername = new ConcurrentHashMap<>();
        this.hardSave();
        return;
      }

      this.users = loadData(this.USERS_FILE_PATH);
      this.usernameToUid = loadData(this.USERNAME_MAPPING_PATH);
      this.uidToUsername = loadData(this.USERS_ID_MAPPING_PATH);

    } catch (Exception e) {
      System.out.println("Error loading the data");
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T loadData(String filePath) {
    T data = null;
    try {
      FileInputStream fileIn = new FileInputStream(filePath);
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      data = (T)(objIn.readObject());
      fileIn.close();
      objIn.close();
    } catch (Exception e) {
      System.out.println("Error loading " + filePath);
      e.printStackTrace();
    }
    return data;
  }

  private <T> void saveData(T data, String filePath) {
    try {
      FileOutputStream fileOut = new FileOutputStream(filePath);
      ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
      objOut.writeObject(data);
      fileOut.close();
      objOut.close();
    } catch (Exception e) {
      System.out.println("Error saving " + filePath);
      e.printStackTrace();
    }
  }

  public void save() {
    if (this.numChanges >= this.bufferEntriesNum) {
      this.hardSave();
    }
    this.numChanges = 0;
  }

  public synchronized void hardSave() {
    try {
      saveData(this.users, this.USERS_FILE_PATH);
      saveData(this.usernameToUid, this.USERNAME_MAPPING_PATH);
      saveData(this.uidToUsername, this.USERS_ID_MAPPING_PATH);
    } catch (Exception e) {
      System.out.println("Error saving the data");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private boolean usernameTaken(String username) {
    return this.usernameToUid.containsKey(username);
  }

  public String getUsername(String userId) {
    return this.uidToUsername.get(userId);
  }

  public String getUserId(String username) {
    return this.usernameToUid.get(username);
  }

  public void changeUsername(String uid, String newUsername) {
    String oldUsername = this.uidToUsername.get(uid);
    this.usernameToUid.remove(oldUsername);
    this.uidToUsername.put(uid, newUsername);
    this.usernameToUid.put(newUsername, uid);
    this.numChanges++;
    this.save();
  }

  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    if (!this.users.get(userId).hasPassword(oldPassword)) {
      return false;
    }
    this.users.get(userId).updatePassword(newPassword);
    this.numChanges++;
    this.save();
    return true;
  }

  public User authenticate(String username, String password) {
    if (this.usernameToUid.containsKey(username)) {
      String userId = this.usernameToUid.get(username);
      if (this.users.get(userId).hasPassword(password)) {
        return this.users.get(userId);
      }
      return null;
    }
    return null;
  }

  public boolean authenticateToken(String userId, Token token) {
    if (TempData.tokens.get(userId) == token) {
      return true;
    }
    return false;
  }

  public synchronized User add(String username, String password, String description) {
    if (this.usernameTaken(username)) {
      return null;
    }
    User user = new User(username, password, description);
    this.users.put(user.getUserId(), user);
    this.usernameToUid.put(username, user.getUserId());
    this.uidToUsername.put(user.getUserId(), username);
    this.numChanges++;
    this.save();
    return user;
  }

}
