package client;

/**
 * Represents an error message with a string of the error and a priority.
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

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
