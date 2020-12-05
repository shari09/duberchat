package common.entities.payload;

public abstract class ClientToServer extends Payload {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private int priority;

  public ClientToServer(PayloadType type, int priority) {
    super(type);
    this.priority = priority;
  }

  public int getPriority() {
    return this.priority;
  }

}
