package android.widget;

import com.example.mecanumdrive.R;
import com.robotopen.RobotOpenRobot;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class JoystickListener extends Activity implements OnTouchListener {
	View _view;
	private int _xDelta;
	private int _yDelta;
	int recentX;
	int recentY;
	int leftM;
	int topM;
	int x;
	int y;
	RobotOpenRobot robot;
	Context context;
	ToggleButton enable , connect;
	String ip;
	TextView leftY, leftX, rightX;
	
	//Creates a constructor that takes all of the objects we will be using from the main activity
	public JoystickListener (View view, RobotOpenRobot robot, Context mContext, ToggleButton connect, ToggleButton enable, String ip , 
			TextView leftY, TextView leftX, TextView rightX) {
		
		this._view = view;
		this.robot = robot;
		this.context = mContext;
		this.connect = connect;
		this.enable = enable;
		this.ip = ip;
		this.leftY = leftY;
		this.leftX = leftX;
		this.rightX = rightX;
		
	}
	
	
	//When the view is clicked
	public boolean onTouch(View view, MotionEvent event) {
		//The action of the evente.g. touch
        final int action = event.getAction();
        
        //The X value of the event
        final int X = (int) event.getRawX();
        
        //the Y value of the event
        final int Y = (int) event.getRawY();
        switch (action & MotionEvent.ACTION_MASK) {
        	
        	//On first touch
            case MotionEvent.ACTION_DOWN: 
            	
            	//Get the parameters of the view i.e. width and height
             	RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
             	
             	//Is the touch inside the parameters
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            

            case MotionEvent.ACTION_MOVE:
				
            	RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
				
				//Follow the touch event
				leftM = X - _xDelta;
				topM = Y - _yDelta;
				layoutParams.rightMargin = -250;
				layoutParams.bottomMargin = -250;
	            
				//This helps keep the thumb in the large circle
				if(leftM < 0) {
				
					leftM = 0;
				
				} else if (leftM > 220) {
				
					leftM= 220;
				
				}
			
				if(topM < 0) {
				
					topM = 0;
				
				} else if (topM > 220) {
				
					topM = 220;
				
				}
			 
				/*These `if statements` re-map each quadrant onto a new coordinate plane to keep the 
				thumb in the larger circle using the equation of a circle. */
				
				//Quadrant I
				if (topM < 110 && leftM > 110 ){
				 
					x = (int) (leftM - 110);
					y = (int) (110 - topM);
			 
				//Quadrant II
				} else if (topM < 110 && leftM < 110) {
				 
					x = (int) (leftM - 110);
					y = (int) (110 - topM);
				 
				//Quadrant III
				} else if ( topM > 110 && leftM < 110) {
				 
					x = (int) (leftM - 110);
					y = (int) -(topM - 110);
				 
				//Quadrant IV 
				} else if (topM > 110 && leftM > 110){
				 
					x = (int) (leftM - 110);
					y = (int) -(topM - 110);
			
				//Origin
				} else {
				 
					layoutParams.leftMargin = leftM;
					layoutParams.topMargin = topM;
				 
				}
				
				/* Now that the x and y values are mapped on a new scale, we can use the equation of a circle
				 * (x^2 + y^2 = r^2) to see if the touch event is inside of the circle. If it is not then we 
				 * save the last recorded values of x and y that were inside the circle and keep the thumb there. */
				if ((Math.pow(x, 2) + Math.pow(y, 2)) <= 12100) {
				 
					recentX = leftM;
					recentY = topM;
					layoutParams.topMargin = topM;
					layoutParams.leftMargin = leftM;
				 
				 
				} else{
				 
					layoutParams.leftMargin = recentX;
					layoutParams.topMargin = recentY;
				 
				}
				
				try { 
					
			 		//Set the layout parameters to what we defined earlier.
					view.setLayoutParams(layoutParams);
					
					//Get the R.id of the view
					switch (view.getId()) {
					
						//If the view pressed is the Left Thumb
					 	case R.id.thumbL: 
					 
					 		if (layoutParams.topMargin < 110) {
					 			robot.joystick1.LeftY = (byte) (layoutParams.topMargin);
					 			leftY.setText("Left Y: " +  (255 - layoutParams.topMargin));
					 		} else {
					 			
					 			//This lets the max Y value be 255
					 			robot.joystick1.LeftY = (byte) (layoutParams.topMargin + 37);
					 			leftY.setText("Left Y: " + (255 - (layoutParams.topMargin +  37)));
					 		}
					 		
					 		if (layoutParams.leftMargin < 110) {
					 			
					 			robot.joystick1.LeftX = (byte) (layoutParams.leftMargin);
					 			leftX.setText("Left X: " + (layoutParams.leftMargin));
					 			
					 		} else {
					 			
					 			//This lets the max X value be 255
					 			robot.joystick1.LeftX = (byte) (layoutParams.leftMargin + 37);
					 			leftX.setText("Left X: " + (layoutParams.leftMargin + 37));
					 			
					 		}
							System.out.println("Left X Value: " + (layoutParams.leftMargin + 37));
							System.out.println("Left Y Value: " + layoutParams.topMargin + 37);
							break;
					 	
						//If the right thumb is pressed
					 	case R.id.thumbR: 
		                	view.setLayoutParams(layoutParams);
		                	if (layoutParams.leftMargin < 110) {
		                		
		                		robot.joystick1.RightX = (byte) (layoutParams.leftMargin);
		                		rightX.setText("Right X: " + (layoutParams.leftMargin));
		                		
		                	} else {
		                		
		                		//This lets the max X value be 255
		                		robot.joystick1.RightX = (byte) (layoutParams.leftMargin + 37);
		                		rightX.setText("Right X: " + (layoutParams.leftMargin +  37));
		                		
		                	}
		                	
		                	System.out.println(" Right X Value: " + layoutParams.leftMargin +37);
							break;
					 	
					 }
				//If you can't do the about, catch the null pointer exception and do this:	
				} catch (NullPointerException e) {

					layoutParams.leftMargin = 110;
					layoutParams.topMargin = 110;
					view.setLayoutParams(layoutParams);
					
				}
			break;
		
                 
        	//When the touch event is done             
            case MotionEvent.ACTION_UP:
               
          
	           RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	           
	           //Set the thumb and text to neutral
	     	   LayoutParams.leftMargin = 110;
	     	   LayoutParams.topMargin = 110;
	     	   view.setLayoutParams(LayoutParams);
	     	   leftY.setText("Left Y: NEUTRAL");
	     	   leftX.setText("Left X: NEUTAL");
	     	   rightX.setText("Right X: NEUTAL");
	     	   
	     	   //Try to set the wheels to neutral
	     	   try {
            		
	     		   robot.joystick1.LeftY = (byte) (127);
		           robot.joystick1.LeftX = (byte) (127);
		           robot.joystick1.RightX = (byte) (127);

		          //If not, catch the exception
            	} catch (NullPointerException e) {
        			
        			int duration = Toast.LENGTH_SHORT;
        			
        			if(connect.isChecked() == false) {

        				Toast.makeText(context, "Please Connect", duration).show();
        				
        			} else if (enable.isChecked() == false) {
        				
        				Toast.makeText(context, "Please Enable", duration).show();
        				
        			} else {
        				
        				Toast.makeText(context, "No Connection. Check IP Address OR Check That Your Robot Is On ", Toast.LENGTH_LONG).show();
        				
        			} 
            	}
		     	   break;
		}
		return true;
	}
}