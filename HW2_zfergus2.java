import javax.media.opengl.*;
import com.sun.opengl.util.*;
import java.awt.event.*;
import java.util.*;

/**
  * HW02 of CS451 Computer Graphics: Draws geometric primitives bouncing in 
  * between a rectangle and a circle inside said rectangle.
  * @author Zachary Ferguson
  */
public class HW2_zfergus2 extends HW1_zfergus2
{
	/** Static reference to the OpenGL Utilities. **/
	public final static GLUT glut = new GLUT();
	
	/** Width of the rectangle. **/
	protected float rectangleWidth;
	/** Height of the rectangle. **/
	protected float rectangleHeight;
	/** Width of the clipping area. **/
	protected float clipWidth;
	/** Height of the clipping area. **/
	protected float clipHeight;
	/** Array of points in the bounds. **/
	protected double[][] points;
	/** Directional vectors for the points to travel in. **/
	protected double[][] velocityVectors;
	
	/* Colors of this object. */
	/** Color of the clipping rectangle. **/
	protected float[] rectColor;
	/** Color of the clipped areas. **/
	protected float[] clippedColor;
	/** Color of the primitive shapes. **/
	protected float[][] primitiveColors;
	/** Color of the label text. **/
	protected float[] labelColor;

	/**
	  * Main method for demoing the HW2_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  */
	public static void main(String[] args) 
	{	
		/* Create a HW2_zfergus2 frame. */
		HW2_zfergus2 hw = new HW2_zfergus2(800, 800, 
			"Zachary Ferguson - Primitives Bound");
		
		/* Display the hw frame */
		hw.setVisible(true);
	}
	
