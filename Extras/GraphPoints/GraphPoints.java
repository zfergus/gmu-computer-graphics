import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 
import java.awt.event.*;

import java.util.*;
import java.io.*;

/**
  * Draws the points read from "points.txt" out to the screen.
  * @author Zachary Ferguson 
  **/
public class GraphPoints extends J1_1_Point
{
	/** Width of this frame. **/
	protected int width; 
	/** Height of the frame. **/
	protected int height; 
	/** Vars for changing the color. **/
	private int colorVar;
	private int delta;
	/* 2D array of point values. */
	ArrayList<ArrayList<Float>> points;
	
	/**
	  * Main method for demoing the GraphPoints class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args)
	{
		try
		{
			/* Create a GraphPoints frame. */
			GraphPoints gp = new GraphPoints();
		
			/* Display the hw frame */
			gp.setVisible(true);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	  * Creates an instance of the GraphPoints class. Defaults: width = 800px, 
	  * height = 800px, title = "GraphPoints".
	  **/
	public GraphPoints() throws IOException
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "GraphPoints", "points.txt");
	}
	
	/**
	  * Creates an instance of the GraphPoints class given the width, 
	  * height, radius, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  **/
	public GraphPoints(int width, int height, String title, String filename) 
		throws IOException
	{
		/* Call the supers constructor. */
		super();
		
		/* Set the fields. */
		this.width = width;
		this.height = height;
		
		this.colorVar = 0;
		this.delta = 1;
		
		/* Scan through the file and make the array of points. */
		Scanner input = new Scanner(new File(filename));
		points = new ArrayList<ArrayList<Float>>();
		while(input.hasNextLine())
		{
			ArrayList<Float> point = new ArrayList<Float>();
			point.add(new Float(input.nextFloat()));
			point.add(new Float(input.nextFloat()));
			points.add(point);
		}
		input.close();
		
		/* Set the size of the frame. */
		this.setSize(this.width, this.height);
		/* Set the title of the frame. */
		this.setTitle(title);
		/* Fixed size window. */
		this.setResizable(false);
	}
	
	/**
	  * Method called once for OpenGL initialization. Initializes to draw only 
	  * on the back frame buffer.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void init(GLAutoDrawable drawable)
	{
		super.init(drawable);
		
		/* Set the clear color to black. */
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		/* Draw only on to the back buffer. */
		gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
	}
	
	/**
	  * Method called once for rendering the circle and point. Clears the buffer
	  * and then draws the points read from the file out ot the display.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glColor3d(1,0,0);
		
		gl.glBegin(GL.GL_POINTS);
		for(int i = 0; i < points.size(); i++)
		{
			double angle = (colorVar/1000F * Math.PI * 2);
			gl.glColor3d((Math.cos(angle)+1)/2,
				(Math.sin(angle)+1)/2, 
				(Math.cos(angle+Math.PI)+1)/2);
			gl.glVertex2f(points.get(i).get(0), points.get(i).get(1));
		}
		gl.glEnd();
		
		this.colorVar+=this.delta;
		if(colorVar > 1000 || colorVar <= 0)
		{
			this.delta = -this.delta;
			this.colorVar+=this.delta;
		}
	}
	
	/**
	  * Method called for OpenGL rendering every reshape. Adjusts the width and 
	  * height stored values. Calculate the radius of the circle. Centers the 
	  * viewport so (0,0) is in the center.
	  * @param drawable Needed, but unused parameter.
	  * @param x X position of the frame.
	  * @param y Y position of the frame.
	  * @param width The new width of the frame.
	  * @param height The new height of the frame.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, 
		int height)
	{
		/* Update the dimensions. */
		this.width = width;
		this.height = height;
		
		/* Set the orthogonal view of the frame. */
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho( -this.width/2.0,  this.width/2.0, 
				   -this.height/2.0, this.height/2.0,
				               -1.0,             1.0);

		/* Set the viewport of the GL frame. */
		gl.glViewport(0, 0, this.width, this.height); 
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}
}