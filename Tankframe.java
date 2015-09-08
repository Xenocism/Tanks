/******************************************************************************
  * Creator: Samuel Ferguson
  * (incorperates large amounts of code from StdDraw (Princeton COS126) by Kevin Wayne & Robert Sedgewick)
  *
  * Dependancies: none
  * 
  * Descrition: Creates game window and functions
  * 
  ****************************************************************************/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.LinkedList;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class Tankframe implements MouseListener, MouseMotionListener, KeyListener {

	// show we draw immediately or wait until next show?
    private static boolean defer = false;

        // queue of typed key characters
    private static LinkedList<Character> keysTyped = new LinkedList<Character>();

    // set of key codes currently pressed down
    private static TreeSet<Integer> keysDown = new TreeSet<Integer>();

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;

    private static final Color DEFAULT_CLEAR_COLOR = Color.WHITE;

	// boundary of drawing canvas, 0% border
    // private static final double BORDER = 0.05;
    private static final double BORDER = 0.00;
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax, ymax;

    // double buffered graphics
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;

    // singleton for callbacks: avoids generation of extra .class files
    private static Tankframe std = new Tankframe();

    // the frame for drawing to the screen
    private static JFrame frame;

    // mouse state
    private static boolean mousePressed = false;
    private static double mouseX = 0;
    private static double mouseY = 0;
    
    // for synchronization
    private static Object mouseLock = new Object();
    private static Object keyLock = new Object();

    // time in milliseconds (from currentTimeMillis()) when we can draw again
    // used to control the frame rate
    private static long nextDraw = -1;  

    // singleton pattern: client can't instantiate
    private Tankframe() { }

    // static initializer
    static {
        init();
    }

     //Sets the window size to the default size 512-by-512 pixels.
     //This method must be called before any other commands.
    public static void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }

     // Sets the window size to <tt>w</tt>-by-<tt>h</tt> pixels.
     // This method must be called before any other commands.

     // @param  w the width as a number of pixels
     // @param  h the height as a number of pixels
     // @throws IllegalArgumentException if the width or height is 0 or negative
    public static void setCanvasSize(int w, int h) {
        if (w < 1 || h < 1) throw new IllegalArgumentException("width and height must be positive");
        width = w;
        height = h;
        init();
    }

    // init
    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        clear();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                  RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // frame stuff
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);

        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);

        frame.setContentPane(draw);
        frame.addKeyListener(std);    // JLabel cannot get keyboard focus
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
        frame.setTitle("Tanks");
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }


