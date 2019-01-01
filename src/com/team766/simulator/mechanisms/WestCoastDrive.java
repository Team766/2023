package com.team766.simulator.mechanisms;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.team766.simulator.ElectricalSystem;
import com.team766.simulator.Parameters;
import com.team766.simulator.elements.DCMotor;
import com.team766.simulator.elements.DriveBase;
import com.team766.simulator.elements.Gears;
import com.team766.simulator.elements.PwmSpeedController;
import com.team766.simulator.elements.SpeedController;
import com.team766.simulator.elements.Wheel;
import com.team766.simulator.interfaces.MechanicalDevice;

public class WestCoastDrive extends DriveBase {
	private DCMotor leftMotor = DCMotor.makeCIM();
	private DCMotor rightMotor = DCMotor.makeCIM();
	
	private SpeedController leftController = new PwmSpeedController(6, leftMotor);
	private SpeedController rightController = new PwmSpeedController(4, rightMotor);
	
	private Gears leftGears = new Gears(Parameters.DRIVE_GEAR_RATIO, leftMotor);
	private Gears rightGears = new Gears(Parameters.DRIVE_GEAR_RATIO, rightMotor);
	
	private Wheel leftWheels = new Wheel(Parameters.DRIVE_WHEEL_DIAMETER, leftGears);
	private static final Vector3D LEFT_WHEEL_POSITION = new Vector3D(0., 0.4064, 0.);
	private Wheel rightWheels = new Wheel(Parameters.DRIVE_WHEEL_DIAMETER, rightGears);
	private static final Vector3D RIGHT_WHEEL_POSITION = new Vector3D(0., -0.4064, 0.);
	
	private Vector3D robotPosition = Vector3D.ZERO;
	private Rotation robotRotation = Rotation.IDENTITY;
	private Vector3D linearVelocity = Vector3D.ZERO;
	private Vector3D angularVelocity = Vector3D.ZERO;
	private Vector3D linearAcceleration = Vector3D.ZERO;
	private Vector3D angularAcceleration = Vector3D.ZERO;
	
	public WestCoastDrive(ElectricalSystem electricalSystem) {
		electricalSystem.addDevice(leftController);
		electricalSystem.addDevice(rightController);
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
		
		netForce = netForce.add(
				new Vector3D(
						-Math.signum(linearVelocity.getX()),
						-Math.signum(linearVelocity.getY()),
						-Math.signum(linearVelocity.getZ())).scalarMultiply(
								Parameters.ROBOT_MASS * 9.81 * Parameters.ROLLING_RESISTANCE));
		
		linearAcceleration = netForce.scalarMultiply(1.0 / Parameters.ROBOT_MASS);
		linearVelocity = linearVelocity.add(linearAcceleration.scalarMultiply(Parameters.TIME_STEP));
		robotPosition = robotPosition.add(robotRotation.applyTo(linearVelocity.scalarMultiply(Parameters.TIME_STEP)));
		
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
