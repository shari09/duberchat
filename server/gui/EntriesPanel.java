package server.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import server.entities.EventType;
import server.services.GlobalServerServices;


/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class EntriesPanel extends JPanel implements ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final int WIDTH = 225;
  private JPanel entriesPanel;
  private JScrollPane scrollPane;
  private ConcurrentHashMap<JButton, JPanel> entries;

  public EntriesPanel(String title) {
    super();
    this.entries = new ConcurrentHashMap<>();
    this.setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Style.getFont(15));
    titleLabel.setForeground(Style.LIGHT_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(Style.GRAY4);
    // titlePanel.setPreferredSize(titleLabel.getPreferredSize());
    this.add(titlePanel, BorderLayout.PAGE_START);

    this.entriesPanel = new JPanel();
    this.entriesPanel.setLayout(new BoxLayout(
      this.entriesPanel, 
      BoxLayout.PAGE_AXIS
    ));
    this.entriesPanel.setPreferredSize(new Dimension(
      EntriesPanel.WIDTH, 
      this.entriesPanel.getPreferredSize().height
    ));
    this.entriesPanel.setBackground(Style.GRAY1);
    this.entriesPanel.setBorder(BorderFactory.createEmptyBorder());
    this.entriesPanel.setAlignmentX(CENTER_ALIGNMENT);

    this.scrollPane = new JScrollPane(this.entriesPanel);
    this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
    // scroller.setPreferredSize(preferredSize);
    this.add(this.scrollPane);

  }

  public void addEntry(JButton tab, JPanel content) {
    tab.addActionListener(this);
    tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
    tab.setForeground(Style.LIGHT_TEXT_OVERLAY);
    tab.setBackground(Style.GRAY1);
    tab.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
    tab.setSize(new Dimension(
      EntriesPanel.WIDTH,
      tab.getPreferredSize().height
    ));
    tab.setMaximumSize(tab.getSize());
    tab.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        tab.setBackground(Style.OVERLAY);
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        tab.setBackground(Style.GRAY1);
      }
    });
    tab.setFocusPainted(false);

    this.entriesPanel.add(tab);
    this.entriesPanel.revalidate();
    this.revalidate();
    this.setVisible(true);
    this.entries.put(tab, content);
  }

  public void removeEntry(JButton tab) {
    this.entriesPanel.remove(tab);
    this.entriesPanel.revalidate();
    this.revalidate();
    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton button = (JButton)e.getSource();
    // button.setBackground(bg);

    //TODO: duct tape solution over here regarding my event emitter
    GlobalServerServices.guiEventQueue.emitEvent(
      EventType.SELECT_USER, 
      1, 
      this.entries.get(button)
    );

  }
  
}
