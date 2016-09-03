import javax.media.opengl.*;
import com.sun.opengl.util.*;
import java.awt.event.*;
import java.util.*;

/**
  * HW03 of CS451 Computer Graphics: Draws geometric primitives bouncing in 
  * between a rectangle and a circle inside said rectangle. Use 2D 
  * transformations to achieve this task.
  * @author Zachary Ferguson
  */
public class HW3_zfergus2 extends HW2_zfergus2
{
	public static Transform2D trans2D;

	/**
	  * Main method for demoing the HW3_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  */
	public static void main(String[] args) 
	{	
		/* Create a HW3_zfergus2 frame. */
		HW3_zfergus2 hw = new HW3_zfergus2(800, 800, 
			"Zachary Ferguson - Primitives Bound");
		
		/* Display the hw frame */
		hw.setVisible(true);
	}
	
	/**
	  * Creates an instance of the HW3_zfergus2 class. Defaults: width = 800px, 
	  * height = 800px, title = "HW3_zfergus2".
	  */
	public HW3_zfergus2()
	{
		/* Call the full constructor with the default values. */
		this(800, 800, "HW3_zfergus2");
	}
	
	/**
	  * Creates an instance of the HW2_zfergus2 class given the width, 
	  * height, and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  */
	public HW3_zfergus2(int width, int height, String title)
	{
		/* Call the supers constructor. */
		super(width, height, title);
		
		trans2D = new Transform2D();
	}
	
