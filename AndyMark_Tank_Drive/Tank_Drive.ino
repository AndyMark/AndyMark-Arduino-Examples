/*--------------------------------------------------------------
|   This code is for the Arduino Ethernet and follows with the |
|   "Arduino Tutorial - Tank Drive" provided by AndyMark.      |
|   This program utilizes your robot  to use Tank Drive.       |
|   It may be useful to download the "Mecanum Drive" program   |
|   from AndyMark as it is a good example program if you have  |
|   mecanum wheels.                                            |
----------------------------------------------------------------
    Needed Parts:
      -TBD
--------------------------------------------------------------*/ 

#include <SPI.h>
#include <Ethernet.h>
#include <Servo.h>
#include <EEPROM.h>
#include <RobotOpen.h>
#define NEUTRAL 127
#define FORWARD 255
#define REVERSE 0 
#define FORWARD_MS 2000
#define REVERSE_MS 1000
#define NEUTRAL_MS 1500


//Declaring which joystick we are using
ROJoystick usb1(1);

//Declaring that we have two Servos
Servo leftRear;
Servo leftFront;
Servo rightRear;
Servo rightFront;

//Delay time for calibration
int d = 250;

//IP Address of the Arduino
IPAddress ip ( , , , );     //<=== Set IP Address

void setup() {
  
  //Identify which digital pins the servos are using
  rightRear.attach(5);
  rightFront.attach(6);
  leftRear.attach(3);
  leftFront.attach(9);
  
  //This function makes sure the speed controller and the Arduino understand each other. 
  calibrate();
  
  //Communicates with the RobotOpen Driver Station
  RobotOpen.setIP(ip);
  
  //Begins the RobotOpen tasks
  RobotOpen.begin(&enabled, &disabled, &timedtasks);
} 

void enabled() {
 
 //Records the value of the left analog stick on our selected joystick.
 //The motors move slightly slower when moving in reverse than when moving forward. The bias() function takes care of this problem.
 //Adjustmants may be needed depending on your setup.  
 int valL = bias(usb1.leftY());
 int valR = bias(usb1.rightY());

 //Maps valL and valR on a scale of 1000 - 2000. 
 //The first two numbers are your high and low for the former scale and the last two are the high and low for the new scale. 
 int spdL = map (valL, 0, 255, 1000, 2000);
 int spdR = map (valR, 0, 255, 1000, 2000);
 
 //The value becomes our wheel speeds 
 leftFront.writeMicroseconds(spdL);
 leftRear.writeMicroseconds(spdL);
 rightFront.writeMicroseconds(spdR);
 rightRear.writeMicroseconds(spdR);
}


void disabled() {
    // safety code
    leftRear.writeMicroseconds(NEUTRAL_MS);
    leftFront.writeMicroseconds(NEUTRAL_MS);
    rightRear.writeMicroseconds(NEUTRAL_MS);
    rightFront.writeMicroseconds(NEUTRAL_MS);
}

void timedtasks() {
  //Publishing the values of the analog sticks to the RobotOpenDS
  RODashboard.publish("usb1.leftY()", usb1.leftY());
  RODashboard.publish("usb1.leftX()", usb1.leftX());
  RODashboard.publish("usb1.rightY()", usb1.rightY());
  RODashboard.publish("usb1.rightX()", usb1.rightX());
  RODashboard.publish("Uptime Seconds", ROStatus.uptimeSeconds());
}

void calibrate() {
  //Calibrating the Contorllers. See Appendix A in the Arduino Tutorial for more info
  leftRear.writeMicroseconds(NEUTRAL_MS);
  leftFront.writeMicroseconds(NEUTRAL_MS);
  rightRear.writeMicroseconds(NEUTRAL_MS);
  rightFront.writeMicroseconds(NEUTRAL_MS);
  delay(d);
  leftRear.writeMicroseconds(FOWARD_MS);
  leftFront.writeMicroseconds(FOWARD_MS);
  rightRear.writeMicroseconds(FOWARD_MS); 
  rightFront.writeMicroseconds(FOWARD_MS); 
  delay(d);
  leftRear.writeMicroseconds(REVERSE_MS);
  leftFront.writeMicroseconds(REVERSE_MS);
  rightRear.writeMicroseconds(REVERSE_MS);
  rightFront.writeMicroseconds(REVERSE_MS);
  delay(d);
  leftRear.writeMicroseconds(NEUTRAL_MS);
  leftFront.writeMicroseconds(NEUTRAL_MS);
  rightRear.writeMicroseconds(NEUTRAL_MS);
  rightFront.writeMicroseconds(NEUTRAL_MS);
}

//We declare that we have a function that is going to take an integer
int bias (int motor){
  
  //The motors move around 15% slower when in reverse.
  float reverseBias = .85;
  
  //If the motor is moving forward we want to slow it down by 15%
  if(motor>NEUTRAL){
    //Maps a new scale that will match the speed of the motors running in reverse
    //SEE APPENDIX C IN THE TUTORIAL FOR MORE INFO
    return map(motor, REVERSE , FORWARD, (FORWARD*(1.0-reverseBias)), (FORWARD*reverseBias));
  
  }else {
    //Maps a new scale that will match the speed of the motors running in reverse
    return motor;
  }
}

void loop() {
 //Continually communicates with the Robot Open Driver Station
 RobotOpen.syncDS();
}