	/**
	  * Creates an instance of the HW2_zfergus2 class. Defaults: width = 800px, 
	  * height = 800px, title = "HW2_zfergus2".
	  */
	public HW2_zfergus2()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "HW2_zfergus2");
	}
	
	/**
	  * Creates an instance of the HW2_zfergus2 class given the width, 
	  * height, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  */
	public HW2_zfergus2(int width, int height, String title)
	{
		/* Call the supers constructor. */
		super(width, height, title);
			
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.125f;
	
		/* Set the dimensions of the rectangle. */
		this.rectangleWidth = 0.90f * this.width;
		this.rectangleHeight = 0.90f * this.height;
		this.rectColor = new float[]{0, 1, 0};
		
		/* Set the dimensions of the clipping area. */
		this.clipWidth = 0.75f * this.rectangleWidth;
		this.clipHeight = 0.75f * this.rectangleHeight;
		
		/* Set the color of the clipped area. */
		this.clippedColor = new float[]{0.25f, 0.25f, 0.25f};
		
		/* Color of the primitives. */
		this.primitiveColors = new float[][]{{1, 0, 1},{1, 0.5f, 0},{1, 0, 0}};
		
		/* Color of the labels. */
		this.labelColor = new float[]{1, 1, 0};
		
		/* Randomly initialize the points and velocities. */
		this.initPoints();
	}
	
	/**
	  * Initialize the array of points and the array of velocity vectors to 
	  * random values.
	  */
	protected void initPoints()
	{
		/* Construct both of the arrays for random points and velocity */
		/* vector.                                                     */
		this.points = new double[6][];
		this.velocityVectors = new double[6][];
		for(int i = 0; i < this.points.length; i++)
		{
			/* Create a random point in the bounds. */
			this.points[i] = rand2DPosVector(this.rectangleWidth, 
				this.rectangleHeight, this.radius);
			
			/* Create a random velocity vector. */
			this.velocityVectors[i] = rand2DNormVector();
		}
	}
	
	/**
	  * Method called every frame to draw the primitives and bounds. Also 
	  * animates and clips the primitives.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  */
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		/* Draw the circle. */
		HW1_zfergus2.drawCircle(0, 0, this.radius);
		
		/* Draw the point on the circle. */
		this.animatePointsInCircle();
		
		/* Draw the outer rectangle. */
		drawRectangle(-this.rectangleWidth/2, -this.rectangleHeight/2, 
			this.rectangleWidth/2, this.rectangleHeight/2, this.clippedColor);
		/* Draw the clipping rectangle. */
		drawRectangle(-this.clipWidth/2, -this.clipHeight/2,
			this.clipWidth/2, this.clipHeight/2, this.rectColor);
		
		/* Label the points. */
		this.labelPoints();
		
		/* Pause the animation for 17ms. */
		try { Thread.sleep(17); }
		catch(Exception e){}
	}
	
	/**
	  * Draws an empty rectangle given the bottom left and upper right corner.
	  * @param xMin X position of the bottom left corner.
	  * @param yMin Y position of the bottom left corner.
	  * @param xMax X position of the upper right corner.
	  * @param yMax Y position of the upper right corner.
	  * @param color A float[] for the normalized color values of the rectangle.
	  */
	public static void drawRectangle(double xMin, double yMin, double xMax, 
		double yMax, float[] color)
	{
		/* Set the drawing color of the rectangle. */
		gl.glColor3f(color[0], color[1], color[2]);
		
		/* Set point size to one pixel. */
		gl.glLineWidth(2);
		
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(xMin, yMin);
			gl.glVertex2d(xMin, yMax);
			gl.glVertex2d(xMax, yMax);
			gl.glVertex2d(xMax, yMin);
		gl.glEnd();
	}
	
	/** 
	  * Animates the points in the bounds and calculates any bounces if 
	  * necessary.
	  */
	public void animatePointsInCircle()
	{
		this.drawPrimitives();
		
		this.movePoints();
	}
	
	/**
	  * Draws the primitive shapes, point, line, triangle. Clips the shapes 
	  * outside the clipping area.
	  */
	protected void drawPrimitives()
	{
		/* Set point size to five pixels. */
		gl.glPointSize(5);
		gl.glLineWidth(2);
		
		/* Array of primitives to be drawn. */
		int[] primitives = {GL.GL_POINTS, GL.GL_LINES, GL.GL_POLYGON};
		
		/* Set the color to grey. */
		gl.glColor3f(this.clippedColor[0], this.clippedColor[1], 
			this.clippedColor[2]);
		
		/* Draw the primitives. */
		int p = 5;
		for(int i = primitives.length-1; i >= 0; i--)
		{				
			gl.glBegin(primitives[i]);
				for(int j = 0; j < i+1; j++)
				{
					gl.glVertex2d(this.points[p][0], this.points[p--][1]);
				}
			gl.glEnd();
		}
		
		/* Define the clipping area for later use. */
		float xmin = -this.clipWidth/2,  xmax = this.clipWidth/2,
			  ymin = -this.clipHeight/2, ymax = this.clipHeight/2;
		/* Clip the primitives according to the clipping area. */
		double[][][] clippedPrims = new double[][][]{
			/* Clip the polygon. */
			SutherlandHodgmanPolygonClipping.clipPolygon(new double[][]{
				this.points[3], this.points[4], this.points[5]}, xmin, xmax, 
				ymin, ymax),
			/* Clip the line. */
			CohenSutherlandLineClipping.clipLine(copyVector(this.points[1]), 
				copyVector(this.points[2]), xmin, xmax, ymin, ymax),
			/* Clip the point. */
			(CohenSutherlandLineClipping.getOutCode(this.points[0][0], 
				this.points[0][1], xmin, xmax, ymin, ymax) != 
				CohenSutherlandLineClipping.INSIDE_OUTCODE) ? (null):(new 
				double[][]{this.points[0]})
		};
		
		/* Draw the clipped primitives. */
		int prim = 2;
		for(double[][] vertices : clippedPrims)
		{
			if(vertices != null)
			{
				/* Set the color of the clipped primitive. */
				gl.glColor3f(this.primitiveColors[prim][0], 
					this.primitiveColors[prim][1], 
					this.primitiveColors[prim][2]);
					
				gl.glBegin(primitives[prim]);
				for(double[] vertex : vertices)
				{
					gl.glVertex2d(vertex[0], vertex[1]);
				}
				gl.glEnd();
			}
			prim--;
		}
	}
	
	/**
	  * Moves the points one step according to there velocities, checking for 
	  * bounces along the way.
	  */
	protected void movePoints()
	{
		/* Iterate over the points checking for boundary collisions and then */
		/* moves the point one step.                                         */
		for(int i = 0; i < this.points.length; i++)
		{	
			this.collideWithBounds(i);
			this.movePoint(this.points[i], this.velocityVectors[i]);
		}
	}
	
	/**
	  * Check for a collision between the point and the bounds. If there is a 
	  * collision, bounce the point of the bound.
	  * @param pointIndex Index of the point and velocity vector.
	  */
	protected void collideWithBounds(int pointIndex)
	{
		/* Check for rectangle bound collision. */
		boolean isRectCollision = this.checkRectBound(this.points[pointIndex]);
		
		/* If there is a collision. */
		if(isRectCollision || this.checkCircleBound(this.points[pointIndex]))
		{
			/* Compute the backwards velocity for reflections. */
			double[] backwardsVelocity = scalarMultiplication(-1, 
				this.velocityVectors[pointIndex]);
			
			/* Obtain the vector to reflect about. */
			double[] reflectV = (isRectCollision) ? (this.getRectReflectVector(
				this.points[pointIndex])):(copyVector(this.points[pointIndex]));
				
			/* Normalize the reflect vector. */
			normalizeVector(reflectV);
			
			/* Reflect the velocity. */
			this.reflect(backwardsVelocity, reflectV, 
				this.velocityVectors[pointIndex]);
				
			/* Move the point inside the circle. */
			this.movePoint(this.points[pointIndex], backwardsVelocity);
		}
	}
	
	/**
	  * Check if the given point is colliding with the circle.
	  * @param distV A double[] for the distance vector of the point.
	  * @return Returns a boolean for if the point is colliding with the circle.
	  */
	protected boolean checkCircleBound(double[] distV)
	{
		/* Calculate the magnitude of distV. */
		double dist = this.magnitude(distV);
		
		return dist <= this.radius;
	}
	
	/**
	  * Check if the given point is colliding with the rectangle.
	  * @param pos A double[] for the positions vector.
	  * @return Returns a boolean for if the point is colliding with the 
	  * 	rectangle.
	  */
	protected boolean checkRectBound(double[] pos)
	{
		if(pos == null || pos.length < 2)
		{ 
			throw new IllegalArgumentException("Null or too short vector.");
		}

		double halfW = this.rectangleWidth/2;
		double halfH = this.rectangleHeight/2;
		
		return pos[0] >= halfW || pos[0] <= -halfW || pos[1] >= halfH || 
			pos[1] <= -halfH;
	}
	
	/**
	  * Computes a reflect vector based on the x,y position.
	  * @param pos A double[] for the positions vector.
	  * @return Returns a double[] for the reflect vector computed.
	  */
	protected double[] getRectReflectVector(double[] pos)
	{
		if(pos == null || pos.length < 2)
		{ 
			throw new IllegalArgumentException("Null or too short vector.");
		}
		
		double halfW = this.rectangleWidth/2;
		double halfH = this.rectangleHeight/2;
		
		/* Determine the reflect vector based on the x,y position. */
		return new double[]{(pos[0]>=halfW) ? (-1):((pos[0]<=-halfW) ? (1):(0)),
			(pos[1]>=halfH) ? (-1):((pos[1]<=-halfH) ? (1):(0)), 0};
	}
	
	/** Labels the points v0 - v1 using bitmap characters. **/
	protected void labelPoints()
	{
		/* Set label color. */
		gl.glColor3f(this.labelColor[0], this.labelColor[1], 
			this.labelColor[2]);
		
		for(int i = 0; i < this.points.length; i++)
		{
			/* Set the position equal to the point's position. */
			gl.glWindowPos3d(this.points[i][0] +  this.width/2, 
							 this.points[i][1] + this.height/2, 0);
		
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "V" + i);
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
		super.reshape(drawable, x, y, width, height);
		
		/* Set the radius as a fraction of  Min(width,height). */
		this.radius = ((this.width < this.height) ? (this.width):(this.height)) 
			* 0.25f;
		
		/* Set the dimensions of the rectangle. */
		this.rectangleWidth = 0.90f * this.width;
		this.rectangleHeight = 0.90f * this.height;
		
		/* Set the dimensions of the clipping area. */
		this.clipWidth = 0.75f * this.rectangleWidth;
		this.clipHeight = 0.75f * this.rectangleHeight;
		
		/* Reinitialize the points to be inside the bounds. */
		this.initPoints();
	}

