/*
 * Created on 2004-2-20
 * @author Jim X. Chen: draw randomly generated points
 */

import javax.media.opengl.*;

import com.sun.opengl.util.Animator; 
import java.awt.event.*;





// built on J1_O_Point class
@SuppressWarnings("serial")
public class J1_1_Point extends J1_0_Point {
	
	static Animator  animator; // drive display() in a loop

	public J1_1_Point() {

		// use super's constructor to initialize drawing

		// 1. add a listener for window closing
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				animator.stop(); // stop animation
				System.exit(0);
			}
		});
	}

	// called one-time for OpenGL initialization
	public void init(GLAutoDrawable drawable) {

		super.init(drawable); // to draw into both buffers

		// 2. clear the background to black
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// 3. drive the display() in a loop
		animator = new Animator(canvas);
		animator.start(); // start animator thread

		// display OpenGL and graphics system information
		System.err.println("INIT GL IS: " + gl.getClass().getName());
		System.err.println(drawable.getChosenGLCapabilities());
		System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		super.reshape(drawable, x, y, width, height);
		
		// 5. New width and height of the window after reshape is saved
		WIDTH = width; 
		HEIGHT = height;

		// 6. the real drawing area inside the window (physical address and area)
		gl.glViewport(0, 0, WIDTH, HEIGHT); 
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}


	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable) {
		
		// 7. generate a random point		
		double x = Math.random() * WIDTH;
		double y = Math.random() * HEIGHT;

		// specify a drawing color: random
		gl.glColor3d(Math.random(), Math.random(), Math.random());
		
		// specify to draw a point
		drawPoint(x, y);
	}

	
	// specify to draw a point
	  public void drawPoint(double x, double y) {

	  gl.glBegin(GL.GL_POINTS);
	  gl.glVertex2d(x, y);
	  gl.glEnd();
	}
	
	
	public static void main(String[] args) {
		J1_1_Point f = new J1_1_Point();

		// 8. add a title on the frame
		f.setTitle("JOGL J1_1_Point");

		f.setSize(WIDTH, HEIGHT);
		f.setVisible(true);
	}
	
	
	/* some math operations used often */

	// dot product of two vectors
	public double dotprod(double[] a, double[] b) {

		return (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]);
	}

	// cros product of two vectors
	public void crossprod(double[] a, double[] b, double[] v) {

		v[0] = a[1] * b[2] - a[2] * b[1];
		v[1] = a[2] * b[0] - a[0] * b[2];
		v[2] = a[0] * b[1] - a[1] * b[0];
	}


	// normalize a vector to unit vector
	public void normalize(double vector[]) {
		double d = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]
				+ vector[2] * vector[2]);

		if (d == 0) {
			System.err.println("0 length vector: normalize().");
			return;
		}
		vector[0] /= d;
		vector[1] /= d;
		vector[2] /= d;
	}

	
	public void normalize(float[] vector) {
		// TODO Auto-generated method stub
		float d = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]
				+ vector[2] * vector[2]);

		if (d == 0) {
			System.err.println("0 length vector: normalize().");
			return;
		}
		vector[0] /= d;
		vector[1] /= d;
		vector[2] /= d;

	}

	
	
	// reflect v1 around n to v2
	public void reflect(double v1[], double n[], double v2[]) {

		// v2 = 2*dot(v1, n)*n + v1
		for (int i = 0; i < 3; i++) {
			v2[i] = 2 * dotprod(v1, n) * n[i] - v1[i];
		}
	}
	
	// distance between two points
	public double distance(double[] a, double[] b) {

		return (Math.sqrt((b[0] - a[0]) * (b[0] - a[0]) + (b[1] - a[1])
				* (b[1] - a[1]) + (b[2] - a[2]) * (b[2] - a[2])));
	}

	// distance between two points
	double distance(double x0, double y0, double x1, double y1) {

		return (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)));
	}

	
	public boolean quadraticFormula(double a, double b, double c, double ans[]) {

		double d = b*b - 4*a*c; 
		if (d<0) {
			return (false); 
			
		} else {
			ans[0] = (-b+Math.sqrt(d))/(2*a); 
			ans[1] = (-b-Math.sqrt(d))/(2*a); 
			return (true); 
		}
	}
}
