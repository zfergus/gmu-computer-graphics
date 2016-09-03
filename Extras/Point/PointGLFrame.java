import java.awt.*;
import javax.media.opengl.*;

public class PointGLFrame extends GLFrame
{
	public static void main(String[] args)
	{
		PointGLFrame gl_frame = new PointGLFrame();   
		gl_frame.setVisible(true);
	}

	public PointGLFrame()
	{
		super();
	}

	public PointGLFrame(int width, int height)
	{
		super(width, height);
	}
   
	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable)
	{
		super.display(drawable);

		/* Provide a drawing color. */
		gl.glColor3f(0.5f, 0.5f, 0.5f);

		this.drawPoint(10, this.width/2, this.height/2);
	}
   
	protected void drawPoint(int size, int x, int y)
	{
		gl.glPointSize(size); // just to make it large enough to be seen
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex2i(x, y);
		gl.glEnd();
	}
}