/***************************************************************************
*  User and screen coordinate systems.
***************************************************************************/

     // Sets the x-scale to be the default (between 0.0 and 1.0).
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

     // Sets the y-scale to be the default (between 0.0 and 1.0).
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

     // Sets the x-scale.
     
    // @param min the minimum value of the x-scale
    // @param max the maximum value of the x-scale
    public static void setXscale(double min, double max) {
        double size = max - min;
        synchronized (mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
        }
    }

     // Sets the y-scale.
     
     // @param min the minimum value of the y-scale
     // @param max the maximum value of the y-scale
    public static void setYscale(double min, double max) {
        double size = max - min;
        synchronized (mouseLock) {
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    // helper functions that scale from user coordinates to screen coordinates and back
    private static double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
    private static double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
    private static double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private static double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    private static double   userX(double x) { return xmin + x * (xmax - xmin) / width;    }
    private static double   userY(double y) { return ymax - y * (ymax - ymin) / height;   }

    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

     // Clears the screen to the given color.
     
     // @param color the color to make the background
    public static void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        draw();
    }

/***************************************************************************
*  Drawing images.
***************************************************************************/

    // get an image from the given filename
    private static Image getImage(String filename) {

        // to read from file
        ImageIcon icon = new ImageIcon(filename);
        return icon.getImage();
    }

     // Draw picture (gif, jpg, or png) centered on (x, y).
     // @param x the center x-coordinate of the image
     // @param y the center y-coordinate of the image
     // @param s the name of the image/picture, e.g., "ball.gif"
     // @throws IllegalArgumentException if the image is corrupt
    public static void picture(double x, double y, String s) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + s + " is corrupt");

        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        draw();
    }

     // Draw picture (gif, jpg, or png) centered on (x, y),
     // rotated given number of degrees.
     // @param x the center x-coordinate of the image
     // @param y the center y-coordinate of the image
     // @param s the name of the image/picture, e.g., "ball.gif"
     // @param degrees is the number of degrees to rotate counterclockwise
     // @throws IllegalArgumentException if the image is corrupt
    public static void picture(double x, double y, String s, double degrees) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + s + " is corrupt");

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);

        draw();
    }

     // Draw picture (gif, jpg, or png) centered on (x, y), rescaled to w-by-h.
     // @param x the center x coordinate of the image
     // @param y the center y coordinate of the image
     // @param s the name of the image/picture, e.g., "ball.gif"
     // @param w the width of the image
     // @param h the height of the image
     // @throws IllegalArgumentException if the width height are negative
     // @throws IllegalArgumentException if the image is corrupt
    public static void picture(double x, double y, String s, double w, double h) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        if (w < 0) throw new IllegalArgumentException("width is negative: " + w);
        if (h < 0) throw new IllegalArgumentException("height is negative: " + h);
        double ws = factorX(w);
        double hs = factorY(h);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + s + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else {
            offscreen.drawImage(image, (int) Math.round(xs - ws/2.0),
                                       (int) Math.round(ys - hs/2.0),
                                       (int) Math.round(ws),
                                       (int) Math.round(hs), null);
        }
        draw();
    }


     // Draw picture (gif, jpg, or png) centered on (x, y), rotated
     // given number of degrees, rescaled to w-by-h.
     // @param x the center x-coordinate of the image
     // @param y the center y-coordinate of the image
     // @param s the name of the image/picture, e.g., "ball.gif"
     // @param w the width of the image
     // @param h the height of the image
     // @param degrees is the number of degrees to rotate counterclockwise
     // @throws IllegalArgumentException if the image is corrupt
    public static void picture(double x, double y, String s, double w, double h, double degrees) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(w);
        double hs = factorY(h);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + s + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0),
                                   (int) Math.round(ys - hs/2.0),
                                   (int) Math.round(ws),
                                   (int) Math.round(hs), null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);

        draw();
    }

/***************************************************************************
*  Drawing geometric shapes.
***************************************************************************/

     // Draw one pixel at (x, y).
     // @param x the x-coordinate of the pixel
     // @param y the y-coordinate of the pixel
    private static void pixel(double x, double y) {
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }

/**********************************************************************************************
* Timer and display functions
***********************************************************************************************/
     // Display on screen, pause for t milliseconds, and turn on
     // <em>animation mode</em>: subsequent calls to
     // drawing methods such as <tt>line()</tt>, <tt>circle()</tt>, and <tt>square()</tt>
     // will not be displayed on screen until the next call to <tt>show()</tt>.
     // This is useful for producing animations (clear the screen, draw a bunch of shapes,
     // display on screen for a fixed amount of time, and repeat). It also speeds up
     // drawing a huge number of shapes (call <tt>show(0)</tt> to defer drawing
     // on screen, draw the shapes, and call <tt>show(0)</tt> to display them all
     // on screen at once).
     // @param t number of milliseconds
    public static void show(int t) {
        // sleep until the next time we're allowed to draw
        long millis = System.currentTimeMillis();
        if (millis < nextDraw) {
            try {
                Thread.sleep(nextDraw - millis);
            }
            catch (InterruptedException e) {
                System.out.println("Error sleeping");
            }
            millis = nextDraw;
        }

        defer = false;
        draw();
        defer = true;

        // when are we allowed to draw again
        nextDraw = millis + t;
    }

     // Display on-screen and turn off animation mode:
     // subsequent calls to
     // drawing methods such as <tt>line()</tt>, <tt>circle()</tt>, and <tt>square()</tt>
     // will be displayed on screen when called. This is the default.
    public static void show() {
        defer = false;
        nextDraw = -1;
        draw();
    }

    // draw onscreen if defer is false
    private static void draw() {
        if (defer) return;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }

