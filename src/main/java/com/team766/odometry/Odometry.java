package com.team766.odometry;

import com.team766.framework.LoggingBase;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;
import com.ctre.phoenix.sensors.CANCoder;
import com.team766.logging.Category;
import com.team766.robot.*;

public class Odometry extends LoggingBase {

	private RateLimiter odometryLimiter;

	private MotorController[] motorList;
	//The order of CANCoders should be the same as in motorList
	private CANCoder[] CANCoderList;
	private	int motorCount; 

	private PointDir[] prevPositions;
	private PointDir[] currPositions;
	private double[] prevEncoderValues;
	private double[] currEncoderValues;
	private double gyroPosition;

	private PointDir currentPosition;

	//In Meters
	private static double WHEEL_CIRCUMFERENCE;
	public static double GEAR_RATIO;
	public static int ENCODER_TO_REVOLUTION_CONSTANT;

	//In the same order as motorList, relative to the center of the robot
	private Point[] wheelPositions;
	
	public Odometry(MotorController[] motors, CANCoder[] CANCoders, Point[] wheelLocations, double wheelCircumference, double gearRatio, int encoderToRevolutionConstant, double rateLimiterTime) {
		loggerCategory = Category.ODOMETRY;

		odometryLimiter = new RateLimiter(rateLimiterTime);
		motorList = motors;
		CANCoderList = CANCoders;
		motorCount = motorList.length;
		log("Motor count " + motorCount);
		prevPositions = new PointDir[motorCount];
		currPositions = new PointDir[motorCount];
		prevEncoderValues = new double[motorCount];
		currEncoderValues = new double[motorCount];

		wheelPositions = wheelLocations;
		WHEEL_CIRCUMFERENCE = wheelCircumference;
		GEAR_RATIO = gearRatio;
		ENCODER_TO_REVOLUTION_CONSTANT = encoderToRevolutionConstant;

		currentPosition = new PointDir(0, 0, 0);
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i] = new PointDir(0,0, 0);
			currPositions[i] = new PointDir(0,0, 0);
			prevEncoderValues[i] = 0;
			currEncoderValues[i] = 0;
		}
	}

	public String getName() {
		return "Odometry";
	}

	public void resetCurrentPosition() {
		currentPosition.set(0, 0);
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i].set(currentPosition.add(wheelPositions[i]));
			currPositions[i].set(0,0);
		}
	}

	private void setCurrentEncoderValues() {
		for (int i = 0; i < motorCount; i++) {
			prevEncoderValues[i] = currEncoderValues[i];
			currEncoderValues[i] = motorList[i].getSensorPosition();
		}
	}

	private void updateCurrentPositions() {
		double angleChange;
		double radius;
		double deltaX;
		double deltaY;
		gyroPosition = Robot.gyro.getGyroYaw();
		Point slopeFactor = new Point(Math.sqrt(Math.cos(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroPitch()) * Math.cos(Robot.gyro.getGyroPitch()) + Math.sin(Robot.gyro.getGyroYaw()) * Math.sin(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroRoll()) * Math.cos(Robot.gyro.getGyroRoll())), Math.sqrt(Math.sin(Robot.gyro.getGyroYaw()) * Math.sin(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroPitch()) * Math.cos(Robot.gyro.getGyroPitch()) + Math.cos(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroYaw()) * Math.cos(Robot.gyro.getGyroRoll()) * Math.cos(Robot.gyro.getGyroRoll())));

		for (int i = 0; i < motorCount; i++) {
			//prevPositions[i] = new PointDir(currentPosition.getX() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.cos(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currentPosition.getY() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.sin(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currPositions[i].getHeading());
			prevPositions[i].set(currentPosition.add(wheelPositions[i]), currPositions[i].getHeading());
			currPositions[i].setHeading(CANCoderList[i].getAbsolutePosition() + gyroPosition);
			angleChange = currPositions[i].getHeading() - prevPositions[i].getHeading();

			if (angleChange != 0) {
				radius = 180 * (currEncoderValues[i] - prevEncoderValues[i]) / (Math.PI * angleChange);
				deltaX = radius * Math.sin(Math.toRadians(angleChange));
				deltaY = radius * (1 - Math.cos(Math.toRadians(angleChange)));
				currPositions[i].setX(prevPositions[i].getX() + (Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaX - Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * slopeFactor.getX() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				currPositions[i].setY(prevPositions[i].getY() + (Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaX + Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * slopeFactor.getY() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			} else {
				currPositions[i].setX(prevPositions[i].getX() + (currEncoderValues[i] - prevEncoderValues[i]) * Math.cos(Math.toRadians(prevPositions[i].getHeading())) * slopeFactor.getX() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				currPositions[i].setY(prevPositions[i].getY() + (currEncoderValues[i] - prevEncoderValues[i]) * Math.sin(Math.toRadians(prevPositions[i].getHeading())) * slopeFactor.getY() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			}
		}
	}

	private void findRobotPosition() {
		double sumX = 0;
		double sumY = 0;
		for (int i = 0; i < motorCount; i++) {
			sumX += currPositions[i].getX();
			sumY += currPositions[i].getY();
		}
		currentPosition.set(sumX / motorCount, sumY / motorCount, gyroPosition);
	}

	//Intended to be placed inside Robot.drive.run()
	public PointDir run() {
		if (odometryLimiter.next()) {
			setCurrentEncoderValues();
			updateCurrentPositions();
			findRobotPosition();
		}
		return currentPosition;
	}
}