/******************************************************************************/
/***********************  MORE LINEAR ALGEBRA METHODS  ************************/
/******************************************************************************/
	
	/**
	  * Multiples the given vector by the given constant. Returns the result.
	  * @param c A double for the scalar to multiply by.
	  * @param v A double[] for the vector to multiply.
	  * @return Returns a double[] for the resulting product, c*v;
	  */
	public static double[] scalarMultiplication(double c, double[] v)
	{
		double[] product = new double[v.length];
		for(int i = 0; i < v.length; i++)
		{
			product[i] = c * v[i];
		}
		return product;
	}
	
	/**
	  * Moves a point in the direction of the vector given. Moves the point in 
	  * place and does not return.
	  * @param point A double[] for the point to move.
	  * @param vector A double[] for the vector in the direction and length for 
	  * 	the point to move to.
	  */
	public static void movePoint(double[] point, double[] vector)
	{
		for(int i = 0; i < point.length && i < vector.length; i++)
		{
			point[i] += vector[i];
		}
	}
	
	/**
	  * Computes the magnitude of the given vector.
	  * @param v A double[] for the vector to compute the magnitude of.
	  * @return Returns a double for the magnitude of v.
	  */
	public static double magnitude(double[] v)
	{
		double sum = 0;
		for(int i = 0; i < v.length; i++)
		{
			sum += v[i] * v[i];
		}
		return Math.sqrt(sum);
	}
	
	/**
	  * Converts the given vector to a String representation.
	  * @param v A double[] for the vector to convert to a String.
	  * @return Returns a String representation of v.
	  */
	public static String vectorToString(double[] v)
	{
		if(v != null)
		{
			StringBuilder vectorStr = new StringBuilder();
			vectorStr.append("[");
			for(int i = 0; i < v.length; i++)
			{
				vectorStr.append(
					String.format("%s%g", i > 0 ? (", "):(""), v[i]));
			}
			vectorStr.append("]");
			return vectorStr.toString();
		}
		return "null";
	}
	
	/**
	  * Normalizes the given vector. Normalizes in place and does not return.
	  * @param vector A double[] for the vector to normalize.
	  * @throws IllegalArgumentException Throws an exception if vector is the 
	  * 	zero vector.
	  */
	public static void normalizeVector(double[] vector)
	{
		double magnitude = magnitude(vector);
		if(magnitude == 0)
		{
			throw new IllegalArgumentException("0 length vector: normalize().");
		}
		
		/* Divide by the magnitude. */
		for(int i = 0; i < vector.length; i++)
		{
			vector[i] = vector[i] / magnitude;
		}
	}
	
	/**
	  * Creates and returns a deep copy of the given vector.
	  * @param v A double[] for the vector to copy.
	  * @return Returns a deep copy of the given double[];
	  */
	public static double[] copyVector(double[] v)
	{
		if(v == null) {	return null; }
		
		double[] copy = new double[v.length];
		for(int i = 0; i < v.length; i++)
		{
			copy[i] = v[i];
		}
		return copy;
	}
	
	/**
	  * Creates and returns a deep copy of the given array of vectors.
	  * @param v A double[][] for the array of vector to copy.
	  * @return Returns a deep copy of the given double[][];
	  */
	public static double[][] copyVectors(double[][] v)
	{
		if(v == null) {	return null; }
		
		double[][] copy = new double[v.length][];
		for(int i = 0; i < v.length; i++)
		{
			copy[i] = copyVector(v[i]);
		}
		return copy;
	}
	
	/**
	  * Creates a random position vector between a rectangle of width and height
	  * given and a circle with the given radius all centered at (0,0).
	  * @param w The width of the rectangle bound.
	  * @param h The height of the rectangle bound.
	  * @param r The radius of the circular bound.
	  * @return Returns a double[] for the random position vector between the 
	  *	circle and rectangle.
	  */
	public static double[] rand2DPosVector(double w, double h, double r)
	{
		/* Generate a random y in the range [-h/2, h/2). */
		double y = (h * Math.random()) - (h/2);
		
		/* Randomly determine if the x will be positive or negative. */
		int xSign = Math.random() >= 0.5f ? (1):(-1);
		/* Compute x position on the circle given the y above. (i.e. x min) */
		double xOnCir = r * Math.cos(Math.asin(Math.abs(y) <= r ? (y/r):(1)));
		/* Compute a random x. */
		double x = xSign * ((w/2 - xOnCir) * Math.random() + xOnCir);

		return new double[]{ x, y, 0 };
	}
	
	/**
	  * Creates a random normalized vector.
	  * @return Returns a double[] for the random normalized vector.
	  */
	public static double[] rand2DNormVector()
	{
		/* Compute a random vector with values in the range [-1, 0)U(0, 1] */
		double[] v = {0,0,0};
		/* Is the new velocity vector the zero vector. */
		boolean isZero = true;
		
		/* Loop until the vector is not zero. */
		do
		{
			try
			{
				v = new double[]{(Math.random()*2)-1, (Math.random()*2)-1, 0};
				/* Normalize the vector. */ 
				normalizeVector(v);
				isZero = false;
			}
			/* If the vector is the zero vector. */
			catch(Exception e)
			{
				isZero = true;
			}
		}
		while(isZero);
		
		return v;
	}

