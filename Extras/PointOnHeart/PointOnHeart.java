import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 
import java.awt.event.*;

/**
  * HW01 of CS451 Computer Graphics: Draws a heart and a point that goes along 
  * a of the heart. 
  * @author Zachary Ferguson 
  **/
public class PointOnHeart extends J1_1_Point
{
	/** Width of this frame. **/
	protected int width; 
	/** Height of the frame. **/
	protected int height; 
	/** Radius of the heart to draw. **/
	protected float radius;
	/** The angle on the heart that the point is currently at. **/
	private float angleOnHeart;
	
	/**
	  * Main method for demoing the PointOnHeart class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		/* Create a PointOnHeart frame. */
		PointOnHeart hw01 = new PointOnHeart(800, 800, 
			"Zachary Ferguson - PointOnHeart");
		
		/* Display the hw frame */
		hw01.setVisible(true);
	}
	
	/**
	  * Creates an instance of the PointOnHeart class. Defaults: width = 
	  * 800px, height = 800px, title = "".
	  **/
	public PointOnHeart()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "");
	}
	
	/**
	  * Creates an instance of the PointOnHeart class given the width, 
	  * height, radius, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  **/
	public PointOnHeart(int width, int height, String title)
	{
		/* Call the supers constructor. */
		super();
		
		/* Set the fields. */
		this.width = width;
		this.height = height;
		
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.375f;
		
		/* Set the point origin to an angle of 0 radians. */
		this.angleOnHeart = 0;
		
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
	  * Method called once for rendering the heart and point. Clears the buffer
	  * and then draws a new heart and a point on the heart at an angle of 
	  * delta angle + previous angle, where delta angle is 5 degrees. 
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		/* Draw the heart. */
		PointOnHeart.drawHeart(0, this.height * 0.3125f, this.radius);
		// PointOnHeart.drawHeart(this.width/4f, 0, this.radius);
		
		/* Draw the point on the heart. */
		this.animatePointOnHeart(0, this.height * 0.3125f);
		// this.animatePointOnHeart(this.width/4f, 0);
		
		/* Pause the animation for 7ms. */
		try { Thread.sleep(7); }
		catch(Exception e){}
	}
	
	/** Conversion value from one degree to the corresponding radians. **/
	public static final double DEG_TO_RAD = (2 * Math.PI)/360.0;
	
	/**
	  * Draws a heart at a centered around (origin_x, origin_y) with a radius 
	  * given.
	  * @param origin_x X position of the origin of the heart.
	  * @param origin_y Y position of the origin of the heart.
	  * @param radius Radius of the cicle to be drawn.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public static void drawHeart(float origin_x, float origin_y, float radius)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(2);
		/* Set the drawing color to blue. */
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		
		/* Loop from 0 to 2*PI */
		for(float theta = 0; theta < 2 * Math.PI; theta += 0.01 * DEG_TO_RAD)
		{
			// float r = (float)(theta * radius/5);
			float r = (float)(1-Math.sin(theta)) * (radius);
			// float r = (float)(Math.sin(44*theta) - 2*Math.cos(theta)) * 
				// radius/1.5f;
			float x = (float)(origin_x + r * Math.cos(theta));
			float y = (float)(origin_y + r * Math.sin(theta));
			
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2f(x, y);
			gl.glEnd();
		}
	}
	
	/**
	  * Draws a point at the current angle on the heart. Increases the angle 
	  * for the next animation by 0.25 degrees.
	  * @param origin_x X position of the origin of the heart.
	  * @param origin_y Y position of the origin of the heart.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public void animatePointOnHeart(float origin_x, float origin_y)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(10);
		/* Set the drawing color to red. */
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		
		float r = (float)(1-Math.sin(this.angleOnHeart)) * (this.radius);
		// float r = (float)(Math.sin(44*this.angleOnHeart) - 
			// 2*Math.cos(this.angleOnHeart)) * radius/1.5f;
		float x = (float)(origin_x + r * Math.cos(this.angleOnHeart));
		float y = (float)(origin_y + r * Math.sin(this.angleOnHeart));
		
		/* Draw the point at the angle on the heart. */
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex2f(x, y);
		gl.glEnd();
		
		this.angleOnHeart += 0.25 * DEG_TO_RAD;
		this.angleOnHeart %= 2*Math.PI;
	}
	
	/**
	  * Method called for OpenGL rendering every reshape. Adjusts the width and 
	  * height stored values. Calculate the radius of the heart. Centers the 
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
		
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.375f;
		
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