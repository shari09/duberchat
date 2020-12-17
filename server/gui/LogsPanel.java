package server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import common.gui.Theme;
import server.entities.Log;

/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogsPanel extends JPanel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private JScrollPane scrollPane;
  private JPanel logPane;

  private GridBagConstraints c;

  public LogsPanel(String title) {
    super();
    this.setLayout(new BorderLayout());

    this.add(
      ServerGUIFactory.getHeader(title), 
      BorderLayout.PAGE_START
    );

    this.logPane = new JPanel();
    this.logPane.setLayout(new GridBagLayout());
    this.logPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
    this.logPane.setAlignmentX(LEFT_ALIGNMENT);
    this.logPane.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    this.logPane.setForeground(ServerGUIFactory.GENERAL_TEXT);

    this.c = ServerGUIFactory.getScrollConstraints();
    this.logPane.add(Box.createVerticalGlue(), this.c);

    this.c.weighty = 0;

    this.scrollPane = new JScrollPane(this.logPane);
    this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
    this.scrollPane.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    this.scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
    );
    this.scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    this.scrollPane.getVerticalScrollBar().setBackground(ServerGUIFactory.HEADING);
    this.scrollPane.getVerticalScrollBar().setUI(ServerGUIFactory.getScrollbarUI());
    this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    this.add(this.scrollPane);
  }

  public synchronized void addLog(Log log) {
    Color textColor = null;
    switch (log.getType()) {
      case SUCCESS:
        textColor = ServerGUIFactory.LOG_SUCCESS;
        break;
      case CLIENT_ERROR:
        textColor = ServerGUIFactory.LOG_CLIENT_ERROR;
        break;
      case SERVER_ERROR:
        textColor = ServerGUIFactory.LOG_SYSTEM_ERROR;
        break;
      default:
        textColor = ServerGUIFactory.LOG_INFO;
        break;
    }
    JLabel timeLabel = new JLabel(log.getCreated().toString());
    timeLabel.setFont(Theme.getPlainFont(12));
    timeLabel.setForeground(ServerGUIFactory.GENERAL_TEXT);
    timeLabel.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    JTextArea msgLabel = new JTextArea(String.format("[%s] %s", log.getType(), log.getMsg()));
    msgLabel.setFont(Theme.getPlainFont(15));
    msgLabel.setBackground(ServerGUIFactory.GENERAL_TEXT_BG);
    msgLabel.setForeground(textColor);
    msgLabel.setLineWrap(true);
    msgLabel.setWrapStyleWord(true);
    msgLabel.setEditable(false);

    this.logPane.add(ServerGUIFactory.getEmptyHeight(10), this.c);
    this.logPane.add(timeLabel, this.c);
    this.logPane.add(msgLabel, this.c);

    
    this.logPane.requestFocus();
    this.requestFocus();
    JScrollBar bar = this.scrollPane.getVerticalScrollBar();
    bar.setValue(bar.getMaximum());
    this.repaint();

  }


}
