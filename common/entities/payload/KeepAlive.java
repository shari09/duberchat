package common.entities.payload;

/**
 * A payload for the sake of keeping the socket connection alive.
 * <p>
 * Created on 2020.12.07.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class KeepAlive extends Payload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public KeepAlive() {
    super(PayloadType.KEEP_ALIVE, 1);
  }
}
