/***********************************************************
 * Created on 2004-2-20
 * @author Jim X. Chen: draw a point
 * Modified 2007-9-8 for the new JOGL system

 * Updated 2012 for simplicity and changes in most of the sample programs
 */
import java.awt.*;
import javax.media.opengl.*;

@SuppressWarnings("serial")
public class J1_0_Point extends Frame implements GLEventListener {

	static int HEIGHT = 800, WIDTH = 800;
	static GL gl; // interface to OpenGL
	static GLCanvas canvas; // drawable in a frame

	public J1_0_Point() {

		// 1. specify a drawable: canvas
		canvas = new GLCanvas();
		
		// 2. listen to the events related to canvas: init, reshape, display, and displayChanged
		canvas.addGLEventListener(this);

		// 3. add the canvas to fill the Frame container
		this.add(canvas, BorderLayout.CENTER);

		// 4. interface to OpenGL functions
		gl = canvas.getGL();
	}

	public static void main(String[] args) {
		J1_0_Point frame = new J1_0_Point();

		// 5. set the size of the frame and make it visible
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
	}

	// called once for OpenGL initialization
	public void init(GLAutoDrawable drawable) {
		
		//gl = drawable.getGL(); // just to show what the drawable provides

		// 6. specify a drawing color: gray
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		
		// this is to say that it will draw into both buffers 
		gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
	}

	// called for handling reshaped drawing area
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		// 7. specify the drawing area (frame) coordinates
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, 0, height, -1.0, 1.0);
	}

	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable) {

		// 8. specify to draw a point
		gl.glPointSize(10); // just to make it large enough to be seen
		gl.glBegin(GL.GL_POINTS);
		   gl.glVertex2i(WIDTH / 2, HEIGHT / 2);
		gl.glEnd();
		
		// sometimes more buffers are used, so draw it again. 
		//drawable.repaint(); //Schedules a repaint of the component at some point in the future.
		//gl.glFlush(); // seems not needed anyway
	}

	// called if display mode or device are changed
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
}
