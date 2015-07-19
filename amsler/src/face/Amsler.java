/**
 * 
 */
package face;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
/**
 * @author Anindya Dutta
 *
 */
public class Amsler extends JFrame{

	//JFrame is the Frame in which the entire software exists. Extending it means that we want a JFrame to hold our stuff.
	/**
	 * Amsler Grid Software
	 */
	private static final long serialVersionUID = 887142260258233188L; //version ID: dummy.
	
	int arr[][],arr2[][],c,d,stX,stY,enX,enY,val; //All explanations of variables given as and when used.
	float th; //th is the thickness of the line. Varies 1 unit for thin wavy lines, and 5 units for thick white patches.
	Robot robot; //declared public so that it can be accessed anywhere in the class. Note about it in the constructor.
	
	public void paint(Graphics g)
	{
		
		//The thing with paint() is that it redraws the entire screen every time. This is kind of tedious, but a simple approach,
		//where we need not save the graphical data over and over again. So we redraw the entire grid each time.
		
		Graphics2D g2=(Graphics2D)g; 	//this will be used for the zigzag stroke, explained later.
		g.clearRect(0, 0, 380, 380);	//clearing the entire grid so we can redraw.
		setBackground(Color.white);		//white background
		g.setColor(Color.black);		//black spots and black lines, hence the foreground is black, initially
		
		//To start with the painting, we draw the graph.
		for(int i=0;i<380;i+=38) //increase i by 38 each time. 38=1cm, the black lines are a centimeter apart, so increase by 38.
		{
			g.setColor(Color.black);
			g.drawLine(0, i, 379, i); //this draws the deep black line.
			
			g.setColor(Color.lightGray); //set the color to light gray for the light lines
			
			for(int j=i+1;j<i+38;j=j+4) //draw the light lines. 
										//increasing by 4. We need 10mm, 4*10 = 38, approximately, so this is an approximation			
				g.drawLine(0, j, 379, j);
			
		}
		
		//Now the vertical lines of the graph. Not commenting, follows same as above.
		for(int i=0;i<380;i+=38)
		{
			g.setColor(Color.black);
			g.drawLine(i, 0, i, 379);
			
			g.setColor(Color.lightGray);
			for(int j=i+1;j<i+38;j=j+4)
				g.drawLine(j, 0, j, 379);
			
		}
		
		//Draw the circle in the middle of the grid.
		g.setColor(Color.black);
		g.fillOval(185, 185, 10, 10); //(185,185) midpoint approximately. Diameter of 10 is used so that circle is visible.
		
		g.setColor(Color.BLACK);
		//Draw all the black spots, using the values from arr.
		for(int i=0;i<c;i++)
			g.fillOval(arr[i][0], arr[i][1], 10, 10);
		
		
		//Draw all the lines.
		for(int i=0;i<d;i++) {
			
			int x=arr2[i][0],y=arr2[i][1],z=arr2[i][2],w=arr2[i][3],val=arr2[i][4];
			//this means that the coordinates are (x,y) and (z,w) respectively.
			
			if(val==1) { //wavy line.
				g.setColor(Color.black); 
				th=1.0f; //the thickness is one unit to make thin lines.
			}
			else if(val==2) {
				g.setColor(Color.white);	
				th=5.0f; //the thickness is five to make white patches.
			}
			
			//make a wavy line
			final ZigzagStroke zig=new ZigzagStroke((Stroke)new BasicStroke(th), 4.0f, 2.0f); //using the second class. Refer to that.
			g2.setStroke(zig);
			g2.drawLine(x,y,z,w);
		}
	}
	
