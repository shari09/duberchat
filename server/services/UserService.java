package server.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.ChannelMetadata;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.UserStatus;
import server.entities.EventType;
import server.entities.LogType;
import server.entities.User;

/**
 * {@code UserService} deals with all user related actions.
 * <ul>
 * <li> saves user data to file
 * <li> loading in user data from file
 * <li> get username/userId
 * <li> password checking
 * <li> friend requests sending/cancelling/accepting/rejecting
 * <li> adding/leaving channels
 * <li> checking if two users are friends/blocked
 * <li> change profile data (username, password, description, status)
 * </ul>
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserService {
  private final String USERS_PATH = "database/users/users.ser";
  private final String USERNAME_MAPPING_PATH = "database/users/username-to-uid-mapping.ser";
  private final String USER_ID_MAPPING_PATH = "database/users/uid-to-username-mapping.ser";
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, String> usernameToId;
  private ConcurrentHashMap<String, String> idToUsername;
  /** Number of changes in user data */
  private int numChanges = 0;
  /**
   * Number of changes before a save operation.
   * <p>
   * It is set at 1 right now (save on every change) 
   * because properly data buffering to prevent data loss is not implemented. 
   * However, this is useful in the long run.
   */
  private int bufferEntriesNum = 1;

  /**
   * [constructor]
   */
  public UserService() {
    try {
      File usersFile = new File(this.USERS_PATH);
      if (!usersFile.exists()) {
        //log
        CommunicationService.log("Creating new user data files", LogType.SUCCESS);
        this.users = new ConcurrentHashMap<>();
        this.usernameToId = new ConcurrentHashMap<>();
        this.idToUsername = new ConcurrentHashMap<>();
        this.hardSave();
        return;
      }

      this.users = DataService.loadData(this.USERS_PATH);
      this.usernameToId = DataService.loadData(this.USERNAME_MAPPING_PATH);
      this.idToUsername = DataService.loadData(this.USER_ID_MAPPING_PATH);

    } catch (Exception e) {
      //log
      CommunicationService.log(
        "Loading user data files", LogType.ERROR
      );
    }
  }

  /**
   * Saves the user data once it reaches the buffer.
   * @see UserService#bufferEntriesNum
   */
  public void save() {
    this.numChanges++;
    if (this.numChanges >= this.bufferEntriesNum) {
      this.hardSave();
      this.numChanges = 0;
    }
  }

  /**
   * The actual method that saves the data to files
   * @see UserService#bufferEntriesNum
   * @see UserService#save()
   */
  public synchronized void hardSave() {
    try {
      DataService.saveData(this.users, this.USERS_PATH);
      DataService.saveData(this.usernameToId, this.USERNAME_MAPPING_PATH);
      DataService.saveData(this.idToUsername, this.USER_ID_MAPPING_PATH);
    } catch (Exception e) {
      //log
      CommunicationService.log(String.format(
        "Saving user data files: %s \n%s",
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
  }

  /**
   * If the username already exists.
   * @param username        the username
   * @return                whether or not it exists in the database
   */
  public boolean usernameExist(String username) {
    return this.usernameToId.containsKey(username);
  }

  /**
   * Gets the username of a user given their ID.
   * @param userId     the userId     
   * @return           the username, or null if the ID is invalid
   */
  public String getUsername(String userId) {
    if (!this.validUserId(userId)) {
      return null;
    }
    return this.idToUsername.get(userId);
  }

  /**
   * Gets the user ID given the user's username.
   * @param username    the username
   * @return            the user ID, or null if the username is invalid
   */
  public String getUserId(String username) {
    if (!this.usernameToId.containsKey(username)) {
      return null;
    }
    return this.usernameToId.get(username);
  }

  /**
   * Checks whether the userId is valid.
   * @param userId        the user ID
   * @return              whether or not the user ID is valid 
   */
  public boolean validUserId(String userId) {
    return this.users.containsKey(userId);
  }

  /**
   * Changes a given user's password.
   * <p>
   * It checks whether or not the user's old password is correct before changing.
   * 
   * @param userId      the user's userId
   * @param oldPassword the user's old password
   * @param newPassword the user's desired new password
   * @return            whether the change was a success
   */
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    if (!this.users.get(userId).hasPassword(oldPassword)) {
      return false;
    }
    this.users.get(userId).updatePassword(newPassword);
    this.save();
    return true;
  }

  /**
   * Authenticate the user given their username and password.
   * If they have an incorrect username or password,
   * they will fail the authentication.
   * 
   * @param username       the username
   * @param password       the password
   * @return               whether the user is successfully authenticated or not
   */
  public User authenticate(String username, String password) {
    if (this.usernameToId.containsKey(username)) {
      String userId = this.usernameToId.get(username);
      if (this.users.get(userId).hasPassword(password)) {
        return this.users.get(userId);
      }
      return null;
    }
    return null;
  }

  /**
   * Authenticates a token against their user ID.
   * The purpose of this is to prevent others who know their user ID
   * to send a request. Since tokens longer pseudorandom values
   * generated at connection, they're much safer. 
   * @param userId       the user ID
   * @param token        the user's token
   * @return             whether the user is authenticated or not
   */
  public boolean authenticateToken(String userId, Token token) {
    return (GlobalServices.tokens.get(userId).getValue().equals(token.getValue()));
  }

  /**
   * Create a new user entry.
   * If the username is already taken, the user will be asked to change
   * a desired username.
   * <p>
   * Emits a {@code EventType.NEW_USER} event that notifies subscribers of a new user.
   * @param username       the desired username
   * @param password       the desired password
   * @param description    the desired description
   * @return               whether the new user has successfully created or not
   */
  public User newUser(String username, String password, String description) {
    if (this.usernameExist(username)) {
      return null;
    }
    User user = new User(username, password, description);
    this.users.put(user.getId(), user);
    this.usernameToId.put(username, user.getId());
    this.idToUsername.put(user.getId(), username);
    GlobalServices.serverEventQueue.emitEvent(EventType.NEW_USER, 1, user.getMetdata());
    this.save();
    return user;
  }

  /**
   * Gets the {@link UserMetadata} of a user.
   * @param userId      the user ID
   * @return            the metadata
   */
  public UserMetadata getUserMetadata(String userId) {
    if (!this.validUserId(userId)) {
      return null;
    }
    return this.users.get(userId).getMetdata();
  }

  /**
   * Changes a user's username.
   * If the desired name is already taken, the user will not be allowed
   * to change into that username.
   * <p>
   * {@link UserService#broadcastChanges(User)}
   * @param userId          the user ID
   * @param newUsername     the new desired username
   * @return                whether or not the user has been 
   */
  public boolean changeUsername(String userId, String newUsername) {
    if (this.usernameExist(newUsername)) {
      return false;
    }
    String oldUsername = this.idToUsername.get(userId);
    this.usernameToId.remove(oldUsername);
    this.idToUsername.put(userId, newUsername);
    this.usernameToId.put(newUsername, userId);
    this.broadcastChanges(this.users.get(userId));
    this.save();
    return true;
  }

  /**
   * Updates the user's current status.
   * <p>
   * {@link UserService#broadcastChanges(User)}
   * @param userId            the user ID
   * @param status            the updated status
   */
  public void updateUserStatus(String userId, UserStatus status) {
    User user = this.users.get(userId);
    user.updateStatus(status);
    this.broadcastChanges(user);
    this.save();
  }

  /**
   * Sends a friend request to another user.
   * <p>
   * If the other user does not exist or the user is blocked by them,
   * the friend request will not send.
   * If the other person already sent them a request,
   * or if they already sent the other person a request,
   * this operation will fail as well.
   * 
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * 
   * @param userId          the user ID
   * @param recipientId     the recipient ID
   * @param msg             the request message
   * @return                if the friend request was successfully sent
   */
  public boolean sendFriendRequest(String userId, String recipientId, String msg) {
    if (
      recipientId == null 
      || !this.validUserId(recipientId) 
      || this.isBlocked(recipientId, userId)
    ) {
      return false;
    }

    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    if (user.hasOutgoingFriendRequest(friend.getMetdata())
       || user.hasIncomingFriendRequest(friend.getMetdata())) {
      return false;
    }
    user.addOutgoingFriendRequest(friend.getMetdata(), msg);
    friend.addIncomingFriendRequest(user.getMetdata(), msg);
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(friend);
    this.save();
    return true;
  }

  /**
   * Checks if one user has the other user blocked.
   * @param blockerId         the blocker ID 
   * @param toBeBlockedId     the blockee ID
   * @return                  whether the blocker blocked the blockee or not
   */
  public boolean isBlocked(String blockerId, String toBeBlockedId) {
    UserMetadata blockee = this.getUserMetadata(toBeBlockedId);
    return this.users.get(blockerId).getBlocked().contains(blockee);
  }

  /**
   * Get all the channels a user is in in forms of {@link ChannelMetadata}.
   * @param userId      the user ID
   * @return            the channels that the user is in.
   */
  public LinkedHashSet<ChannelMetadata> getChannels(String userId) {
    return this.users.get(userId).getChannels();
  }

  /**
   * Accepts an incoming friend request.
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * @param userId          the user accepting the request
   * @param requesterId     the user that sent the request
   * @return                whether the request has been successfully accepted
   */
  public boolean acceptFriendRequest(String userId, String requesterId) {
    if (!this.validUserId(requesterId)) {
      return false;
    }

    User user = this.users.get(userId);
    User requester = this.users.get(requesterId);
    user.addFriend(requester.getMetdata());
    requester.addFriend(user.getMetdata());
    user.removeIncomingFriendRequest(requester.getMetdata());
    requester.removeOutgoingFriendRequest(user.getMetdata());
    ChannelMetadata channel = GlobalServices.channels.createPrivateChannel(
      user.getMetdata(),
      requester.getMetdata()
    );
    user.addChannel(channel);
    requester.addChannel(channel);
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(requester);
    return true;
  }

  /**
   * Adds a {@link ChannelMetadata} to the user.
   * @param userId      the user ID
   * @param channel     the channel metadata
   */
  public void addChannel(String userId, ChannelMetadata channel) {
    User user = this.users.get(userId);
    user.addChannel(channel);
    this.save();
  }

  /**
   * Leaves a specified channel.
   * @param userId       the user ID
   * @param channel      the channel's {@link ChannelMetadata}
   */
  public void leaveChannel(String userId, ChannelMetadata channel) {
    this.users.get(userId).removeChannel(channel);
  }

  /**
   * Rejects an incoming user request.
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * @param userId           the user rejecting the request
   * @param requesterId      the user that sent the request
   * @return                 whether the request was successfully rejected
   */
  public boolean rejectFriendRequest(String userId, String requesterId) {
    if (!this.validUserId(requesterId)) {
      return false;
    }

    User user = this.users.get(userId);
    User requester = this.users.get(requesterId);
    user.removeIncomingFriendRequest(requester.getMetdata());
    requester.removeOutgoingFriendRequest(user.getMetdata());
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(requester);
    this.save();
    return true;
  }

  /**
   * Cancels an outgoing friend request.
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * @param userId           the user cancelling the request
   * @param recipientId      the user the request was sent to
   * @return                 whether the request was successfully cancelled
   */
  public boolean cancelFriendRequest(String userId, String recipientId) {
    if (!this.validUserId(recipientId)) {
      return false;
    }

    User user = this.users.get(userId);
    User friend = this.users.get(recipientId);
    user.removeOutgoingFriendRequest(friend.getMetdata());
    friend.removeIncomingFriendRequest(user.getMetdata());
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(friend);
    this.save();
    return true;
  }

  /**
   * Updates the description on a user's profile.
   * <p>
   * {@link UserService#broadcastChanges(User)}
   * @param userId          the user ID
   * @param description     the new description
   * @see                   User
   */
  public void updateDescription(String userId, String description) {
    User user = this.users.get(userId);
    user.updateDescription(description);
    this.broadcastChanges(user);
    this.save();
  }

  /**
   * Removes a friend.
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * @param userId      the user removing the friend
   * @param friendId    the friend the user is trying to remove
   */
  public void removeFriend(String userId, String friendId) {
    User user = this.users.get(userId);
    User friend = this.users.get(friendId);
    if (friend == null) {
      return;
    }
    user.removeFriend(friend.getMetdata());
    friend.removeFriend(friend.getMetdata());
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(friend);
    this.save();
  }

  /**
   * Checks to see if one user is friends with another.
   * @param userA        the first user
   * @param userB        the second user
   * @return             whether they are friends
   */
  public boolean isFriend(String userA, String userB) {
    if (!this.validUserId(userB)) {
      return false;
    }
    return this.users.get(userA).hasFriend(this.users.get(userB).getMetdata());
  }

  /**
   * Blocking a user. 
   * If the user they are trying to block does not exist, 
   * this method will return false.
   * <p>
   * {@link UserService#notifyFriendsUpdate(User)}
   * @param blockerId           the user that is trying to block the other user
   * @param blockeeUsername     the user that is being blocked
   * @return                    whether the other user is successfully blocked
   */
  public boolean blockUser(String blockerId, String blockeeUsername) {
    if (!this.usernameExist(blockeeUsername)) {
      return false;
    }
    User user = this.users.get(blockerId);
    User blocked = this.users.get(this.usernameToId.get(blockeeUsername));
    user.removeFriend(blocked.getMetdata());
    blocked.removeFriend(user.getMetdata());
    user.addBlocked(blocked.getMetdata());
    this.notifyFriendsUpdate(user);
    this.notifyFriendsUpdate(blocked);
    this.save();
    return true;
  }

  /**
   * Changes the description of a user on their profile.
   * <p>
   * {@link UserService#broadcastChanges(User)}
   * @param userId             the user ID
   * @param description        the new description
   * @return                   whether the change was successful or not
   */
  public boolean changeDescription(String userId, String description) {
    User user = this.users.get(userId);
    user.updateDescription(description);
    this.broadcastChanges(user);
    this.save();

    return true;
  }

  /**
   * Broadcast changes to everyone they are in a channel with.
   * This is usually profile changes and not friendship changes.
   * This includes all their friends, and people they share a group channel with.
   * @param user       the user that had changes
   */
  private void broadcastChanges(User user) {
    Iterator<ChannelMetadata> channelsItr = user.getChannels().iterator();
    while (channelsItr.hasNext()) {
      GlobalServices.serverEventQueue.emitEvent(
        EventType.CHANNEL_UPDATE, 
        1, 
        channelsItr.next()
      );
    }

    Iterator<UserMetadata> itr = user.getFriends().iterator();
    while (itr.hasNext()) {
      User friend = this.users.get(itr.next().getUserId());
      GlobalServices.serverEventQueue.emitEvent(
        EventType.FRIEND_UPDATE, 
        1, 
        friend
      );
    }
  }

  /**
   * Gets the {@link UserMetadata} of all users that are in the database.
   * @return      all users
   */
  public ArrayList<UserMetadata> getAllUsers() {
    ArrayList<UserMetadata> users = new ArrayList<>();
    for (User user: this.users.values()) {
      users.add(user.getMetdata());
    }
    return users;
  }

  /**
   * Notifies a user's of their changes of friendship.
   * This may be due to sending/cancelling/accepting/reject friend requests,
   * and block/remove friend.
   * @param user         the user that has an update of their friendship
   */
  private void notifyFriendsUpdate(User user) {
    GlobalServices.serverEventQueue.emitEvent(EventType.FRIEND_UPDATE, 1, user);
  }

}
