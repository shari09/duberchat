package common.entities.payload;

/**
 * A payload from client to server that
 * contains the data for registering a new user.
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class NewUser extends Payload {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  private final String username;
  private final String password;
  private final String description;

  public NewUser(
    int priority,
    String username,
    String password,
    String description
  ) {
    super(PayloadType.NEW_USER, priority);

    this.username = username;
    this.password = password;
    this.description = description;
  }

  public String getPassword() {
    return this.password;
  }

  public String getUsername() {
    return this.username;
  }
  
  public String getDescription() {
    return this.description;
  }
}
