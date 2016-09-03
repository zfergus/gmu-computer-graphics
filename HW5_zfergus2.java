import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import java.awt.event.*;
import java.util.*;

/**
  * HW05 of CS451 Computer Graphics: Draws 3d objects bouncing in a box and off 
  * one another with a directional light and a movable point light.
  * 
  * Controls:
  * Move Light in XY Plane: Move Mouse  OR Arrow Keys
  * Move Light in Z: Enter and Shift  OR  Drag Mouse in Y
  * Change Light Intensity: Mouse Wheel  OR  W and S
  * Change Light Intensity to Value: Number Keys
  * 
  * @author Zachary Ferguson
  */
public class HW5_zfergus2 extends HW4_zfergus2 implements KeyListener, 
	MouseListener, MouseMotionListener, MouseWheelListener
{
	/** Instruction for controls **/
	public static final String instructions = 
		"Instructions:\n"+
		"Move Light in XY Plane: Move Mouse  OR Arrow Keys\n"+
		"Move Light in Z: Enter and Shift  OR  Drag Mouse in Y\n" +
		"Change Light Intensity: Mouse Wheel  OR  W and S\n" + 
		"Change Light Intensity to Value: Number Keys\n"+
		"LightPos: %s, Intensity: %.2f";
	protected boolean showHelp;
	
	/** Static reference to glu. **/
	public static GLU glu;
	
	/* Directional light properties. */
	protected Vector3 dirLightDir;
	protected Color   dirLightColor;
	
	/* Point light properties. */
	protected Vector3 pointLightPos;
	protected Vector3 pointLightScale;
	protected Color   pointLightColor;
	protected Color   pointLightDiffuseColor;
	
	/* Attenuation values for the point light. */
	protected float constAtt     = 0.20000f;
	protected float linearAtt    = 0.00050f; 
	protected float quadraticAtt = 0.00005f;
	
	/** Variable used to vary intensity. **/
	protected float intensityFactor;
	
	/* Variables for user input deltas. */
	protected final float DELTA_X = 10, DELTA_Y = 10, DELTA_Z = 20;
	protected float prevY;
	
	/* Used to convert view screen to world coordinates.  */
	protected int[] viewport;
	protected double[] projectionMat, modelviewMat;

	/**
	  * Main method for demoing the HW5_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args) 
	{
		/* Create a HW5_zfergus2 frame. */
		HW5_zfergus2 hw05 = new HW5_zfergus2(800, 800, "Zachary Ferguson - HW5"+
			"_zfergus2");
      
		/* Display the hw frame */
		hw05.setVisible(true);
	}
	
	/**
	  * Crea1tes an instance of the HW5_zfergus2 class given the width, height, 
	  * and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  */
	public HW5_zfergus2(int width, int height, String title)
	{
		super(width, height, title);
		
		/* Listen for Keyboard input. */
		this.addKeyListener(this);
		/* Listen for Mouse input. */
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.requestFocus();
		
		this.intensityFactor = (float)Math.log(10); // Intensity = 1.0f;
		
		this.dirLightDir = new Vector3(1,1,1);
		this.dirLightColor = Color.BLUE;
		
		this.pointLightPos = new Vector3();
		this.pointLightScale = new Vector3(40,40,40);
		this.pointLightColor = new Color(1,0,0,0.75f);
		this.pointLightDiffuseColor = new Color(1,1,1,0.5f);
		
		this.viewport = null;
		this.modelviewMat = null;
		this.projectionMat = null;
		
		glu = new GLU();
		this.showHelp = true;
		this.prevY = 0;
	}
	
	/**
	  * Method called once for initializing the frame. Enables culling and 
	  * z-buffer.
	  * @param drawable Needed, but unused parameter.
	  **/
	@Override
	public void init(GLAutoDrawable drawable)
	{
		super.init(drawable);
		
		gl.glEnable(GL.GL_RESCALE_NORMAL);
		gl.glEnable(GL.GL_NORMALIZE);
		
		gl.glEnable(GL.GL_LIGHTING);
		
		gl.glEnable(GL.GL_LIGHT0);
		/* Initialize Direction Light */
		this.setLighting(GL.GL_LIGHT0, this.dirLightDir, false, 
			this.dirLightColor, this.dirLightColor, this.dirLightColor);

		gl.glEnable(GL.GL_LIGHT1);
		/* Attenuation */
		this.setAttenuation(GL.GL_LIGHT1, this.constAtt, this.linearAtt, 
			this.quadraticAtt);
		/* Initialize Point Light */
		this.setLighting(GL.GL_LIGHT1, pointLightPos, true, pointLightColor, 
			pointLightColor, pointLightColor);
		
		/* Default Material properties. */
		this.setMaterial(Color.BLACK, Color.BLACK, Color.LIGHT_GREY, 
			Color.WHITE, 100);
		
		/* Blending */
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		/* Anti-aliasing */
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH, GL.GL_NICEST);
		
		//gl.glEnable(GL.GL_COLOR_MATERIAL);
	}
	
	/** Set the lighting conditions for lightN. */
	protected void setLighting(int lightN, Vector3 pos, 
		boolean isPointLight, Color ambient, Color diffuse, Color specular)
	{
		/* Set Light Position */
		float[] homoPos = new float[]{pos.x, pos.y, pos.z, isPointLight ? (1):
			(0)};
		gl.glLightfv(lightN, GL.GL_POSITION, homoPos, 0);
		
		/* Set Light Color */
		gl.glLightfv(lightN, GL.GL_AMBIENT, ambient.toArray(),0);
		gl.glLightfv(lightN, GL.GL_DIFFUSE, diffuse.toArray(), 0);
		gl.glLightfv(lightN, GL.GL_SPECULAR, specular.toArray(), 0);
	}
	
	/** Set the lighting attenuation for lightN. */
	protected void setAttenuation(int lightN, float constant, float linear, 
		float quadratic)
	{
		gl.glLightf(lightN, GL.GL_CONSTANT_ATTENUATION,  constant);
		gl.glLightf(lightN, GL.GL_LINEAR_ATTENUATION,    linear);
		gl.glLightf(lightN, GL.GL_QUADRATIC_ATTENUATION, quadratic);
	}
	
	/** Set the material color to the Colors given.  */
	protected void setMaterial(Color emission, Color ambient, Color diffuse, 
		Color specular, float shininess)
	{
		int faceOption = GL.GL_FRONT;
		gl.glMaterialfv(faceOption, GL.GL_EMISSION,  emission.toArray(), 0);
		gl.glMaterialfv(faceOption, GL.GL_AMBIENT,   ambient.toArray(),  0);
		gl.glMaterialfv(faceOption, GL.GL_DIFFUSE,   diffuse.toArray(),  0);
		gl.glMaterialfv(faceOption, GL.GL_SPECULAR,  specular.toArray(), 0);
		gl.glMaterialf (faceOption, GL.GL_SHININESS, shininess);
	}
	
	/**
	  * Method called every frame to draw out the objects and computes 
	  * collisions.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	@Override
	public void display(GLAutoDrawable drawable)
	{
		/* Set the default material color. */
		this.setMaterial(Color.DARK_GREY, Color.BLACK, Color.LIGHT_GREY, 
			Color.WHITE, 100);
			
		super.display(drawable);

		this.requestFocus();
		
		/* Draw the movable light source. */
		Color intenseColor = Color.multConstant(
			(float)Math.exp(intensityFactor)/10f,pointLightColor);
		
		this.setAttenuation(GL.GL_LIGHT1, 
			this.constAtt/((float)Math.exp(intensityFactor)/10f), 
			this.linearAtt/((float)Math.exp(intensityFactor)/10f), 
			this.quadraticAtt/((float)Math.exp(intensityFactor)/10f));
		this.setLighting(GL.GL_LIGHT1, pointLightPos, true, intenseColor, 
			intenseColor, intenseColor);
			
		/* Set the movable light source color. */
		this.setMaterial(intenseColor, intenseColor, 
			this.pointLightDiffuseColor, intenseColor, 100);
		gl.glPushMatrix();
			gl.glTranslatef(pointLightPos.x, pointLightPos.y, pointLightPos.z);
			//Vector3 intenseScale = Vector3.scalarMult(
				//clamp(this.intensityFactor, 2.5f,4.5f), this.pointLightScale);
			gl.glScalef(pointLightScale.x, pointLightScale.y, 
				pointLightScale.z);
			this.drawUnitSphere(3);
		gl.glPopMatrix();
		
		if(showHelp)
		{
			this.displayString(String.format(instructions, this.pointLightPos,
				(float)Math.exp(intensityFactor)/10f), 
				new Vector3(-this.width + 15, -this.height - 15, 0));
		}
		else
		{
			this.displayString("Click for instructions", 
				new Vector3(-this.width + 15, -this.height - 15, 0));
		}
	}
	
	/**
	  * Display a string at the anchor position on the screen.
	  * @param str String to display.
	  * @param anchor Position to anchor the string at.
	  */
	public void displayString(String str, Vector3 anchor)
	{
		/* Set label color. */
		this.setMaterial(Color.YELLOW, Color.BLACK, Color.LIGHT_GREY, 
			Color.WHITE, 100);
		
		String[] lines = str.split("\n");
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		for(int i = 0; i < lines.length; i++)
		{
			gl.glRasterPos3f(anchor.x, anchor.y + 30*(lines.length-i), 
				anchor.z);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, lines[i]);
		}
		gl.glEnable(GL.GL_DEPTH_TEST);
	}
	
	/** Draw the box. */
	@Override
	protected void drawBox()
	{
		gl.glPushMatrix();
			//gl.glColor3f(1, 0.5f, 0);	
			gl.glScalef(this.boxScale, this.boxScale, this.boxScale);
			gl.glTranslatef(-0.5f,-0.5f,-0.5f); /* Translate to origin. */
			
			/* Save the transformation matrix. */
			this.boxMat = new float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, this.boxMat, 0);
			
			HW5_zfergus2.drawUnitCube();
		gl.glPopMatrix();
	}
	
	/** Draw the sphere. */
	@Override
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
			
			drawUnitSphere(4);
		gl.glPopMatrix();
	}
	
	/** Draw the tetrahedron. */
	@Override
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
			
			HW5_zfergus2.drawUnitTetrahedron();
		gl.glPopMatrix();
	}
	
	/** Draw the line. */
	@Override
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
			
			//gl.glColor3f(0.5f,1,0);
			//gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION,Color.BLUE.toArray(),
			//	0);
			gl.glBegin(GL.GL_LINES);
				gl.glNormal3f(1,1,1);
				gl.glVertex3f(0,-0.5f,0);
				gl.glNormal3f(1,1,1);
				gl.glVertex3f(0,0.5f,0);
			gl.glEnd();
		gl.glPopMatrix();
	}
	
	/** Draws the unit cube with the diagonal from (0,0,0) to (1,1,1) **/
	public static void drawUnitCube()
	{
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glNormal3f(0,0,-1); gl.glVertex3f(0,0,0);
			gl.glNormal3f(0,0,-1); gl.glVertex3f(0,1,0);
			gl.glNormal3f(0,0,-1); gl.glVertex3f(1,1,0);
			gl.glNormal3f(0,0,-1); gl.glVertex3f(1,0,0);			
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glNormal3f(0,0,1); gl.glVertex3f(0,0,1); 
			gl.glNormal3f(0,0,1); gl.glVertex3f(1,0,1); 
			gl.glNormal3f(0,0,1); gl.glVertex3f(1,1,1);
			gl.glNormal3f(0,0,1); gl.glVertex3f(0,1,1);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glNormal3f(1,0,0); gl.glVertex3f(1,0,0); 
			gl.glNormal3f(1,0,0); gl.glVertex3f(1,1,0); 
			gl.glNormal3f(1,0,0); gl.glVertex3f(1,1,1);
			gl.glNormal3f(1,0,0); gl.glVertex3f(1,0,1);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glNormal3f(-1,0,0); gl.glVertex3f(0,0,0); 
			gl.glNormal3f(-1,0,0); gl.glVertex3f(0,0,1); 
			gl.glNormal3f(-1,0,0); gl.glVertex3f(0,1,1);
			gl.glNormal3f(-1,0,0); gl.glVertex3f(0,1,0);
		gl.glEnd();	
	}
	
	/* Draw face of sphere. */
	@Override
	protected void subdivideSphere(float v1[], float v2[], float v3[], 
		int depth)
	{
		float v12[] = new float[3]; 
		float v23[] = new float[3]; 
		float v31[] = new float[3];
		int i;

		if (depth==0)
		{
			//gl.glColor3f(v1[0]*v1[0], v2[1]*v2[1], v3[2]*v3[2]);
			//Color color = new Color(v1[0]*v1[0], v2[1]*v2[1], v3[2]*v3[2]);
			//gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, color.toArray(), 0);
			gl.glBegin(GL.GL_TRIANGLES);
				gl.glNormal3f(v1[0], v1[1], v1[2]);
				gl.glVertex3f(v1[0], v1[1], v1[2]);
				
				gl.glNormal3f(v2[0], v2[1], v2[2]);
				gl.glVertex3f(v2[0], v2[1], v2[2]);
				
				gl.glNormal3f(v3[0], v3[1], v3[2]);
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
	
	/** Draws out the Unit Tetrahedron. **/
	public static void drawUnitTetrahedron()
	{
		gl.glBegin(GL.GL_TRIANGLES);
		{
			//int c = 0b001100;
			for(Plane face : unitTetrahedron)
			{
				//gl.glColor3f((c >>> 2)&0x1, (c >>> 1)&0x1, c & 0x1);
				//float[] color = new float[]{(c >>> 2)&0x1, (c >>> 1)&0x1, 
				//	c & 0x1};
				//gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, 
				//	Color.BLUE.toArray(), 0);
				
				float[] norm = face.getNormal().toArray();
				
				gl.glNormal3fv(norm, 0);
				gl.glVertex3f(face.v0.x,face.v0.y,face.v0.z);
				gl.glNormal3fv(norm, 0);
				gl.glVertex3f(face.v1.x,face.v1.y,face.v1.z);
				gl.glNormal3fv(norm, 0);
				gl.glVertex3f(face.v2.x,face.v2.y,face.v2.z);
				//c >>>= 1;
			}
		}
		gl.glEnd();
	}
	
	/** Method called for OpenGL rendering every reshape. **/
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
		int height)
	{
		super.reshape(drawable, x, y, width, height);
		
		this.viewport = new int[4];
		this.modelviewMat = new double[16];
		this.projectionMat = new double[16];
		
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelviewMat, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMat, 0);
	}
	
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if(code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9)
		{
			this.intensityFactor = (float)Math.log(10*(e.getKeyCode() - 0x60));
			return;
		}
		if(code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9)
		{
			this.intensityFactor = (float)Math.log(10*(e.getKeyCode() - 0x30));
			return;
		}
		
		switch(e.getKeyCode())
		{
			case(KeyEvent.VK_RIGHT):
				this.pointLightPos.x += DELTA_X;
				break;
			case(KeyEvent.VK_LEFT):
				this.pointLightPos.x -= DELTA_X;
				break;
			case(KeyEvent.VK_UP):
				this.pointLightPos.y += DELTA_Y;
				break;
			case(KeyEvent.VK_DOWN):
				this.pointLightPos.y -= DELTA_Y;
				break;
			case(KeyEvent.VK_SHIFT):
				this.pointLightPos.z += DELTA_Z;
				break;
			case(KeyEvent.VK_ENTER):
				this.pointLightPos.z -= DELTA_Z;
				break;
			case(KeyEvent.VK_W):
				this.intensityFactor += 1/10f;
				this.intensityFactor  = clamp(intensityFactor, -1.0f, 4.5f);
				break;
			case(KeyEvent.VK_S):
				this.intensityFactor -= 1/10f;
				this.intensityFactor  = clamp(intensityFactor, -1.0f, 4.5f);
			case(KeyEvent.VK_Z):
				this.pointLightPos = new Vector3();
				break;
			case(KeyEvent.VK_H):
				showHelp = !showHelp;
				break;
		}
	}
	
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	
	public void mouseMoved(MouseEvent e)
	{
		/* Store previous mouse y position. */
		prevY = e.getY();
		
		// System.out.printf("Mouse Moved: e.x = %d, e.y = %d, z = %g\n", 
			// e.getX(), e.getY(), pointLightPos.z);
		
		Vector3 worldCoords = screenToWorldCoord(new Vector3(e.getX(), 
			this.height - e.getY(), pointLightPos.z));
		if(worldCoords == null)
			return;
		
		this.pointLightPos = worldCoords;
		//System.out.printf("LightPos: %s\n", this.pointLightPos);
	}
	
	/** Converts a given vector in screen coordinates to world coordinates. **/
	public Vector3 screenToWorldCoord(Vector3 pos)
	{
		if(viewport == null || projectionMat == null || modelviewMat == null)
			return null;
		
		double[] start = new double[3];
		double[] end   = new double[3];
		if(glu.gluUnProject(pos.x, pos.y, 0, modelviewMat, 0, projectionMat, 0,
			viewport, 0, start, 0) && 
		   glu.gluUnProject(pos.x, pos.y, 1, modelviewMat, 0, projectionMat, 0, 
			viewport, 0, end, 0))
		{
			double t = (pos.z-start[2]) / (end[2]-start[2]);
			Line ray = new Line(
				new Vector3((float)start[0], (float)start[1], (float)start[2]), 
				new Vector3((float)end[0],   (float)end[1],   (float)end[2]));
			return ray.getPointOnLine((float)t);
		}
		return null;
	}
	
	public void mouseDragged(MouseEvent e)
	{
		float y = e.getY();
		this.pointLightPos.z += 5*(y-prevY);
		this.mouseMoved(e);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		this.intensityFactor -= (float)e.getPreciseWheelRotation()/10f;
		this.intensityFactor  = clamp(intensityFactor, -1.0f, 4.5f);
		//System.out.printf("Intensity: %.2f\n", this.intensityFactor);
	}
	
	public static float clamp(float val, float min, float max)
	{
		return Math.max(min, Math.min(max, val));
	}
	
	public static float map(float val, float valMin, float valMax, float mapMin,
		float mapMax)
	{
		return (val - valMin) * (mapMax - mapMin) / (valMax - valMin) + mapMin;
	}
	
	/* Toggle the help section. */
	public void mouseClicked(MouseEvent e)
	{ 
		//System.out.println("Clicked");
		showHelp = !showHelp;
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
/******************************************************************************/
/********************************  Color Class  *******************************/
/******************************************************************************/
public static class Color
{
	float r,g,b,a;
	
	public static final Color 
		RED = new Color(1,0,0),
		GREEN = new Color(0,1,0), 
		BLUE = new Color(0,0,1),
		YELLOW = new Color(1,1,0), 
		MAGENTA = new Color(1,0,1), 
		CYAN = new Color(0,1,1),
		ORANGE=new Color(1,0.5f,0),
		BLACK = new Color(0,0,0), 
		DARK_GREY = new Color(0.25f,0.25f,0.25f),
		GREY = new Color(0.5f,0.5f,0.5f), 
		LIGHT_GREY = new Color(0.75f,0.75f,0.75f),
		WHITE = new Color(1,1,1);
		
	
	public Color(float r, float g, float b)
	{
		this(r,g,b,1);
	}
	
	public Color(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public float[] toArray()
	{
		return new float[]{r,g,b,a};
	}
	
	public static Color multConstant(float c, Color colour)
	{
		return new Color(c*colour.r, c*colour.g, c*colour.b);
	}
}
}