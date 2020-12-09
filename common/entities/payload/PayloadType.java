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
  /** Change the user's profile settings (description, status, etc) */
  CHANGE_PROFILE,
  /** Change the channel's settings (description, profile pic, etc) */
  CHANGE_CHANNEL,
  /** Sends a new message */
  SEND_MESSAGE,
  /** Remove a message */
  REMOVE_MESSAGE,
  /** Edit the content of a message */
  EDIT_MESSAGE,
  /** Requesting the history of messages for a channel. */
  REQUEST_MESSAGES,
  /** Sending a friend request to a specified user */
  FRIEND_REQUEST,
  /** The response to a friend request. Either accepted or denied. */
  FRIEND_REQUEST_RESPONSE,
  /** Request to download a file that someone sent in a channel. */
  REQUEST_ATTACHMENT,
  /** A payload with no purpose other than keeping the connection alive */
  KEEP_ALIVE,

  // server to client
  /**
   * If the client request was made successfully or if there
   * was an error (ex. not authorized).
   */
  CLIENT_REQUEST_STATUS,
  /** The information of a client once logged in. */
  CLIENT_INFO,
  /** the requested messages from the client */
  MESSAGES_HISTORY,
  /** The requested file from the client */
  SEND_ATTACHMENT,

}
