package com.team766.simulator.mechanisms;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.team766.simulator.ElectricalSystem;
import com.team766.simulator.Parameters;
import com.team766.simulator.PhysicalConstants;
import com.team766.simulator.ProgramInterface;
import com.team766.simulator.elements.DCMotor;
import com.team766.simulator.elements.DriveBase;
import com.team766.simulator.elements.Gears;
import com.team766.simulator.elements.PwmMotorController;
import com.team766.simulator.elements.MotorController;
import com.team766.simulator.elements.Wheel;
import com.team766.simulator.interfaces.MechanicalDevice;

public class WestCoastDrive extends DriveBase {
	private DCMotor leftMotor = DCMotor.makeCIM();
	private DCMotor rightMotor = DCMotor.makeCIM();
	
	private MotorController leftController = new PwmMotorController(6, leftMotor);
	private MotorController rightController = new PwmMotorController(4, rightMotor);
	
	private Gears leftGears = new Gears(Parameters.DRIVE_GEAR_RATIO, leftMotor);
	private Gears rightGears = new Gears(Parameters.DRIVE_GEAR_RATIO, rightMotor);
	
	private Wheel leftWheels = new Wheel(Parameters.DRIVE_WHEEL_DIAMETER, leftGears);
	private static final Vector3D LEFT_WHEEL_POSITION = new Vector3D(0., 0.3302, 0.);
	private Wheel rightWheels = new Wheel(Parameters.DRIVE_WHEEL_DIAMETER, rightGears);
	private static final Vector3D RIGHT_WHEEL_POSITION = new Vector3D(0., -0.3302, 0.);
	private static final double WHEEL_BASE = 0.585;
	private static final double ENCODER_TICKS_PER_METER = Parameters.ENCODER_TICKS_PER_REVOLUTION / (Parameters.DRIVE_WHEEL_DIAMETER * Math.PI);
	
	private Vector3D robotPosition = Vector3D.ZERO;
	private Rotation robotRotation = Rotation.IDENTITY;
	private Vector3D linearVelocity = Vector3D.ZERO;
	private Vector3D angularVelocity = Vector3D.ZERO;
	private Vector3D linearAcceleration = Vector3D.ZERO;
	private Vector3D angularAcceleration = Vector3D.ZERO;

	private double leftEncoderResidual = 0;
	private double rightEncoderResidual = 0;
	
	public WestCoastDrive(ElectricalSystem electricalSystem) {
		electricalSystem.addDevice(leftController);
		electricalSystem.addDevice(rightController);
	}

	private static double softSignum(double x) {
		x /= 0.01;
		if (x > 1.0) {
			x = 1.0;
		} else if (x < -1.0) {
			x = -1.0;
		}
		return x;
	}

	public void step() {
		Vector3D wheelForce;
		Vector3D netForce = Vector3D.ZERO;
		Vector3D netTorque = Vector3D.ZERO;
		MechanicalDevice.Input leftWheelInput = new MechanicalDevice.Input(
				LEFT_WHEEL_POSITION,
				linearVelocity.scalarMultiply(-1.));
		wheelForce = leftWheels.step(leftWheelInput).force.scalarMultiply(-1.0);
		netForce = netForce.add(wheelForce);
		netTorque = netTorque.add(Vector3D.crossProduct(LEFT_WHEEL_POSITION, wheelForce));
		MechanicalDevice.Input rightWheelInput = new MechanicalDevice.Input(
				RIGHT_WHEEL_POSITION,
				linearVelocity.scalarMultiply(-1.));
		wheelForce = rightWheels.step(rightWheelInput).force.scalarMultiply(-1.0);
		netForce = netForce.add(wheelForce);
		netTorque = netTorque.add(Vector3D.crossProduct(RIGHT_WHEEL_POSITION, wheelForce));
		
		Vector3D ego_velocity = robotRotation.applyInverseTo(linearVelocity);
		
		double rateLeft = ENCODER_TICKS_PER_METER * (ego_velocity.getX() - angularVelocity.getZ() * LEFT_WHEEL_POSITION.getNorm());
		double rateRight = ENCODER_TICKS_PER_METER * (ego_velocity.getX() + angularVelocity.getZ() * RIGHT_WHEEL_POSITION.getNorm());
		leftEncoderResidual += rateLeft * Parameters.TIME_STEP;
		rightEncoderResidual += rateRight * Parameters.TIME_STEP;
		ProgramInterface.encoderChannels[0].distance += (long)leftEncoderResidual;
		ProgramInterface.encoderChannels[0].rate = rateLeft;
		ProgramInterface.encoderChannels[2].distance += (long)rightEncoderResidual;
		ProgramInterface.encoderChannels[2].rate = rateRight;
		leftEncoderResidual %= 1;
		rightEncoderResidual %= 1;

		ProgramInterface.gyro.angle = Math.toDegrees(robotRotation.getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR)[2]);
		ProgramInterface.gyro.rate = Math.toDegrees(angularVelocity.getZ());

		Vector3D rollingResistance = new Vector3D(-softSignum(ego_velocity.getX()), 0.0, 0.0).scalarMultiply(
				Parameters.ROBOT_MASS * PhysicalConstants.GRAVITY_ACCELERATION * Parameters.ROLLING_RESISTANCE);
		netForce = netForce.add(rollingResistance);

		Vector3D transverseFriction = new Vector3D(0., -softSignum(ego_velocity.getY()), 0.).scalarMultiply(
				Parameters.WHEEL_COEFFICIENT_OF_FRICTION * Parameters.ROBOT_MASS * PhysicalConstants.GRAVITY_ACCELERATION);
		netForce = netForce.add(transverseFriction);

		double maxFriction = Parameters.WHEEL_COEFFICIENT_OF_FRICTION * Parameters.ROBOT_MASS * PhysicalConstants.GRAVITY_ACCELERATION * Parameters.TURNING_RESISTANCE_FACTOR;
		netTorque = netTorque.add(
				new Vector3D(0, 0, -softSignum(angularVelocity.getZ())).scalarMultiply(
						maxFriction * WHEEL_BASE / 2));
		
		linearAcceleration = robotRotation.applyTo(netForce).scalarMultiply(1.0 / Parameters.ROBOT_MASS);
		linearVelocity = linearVelocity.add(linearAcceleration.scalarMultiply(Parameters.TIME_STEP));
		robotPosition = robotPosition.add(linearVelocity.scalarMultiply(Parameters.TIME_STEP));
		
		angularAcceleration = netTorque.scalarMultiply(1.0 / Parameters.ROBOT_MOMENT_OF_INERTIA);
		angularVelocity = angularVelocity.add(angularAcceleration.scalarMultiply(Parameters.TIME_STEP));
		Vector3D angularDelta = angularVelocity.scalarMultiply(Parameters.TIME_STEP);
		robotRotation = robotRotation.compose(
			new Rotation(RotationOrder.XYZ,
			             RotationConvention.VECTOR_OPERATOR,
			             angularDelta.getX(),
			             angularDelta.getY(),
			             angularDelta.getZ()),
			RotationConvention.VECTOR_OPERATOR);
	}
	
	public Vector3D getPosition() {
		return robotPosition;
	}
	
	public Rotation getRotation() {
		return robotRotation;
	}
	
	public Vector3D getLinearVelocity() {
		return linearVelocity;
	}
	
	public Vector3D getAngularVelocity() {
		return angularVelocity;
	}
	
	public Vector3D getLinearAcceleration() {
		return linearAcceleration;
	}
	
	public Vector3D getAngularAcceleration() {
		return angularAcceleration;
	}
}
