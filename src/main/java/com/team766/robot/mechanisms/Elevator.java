package com.team766.robot.mechanisms;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static com.team766.robot.constants.ConfigConstants.*;

public class Elevator extends Mechanism {
	public enum Position {

		// TODO: do we need separate heights for cones vs cubes?

		/** Elevator is at lowest (start) position. */
		LOW(0), 
		/** Elevator is the appropriate height to place game pieces at the mid node. */
		MID(45),
		/** Elevator is at appropriate height to place game pieces at the high node. */
		HIGH(180), 
		/** Elevator is at appropriate height to grab cubes from the human player. */
		HUMAN_CUBES(200),
		/** Elevator is at appropriate height to grab cones from the human player. */
		HUMAN_CONES(200);

		private final int height;

		Position(int position) {
			this.height = position;
		}

		private int getHeight() {
			return height;
		}
	}

	private final CANSparkMax leftMotor;
	private final CANSparkMax rightMotor;
	private final SparkMaxPIDController pidController;
	private final ValueProvider<Double> pGain;
	private final ValueProvider<Double> iGain;
	private final ValueProvider<Double> dGain;
	private final ValueProvider<Double> ffGain;

	private final RateLimiter rateLimiter = new RateLimiter(1.0 /* seconds */);
	
	/**
	 * Contructs a new Elevator.
	 */
	public Elevator() {
		MotorController halLeftMotor = RobotProvider.instance.getMotor(ELEVATOR_LEFT_MOTOR);
		MotorController halRightMotor = RobotProvider.instance.getMotor(ELEVATOR_RIGHT_MOTOR);

		if (!((halLeftMotor instanceof CANSparkMax)&&(halRightMotor instanceof CANSparkMax))) {
			log(Severity.ERROR, "Motors are not CANSparkMaxes!");
			throw new IllegalStateException("Motor are not CANSparkMaxes!");
		}

		leftMotor = (CANSparkMax) halLeftMotor;
		rightMotor = (CANSparkMax) halRightMotor;

		rightMotor.follow(leftMotor);

		pidController = leftMotor.getPIDController();
		pidController.setFeedbackDevice(leftMotor.getEncoder());

		pGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_PGAIN);
		iGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_IGAIN);
		dGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_DGAIN);
		ffGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_FFGAIN);
	}

	/**
	 * Returns the current height of the elevator, in inches ('Murica).
	 */
	public double getHeight() {
		return EncoderUtils.elevatorEUToHeight(leftMotor.getEncoder().getPosition());
	}

	/**
	 * Moves the elevator to a pre-set {@link Position}.
	 */
	public void moveTo(Position position) {
		moveTo(position.getHeight());
	}

	/**
	 * Moves the elevator to a specific position (in inches).
	 */
	public void moveTo(int position) {
		checkContextOwnership();

		// set the PID controller values with whatever the latest is in the config
		pidController.setP(pGain.get());
		pidController.setI(iGain.get());
		pidController.setD(dGain.get());
		pidController.setFF(ffGain.get());

		// convert the desired target degrees to encoder units
		double encoderUnits = EncoderUtils.elevatorHeightToEU(position);

		// set the reference point for the wrist
		pidController.setReference(encoderUnits, ControlType.kSmartMotion);
	}

	@Override
	public void run() {
		if (rateLimiter.next()) {
			SmartDashboard.putNumber("ElevatorHeight", getHeight());
		}
	}
}
