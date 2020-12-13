package server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

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

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Style.getFont(15));
    titleLabel.setForeground(Style.LIGHT_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(Style.GRAY2);
    titlePanel.setPreferredSize(titlePanel.getPreferredSize());
    this.add(titlePanel, BorderLayout.PAGE_START);

    this.logPane = new JPanel();
    this.logPane.setLayout(new GridBagLayout());
    this.logPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    this.logPane.setAlignmentX(LEFT_ALIGNMENT);
    this.logPane.setBackground(Color.WHITE);

    this.c = new GridBagConstraints();
    this.c.fill = GridBagConstraints.HORIZONTAL;
    this.c.anchor = GridBagConstraints.NORTH;
    this.c.weightx = 1;
    this.c.weighty = 1;
    this.c.gridx = 0;
    this.logPane.add(Box.createVerticalGlue(), this.c);

    this.c.weighty = 0;

    this.scrollPane = new JScrollPane(this.logPane);
    this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
    this.scrollPane.setBackground(Color.WHITE);
    this.scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );
    this.scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    this.add(this.scrollPane);
  }

  public synchronized void addLog(Log log) {
    JLabel timeLabel = new JLabel(log.getCreated().toString());
    timeLabel.setFont(Style.getFont(10));
    timeLabel.setForeground(Style.DIM_TEXT);
    timeLabel.setBackground(Color.WHITE);
    JTextArea msgLabel = new JTextArea(log.getMsg());
    msgLabel.setFont(Style.getFont(15));
    msgLabel.setBackground(Color.WHITE);
    msgLabel.setForeground(Style.TEXT);
    msgLabel.setLineWrap(true);
    msgLabel.setEditable(false);

    this.logPane.add(Style.getEmptyHeight(10), this.c);
    this.logPane.add(timeLabel, this.c);
    this.logPane.add(msgLabel, this.c);

    
    this.logPane.requestFocus();
    this.requestFocus();
    JScrollBar bar = this.scrollPane.getVerticalScrollBar();
    bar.setValue(bar.getMaximum());
    this.repaint();

  }


}
