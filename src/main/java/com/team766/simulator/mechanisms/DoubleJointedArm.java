package com.team766.simulator.mechanisms;

import com.team766.simulator.ElectricalSystem;
import com.team766.simulator.PhysicalConstants;
import com.team766.simulator.elements.CanMotorController;
import com.team766.simulator.elements.DCMotor;
import com.team766.simulator.elements.Gears;
import com.team766.simulator.interfaces.MechanicalAngularDevice;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.*;

/**
 * A simulation model of the 2023 robot arm: two joints, where the second joint is driven by a
 * "virtual linkage".
 */
public class DoubleJointedArm {
	// General nomenclature:
	// J1 refers to the first (shoulder) joint, as well as the first link of the arm.
	// J2 refers to the second (elbow) joint, as well as the second link of the arm.

	private static final double J1_INITIAL_ANGLE = Math.toRadians(202.); // radians, relative to "down"
	private static final double J2_INITIAL_ANGLE = Math.toRadians(32.);  // radians, relative to "down"

	// Joint limits
	private static final double J1_MIN = Math.toRadians(90.);  // radians, relative to "down"
	private static final double J1_MAX = Math.toRadians(205.); // radians, relative to "down"
	private static final double J2_MIN = Math.toRadians(-170); // radians, relative to J1
	private static final double J2_MAX = Math.toRadians(170); // radians, relative to J1

	// Physical characterisics of the arm links.
	// NOTE(rcahoon, 2023-02-09): We could determine these from CAD data, but using guessed/approximate values for now.
	private static final double J1_LENGTH = 1; // m
	private static final double J2_LENGTH = 1; // m
	private static final double J1_MASS = 2; // kg
	private static final double J2_MASS = 5; // kg
	private static final double J1_FIRST_MOMENT_OF_MASS = J1_MASS * J1_LENGTH;              // kg m, with the origin/axis at the first joint
	private static final double J1_SECOND_MOMENT_OF_MASS = J1_MASS * J1_LENGTH * J1_LENGTH; // kg m^2, with the origin/axis at the first joint - aka moment of inertia
	private static final double J2_FIRST_MOMENT_OF_MASS = J2_MASS * J2_LENGTH;              // kg m, with the origin/axis at the second joint
	private static final double J2_SECOND_MOMENT_OF_MASS = J2_MASS * J2_LENGTH * J2_LENGTH; // kg m^2, with the origin/axis at the second joint - aka moment of inertia

	private static final double J1_GEAR_RATIO = 4. * 4. * 3. * (58. / 14.);
	private static final double J2_GEAR_RATIO = 4. * 4. * 3. * (58. / 14.);

	// Simulated CAN Bus IDs for the two motors.
	private static final int J1_CAN_CHANNEL = 98;
	private static final int J2_CAN_CHANNEL = 99;

	private DCMotor j1Motor = DCMotor.makeNeo("ArmJoint1");
	private DCMotor j2Motor = DCMotor.makeNeo("ArmJoint2");

	private CanMotorController j1Controller = new CanMotorController(J1_CAN_CHANNEL, j1Motor);
	private CanMotorController j2Controller = new CanMotorController(J2_CAN_CHANNEL, j2Motor);

	private Gears j1Gears = new Gears(J1_GEAR_RATIO, j1Motor);
	private Gears j2Gears = new Gears(J2_GEAR_RATIO, j2Motor);

	private Matrix<N4, N1> state = VecBuilder.fill(
		J1_INITIAL_ANGLE, // j1 angle, radians, relative to "down"
		J2_INITIAL_ANGLE, // j2 angle, radians, relative to "down"
		0,                // j1 velocity, radians/sec
		0                 // j2 velocity, radians/sec
	);

