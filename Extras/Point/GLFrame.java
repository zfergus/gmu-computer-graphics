import java.awt.event.*;
import java.awt.*;
import javax.media.opengl.*;

public class GLFrame extends Frame implements GLEventListener
{
   protected int width, height;
   public static GL gl; // interface to OpenGL
	public static GLCanvas canvas; // drawable in a frame
   
	public GLFrame()
	{
		this(800, 800);
	}

	public GLFrame(int width, int height)
	{
		/* Initialize the drawable canvas. */
		canvas = new GLCanvas();

		/* Listen to the events related to the canvas. */
		canvas.addGLEventListener(this);

		/* Add the canvas to fill the Frame container. */
		this.add(canvas, BorderLayout.CENTER);

		/* Interface to OpenGL functions */
		gl = canvas.getGL();

		/* Set the size of this frame. */
		this.setSize(this.width, this.height);

		/* Exit when exit button pressed */
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		this.width = width;
		this.height = height;

		/* Set the size of this frame. */
		this.setSize(this.width, this.height);
	}
   
	// called once for OpenGL initialization
	public void init(GLAutoDrawable drawable)
	{
		// this is to say that it will draw into both buffers 
		gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
	}

	// called for handling reshaped drawing area
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

	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable){}

	// called if display mode or device are changed
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, 
		boolean deviceChanged){}
}