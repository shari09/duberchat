package common.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import common.entities.Constants;

/**
 * 
 * <p>
 * Created on 2020.12.14.
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class Theme {
  
  public static BufferedImage getIcon() {
    BufferedImage icon = null;
    try {
      icon = ImageIO.read(new File(Constants.ICON_PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return icon;
  }

  public static Color getAlphaColor(Color color, float alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  public static Color getColour(String hex) {
    return Color.decode("#"+hex);
  }

  public static Color getColour(String hex, int alpha) {
    Color color = Color.decode("#"+hex);
    return new Color(
      color.getRed(),
      color.getGreen(),
      color.getBlue(),
      (int)((100-alpha)/100.0*255)
    );
  }

  public static Font getPlainFont(int size) {
    return new Font("Segoe UI", Font.PLAIN, size);
  }

  public static Font getBoldFont(int size) {
    return new Font("Segoe UI", Font.BOLD, size);
  }

  public static Font getItalicFont(int size) {
    return new Font("Segoe UI", Font.ITALIC, size);
  }
}