/******************************************************************************/
/***************************** Clipping ALgorithms ****************************/
/******************************************************************************/
	
/** Implementation of the Cohen Sutherland Line Clipping algorithm, algorithm
  * for clipping a line to be inside a clipping area.
  * @author Zachary Ferguson
  */
public static class CohenSutherlandLineClipping
{
	/* Out codes for TBRL. */
	/** Out code for inside the clipping area. **/
	public static final byte INSIDE_OUTCODE = 0b0000;
	/** Out code for left of the clipping area. **/
	public static final byte LEFT_OUTCODE = 0b0001;
	/** Out code for right of the clipping area. **/
	public static final byte RIGHT_OUTCODE = 0b0010;
	/** Out code for below the clipping area. **/
	public static final byte BOTTOM_OUTCODE = 0b0100;
	/** Out code for above the clipping area. **/
	public static final byte TOP_OUTCODE = 0b1000;
	
	/**
	  * Computes the out code for the given point. Used to determine position of 
	  * the point in the Cohen Sutherland Line Clipping algorithm.
	  * @param x The x position of the point.
	  * @param y The y position of the point.
	  * @param xmin The minimum x position of the clipping area.
	  * @param xmax The maximum x position of the clipping area.
	  * @param ymin The minimum y position of the clipping area.
	  * @param ymax The maximum y position of the clipping area.
	  * @return Returns a byte for the out code of the given point.
	  */
	public static byte getOutCode(double x, double y, double xmin, 
		double xmax, double ymin, double ymax)
	{
		/* Assume the code is inside. */
		byte code = INSIDE_OUTCODE;
		
		/* Check for the x's position. */
		code |= (x < xmin) ? (LEFT_OUTCODE):((x > xmax) ? (RIGHT_OUTCODE):(0));
		/* Check for the y's position. */
		code |= (y < ymin) ? (BOTTOM_OUTCODE):((y > ymax) ? (TOP_OUTCODE):(0));
		
		return code;
	}
	
