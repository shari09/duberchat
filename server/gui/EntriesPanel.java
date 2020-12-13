package server.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import server.entities.EventType;
import server.services.GlobalServices;


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
  private boolean fixedDefaultEntry;

  private GridBagConstraints c;

  public EntriesPanel(String title) {
    super();
    this.entries = new ConcurrentHashMap<>();
    this.defaultEntry = new JButton();
    this.setLayout(new BorderLayout());

    JPanel titlePanel = Components.getHeader(title, Components.GRAY4);
    titlePanel.setPreferredSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    titlePanel.setMinimumSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    this.add(titlePanel, BorderLayout.PAGE_START);

    this.entriesPanel = new JPanel();
    this.entriesPanel.setLayout(new GridBagLayout());
    this.entriesPanel.setBackground(Components.GRAY1);
    this.entriesPanel.setBorder(BorderFactory.createEmptyBorder());

    this.c = Components.getScrollConstraints();
    this.entriesPanel.add(Box.createVerticalGlue(), this.c, 0);

    this.c.weighty = 0;

    this.scrollPane = Components.getScrollPane(this.entriesPanel, false);

    this.fixedDefaultEntry = false;
    this.add(this.scrollPane);
    this.setVisible(false);
  }

  public JButton addEntry(String text, JPanel content) {
    JButton tab = Components.getButton(
      text, Components.LIGHT_TEXT_OVERLAY, 13, 
      4, 10, 
      Components.GRAY1, 
      Components.OVERLAY
    );
    tab.addActionListener(this);

    this.entriesPanel.add(tab, this.c, this.entries.size());
    if (!this.fixedDefaultEntry) {
      this.defaultEntry = tab;
    }
    
    this.entriesPanel.revalidate();
    this.scrollPane.revalidate();
    this.entries.put(tab, content);
    this.repaint();
    return tab;
  }

  public JPanel getEntriesPanel() {
    return this.entriesPanel;
  }


  public void setFixedDefaultEntry(JButton tab) {
    this.defaultEntry = tab;
    this.fixedDefaultEntry = true;
  }

  public void removeEntry(JButton tab) {
    this.entriesPanel.remove(tab);
    this.entriesPanel.revalidate();
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
