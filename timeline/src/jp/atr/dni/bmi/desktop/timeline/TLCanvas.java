/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.timeline;

import java.awt.Dimension;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <a href="http://www.atr.jp">ATR - [株式会社・国際電氣通信基礎技術研究所]</a>
 *
 * @version 2011/07/14
 */
public class TLCanvas extends GLCanvas {

   public TLCanvas(GLCapabilities cap) {
      super(cap);
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(0, 0);
   }

//   @Override
//   public Dimension getMaximumSize() {
//      return new Dimension(0, 0);
//   }

   @Override
   public Dimension getMinimumSize() {
      return new Dimension(0, 0);
   }
   
   
}
