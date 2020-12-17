package client.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import client.entities.ClientSocket;

/**
 * A super class for all the client side GUI frames that
 * asks for confirmation of the user
 * and disconnects the client socket upon closing.
 * <p>
 * Created on 2020.12.09.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public abstract class DisconnectOnCloseFrame extends UserFrame implements WindowListener {

  public DisconnectOnCloseFrame(ClientSocket clientSocket) {
    super(clientSocket);
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(this);
  }

  @Override
  public void windowOpened(WindowEvent e) {
  }

  @Override
  public void windowClosing(WindowEvent e) {
    String[] options = {
      "Yes, disconnect",
      "Cancel"
    };
    int choice = JOptionPane.showOptionDialog(
      this,
      "Would you like to close all windows and disconnect?",
      "Confirm Action",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      ClientGUIFactory.getDialogConfirmationIcon(30, 30),
      options,
      null
    );
    
    if (choice == JOptionPane.OK_OPTION) {
      this.getClientSocket().terminate();
      this.dispose();
      System.exit(0); // manual EXIT_ON_CLOSE again lol
    }
  }

  @Override
  public void windowIconified(WindowEvent e) {
  }
            
  @Override
  public void windowDeiconified(WindowEvent e) {
  }
  
  @Override
  public void windowDeactivated(WindowEvent e) {
  }
  
  @Override
  public void windowActivated(WindowEvent e) {
  }
  
  @Override
  public void windowClosed(WindowEvent e) {
  }

}
