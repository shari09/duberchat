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
  MESSAGE_TO_SERVER,
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
  /** Create a new group channel */
  CREATE_CHANNEL,
  /** Block a certain user knowing their username */
  BLOCK_USER,
  /** Add an user to the group channel */
  ADD_PARTICIPANTS_TO_CHANNEL,
  /** Remove an user from a group channel */
  REMOVE_PARTICIPANT,
  /** Blacklist a user from a group channel */
  BLACKLIST_PARTICIPANT,
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
  /** Updates on the user's about the channel's they're in */
  CLIENT_CHANNELS_UPDATE,
  /** the requested messages from the client */
  MESSAGES_TO_CLIENT,
  /** The requested file from the client */
  ATTACHMENT_TO_CLIENT,
  /** 
   * An update on the user's friends.
   * <ul>
   * <li> sent new friend request
   * <li> cancelled friend request
   * <li> accepted friend request
   * <li> rejected friend request
   * <li> blocked (removed as friends) 
   * <li> new channel
   * <li> removed from channel
   * </ul>
   */
  CLIENT_INFO_UPDATE,

}
