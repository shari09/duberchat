package common.entities.payload;

public class NewUser extends Payload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;

  public NewUser(String username, String password) {
    super(PayloadType.NEW_USER);
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
