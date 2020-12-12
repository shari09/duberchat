package client.resources;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import common.entities.ClientData;
import common.entities.Message;

/**
 * [description]
 * <p>
 * Created on 2020.12.08.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalClient {
  private static final String DOWNLOAD_ROOT_FOLDER_PATH = "downloads/";
  private static final String DEFAULT_DOWNLOAD_FOLDER_PATH = "guest/";
  
  public static ClientData clientData = null;
  
  public static ConcurrentHashMap<String, ConcurrentSkipListSet<Message>> messagesData = new ConcurrentHashMap<>();
  
  public static boolean hasData() {
    return GlobalClient.clientData != null;
  }

  public static String getDownloadFolderPath() {
    if (!GlobalClient.hasData()) {
      return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.DEFAULT_DOWNLOAD_FOLDER_PATH;
    }
    return GlobalClient.DOWNLOAD_ROOT_FOLDER_PATH + GlobalClient.clientData.getUserId() + "/";
  }

}
