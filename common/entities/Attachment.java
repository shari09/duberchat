package common.entities;

import java.io.Serializable;
import java.util.UUID;

import common.entities.Identifiable;

/**
 * Stores an attachment of any type.
 * Binary file serialized.
 * <p>
 * Created on 2020.12.08.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class Attachment implements Serializable, Identifiable {
  
  private static final long serialVersionUID = 1L;
  private String name;
  private String id;
  private byte[] data;

  public Attachment(String name, byte[] data) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.data = data;
  }

  @Override
  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public byte[] getData() {
    return this.data;
  }


}
