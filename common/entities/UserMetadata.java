package common.entities;

import java.io.Serializable;

/**
 * Contains the id and profile information of a user.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserMetadata implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String userId;
  private String username;
  private String description;
  private UserStatus status;

  public UserMetadata(
    String userId,
    String username,
    String description,
    UserStatus status
  ) {
    this.userId = userId;
    this.username = username;
    this.description = description;
    this.status = status;
  }


  public String getUserId() {
    return this.userId;
  }

  public String getUsername() {
    return this.username;
  }

  public String getDescription() {
    return this.description;
  }
  
  public UserStatus getStatus() {
    return this.status;
  }
  
}
