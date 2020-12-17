package common.entities.payload;

/**
 * Represents a group of available payload types.
 * <p>
 * Created on 2020.12.05.
 * 
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public enum PayloadType {
  // --- client to server

  /** Creating a new user. */
  NEW_USER,

  /** Logging in as an existing user. */
  LOGIN,

  /** A password change. */
  CHANGE_PASSWORD,

  /**
   * A change in one of the user's profile fields
   * that are stored as strings (username, description, etc). 
   */
  CHANGE_PROFILE,

  /** An update on the user's status that is displayed to others. */
  UPDATE_STATUS,

  /** A change the channel's settings (name, etc). */
  CHANGE_CHANNEL,

  /** Sending a new message. */
  MESSAGE_TO_SERVER,

  /** Removing a message. */
  REMOVE_MESSAGE,

  /** Editing the content of a message. */
  EDIT_MESSAGE,

  /** Requesting an amount of message history in a channel. */
  REQUEST_MESSAGES,

  /** Sending a friend request to a specified user. */
  FRIEND_REQUEST,

  /** Canceling an outgoing friend request. */
  CANCEL_FRIEND_REQUEST,

  /** The response to a friend request. Either accepted or denied. */
  FRIEND_REQUEST_RESPONSE,

  /** Removing a friend. */
  REMOVE_FRIEND,

  /** Requesting to download a file that someone sent in a channel. */
  REQUEST_ATTACHMENT,
  
  /** Creating a new group channel. */
  CREATE_CHANNEL,

  /** Blocking a certain user knowing their username. */
  BLOCK_USER,

  /** Adding an user to the group channel. */
  ADD_PARTICIPANT,

  /** Removing an user from a group channel. */
  REMOVE_PARTICIPANT,

  /** Blacklisting a user from a group channel. */
  BLACKLIST_USER,

  /** Leaving a group channel. */
  LEAVE_CHANNEL,

  /** Transferring the ownership/admin to a different user in the group channel. */
  TRANSFER_OWNERSHIP,

  /** A payload with the only purpose of keeping the connection alive */
  KEEP_ALIVE,


  // ---server to client
  /**
   * If the client request was made successfully or if there
   * was an error (ex. not authorized).
   */
  CLIENT_REQUEST_STATUS,

  /** An initialization of the client's data. */
  CLIENT_INFO,

  /** An update on the user's friends info. */
  CLIENT_FRIENDS_UPDATE,

  /** An update on the channels the user is in. */
  CLIENT_CHANNELS_UPDATE,

  /** The requested messages from the client. */
  MESSAGES_TO_CLIENT,

  /** The requested file from the client. */
  ATTACHMENT_TO_CLIENT,

  /** An update on a message in a channel the user is in. */
  MESSAGE_UPDATE_TO_CLIENT,

  /** A message from server to all connected clients. */
  SERVER_BROADCAST,

}
