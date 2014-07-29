package com.example.tankdrive;

/*
 *		This app is to be used with an Arduino Ethernet.
 *		It was developed by AndyMark and was created in
 *		with Arduino programs that can be found at
 *		AndyMark.com. The passphrase for pushing 
 *		is "bgarland".
 *									*/


import android.app.Activity;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.OnChangeVerticalSeekBarListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VerticalSeekBar;

import com.robotopen.*;

public class MainActivity extends Activity {

	TextView txtLeft, txtRight;
    ToggleButton enable , connect;
    EditText text;
    String ip;
    RobotOpenRobot roboCop;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		//Create saved instances
        super.onCreate(savedInstanceState);
		
		//Set the Layout up
        setContentView(R.layout.activity_main);
 
		//Declare two of our buttons
		connect = (ToggleButton) findViewById(R.id.buttonConnect);
		enable = (ToggleButton) findViewById(R.id.buttonEnable);
		
		//Localize the two text boxes
		txtLeft = (TextView) findViewById(R.id.txtDebug);
		txtRight = (TextView) findViewById(R.id.txtRight);
		
		//Text = the IP text box
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
        	
        	if (enable.isChecked()) {
        		
        		System.out.println("Enabled");
        		roboCop.enable();
        		
        	}
			
        	System.out.println("Out of Connect statement");
        	
			//Keep the keyboard hidden
        	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	
    	    txtLeft.setText("NEUTRAL");
    	    txtRight.setText("NEUTRAL");
    	    
 
        } else{
	       
        	System.out.println("Not Saved Instance statement");
        	
			//Notify them that they must put an IP ADDRESS
			text.setError("Field cannot be blank.");
			
        }
        
        System.out.println("Setting up OnChangeListeners statement");
        
	    //Set the OnChangeListeners up 
        VerticalSeekBar seekL = (VerticalSeekBar) findViewById(R.id.seekBarL);
        seekL.setOnSeekBarChangeListener(new OnChangeVerticalSeekBarListener(txtLeft , roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip));
        VerticalSeekBar seekR = (VerticalSeekBar) findViewById(R.id.seekBarR);
        seekR.setOnSeekBarChangeListener(new OnChangeVerticalSeekBarListener(txtRight , roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip));
      }

    public void onToggleClicked(View view) {
    	
    	switch(view.getId()) {
    	
    		case R.id.buttonConnect:
    			
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
    		break;	

    				    	
    			
    		case R.id.buttonEnable:
    			
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
		    		        VerticalSeekBar seekL = (VerticalSeekBar) findViewById(R.id.seekBarL);
		    		        seekL.setOnSeekBarChangeListener(new OnChangeVerticalSeekBarListener(txtLeft , roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip));
		    		        VerticalSeekBar seekR = (VerticalSeekBar) findViewById(R.id.seekBarR);
		    		        seekR.setOnSeekBarChangeListener(new OnChangeVerticalSeekBarListener(txtRight , roboCop, MainActivity.this.getApplicationContext(), connect, enable, ip));
	    					
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
    		break;	
    			
    		default:
    		
    		break;	

    		}
    	
    	}

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
    
    

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  
		super.onSaveInstanceState(savedInstanceState);
		String strIp;
	
	//You may need to disable the check on this statment
	//Hint: Right-Click on yellow sign and select "disable check"	
	if (ip != null && !ip.isEmpty()) {
		
		EditText text = (EditText)findViewById(R.id.editText1);
		strIp = text.getText().toString();
		
	} else{ 
		
		strIp = null;
		
	}
        ToggleButton connect = (ToggleButton) findViewById(R.id.buttonConnect);
        ToggleButton enable = (ToggleButton) findViewById(R.id.buttonEnable);
		boolean blnConnect = connect.isChecked();
		boolean blnEnable = enable.isChecked();
		
		savedInstanceState.putString("ip", strIp);   
		savedInstanceState.putBoolean("Connect", blnConnect); 
		savedInstanceState.putBoolean("Enable", blnEnable);   
	}
}