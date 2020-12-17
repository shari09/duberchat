package common.entities;

import common.services.RegexValidator;

/**
 * Contains constants that are shared between the client and the server.
 * <p>
 * Created on 2020.12.08.
 * 
 * @author  Candice Zhang, Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Constants {

  public static final int HEARTBEAT_FREQUENCY = 1000*5;
  
  public static final int SOCKET_TIMEOUT = 1000*60*5;

  public static final String APPLICATION_NAME = "DQ";
  
  public static final String ICON_PATH = "common/assets/icon.png";
  
  public static final RegexValidator NAME_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{1,20}$"
    },
    "Name: between 1 to 20 characters."
  );

  public static final RegexValidator PASSWORD_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{8,20}$",
      "^.*(?=.*\\d).*$",
      "^.*(?=.*[a-z]).*$",
      "^.*(?=.*[A-Z]).*$"
    },
    "Password: between 8 to 20 characters, containing at least 1 number, 1 uppercase letter and 1 lowercase letter."
  );

  public static final RegexValidator DESCRIPTION_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{0,50}$"
    },
    "Description: between 0 to 50 characters."
  );

  public static final RegexValidator MESSAGE_VALIDATOR = new RegexValidator(
    new String[] {
      "^[\\s\\S]{0,200}$"
    },
    "Message: between 0 to 200 characters."
  );

  public static final RegexValidator FRIEND_REQUEST_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{0,20}$"
    },
    "Friend request message: between 0 to 20 characters."
  );
}
