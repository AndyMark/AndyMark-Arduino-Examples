/*--------------------------------------------------------------
|   This code is for the Arduino Ethernet and follows with the |
|   "Arduino Tutorial - Mecanum Drive" provided by AndyMark.   |
|   It may be useful to download the "Tank Drive" program from |
|    AndyMark as it contains useful explanations inside.       |
|                                                              |
|  Email: arduinoquestions@andymark.com                        |
----------------------------------------------------------------   
    Needed Parts:
       - TBD
---------------------------------------------------------------*/  
  
  
#include <SPI.h>
#include <Ethernet.h>
#include <Servo.h>
#include <EEPROM.h>
#include <RobotOpen.h>



#define FORWARD 255
#define REVERSE 0
#define NEUTRAL 127
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

//FOR DEBUG
int front_left = 0;
int rear_left = 0;
int front_right = 0;
int rear_right = 0;




//IP Address of the Arduino
IPAddress ip ( , , ,  );     //<=== Set IP Address!!

void setup(){  
  
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
  //Matching the motors to fit with the analog sticks. 
  int drive  = (FORWARD - usb1.leftY()) - NEUTRAL;
  int strafe = usb1.leftX() - NEUTRAL;
  int rotate = usb1.rightX() - NEUTRAL;
  
   front_left  = drive + strafe + rotate + 126;
   rear_left   = drive - strafe + rotate + 126;
   front_right = drive - strafe - rotate + 126;
   rear_right  = drive + strafe - rotate + 126;
  
  //Localize to motor
  leftFront.writeMicroseconds(bias_ms(maximum(front_left)));
  leftRear.writeMicroseconds(bias_ms(maximum(rear_left)));
  rightFront.writeMicroseconds(bias_ms(maximum(FORWARD-front_right)));
  rightRear.writeMicroseconds(bias_ms(maximum(FORWARD-rear_right)));
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
  
  RODashboard.publish("front_left", front_left);
  RODashboard.publish("rear_left", rear_left);
  RODashboard.publish("front_right", front_right);
  RODashboard.publish("rear_right", rear_right);
  
  RODashboard.publish("Uptime Seconds", ROStatus.uptimeSeconds());
}

void calibrate() {
  //Calibrating the Contorllers. See Appendix A in the Arduino Tutorial for more info
  leftRear.writeMicroseconds(NEUTRAL_MS);
  leftFront.writeMicroseconds(NEUTRAL_MS);
  rightRear.writeMicroseconds(NEUTRAL_MS);
  rightFront.writeMicroseconds(NEUTRAL_MS);
  delay(d);
  leftRear.writeMicroseconds(FORWARD_MS);
  leftFront.writeMicroseconds(FORWARD_MS);
  rightRear.writeMicroseconds(FORWARD_MS); 
  rightFront.writeMicroseconds(FORWARD_MS); 
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
int bias_ms (int motor){
  //The motors move around 15% slower when in reverse.
  float reverseBias = .85;
  
  //If the motor is moving forward we want to slow it down by 15%
  if(motor>NEUTRAL){
    
    //Maps a new scale that will match the speed of the motors running in reverse
    return map(motor, REVERSE , FORWARD, (FORWARD_MS*(1.0-reverseBias)+1000), (FORWARD_MS*reverseBias));
    
  }else {
    
    //Maps a new scale that will match the speed of the motors running in reverse
    return map(motor, REVERSE, FORWARD, REVERSE_MS, FORWARD_MS);
    
  }
}

//this function makes sure the speed never goes above full forward or below full reverse.
int maximum (int motor) {
  
 if (motor > FORWARD) {
   return motor = FORWARD;
   
 }else if (motor < REVERSE){
   return motor = REVERSE; 
   
 }else {
   return motor;
 } 
 
}

void loop() {
  //Continually communicates with the Robot Open Driver Station
  RobotOpen.syncDS();
} 
