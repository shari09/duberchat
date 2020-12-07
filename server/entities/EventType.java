package server.entities;

/**
 * Contains a group of available event types.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.1
 * @since 1.0.0
 */

public enum EventType {
  PAYLOAD,
  NEW_CLIENT,
  AUTHENTICATED_PAYLOAD,
  AUTHENTICATED_CLIENT,
  CLIENT_DISCONNECTED,
}
