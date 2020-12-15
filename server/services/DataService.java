package server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import server.entities.LogType;

/**
 * Loading/saving data.
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataService {
  /**
   * Loads a file from the specified path.
   * @param <T>        the type of object
   * @param filePath   the file path
   * @return           file data or null (file does not exist)
   */
  @SuppressWarnings("unchecked")
  public synchronized static <T> T loadData(String filePath) {
    T data = null;
    try {
      FileInputStream fileIn = new FileInputStream(filePath);
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      data = (T)(objIn.readObject());
      fileIn.close();
      objIn.close();
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Loading %s: %s \n%s",
        filePath, 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
    return data;
  }

  /**
   * Serializes and saves data to a specific path.
   * @param <T>        the type of object
   * @param data       the object/data being saved
   * @param filePath   the file path to save the data to
   */
  public synchronized static <T> void saveData(T data, String filePath) {
    try {
      new File(filePath).getParentFile().mkdirs();
      FileOutputStream fileOut = new FileOutputStream(filePath);
      ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
      objOut.reset();
      objOut.writeObject(data);
      fileOut.close();
      objOut.close();
    } catch (Exception e) {
      CommunicationService.log(String.format(
        "Saving %s: %s \n%s",
        filePath, 
        e.getMessage(),
        CommunicationService.getStackTrace(e)
      ), LogType.ERROR);
    }
  }
}
