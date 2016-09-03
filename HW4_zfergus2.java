import javax.media.opengl.*;
import com.sun.opengl.util.*;
import java.awt.event.*;
import java.util.*;

/**
  * HW04 of CS451 Computer Graphics: Draws 3d objects bouncing in a box and off 
  * one another.
  * @author Zachary Ferguson
  */
public class HW4_zfergus2 extends HW3_zfergus2
{
	/* Transformation values */
	protected float    boxScale,    sphereScale,    tetraScale,    lineScale;
	protected float      boxRot,      sphereRot,      tetraRot,      lineRot;
	protected float              sphereRotDelta, tetraRotDelta, lineRotDelta;
	protected Vector3               sphereTrans,    tetraTrans,    lineTrans;
	protected Vector3                 sphereVel,      tetraVel,      lineVel;
	protected float[]    boxMat,      sphereMat,      tetraMat,      lineMat;
	
	public static final double EPSILON = 0.000001; 
	
	/**
	  * Main method for demoing the HW4_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		/* Create a HW4_zfergus2 frame. */
		HW4_zfergus2 hw04 = new HW4_zfergus2(800, 800, "Zachary Ferguson - HW4"+
			"_zfergus2");
      
		/* Display the hw frame */
		hw04.setVisible(true);
	}
	
	/**
	  * Creates an instance of the HW4_zfergus2 class given the width, height, 
	  * and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  */
	public HW4_zfergus2(int width, int height, String title)
	{
		super(width, height, title);
		
		/* Initialize the Sphere. */
		this.boxScale = 1.125f * this.width;
		this.boxRot   = 0;
		this.boxMat = new float[16];
		
		/* Initialize the Sphere. */
		this.sphereScale = this.width/8f;
		this.sphereRot = 0; this.sphereRotDelta = 1;
		this.sphereTrans = Vector3.randomVectorInCube(this.boxScale - 
			2*this.sphereScale);
		this.sphereVel = Vector3.scalarMult(10, Vector3.randomUnitVector());
		this.sphereMat = new float[16];
		
		/* Initialize the Tetrahedron. */
		this.tetraScale = this.height/4f;
		this.tetraRot = 0; this.tetraRotDelta = 1;
		this.tetraTrans = Vector3.randomVectorInCube(this.boxScale - 
			2*this.tetraScale);
		this.tetraVel = Vector3.scalarMult(10, Vector3.randomUnitVector());
		this.tetraMat = new float[16];
		
		/* Initialize the Line. */
		this.lineScale = this.height/4f;
		this.lineRot = 0; this.lineRotDelta = 1;
		this.lineTrans = Vector3.randomVectorInCube(this.boxScale - 
			2*this.lineScale);
		this.lineVel = Vector3.scalarMult(10, Vector3.randomUnitVector());
		this.lineMat = new float[16];
	}
	
	/**
	  * Method called once for initializing the frame. Enables culling and 
	  * z-buffer.
	  * @param drawable Needed, but unused parameter.
	  **/
	public void init(GLAutoDrawable drawable)
	{
		super.init(drawable);
		
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		gl.glLineWidth(2);
	}
	
	protected boolean altOrder = true;
	
	/**
	  * Method called every frame to draw out the objects and computes 
	  * collisions.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{		
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glPushMatrix();
			/* Rotate the box and the objects together. */
			gl.glRotatef(this.boxRot++, 0, 1, 0);
			
			/* Draw the box. */
			this.drawBox();
			/* Draw the sphere. */
			this.drawSphere();
			/* Draw the tetrahedron. */
			this.drawTetrahedron();
			/* Draw the line. */
			this.drawLine();
		gl.glPopMatrix();
		
		
		if(altOrder)
		{
			/* Collision with bounds. */
			boxCollisions();
			/* Collision between objects. */
			objectCollisions();
		}
		else
		{
			/* Collision between objects. */
			objectCollisions();
			/* Collision with bounds. */
			boxCollisions();
		}
		
		altOrder = !altOrder;
		
		/* Pause the animation for 17ms. */
		try { Thread.sleep(17); }
		catch(Exception e){}
	}
	
	/** Draw the box. */
	protected void drawBox()
	{
		gl.glPushMatrix();
			gl.glColor3f(1, 0.5f, 0);	
			gl.glScalef(this.boxScale, this.boxScale, this.boxScale);
			gl.glTranslatef(-0.5f,-0.5f,-0.5f); /* Translate to origin. */
			
			/* Save the transformation matrix. */
			this.boxMat = new float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, this.boxMat, 0);
			
			drawUnitCube();
		gl.glPopMatrix();
	}
	
	/** Draw the sphere. */
	protected void drawSphere()
	{
		this.sphereTrans = Vector3.addVectors(this.sphereTrans, this.sphereVel);
		this.sphereRot += this.sphereRotDelta;
		gl.glPushMatrix();
			gl.glTranslatef(this.sphereTrans.x, this.sphereTrans.y, 
				this.sphereTrans.z);
			gl.glRotatef(this.sphereRot, 1, 1, 1);
			gl.glScalef(this.sphereScale, this.sphereScale, this.sphereScale);
			
			/* Save the transformation matrix. */
			this.sphereMat = new  float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, this.sphereMat, 0);
			
			drawUnitSphere(6);
		gl.glPopMatrix();
	}
	
	/** Draw the tetrahedron. */
	protected void drawTetrahedron()
	{
		this.tetraTrans = Vector3.addVectors(this.tetraTrans, this.tetraVel);
		this.tetraRot += this.tetraRotDelta;
		gl.glPushMatrix();
			gl.glTranslatef(this.tetraTrans.x, this.tetraTrans.y, 
				this.tetraTrans.z);
 			gl.glRotatef(this.tetraRot, 0, 1, 1);
			gl.glScalef(this.tetraScale, this.tetraScale, this.tetraScale);
			gl.glTranslatef(-0.5f,-0.5f,-0.5f); /* Translate to origin. */
			
			/* Save the transformation matrix. */
			this.tetraMat = new  float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, this.tetraMat, 0);
			
			drawUnitTetrahedron();
		gl.glPopMatrix();
	}
	
	/** Draw the line. */
	protected void drawLine()
	{
		this.lineTrans = Vector3.addVectors(this.lineTrans, this.lineVel);
		this.lineRot += this.lineRotDelta;
		gl.glPushMatrix();
			gl.glTranslatef(this.lineTrans.x, this.lineTrans.y, 
				this.lineTrans.z);
			gl.glRotatef(this.lineRot, 0, 1, 1);
			gl.glScalef(this.lineScale, this.lineScale, this.lineScale);
			
			/* Save the transformation matrix. */
			this.lineMat = new  float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, this.lineMat, 0);
			
			gl.glColor3f(0.5f,1,0);
			gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0,-0.5f,0);
				gl.glVertex3f(0,0.5f,0);
			gl.glEnd();
		gl.glPopMatrix();
	}
	
	/** Draws the unit cube with the diagonal from (0,0,0) to (1,1,1) **/
	public static void drawUnitCube()
	{
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0,0,0); gl.glVertex3f(1,0,0); gl.glVertex3f(1,1,0);
			gl.glVertex3f(0,1,0);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0,0,1); gl.glVertex3f(1,0,1); gl.glVertex3f(1,1,1);
			gl.glVertex3f(0,1,1);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINES);
			for(int x = 0; x <= 1; x++)
			{
				for(int y = 0; y<= 1; y++)
				{
					for(int z = 0; z <= 1; z++)
					{
						gl.glVertex3f(x,y,z);
					}
				}
			}
		gl.glEnd();
	}
	
	/** Vertices of a Unit Octahedron. **/
	public static final float sVdata[][] = { 
		{ 1.0f,  0.0f,  0.0f}, { 0.0f,  1.0f,  0.0f}, { 0.0f,  0.0f,  1.0f},
		{-1.0f,  0.0f,  0.0f}, { 0.0f, -1.0f,  0.0f}, { 0.0f,  0.0f, -1.0f}
	};
	
	/** Draw the radius 1 sphere at the origin. */
	public void drawUnitSphere(int depth)
	{
		subdivideSphere(sVdata[0], sVdata[1], sVdata[2], depth);
		subdivideSphere(sVdata[0], sVdata[2], sVdata[4], depth);
		subdivideSphere(sVdata[0], sVdata[4], sVdata[5], depth);
		subdivideSphere(sVdata[0], sVdata[5], sVdata[1], depth);

		subdivideSphere(sVdata[3], sVdata[1], sVdata[5], depth);
		subdivideSphere(sVdata[3], sVdata[5], sVdata[4], depth);
		subdivideSphere(sVdata[3], sVdata[4], sVdata[2], depth);
		subdivideSphere(sVdata[3], sVdata[2], sVdata[1], depth);
	}
	
	/* Draw face of sphere. */
	protected void subdivideSphere(float v1[], float v2[], float v3[], 
		int depth)
	{
		float v12[] = new float[3]; 
		float v23[] = new float[3]; 
		float v31[] = new float[3];
		int i;

		if (depth==0)
		{
			gl.glColor3f(v1[0]*v1[0], v2[1]*v2[1], v3[2]*v3[2]);
			gl.glBegin(GL.GL_TRIANGLES);
				gl.glVertex3f(v1[0], v1[1], v1[2]);
				gl.glVertex3f(v2[0], v2[1], v2[2]);
				gl.glVertex3f(v3[0], v3[1], v3[2]);
			gl.glEnd();
			return;
		}

		for (i = 0; i<3; i++) 
		{
			v12[i] = v1[i]+v2[i];
			v23[i] = v2[i]+v3[i];
			v31[i] = v3[i]+v1[i];
		}
		normalize(v12);
		normalize(v23);
		normalize(v31);
		subdivideSphere(v1, v12, v31, depth-1);
		subdivideSphere(v2, v23, v12, depth-1);
		subdivideSphere(v3, v31, v23, depth-1);
		subdivideSphere(v12, v23, v31, depth-1);
	}
	
	/** Array of planes for the Unit Tetrahedron. **/
	public static final Plane[] unitTetrahedron = new Plane[]{
		new Plane(new Vector3(0,0,0), new Vector3(1,0,0), new Vector3(0,0,1)),
		new Plane(new Vector3(0,0,0), new Vector3(0,1,0), new Vector3(1,0,0)),
		new Plane(new Vector3(0,0,0), new Vector3(0,0,1), new Vector3(0,1,0)),
		new Plane(new Vector3(0,1,0), new Vector3(0,0,1), new Vector3(1,0,0))
	};
	
	/** Draws out the Unit Tetrahedron. **/
	public static void drawUnitTetrahedron()
	{
		gl.glBegin(GL.GL_TRIANGLES);
		{
			int c = 0b001100;
			for(Plane face : unitTetrahedron)
			{
				gl.glColor3f((c >>> 2)&0x1, (c >>> 1)&0x1, c & 0x1);
				gl.glVertex3f(face.v0.x,face.v0.y,face.v0.z);
				gl.glVertex3f(face.v1.x,face.v1.y,face.v1.z);
				gl.glVertex3f(face.v2.x,face.v2.y,face.v2.z);
				c >>>= 1;
			}
		}
		gl.glEnd();
	}

	/** Computes the collisions with the bounding box. **/
	protected void boxCollisions()
	{
		boolean xCol, yCol, zCol;
		boolean xPosCol, xNegCol, yPosCol, yNegCol, zPosCol, zNegCol;
		Vector3 reflectV = new Vector3();
		
		// Sphere
		xPosCol = this.sphereTrans.x + this.sphereScale >=  this.boxScale/2;
		xNegCol = this.sphereTrans.x - this.sphereScale <= -this.boxScale/2;
		yPosCol = this.sphereTrans.y + this.sphereScale >=  this.boxScale/2;
		yNegCol = this.sphereTrans.y - this.sphereScale <= -this.boxScale/2;
		zPosCol = this.sphereTrans.z + this.sphereScale >=  this.boxScale/2;
		zNegCol = this.sphereTrans.z - this.sphereScale <= -this.boxScale/2;
		if(xPosCol || xNegCol || yPosCol || yNegCol || zPosCol || zNegCol)
		{
			reflectV = new Vector3( 
				xPosCol ? (-1):(xNegCol ? (1):(0)),
				yPosCol ? (-1):(yNegCol ? (1):(0)),
				zPosCol ? (-1):(zNegCol ? (1):(0))
			);
			
			/* Sphere bounce off wall. */
			Vector3.bounce(reflectV, this.sphereVel, this.sphereTrans);
			/* Switch rotation direction. */
			this.sphereRotDelta *= -1;
		}
		
		// Tetra
		/* Get the transformed tetra. */
		float[] mat = new float[16];
		gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(this.tetraTrans.x, this.tetraTrans.y, 
				this.tetraTrans.z);
 			gl.glRotatef(this.tetraRot, 0, 1, 1);
			gl.glScalef(this.tetraScale, this.tetraScale, this.tetraScale);
			gl.glTranslatef(-0.5f,-0.5f,-0.5f);
			/* Save the transformation matrix. */
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, mat, 0);
		gl.glPopMatrix();
		
		Vector3[] tetraVertices = new Vector3[]{ new Vector3(0,0,0), 
			new Vector3(1,0,0), new Vector3(0,1,0), new Vector3(0,0,1) };
		for(Vector3 vertex : tetraVertices)
		{
			vertex = vertex.multMatrix(mat);
			
			xPosCol = vertex.x >=  this.boxScale/2;
			xNegCol = vertex.x <= -this.boxScale/2;
			yPosCol = vertex.y >=  this.boxScale/2;
			yNegCol = vertex.y <= -this.boxScale/2;
			zPosCol = vertex.z >=  this.boxScale/2;
			zNegCol = vertex.z <= -this.boxScale/2;
		
			if(xPosCol || xNegCol || yPosCol || yNegCol || zPosCol || zNegCol)
			{			
				reflectV = new Vector3( 
					xPosCol ? (-1):(xNegCol ? (1):(0)),
					yPosCol ? (-1):(yNegCol ? (1):(0)),
					zPosCol ? (-1):(zNegCol ? (1):(0))
				);
			
				/* Line bounces of the tetra. */
				Vector3.bounce(reflectV, this.tetraVel, this.tetraTrans);
				this.tetraTrans = Vector3.addVectors(this.tetraTrans, 
					Vector3.scalarMult(10, reflectV));
				/* Switch rotation direction. */
				this.tetraRotDelta *= -1;
			
				/* Only one collision per frame. */
				break;
			}
		}
		
		/* Line */
		/* Get the transformed line. */
		mat = new float[16];
		gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(this.lineTrans.x, this.lineTrans.y, 
				this.lineTrans.z);
			gl.glRotatef(this.lineRot, 0, 1, 1);
			gl.glScalef(this.lineScale, this.lineScale, this.lineScale);
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, mat, 0);
		gl.glPopMatrix();
		Line line = new Line(new Vector3(0,-0.5f,0), new Vector3(0,0.5f,0));
		line.v0 = line.v0.multMatrix(mat);
		line.v1 = line.v1.multMatrix(mat);
		
		xPosCol=line.v0.x >=  this.boxScale/2 || line.v1.x >=  this.boxScale/2;
		xNegCol=line.v0.x <= -this.boxScale/2 || line.v1.x <= -this.boxScale/2;
		yPosCol=line.v0.y >=  this.boxScale/2 || line.v1.y >=  this.boxScale/2;
		yNegCol=line.v0.y <= -this.boxScale/2 || line.v1.y <= -this.boxScale/2;
		zPosCol=line.v0.z >=  this.boxScale/2 || line.v1.z >=  this.boxScale/2;
		zNegCol=line.v0.z <= -this.boxScale/2 || line.v1.z <= -this.boxScale/2;
		
		if(xPosCol || xNegCol || yPosCol || yNegCol || zPosCol || zNegCol)
		{
			reflectV = new Vector3( 
				xPosCol ? (-1):(xNegCol ? (1):(0)),
				yPosCol ? (-1):(yNegCol ? (1):(0)),
				zPosCol ? (-1):(zNegCol ? (1):(0))
			);
		
			/* Line bounces of the tetra. */
			Vector3.bounce(reflectV, this.lineVel, this.lineTrans);
			/* Switch rotation direction. */
			this.lineRotDelta *= -1;
		}
	}
	
	/** Computes the collisions between the objects. **/
	protected void objectCollisions()
	{
		/* Reusable variables. */
		float distance;
		Vector3 intersection;
		
		/////////////////////////  Transform Objects  //////////////////////////
		Vector3 spherePos = (new Vector3()).multMatrix(this.sphereMat);
		
		/* Get the transformed line. */
		Line line = new Line(new Vector3(0,-0.5f,0), new Vector3(0,0.5f,0));
		line.v0 = line.v0.multMatrix(this.lineMat);
		line.v1 = line.v1.multMatrix(this.lineMat);
		
		Vector3 tetraPos = (new Vector3()).multMatrix(this.sphereMat);
		
		/* Get the transformed tetrahedron. */
		Plane[] transformedTetra = new Plane[]{
		  new Plane(new Vector3(0,0,0), new Vector3(1,0,0), new Vector3(0,0,1)),
		  new Plane(new Vector3(0,0,0), new Vector3(0,1,0), new Vector3(1,0,0)),
		  new Plane(new Vector3(0,0,0), new Vector3(0,0,1), new Vector3(0,1,0)),
		  new Plane(new Vector3(0,1,0), new Vector3(0,0,1), new Vector3(1,0,0))
		};
		
		for(Plane face : transformedTetra)
		{
			face.v0 = face.v0.multMatrix(this.tetraMat);
			face.v1 = face.v1.multMatrix(this.tetraMat);
			face.v2 = face.v2.multMatrix(this.tetraMat);
		}
		////////////////////////////////////////////////////////////////////////
		
		/* Line and Sphere */
		Vector3 closestP = line.closestPointOnLineSegment(spherePos);
		distance = (Vector3.vectorFromPoints(spherePos, closestP)).magnitude();
		if(distance <= this.sphereScale)
		{
			System.out.println("Line Sphere Collision.");
			
			/* Shift away from the collision. */
			Vector3.shiftBackwards(this.sphereTrans, this.sphereVel);
			Vector3.shiftBackwards(this.lineTrans, this.lineVel);
			
			/* Swap velocities. */
			Vector3.swapVectors(this.sphereVel, this.lineVel);

			/* Switch rotation direction. */
			this.sphereRotDelta *= -1;
			this.lineRotDelta *= -1;
		}
		
		/* Tetrahedron and Sphere */
		for(Plane face : transformedTetra)
		{	
			intersection = face.triangleSphereIntersection(spherePos, 
				this.sphereScale);
			if(intersection != null) // There is an intersection.
			{
				System.out.println("Tetrahedron Sphere Collision.");
				
				/* Shift away from the collision. */
				Vector3.shiftBackwards(this.sphereTrans, this.sphereVel);
				Vector3.shiftBackwards(this.tetraTrans, this.tetraVel);
				
				/* Swap velocities. */
				Vector3.swapVectors(this.sphereVel, this.tetraVel);
				
				/* Switch rotation direction. */
				this.sphereRotDelta *= -1;
				this.tetraRotDelta *= -1;
				
				/* Only one collision per frame. */
				break;
			}
		}
		
		/* Line and Tetrahedron	*/
		for(Plane face : transformedTetra)
		{
			intersection =  face.intersectionWithLineSegment(line);
			
			if(intersection != null && face.isPointInsideTriangle(intersection))
			{
				System.out.println("Line Tetrahedron Collision.");
				
				/* Shift away from the collision. */
				Vector3.shiftBackwards(this.tetraTrans, this.tetraVel);
				Vector3.shiftBackwards(this.lineTrans, this.lineVel);
				
				/* Swap velocities. */
				Vector3.swapVectors(this.tetraVel, this.lineVel);
				
				/* Switch rotation direction. */
				this.sphereRotDelta *= -1;
				this.tetraRotDelta *= -1;
				
				/* Only one collision per frame. */
				break;
			}
		}
	}

	/** Method called for OpenGL rendering every reshape. **/
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
		int height)
	{
		/* Update the dimensions. */
		this.width = width;
		this.height = height;
		
		/* Set the viewport of the GL frame. */
		gl.glViewport(0, 0, this.width, this.height);
		
		/* Set the perspective view of the frame. */
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glFrustum(-this.width/4.0,  this.width/4.0, -this.height/4.0, 
			this.height/4.0, this.width/2, 4*this.width);
		gl.glTranslatef(0, 0, -2*this.width);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
/******************************************************************************/
/*******************************  Vector Class  *******************************/
/******************************************************************************/
/** Class for a three dimensional vector. **/
public static class Vector3
{
	public float x, y, z;
	
	public Vector3(){ this(0,0,0); }
	
	/* Create a new 3d vector. */
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/* Converts this vector to a float array. */
	public float[] toArray()
	{
		return new float[]{x,y,z};
	}
	
	/* Converts this vector to a double array. */
	public double[] toDoubleArray()
	{
		return new double[]{x,y,z};
	}
	
	/** Subtracts p0 from p1, i.e. p1-p0, to get the resulting vector. **/
	public static Vector3 vectorFromPoints(Vector3 p0, Vector3 p1)
	{
		return new Vector3(p1.x-p0.x, p1.y-p0.y, p1.z-p0.z);
		//return addVectors(p1, scalarMult(-1, p0));
	}
	
	public static Vector3 addVectors(Vector3 v1, Vector3 v2)
	{
		return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public static Vector3 scalarMult(float c, Vector3 v)
	{
		return new Vector3(c*v.x, c*v.y, c*v.z);
	}
	
	public static float dotProduct(Vector3 a, Vector3 b)
	{
		return (a.x * b.x + a.y * b.y + a.z * b.z);
	}
	
	public static Vector3 crossProduct(Vector3 a, Vector3 b)
	{
		Vector3 vec = new Vector3();
		vec.x = a.y * b.z - a.z * b.y;
		vec.y = a.z * b.x - a.x * b.z;
		vec.z = a.x * b.y - a.y * b.x;
		return vec;
	}
	
	public float magnitude()
	{
		return (float)Math.sqrt(this.x * this.x + this.y * this.y + 
			this.z * this.z);
	}
	
	public void normalize()
	{
		float mag = this.magnitude();

		if (mag == 0) {
			System.err.println("0 length vector: normalize().");
			return;
		} 
		
		this.x /= mag;
		this.y /= mag;
		this.z /= mag;
	}
	
	public static Vector3 reflect(Vector3 v1, Vector3 n)
	{
		float v1_dot_n = dotProduct(v1, n);
		
		return new Vector3(
			2 * v1_dot_n * n.x-v1.x,
			2 * v1_dot_n * n.y-v1.y,
			2 * v1_dot_n * n.z-v1.z
		);
	}
	
	/* Multiply this vector by the given OpenGL transform matrix. */
	public Vector3 multMatrix(float[] mat)
	{
		if(mat == null || mat.length < 16)
			throw new IllegalArgumentException();
		
		float[] prod = new float[3];
		for(int i = 0; i < 3; i++)
		{
			prod[i] = mat[i] * this.x + mat[i+4] * this.y + 
					  mat[i+8] * this.z + mat[i+12] * 1;
		}
		return new Vector3(prod[0], prod[1], prod[2]);
	}
	
	public static Vector3 randomVector(float xmin, float xmax, float ymin, 
		float ymax, float zmin, float zmax)
	{
		float x = (float)Math.random() * (xmax - xmin) + xmin;
		float y = (float)Math.random() * (ymax - ymin) + ymin;
		float z = (float)Math.random() * (zmax - zmin) + zmin;
		
		return new Vector3(x,y,z);
	}
	
	public static Vector3 randomUnitVector()
	{
		Vector3 randN = randomVector(-1,1, -1,1, -1,1);
		randN.normalize();
		return randN;
	}
	
	/** Generates a random position vector in cube centered at the origin. **/
	public static Vector3 randomVectorInCube(float sideLength)
	{
		return randomVector(-sideLength/2, sideLength/2, -sideLength/2, 
			sideLength/2, -sideLength/2, sideLength/2);
	}
	
	/* Bounces the velocity vector of a surface with the normal. */
	/* Changes values of vel and pos does not return anything.   */
	public static void bounce(Vector3 reflectV, Vector3 vel, Vector3 pos)
	{
		/* Normalize the reflect vector. */
		reflectV.normalize();
		/* Compute the backwards velocity for reflections. */
		Vector3 backwardsVel = Vector3.scalarMult(-1, vel);
		/* Reflect the velocity. */
		vel.copyValues(Vector3.reflect(backwardsVel, reflectV));
		/* Move the point away from the collision. */
		pos.copyValues(Vector3.addVectors(pos,backwardsVel));
	}
	
	/* Moves the position in the direction of -velocity. */
	public static void shiftBackwards(Vector3 pos, Vector3 vel)
	{
		/* Compute the backwards velocity for reflections. */
		Vector3 backwardsVel = Vector3.scalarMult(-1, vel);
		/* Move the point away from the collision. */
		pos.copyValues(Vector3.addVectors(pos,backwardsVel));
	}
	
	/** Copies the values to this vector. **/
	public void copyValues(Vector3 orig)
	{
		this.x = orig.x;
		this.y = orig.y;
		this.z = orig.z;
	}
	
	/* Swap the values of two given vectors. */	
	public static void swapVectors(Vector3 v0, Vector3 v1)
	{
		Vector3 temp = new Vector3();
		temp.copyValues(v0);
		v0.copyValues(v1);
		v1.copyValues(temp);
	}
	
	/** Compares if the given object is equal to this one. **/
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Vector3))
			return false;
		Vector3 other = (Vector3)obj;
		
		float deltaX = other.x - this.x;
		float deltaY = other.y - this.y;
		float deltaZ = other.z - this.z;
		
		return  Math.abs(deltaX) < EPSILON && Math.abs(deltaY) < EPSILON && 
			Math.abs(deltaZ) < EPSILON;
	}
	
	/** Creates a deep copy of this vector. **/
	public Vector3 clone()
	{
		return new Vector3(this.x, this.y, this.z);
	}
	
	/** Returns a string representation of this vector. **/
	public String toString()
	{
		return String.format("[%.2f, %.2f, %.2f]", this.x, this.y, this.z);
	}
}