	/**
	  * Clip the line given the end points and the clipping area, and return 
	  * this clipped line.
	  * @param v0 A double[] for the first endpoint's position.
	  * @param v1 A double[] for the second endpoint's position.
	  * @param xmin The minimum x position of the clipping area.
	  * @param xmax The maximum x position of the clipping area.
	  * @param ymin The minimum y position of the clipping area.
	  * @param ymax The maximum y position of the clipping area.
	  * @return Returns a double[][] for the clipped line. Returns null if 
	  * 	outside the clipping area.
	  */
	public static double[][] clipLine(double v0[], double v1[], double xmin, 
		double xmax, double ymin, double ymax)
	{
		/* Ensure the vertices are valid. */
		if(v0 == null || v1 == null || v0.length < 2 || v1.length < 2 )
		{
			throw new IllegalArgumentException("Invalid line.");
		}
		
		/* Compute the out-codes of the points. */
		byte outcode0 = getOutCode(v0[0], v0[1], xmin, xmax, ymin, ymax);
		byte outcode1 = getOutCode(v1[0], v1[1], xmin, xmax, ymin, ymax);
		
		/* Loop until the line is inside the clipping area. */
		/* Clip the line if necessary.                      */
		while((outcode0 | outcode1) != INSIDE_OUTCODE)
		{
			/* The line is trivially outside of the clipping area. */
			if((outcode0 & outcode1) != INSIDE_OUTCODE)
			{
				return null;
			}
			else
			{
				double x = 0, y = 0, t;
				/* Select the vertex which is outside. */
				byte outsideOutcode = outcode0 != INSIDE_OUTCODE ? 
					(outcode0):(outcode1);
				
				/*************************************************************/
				/* Determine where the selected vertex is and move it to the */
				/* point of intersection.                                    */
				/*************************************************************/
				/* If the selected vertex is above the clipping area. */
				if((outsideOutcode & TOP_OUTCODE) != INSIDE_OUTCODE)
				{
					t = (ymax - v0[1]) / (v1[1] - v0[1]);
					x = v0[0] + t * (v1[0] - v0[0]);
					y = ymax;
				}
				/* If the selected vertex is below the clipping area. */
				else if((outsideOutcode & BOTTOM_OUTCODE) != INSIDE_OUTCODE)
				{
					t = (ymin - v0[1]) / (v1[1] - v0[1]);
					x = v0[0] + t * (v1[0] - v0[0]);
					y = ymin;
				}
				/* If the selected vertex is right of the clipping area. */
				else if((outsideOutcode & RIGHT_OUTCODE) != INSIDE_OUTCODE)
				{
					t = (xmax - v0[0]) / (v1[0] - v0[0]);
					y = v0[1] + t * (v1[1] - v0[1]);
					x = xmax;
				}
				/* If the selected vertex is left of the clipping area. */
				else if((outsideOutcode & LEFT_OUTCODE) != INSIDE_OUTCODE)
				{
					t = (xmin - v0[0]) / (v1[0] - v0[0]);
					y = v0[1] + t * (v1[1] - v0[1]);
					x = xmin;
				}
				
				/* Move the vertex to the calculated point of intersection. */
				/* If the first vertex was selected. */
				if(outsideOutcode == outcode0)
				{
					v0[0] = x;
					v0[1] = y;
					/* Recompute the out code for the vertex. */
					outcode0 = getOutCode(v0[0], v0[1], xmin, xmax, ymin, 
						ymax);
				}
				/* If the second vertex was selected. */
				else
				{
					v1[0] = x;
					v1[1] = y;
					/* Recompute the out code for the vertex. */
					outcode1 = getOutCode(v1[0], v1[1], xmin, xmax, ymin, 
						ymax);
				}
			}
		}
		
		/* Return the new clipped line. */
		return new double[][]{v0, v1};
	}
}

