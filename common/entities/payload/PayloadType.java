package common.entities.payload;

/**
 * Represents a group of available payload types.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public enum PayloadType {
  // client to server
  /** Creating a new user */
  NEW_USER,
  /** Logging in an existent user */
  LOGIN,
  /** Change password */
  CHANGE_PASSWORD,
  /** Change the user's profile fields that are stored as strings (username, description, etc) */
  CHANGE_PROFILE,
  /** Updates the user's status that is displayed to others */
  UPDATE_STATUS,
  /** Change the channel's settings (description, profile pic, etc) */
  CHANGE_CHANNEL,
  /** Sends a new message */
  MESSAGE_TO_SERVER,
  /** Remove a message */
  REMOVE_MESSAGE,
  /** Edit the content of a message */
  EDIT_MESSAGE,
  /** Requesting the history of messages for a channel. */
  REQUEST_MESSAGES,
  /** Sending a friend request to a specified user */
  FRIEND_REQUEST,
  /** Cancels an outgoing friend request */
  CANCEL_FRIEND_REQUEST,
  /** The response to a friend request. Either accepted or denied. */
  FRIEND_REQUEST_RESPONSE,
  /** Removes a friend */
  REMOVE_FRIEND,
  /** Request to download a file that someone sent in a channel. */
  REQUEST_ATTACHMENT,
  /** Create a new group channel */
  CREATE_CHANNEL,
  /** Block a certain user knowing their username */
  BLOCK_USER,
  /** Add an user to the group channel */
  ADD_PARTICIPANT,
  /** Remove an user from a group channel */
  REMOVE_PARTICIPANT,
  /** Blacklist a user from a group channel */
  BLACKLIST_USER,
  /** Leave the group channel */
  LEAVE_CHANNEL,
  /** Transfer the ownership/admin to a different user */
  TRANSFER_OWNERSHIP,
  /** A payload with no purpose other than keeping the connection alive */
  KEEP_ALIVE,

  // server to client
  /**
   * If the client request was made successfully or if there
   * was an error (ex. not authorized).
   */
  CLIENT_REQUEST_STATUS,
  /** Updates/Initializes the client's data. */
  CLIENT_INFO,
  /** Updates on the user's friends info. */
  CLIENT_FRIENDS_UPDATE,
  /** Updates on the users about the channels they're in */
  CLIENT_CHANNELS_UPDATE,
  /** The requested messages from the client */
  MESSAGES_TO_CLIENT,
  /** The requested file from the client */
  ATTACHMENT_TO_CLIENT,
  /** Update on a message in a channel the user is in */
  MESSAGE_UPDATE_TO_CLIENT,
  /** A message from server to all connected clients */
  SERVER_BROADCAST,

}