/******************************************************************************/
/********************************  Line Class  ********************************/
/******************************************************************************/
/** Class for a three dimensional line. **/
public static class Line
{
	/* Two points on the line, and the start and end of the line segment. */
	public Vector3 v0, v1;
	
	/** Creates a new line from v0 to v1 **/
	public Line(Vector3 v0, Vector3 v1)
	{
		this.v0 = v0;
		this.v1 = v1;
	}
	
	/** Given a parametric value, t, returns the point on the line. **/
	public Vector3 getPointOnLine(float t)
	{
		return new Vector3( (1-t)*this.v0.x + t*this.v1.x,
			(1-t)*this.v0.y + t*this.v1.y, (1-t)*this.v0.z + t*this.v1.z );
	}
	
	/** Returns the closest point on the line segment to the given point. **/
	public Vector3 closestPointOnLineSegment(Vector3 p)
	{
		Vector3 pv0 = Vector3.vectorFromPoints(p, this.v0);
		Vector3 v0v1 = Vector3.vectorFromPoints(this.v0, this.v1);

		float closestT = -1*((Vector3.dotProduct(pv0, v0v1)) / 
							 Vector3.dotProduct(v0v1, v0v1));
		/* Make sure closestP is on the line segment. */
		closestT = closestT > 1 ? (1):(closestT < 0 ? (0):(closestT));
		
		return this.getPointOnLine(closestT);
	}
	