	// Derivation of the equations of motion via Lagrangian mechanics.
	// This system is often referred to as a "double pendulum", and is a commonly studied problem,
	// so literature is available. See this series of videos for an explanation:
	// https://youtu.be/tc2ah-KnDXw, https://youtu.be/eBg8gof1RBg, https://youtu.be/QE1_H2vtHLU
	// The formulation given below is slightly more generalized to allow arbitrary mass
	// distributions along the arm links.
	//
	// Kinematics:
	// x1 = J1_FIRST_MOMENT_OF_MASS / J1_MASS * sin(theta1)
	// y1 = -J1_FIRST_MOMENT_OF_MASS / J1_MASS * cos(theta1)
	// x2 = J1_LENGTH * sin(theta1) + J2_FIRST_MOMENT_OF_MASS / J2_MASS * sin(theta2)
	// y2 = -J1_LENGTH * cos(theta1) - J2_FIRST_MOMENT_OF_MASS / J2_MASS * cos(theta2)
	//
	// Lagrangian:
	// L = T - V
	//   =   1/2 * J1_MASS * (x1'^2 + y1'^2)
	//     + 1/2 * (J1_SECOND_MOMENT_OF_MASS - J1_FIRST_MOMENT_OF_MASS^2 / J1_MASS) * theta1'^2
	//     + 1/2 * J2_MASS * (x2'^2 + y2'^2)
	//     + 1/2 * (J2_SECOND_MOMENT_OF_MASS - J2_FIRST_MOMENT_OF_MASS^2 / J2_MASS) * theta2'^2
	//     - J1_MASS * y1 * PhysicalConstants.GRAVITY_ACCELERATION
	//     - J2_MASS * y2 * PhysicalConstants.GRAVITY_ACCELERATION
	//   =   1/2 * J1_SECOND_MOMENT_OF_MASS * theta1'^2
	//     + 1/2 * J2_MASS * ((J1_LENGTH * cos(theta1) * theta1' + J2_FIRST_MOMENT_OF_MASS / J2_MASS * cos(theta2) * theta2')^2 + (J1_LENGTH * sin(theta1) * theta1' + J2_FIRST_MOMENT_OF_MASS / J2_MASS * sin(theta2) * theta2')^2)
	//     + 1/2 * (J2_SECOND_MOMENT_OF_MASS - J2_FIRST_MOMENT_OF_MASS^2 / J2_MASS) * theta2'^2
	//     + J1_FIRST_MOMENT_OF_MASS * cos(theta1) * PhysicalConstants.GRAVITY_ACCELERATION
	//     + (J2_MASS * J1_LENGTH * cos(theta1) + J2_FIRST_MOMENT_OF_MASS * cos(theta2)) * PhysicalConstants.GRAVITY_ACCELERATION
	//   =   1/2 * J1_SECOND_MOMENT_OF_MASS * theta1'^2
	//     + 1/2 * (J2_MASS * J1_LENGTH^2 * theta1'^2 + 2 * J1_LENGTH * cos(theta1 - theta2) * theta1' * J2_FIRST_MOMENT_OF_MASS * theta2' + J2_FIRST_MOMENT_OF_MASS^2 / J2_MASS * theta2'^2)
	//     + 1/2 * (J2_SECOND_MOMENT_OF_MASS - J2_FIRST_MOMENT_OF_MASS^2 / J2_MASS) * theta2'^2
	//     + J1_FIRST_MOMENT_OF_MASS * cos(theta1) * PhysicalConstants.GRAVITY_ACCELERATION
	//     + (J2_MASS * J1_LENGTH * cos(theta1) + J2_FIRST_MOMENT_OF_MASS * cos(theta2)) * PhysicalConstants.GRAVITY_ACCELERATION
	//   =   1/2 * (J1_SECOND_MOMENT_OF_MASS + J2_MASS * J1_LENGTH^2) * theta1'^2
	//     + 1/2 * J2_SECOND_MOMENT_OF_MASS * theta2'^2
	//     + J2_FIRST_MOMENT_OF_MASS * J1_LENGTH * cos(theta1 - theta2) * theta1' * theta2'
	//     + (J1_FIRST_MOMENT_OF_MASS + J2_MASS * J1_LENGTH) * PhysicalConstants.GRAVITY_ACCELERATION * cos(theta1)
	//     + J2_FIRST_MOMENT_OF_MASS * PhysicalConstants.GRAVITY_ACCELERATION * cos(theta2)
	//
	// Define some constants to simplify the following equations
	// I1 = J1_SECOND_MOMENT_OF_MASS + J2_MASS * J1_LENGTH^2
	// I2 = J2_SECOND_MOMENT_OF_MASS
	// Ix = J2_FIRST_MOMENT_OF_MASS * J1_LENGTH
	// g1 = (J1_FIRST_MOMENT_OF_MASS + J2_MASS * J1_LENGTH) * PhysicalConstants.GRAVITY_ACCELERATION
	// g2 = J2_FIRST_MOMENT_OF_MASS * PhysicalConstants.GRAVITY_ACCELERATION
	//
	// L = 1/2 * I1 * theta1'^2 + 1/2 * I2 * theta2'^2 + Ix * cos(theta1 - theta2) * theta1' * theta2' + g1 * cos(theta1) + g2 * cos(theta2)
	//
	// Euler-Lagrangian:
	// d/dt [Partial]L/[Partial]theta' - [Partial]L/[Partial]theta = tau
	// 
	// d/dt [Partial]L/[Partial]theta1' - [Partial]L/[Partial]theta1 = I1 * theta1'' + Ix * cos(theta1 - theta2) * theta2'' - Ix * sin(theta1 - theta2) * (theta1' - theta2') * theta2'
	//                                 + Ix * sin(theta1 - theta2) * theta1' * theta2' + g1 * sin(theta1)
	//                               = I1 * theta1'' + Ix * cos(theta1 - theta2) * theta2'' + Ix * sin(theta1 - theta2) * theta2'^2 + g1 * sin(theta1)
	//                               = tau1
	//
	// d/dt [Partial]L/[Partial]theta2' - [Partial]L/[Partial]theta2 = I2 * theta2'' + Ix * cos(theta1 - theta2) * theta1'' - Ix * sin(theta1 - theta2) * (theta1' - theta2') * theta1'
	//                                 - Ix * sin(theta1 - theta2) * theta1' * theta2' + g2 * sin(theta2))
	//                               = I2 * theta2'' + Ix * cos(theta1 - theta2) * theta1'' - Ix * sin(theta1 - theta2) * theta1'^2 + g2 * sin(theta2)
	//                               = tau2
	//
	// Rewrite as a matrix equation:
	// M * theta'' + G = tau
	// theta'' = M^-1 * (tau - G)
	//
	// M = [ I1, Ix * cos(theta1 - theta2) ]
	//   = [ Ix * cos(theta1 - theta2), I2 ]
	// G = [  Ix * sin(theta1 - theta2) * theta1' * theta2' + g1 * sin(theta1) ]
	//     [ -Ix * sin(theta1 - theta2) * theta1' * theta2' + g2 * sin(theta2) ]

