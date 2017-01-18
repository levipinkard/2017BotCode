package org.usfirst.frc.team4580.robot;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	RobotDrive myRobot;
	Joystick stick;
	double wheelSize;
	SerialPort arduino;
	int autoLoopCounter;
    boolean joystickA;
    AnalogOutput anOut;
	boolean joystickB;
	boolean joystickX;
	boolean joystickY;
	boolean joystickLSB;
	boolean joystickRSB;
	boolean joystickLB;
	boolean joystickRB;
	double joystickLSX;
	double joystickLSY;
	double joystickRSX;
	double joystickRSY;
	double joyLeftOut;
	double joyRightOut;
	CameraServer camera;
	boolean slowBool;
	boolean interlock;
	byte testByte[];
	boolean interlock2;
	//Declares the Talon SRX motor controllers
	CANTalon leftFront;
	CANTalon rightFront;
	CANTalon leftRear;
	CANTalon rightRear;
	//Compressor pneumatic;
	Encoder rightEncode;
	Encoder leftEncode;
	//Second joystick for flight sticks
	Joystick stick2;
	//Variable to hold distance per pulse
	double encodeDistance;
	//Distance to travel, set by dashboard
	double autoGoDistance;
	PIDController turnController;
	AHRS ahrs;
	double encoderCompensate;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//chooser.addDefault("Default Auto", defaultAuto);
		//chooser.addObject("My Auto", customAuto);
		//SmartDashboard.putData("Auto choices", chooser);
		//Sets up Talons to respective CAN IDs	
		leftFront = new CANTalon(2);
		rightFront = new CANTalon(4);
		leftRear = new CANTalon(3);
		rightRear = new CANTalon(5);
		//Sets left motor to left and right Talons
		myRobot = new RobotDrive(leftFront,leftRear,rightFront,rightRear);
    	//Stick is right logitech flight stick, stick two is left flight stick
    	stick = new Joystick(1);
    	stick2 = new Joystick(0);
    	//Sets up variables to allow for slow mode
    	slowBool = false;
    	interlock = true;
    	interlock2 = true;
    	leftEncode = new Encoder(2, 3, false, EncodingType.k4X);
    	rightEncode = new Encoder(0, 1, false, EncodingType.k4X);
    	//If using pneumatics, this will set up compressor and enable it so that it fills itself
    	//pneumatic = new Compressor(0);
    	//pneumatic.setClosedLoopControl(true);
    	arduino = new SerialPort(9600, SerialPort.Port.kUSB);
    	//arduino.writeString("test");
        /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
        /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
        /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
        ahrs = new AHRS (SPI.Port.kMXP); 
    	//testByte = new byte[5];
        encoderCompensate = 0.94224842556;
        CameraServer.getInstance();
        camera.startAutomaticCapture("cam0", "cam0");
        
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		//autoSelected = chooser.getSelected();
		//autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		//System.out.println("Auto selected: " + autoSelected);
		rightEncode.reset();
    	leftEncode.reset();
		//Grabs wheelSize and autoGoDistance from the dashboard (hopefully)
		//wheelSize = SmartDashboard.getNumber("Wheel", 8);
    	//autoGoDistance = SmartDashboard.getNumber("Distance", 27);
		/*
		 * Calculates the distance per pulse by taking the wheel diameter * pi
    	 * to get the circumference and dividing that by 
    	 * the pulses per revolution
    	 */
    	encodeDistance = 7.4 * Math.PI / 270;
		rightEncode.setDistancePerPulse(encodeDistance);
		leftEncode.setDistancePerPulse(encodeDistance);
		rightEncode.setMinRate(.5);
		leftEncode.setMinRate(.5);
		//Drives forward until the robot goes the distance set by autoGoDistance
		while (Math.abs(rightEncode.getDistance()) <= 147) {
			//wheelSize = SmartDashboard.getNumber("Wheel", 8);
	    	//autoGoDistance = SmartDashboard.getNumber("Distance", 27);
			/*
			 * Calculates the distance per pulse by taking the wheel diameter * pi
	    	 * to get the circumference and dividing that by 
	    	 * the pulses per revolution
	    	 * */

			
			myRobot.tankDrive(-1 ,-1 * encoderCompensate);
			//Adds encoder values to dashboard during autonomous mode
			//SmartDashboard.putNumber("Auto Right Distance:", Math.abs(rightEncode.getDistance()));
			//SmartDashboard.putNumber("Auto Left Distance:", Math.abs(leftEncode.getDistance()));
		} 
		ahrs.reset();
		//double navAngle;
		//navAngle = Math.abs(ahrs.getAngle());
		//while (navAngle <  345) {
		//	navAngle = Math.abs(ahrs.getAngle());
		//	SmartDashboard.putNumber("Z axis:", ahrs.getAngle());
		//	myRobot.tankDrive(.6 * encoderCompensate, -.6);
		//}
			SmartDashboard.putNumber("Auto Right Rotations:", Math.abs(rightEncode.getRate()));
			SmartDashboard.putNumber("Auto Left Rotations:", Math.abs(leftEncode.getRate()));
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		/*switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			break;
		} */
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() throws IllegalMonitorStateException {
    	//Assigns various variables to current values of stick
		joystickA = stick.getRawButton(3);
        joystickB = stick.getRawButton(2);
    	joystickX = stick.getRawButton(4);
    	joystickY = stick.getRawButton(1);
    	joystickLSB = stick.getRawButton(11);
    	joystickRSB = stick.getRawButton(12);
    	joystickLB = stick.getRawButton(5);
    	joystickRB = stick.getRawButton(6);
    	joystickLSX = stick.getRawAxis(0);
    	joystickLSY = stick.getRawAxis(1);
    	joystickRSX = stick.getRawAxis(3);
    	joystickRSY = stick2.getRawAxis(1);
    	//Must release button and repress to toggle slowBool, avoids rapid switching every time teleopPeriodic runs
    	if (joystickA && interlock) {
    		slowBool = !slowBool;
    		interlock = false;
    		arduino.writeString("on");
    		Timer.delay(.02);
    		arduino.flush();
    	}
    	//If button is not pressed, reset interlock allowing another press
    	else if (!joystickA) {
    		interlock = true;
    	}
    	if (joystickX && interlock2) {
    		ahrs.reset();
    	}
    	else if (!joystickX) {
    		interlock2 = true;
    	}
    	//Robot drive code
    	if (slowBool) {
    		//If slowBool (activated by A) is true, then drive speed is halved
    		joyLeftOut = joystickLSY * .5;
    		joyRightOut = joystickRSY * .5;
    	}
    	else {
    		joyLeftOut = joystickLSY;
    		joyRightOut = joystickRSY;
    	}
    	// Sets tank drive equal to joystick variables
    	myRobot.tankDrive(joyLeftOut, joyRightOut, true);
    	//Adds encoding distances to dashboard
    	SmartDashboard.putNumber("TeleOp Right Distance:", rightEncode.getDistance());
    	SmartDashboard.putNumber("TeleOp Left Distance:", leftEncode.getDistance());
    	SmartDashboard.putNumber("Z axis:", ahrs.getAngle());

	}
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