	/** 
	  * Determines the minimum distance from the point p to this line segment. 
	  */
	public float distanceToPoint(Vector3 p)
	{
		Vector3 closestP = this.closestPointOnLineSegment(p);
		return (Vector3.vectorFromPoints(p, closestP)).magnitude();
	}
	
	/** Determines if the given point lies on the line segment, V0V1. **/
	public boolean isPointOnLineSegment(Vector3 p)
	{
		Vector3 pV1 = Vector3.vectorFromPoints(p, v1);
		Vector3 pV0 = Vector3.vectorFromPoints(p, v0);
		
		return Vector3.dotProduct(pV1, pV0) <= EPSILON && 
			Vector3.crossProduct(pV1, pV0).magnitude() <= EPSILON;
	}
	
	/** Compares if the given object is equal to this one. **/
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Line))
			return false;
		
		Line other = (Line)obj;
		return  this.v0.equals(other.v0) && this.v1.equals(other.v1);
	}
	
	/** Creates a deep copy of this line. **/
	public Line clone()
	{
		return new Line(this.v0.clone(), this.v1.clone());
	}
	
	/** Returns a string representation of this line. **/
	public String toString()
	{
		return String.format("----%s-----%s---", this.v0, this.v1);
	}
}

/******************************************************************************/
/********************************  Plane Class  *******************************/
/******************************************************************************/
/** Class for a plane/triangle in 3D space. **/
public static class Plane
{
	public Vector3 v0, v1, v2;
	