	/**
	  * Method called every frame to draw the primitives and bounds. Also 
	  * animates and clips the primitives.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  */
	@Override
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
			this.rectangleWidth/2, this.rectangleHeight/2, this.rectColor);
		
		/* Label the points. */
		this.labelPoints();
		
		/* Pause the animation for 17ms. */
		try { Thread.sleep(17); }
		catch(Exception e){}
	}
	
	/**
	  * Draws the primitive shapes, point, line, triangle. Clips the shapes 
	  * outside the clipping area.
	  */
	@Override
	protected void drawPrimitives()
	{
		/* Set point size to five pixels. */
		gl.glPointSize(5);
		gl.glLineWidth(2);

		/* Array of primitives to be drawn. */
		int[] primitives = {GL.GL_POINTS, GL.GL_LINES, GL.GL_POLYGON};
		
		/* Origin Point */
		float[] origin = new float[]{0,0,0};
		
		/* Draw the primitives. */
		int p = 5;
		for(int i = primitives.length-1; i >= 0; i--)
		{		
			/* Set the color to corresponding colors. */
			gl.glColor3f(this.primitiveColors[i][0], this.primitiveColors[i][1], 
				this.primitiveColors[i][2]);

			gl.glBegin(primitives[i]);
			{
				for(int j = 0; j < i+1; j++, p--)
				{
					/* Push a new matrix on the matrix stack. */
					trans2D.my2dPushMatrix();
					
					/* Translate to the point, p. */
					trans2D.my2dTranslatef((float)this.points[p][0], 
						(float)this.points[p][1]);
					
					trans2D.transDraw(origin);

					trans2D.my2dPopMatrix();
				}
			}
			gl.glEnd();
		}
	}
	
	/** Labels the points v0 - v1 using bitmap characters. **/
	@Override
	protected void labelPoints()
	{
		/* Set label color. */
		gl.glColor3f(this.labelColor[0], this.labelColor[1], 
			this.labelColor[2]);
		
		/* Origin Point */
		float[] origin = new float[]{0,0,0};
		
		for(int i = 0; i < this.points.length; i++)
		{
			/* Push a new matrix on the matrix stack. */
			trans2D.my2dPushMatrix();
			
			/* Translate to the point, i. */
			trans2D.my2dTranslatef((float)this.points[i][0], 
				(float)this.points[i][1]);
				
			trans2D.transDrawString(origin, "V" + i, this.width, this.height);
			
			trans2D.my2dPopMatrix();
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
		
		trans2D.my2dLoadIdentity();
	}

	
/******************************************************************************/
/**************************  Transformation methods  **************************/
/*******************  J2_0_2DTransform + TransDraw methods.  ******************/
/******************************************************************************/
	
	
	/**
	  * Created on 2004-2-29
	  * @author Jim X. Chen: 2D transformation OpenGL style implementatoin
	  */
	public static class Transform2D
	{
		private static float my2dMatStack[][][] = new float[24][3][3];
		private static int stackPtr = 0;
		static float vdata[][] = { 
		{1.0f, 0.0f, 0.0f},
		{0.0f, 1.0f, 0.0f},
		{-1.0f, 0.0f, 0.0f},
		{0.0f, -1.0f, 0.0f}
		};
		static int cnt = 1;

		public Transform2D(){}
		
		/**
		  * Draws the point given after the transformation.
		  * @param v1 The position vector to be transformed and drawn at.
		  */
		public void transDraw(float[] v1)
		{
			float v[] = new float[3];
			
			my2dTransformf(v1, v);
			
			gl.glVertex3fv(v, 0);
		}
		
		/**
		  * Draws the point and string given after the transformation
		  * @param v1 The position vector to be transformed and drawn at.
		  * @param str The string to draw.
		  * @param width The width of the display area.
		  * @param height The height of the display area.
		  */
		public void transDrawString(float[] v1, String str, float width, 
			float height)
		{
			float v[] = new float[3];
			
			my2dTransformf(v1, v);
			
			/* Set the position equal to the point's position. */
			gl.glWindowPos3d(v[0] + width/2, v[1] + height/2, 0);
		
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, str);
		}
		
		// the vertices are transformed first then drawn
		public void transDrawTriangle(float[] v1, float[] v2, float[] v3)
		{
			float v[][] = new float[3][3];

			my2dTransformf(v1, v[0]);
			my2dTransformf(v2, v[1]);
			my2dTransformf(v3, v[2]);

			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3fv(v[0],0);
			gl.glVertex3fv(v[1],0);
			gl.glVertex3fv(v[2],0);
			gl.glEnd();
		}


		// initialize a 3*3 matrix to all zeros
		private void my2dClearMatrix(float mat[][])
		{
			for (int i = 0; i<3; i++) 
			{
				for (int j = 0; j<3; j++)
				{
					mat[i][j] = 0.0f;
				}
			}
		}


		// initialize a matrix to Identity matrix
		private void my2dIdentity(float mat[][]) {

			my2dClearMatrix(mat);
			for (int i = 0; i<3; i++) {
				mat[i][i] = 1.0f;
			}
		}


		// initialize the current matrix to Identity matrix
		public void my2dLoadIdentity() {
			my2dIdentity(my2dMatStack[stackPtr]);
		}


		// multiply the current matrix with mat
		public void my2dMultMatrix(float mat[][]) {
			float matTmp[][] = new float[3][3];

			my2dClearMatrix(matTmp);

			for (int i = 0; i<3; i++) {
				for (int j = 0; j<3; j++) {
					for (int k = 0; k<3; k++) {
						matTmp[i][j] +=
						my2dMatStack[stackPtr][i][k]*mat[k][j];
					}
				}
			}
			// save the result on the current matrix
			for (int i = 0; i<3; i++) {
				for (int j = 0; j<3; j++) {
					my2dMatStack[stackPtr][i][j] = matTmp[i][j];
				}
			}
		}


		// multiply the current matrix with a translation matrix
		public void my2dTranslatef(float x, float y) {
			float T[][] = new float[3][3];

			my2dIdentity(T);

			T[0][2] = x;
			T[1][2] = y;

			my2dMultMatrix(T);
		}


		// multiply the current matrix with a rotation matrix
		public void my2dRotatef(float angle) {
			float R[][] = new float[3][3];

			my2dIdentity(R);

			R[0][0] = (float)Math.cos(angle);
			R[0][1] = (float)-Math.sin(angle);
			R[1][0] = (float)Math.sin(angle);
			R[1][1] = (float)Math.cos(angle);

			my2dMultMatrix(R);
		}


		// multiply the current matrix with a scale matrix
		public void my2dScalef(float x, float y) {
			float S[][] = new float[3][3];

			my2dIdentity(S);

			S[0][0] = x;
			S[1][1] = y;

			my2dMultMatrix(S);
		}


		// v1 = (the current matrix) * v
		// here v and v1 are vertices in homogeneous coord.
		public void my2dTransHomoVertex(float v[], float v1[]) {
			int i, j;

			for (i = 0; i<3; i++) {
				v1[i] = 0.0f;
			}
			
			for (i = 0; i<3; i++) {
				for (j = 0; j<3; j++) {
					v1[i] +=
					my2dMatStack[stackPtr][i][j]*v[j];
				}
			}
		}


		// vertex = (the current matrix) * vertex
		// here vertex is in homogeneous coord.
		public void my2dTransHomoVertex(float vertex[]) {
			float vertex1[] = new float[3];

			my2dTransHomoVertex(vertex, vertex1);
			for (int i = 0; i<3; i++) {
				vertex[i] = vertex1[i];
			}
		}


		// transform v to v1 by the current matrix
		// here v and v1 are not in homogeneous coordinates
		public void my2dTransformf(float v[], float v1[]) {
			float vertex[] = new float[3];

			// extend to homogenius coord
			vertex[0] = v[0];
			vertex[1] = v[1];
			vertex[2] = 1;

			// multiply the vertex by the current matrix
			my2dTransHomoVertex(vertex);

			// return to 3D coord
			v1[0] = vertex[0]/vertex[2];
			v1[1] = vertex[1]/vertex[2];
		}


		// transform v by the current matrix
		// here v is not in homogeneous coordinates
		public void my2dTransformf(float[] v) {
			float vertex[] = new float[3];

			// extend to homogenius coord
			vertex[0] = v[0];
			vertex[1] = v[1];
			vertex[2] = 1;

			// multiply the vertex by the current matrix
			my2dTransHomoVertex(vertex);

			// return to 3D coord
			v[0] = vertex[0]/vertex[2];
			v[1] = vertex[1]/vertex[2];
		}


		// move the stack pointer up, and copy the previous
		// matrix to the current matrix
		public void my2dPushMatrix() {
			int tmp = stackPtr+1;

			for (int i = 0; i<3; i++) {
				for (int j = 0; j<3; j++) {
					my2dMatStack[tmp][i][j] =
					my2dMatStack[stackPtr][i][j];
				}
			}
			stackPtr++;
		}


		// move the stack pointer down
		public void my2dPopMatrix() {
			stackPtr--;
		}
	}
	
}