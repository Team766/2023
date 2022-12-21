package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.math.Rotation2d;
import com.team766.math.Vector2d;
import com.team766.math.Vector3d;

public class Drive extends Mechanism {
	private MotorController m_fld = RobotProvider.instance.getMotor("drive.frontLeftDriveMotor");
	private MotorController m_frd = RobotProvider.instance.getMotor("drive.frontRightDriveMotor");
	private MotorController m_bld = RobotProvider.instance.getMotor("drive.backLeftDriveMotor");
	private MotorController m_brd = RobotProvider.instance.getMotor("drive.backRightDriveMotor");
	private MotorController m_fls = RobotProvider.instance.getMotor("drive.frontLeftSteerMotor");
	private MotorController m_frs = RobotProvider.instance.getMotor("drive.frontRightSteerMotor");
	private MotorController m_bls = RobotProvider.instance.getMotor("drive.backLeftSteerMotor");
	private MotorController m_brs = RobotProvider.instance.getMotor("drive.backRightSteerMotor");

	private GyroReader m_gyro = RobotProvider.instance.getGyro("drive.gyro");

	public Drive() {
		loggerCategory = Category.DRIVE;
	}

	private static final Vector3d FL_LOCATION = new Vector3d(0.635, 0.635, 0);
	private static final Vector3d FR_LOCATION = new Vector3d(0.635, -0.635, 0);
	private static final Vector3d BL_LOCATION = new Vector3d(-0.635, 0.635, 0);
	private static final Vector3d BR_LOCATION = new Vector3d(-0.635, -0.635, 0);
	private static final Vector2d FL_ROTATION_UNIT = Vector3d.UNIT_Z.cross(FL_LOCATION).xy();
	private static final Vector2d FR_ROTATION_UNIT = Vector3d.UNIT_Z.cross(FR_LOCATION).xy();
	private static final Vector2d BL_ROTATION_UNIT = Vector3d.UNIT_Z.cross(BL_LOCATION).xy();
	private static final Vector2d BR_ROTATION_UNIT = Vector3d.UNIT_Z.cross(BR_LOCATION).xy();

	private static final double MAX_ACHIEVABLE_WHEEL_SPEED = 4.0;

	private void setModule(final Vector2d vector, MotorController driveMotor, MotorController steerMotor) {
		checkContextOwnership();

		final double currentSteerAngle = steerMotor.getSensorPosition();

		final double speed = vector.magnitude();
		final double driveAngle = Math.toDegrees(vector.angle());
		final double steerAngle =
			Math.IEEEremainder(driveAngle - currentSteerAngle, 180.) + currentSteerAngle;
		final double velocity =
			Math.abs(Math.IEEEremainder(steerAngle - driveAngle, 360.)) > 90 ? -speed : speed;

		steerMotor.set(ControlMode.Position, steerAngle);
		driveMotor.set(ControlMode.Velocity, velocity);
	}

	public static Vector2d infintessimal(Vector2d v) {
		return v.scale(1e-6 / v.magnitude());
	}

	public void setX() {
		setModule(infintessimal(FL_LOCATION.xy()), m_fld, m_fls);
		setModule(infintessimal(FR_LOCATION.xy()), m_frd, m_frs);
		setModule(infintessimal(BL_LOCATION.xy()), m_bld, m_bls);
		setModule(infintessimal(BR_LOCATION.xy()), m_brd, m_brs);
	}

	public void setDrive(double translationLongitudinal, double translationLateral, double rotation) {
		final Rotation2d orientation = new Rotation2d(Math.toRadians(-m_gyro.getAngle()));

		final Vector2d translation = orientation.transform(
			new Vector2d(translationLongitudinal, translationLateral));

		Vector2d flVector = translation.add(FL_ROTATION_UNIT.scale(rotation));
		Vector2d frVector = translation.add(FR_ROTATION_UNIT.scale(rotation));
		Vector2d blVector = translation.add(BL_ROTATION_UNIT.scale(rotation));
		Vector2d brVector = translation.add(BR_ROTATION_UNIT.scale(rotation));

		final double maxMagnitude =
			Math.max(flVector.magnitude(),
			Math.max(frVector.magnitude(),
			Math.max(blVector.magnitude(),
					 brVector.magnitude())));
		if (maxMagnitude > MAX_ACHIEVABLE_WHEEL_SPEED) {
			final double scalar = MAX_ACHIEVABLE_WHEEL_SPEED / maxMagnitude;
			flVector = flVector.scale(scalar);
			frVector = frVector.scale(scalar);
			blVector = blVector.scale(scalar);
			brVector = brVector.scale(scalar);
		}

		setModule(flVector, m_fld, m_fls);
		setModule(frVector, m_frd, m_frs);
		setModule(blVector, m_bld, m_bls);
		setModule(brVector, m_brd, m_brs);

		log(Severity.INFO, "FL %f  FR %f  BL %f  BR %f",
			m_fld.getSensorVelocity(),
			m_frd.getSensorVelocity(),
			m_bld.getSensorVelocity(),
			m_brd.getSensorVelocity());
	}
}