	public Plane(Vector3 v0, Vector3 v1, Vector3 v2)
	{
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
	}
	
	/* Returns the normal of this plane. */
	public Vector3 getNormal()
	{
		Vector3 v1v0 = Vector3.vectorFromPoints(v1,v0), 
				v1v2 = Vector3.vectorFromPoints(v1,v2);
		
		Vector3 norm = (Vector3.crossProduct(v1v2, v1v0));
		norm.normalize();
		return norm;
	}
	
	/* Returns the minimum distance from the plane to a point. */
	public float distanceToPoint(Vector3 p)
	{
		Vector3 v0p = Vector3.vectorFromPoints(v0, p);
		Vector3 normal = this.getNormal();
		return Vector3.dotProduct(normal, v0p);
	}
	
	/* Returns the value of the parametric variable, t, for the intersection. */
	public float timeOfIntersection(Line line)
	{
		Vector3 v0p0 = Vector3.vectorFromPoints(this.v0, line.v0);
		Vector3 p0p1 = Vector3.vectorFromPoints(line.v0, line.v1);
		Vector3 norm = this.getNormal();
		
		float t = Vector3.dotProduct(Vector3.scalarMult(-1, norm), v0p0);
		float denom = Vector3.dotProduct(norm, p0p1);
		
		/* Check if line in plane. */
		if(Math.abs(denom) < EPSILON && this.isPointInPlane(line.v0))
		{
			return 0;
		}
		/* If the line is parallel. */
		else if(Math.abs(denom) < EPSILON)
		{
			return Float.POSITIVE_INFINITY;
		}
		
		t /= denom;
		return t;
	}
	
