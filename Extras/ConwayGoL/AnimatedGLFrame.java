import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 

/**
  * Child class of the GLFrame class. This class adds the animator to repeatably
  * call the display method.
  * @author Zachary Ferguson 
  **/
public class AnimatedGLFrame extends GLFrame
{
	/** Animator for looping the display method. **/
	protected static Animator animator;

	/**
	  * Default constructor that creates an AnimatedGLFrame of size 800x800 and 
	  * with a title of "AnimatedGLFrame". 
	  **/
	public AnimatedGLFrame()
	{
		this(800, 800);
	}
   
	/**
	  * Constructor that creates an AnimatedGLFrame of size based on the given 
	  * width and height and with a title of "AnimatedGLFrame". 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  **/
	public AnimatedGLFrame(int width, int height)
	{
		this(width, height, "AnimatedGLFrame");
	}
	
	/**
	  * Constructor that creates an AnimatedGLFrame of size based on the given 
	  * width and height and with the title given. 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  * @param title A string for title of the frame.
	  **/
	public AnimatedGLFrame(int width, int height, String title)
	{
		super(width, height, title);
		
		/* Animator for looping the display method. */    
		this.animator = new Animator(canvas);
	}

	/**
	  * Method called once for OpenGL initialization. Starts the animator to 
	  * loop the display method.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void init(GLAutoDrawable drawable)
	{
		super.init(drawable);

		/* Start the animator which calls display repeatably. */
		this.animator.start();
	}
}
