package android.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.tankdrive.MainActivity;
import com.example.tankdrive.R;
import com.robotopen.*;

public class OnChangeVerticalSeekBarListener extends Activity implements OnSeekBarChangeListener{
	
	TextView debugOut = null;
	RobotOpenRobot robot;
	Context context;
	ToggleButton enable , connect;
	String ip;

	
	public OnChangeVerticalSeekBarListener (TextView debugOut, RobotOpenRobot robot, Context mContext, ToggleButton connect, ToggleButton enable, String ip) {
		
		this.debugOut = debugOut;
		this.robot = robot;
		this.context = mContext;
		this.connect = connect;
		this.enable = enable;
		this.ip = ip;
	}
	
	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
			switch (seekBar.getId()) {
				
				case R.id.seekBarL:
				
					try {
						if (progress > 255) {
							
							debugOut.setText("255");
							robot.joystick1.LeftY = (byte) 0;
							
						} else if (progress < 0) {
							
							debugOut.setText("0");
							robot.joystick1.LeftY = (byte) 255;
							
						} else {
							
							debugOut.setText(Integer.toString(progress));
							System.out.println(" " + Integer.toString(progress));
							robot.joystick1.LeftY = (byte) (255 - progress);
							
						}
					} catch (NullPointerException e) {
						
						debugOut.setText("NEUTRAL");
						seekBar.setProgress(127);
						
					}
				break;
		
				case R.id.seekBarR:
					try {
						if (progress > 255) {
							
							debugOut.setText("255");
							robot.joystick1.RightY = (byte) 0;
							
						} else if (progress < 0) {
							
							debugOut.setText("0");
							robot.joystick1.RightY = (byte) 255;
							
						} else {
							
							debugOut.setText(Integer.toString(progress));
							System.out.println(" " + Integer.toString(progress));
							robot.joystick1.RightY = (byte) (255 - progress);
							
						}	
					} catch (NullPointerException e) {
						
						debugOut.setText("NEUTRAL");
						seekBar.setProgress(127);
						
					}
						
					break;
		
				default:
					break;
		
				}
			
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	
		debugOut.setText("NEUTRAL");
		seekBar.setProgress(127);
		
		try{
			robot.joystick1.RightY = (byte) (127);
			robot.joystick1.LeftY = (byte) (127);
		}  catch (NullPointerException e) {
			
			int duration = Toast.LENGTH_SHORT;
			
			if(connect.isChecked() == false) {

				Toast.makeText(context, "Please Connect", duration).show();
				
			} else if (enable.isChecked() == false) {
				
				Toast.makeText(context, "Please Enable", duration).show();
				
			} else {
				
				Toast.makeText(context, "No Connection. Check IP Address OR Check That Your Robot Is On ", Toast.LENGTH_LONG).show();
				
			}
		}
	}

}
