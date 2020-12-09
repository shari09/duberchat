package common.entities;

import java.io.Serializable;

/**
 * Contains the id and profile information of a user.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.1
 */

public class UserMetadata implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String userId;
  private String username;
  private UserStatus status;

  public UserMetadata(String userId, String username, UserStatus status) {
    this.userId = userId;
    this.username = username;
    this.status = status;
  }


  public String getUserId() {
    return this.userId;
  }

  public String getUsername() {
    return this.username;
  }

  public UserStatus getStatus() {
    return this.status;
  }
  
}