	public DoubleJointedArm(ElectricalSystem electricalSystem) {
		electricalSystem.addDevice(j1Controller);
		electricalSystem.addDevice(j2Controller);
	}

	/**
	 * Advance the simulation model by the given amount of time.
	 */
	public void step(double dt) {
		final MechanicalAngularDevice.Output j1Drive = j1Gears.step(new MechanicalAngularDevice.Input(getJ1Velocity()), dt);
		final MechanicalAngularDevice.Output j2Drive = j2Gears.step(new MechanicalAngularDevice.Input(getJ2Velocity()), dt);

		double j1Limit = 0.;
		if (state.get(0, 0) <= J1_MIN) {
			state.set(0, 0, J1_MIN);
			state.set(2, 0, 0.0);
			j1Limit = 1.;
		}
		if (state.get(0, 0) >= J1_MAX) {
			state.set(0, 0, J1_MAX);
			state.set(2, 0, 0.0);
			j1Limit = -1.;
		}
		double j2Limit = 0.;
		if (state.get(1, 0) - state.get(0, 0) <= J2_MIN) {
			state.set(1, 0, J2_MIN + state.get(0, 0));
			j2Limit = 1.;
		}
		if (state.get(1, 0) - state.get(0, 0) >= J2_MAX) {
			state.set(1, 0, J2_MAX + state.get(0, 0));
			j2Limit = -1.;
		}

		if (state.get(2, 0) * j1Limit < 0) {
			state.set(2, 0, 0.0);
		}
		if (state.get(3, 0) * j2Limit < 0) {
			state.set(3, 0, 0.0);
		}

		final var u = VecBuilder.fill(j1Drive.torque, j2Drive.torque, j1Limit, j2Limit);
		final var stateDot = S(state, u);
		state = state.plus(stateDot.times(dt));

		j1Controller.setSensorPosition(state.get(0, 0));
		j2Controller.setSensorPosition(state.get(1, 0));
		j1Controller.setSensorVelocity(state.get(2, 0));
		j2Controller.setSensorVelocity(state.get(3, 0));
	}

	/**
	 * State space model X'(t) = S(X(t), u(t)) of the system
	 */
	// Defined as a static method to make sure we don't accidentally refer to any of the state
	// stored in class member variables when we should be referring to the state the integrator
	// gave us.
	private static Vector<N4> S(Matrix<N4, N1> state, Matrix<N4, N1> u) {
		Vector<N2> motorTorques = VecBuilder.fill(u.get(0, 0), u.get(1, 0));
		Vector<N2> jointLimits = VecBuilder.fill(u.get(2, 0), u.get(3, 0));
		Vector<N2> velocity = VecBuilder.fill(state.get(2, 0), state.get(3, 0));

		final double Mx = J2_FIRST_MOMENT_OF_MASS * J1_LENGTH * Math.cos(state.get(0, 0) - state.get(1, 0));
		var M = Matrix.mat(N2.instance, N2.instance).fill(
			J1_SECOND_MOMENT_OF_MASS + J2_MASS * J1_LENGTH * J1_LENGTH, Mx,
			Mx, J2_SECOND_MOMENT_OF_MASS
		);

		final double C = J2_FIRST_MOMENT_OF_MASS * J1_LENGTH * Math.sin(state.get(0, 0) - state.get(1, 0)) * state.get(2, 0) * state.get(3, 0);
		final var G = VecBuilder.fill(
			C + (J1_FIRST_MOMENT_OF_MASS + J2_MASS * J1_LENGTH) * PhysicalConstants.GRAVITY_ACCELERATION * Math.sin(state.get(0, 0)),
			-C + J2_FIRST_MOMENT_OF_MASS * PhysicalConstants.GRAVITY_ACCELERATION * Math.sin(state.get(1, 0))
		);
		var T = motorTorques.minus(G);

		if (T.get(0, 0) * jointLimits.get(0, 0) < 0) {
			M.set(0, 0, 1.0);
			M.set(1, 0, 0.0);
			M.set(0, 1, 0.0);
			T.set(0, 0, 0.0);
		}
		if (T.get(1, 0) * jointLimits.get(1, 0) < 0) {
			M.set(1, 1, 1.0);
			M.set(1, 0, 0.0);
			M.set(0, 1, 0.0);
			T.set(1, 0, 0.0);
		}

		final var acceleration = M.inv().times(T);

		return VecBuilder.fill(velocity.get(0, 0), velocity.get(1, 0), acceleration.get(0, 0), acceleration.get(1, 0));
	}

