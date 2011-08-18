/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.atr.dni.brainliner.desktop.timeline;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <i>ATR - [株式会社・國際電氣通信基礎技術研究所]</i>
 *
 * @version 2011/02/15
 */
public class TimelineObject implements Shape {

   @Override
   public Rectangle getBounds() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Rectangle2D getBounds2D() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean contains(double d, double d1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean contains(Point2D pd) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean intersects(double d, double d1, double d2, double d3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean intersects(Rectangle2D rd) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean contains(double d, double d1, double d2, double d3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean contains(Rectangle2D rd) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PathIterator getPathIterator(AffineTransform at) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PathIterator getPathIterator(AffineTransform at, double d) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}