/** 
  * Implementation of the Sutherland Hodgman Polygon-Clipping Algorithm, 
  * algorithm for clipping a polygon to be inside a clipping area.
  * @author Zachary Ferguson
  */
public static class SutherlandHodgmanPolygonClipping
{
	/** Enumeration of the edges to clip. **/
	public static enum ClippingEdge { LEFT, RIGHT, TOP, BOTTOM };
   
	/**
	  * Clips the polygon given the vertices and the clipping area.
	  * @param polygon A double[][] for the vertices of the polygon.
	  * @param xmin The minimum x position of the clipping area.
	  * @param xmax The maximum x position of the clipping area.
	  * @param ymin The minimum y position of the clipping area.
	  * @param ymax The maximum y position of the clipping area.
	  * @return Returns a double[][] for the clipped polygon's vertices.
	  */
	public static double[][] clipPolygon(double[][] polygon, float xmin, 
		float xmax, float ymin, float ymax)
	{
		/* Create ArrayLists to store the vertices in. */
		ArrayList<double[]> polygonAL = new ArrayList<double[]>(
			Arrays.asList(polygon));
		ArrayList<double[]> clippedPolygon = new ArrayList<double[]>();
		/* Create an array of the clipping bounds. */
		float[] edgeVals = new float[]{xmin, xmax, ymin, ymax};
		
		/* Loop over all the clipping edges. */
		int e = 0;
		for(ClippingEdge edge : ClippingEdge.values())
		{
			/* Loop through the current polygon's vertices. */
			for(int i = 0; i < polygonAL.size(); i++)
			{
				/* Get the start and end vertices of the polygon's ith edge. */
				double[] v0 = polygonAL.get(i);
				double[] v1 = polygonAL.get((i+1)%polygonAL.size());
				
				boolean v0Inside = isInside(v0, edgeVals[e], edge);
				boolean v1Inside = isInside(v1, edgeVals[e], edge);
				
				/* If inside to outside or outside to inside, */
				if((v0Inside && !v1Inside) || (!v0Inside && v1Inside))
				{
					/* add the point of intersection with the edge. */
					clippedPolygon.add(getIntersection(v0, v1, edgeVals[e], 
						edge));
				}
				/* If inside to inside or outside to inside, */
				if((v0Inside && v1Inside) || (!v0Inside && v1Inside))
				{
					/* add the point endpoint of the edge. */
					clippedPolygon.add(v1);
				}
			}
			/* Store the clippedPolygon in the polygonAL. */
			polygonAL = clippedPolygon;
			clippedPolygon = new ArrayList<double[]>();
			e++;
		}
		
		/* Return a double[][] for the clipped polygon's vertices. */
		return polygonAL.toArray(new double[0][0]);
	}
	
