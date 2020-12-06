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
  NEW_USER,
  LOGIN,
  CHANGE_PASSWORD,
  CHANGE_PROFILE,
  CHANGE_CHANNEL,
  SEND_MESSAGE,
  REQUEST_MESSAGES,
  FRIEND_REQUEST,
  FRIEND_REQUEST_RESPONSE,
  GET_FILE,

  // server to client
  CLIENT_REQUEST_STATUS,
  CLIENT_INFO,
  MESSAGES,
  FILE,

}
