package server.entities;

/**
 * The type of log it is.
 * <p>
 * Created on 2020.12.15.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public enum LogType {
  /** Success operation sending/receiving */
  SUCCESS,
  /** An error on client's end such as incorrect username/password */
  CLIENT_ERROR,
  /** Server throwing errors */
  SERVER_ERROR,
  /** Connecting and disconnecting */
  CONNECTION,
  /** Server starting */
  SERVER,
  /** Processing */
  PROCESSING
}
