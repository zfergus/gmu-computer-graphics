import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;

/**
  * HW06 of CS451 Computer Graphics: Draws 3d objects bouncing in a box and off 
  * one another with a directional light and a movable point light. Also uses a 
  * texture mapping on the Tetrahedron.
  * 
  * Controls:
  * Move Light in XY Plane: Move Mouse  OR Arrow Keys
  * Move Light in Z: Enter and Shift  OR  Drag Mouse in Y
  * Change Light Intensity: Mouse Wheel  OR  W and S
  * Change Light Intensity to Value: Number Keys
  * 
  * @author Zachary Ferguson
  */
public class HW6_zfergus2 extends HW5_zfergus2 implements KeyListener, 
	MouseListener, MouseMotionListener, MouseWheelListener
{

	protected Texture tetraTexture;

	/**
	  * Main method for demoing the HW6_zfergus2 class.
	  * @param args Command line arguments (Ignored).
	  **/
	public static void main(String[] args)
	{
		/* Create a HW6_zfergus2 frame. */
		try
		{
			HW6_zfergus2 hw06 = new HW6_zfergus2(800, 800, "Zachary Ferguson" +
				" - HW6_zfergus2");

			/* Display the hw frame */
			hw06.setVisible(true);
		}
		catch(IOException ioE)
		{
			ioE.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	  * Crea1tes an instance of the HW6_zfergus2 class given the width, height, 
	  * and title.
	  * @param width Width of the frame.
	  * @param height Height of the frame.
	  * @param title The title of the frame.
	  */
	public HW6_zfergus2(int width, int height, String title) throws IOException
	{
		super(width, height, title);
		
		this.tetraTexture = new Texture("./DT_LoRes_color.jpg");
		System.err.println("\nINSTRUCTIONS: PLACE TEXTURE IMAGE FILE IN LOCAL "+
			"DIRECTORY.\n");
		
		this.dirLightColor = new Color(0.5f,0.5f,1);
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
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, 
			GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, 
			GL.GL_NEAREST);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, 
			this.tetraTexture.getWidth(), this.tetraTexture.getHeight(), 0, 
			this.tetraTexture.getGLType(), GL.GL_UNSIGNED_BYTE, 
			ByteBuffer.wrap(this.tetraTexture.getData()));
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
		super.display(drawable);
	}
	
	/** Draw the tetrahedron. */
	@Override
	protected void drawTetrahedron()
	{
		/* Set Material to white. */
		this.setMaterial(Color.DARK_GREY, Color.BLACK, Color.WHITE, 
			Color.WHITE, 100);
		
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
			
			HW6_zfergus2.drawUnitTetrahedron();
		gl.glPopMatrix();
		
		/* Reset material to default. */
		this.setMaterial(Color.DARK_GREY, Color.BLACK, Color.LIGHT_GREY, 
			Color.WHITE, 100);
	}
	
	/** Draws out the Unit Tetrahedron. **/
	public static void drawUnitTetrahedron()
	{
		/* Set the texture to modulate the material color. */
		gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL,GL.GL_SEPARATE_SPECULAR_COLOR);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL.GL_TRIANGLES);
		{
			for(Plane face : unitTetrahedron)
			{	
				float[] norm = face.getNormal().toArray();
				
				gl.glNormal3fv(norm, 0);
				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3f(face.v0.x,face.v0.y,face.v0.z);
				
				gl.glNormal3fv(norm, 0);
				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3f(face.v1.x,face.v1.y,face.v1.z);
				
				gl.glNormal3fv(norm, 0);
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3f(face.v2.x,face.v2.y,face.v2.z);
			}
		}
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
/**
  * Class for opening and reading byte data from an image file. Stores data and 
  * properties of the image in memory.
  * @author Zachary Ferguson
  */
public static class Texture
{
	/** Pixel data for the image that was opened. **/
	protected byte[] data;
	
	protected int width, height, imgType;
	
	public Texture(String textureFilename) throws IOException
	{
		/* Read the data from the image file. */
		File imgFile = new File(textureFilename);
		BufferedImage imgBuffer = ImageIO.read(imgFile);
		
		this.width   = imgBuffer.getWidth();
		this.height  = imgBuffer.getHeight();
		
		/* TYPE_BYTE_GRAY 10; TYPE_3BYTE_BGR 5 */
		this.imgType = imgBuffer.getType(); 
		
		Raster imgRaster = imgBuffer.getData();
		DataBufferByte dataBuffer = (DataBufferByte)imgRaster.getDataBuffer();
		this.data = dataBuffer.getData();
		//this.getGLType()();
		this.swapDataOrder();
	}
	
	/**
	  * Swap the order of the bytes in data.
	  */
	protected void swapDataOrder()
	{
		if(this.getGLType() == GL.GL_RGB)
		{	
			// Order: BGR
			for(int i = 0; i < data.length-2; i += 3)
			{
				byte tmp = this.data[i];
				this.data[i] = this.data[i+2];
				this.data[i+2] = tmp;
			}
		}
		else if(this.getGLType() == GL.GL_RGBA)
		{
			// Order: ABGR
			for(int i = 0; i < data.length-2; i += 4)
			{
				// Swap A and R
				byte tmp = this.data[i];
				this.data[i] = this.data[i+2];
				this.data[i+3] = tmp;
				
				// Swap B and G
				tmp = this.data[i+1];
				this.data[i+1] = this.data[i+2];
				this.data[i+2] = tmp;
			}
		}
	}
	
	/**
	  * Gets the byte data for this texture.
	  * @return Returns the byte data of this texture.
	  */
	public byte[] getData()
	{
		return this.data;
	}
	
	/**
	  * Get the width of this texture.
	  * @return Returns the width of this texture.
	  */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	  * Get the height of this texture.
	  * @return Returns the height of this texture.
	  */
	public int getHeight()
	{
		return this.height;
	}
	
	/** 
	  * Gets the image type of this texture.
	  * @return Returns the BufferedImage image type.
	  */
	public int getImgType()
	{
		return this.imgType;
	}
	
	/**
	  * Gets the OpenGL type for this textures data.
	  * @return Returns the GL type of this texture.
	  */
	public int getGLType()
	{
		switch(this.imgType)
		{
			case BufferedImage.TYPE_3BYTE_BGR:
				return GL.GL_RGB;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				return GL.GL_RGBA;
			case BufferedImage.TYPE_BYTE_GRAY:
				return GL.GL_LUMINANCE;
			default:
				return 0;
		}
		
		/*
		System.out.printf("Image Type: %d\n", imgType);
		System.out.printf("OpenGL Types:\n");
		System.out.printf("GL_ALPHA: %d\n", GL.GL_ALPHA);
		System.out.printf("GL_LUMINANCE: %d\n", GL.GL_LUMINANCE);
		System.out.printf("GL_LUMINANCE_ALPHA: %d\n", GL.GL_LUMINANCE_ALPHA);
		System.out.printf("GL_RGB: %d\n", GL.GL_RGB);
		System.out.printf("GL_RGBA: %d\n", GL.GL_RGBA);
		*/
	}
}
}