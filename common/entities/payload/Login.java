package common.entities.payload;

public class Login extends ClientToServer {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;

  public Login(String username, String password, int priority) {
    super(PayloadType.LOGIN, priority);
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
