package server.resources;

import java.io.BufferedWriter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class TempData {
  public static ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<String, BufferedWriter> clientConnections = new ConcurrentHashMap<>();
}
