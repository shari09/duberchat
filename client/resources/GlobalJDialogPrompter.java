package client.resources;

import java.util.Iterator;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import client.entities.ClientSocket;
import client.services.UserServices;

import java.awt.Component;
import java.beans.Expression;
import java.util.LinkedHashSet;

import common.entities.payload.RequestAttachment;
import common.entities.payload.EditMessage;
import common.entities.payload.RemoveMessage;
import common.entities.payload.ChangeProfile;
import common.entities.payload.ChangePassword;
import common.entities.payload.LeaveChannel;
import common.entities.payload.AddParticipant;
import common.entities.payload.BlacklistUser;
import common.entities.payload.RemoveParticipant;
import common.entities.payload.TransferOwnership;
import common.entities.payload.BlockUser;
import common.entities.payload.FriendRequestResponse;
import common.entities.ProfileField;
import common.entities.Constants;
import common.entities.GroupChannelMetadata;
import common.entities.Message;
import common.entities.Token;
import common.entities.UserMetadata;
/**
 * [description]
 * <p>
 * Created on 2020.12.13.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalJDialogPrompter {
  public static synchronized void promptChangeUsername(
    Component parentComponent,
    ClientSocket clientSocket
  ) {
    String curUsername = GlobalClient.clientData.getUsername();
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String newUsername = JOptionPane.showInputDialog(
      parentComponent,
      "New Username: ",
      curUsername
    );

    if (
      (newUsername == null)
      || (newUsername.length() == 0)
      || (newUsername == curUsername)
    ) {
      return;
    }

    if ((!Constants.NAME_VALIDATOR.matches(newUsername))) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "New username does not meet requirements:"
        + "\n" + Constants.NAME_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    clientSocket.sendPayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.USERNAME,
        newUsername
      )
    );
  }

  public static synchronized void promptChangePassword(
    Component parentComponent,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    JLabel oldPassLabel = new JLabel("Enter old password:");
    JPasswordField oldPassField = new JPasswordField(20);
    JLabel newPassLabel = new JLabel("Enter new password:");
    JPasswordField newPassField = new JPasswordField(20);
    JLabel confirmPassLabel = new JLabel("Re-type new password:");
    JPasswordField confirmPassField = new JPasswordField(20);
    panel.add(oldPassLabel);
    panel.add(oldPassField);
    panel.add(newPassLabel);
    panel.add(newPassField);
    panel.add(confirmPassLabel);
    panel.add(confirmPassField);
    String[] options = new String[] {"OK", "Cancel"};

    int option = JOptionPane.showOptionDialog(
      parentComponent,
      panel,
      "Change Password",
      JOptionPane.NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      null
    );

    // if user canceled change
    if(option != 0) {
      return;
    }

    String oldPassStr = String.valueOf(oldPassField.getPassword());
    String newPassStr = String.valueOf(newPassField.getPassword());
    String confirmNewPassStr = String.valueOf(confirmPassField.getPassword());

    if (
      (oldPassStr == null) || (oldPassStr.length() == 0)
      || (newPassStr == null) || (newPassStr.length() == 0)
      || (confirmNewPassStr== null) || (confirmNewPassStr.length() == 0)
    ) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "Required fields incomplete",
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    if ((!Constants.PASSWORD_VALIDATOR.matches(newPassStr))) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "New password does not meet requirements:"
        + "\n" + Constants.PASSWORD_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    if (!newPassStr.equals(confirmNewPassStr)) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "New password and confirm password does not match",
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    clientSocket.sendPayload(
      new ChangePassword(
        1,
        userId,
        token,
        oldPassStr,
        newPassStr
      )
    );
  }

  public static synchronized void promptChangeProfileDescription(
    Component parentComponent,
    ClientSocket clientSocket
  ) {
    String curDescription = GlobalClient.clientData.getDescription();
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String newDescription = JOptionPane.showInputDialog(
      parentComponent,
      "New Description: ",
      curDescription
    );

    if (
      (newDescription == null)
      || (newDescription.length() == 0)
      || (newDescription == curDescription)
    ) {
      return;
    }

    if ((!Constants.DESCRIPTION_VALIDATOR.matches(newDescription))) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "New description does not meet requirements:"
        + "\n" + Constants.DESCRIPTION_VALIDATOR.getDescription(),
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    clientSocket.sendPayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.DESCRIPTION,
        newDescription
      )
    );
  }

  public static synchronized void promptRespondFriendRequest(
    Component parentComponent,
    UserMetadata sender,
    String requestMessage,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String strToShow = "Accept friend request from " + sender.getUsername() + "?";
    if ((requestMessage != null) || (requestMessage.length() > 0)) {
      strToShow += "\n" + requestMessage;
    }

    String[] options = new String[] {"Accept", "Decline", "Cancel"};
    int choice = JOptionPane.showOptionDialog(
      parentComponent,
      strToShow,
      "Friend request response",
      JOptionPane.YES_NO_CANCEL_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      null
    );

    if (choice == JOptionPane.YES_OPTION) {
      clientSocket.sendPayload(
        new FriendRequestResponse(
          1,
          userId,
          token,
          sender.getUserId(),
          true
        )
      );
    } else if (choice == JOptionPane.NO_OPTION) {
      clientSocket.sendPayload(
        new FriendRequestResponse(
          1,
          userId,
          token,
          sender.getUserId(),
          false
        )
      );
    }
  }

  public static synchronized void promptCancelFriendRequest(
    Component parentComponent,
    UserMetadata recipient,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String[] options = new String[] {"Confirm", "Cancel"};
    int choice = JOptionPane.showOptionDialog(
      parentComponent,
      "Cancel friend request to " + recipient.getUsername() + "?",
      "Cancel Friend Request",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      null
    );

    if (choice == JOptionPane.YES_OPTION) {
    } else if (choice == JOptionPane.NO_OPTION) {
    }
  }
  
  public static synchronized void displayUserMetadata(
    Component parentComponent,
    UserMetadata metadata
  ) {
    JOptionPane.showMessageDialog(
      parentComponent,
      metadata,
      "Friend Profile",
      JOptionPane.PLAIN_MESSAGE
    );
  }

  public static synchronized void promptFriendAction(
    Component parentComponent,
    UserMetadata metadata,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();
    String[] choices = new String[] {
      "block",
      "remove friend"
    };

    int choice = JOptionPane.showOptionDialog(
      parentComponent,
      "Select an operation",
      metadata.getUsername(),
      JOptionPane.YES_NO_OPTION,
      JOptionPane.PLAIN_MESSAGE,
      null,
      choices,
      null
    );

    // block
    if (choice == JOptionPane.YES_OPTION) {
      if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
        clientSocket.sendPayload(
          new BlockUser(
            1,
            userId,
            token,
            metadata.getUsername()
          )
        );
      }
    // remove friend
    } else if (choice == JOptionPane.NO_OPTION) {
      if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
        //TODO: remove friend
      }
    }
  }

  public static synchronized void promptMessageAction(
    Component parentComponent,
    Message message,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();
    String[] choices;
    System.out.println(message.hasAttachment());
    if (message.getSenderId().equals(userId)) {
      if (message.hasAttachment()) {
        choices = new String[] {
          "edit message", "remove message", "download attachment"
        };
      } else {
        choices = new String[] {
          "edit message", "remove message"
        };
      }
    } else if (message.hasAttachment()) {
      choices = new String[] {
        "download attachment"
      };
    } else {
      return;
    }
    String title = message.getContent();
    if (title.length() >= 10) {
      title = title.substring(0, 7) + "...";
    }
    String choice = (String)(JOptionPane.showInputDialog(
      parentComponent,
      "Select an operation",
      title,
      JOptionPane.PLAIN_MESSAGE,
      null,
      choices,
      null
    ));
    if ((choice == null) || (choice.length() == 0)) {
      return;
    }

    if (choice.equals("edit message")) {
      JTextArea area = new JTextArea(5,20);
      area.setText(message.getContent());
      area.setEditable(true);
      int result = JOptionPane.showConfirmDialog(
        parentComponent,
        area,
        "Edit message",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
      );
      if (result != JOptionPane.OK_OPTION) {
        return;
      }
      String newContent = area.getText();
      if ((newContent != null) && (newContent.length() > 0)) {
        clientSocket.sendPayload(
          new EditMessage(
            1,
            userId,
            token,
            message.getChannelId(),
            message.getId(),
            newContent
          )
        );
      }
    } else if (choice.equals("remove message")) {
      if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
        clientSocket.sendPayload(
          new RemoveMessage(
            1,
            userId,
            token,
            message.getChannelId(),
            message.getId(),
            ""
          )
        );
      }

    } else if (choice.equals("download attachment")) {
      clientSocket.sendPayload(
        new RequestAttachment(
          1,
          userId,
          token,
          message.getAttachmentId()
        )
      );
    }
  }

  public static synchronized void promptGroupChannelAction(
    Component parentComponent,
    GroupChannelMetadata metadata,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    String[] options;
    // admin options
    if (metadata.getOwnerId().equals(userId)) {
      options = new String[] {
        "add participant",
        "remove participant",
        "blacklist participant",
        "leave channel",
        "transfer ownership"
      };
      String choice = (String)(JOptionPane.showInputDialog(
        parentComponent,
        "Select an operation",
        metadata.getChannelName(),
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        null
      ));
      if ((choice != null) && (choice.length() > 0)) {
        if (choice.equals("add participant")) {
          GlobalJDialogPrompter.promptAddParticipantToChannel(
            parentComponent,
            metadata,
            clientSocket
          );

        } else if (choice.equals("remove participant")) {
          String userIdToRemove = GlobalJDialogPrompter.promptSelectParticipantFromChannel(
            parentComponent,
            metadata,
            false,
            clientSocket
          );
          if ((userIdToRemove != null) && (userIdToRemove.length() > 0)) {
            clientSocket.sendPayload(
              new RemoveParticipant(
                1,
                userId,
                token,
                metadata.getChannelId(),
                userIdToRemove
              )
            );
          }

        } else if (choice.equals("blacklist participant")) {
          String userIdToBlacklist = GlobalJDialogPrompter.promptSelectParticipantFromChannel(
            parentComponent,
            metadata,
            false,
            clientSocket
          );
          if ((userIdToBlacklist != null) && (userIdToBlacklist.length() > 0)) {
            clientSocket.sendPayload(
              new BlacklistUser(
                1,
                userId,
                token,
                metadata.getChannelId(),
                userIdToBlacklist
              )
            );
          }

        } else if (choice.equals("leave channel")) {
          if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
            clientSocket.sendPayload(
              new LeaveChannel(
                1,
                userId,
                token,
                metadata.getChannelId()
              )
            );
          }

        } else if (choice.equals("transfer ownership")) {
          String userIdToTransfer = GlobalJDialogPrompter.promptSelectParticipantFromChannel(
            parentComponent,
            metadata,
            false,
            clientSocket
          );
          if ((userIdToTransfer != null) && (userIdToTransfer.length() > 0)) {
            clientSocket.sendPayload(
              new TransferOwnership(
                1,
                userId,
                token,
                metadata.getChannelId(),
                userIdToTransfer
              )
            );
          }
        }
      }

    // normal participant options
    } else {
      options = new String[] {
        "add participant",
        "leave channel"
      };
      String choice = (String)(JOptionPane.showInputDialog(
        parentComponent,
        "Select an operation",
        metadata.getChannelName(),
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        null
      ));
      if ((choice != null) && (choice.length() > 0)) {
        if (choice.equals("add participant")) {
          GlobalJDialogPrompter.promptAddParticipantToChannel(
            parentComponent,
            metadata,
            clientSocket
          );
        } else if (choice.equals("leave channel")) {
          if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
            clientSocket.sendPayload(
              new LeaveChannel(
                1,
                userId,
                token,
                metadata.getChannelId()
              )
            );
          }
        }
      }
    }
  }

  public static synchronized void promptAddParticipantToChannel(
    Component parentComponent,
    GroupChannelMetadata metadata,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    Token token = GlobalClient.clientData.getToken();

    LinkedHashSet<UserMetadata> friends = GlobalClient.clientData.getFriends();
    int numFriends = friends.size();
    String[] friendUsernames = new String[numFriends];
    String[] friendIds = new String[numFriends];
    Iterator<UserMetadata> iterator = friends.iterator(); 
    int count = 0;
    // check values
    while (iterator.hasNext()) {
      UserMetadata userMetadata = iterator.next();
      friendUsernames[count] = userMetadata.getUsername();
      friendIds[count] = userMetadata.getUserId();
      count++;
    }
    // ask to select a friend
    String choice = (String)(JOptionPane.showInputDialog(
      parentComponent,
      "Add a friend to this channel",
      metadata.getChannelName(),
      JOptionPane.PLAIN_MESSAGE,
      null,
      friendUsernames,
      null
    ));

    if ((choice != null) && (choice.length() > 0)) {
      String userIdToAdd = friendIds[Arrays.asList(friendUsernames).indexOf(choice)];
      clientSocket.sendPayload(
        new AddParticipant(
          1,
          userId,
          token,
          metadata.getChannelId(),
          userIdToAdd
        )
      );
    }
  }

  public static synchronized String promptSelectParticipantFromChannel(
    Component parentComponent,
    GroupChannelMetadata metadata,
    boolean canBeSelf,
    ClientSocket clientSocket
  ) {
    String userId = GlobalClient.clientData.getUserId();
    
    LinkedHashSet<UserMetadata> participants = metadata.getParticipants();
    int numChoices = participants.size();
    if (!canBeSelf) {
      numChoices -= 1;
    }
    String[] participantsUsernames = new String[numChoices];
    String[] participantsIds = new String[numChoices];
    Iterator<UserMetadata> iterator = participants.iterator(); 
    int count = 0;
    while (iterator.hasNext()) {
      UserMetadata userMetadata = iterator.next();
      String curUsername = userMetadata.getUsername();
      String curUserId = userMetadata.getUserId();
      if ((canBeSelf) || (!curUserId.equals(userId))) {
        participantsUsernames[count] = curUsername;
        participantsIds[count] = curUserId;
        count++;
      }
    }
    // ask to select a friend
    String choice = (String)(JOptionPane.showInputDialog(
      parentComponent,
      "Select a participant from this channel",
      metadata.getChannelName(),
      JOptionPane.PLAIN_MESSAGE,
      null,
      participantsUsernames,
      null
    ));

    return choice;
  }

  public static synchronized boolean confirmAction(Component parentComponent) {
    int n = JOptionPane.showConfirmDialog(
      parentComponent,
      "Are you sure you want to perform this action?",
      "Confirm Action",
      JOptionPane.YES_NO_OPTION
    );
    if (n == JOptionPane.YES_OPTION) {
      return true;
    }
    return false;
  }

}
