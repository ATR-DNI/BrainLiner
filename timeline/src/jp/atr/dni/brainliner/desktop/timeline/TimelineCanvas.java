///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package jp.atr.dni.bmi.desktop.timeline;
//
//import com.jogamp.opengl.util.Animator;
//import com.jogamp.opengl.util.gl2.GLUT;
//import java.awt.Dimension;
//import java.awt.Point;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseWheelEvent;
//import java.awt.event.MouseWheelListener;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Point2D;
//import java.util.ArrayList;
//import java.util.Date;
//import javax.media.opengl.GL;
//import javax.media.opengl.GL2;
//import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCapabilities;
//import javax.media.opengl.GLEventListener;
//import javax.media.opengl.awt.GLCanvas;
//import javax.media.opengl.glu.GLU;
//
//import jp.atr.dni.bmi.desktop.model.GeneralFileInfo;
//import org.openide.util.Lookup;
//import org.openide.util.LookupEvent;
//import org.openide.util.LookupListener;
//import org.openide.util.Utilities;
//
///**
// *
// * @author Makoto Takemiya - [武宮 誠] <br />
// * <i>ATR - [株式会社・國際電氣通信基礎技術研究所]</i>
// *
// * @version 2011/02/15
// */
//public class TimelineCanvas extends GLCanvas implements GLEventListener {
//
//   private boolean SNAP_TO_GRID = false;
//   private boolean SHOW_GRID;
//   private double timeIncre = 1;
//   /** the amout to translate the canvas */
//   public static final double TRANSLATE_AMOUNT = 10.0;
//   /** the amout to scale the canvas */
//   public static final double SCALE_AMOUNT = 1.1;
//   /** the x translation of the map */
//   private double translationX = 0;
//   /** the y translation of the map */
//   private double translationY = 0;
//   /** the scale of the map */
//   private double scale = 1.0;
//   /** the previously point clicked by the mouse, in screen coordinates */
//   private Point screenPickedPoint;
//   /** the previously point clicked by the mouse, in virtual coordinates */
//   private Point2D pickedPoint;
//   /** the current mouse location, in screen coordinates */
//   private Point screenCurrentPoint;
//   /** the current mouse location, in virtual coordinates */
//   private Point2D currentPoint;
//   /** the previous mouse location, in screen coordinates */
//   private Point screenPreviousPoint;
//   /** the previous mouse location, in virtual coordinates */
//   private Point2D previousPoint;
//   /** the transform for virtual to screen coordinates */
//   private AffineTransform transform = new AffineTransform();
//   /** the transform for screen to virtual coordinates */
//   private AffineTransform inverseTransform = new AffineTransform();
//   private GLUT glut;
//   private GLU glu;
//   private Dimension size;
////   private TextRenderer renderer;
//   private GeneralFileInfo fileInfo;
//
//   /**
//    * Default constructor.
//    */
//   public TimelineCanvas(GLCapabilities caps) {
//      super(caps);
//
//      initGL();
//      SHOW_GRID = true;
//   }
//
//   /**
//    * code to initialize the openGL timeline
//    */
//   private void initGL() {
//      System.out.println("initgl2");
//
//
////      glut = new GLUT();
////      glu = new GLU();
//      scale = .05;
//
////      renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
//
//      addGLEventListener(this);
//
//      addMouseListener(new MouseListener() {
//
//         @Override
//         public void mouseClicked(MouseEvent me) {
//         }
//
//         @Override
//         public void mousePressed(MouseEvent me) {
//            // update the virtual points
//            screenCurrentPoint = me.getPoint();
//            screenPickedPoint = screenCurrentPoint;
//            currentPoint = getVirtualCoordinates(me.getX(), me.getY());
//            pickedPoint = currentPoint;
//            if (previousPoint == null) {
//               screenPreviousPoint = screenCurrentPoint;
//               previousPoint = currentPoint;
//            }
//         }
//
//         @Override
//         public void mouseReleased(MouseEvent me) {
//         }
//
//         @Override
//         public void mouseEntered(MouseEvent me) {
//         }
//
//         @Override
//         public void mouseExited(MouseEvent me) {
//         }
//      });
//
//      addMouseMotionListener(new MouseMotionListener() {
//
//         @Override
//         public void mouseDragged(MouseEvent me) {
//            Point2D currentPoint = me.getPoint();// getVirtualCoordinates(arg0.getX(),
//            // arg0.getY());
//            // getVirtualCoordinates(arg0.getX(), arg0.getY());
//
//            // left button performs an action or drags the canvas
//            if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) > 0) {
//               double dx = currentPoint.getX() - previousPoint.getX();
//               double dy = previousPoint.getY() - currentPoint.getY();
//               //            System.out.println("dx: " + dx + "\tdy: " + dy);
//               translationX += dx;
//               translationY += dy;
//
//               previousPoint = me.getPoint();
//
//               buildTransforms();
//            } else if ((me.getModifiers() & MouseEvent.BUTTON3_MASK) > 0) {
//            }
//         }
//
//         @Override
//         public void mouseMoved(MouseEvent me) {
//            previousPoint = me.getPoint();// getVirtualCoordinates(arg0.getX(), arg0.getY());
//         }
//      });
//
//      addMouseWheelListener(new MouseWheelListener() {
//
//         @Override
//         public void mouseWheelMoved(MouseWheelEvent mwe) {
//            System.out.println("wheel moved");
//            if (mwe.getWheelRotation() < 0) {
//               setScale(getScale() * SCALE_AMOUNT);
//            } else if (mwe.getWheelRotation() > 0) {
//               setScale(getScale() / SCALE_AMOUNT);
//            }
//         }
//      });
//
//      Animator animator = new Animator(this);
//      animator.add(this);
//      animator.start();
//      System.out.println("animator started");
//
//
//
//      //TODO:add a resize listener
////      this.addComponentListener(new ComponentAdapter() {
////         @Override
////			public void componentResized(ComponentEvent arg0) {
////            System.out.println("resized");
////
////				// resize about the center of the scene
////				if (getSize() != null) {
////					Dimension newSize = TimelineCanvas.this.getSize();
////					translationX += (newSize.width - getSize().width)/2.0;
////					translationY += (newSize.height - getSize().height)/2.0;
////				}
////
////				// update the view transforms when the canvas is resized
////				buildTransforms();
////				setSize(TimelineCanvas.this.getSize());
////			}
////		});
//
////      Lookup.Result<GeneralFileInfo> fileInfos = Utilities.actionsGlobalContext().lookupResult(GeneralFileInfo.class);
////      fileInfos.allItems();  // This means something. THIS IS IMPORTANT.
////      fileInfos.addLookupListener(new LookupListener(){
////         @Override
////         public void resultChanged(LookupEvent e){
////            System.out.println("change");
////
////            GeneralFileInfo obj = Utilities.actionsGlobalContext().lookup(GeneralFileInfo.class);
////
////            if (obj != null && obj.getFileExtention().equals("nsn")) {
////               System.out.println("adding data");
////               fileInfo = obj;
////               NSReader reader = new NSReader();
////               NeuroshareFile nsn = reader.readNSFileAllData(fileInfo.getFilePath());
////               fileInfo.setNsObj(nsn);
////            }
////          }}
////      );
//   }
//
//   /**
//    * Converts screen coordinates to virtual coordinates.
//    *
//    * @param x - the x component of the screen coordinate
//    * @param y - the y component of the screen coordinate
//    * @return - the point in virtual coordinates.
//    */
//   public Point2D getVirtualCoordinates(double x, double y) {
//      return inverseTransform.transform(new Point2D.Double(x, y), null);
//   }
//
//   /**
//    * Converts virtual coordinates to screen coordinates.
//    *
//    * @param x - the x component of the virtual coordinate
//    * @param y - the y component of the virtual coordinate
//    * @return - the point in screen coordinates.
//    */
//   public Point2D getScreenCoordinates(double x, double y) {
//      return transform.transform(new Point2D.Double(x, y), null);
//   }
//
//   /**
//    * Rebuilds the view transform and the inverse view transforms.
//    */
//   private void buildTransforms() {
//
//      double width = getWidth();
//      double height = getHeight();
//      transform = new AffineTransform(1, 0, 0, 1, 0, 0);
//      transform.translate(0.5 * width, 0.5 * height);
//      transform.scale(scale, scale);
//      transform.translate(translationX - width / 2.0, translationY - height / 2.0);
//
//      try {
//         inverseTransform = transform.createInverse();
//      } catch (Exception e) {
//      }
//   }
//
//   /**
//    * Gets the tranlation in the x direction.
//    */
//   public double getTranslationX() {
//      return translationX;
//   }
//
//   /**
//    * Sets the translation in the x direction and rebuilds the transforms.
//    */
//   public void setTranslationX(double translationX) {
//      this.translationX = translationX;
//      buildTransforms();
//   }
//
//   /**
//    * Gets the tranlation in the y direction.
//    */
//   public double getTranslationY() {
//      return translationY;
//   }
//
//   /**
//    * Sets the translation in the y direction and rebuilds the transforms.
//    */
//   public void setTranslationY(double translationY) {
//      this.translationY = translationY;
//      buildTransforms();
//   }
//
//   /**
//    * Gets the scale scale.
//    */
//   public double getScale() {
//      return scale;
//   }
//
//   /**
//    * Sets the scale and rebuilds the affine transforms.
//    */
//   public void setScale(double scale) {
//      if (scale < 0.008 || scale > .2) {
//         return;
//      }
//
//      this.scale = scale;
//      buildTransforms();
//   }
//
//   /**
//    * Order to draw:
//    * 1) grid
//    * 2) data
//    * 3) labels
//    * 4) timeline
//    *
//    * @param drawable
//    */
//   private void render(GLAutoDrawable drawable) {
//      System.out.println("render");
//      int width = getWidth();
//      int height = getHeight();
//
//      GL2 gl = drawable.getGL().getGL2();
//      gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
//
//      if (SHOW_GRID) {
//         gl.glLoadIdentity();
//      }
////      gl.glClearColor( .9f, .9f, .9f, 1.0f );
//
//      gl.glMatrixMode(GL2.GL_PROJECTION);
//      gl.glLoadIdentity();
////glu.gluOrtho2D (0,
////                 getWidth(),
////                0,
////                getHeight());
//
//      gl.glViewport(0, 0, width, height);//TODO: look into this some more
//
//      if (getFileInfo() == null) {
//         return;
//      }
//
//      int max = 500;
//
//      gl.glColor3d(.6, .1, .5);
//
//      max = getFileInfo().getNsObj().getEntities().size();
//
//      //Draw data
//      gl.glLineWidth(1);
//
//      gl.glLoadIdentity();
//      gl.glTranslated(translationX / (width * .5), translationY / (height * .5), 0);
//      gl.glScaled(scale, scale, 0);
//
//      gl.glBegin(GL.GL_LINES);
//
//      double yOffset = 0;
//
//      //draw data
//      for (Entity e : getFileInfo().getNsObj().getEntities()) {
//         if (e.getTag().getEntityType() == EntityType.ENTITY_ANALOG) {
//            AnalogInfo ai = (AnalogInfo) e;
//
//            if (ai == null || ai.getData() == null) {
//               continue;
//            }
//
//            double normalizer = Math.max(Math.abs(ai.getMaxVal()), Math.abs(ai.getMinVal()));
//
//            for (AnalogData ad : ai.getData()) {
//
//               ArrayList<Double> vals = ad.getAnalogValues();
//               double lastX = 0;
//               double lastY = (vals.get(0) / normalizer) - yOffset * 5;
//
//               for (int i = 0; i < vals.size(); i++) {
//                  if (i % 2 == 0) {
//                     gl.glVertex2d(lastX, lastY);
//                  } else {
//                     lastY = (vals.get(i) / normalizer) - yOffset * 5;
//                     gl.glVertex2d(i, lastY);
//                  }
//                  lastX = i;
//               }
//               yOffset++;
//            }
//         }
//      }
//      gl.glEnd();
//
//      //Draw labels
//      gl.glLoadIdentity();
//      gl.glTranslated((-scale / (width * .5)) - .99, translationY / (height * .5), 0);
//      gl.glScaled(scale, scale, 0);
//
//      for (int i = 0; i < max; i++) {
//         //Draw label
//         drawText(gl, getFileInfo().getNsObj().getEntities().get(i).getEntityInfo().getEntityLabel(), 0, -5 * i, 0.0125f, 2.0f);
//      }
//
//      //Draw bottom time lables
//      gl.glLoadIdentity();
//      gl.glTranslated((translationX / (width * .5)), -scale / (height * .5) - .83, 0);
//      gl.glScaled(scale, scale, 0);
//
//      for (int i = 0; i < 1000; i += 35) {
//         drawText(gl, new Date(i).toString(), i, 0, 0.0125f, 2.0f);
//      }
//
//      //Draw bottom time scroller
//      drawTimelineScroller(gl, width);
//   }
//
//   /**
//    * Utility function for drawing text
//    *
//    * @param gl - the JOGL context
//    * @param text 0 the text to draw
//    * @param x - the x position
//    * @param y - the y position
//    * @param size - the size of the text
//    * @param width - the width of the letters
//    */
//   public void drawText(GL2 gl, String text, int x, int y, float size, float width) {
////      gl.glPushMatrix();
////gl.glTranslated(x, y, 0);
////        gl.glScalef(size, size, 0.0f);
////      renderer.beginRendering(glCanvas.getWidth(), glCanvas.getHeight());
////    // optionally set the color
////    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
////    renderer.draw("Text to draw", x, y);
////    // ... more draw commands, color changes, etc.
////    renderer.endRendering();
////    gl.glPopMatrix();
//
//      gl.glPushMatrix();
//      gl.glTranslated(x, y, 0);
////      gl.glRotatef(angle, 0, 0, 1);
//      gl.glScalef(size, size, 0.0f);
//      gl.glLineWidth(width);
//      glut.glutStrokeString(GLUT.STROKE_ROMAN, text);
//      gl.glPopMatrix();
//   }
//
//   public void drawTimelineScroller(GL2 gl, float width) {
//      gl.glLoadIdentity();
//      gl.glColor3f(1, 0, 0);
//      gl.glBegin(GL2.GL_QUADS);
////      gl.glTexCoord2f(0, 0);
//      gl.glVertex3f(-width / 2, (float) -.85, 0);
////      gl.glTexCoord2f(0, 1);
//      gl.glVertex3f(-width / 2, -1, 0);
////      gl.glTexCoord2f(1, 1);
//      gl.glVertex3f(width / 2, -1, 0);
////      gl.glTexCoord2f(1, 0);
//      gl.glVertex3f(width / 2, (float) -.85, 0);
//      gl.glEnd();
//   }
//
//   @Override
//   public void init(GLAutoDrawable drawable) {
//      GL2 gl = (GL2) drawable.getGL();
//      glu = new GLU();
//      glut = new GLUT();
//
//      // set the drawing parameters
//      gl.glClearColor(.9f, .9f, .9f, 1.0f);
//      gl.glPointSize(3.0f);
//      gl.glEnable(GL2.GL_LINE_SMOOTH);
//      gl.glEnable(GL2.GL_BLEND);
//      gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
//      gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_DONT_CARE);
//      gl.glLineWidth(1.5f);
//      drawable.getGL().setSwapInterval(1);
//   }
//
//   @Override
//   public void dispose(GLAutoDrawable glad) {
//   }
//
//   @Override
//   public void display(GLAutoDrawable drawable) {
//      render(drawable);
//   }
//
//   @Override
//   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
//      GL2 gl = (GL2) drawable.getGL();
//      gl.glViewport(0, 0, width, height);
//      gl.glMatrixMode(GL2.GL_PROJECTION);
//      gl.glLoadIdentity();
//      glu.gluOrtho2D(0.0, width, height, 0);
//   }
//
//   /**
//    * @return the fileInfo
//    */
//   public GeneralFileInfo getFileInfo() {
//      return fileInfo;
//   }
//
//   /**
//    * @param fileInfo the fileInfo to set
//    */
//   public void setFileInfo(GeneralFileInfo fileInfo) {
//      this.fileInfo = fileInfo;
//   }
//
//   /**
//    * @return the size
//    */
//   public Dimension getSize() {
//      return size;
//   }
//
//   /**
//    * @param size the size to set
//    */
//   public void setSize(Dimension size) {
//      this.size = size;
//   }
//}
