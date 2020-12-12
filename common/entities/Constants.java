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

  public static final RegexFilter NAME_FILTER = new RegexFilter(
    new String[] {
      "^.{1,20}$"
    },
    "Between 1 to 20 characters"
  );

  public static final RegexFilter PASSWORD_FILTER = new RegexFilter(
    new String[] {
      "^.{8,20}$",
      "^\\d$",
      "^[A-Za-z]$"
    },
    "Between 8 to 20 characters, containing at least 1 number and 1 letter"
  );

  public static final RegexFilter DESCRIPTION_FILTER = new RegexFilter(
    new String[] {
      "^.{0,50}$"
    },
    "Between 0 to 50 characters."
  );
}
