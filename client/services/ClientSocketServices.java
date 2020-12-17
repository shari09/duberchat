package client.services;

import common.entities.payload.Payload;
import common.entities.payload.client_to_server.BlockUser;
import common.entities.payload.client_to_server.ChangeChannel;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.CreateChannel;

/**
 * Contains static methods to retrieve/update the user's active time
 * and to return the notification message for successful client request payloads.
 * <p>
 * Created on 2020.12.14.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientSocketServices {
  /** The latest time the user is reported active, in milliseconds. */
  private static long lastActiveTimeMills = System.currentTimeMillis();

  /**
   * Sets the last active time of the user to the current time.
   */
  public static void updateLastActiveTime() {
    ClientSocketServices.lastActiveTimeMills = System.currentTimeMillis();
  }

  /**
   * Returns the latest time the user is reported active, in milliseconds.
   * @return The latest time the user is reported active, in milliseconds.
   */
  public static long getLastActiveTimeMills() {
    return ClientSocketServices.lastActiveTimeMills;
  }

  /**
   * Return the notification message for successful client request payloads.
   * @param payload The payload used to determine the notification message.
   */
  public static String getRequestSuccessNotifMessage(Payload payload) {
    String strToReturn = "";

    switch (payload.getType()) {
      case ADD_PARTICIPANT:
        strToReturn = "Successfully added participant to channel";
        break;

      case BLACKLIST_USER:
        strToReturn = "Successfully blacklisted user";
        break;

      case BLOCK_USER:
        strToReturn = "Successfully blocked " + ((BlockUser)payload).getBlockUsername();
        break;

      case CANCEL_FRIEND_REQUEST:
        strToReturn = "Successfully cancelled friend request";
        break;

      case CHANGE_CHANNEL:
        strToReturn = "Successfully changed channel ";
        switch (((ChangeChannel)payload).getFieldToChange()) {
          case NAME:
            strToReturn += "name";
        }
        strToReturn += " to " + ((ChangeChannel)payload).getNewValue();
        break;

      case CHANGE_PASSWORD:
        strToReturn = "Successfully changed password";
        break;

      case CHANGE_PROFILE:
        strToReturn = "Successfully changed ";
        switch (((ChangeProfile)payload).getFieldToChange()) {
          case USERNAME:
            strToReturn += "name";
          case DESCRIPTION:
            strToReturn += "description";
        }
        strToReturn += " to " + ((ChangeProfile)payload).getNewValue();
        break;

      case CREATE_CHANNEL:
        strToReturn = "Successfully created channel: " + ((CreateChannel)payload).getName();
        break;

      case EDIT_MESSAGE:
        strToReturn = "Successfully edited message";
        break;

      case FRIEND_REQUEST:
        strToReturn = "Successfully sent friend request";
        break;
      
      case FRIEND_REQUEST_RESPONSE:
        strToReturn = "Successfully responded to friend request";
        break;

      case KEEP_ALIVE:
        strToReturn = "Successfully refreshed inactivity timing";
        break;

      case LEAVE_CHANNEL:
        strToReturn = "Successfully left channel";
        break;
      
      case LOGIN:
        strToReturn = "Successfully logged in";
        break;

      case MESSAGE_TO_SERVER:
        strToReturn = "Successfully sent message";
        break;
      
      case NEW_USER:
        strToReturn = "Successfully created account and logged in";
        break;

      case REMOVE_FRIEND:
        strToReturn = "Successfully removed friend";

      case REMOVE_MESSAGE:
        strToReturn = "Successfully removed message";
        break;

      case REMOVE_PARTICIPANT:
        strToReturn = "Successfully removed participant from channel";
        break;

      case REQUEST_ATTACHMENT:
        strToReturn = "Successfully received attachment";
        break;

      case REQUEST_MESSAGES:
        strToReturn = "Successfully received requested messages";
        break;

      case TRANSFER_OWNERSHIP:
        strToReturn = "Successfully transferred ownership";
        break;

      case UPDATE_STATUS:
        strToReturn = "Successfully updated status";
        break;
      
      default:
        strToReturn = "Received server updates";
    }
    return strToReturn;
  }

}