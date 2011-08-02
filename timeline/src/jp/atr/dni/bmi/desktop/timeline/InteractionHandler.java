package jp.atr.dni.bmi.desktop.timeline;

import static jp.atr.dni.bmi.desktop.timeline.TimelineTopComponent.SCROLLBAR_HEIGHT;
import static jp.atr.dni.bmi.desktop.timeline.TimelineTopComponent.Y_SPACER;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import jp.atr.dni.bmi.desktop.timeline.model.ViewerChannel;

/**
 * 
 * @author makoto
 */
public class InteractionHandler implements KeyListener, MouseListener,
        MouseMotionListener, MouseWheelListener {

   /** a mapping of interactor names to interactors */
//	LinkedHashMap<String, Mode> modes = new LinkedHashMap<String, Interactor>();
   // interactor names
   public static String DRAG_MODE = "Drag Mode";
   public static String VIEW_MODE = "View Mode";
   /** the current interaction */
//	private Mode mode;
   /** the canvas */
   private TimelineTopComponent canvas;
   /** the previously point clicked by the mouse, in screen coordinates */
   private Point screenPickedPoint;
   /** the previously point clicked by the mouse, in virtual coordinates */
   private Point2D pickedPoint;
   /** the current mouse location, in screen coordinates */
   private Point screenCurrentPoint;
   /** the current mouse location, in virtual coordinates */
   private Point2D currentPoint;
   /** the previous mouse location, in screen coordinates */
   private Point screenPreviousPoint;
   /** the previous mouse location, in virtual coordinates */
   private Point2D previousPoint;
   /** the amout to translate the canvas */
   private double TRANSLATE_AMOUNT = 25.0;
   /** the amout to scale the canvas */
   private double SCALE_AMOUNT = 1.1;
   private ViewerChannel selectedChannel;
   private boolean draggingVerticalScrollbar;
   private boolean draggingHorizontalScrollbar;
   private long spanX;
   private long spanY;

   /**
    * Constructs the mode handler.
    *
    * @param canvas - the canvas
    */
   public InteractionHandler(TimelineTopComponent canvas) {
      this.canvas = canvas;
      draggingVerticalScrollbar = false;
      draggingHorizontalScrollbar = false;
   }

   /**
    * Sets the given object as selected and deselects the previously selected
    * object.
    *
    * @param object - the object to select
    */
   public void setSelectedObject(ViewerChannel object) {

      // deselect previous object
      if (selectedChannel != null) {
         selectedChannel.setSelected(false);
      }

      selectedChannel = object;
      if (selectedChannel != null) {
         selectedChannel.setSelected(true);
//			canvas.getMessageBar().setSelectedObject(object.getAttributeValue("name"));
      } else {
         selectedChannel = null;
//			canvas.getMessageBar().setSelectedObject("");
      }
   }

   /**
    * Gets the last picked point in screen coordinates.
    */
   public Point getScreenPickedPoint() {
      return screenPickedPoint;
   }

   /**
    * Gets the last picked point in virtual coordinates.
    */
   public Point2D getPickedPoint() {
      return pickedPoint;
   }

   /**
    * Gets the previous mouse location in screen coordinates.
    */
   public Point getScreenPreviousPoint() {
      return screenPreviousPoint;
   }

   /**
    * Gets the previous mouse location in virtual coordinates.
    */
   public Point2D getPreviousPoint() {
      return previousPoint;
   }

   /**
    * Gets the current mouse location in screen coordinates.
    */
   public Point getScreenCurrentPoint() {
      return screenCurrentPoint;
   }

   /**
    * Gets the current mouse location in virtual coordinates.
    */
   public Point2D getCurrentPoint() {
      return currentPoint;
   }

   /**
    * Informs the current interactor of a key pressed events. Also checks if
    * the key pressed is a hotkey that transforms the canvas or selects a
    * new interaction.
    */
   public void keyPressed(KeyEvent arg0) {

      int ke = arg0.getKeyCode();

      if (ke == KeyEvent.VK_LEFT) {
         // canvas operations
         double halfWidth = SCROLLBAR_HEIGHT * 2d;
         double invHalfWidth = canvas.getWidth() - halfWidth;
         Point2D timeStart = canvas.getScreenCoordinates(0, Y_SPACER);

         if (!((timeStart.getX() + TRANSLATE_AMOUNT) > invHalfWidth)) {
            canvas.setTranslationX(canvas.getTranslationX() + TRANSLATE_AMOUNT / canvas.getScale());
         }
      } else if (ke == KeyEvent.VK_RIGHT) {
         double halfWidth = SCROLLBAR_HEIGHT * 2d;
         Point2D timeEnd = canvas.getScreenCoordinates(spanX, -spanY);

         if (!((timeEnd.getX() + TRANSLATE_AMOUNT) < halfWidth)) {
            canvas.setTranslationX(canvas.getTranslationX() - TRANSLATE_AMOUNT / canvas.getScale());
         }
      } else if (ke == KeyEvent.VK_UP) {
         double halfWidth = SCROLLBAR_HEIGHT * 2d;
         double invHalfHeight = canvas.getHeight() - halfWidth;
         Point2D timeStart = canvas.getScreenCoordinates(0, Y_SPACER);

         if (!((timeStart.getY() + TRANSLATE_AMOUNT) > invHalfHeight)) {
            canvas.setTranslationY(canvas.getTranslationY() + TRANSLATE_AMOUNT / canvas.getScale());
         }
      } else if (ke == KeyEvent.VK_DOWN) {
         double halfWidth = SCROLLBAR_HEIGHT * 2d;
         Point2D timeEnd = canvas.getScreenCoordinates(spanX, -spanY);

         if (!((timeEnd.getY() + TRANSLATE_AMOUNT) < halfWidth)) {
            canvas.setTranslationY(canvas.getTranslationY() - TRANSLATE_AMOUNT / canvas.getScale());
         }
      } else if (ke
              == KeyEvent.VK_Z && !arg0.isShiftDown()) {
         canvas.setScale(canvas.getScale() * SCALE_AMOUNT);
      } else if (ke
              == KeyEvent.VK_Z && arg0.isShiftDown()) {
         canvas.setScale(canvas.getScale() / SCALE_AMOUNT);
      } else if (ke
              == KeyEvent.VK_V) {
         canvas.zoomAll();
      }
//		case KeyEvent.VK_G:
//			canvas.getGrid().setVisible(!canvas.getGrid().isVisible());
//			mainWindow.setGridVisible(canvas.getGrid().isVisible());
//			break;
   }

   @Override
   public void mousePressed(MouseEvent me) {

      // update the virtual points
      screenCurrentPoint = me.getPoint();
      screenPickedPoint = screenCurrentPoint;
      currentPoint = canvas.getVirtualCoordinates(me.getX(), me.getY());
      pickedPoint = currentPoint;
      if (previousPoint == null) {
         screenPreviousPoint = screenCurrentPoint;
         previousPoint = currentPoint;
      }
      draggingHorizontalScrollbar = isInsideHorizontalScrollbar(screenPickedPoint);
      draggingVerticalScrollbar = isInsideVerticalScrollbar(screenPickedPoint);
   }

   /**
    *
    * @param arg0
    */
   public void mouseReleased(MouseEvent arg0) {
      draggingVerticalScrollbar = false;
      draggingHorizontalScrollbar = false;
   }

   /**
    * Updates the current and previous points
    */
   public void mouseMoved(MouseEvent me) {
      screenCurrentPoint = me.getPoint();
      currentPoint = canvas.getVirtualCoordinates(me.getX(), me.getY());
      if (previousPoint == null) {
         screenPreviousPoint = screenCurrentPoint;
         previousPoint = currentPoint;
      }

      screenPreviousPoint = me.getPoint();
      previousPoint = canvas.getVirtualCoordinates(me.getX(), me.getY());
   }

   /**
    * Updates the current and previous points. If the left button is
    * performing the drag, then the current interactor is informed of the
    * event. If the right button is performing the drag, then the canvas is
    * panned and interactors are not informed of the event.
    */
   public void mouseDragged(MouseEvent me) {

      int meX = me.getX();
      int meY = me.getY();
      double halfWidth = SCROLLBAR_HEIGHT * 2d;
      double invHalfWidth = canvas.getWidth() - halfWidth;
      double invHalfHeight = canvas.getHeight() - halfWidth;

      screenCurrentPoint = me.getPoint();
      currentPoint = canvas.getVirtualCoordinates(meX, meY);

      double dx = screenCurrentPoint.getX() - screenPreviousPoint.getX();
      double dy = screenCurrentPoint.getY() - screenPreviousPoint.getY();

      Point2D timeStart = canvas.getScreenCoordinates(0, Y_SPACER);
      Point2D timeEnd = canvas.getScreenCoordinates(spanX, -spanY);

      if (draggingHorizontalScrollbar) {
         dx *= ((canvas.getWidth() - SCROLLBAR_HEIGHT) / (canvas.getDataUpperX() - canvas.getDataLowerX()));

         if ((timeStart.getX() - dx) > invHalfWidth) {
            dx = timeStart.getX() - invHalfWidth;
         } else if ((timeEnd.getX() - dx) < halfWidth) {
            dx = halfWidth - timeEnd.getX();
            dx *= -1;
         }
         canvas.setTranslationX(canvas.getTranslationX() - dx);
      } else if (draggingVerticalScrollbar) {
         dy *= (canvas.getHeight() - SCROLLBAR_HEIGHT) / (canvas.getDataUpperY());

         if ((timeEnd.getY() - dy) < halfWidth) {
            dy = halfWidth - timeEnd.getY();
            dy *= -1;
         } else if ((timeStart.getY() - dy) > invHalfHeight) {
            dy = timeStart.getY() - invHalfHeight;
         }

         canvas.setTranslationY(canvas.getTranslationY() - dy);
      } else {
         if ((timeEnd.getX() + dx) < halfWidth) {
            dx = halfWidth - timeEnd.getX();
         } else if ((timeStart.getX() + dx) > invHalfWidth) {
            dx = invHalfWidth - timeStart.getX();
         }

         if ((timeEnd.getY() + dy) < halfWidth) {
            dy = halfWidth - timeEnd.getY();
         } else if ((timeStart.getY() + dy) > invHalfHeight) {
            dy = timeStart.getY() - invHalfHeight;
            dy *= -1;
         }
         canvas.setTranslationX(canvas.getTranslationX() + dx);
         canvas.setTranslationY(canvas.getTranslationY() + dy);
      }

      screenPreviousPoint.setLocation(screenCurrentPoint.getX(), screenCurrentPoint.getY());
      previousPoint = canvas.getVirtualCoordinates(screenCurrentPoint.getX(), screenCurrentPoint.getY());
   }

   /**
    * The mouse wheel controls the current zoom factor. Iteractors do not
    * receive mouse wheel events.
    */
   public void mouseWheelMoved(MouseWheelEvent me) {
      Point2D point = me.getPoint();//canvas.getVirtualCoordinates(me.getX(), me.getY());
      System.out.println("x: " +me.getX()+"\ty: "+me.getY() );
      if (me.getWheelRotation() < 0) {
         canvas.setScale(canvas.getScale() * SCALE_AMOUNT, point);
      } else if (me.getWheelRotation() > 0) {
         canvas.setScale(canvas.getScale() / SCALE_AMOUNT, point);
      }
   }

   /**
    * Not implemented.
    */
   public void mouseClicked(MouseEvent me) {
//      System.out.println(me.getYOnScreen() + "\t" + me.getY() + "\t"+canvas.getScale()*(-canvas.getScale() / (canvas.getHeight()*.5)));
//      GLU glu = new GLU();
//
//      int viewport[] = new int[4];
//      float mvmatrix[] = new float[16];
//      float projmatrix[] = new float[16];
//
//      canvas.getGlCanvas().getGL().glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
//      canvas.getGlCanvas().getGL().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
//      canvas.getGlCanvas().getGL().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
//      int realy = 0;// GL y coord pos
//      realy = viewport[3] - (int) me.getY() - 1;
//
//      float wcoord[] = new float[4];// wx, wy, wz;// returned xyz coords
//
//      glu.gluUnProject((float) me.getX(), (float) realy, 0.0f, //
//              mvmatrix, 0,
//              projmatrix, 0,
//              viewport, 0,
//              wcoord, 0);
//          System.out.println("World coords at z=0.0 are ( " //
//                             + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
//                             + ")");
//          glu.gluUnProject((float) me.getX(), (float) realy, 1.0f, //
//              mvmatrix, 0,
//              projmatrix, 0,
//              viewport, 0,
//              wcoord, 0);
//          System.out.println("World coords at z=1.0 are (" //
//                             + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
//                             + ")");
   }

   /**
    * Not implemented.
    */
   public void keyTyped(KeyEvent arg0) {
   }

   /**
    * Not implemented.
    */
   public void mouseEntered(MouseEvent arg0) {
   }

   /**
    * Not implemented.
    */
   public void mouseExited(MouseEvent arg0) {
   }

   @Override
   public void keyReleased(KeyEvent ke) {
      //throw new UnsupportedOperationException("Not supported yet.");
   }

   private boolean isInsideVerticalScrollbar(Point p) {
      double x = p.getX();
      double y = p.getY();

      return x > 0 && x < SCROLLBAR_HEIGHT && y > canvas.getDataLowerY() && y < canvas.getDataLowerY() + canvas.getDataUpperY();
   }

   private boolean isInsideHorizontalScrollbar(Point p) {
      double x = p.getX();
      double y = p.getY();

      return x > SCROLLBAR_HEIGHT + canvas.getDataLowerX() && x < SCROLLBAR_HEIGHT + canvas.getDataUpperX() && y > canvas.getHeight() - SCROLLBAR_HEIGHT;
   }

   /**
    * @return the spanX
    */
   public long getSpanX() {
      return spanX;
   }

   /**
    * @param spanX the spanX to set
    */
   public void setSpanX(long spanX) {
      this.spanX = spanX;
   }

   /**
    * @return the spanY
    */
   public long getSpanY() {
      return spanY;
   }

   /**
    * @param spanY the spanY to set
    */
   public void setSpanY(long spanY) {
      this.spanY = spanY;
   }
}