package common.entities;

/**
 * [description]
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Constants {

  public static final int HEARTBEAT_FREQUENCY = 1000*5;
  
  public static final int SOCKET_TIMEOUT = 1000*30;

  public static final RegexValidator NAME_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{1,20}$"
    },
    "Between 1 to 20 characters."
  );

  public static final RegexValidator PASSWORD_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{8,20}$",
      "^.*(?=.*\\d).*$",
      "^.*(?=.*[a-z]).*$",
      "^.*(?=.*[A-Z]).*$"
    },
    "Between 8 to 20 characters, containing at least 1 number, 1 uppercase letter and 1 lowercase letter."
  );

  public static final RegexValidator DESCRIPTION_VALIDATOR = new RegexValidator(
    new String[] {
      "^.{0,50}$"
    },
    "Between 0 to 50 characters."
  );
}
