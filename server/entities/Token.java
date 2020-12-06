package server.entities;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

/**
 * [insert description]
 * <p>
 * Created on 2020.12.05.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class Token {
  private static final SecureRandom rand = new SecureRandom();
  private static final Base64.Encoder encoder = Base64.getEncoder();

  private static String generateToken() {
    byte[] randomBytes = new byte[24];
    rand.nextBytes(randomBytes);
    return encoder.encodeToString(randomBytes);
  }

  private String value;
  private Timestamp created;

  public Token() {
    this.value = generateToken();
    this.created = new Timestamp(System.currentTimeMillis());
  }

  public String getValue() {
    return this.value;
  }

  public Timestamp getCreatedTime() {
    return this.created;
  }
}
