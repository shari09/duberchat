package client;

public class ErrorMessage implements Comparable<ErrorMessage> {

  private final int priority;
  private final String message;

  public ErrorMessage(int priority, String message) {
    this.priority = priority;
    this.message = message;
  }

  @Override
  public int compareTo(ErrorMessage other) {
    return this.priority - other.getPriority();
  }

  public int getPriority() {
    return this.priority;
  }

  public String getMessage() {
    return this.message;
  }

}