/***************************************************************************
*  Mouse interactions.
***************************************************************************/


     // Returns true if the mouse is being pressed.
     
     // @return <tt>true</tt> if the mouse is being pressed; <tt>false</tt> otherwise
    public static boolean mousePressed() {
        synchronized (mouseLock) {
            return mousePressed;
        }
    }

     // Returns the x-coordinate of the mouse.
     
     // @return the x-coordinate of the mouse
    public static double mouseX() {
        synchronized (mouseLock) {
            return mouseX;
        }
    }

     // Returns the y-coordinate of the mouse.
     
     // @return y-coordinate of the mouse
    public static double mouseY() {
        synchronized (mouseLock) {
            return mouseY;
        }
    }


     // This method cannot be called directly.
    @Override
    public void mouseClicked(MouseEvent e) { }

     // This method cannot be called directly.
    @Override
    public void mouseEntered(MouseEvent e) { }

     // This method cannot be called directly.
    @Override
    public void mouseExited(MouseEvent e) { }

     // This method cannot be called directly.
    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = Tankframe.userX(e.getX());
            mouseY = Tankframe.userY(e.getY());
            mousePressed = true;
        }
    }

     // This method cannot be called directly.
    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseLock) {
            mousePressed = false;
        }
    }

     // This method cannot be called directly.
    @Override
    public void mouseDragged(MouseEvent e)  {
        synchronized (mouseLock) {
            mouseX = Tankframe.userX(e.getX());
            mouseY = Tankframe.userY(e.getY());
        }
    }

     // This method cannot be called directly.
    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = Tankframe.userX(e.getX());
            mouseY = Tankframe.userY(e.getY());
        }
    }

/***************************************************************************
*  Keyboard interactions.
***************************************************************************/

     // Returns true if the user has typed a key.
     // @return <tt>true</tt> if the user has typed a key; <tt>false</tt> otherwise
    public static boolean hasNextKeyTyped() {
        synchronized (keyLock) {
            return !keysTyped.isEmpty();
        }
    }

     // The next key that was typed by the user.
     // <p>
     // This method returns a Unicode character corresponding to the key
     // typed (such as <tt>'a'</tt> or <tt>'A'</tt>).
     // It cannot identify action keys (such as F1 and arrow keys)
     // or modifier keys (such as control).
     
     // @return the next key typed by the user
    public static char nextKeyTyped() {
        synchronized (keyLock) {
            return keysTyped.removeLast();
        }
    }

     // Returns true if the given key is being pressed.
     // <p>
     // This method takes the keycode (corresponding to a physical key)
     // as an argument. It can handle action keys
     // (such as F1 and arrow keys) and modifier keys (such as shift and control).
     // See {@link KeyEvent} for a description of key codes.
     
     // @param  keycode the key to check if it is being pressed
     // @return <tt>true</tt> if <tt>keycode</tt> is currently being pressed;
     //         <tt>false</tt> otherwise
    public static boolean isKeyPressed(int keycode) {
        synchronized (keyLock) {
            return keysDown.contains(keycode);
        }
    }


     // This method cannot be called directly.
    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (keyLock) {
        	Character c = new Character(e.getKeyChar());
            keysTyped.addFirst(e.getKeyChar());
        }
    }

     // This method cannot be called directly.
    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.add(e.getKeyCode());
        }
    }

     // This method cannot be called directly.
    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.remove(e.getKeyCode());
        }
    }


}