package common.entities.payload;

/**
 * A payload from client to server that
 * contains the data for logging in as an existing user.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Login extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String username;
  private final String password;

  public Login(int priority, String username, String password) {
    super(PayloadType.LOGIN, priority);

    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return password;
  }
  
}
