package server.gui;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ComponentsFactory {
  public static JPanel getHeader(String title, Color bg) {
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Style.getFont(15));
    titleLabel.setForeground(Style.LIGHT_TEXT);
    JPanel titlePanel = new JPanel();
    titlePanel.add(titleLabel);
    titlePanel.setBackground(bg);
    titlePanel.setPreferredSize(titlePanel.getPreferredSize());
    return titlePanel;
  }

  public static GridBagConstraints getScrollConstraints() {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    return c;
  }

  // public static JScrollPane createScrollingButtons(
  //   JPanel panel, 
  //   GridBagConstraints c
  // ) {

  //   panel.setLayout(new GridBagLayout());
  //   panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  //   panel.setBackground(Color.GRAY);


  //   c.fill = GridBagConstraints.HORIZONTAL;
  //   c.anchor = GridBagConstraints.NORTH;
  //   c.weightx = 1;
  //   c.weighty = 1;
  //   c.gridx = 0;

  //   panel.add(Box.createVerticalGlue(), c);

  //   c.weighty = 0;

  //   JScrollPane scrollPane = new JScrollPane(panel);
  //   scrollPane.setBorder(BorderFactory.createEmptyBorder());
  //   scrollPane.setVerticalScrollBarPolicy(
  //     ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
  //   );
  //   scrollPane.setHorizontalScrollBarPolicy(
  //     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
  //   );

  //   // if (!visibleScrollBar) {
  //   //   scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
  //   // }
  //   scrollPane.getVerticalScrollBar().setUnitIncrement(16);
  //   return scrollPane;
  // }


  public static JScrollPane getScrollPane(JPanel panel, boolean visibleScrollBar) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );
    scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    if (!visibleScrollBar) {
      scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    }
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

}
