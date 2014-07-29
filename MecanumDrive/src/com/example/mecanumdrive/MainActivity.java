package com.example.mecanumdrive;

/*
 *		This app is to be used with an Arduino Ethernet.
 *		It was developed by AndyMark and was created in
 *		with Arduino programs that can be found at
 *		AndyMark.com. The passphrase for pushing 
 *		is "bgarland".
 *									*/

import com.example.mecanumdrive.R;
import com.robotopen.RobotOpenRobot;
import android.app.Activity;
import android.widget.EditText;
import android.widget.JoystickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class MainActivity extends Activity {
    
	ToggleButton enable , connect;
    EditText text;
    String ip;
    RobotOpenRobot roboCop;
    View thumbL, thumbR;
    ViewGroup rectangleL, rectangleR;
    TextView leftY, leftX, rightX;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		//Set the Layout up
	    setContentView(R.layout.activity_main);

	    thumbL = (View) findViewById(R.id.thumbL);
	    thumbR = (View) findViewById(R.id.thumbR);
	    rectangleL = (ViewGroup) findViewById(R.id.rlvL);
	    rectangleR = (ViewGroup) findViewById(R.id.rlvR);

 
		//Creates our connect and enable buttons
		connect = (ToggleButton) findViewById(R.id.buttonConnect);
		enable = (ToggleButton) findViewById(R.id.buttonEnable);
		
		//Creates our text boxes that display the speed
		leftY = (TextView) findViewById(R.id.leftY);
		leftX = (TextView) findViewById(R.id.leftX);
		rightX = (TextView) findViewById(R.id.rightX);
		
		//Create or ip address text field
		text = (EditText)findViewById(R.id.editText1);
		
        //if there is something in the save instance
        if (savedInstanceState != null) {
			
        	System.out.println("Saved statement");
        	
			//strVal == the previous IP
        	String strVal = savedInstanceState.getString("ip");
			
			//Set the two buttons to their previous state
        	connect.setChecked(savedInstanceState.getBoolean("Connect"));
        	enable.setChecked(savedInstanceState.getBoolean("Enable"));
        	
			//If the IP address had something in it, the connect button was checked, and the enable button was checked.
        	if (connect.isChecked()) {
				
        		//let me know we are in this if statement
        		System.out.println("Connected");
        		
				//set the text box to the previous IP Address
				text.setText(strVal);
				
				//Set IP too the strVal
	            String ip = (strVal);
				
				//Give our robot the IP address
	            roboCop = new RobotOpenRobot(ip);
				
        		//Connect too the robot
        		roboCop.connect();	
				
        	}
        	
        	//if the enable button is on
        	if (enable.isChecked()) {
        		
        		//Tell me we are in this statement
        		System.out.println("Enabled");
        		
        		//Enable the robot
        		roboCop.enable();
        		
        	}
			
        	//Tell me we are out of those two statments
        	System.out.println("Out of Connect statement");
        	
			//Keep the keyboard hidden
        	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	
        	//Set the text boxes to NEUTRAL
    	    leftY.setText("Left Y: NEUTRAL");
    	    leftX.setText("Left X: NEUTRAL");
    	    rightX.setText("Right X: NEUTRAL");
    	    
    	  //If there are no saved instances
        } else{
	       
        	System.out.println("Not Saved Instance statement");
        	
			//Notify them that they must put an IP ADDRESS
			text.setError("Field cannot be blank.");
			
        }
        
        System.out.println("Setting up OnChangeListeners statement");
        
	    //Set the OnChangeListeners up 
	    thumbL.setOnTouchListener(new JoystickListener(thumbL, roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip, leftY, leftX, rightX));
	    thumbR.setOnTouchListener(new JoystickListener(thumbR, roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip, leftY, leftX, rightX));
      }

    public void onToggleClicked(View view) {
    	
    	int id = view.getId();
		if (id == R.id.buttonConnect) {
			ip = text.getText().toString();
			//You may need to disable the check on this statment
			//Hint: Right-Click on yellow sign and select "disable check"
			if (ip != null && !ip.isEmpty()) { 
			
				 if(connect.isChecked()){
					try {
						
						//Take the text from editText1 and make it the IP Address
						roboCop = new RobotOpenRobot(ip);
						roboCop.connect();
						
					} catch (NetworkOnMainThreadException e) {
						
						CharSequence text1 = "Type A Correct IP Address OR Check Your Connection";
						int duration = Toast.LENGTH_LONG;
						Toast.makeText(getApplicationContext(), text1, duration).show();
						connect.setChecked(false);
					}
				
				} else {
					
					roboCop.disable();
					roboCop.disconnect();
					enable.setChecked(false);
				} 
			} else {
					
					connect.setChecked(false);
					CharSequence text1 = "Please Type An IP Address";
					int duration = 3;
					Toast.makeText(getApplicationContext(), text1, duration).show();
					
				}
		} else if (id == R.id.buttonEnable) {
			boolean click = ((ToggleButton) view).isChecked();
			try {	
				if (connect.isChecked() == false) {
					
					enable.setChecked(false);
					CharSequence text = "Please Connect First";
					int duration = 3;
					Toast.makeText(getApplicationContext(), text, duration).show();

				} else if (click) {
					
						roboCop.connect();
						roboCop.enable();
						
						//Set the OnChangeListeners up 
					    thumbL.setOnTouchListener(new JoystickListener(thumbL, roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip, leftY, leftX, rightX));
					    thumbR.setOnTouchListener(new JoystickListener(thumbR, roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip, leftY, leftX, rightX));
						
				} else {
					
						roboCop.disable();
					
					}	
			}catch (NullPointerException e) {
				
				CharSequence text = "Please Connect First";
				int duration = 3;
				Toast.makeText(getApplicationContext(), text, duration).show();
				ToggleButton enable = (ToggleButton) findViewById(R.id.buttonEnable);
				enable.setChecked(false);
			}
		} else {
		}
    	
    	}

    //When the app is stopped, disable and disconnect
    @Override
    public void onStop() {
    	
    	super.onStop();
    	if (enable.isChecked()) {
  
    		roboCop.disable();
    		enable.setChecked(false);
    	
    	}
    	
    	if (connect.isChecked()) {
    	
    		roboCop.disconnect();
    		connect.setChecked(false);
    		
    	} 	
    }
    
    
    //On landscape Change
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  
		super.onSaveInstanceState(savedInstanceState);
		String strIp;
	//Save the IP address if it contains an IP	
	//You may need to disable the check on this statment
	//Hint: Right-Click on yellow sign and select "disable check"
	if (ip != null && !ip.isEmpty()) {
		
		EditText text = (EditText)findViewById(R.id.editText1);
		strIp = text.getText().toString();
		
	} else{ 
		
		strIp = null;
		
	}
		//Save the state of the toggle buttons
        ToggleButton connect = (ToggleButton) findViewById(R.id.buttonConnect);
        ToggleButton enable = (ToggleButton) findViewById(R.id.buttonEnable);
		boolean blnConnect = connect.isChecked();
		boolean blnEnable = enable.isChecked();
		
		savedInstanceState.putString("ip", strIp);   
		savedInstanceState.putBoolean("Connect", blnConnect); 
		savedInstanceState.putBoolean("Enable", blnEnable);   
	}


	
}