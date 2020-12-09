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
  /** New message sent */
  NEW_MESSAGE,
  /** Message edited */
  EDIT_MESSAGE,
  /** Message removed */
  REMOVE_MESSAGE,
  /** New, accepting, rejecting, or cancelling friend request */
  FRIEND_REQUEST,
}
