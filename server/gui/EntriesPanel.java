package server.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import server.entities.EventType;
import server.services.GlobalServices;
import server.services.Subscribable;


/**
 * 
 * <p>
 * Created on 2020.12.12.
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class EntriesPanel extends JPanel implements ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private JPanel entriesPanel;
  private JScrollPane scrollPane;
  private ConcurrentHashMap<JButton, JPanel> entries;
  private JButton defaultEntry;

  private GridBagConstraints c;

  public EntriesPanel(String title) {
    super();
    this.entries = new ConcurrentHashMap<>();
    this.defaultEntry = new JButton();
    this.setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Style.getFont(15));
    titleLabel.setForeground(Style.LIGHT_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(Style.GRAY4);
    titlePanel.setPreferredSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    titlePanel.setMinimumSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    this.add(titlePanel, BorderLayout.PAGE_START);

    this.entriesPanel = new JPanel();
    this.entriesPanel.setLayout(new GridBagLayout());
    this.entriesPanel.setBackground(Style.GRAY1);
    this.entriesPanel.setBorder(BorderFactory.createEmptyBorder());

    this.c = new GridBagConstraints();
    this.c.fill = GridBagConstraints.HORIZONTAL;
    this.c.anchor = GridBagConstraints.NORTH;
    this.c.weightx = 1;
    this.c.weighty = 100;
    this.c.gridx = 0;
    this.entriesPanel.add(Box.createVerticalGlue(), this.c);

    this.c.weighty = 0;



    this.scrollPane = new JScrollPane(this.entriesPanel);
    this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
    this.scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );
    this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    //TODO: get better scrollbar
    // this.scrollPane.getVerticalScrollBar().setUI(new TinyScrollUI());
    this.add(this.scrollPane);
    this.setVisible(false);
  }

  public void addEntry(JButton tab, JPanel content) {
    tab.addActionListener(this);
    tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
    tab.setForeground(Style.LIGHT_TEXT_OVERLAY);
    tab.setBackground(Style.GRAY1);
    tab.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
    tab.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        tab.setBackground(Style.OVERLAY);
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        tab.setBackground(Style.GRAY1);
      }
    });
    tab.setFocusPainted(false);

    this.entriesPanel.add(tab, c, 0);
    this.defaultEntry = tab;
    
    this.entriesPanel.revalidate();
    this.scrollPane.revalidate();
    this.entries.put(tab, content);
    this.requestFocus();
    this.repaint();
  }

  public void removeEntry(JButton tab) {
    this.entriesPanel.remove(tab);
    this.entriesPanel.revalidate();
    this.requestFocus();
    this.repaint();
  }

  public JPanel getContent(JButton button) {
    return this.entries.get(button);
  }

  public JPanel getDefaultContent() {
    if (this.defaultEntry == null) {
      return null;
    }
    return this.entries.get(this.defaultEntry);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton button = (JButton)e.getSource();
    // button.setBackground(bg);

    GlobalServices.guiEventQueue.emitEvent(
      EventType.ENTRY_SELECTED, 
      2, 
      button
    );

  }

  
}
