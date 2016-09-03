import java.awt.event.*;
import java.awt.*;
import javax.media.opengl.*;

import javax.media.opengl.*;
import com.sun.opengl.util.Animator; 

/**
  * Class for displaying and simulating John Conway's game of life using OpenGL.
  * @author Zachary Ferguson 
  **/
public class GameOfLife extends AnimatedGLFrame
{
	public static void main(String[] args)
	{
		GameOfLife game = new GameOfLife();
		
		game.setVisible(true);
	}
	
	/** 2D Array of booleans for the board of cells. **/
	private boolean[][] board;
	
	/**
	  * Default constructor that creates an GameOfLife of size 800px x 800px and 
	  * with a title of "GameOfLife". 
	  **/
	public GameOfLife()
	{
		this(800, 800);
	}
   
	/**
	  * Constructor that creates an GameOfLife of size based on the given width 
	  * and height and with a title of "GameOfLife". 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  **/
	public GameOfLife(int width, int height)
	{
		this(width, height, "GameOfLife");
	}
	
	/**
	  * Constructor that creates an GameOfLife of size based on the given width 
	  * and height and with the title given. 
	  * @param width The width of the new frame in pixels.
	  * @param height The height of the new frame in pixels.
	  * @param title A string for title of the frame.
	  **/
	public GameOfLife(int width, int height, String title)
	{
		super(width, height, title);

		this.initializeBoard();
		
		this.setResizable(false);
	}
	
	/**
	  * Method for initialing the board's state to random values.
	  **/
	private void initializeBoard()
	{
		this.board = new 
			boolean[(int)((this.height-50)/10)][(int)((this.width-20)/10)];
		
		for(int r = 0; r < this.board.length; r++)
		{
			for(int c = 0; c < this.board[0].length; c++)
			{
				this.board[r][c] = (Math.random() >= 0.5) ? (true):(false);
			}
		}
	}
	
	/**
	  * Method called once for OpenGL initialization. Initializes the size of 
	  * the cells to 10px and the color to blue.
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void init(GLAutoDrawable drawable)
	{
		super.init(drawable);
		
		/* Set the cells size to 10px. */
		gl.glPointSize(10);
		
		/* Set color of the cells. */
		gl.glColor3f(0.0f, 0.0f, 1.0f);
	}
	
	/**
	  * Method called repeatably for rendering the GameOfLife. 
	  * @param drawable Needed, but unused parameter.
	  * @see <a href = "http://goo.gl/ma2UdA" target = "_blank">GLEventListener
	  * </a>
	  **/
	public void display(GLAutoDrawable drawable)
	{
		/* Clear the buffer. */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		this.drawBoard();
		
		this.nextCycle();
		
		gl.glFlush();
		
		/* Pause the animation for 1s. */
		try { Thread.sleep(125); }
		catch(Exception e){}
	}
	
	private void drawBoard()
	{
		for(int r = 0; r < this.board.length; r++)
		{
			for(int c = 0; c < this.board[0].length; c++)
			{
				if(this.board[r][c])
				{
					gl.glBegin(GL.GL_POINTS);
						gl.glVertex2i(c*10+5, r*10+5);
					gl.glEnd();
				}
			}
		}
	}
	
	private void nextCycle()
	{
		boolean[][] nextBoard = new boolean[this.board.length]
			[this.board[0].length];
			
		for(int r = 0; r < this.board.length; r++)
		{
			for(int c = 0; c < this.board[0].length; c++)
			{
				byte live_neighbors = countLiveNeighbors(r, c);
					
				if((this.board[r][c] && (live_neighbors == 2 || 
					live_neighbors == 3)) || (!this.board[r][c] &&
					live_neighbors == 3))
				{
					nextBoard[r][c] = true;
				}
				// if(this.board[r][c] && 
					// (live_neighbors < 2 || live_neighbors > 3))
				else
				{
					nextBoard[r][c] = false;
				}
				
			}
		}
		
		this.board = nextBoard;
	}
	
	private byte countLiveNeighbors(int r, int c)
	{
		byte live_neighbors = 0;
		
		live_neighbors += (r > 0 && this.board[r-1][c]) ? (1):(0);
		live_neighbors += (r < this.board.length-1 && this.board[r+1][c]) ? 
			(1):(0);
		live_neighbors += (c > 0 && this.board[r][c-1]) ? (1):(0);
		live_neighbors += (c < this.board[0].length-1 && this.board[r][c+1]) ? 
			(1):(0);
			
		live_neighbors += (r > 0 && c > 0 && this.board[r-1][c-1]) ? (1):(0);
		live_neighbors += (r < this.board.length-1 && c > 0 && 
			this.board[r+1][c-1]) ? (1):(0);
		live_neighbors += (r > 0 && c < this.board[0].length-1 && 
			this.board[r-1][c+1]) ? (1):(0);
		live_neighbors += (r < this.board.length-1 && 
			c < this.board[0].length-1 && this.board[r+1][c+1]) ? (1):(0);
			
		return live_neighbors;
	}
}