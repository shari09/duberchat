package server.entities;

import java.util.concurrent.ConcurrentHashMap;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class TokensMap {
  private ConcurrentHashMap<String, Token> tokensMap;
  public TokensMap() {
    this.tokensMap = new ConcurrentHashMap<>();
  }

  public Token getToken(String userId) {
    if (!this.tokensMap.containsKey(userId)) {
      return null;
    }
    return this.tokensMap.get(userId);
  }

  public void addToken(String userId, Token token) {
    this.tokensMap.put(userId, token);
  }
}