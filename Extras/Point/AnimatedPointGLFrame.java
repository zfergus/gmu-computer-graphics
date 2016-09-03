import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 

public class AnimatedPointGLFrame extends PointGLFrame {
	
	protected static Animator animator; // drive display() in a loop

	public static void main(String[] args) {
		AnimatedPointGLFrame frame = new AnimatedPointGLFrame();

		frame.setTitle("Animated Points");

		frame.setVisible(true);
	}

	public AnimatedPointGLFrame()
   {
      super();
	}
   
   public AnimatedPointGLFrame(int width, int height)
   {
      super(width, height);
	}  

	// called one-time for OpenGL initialization
	public void init(GLAutoDrawable drawable)
   {
		super.init(drawable);
  
      /* Loop the display method. */    
		animator = new Animator(canvas);
		animator.start(); // start animator thread
	}
   
	// called for OpenGL rendering every reshape
	public void display(GLAutoDrawable drawable)
   {
		int x = (int)(Math.random() * this.width);
		int y = (int)(Math.random() * this.height);

		// specify a drawing color: random
		gl.glColor3d(Math.random(), Math.random(), Math.random());
		
		// specify to draw a point
		this.drawPoint(1, x, y);
	}
}
