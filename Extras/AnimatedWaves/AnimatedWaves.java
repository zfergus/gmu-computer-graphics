import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 
import java.awt.event.*;

/**
  * AnimatedWaves: Draws and animates a sine wave.
  * @author Zachary Ferguson 
  **/
public class AnimatedWaves extends J1_1_Point
{
	/** Width of this frame. **/
	protected int width; 
	/** Height of the frame. **/
	protected int height; 
	
	private float c = 0;
	
	/**
	  * Main method for demoing the AnimatedWaves class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		/* Create a AnimatedWaves frame. */
		AnimatedWaves wave = new AnimatedWaves(800, 200, 
			"AnimatedWaves");
		
		/* Display the hw frame */
		wave.setVisible(true);
	}
	
	/**
	  * Creates an instance of the AnimatedWaves class. Defaults: width = 
	  * 800px, height = 800px, title = "".
	  **/
	public AnimatedWaves()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "");
	}
	
	/**
	  * Creates an instance of the AnimatedWaves class given the width, 
	  * height, radius, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  **/
	public AnimatedWaves(int width, int height, String title)
	{
		/* Call the supers constructor. */
		super();
		
		/* Set the fields. */
		this.width = width;
		this.height = height;
		this.c = 0;
		
		/* Set the size of the frame. */
		this.setSize(this.width, this.height);
		/* Set the title of the frame. */
		this.setTitle(title);
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
		gl.glDrawBuffer(GL.GL_BACK);
	}
	
	/**
	  * Method called multiple times for rendering the wave and point. Clears 
	  * the buffer and then draws a new wave and a point on the wave at an 
	  * angle of delta angle + previous angle, where delta angle is 5 degrees. 
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		/* Draw the wave. */
		this.drawWave(0, 0);
		
		/* Draw the point on the wave. */
		this.animatePointOnWave(0, 0);
		
		/* Pause the animation for 7ms. */
		try { Thread.sleep(10); }
		catch(Exception e){}
	}
	
	/** Conversion value from one degree to the corresponding radians. **/
	public static final double DEG_TO_RAD = (2 * Math.PI)/360.0;
	
	/**
	  * Draws a wave at a centered around (origin_x, origin_y) with a radius 
	  * given.
	  * @param origin_x X position of the origin of the wave.
	  * @param origin_y Y position of the origin of the wave.
	  * @param radius Radius of the cicle to be drawn.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public void drawWave(float origin_x, float origin_y)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(1);
		/* Set the drawing color to blue. */
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		
		/* Loop from 0 to 2*PI */
		for(float x = -this.width/2; x < 2 * this.width/2; x += 0.01)
		{
			float y = (float)((this.height/3.0)*Math.sin((( Math.PI *2 * x) / 
				(this.width)) + this.c));
			
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2f(x, y);
			gl.glEnd();
		}
		
		this.c+=0.05;
	}
	
	/**
	  * Draws a point at the current angle on the wave. Increases the angle 
	  * for the next animation by 0.25 degrees.
	  * @param origin_x X position of the origin of the wave.
	  * @param origin_y Y position of the origin of the wave.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public void animatePointOnWave(float origin_x, float origin_y)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(10);
		/* Set the drawing color to red. */
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		
		float x = 0;
		float y = (float)((this.height/3.0)*Math.sin((( Math.PI *2 * x) / 
				(this.width)) + this.c));
		
		/* Draw the point at the angle on the wave. */
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex2f(x, y);
		gl.glEnd();
	}
	
	/**
	  * Method called for OpenGL rendering every reshape. Adjusts the width and 
	  * height stored values. Calculate the radius of the wave. Centers the 
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
		
		this.c = 0;
		
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