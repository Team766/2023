package com.team766.simulator;

public class Parameters {
	public static final double TIME_STEP = 0.001; // seconds
	public static final double DURATION = 5.0; // seconds

	// Robot mode to run in the simulator
	public static final ProgramInterface.RobotMode INITIAL_ROBOT_MODE = ProgramInterface.RobotMode.AUTON;
	
	public static final double BATTERY_VOLTAGE = 12.6; // volts
	public static final double PRIMARY_ELECTRICAL_RESISTANCE = 0.018 + 0.01; // ohms
	
	public static final double STARTING_PRESSURE = 0; // pascals (relative); 120 psi = 827370.875 pascals
	
	public static final double DRIVE_WHEEL_DIAMETER = 0.1524; // 6 inches in meters
	public static final double DRIVE_GEAR_RATIO = 8.0;
	public static final int NUM_LOADED_WHEELS = 2;
	public static final int ENCODER_TICKS_PER_REVOLUTION = 256;
	
	public static final double ROBOT_MASS = 66; // approx. 145 lbs in kg
	public static final double ROBOT_LENGTH = 1.0; // meters
	public static final double ROBOT_WIDTH = 0.8; // meters
	public static final double ROBOT_MOMENT_OF_INERTIA = 1.0/12.0 * ROBOT_MASS * (ROBOT_WIDTH*ROBOT_WIDTH + ROBOT_LENGTH*ROBOT_LENGTH);
	
	public static final double WHEEL_COEFFICIENT_OF_FRICTION = 1.1;
	public static final double ROLLING_RESISTANCE = 0.09;
	public static final double TURNING_RESISTANCE_FACTOR = 0.15;
}
