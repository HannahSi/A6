import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.util.*;

/**  This class represents the painting panel and implements all relevant
 * functionality. */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener  {
	private static final long serialVersionUID = 201801;

	/** This is useful for creating custom cursors. */
    private static Toolkit tk= Toolkit.getDefaultToolkit();

    private final Color defaultForegroundColor= Color.BLACK; // Default foreground color

    private BufferedImage img; // The image.
    private int width;  // width of the image
    private int height;  // height of the image

    private Window window; // main window of the program

    private Tool activeTool; // the active tool.
    private int toolSize; // size of the tool.

    private Point2D.Double mousePos;     // Position of mouse, always
    private Point2D.Double mousePosPrev; // Previous mouse position (used to interpolate)

    // State for LINE drawing. False means that no LINE is being drawn.
    // True means: the LINE tool is active and the first press has been made.
    // If it is true, firstPoint describes the point of the first press.
    private boolean pointPressed;
    private Point2D.Double firstPoint;

    private Color foreColor; // Foreground color (used for drawing).
    private Color backColor; // Background color (used for erasing).

    /** Random generator for airbrush. */
    private Random random= new Random(System.currentTimeMillis());

    /** Constructor: a new drawing panel for application window of
     * size(w, h), background color bckColor, and tool size toolSize. */
    public Canvas(Window window, int w, int h, Color bckColor, int toolSize) {
        this.window= window;
        width= w;
        height= h;
        setToolSize(toolSize);

        // Create image with background color bckColor
        img= new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d= (Graphics2D) img.getGraphics();
        g2d.setColor(bckColor);
        System.out.println("DrawingPanel. mousePos: " + mousePos);
        g2d.fillRect(0, 0, w, h);  //mousePos.distance(center);

        foreColor= defaultForegroundColor;
        backColor= bckColor;

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /** Set the foreground color to c.
     * Throw an IllegalArgumentException if c is null */
    public void setForeGroundColor(Color c) {
        if (c == null) throw new IllegalArgumentException();

        foreColor= c;

        if (activeTool == Tool.LINE  &&  pointPressed) {
            repaint();
        }
    }

    /** Set the background color to c.
     * Throw an IllegalArgumentException if c is null */
    public void setBackGroundColor(Color c) {
        if (c == null) throw new IllegalArgumentException();
        backColor= c;
    }

    /** return the Foreground color.  */
    public Color foreGroundColor() {
        return foreColor;
    }

    /** Return the Background color. */
    public Color backGroundColor() {
        return backColor;
    }

    /** Return the image. */
    public BufferedImage getImg() {
        return img;
    }

    /** Return the tool size. */
    public int getToolSize() {
        return toolSize;
    }

    /** Set the tool size to v.
     * Throw an IllegalArgumentException if v < 0. */
    public void setToolSize(int v) {
        if (v < 0)
            throw new IllegalArgumentException("setToolSize: v < 0");
        toolSize= v;
    }

    /** Create new blank image of width w and height h with
     * background color c. */
    public void newBlankImage(int w, int h, Color c) {
        width= w;
        height= h;

        // reset line state
        pointPressed= false;

        img= new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d= (Graphics2D) img.getGraphics();
        g2d.setColor(c);
        g2d.fillRect(0, 0, w, h);

        repaint();
        revalidate();
    }

    /** Change the image to img. */
    public void newImage(BufferedImage img) {
        System.out.println("newImage");

        // reset line state

        width= img.getWidth();
        height= img.getHeight();
        this.img= img;

        repaint();
        revalidate();
    }


    /** Return the dimension of this image. */
    @Override public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
    
    /** Update the mouse position to the coordinates given by e
     *  and make the position appear in the GUI (it's given by label
     *  PaintGUI.mousePositionLabel) */
    @Override public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
        
        // Passes current mouse position to window
        // TODO #06 Implement me!
        int x = (int) (mousePos.getX() + 0.5);	//adding 0.5 and then truncating essentially rounds
        int y = (int) (mousePos.getY() + 0.5);
        window.setMousePosition(x, y);

        // Draws temporary line 
        drawTempLine(e);
    }



    /** Return the active tool. */
    public Tool getActiveTool() {
        return activeTool;
    }

    /** Change cursor to point (x, y), with image given by im */
    public void setActiveTool(int x, int y, String im) {
        Point hotspot= new Point(x, y);
        Image cursorImage= tk.getImage(im);
        Cursor cursor= tk.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        setCursor(cursor);
    }

    /** Set the active tool (and cursor) to t. */
    public void setActiveTool(Tool t) {
        // reset linestate
        pointPressed= false;

        repaint();

        switch(t) {
            case PENCIL:
                setActiveTool(2, 30, "images/pencil-cursor.png");
                break;
            case ERASER:
                setActiveTool(5, 27, "images/eraser-cursor.png"); 
                break;
            case COLOR_PICKER:
                setActiveTool(9, 23, "images/picker-cursor.png");
                break;
            case AIRBRUSH:
                setActiveTool(1, 25, "images/airbrush-cursor.png");
                break;
            case LINE:
                setActiveTool(0, 0, "images/line-cursor.png");
                break;
            default:System.err.println("setActiveTool " + t);
        }

        activeTool= t;		
    }

    @Override public void mouseClicked(MouseEvent e) {
        // Nothing to do here.
    }

    @Override public void mouseEntered(MouseEvent e) {
        // Nothing to do here.
    }

    /** Update the position of the mouse to the position given by e. */
    private void updateMousePosition(MouseEvent e) {
        int x= e.getX();
        int y= e.getY();
        // center of pixel
        mousePos= new Point2D.Double(x+0.5, y+0.5);
    }

    	/** Draw a square of size (toolSize x toolSize) filled with color c. 
    	 *  Its center should be at location (x,y).
    	 */
    private void colorClick(Graphics2D g2d, Color c, int x, int y) {
    	// TODO #07 Implement me!
    		int topLeftX = x - toolSize/2;
    		int topLeftY = y - toolSize/2;

        g2d.setColor(c);
        g2d.fillRect(topLeftX, topLeftY, toolSize, toolSize);
        
        window.setImageUnsaved();
        repaint();
    }
    
    /** Draws a line with color c and stroke toolSize from
     *  mouse position mousePosPrev to position mousePos.
     */
    private void colorDrag(Graphics2D g2d, Color c) {
    	// TODO #08 Implement me!
    		if (activeTool == Tool.PENCIL || activeTool == Tool.ERASER) {
    	        g2d.setColor(c);
    			g2d.setStroke(new BasicStroke(toolSize));
    	        g2d.drawLine((int) mousePosPrev.x, (int) mousePosPrev.y, (int) mousePos.x, (int) mousePos.y);
    			
    	        window.setImageUnsaved();
    			repaint();
    		}
    }
    
    /** Airbrush with the current foreground color in a square of size
     *  toolsize centered at the current position of the mouse.
     * 
     *  Note: for nicer-looking results, you may use a circle instead 
     *  of a square.		
     */
    private void airBrush(Graphics2D g2d, Color c) {
    	// TODO #9 implement me!
    		g2d.setColor(c);
    		for (Rectangle pixel : getAirbrushPixels(10))
    			g2d.fill(pixel);
    		
        window.setImageUnsaved();
    		repaint();
    }
   
    /** Return an arraylist of 1x1 Rectangles (pixels) that randomly fill a given percentage
     *  of a circle centered at the current mouse position and with a radius of toolSize.
     *  Precondition: percentage is between 0 and 100, inclusive.
     *  */
    private ArrayList<Rectangle> getAirbrushPixels(double percentage){
    		ArrayList<Rectangle> pixels = new ArrayList<Rectangle>();
 
    		int left = (int) Math.round(mousePos.x - toolSize/2.0);
    		int right = (int) Math.round(mousePos.x + toolSize/2.0);
    		
    		for (int x = left; x <= right; x++) {
    			int top = (int) mousePos.y + arcHeight(x, mousePos.x, toolSize/2.0);
    			int bottom = (int) mousePos.y - arcHeight(x, mousePos.x, toolSize/2.0);
    			
    			for (int y = top; y >= bottom; y--)
    				if (random.nextDouble() < percentage/100) 
    					pixels.add(new Rectangle(x, y, 1, 1));
    		}
    		return pixels;
    }
    
    /** Return the height of the circle above its horizontal axis at a given x coordinate, provided its
     *  radius and the x coordinate of its center.
     *  Precondition: The distance between xCenter and xCoordinate doesn't exceed the magnitude of the radius
     *  */
    private int arcHeight(double xCoordinate, double xCenter, double radius) {
    		return (int) Math.round(Math.sqrt(Math.pow(radius, 2) - Math.pow(xCenter-xCoordinate,2)));
    }


    /** Process the press of the mouse, given by e. */
    @Override public void mousePressed(MouseEvent e) {
        updateMousePosition(e);
        System.out.println("mousePressed: " + mousePos + ", active tool: " + getActiveTool());

        Graphics2D g2d= (Graphics2D) img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // anti-aliasing

        if (activeTool == Tool.PENCIL) {
            System.out.println("mousePressed: pencil");
            colorClick(g2d, foreGroundColor(),e.getX(), e.getY());
        }
        else if (activeTool == Tool.ERASER) {
            System.out.println("mousePressed: eraser");
            colorClick(g2d, backGroundColor(),e.getX(), e.getY());
        }
        else if (activeTool == Tool.COLOR_PICKER) {
            System.out.println("mousePressed: pick color");
            // Pick the color of the pixel the mouse is currently over.            
            // Left mouse button pressed: set the new foreground color
            // Right mouse button pressed: set the new background color
            pickColor(e);
        }
        else if (activeTool == Tool.LINE){
            System.out.println("mousePressed: line");

            // TODO: #10a. Implement me!
            // If no mouse press has been made yet with this tool active,
            // this IS the first mouse press; record it.
            // If one has already been made, this is the second mouse press;
            // draw the line.
            if (!pointPressed) {
            		pointPressed = true;
            		firstPoint = new Point2D.Double(e.getX() + 0.5, e.getY() + 0.5);
            }
            else {
            		pointPressed = false;
            		g2d.setColor(foreColor);
        	        g2d.setStroke(new BasicStroke(toolSize));
        	        g2d.drawLine((int) firstPoint.x, (int) firstPoint.y, e.getX(), e.getY());
            		window.setImageUnsaved();
            }
        }
        else if (activeTool == Tool.AIRBRUSH) {
            System.out.println("mousePressed: airbrush");
            airBrush(g2d, foreColor);
        }
        else {
            System.err.println("Unknown tool: " + activeTool);
        }

        // set prevMousePos
        mousePosPrev= mousePos;
    }


    @Override public void mouseExited(MouseEvent e) {
        // Nothing to do here.
    }

    @Override public void mouseReleased(MouseEvent e) {
        // End of drawing, reset prevMousePos.
        mousePosPrev= null;
    }
    


    /** Process the dragging of the mouse given by e. */
    @Override public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
        System.out.println("mouseDragged: " + mousePos + ", active tool: " + activeTool);		

        Graphics2D g2d= (Graphics2D) img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (activeTool == Tool.PENCIL) {
        		colorDrag(g2d,foreGroundColor());

        }
        else if (activeTool == Tool.ERASER) {
        	colorDrag(g2d,backGroundColor());
        }
        else if (activeTool == Tool.COLOR_PICKER) {
            // Nothing to do here.
        }
        else if (activeTool == Tool.AIRBRUSH) {
            airBrush(g2d, foreColor); // note: doesn't interpolate
        }
        else {
            System.err.println("active tool: " + activeTool);
        }

        // update prevMousePos
        mousePosPrev= mousePos;
    }

    private void drawTempLine(MouseEvent e) {
        // TODO #10b. Implement me!
        // If the active tool is the Line  and the first mouse 
        // press has been recognized,  repaint().
        if (activeTool == Tool.LINE && pointPressed)
	        repaint();
    }
    
    /** Paint this component using g. */
    @Override public void paintComponent(Graphics g) {
        System.out.println("Paint drawing pane.");

        super.paintComponent(g);
        Graphics2D g2d= (Graphics2D) g;

        // Draw a border around the image.
        int z= 0;
        for (int i= 0; i<5; i++) {
            Color c= new Color(z,z,z);
            g2d.setColor(c);
            g2d.drawLine(0, height+i, width+i, height+i);
            g2d.drawLine(width+i, 0, width+i, height+i);
            z += 63;
        }

        g2d.drawImage(img, 0, 0, null);

        // TODO: #10c. Implement me!
        // If the active tool is the LINE and the first point has been pressed,
        // draw the line on g2d using the foreColor and toolSize.
        if (activeTool == Tool.LINE && pointPressed) {
	        	g2d.setColor(foreColor);
	        g2d.setStroke(new BasicStroke(toolSize));
	        g2d.drawLine((int) firstPoint.x, (int) firstPoint.y, (int) mousePos.x, (int) mousePos.y);
        }
    }


    /** Pick the color of the pixel of img given by e. 
     * Left mouse button pressed: use color as new foreground color.
     * Right mouse button pressed: use color as new background color. */
    private void pickColor(MouseEvent e) {
        int rgb= img.getRGB(e.getX(),e.getY());
        Color pickedColor= new Color(rgb);
        int b= e.getButton();
        if (b == MouseEvent.BUTTON1) {
            setForeGroundColor(pickedColor);   // Left button clicked
            window.updateForeColor();
        } else if (b == MouseEvent.BUTTON3) {
            setBackGroundColor(pickedColor);  // Right button clicked
            window.updateBackColor();
        }
    }




}
