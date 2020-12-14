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

public class UserMetadata implements Serializable, Comparable<UserMetadata> {
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

  public void updateUsername(String username) {
    this.username = username;
  }

  public String getDescription() {
    return this.description;
  }

  public void updateDescription(String description) {
    this.description = description;
  }
  
  public UserStatus getStatus() {
    return this.status;
  }

  public void updateStatus(UserStatus status) {
    this.status = status;
  }

  public int compareTo(UserMetadata other) {
    return other.getUsername().compareTo(this.getUsername());
  }
  
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof UserMetadata)) {
      return false;
    }
    UserMetadata user = (UserMetadata)other;
    return (this.userId.equals(user.getUserId()));
  }

  public int hashCode() {
    return this.userId.hashCode();
  }


}
