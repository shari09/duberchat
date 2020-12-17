package client.resources;

import java.awt.Component;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import client.gui.ClientGUIFactory;
import common.entities.ChannelField;
import common.entities.Constants;
import common.entities.GroupChannelMetadata;
import common.entities.Message;
import common.entities.ProfileField;
import common.entities.Token;
import common.entities.UserMetadata;
import common.entities.payload.client_to_server.AddParticipant;
import common.entities.payload.client_to_server.BlacklistUser;
import common.entities.payload.client_to_server.BlockUser;
import common.entities.payload.client_to_server.CancelFriendRequest;
import common.entities.payload.client_to_server.ChangeChannel;
import common.entities.payload.client_to_server.ChangePassword;
import common.entities.payload.client_to_server.ChangeProfile;
import common.entities.payload.client_to_server.EditMessage;
import common.entities.payload.client_to_server.FriendRequestResponse;
import common.entities.payload.client_to_server.LeaveChannel;
import common.entities.payload.client_to_server.RemoveFriend;
import common.entities.payload.client_to_server.RemoveMessage;
import common.entities.payload.client_to_server.RemoveParticipant;
import common.entities.payload.client_to_server.RequestAttachment;
import common.entities.payload.client_to_server.TransferOwnership;
import common.gui.Theme;
import common.services.RegexValidator;

