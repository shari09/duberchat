package server.entities;

/**
 * Contains a group of available event types.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public enum EventType {
  /** A new payload is received (no specified client). */
  PAYLOAD,
  /** A new client connection is received by the server socket. */
  NEW_CLIENT,
  /** A newly registered user */
  NEW_USER,
  /**
   * Once the payload is authenticated,
   * meaning the token matches with what the server sent back originally.
   */
  AUTHENTICATED_PAYLOAD,

  /**
   * Once the client has successfully authenticated itself either
   * through logging in or creating a new user.
   */
  AUTHENTICATED_CLIENT,
  /** Once the client socket disconnects. */
  CLIENT_DISCONNECTED,
  /** When the server is ready to remove the client from all its cache data */
  REMOVE_CLIENT_CONNECTION,
  /** New message sent */
  NEW_MESSAGE,
  /** Message edited */
  EDIT_MESSAGE,
  /** Message removed */
  REMOVE_MESSAGE,
  /** New, accepting, rejecting, or cancelling friend request */
  FRIEND_UPDATE,
  /** Any updates relating to channels (updating, removing users, etc) */
  CHANNEL_UPDATE,
  /** When an user leaves a channel */
  LEFT_CHANNEL,

  /** New information for logging */
  NEW_LOG,

  //gui events
  /** A change in a user's profile, used for server gui user list updates */
  PROFILE_CHANGE,
  /** Active/connected users tab */
  USERS_TAB,
  /** Server logs */
  LOGS_TAB,
  /** Admin tab */
  ADMIN_TAB,
  /** Select an entry to view details about */
  ENTRY_SELECTED,
  /** Broadcast to selected users */
  BROADCAST,
  /** Disconnects selected users */
  DISCONNECT,
  
}
