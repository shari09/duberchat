package server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataService {
  /**
   * Loads a file from the specified path.
   * @param <T>
   * @param filePath
   * @return           file data or null
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
      System.out.println("Error loading " + filePath);
      e.printStackTrace();
    }
    return data;
  }

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
      System.out.println("Error saving " + filePath);
      e.printStackTrace();
    }
  }
}