	/**
	  * Determines if the given point is inside the specified edge.
	  * @param v A double[] for the position vector of the point.
	  * @param edgeVal A float for the value of the edge to compare with.
	  * @param clippingEdge The ClippingEdge for the edge to compare with.
	  * @return Returns a boolean for if the point is inside the edge.
	  */
	public static boolean isInside(double[] v, float edgeVal, 
		ClippingEdge clippingEdge)
	{
		switch(clippingEdge)
		{
			case LEFT:   return v[0] > edgeVal;
			case RIGHT:  return v[0] < edgeVal;
			case TOP:    return v[1] > edgeVal;
  			case BOTTOM: return v[1] < edgeVal;
			default:     return false;
		}
	}
	
	/**
	  * Computes the intersection of the line given and the edge specified.
	  * @param v0 A double[] for the position of the line's starting point.
	  * @param v1 A double[] for the position of the line's ending point.
	  * @param edgeVal A float for the value of the edge to intersect with.
	  * @param clippingEdge The ClippingEdge for the edge to intersect with.
	  * @return Returns a double[] for the point of intersection.
	  */
	public static double[] getIntersection(double[] v0, double[] v1, 
		float edgeVal, ClippingEdge clippingEdge)
	{
		double t, x = 0, y = 0;
		switch(clippingEdge)
		{
			/* Intersecting with the left or right edge. */
			case LEFT: case RIGHT:
				t = (edgeVal - v0[0]) / (v1[0] - v0[0]);
				y = v0[1] + t * (v1[1] - v0[1]);
				x = edgeVal;
				break;
				
			/* Intersecting with the top or bottom edge. */
			case TOP: case BOTTOM:
				t = (edgeVal - v0[1]) / (v1[1] - v0[1]);
				x = v0[0] + t * (v1[0] - v0[0]);
				y = edgeVal;
				break;
		}
		
		/* Create the position vector of the intersection. */
		return new double[]{x, y, 0};
	}
}
}