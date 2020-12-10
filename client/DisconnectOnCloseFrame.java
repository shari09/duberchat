package client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

/**
 * [description]
 * <p>
 * Created on 2020.12.09.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public abstract class DisconnectOnCloseFrame extends JFrame implements WindowListener {
  private ClientSocket clientSocket;

  public DisconnectOnCloseFrame(String title, ClientSocket clientSocket) {
    super(title);
    this.clientSocket = clientSocket;
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(this);
  }

  public ClientSocket getClientSocket() {
    return this.clientSocket;
  }


    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
      //TODO: dialog for confirm exiting
      try {
        this.clientSocket.close();
        System.exit(0);
      } catch (IOException ioException) {
        System.out.println("failed to disconnect");
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