	/* Returns the point of intersection with the line given. */
	public Vector3 intersectionWithLine(Line line)
	{
		float t = this.timeOfIntersection(line);
		if(t != Float.POSITIVE_INFINITY)
			return line.getPointOnLine(t);
		return null;
	}

	/* Returns the point of intersection with the line segment given. */
	public Vector3 intersectionWithLineSegment(Line line)
	{
		float t = this.timeOfIntersection(line);
		if(t != Float.POSITIVE_INFINITY && t <= 1 && t >= 0)
			return line.getPointOnLine(t);
		return null;
	}

	/* Returns if the point is in the plane. */
	public boolean isPointInPlane(Vector3 p)
	{
		return Math.abs(Vector3.dotProduct(Vector3.vectorFromPoints(p, this.v0), 
			this.getNormal())) <= EPSILON;
	}
	
	/* Returns if the point is in the triangle, V0V1V2. */
	public boolean isPointInsideTriangle(Vector3 p)
	{
		if(!this.isPointInPlane(p))
			return false;

		/* Compute if inside using Barycentric  coordinates */
		Vector3 v0v2 = Vector3.vectorFromPoints(this.v0, this.v2);
		Vector3 v0v1 = Vector3.vectorFromPoints(this.v0, this.v1);
		Vector3 v0p  = Vector3.vectorFromPoints(this.v0,       p);
		
		// Compute dot products
		float v0v2_dot_v0v2 = Vector3.dotProduct(v0v2, v0v2);
		float v0v2_dot_v0v1 = Vector3.dotProduct(v0v2, v0v1);
		float v0v2_dot_v0p  = Vector3.dotProduct(v0v2,  v0p);
		float v0v1_dot_v0v1 = Vector3.dotProduct(v0v1, v0v1);
		float v0v1_dot_v0vp = Vector3.dotProduct(v0v1,  v0p);

		// Compute barycentric coordinates
		float invDenom = 1 / (v0v2_dot_v0v2 * v0v1_dot_v0v1 - v0v2_dot_v0v1 * 
			v0v2_dot_v0v1);
		float u = (v0v1_dot_v0v1 * v0v2_dot_v0p - v0v2_dot_v0v1 * 
			v0v1_dot_v0vp) * invDenom;
		float v = (v0v2_dot_v0v2 * v0v1_dot_v0vp - v0v2_dot_v0v1 * 
			v0v2_dot_v0p) * invDenom;

		// Check if point is in triangle
		return (u >= 0) && (v >= 0) && (u + v <= 1);
	}
	
