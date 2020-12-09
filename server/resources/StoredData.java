package server.resources;

import server.services.MessagingService;
import server.services.UserService;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class StoredData {
  public static UserService users = new UserService();
  public static MessagingService channels = new MessagingService();
}
