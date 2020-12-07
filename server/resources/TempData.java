package server.resources;

import java.io.BufferedWriter;
import java.util.concurrent.ConcurrentHashMap;

import common.entities.Token;
import server.entities.ClientConnections;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class TempData {
  public static ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<>();
  public static ClientConnections clientConnections = new ClientConnections();
}