	public Amsler()
	{
		
		//From main() we come here.
		
		try {	
			robot = new Robot();
			//Robot is an AWT object. In this class, the function of this Robot is to capture the screenshot later.
			//We have declared robot as a public because we are initializing in this function but will use it later
			//somewhere else.
		}	
		catch(AWTException e) {
			System.out.println(e);
			//Just in case some error comes, I am printing it here. So far, there has been no error, but it is
			//advisable to keep this in a try-catch block.
		}
		
		setSize(380,380); //setting the size to 380 x 380, so that it matches the 10cm x 10cm.
		setResizable(false); //We always want it to be 10cm x 10cm, and therefore we cannot allow maximizing or 
							//resizing of the window.
		
		setLayout(new FlowLayout()); //setting a flow layout just in case we later add any buttons to this so that it works
									//fine. During the initial stages, there were buttons to choose between the different actions.
									//Hence this line was there. In case we put buttons again, I have not removed this line.
		
		arr=new int[500][2]; //arr is used to hold the black spots. Maximum of 500 spots can be recorded in one sitting.
							 //Logically, there is no space for 500 black spots in a 10cm x 10cm grid, so this is a way upper limit.
							 //2 columns: for x and y coordinates
		
		arr2=new int[500][5]; //arr2 is used to hold the wavy lines and white patches.
							  //5 columns to store (x1,y1,x2,y2,type) where (x1,y1) and (x2,y2) are the end points of the line
							  //and type denotes whether it is a wavy line or a white patch.
		
		c=d=0;
	
		addMouseListener(new MouseAdapter() {	
			//Here we are adding the MouseListener. This means that this part of the code is the one which
			//deals with all the mouse drags and mouse clicks and will be used to draw the stuff on the grid.
			
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton()==1) //1 denotes left mouse button, 2 denotes right mouse button.
				  val=1;
				else val=2;
				//so if we press the left mouse button or the right mouse button, it might mean we are about to drag.
				//The variable 'val' -> 'isLeftButtonPressedOrRight'. If the value is 1, it means left button is pressed,
				//if it is 2 it means right button is pressed.
				
				//We need to store the points in stX and stY (startX and startY) which is one of the end points of the line.
				//(stX,stY) are the coordinates of one end point.
				stX=arg0.getX();
				stY=arg0.getY();
			}
			
			public void mouseReleased(MouseEvent arg0) {
				
				//This is called when the mouse is released. Clearly we have dragged it and released it after a while.
				//In this case we will get another point, the point at which the mouse was released.
				//This is the end point, stored in (enX,enY).
				enX=arg0.getX();
				enY=arg0.getY();
				
				//arr2 will now have stX,stY,enX,enY and val saved.
				arr2[d][0]=stX; arr2[d][1]=stY; arr2[d][2]=enX; arr2[d][3]=enY; arr2[d++][4]=val;
				//the index d is used to keep track of the number of entries of lines.
				//Hence the value of d denotes the number of lines drawn on the grid.
				
				repaint(); //Whenever we call repaint(), the function 'public void paint(Graphics)' is called. So
						   //the flow moves to the painting, where we will now paint this line that we just captured.
			}
			
			public void mouseClicked(MouseEvent arg0) {
				
				val=0;	//To check if a point has been made the latest entry into the graph.
				
				int x=arg0.getX(),y=arg0.getY(); //getting the coordinates where the spot has been clicked.
				
				arr[c][0]=x-10;
				arr[c++][1]=y-10;  
				//The coordinates being stored are (x-10,y-10). This is because in Java, when we draw a circle,
				//we assume that the circle is being circumscribed by a square. We then mention the coordinates
				//of the top left point of the square and the diameter. In this case, these values are an useful approximation.
				
				repaint(); //read comment above.
			}
		});
		
		addKeyListener(new KeyAdapter() {
			//KeyListener is to listen to keyboard interactions. We want to enable two interactions here.
			
			public void keyPressed(KeyEvent e) { //whenever a key is pressed, we will get this event.
				
				//We will check is Ctrl is pressed.
				if(e.isControlDown())
				{
					//If it is, then we need to remove the last entry in the respective table.
					
					//If it is a point, decrease c, otherwise decrease d. In both cases, check if there is at least one entry.
					if(val==0 && c>0) c--;
					else if (d>0) d--;
					
					repaint(); //repaint after removing this entry.
				}
				if(e.getKeyCode()==KeyEvent.VK_F1)
					new JFrame().setVisible(true); //for future purposes. If F1 is pressed, we would like to open a Help Window.
													//Currently, we are opening a dummy window.
				}
			});
		
		addWindowListener(new WindowAdapter()
		{
			//This is to capture interactions with the window.
			//The basic interaction with the window we need is the close button.
			//Whenever we close the app, we want the save popup to come and we can save it with the name we want.
			
			public void windowClosing(WindowEvent we)
			{
				//this function is called when the close button is pressed. Or Alt+f4.
				
				final Point p=getLocationOnScreen(); //getting the location of the top left corner of the window on the screen.
				
				final BufferedImage i=robot.createScreenCapture(new Rectangle(p, new Dimension(380,380)));
				//From the top left corner, we need to capture 380 x 380 pixels, which is the size of the Amsler grid.
				//We are saving this image in i for now. We will later parse it into a PNG image.
				
				//Now we will make the Save As box dynamically.
				JFrame f = new JFrame("Save As");
				f.setSize(500,80); //The size of the save as box.
				f.setLayout(new GridLayout(1,3)); //There are three items: the label, the text box, and the button, so three columns.
				f.add(new JLabel("Name")); //adding the name.
				final JTextField tb;	//making the Text box.
				JButton b; //making the button.
				f.add(tb=new JTextField(100)); //adding the TextBox with width 100.
				f.add(b=new JButton("Save"));	//adding the button with text "Save".
				
				b.addActionListener(new ActionListener() {
					//This is called whenever the button is clicked.
					
				public void actionPerformed(ActionEvent arg0) {
					try  {
						String n=tb.getText()+".png"; //We are taking the text in the name field and adding .png to make
													  //a proper PNG filename.
						
						ImageIO.write(i,"png", new File(n)); //take the image file, convert it to "png", save it with name n from textfield.
						System.out.println("made file hello"); //Log to check the file was made.
						System.exit(0); //closing the window. Since we've saved, we should close the app.
					}
					 catch(Exception ex)  {
						System.out.println(ex);      // just in case any IO error occurs, such as bad file name.
					 }
				}});
					
				f.setVisible(true); //Now that we've made the Save As dialog box and defined all the functions, we make it visible.
		 }	 
		});	
		
		repaint(); //repainting in the constructor.
	}	
	
	public static void main(String args[]) {
		
		// All programs start with the main(), so here is where the Amsler grid code starts off.
		
		
		//SwingUtilities is used because we are using Java Swing to make the window.
		SwingUtilities.invokeLater(new Runnable()	{
			//new Runnable() is used to make an anonymous Thread class.
			//Each thread class has a run() function. When we use this run() function, 
			//the part inside it runs in a separate thread, parallely.
			public void run()	{
				new Amsler().setVisible(true);
				// here we call the Amsler() constructor, which makes the Amsler grid window.
				// However, the window is hidden by default. So we are setting its visibility to true,
				// so that we can see it.
				
				//new Amsler() call will call the function public Amsler(). So the code flow is main() -> Amsler().
			}
		});
	}
}