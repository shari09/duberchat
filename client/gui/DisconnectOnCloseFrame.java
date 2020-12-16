package client.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import client.entities.ClientSocket;

/**
 * [description]
 * <p>
 * Created on 2020.12.09.
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
      null,
      options,
      null);
    
    if (choice == JOptionPane.OK_OPTION) {
      try {
        this.getClientSocket().close();
        System.exit(0);
      } catch (IOException ioException) {
        JOptionPane.showMessageDialog(
          this,
          "failed to disconnect",
          "Error",
          JOptionPane.ERROR_MESSAGE
        );
      }
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