/**
 * Contains static methods to prompt for and respond to user inputs/choices.
 * <p>
 * Created on 2020.12.13.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class GlobalJDialogPrompter {

  public static void promptChangeUsername(Component parentComponent) {
    String curUsername;
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      curUsername = GlobalClient.clientData.getUsername();
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }
    String newUsername = (String)(JOptionPane.showInputDialog(
      parentComponent,
      "New username: ",
      "Change Username",
      JOptionPane.QUESTION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30),
      null,
      null
    ));

    if (
      (newUsername == null)
      || (newUsername.length() == 0)
      || (newUsername == curUsername)
    ) {
      return;
    }

    if ((!Constants.NAME_VALIDATOR.matches(newUsername))) {
      GlobalJDialogPrompter.warnInvalidInput(
        parentComponent,
        "New username",
        Constants.NAME_VALIDATOR
      );
      return;
    }

    GlobalPayloadQueue.enqueuePayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.USERNAME,
        newUsername
      )
    );
  }

  public static void promptChangePassword(Component parentComponent) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

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
      ClientGUIFactory.getDialogInformationIcon(30, 30),
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
        JOptionPane.INFORMATION_MESSAGE,
        ClientGUIFactory.getDialogInformationIcon(30, 30)
      );
      return;
    }

    if ((!Constants.PASSWORD_VALIDATOR.matches(newPassStr))) {
      GlobalJDialogPrompter.warnInvalidInput(
        parentComponent,
        "New password",
        Constants.PASSWORD_VALIDATOR
      );
      return;
    }

    if (!newPassStr.equals(confirmNewPassStr)) {
      JOptionPane.showMessageDialog(
        parentComponent,
        "New password and confirm password does not match",
        "Submission failed",
        JOptionPane.INFORMATION_MESSAGE,
        ClientGUIFactory.getDialogInformationIcon(30, 30)
      );
      return;
    }

    GlobalPayloadQueue.enqueuePayload(
      new ChangePassword(
        1,
        userId,
        token,
        oldPassStr,
        newPassStr
      )
    );
  }

  public static void promptChangeProfileDescription(Component parentComponent) {
    String curDescription;
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      curDescription = GlobalClient.clientData.getDescription();
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

    String newDescription = (String)(JOptionPane.showInputDialog(
      parentComponent,
      "New description: ",
      "Change Description",
      JOptionPane.QUESTION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30),
      null,
      null
    ));

    if (
      (newDescription == null)
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
        JOptionPane.INFORMATION_MESSAGE,
        ClientGUIFactory.getDialogInformationIcon(30, 30)
      );
      return;
    }

    GlobalPayloadQueue.enqueuePayload(
      new ChangeProfile(
        1,
        userId,
        token,
        ProfileField.DESCRIPTION,
        newDescription
      )
    );
  }

  public static void promptRespondFriendRequest(
    Component parentComponent,
    UserMetadata sender,
    String requestMessage
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

    String strToShow = "Accept friend request from " + sender.getUsername() + "?";
    if ((requestMessage != null) && (requestMessage.length() > 0)) {
      strToShow += "\n" + requestMessage;
    }

    String[] options = new String[] {"Accept", "Decline", "Cancel"};
    int choice = JOptionPane.showOptionDialog(
      parentComponent,
      strToShow,
      "Friend request response",
      JOptionPane.YES_NO_CANCEL_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30),
      options,
      null
    );

    if (choice == JOptionPane.YES_OPTION) {
      GlobalPayloadQueue.enqueuePayload(
        new FriendRequestResponse(
          1,
          userId,
          token,
          sender.getUserId(),
          true
        )
      );
    } else if (choice == JOptionPane.NO_OPTION) {
      GlobalPayloadQueue.enqueuePayload(
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

  public static void promptCancelFriendRequest(
    Component parentComponent,
    UserMetadata recipient
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

    String[] options = new String[] {"Confirm", "Cancel"};
    int choice = JOptionPane.showOptionDialog(
      parentComponent,
      "Cancel friend request to " + recipient.getUsername() + "?",
      "Cancel Friend Request",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30),
      options,
      null
    );

    if (choice == JOptionPane.YES_OPTION) {
      GlobalPayloadQueue.enqueuePayload(
        new CancelFriendRequest(
          1,
          userId,
          token,
          recipient.getUserId()
        )
      );
    }
  }
  
  public static void displayUserMetadata(
    Component parentComponent,
    UserMetadata metadata
  ) {
    JPanel userProfile = ClientGUIFactory.getUserProfilePanel(
      metadata,
      Theme.getBoldFont(20),
      Theme.getItalicFont(15),
      Theme.getPlainFont(15),
      ClientGUIFactory.BLUE_SHADE_4,
      ClientGUIFactory.GRAY_SHADE_4
    );
    JOptionPane.showMessageDialog(
      parentComponent,
      userProfile,
      "Friend Profile",
      JOptionPane.PLAIN_MESSAGE
    );
  }

  public static void promptFriendAction(
    Component parentComponent,
    UserMetadata metadata
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }
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
      if (GlobalJDialogPrompter.confirmAction(
        parentComponent,
        "Are you sure you want to block this user (permanently)?"
        )
      ) {
        GlobalPayloadQueue.enqueuePayload(
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
        GlobalPayloadQueue.enqueuePayload(
          new RemoveFriend(
            1,
            userId,
            token,
            metadata.getUserId()
          )
        );
      }
    }
  }

  public static void promptMessageAction(
    Component parentComponent,
    Message message
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }
    String[] choices;
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
        GlobalPayloadQueue.enqueuePayload(
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
        GlobalPayloadQueue.enqueuePayload(
          new RemoveMessage(
            1,
            userId,
            token,
            message.getChannelId(),
            message.getId()
          )
        );
      }

    } else if (choice.equals("download attachment")) {
      GlobalPayloadQueue.enqueuePayload(
        new RequestAttachment(
          1,
          userId,
          token,
          message.getAttachmentId()
        )
      );
    }
  }

  public static void promptGroupChannelAction(
    Component parentComponent,
    GroupChannelMetadata metadata
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

    String[] options;
    // admin options
    if (metadata.getOwnerId().equals(userId)) {
      options = new String[] {
        "add participant",
        "remove participant",
        "blacklist participant",
        "rename channel",
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
            metadata
          );

        } else if (choice.equals("remove participant")) {
          String userIdToRemove = GlobalJDialogPrompter.promptSelectParticipantFromChannel(
            parentComponent,
            metadata,
            false
          );
          if ((userIdToRemove != null) && (userIdToRemove.length() > 0)) {
            GlobalPayloadQueue.enqueuePayload(
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
            false
          );
          if (
            (userIdToBlacklist != null)
            && (userIdToBlacklist.length() > 0)
            && (
              GlobalJDialogPrompter.confirmAction(
              parentComponent,
              "Are you sure you want to blacklist this user for this channel (permanently)?"
              )
            )
          ) {
            GlobalPayloadQueue.enqueuePayload(
              new BlacklistUser(
                1,
                userId,
                token,
                metadata.getChannelId(),
                userIdToBlacklist
              )
            );
          }
        
        } else if (choice.equals("remove participant")) {
          String userIdToRemove = GlobalJDialogPrompter.promptSelectParticipantFromChannel(
            parentComponent,
            metadata,
            false
          );
          if ( (userIdToRemove != null) && (GlobalJDialogPrompter.confirmAction(parentComponent))) {
            GlobalPayloadQueue.enqueuePayload(
              new RemoveParticipant(
                1,
                userId,
                token,
                metadata.getChannelId(),
                userIdToRemove
              )
            );
          }
        
        } else if (choice.equals("rename channel")) {
          String newName = (String)(JOptionPane.showInputDialog(
            parentComponent,
            "New name: ",
            "Change Channel Name",
            JOptionPane.QUESTION_MESSAGE,
            ClientGUIFactory.getDialogInformationIcon(30, 30),
            null,
            metadata.getChannelName()
          ));
          if ((newName != null) && (!newName.equals(metadata.getChannelName()))) {
            System.out.println("uhhh..");
            if (Constants.NAME_VALIDATOR.matches(newName)) {
              System.out.println("renaming..");
              GlobalPayloadQueue.enqueuePayload(
                new ChangeChannel(
                  1,
                  userId,
                  token,
                  metadata.getChannelId(),
                  ChannelField.NAME,
                  newName
                )
              );
            } else {
              GlobalJDialogPrompter.warnInvalidInput(
                parentComponent,
                "channel name",
                Constants.NAME_VALIDATOR
              );
            }
          }
        } else if (choice.equals("leave channel")) {
          if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
            GlobalPayloadQueue.enqueuePayload(
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
            false
          );
          if ((userIdToTransfer != null) && (userIdToTransfer.length() > 0)) {
            GlobalPayloadQueue.enqueuePayload(
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
            metadata
          );
        } else if (choice.equals("leave channel")) {
          if (GlobalJDialogPrompter.confirmAction(parentComponent)) {
            GlobalPayloadQueue.enqueuePayload(
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

  public static void warnInvalidInput(Component parentComponent, String field, RegexValidator validator) {
    String strToShow = field + " does not meet requirement:" + "\n" + validator.getDescription();
    JOptionPane.showMessageDialog(
      parentComponent,
      strToShow,
      "Submission failed",
      JOptionPane.INFORMATION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30)
    );
    return;
  }

  public static void warnInvalidInput(Component parentComponent, String field, RegexValidator[] validators) {
    String strToShow = field + " does not meet requirements:";
    for (int i = 0; i < validators.length; i++) {
      strToShow += "\n" + validators[i].getDescription();
    }
    JOptionPane.showMessageDialog(
      parentComponent,
      strToShow,
      "Submission failed",
      JOptionPane.INFORMATION_MESSAGE,
      ClientGUIFactory.getDialogInformationIcon(30, 30)
    );
    return;
  }

  public static void promptAddParticipantToChannel(
    Component parentComponent,
    GroupChannelMetadata metadata
  ) {
    String userId;
    Token token;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
      token = GlobalClient.clientData.getToken();
    }

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
      GlobalPayloadQueue.enqueuePayload(
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

  public static String promptSelectParticipantFromChannel(
    Component parentComponent,
    GroupChannelMetadata metadata,
    boolean canBeSelf
  ) {
    String userId;
    synchronized (GlobalClient.clientData) {
      userId = GlobalClient.clientData.getUserId();
    }
    
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
    
    int index = Arrays.asList(participantsUsernames).indexOf(choice);
    if (index == -1) {
      return null;
    }
    return participantsIds[index];
  }

  public static boolean confirmAction(Component parentComponent, String customMessage) {
    int n = JOptionPane.showConfirmDialog(
      parentComponent,
      customMessage,
      "Confirm Action",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.PLAIN_MESSAGE,
      ClientGUIFactory.getDialogConfirmationIcon(30, 30)
    );
    if (n == JOptionPane.YES_OPTION) {
      return true;
    }
    return false;
  }

  public static boolean confirmAction(Component parentComponent) {
    int n = JOptionPane.showConfirmDialog(
      parentComponent,
      "Are you sure you want to perform this action?",
      "Confirm Action",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.PLAIN_MESSAGE,
      ClientGUIFactory.getDialogConfirmationIcon(30, 30)
    );
    if (n == JOptionPane.YES_OPTION) {
      return true;
    }
    return false;
  }

}