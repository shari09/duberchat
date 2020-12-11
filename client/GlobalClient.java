package client;

import common.entities.ClientData;

/**
 * [description]
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalClient {

  public static ClientData clientData = null;
  
  public static boolean hasData() {
    return GlobalClient.clientData != null;
  }
}
