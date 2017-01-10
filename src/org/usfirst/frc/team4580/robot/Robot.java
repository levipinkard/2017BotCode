package org.usfirst.frc.team4580.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
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
	int autoLoopCounter;
    boolean joystickA;
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
	double joyLeftOut;;
	double joyRightOut;
	boolean slowBool;
	boolean interlock;
	CANTalon leftMotor;
	CANTalon rightMotor;
	Compressor pneumatic;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		//Sets up Talons to respective CAN IDs
		leftMotor = new CANTalon(0);
		rightMotor = new CANTalon(1);
		//Sets left motor to left and right Talons
		myRobot = new RobotDrive(leftMotor,rightMotor);
    	//Sets stick equal to joystick at port #0
    	stick = new Joystick(0);
    	//Sets up variables to allow for slow mode
    	slowBool = false;
    	interlock = true;
    	//If using pneumatics, this will set up compressor and enable it so that it fills itself
    	//pneumatic = new Compressor(0);
    	//pneumatic.setClosedLoopControl(true);
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
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
    	//Assigns various variables to current values of stick
		joystickA = stick.getRawButton(2);
        joystickB = stick.getRawButton(3);
    	joystickX = stick.getRawButton(1);
    	joystickY = stick.getRawButton(4);
    	joystickLSB = stick.getRawButton(11);
    	joystickRSB = stick.getRawButton(12);
    	joystickLB = stick.getRawButton(5);
    	joystickRB = stick.getRawButton(6);
    	joystickLSX = stick.getRawAxis(0);
    	joystickLSY = stick.getRawAxis(1);
    	joystickRSX = stick.getRawAxis(2);
    	joystickRSY = stick.getRawAxis(3);
    	//Must release button and repress to toggle slowBool, avoids rapid switching everytime teleopPeriodic runs
    	if (joystickA && interlock) {
    		slowBool = !slowBool;
    		interlock = false;
    	}
    	//If button is not pressed, reset interlock
    	else if (!joystickA) {
    		interlock = true;
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
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

