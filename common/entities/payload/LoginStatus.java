package common.entities.payload;

import java.util.ArrayList;

public class LoginStatus extends Payload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String token;
  private ArrayList<String> friends;
  private ArrayList<String> incomingFriendRequests;
  private ArrayList<String> outgoingFriendRequests;
  private ArrayList<String> channels;
  private String errorMsg;

  public LoginStatus(String errorMsg) {
    super(PayloadType.LOGIN_STATUS);
    this.errorMsg = errorMsg;
  }

  public LoginStatus(
    String token, 
    ArrayList<String> friends,
    ArrayList<String> incomingFriendRequests,
    ArrayList<String> outgoingFriendRequests,
    ArrayList<String> channels
  ) {
    super(PayloadType.LOGIN_STATUS);
    this.token = token;
    this.friends = friends;
    this.incomingFriendRequests = incomingFriendRequests;
    this.outgoingFriendRequests = outgoingFriendRequests;
    this.channels = channels;
  }


  public String getToken() {
    return this.token;
  }

  public ArrayList<String> getFriends() {
    return this.friends;
  }

  public ArrayList<String> getIncomingFriendRequests() {
    return this.incomingFriendRequests;
  }

  public ArrayList<String> getOutgoingFriendRequests() {
    return this.outgoingFriendRequests;
  }

  public ArrayList<String> getChannels() {
    return this.channels;
  }

  public String getErrorMsg() {
    return this.errorMsg;
  }

}