	/**
	 * Return the angle of the first joint.
	 */
	public double getJ1Angle() {
		return state.get(0, 0);
	}

	/**
	 * Return the angle of the second joint.
	 */
	public double getJ2Angle() {
		return state.get(1, 0);
	}

	/**
	 * Return the velocity of the first joint.
	 */
	public double getJ1Velocity() {
		return state.get(2, 0);
	}

	/**
	 * Return the velocity of the second joint.
	 */
	public double getJ2Velocity() {
		return state.get(3, 0);
	}

	/**
	 * Calculate the total kinetic and potential energy in the arm.
	 * 
	 * If no energy is added or removed (e.g. by motors or friction), this quantity should remain
	 * constant (i.e. conservation of energy), so plotting it over time makes a good sanity check of
	 * the model.
	 */
	public double getEnergy() {
		double y1 = -J1_FIRST_MOMENT_OF_MASS / J1_MASS * Math.cos(getJ1Angle());
		double y2 = -J1_LENGTH * Math.cos(getJ1Angle()) - J2_FIRST_MOMENT_OF_MASS / J2_MASS * Math.cos(getJ2Angle());
		double dx1 = J1_FIRST_MOMENT_OF_MASS / J1_MASS * Math.cos(getJ1Angle()) * getJ1Velocity();
		double dy1 = J1_FIRST_MOMENT_OF_MASS / J1_MASS * Math.sin(getJ1Angle()) * getJ1Velocity();
		double dx2 = J1_LENGTH * Math.cos(getJ1Angle()) * getJ1Velocity() + J2_FIRST_MOMENT_OF_MASS / J2_MASS * Math.cos(getJ2Angle()) * getJ2Velocity();
		double dy2 = J1_LENGTH * Math.sin(getJ1Angle()) * getJ1Velocity() + J2_FIRST_MOMENT_OF_MASS / J2_MASS * Math.sin(getJ2Angle()) * getJ2Velocity();

		double energy = 0.5 * J1_MASS * (dx1*dx1 + dy1*dy1)
		     + 0.5 * (J1_SECOND_MOMENT_OF_MASS - J1_FIRST_MOMENT_OF_MASS * J1_FIRST_MOMENT_OF_MASS / J1_MASS) * getJ1Velocity() * getJ1Velocity()
		     + 0.5 * J2_MASS * (dx2*dx2 + dy2*dy2)
		     + 0.5 * (J2_SECOND_MOMENT_OF_MASS - J2_FIRST_MOMENT_OF_MASS * J2_FIRST_MOMENT_OF_MASS / J2_MASS) * getJ2Velocity() * getJ2Velocity()
		     + J1_MASS * y1 * PhysicalConstants.GRAVITY_ACCELERATION
		     + J2_MASS * y2 * PhysicalConstants.GRAVITY_ACCELERATION;
		return energy;
	}

	/**
	 * Calculuate the cartesian coordinates of the end of the first link.
	 */
	public Vector<N2> getJ1Position() {
		double x1 = J1_LENGTH * Math.sin(getJ1Angle());
		double y1 = -J1_LENGTH * Math.cos(getJ1Angle());
		return VecBuilder.fill(x1, y1);
	}

	/**
	 * Calculuate the cartesian coordinates of the end of the second link.
	 */
	public Vector<N2> getJ2Position() {
		double x2 = J1_LENGTH * Math.sin(getJ1Angle()) + J2_LENGTH * Math.sin(getJ2Angle());
		double y2 = -J1_LENGTH * Math.cos(getJ1Angle()) - J2_LENGTH * Math.cos(getJ2Angle());
		return VecBuilder.fill(x2, y2);
	}
}
