package com.team766.hal.wpilib;

import com.kauailabs.navx.frc.AHRS;
import com.team766.hal.GyroReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj.I2C;

public class NavXGyro implements GyroReader {
	private AHRS m_gyro;

	public NavXGyro(I2C.Port port) {
		m_gyro = new AHRS(port);
		// NOTE: It takes a bit of time until the gyro reader thread updates
		// the connected status, so we can't check it immediately.
		// TODO: Replace this with a status indicator
		/*if (!m_gyro.isConnected()) {
			Logger.get(Category.HAL).logData(Severity.ERROR, "NavX Gyro is not connected!");
		} else {
			Logger.get(Category.HAL).logData(Severity.INFO, "NavX Gyro is connected");
		}*/
	}

	@Override
	public void calibrate() {
		m_gyro.calibrate();
	}

	@Override
	public void reset() {
		m_gyro.reset();
	}

	@Override
	public double getAngle() {
		return m_gyro.getAngle();
	}

	@Override
	public double getRate() {
		return m_gyro.getRate();
	}

	@Override
	public double getPitch() {
		return m_gyro.getPitch();
	}

	@Override
	public double getRoll() {
		return m_gyro.getRoll();
	}
	
}
