package server.entities;

import java.util.concurrent.ConcurrentHashMap;

import server.entities.Token;

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