package server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
  private Button defaultEntry;
  private boolean fixedDefaultEntry;
  private Button focused;

  private GridBagConstraints c;

  public EntriesPanel(String title) {
    super();
    this.entries = new ConcurrentHashMap<>();
    this.defaultEntry = new Button();
    this.focused = this.defaultEntry;
    this.setLayout(new BorderLayout());

    JPanel titlePanel = ServerGUIFactory.getHeader(title);
    titlePanel.setPreferredSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    titlePanel.setMinimumSize(new Dimension(
      225, titlePanel.getPreferredSize().height
    ));
    this.add(titlePanel, BorderLayout.PAGE_START);

    this.entriesPanel = new JPanel();
    this.entriesPanel.setLayout(new GridBagLayout());
    this.entriesPanel.setBackground(ServerGUIFactory.ENTRIES);
    this.entriesPanel.setBorder(BorderFactory.createEmptyBorder());

    this.c = ServerGUIFactory.getScrollConstraints();
    this.entriesPanel.add(Box.createVerticalGlue(), this.c, 0);

    this.c.weighty = 0;

    this.scrollPane = ServerGUIFactory.getScrollPane(this.entriesPanel);

    this.fixedDefaultEntry = false;
    this.add(this.scrollPane);
    this.setVisible(false);
  }

  public Button addEntry(String text, JPanel content) {
    Button tab = ServerGUIFactory.getButton(
      text, 
      ServerGUIFactory.ENTRY_TEXT, 
      16, 
      4, 10, 
      ServerGUIFactory.ENTRIES, 
      ServerGUIFactory.ENTRY_ACTIVE
    );
    tab.addActionListener(this);

    this.entriesPanel.add(tab, this.c, this.entries.size());
    if (!this.fixedDefaultEntry) {
      this.defaultEntry = tab;
      this.setFocused(tab);
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

  private void setFocused(Button tab) {
    this.focused.setFocused(false);
    this.focused = tab;
    this.focused.setFocused(true);
    this.repaint();
  }


  public void setFixedDefaultEntry(Button tab) {
    this.defaultEntry = tab;
    this.fixedDefaultEntry = true;
    this.setFocused(tab);
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
    Button button = (Button)e.getSource();
    this.setFocused(button);

    GlobalServices.guiEventQueue.emitEvent(
      EventType.ENTRY_SELECTED, 
      2, 
      button
    );

  }

  
}