	/* Compute the intersection of this triangle and a sphere. */
	public Vector3 triangleSphereIntersection(Vector3 sphereCenter,float radius)
	{
		float distance = this.distanceToPoint(sphereCenter);
		
		/* If too far away */
		if(Math.abs(distance) > radius)
			return null;
		
		/* Else intersecting with the plane */
		Vector3 intersection = Vector3.addVectors(sphereCenter, 
			Vector3.scalarMult(-distance, this.getNormal()));
		/* Dead on intersection. */
		if(this.isPointInsideTriangle(intersection))
			return intersection;
		
		/* Else possible edge clipping. */
		Line[] edges = new Line[]{ new Line(this.v0, this.v1),
			new Line(this.v0, this.v2),new Line(this.v1, this.v2) };
		for(Line edge : edges)
		{
			Vector3 closestP = edge.closestPointOnLineSegment(sphereCenter);
			distance = (Vector3.vectorFromPoints(sphereCenter, closestP)).
				magnitude();
			if(distance <= radius)
			{
				return closestP;
			}
		}
	
		/* Not intersecting */
		return null;
	}
	
	/** Compares if the given object is equal to this one. **/
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Plane))
			return false;
		
		Plane other = (Plane)obj;
		return  this.v0.equals(other.v0) && this.v1.equals(other.v1) && 
			this.v2.equals(other.v2);
	}
	
	/** Creates a deep copy of this plane. **/
	public Plane clone()
	{
		return new Plane(this.v0.clone(), this.v1.clone(), this.v2.clone());
	}
	
	/** Returns a string representation of this line. **/
	public String toString()
	{
		String formatStr = "[%s, %s, %s]";
		return String.format(formatStr, this.v0, this.v1, this.v2);
	}
}
}