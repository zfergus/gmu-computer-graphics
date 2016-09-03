import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 
import java.awt.event.*;

/**
  * Draws points in side a circle that bounce around. 
  * @author Zachary Ferguson 
  **/
public class PointsInCircle extends J1_1_Point
{
	/** Width of this frame. **/
	protected int width; 
	/** Height of the frame. **/
	protected int height; 
	/** Radius of the circle to draw. **/
	protected double radius;
	/** Array of points in the circle **/
	protected double[][] points;
	/* Directional vectors for the points to travel in. */
	protected double[][] velocityVectors;
	
	/**
	  * Main method for demoing the PointsInCircle class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		int num_points = 10;
		if(args.length > 0)
		{
			num_points = Integer.parseInt(args[0]);
		}
		
		/* Create a PointsInCircle frame. */
		PointsInCircle pInC = new PointsInCircle(800, 800, 
			"Zachary Ferguson - PointsInCircle", num_points);
		
		/* Display the pInC frame */
		pInC.setVisible(true);
	}
	
	/**
	  * Creates an instance of the PointsInCircle class. Defaults: width = 
	  * 800px, height = 800px, title = "", numPoints = 1.
	  **/
	public PointsInCircle()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "PointsInCircle", 1);
	}
	
	/**
	  * Creates an instance of the PointsInCircle class with numPoints = given. 
	  * Other defaults: width = 800px, height = 800px, title = "".
	  * @param numPoints The number of points in the circle.
	  **/
	public PointsInCircle(int numPoints)
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "PointsInCircle", numPoints);
	}
	
	/**
	  * Creates an instance of the PointsInCircle class given the width, 
	  * height, radius, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  * @param numPoints The number of points in the circle.
	  **/
	public PointsInCircle(int width, int height, String title, int numPoints)
	{
		/* Call the supers constructor. */
		super();
		
		/* Set the fields. */
		this.width = width;
		this.height = height;
		
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.375f;
		
		this.initPoints(numPoints);
		
		/* Set the size of the frame. */
		this.setSize(this.width, this.height);
		/* Set the title of the frame. */
		this.setTitle(title);
	}
	
	/**
	  * Initialize the array of points and the array of velocity vectors to 
	  * random values.
	  * @param numPoints The number of points to create.
	  **/
	protected void initPoints(int numPoints)
	{
		/* Construct an array of random points. */
		this.points = new double[numPoints][];
		this.velocityVectors = new double[numPoints][];
		for(int p = 0; p < numPoints; p++)
		{
			/* Compute a random point with values in the range [-1,1) */
			this.points[p] = new double[]{
				(Math.random()*2)-1, 
				(Math.random()*2)-1, 0};
			/* Normalize the points position. */
			this.normalize(this.points[p]);
			/* Multiply the point by the radius making a fraction of the */
			/* radius.                                                   */
			this.points[p] = scalarMultiplication(this.radius * Math.random(), 
				this.points[p]);
			
			/* Compute a random vector with values in the range [-1,1) */
			this.velocityVectors[p] = new double[]{
				(Math.random()*2)-1, 
				(Math.random()*2)-1, 0};
			/* Normalize the vector. */ 
			this.normalize(this.velocityVectors[p]);
		}
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
		
		/* Draw the point on the circle. */
		this.animatePointsInCircle(0, 0);
		
		/* Draw the circle. */
		PointsInCircle.drawCircle(0, 0, this.radius);
		
		/* Pause the animation for 7ms. */
		try { Thread.sleep(5); }
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
	  **/
	public static void drawCircle(double origin_x, double origin_y, 
		double radius)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(2);
		/* Set the drawing color to blue. */
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		
		/* Loop from 0 to 2*PI */
		gl.glBegin(GL.GL_POINTS);
		for(double theta = 0; theta < 2 * Math.PI; theta += 1/radius * 
			DEG_TO_RAD)
		{
			double x = origin_x + radius * Math.cos(theta);
			double y = origin_y + radius * Math.sin(theta);
			
			gl.glVertex2d(x, y);
		}
		gl.glEnd();
	}
	
	/**
	  * Animated the points in the circle and calculates any bounces if 
	  * necessary.
	  * @param origin_x X position of the origin of the circle.
	  * @param origin_y Y position of the origin of the circle.
	  **/
	public void animatePointsInCircle(double origin_x, double origin_y)
	{
		/* Set point size to one pixel. */
		gl.glPointSize(3);
		/* Set the drawing color to red. */
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		
		gl.glBegin(GL.GL_POINTS);
		/* Draw the center point. */
		// gl.glVertex2d(origin_x, origin_x);
		/* Draw the point in the circle. */
		for(int i = 0; i < this.points.length; i++)
		{	
			/* Calculate the vector from the origin to the point. */
			double[] distV = new double[]{this.points[i][0] - origin_x,
				this.points[i][1]-origin_y, this.points[i][2]};
			/* Calculate the magnitude of distV. */
			double dist = this.magnitude(distV);
			
			if(dist >= radius)
			{
				double[] backwardsVelocity = scalarMultiplication(-1, 
					this.velocityVectors[i]);
				/* Move the point inside the circle. */
				this.movePoint(this.points[i], backwardsVelocity);
				/* Normalize the delta vector. */
				this.normalize(distV);
				/* Reflect the velocity. */
				this.reflect(backwardsVelocity, distV, 
					this.velocityVectors[i]);
			}
			
			/* Draw the point in the circle. */
			gl.glVertex2d(this.points[i][0], this.points[i][1]);
			
			this.movePoint(this.points[i], this.velocityVectors[i]);
		}
		gl.glEnd();
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
	  **/
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, 
		int height)
	{
		/* Update the dimensions. */
		this.width = width;
		this.height = height;
		
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.375f;
		
		this.initPoints(this.points.length);
		
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
	
	
	/**********************************/
	/** More linear algebra methods. **/
	/**********************************/
	
	public static double[] scalarMultiplication(double c, double[] v)
	{
		double[] newV = new double[v.length];
		for(int i = 0; i < v.length; i++)
		{
			newV[i] = c * v[i];
		}
		return newV;
	}
	
	public static void movePoint(double[] point, double[] velocityVector)
	{
		for(int i = 0; i < point.length && i < velocityVector.length; i++)
		{
			point[i] += velocityVector[i];
		}
	}
	
	public static double magnitude(double[] v)
	{
		double sum = 0;
		for(int i = 0; i < v.length; i++)
		{
			sum += v[i] * v[i];
		}
		return Math.sqrt(sum);
	}
	
	public static void printVector(double[] v)
	{
		System.out.print("[");
		for(int i = 0; i < v.length; i++)
		{
			System.out.printf("%s%g", i > 0 ? (", "):(""), v[i]);
		}
		System.out.print("]");
	}
	
	public static void printlnVector(double[] v)
	{
		printVector(v);
		System.out.println();
	}
}