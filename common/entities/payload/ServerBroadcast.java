package common.entities.payload;

/**
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServerBroadcast extends Payload {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String msg;
  public ServerBroadcast(String msg) {
    super(PayloadType.SERVER_BROADCAST, 5);
    this.msg = msg;

  }

  public String getMsg() {
    return this.msg;
  }
}
