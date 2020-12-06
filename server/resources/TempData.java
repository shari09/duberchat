package server.resources;

import java.io.BufferedWriter;
import java.util.concurrent.ConcurrentHashMap;

public class TempData {
  public static ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<String, BufferedWriter> clientConnections = new ConcurrentHashMap<>();
}
