package common.entities.payload;

public class NewUser extends ClientToServer {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;

  public NewUser(String username, String password, int priority) {
    super(PayloadType.NEW_USER, priority);
    this.username = username;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }
  
}
