package server.services;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

import common.entities.Token;

/**
 * Provides a static method for generating new tokens.
 * <p>
 * Created on 2020.12.06.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenService {
  private static final SecureRandom rand = new SecureRandom();
  private static final Base64.Encoder encoder = Base64.getEncoder();

  public static Token generateToken() {
    byte[] randomBytes = new byte[24];
    rand.nextBytes(randomBytes);
    String value = encoder.encodeToString(randomBytes);
    Timestamp created = new Timestamp(System.currentTimeMillis());
    return new Token(value, created);
  }
}
