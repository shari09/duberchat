package client;

import javax.swing.JFrame;

import client.gui.ClientGUIFactory;
import client.gui.ClientStartFrame;

/**
 * Launches the client side of the chat program.
 * <p>
 * Created on 2020.12.04.
 * 
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClientLauncher {
  public static void main(String[] args) {
    ClientGUIFactory.initializeLookAndFeel();
    JFrame window = new ClientStartFrame();
  }
}