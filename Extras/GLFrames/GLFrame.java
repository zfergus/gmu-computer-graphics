import java.awt.event.*;
import java.awt.*;
import javax.media.opengl.*;

/**
  * Class for displaying OpenGL in a frame. Implements the GLEventListener in 
  * order to listen for init, resize, and display events.
  * @author Zachary Ferguson 
  **/
public class GLFrame extends Frame implements GLEventListener
{
	/** Width of the GLFrame. **/
	protected int width;
	/** Width of the GLFrame. **/
	protected int height;
	/** Static interface to the OpenGL library. **/
	public static GL gl;
	/** Static canvas that is drawable in a frame. **/
	public static GLCanvas canvas;

	/**
	  * Default constructor that creates a GLFrame of size 800x800 and with a 
	  * title of "GLFrame". 
	  **/
	public GLFrame()
	{
		this(800, 800);
	}
	
	/**
	  * Constructor that creates a GLFrame of size based on the given width and 
	  * height and with a title of "GLFrame". 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  **/
	public GLFrame(int width, int height)
	{
		this(width, height, "GLFrame");
	}
	
	/**
	  * Constructor that creates a GLFrame of size based on the given width and 
	  * height and with the title given. 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  * @param title A string for title of the frame.
	  **/
	public GLFrame(int width, int height, String title)
	{
		/* Initialize the drawable canvas. */
		canvas = new GLCanvas();

		/* Listen to the events related to the canvas. */
		canvas.addGLEventListener(this);

		/* Add the canvas to fill the Frame container. */
		this.add(canvas, BorderLayout.CENTER);

		/* Interface to OpenGL functions */
		gl = canvas.getGL();

		/* Exit when exit button pressed */
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		/* Store the width and height. */
		this.width = width;
		this.height = height;

		/* Set the size of this frame. */
		this.setSize(this.width, this.height);
		/* Set the title of the frame. */
		this.setTitle(title);
	}

	/**
	  * Method called once for OpenGL initialization. Initializes to draw on the
	  * front and back frame buffer.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void init(GLAutoDrawable drawable)
	{
		// this is to say that it will draw into both buffers 
		gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
	}

	/**
	  * Method called once for rendering the GLFrame. 
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable){}
	
	/**
	  * Method called for OpenGL rendering every reshape. Adjusts the width and 
	  * height stored values.
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
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, 0, height, -1.0, 1.0);
	  
		this.width = width;
		this.height = height;

		gl.glViewport(0, 0, this.width, this.height); 
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}

	/**
	  * Method called if display mode or device are changed. 
	  * @param drawable Needed, but unused parameter.
	  * @param modeChanged Boolean for if the mode changed.
	  * @param deviceChanged Boolean for if the device changed.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, 
		boolean deviceChanged){}
}