import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 
import java.awt.event.*;

/**
  * HW01 of CS451 Computer Graphics: Draws a circle and a point that goes along 
  * a of the circle. 
  * @author Zachary Ferguson 
  **/
public class HW1_zfergus2 extends J1_1_Point
{
	/** Width of this frame. **/
	protected int width; 
	/** Height of the frame. **/
	protected int height; 
	/** Radius of the circle to draw. **/
	protected float radius;
	/** The angle on the circle that the point is currently at. **/
	private float angleOnCircle;
	
	/**
	  * Main method for demoing the HW1_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		/* Create a HW1_zfergus2 frame. */
		HW1_zfergus2 hw01 = new HW1_zfergus2(800, 800, 
			"Zachary Ferguson - HW1_zfergus2");
		
		/* Display the hw frame */
		hw01.setVisible(true);
	}
	
	/**
	  * Creates an instance of the HW1_zfergus2 class. Defaults: width = 800px, 
	  * height = 800px, title = "".
	  **/
	public HW1_zfergus2()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "HW1_zfergus2");
	}
	
	/**
	  * Creates an instance of the HW1_zfergus2 class given the width, 
	  * height, radius, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  **/
	public HW1_zfergus2(int width, int height, String title)
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
		this.angleOnCircle = 0;
		
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
	  * Method called once for rendering the circle and point. Clears the buffer
	  * and then draws a new circle and a point on the circle at an angle of 
	  * delta angle + previous angle, where delta angle is 5 degrees. 
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		/* Draw the circle. */
		HW1_zfergus2.drawCircle(0, 0, this.radius);
		
		/* Draw the point on the circle. */
		this.animatePointOnCircle(0, 0);
		
		/* Pause the animation for 7ms. */
		try { Thread.sleep(7); }
		catch(Exception e){}
	}
	
	/** Conversion value from one degree to the corresponding radians. **/
	public static final double DEG_TO_RAD = (2 * Math.PI)/360.0;
	
	/**
	  * Draws a circle at a centered around (origin_x, origin_y) with a radius 
	  * given.
	  * @param origin_x X position of the origin of the circle.
	  * @param origin_y Y position of the origin of the circle.
	  * @param radius Radius of the cicle to be drawn.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public static void drawCircle(float origin_x, float origin_y, float radius)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(2);
		/* Set the drawing color to blue. */
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		
		/* Loop from 0 to PI/4 */
		for(float theta = 0; theta < Math.PI/4.0; theta += (1/radius))
		{
			float x = (float)(origin_x + radius * Math.cos(theta));
			float y = (float)(origin_y + radius * Math.sin(theta));
			
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2f( x,  y);
				gl.glVertex2f(-x,  y);
				gl.glVertex2f( x, -y);
				gl.glVertex2f(-x, -y);
				gl.glVertex2f( y,  x);
				gl.glVertex2f(-y,  x);
				gl.glVertex2f( y, -x);
				gl.glVertex2f(-y, -x);
			gl.glEnd();
		}
	}
	
	/**
	  * Draws a point at the current angle on the circle. Increases the angle 
	  * for the next animation by 0.25 degrees.
	  * @param origin_x X position of the origin of the circle.
	  * @param origin_y Y position of the origin of the circle.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a> 
	  */
	public void animatePointOnCircle(float origin_x, float origin_y)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(10);
		/* Set the drawing color to red. */
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		
		float x = (float)(origin_x + radius * Math.cos(this.angleOnCircle));
		float y = (float)(origin_y + radius * Math.sin(this.angleOnCircle));
		
		/* Draw the point at the angle on the circle. */
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex2f(x, y);
		gl.glEnd();
		
		this.angleOnCircle += 1/this.radius;
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