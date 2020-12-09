package common.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Contains the metadata of a message, including content, sender, the channel it
 * is in, timestamp and attachment, if applicable.
 * <p>
 * Created on 2020.12.06.
 * 
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Message implements Serializable, Comparable<Message> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String messageId;
  private String content;
  private String senderId;
  private String channelId;
  private Timestamp created;
  private Timestamp edited;
  private String attachmentId;
  private String attachmentName;

  public Message(
    String content, 
    String senderId, 
    String channelId, 
    String attachmentId, 
    String attachmentName
  ) {
    this.messageId = UUID.randomUUID().toString();
    this.content = content;
    this.senderId = senderId;
    this.created = new Timestamp(System.currentTimeMillis());
    this.attachmentId = attachmentId;
    this.channelId = channelId;
    this.attachmentName = attachmentName;
  }

  public String getChannelId() {
    return channelId;
  }

  public int compareTo(Message other) {
    return other.getCreated().compareTo(this.created);
  }

  public String getMessageId() {
    return this.messageId;
  }

  public boolean hasAttachment() {
    return this.attachmentId != null;
  }

  public String getAttachmentName() {
    return this.attachmentName;
  }

  public Timestamp getEditedTime() {
    return this.edited;
  }

  public String getContent() {
    return this.content;
  }

  public String getSenderId() {
    return this.senderId;
  }

  public Timestamp getCreated() {
    return this.created;
  }

  public String getAttachmentId() {
    return this.attachmentId;
  }

  public void updateContent(String content) {
    this.content = content;
    this.edited = new Timestamp(System.currentTimeMillis());
  }

}
