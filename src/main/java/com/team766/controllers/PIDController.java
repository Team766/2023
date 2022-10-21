package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.ConstantValueProvider;
import com.team766.library.MissingValue;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

/**
 * When attempting to move something with a control loop, a PID controller can 
 * smoothly decrease the error.  This class is used for the elevator, driving during
 * autonmous, and angle correction with the gyro during the tele-operated period of the
 * match.
 * 
 * Because FRC's PID only supports a narrow range of things - you have to send
 * the output directly to a motor controller, etc.
 * 
 * <p>
 * Possibly later we may create a second class that allows for different PID
 * constants depending on which direction - this might be useful for things like
 * arms where behavior is different depending on direction.
 * 
 * 
 * @author Blevenson
 *
 */

public class PIDController {
	private int printCounter = 0;
	private boolean print = false;

	private ValueProvider<Double> Kp;
	private ValueProvider<Double> Ki;
	private ValueProvider<Double> Kd;
	private ValueProvider<Double> Kff;
	private ValueProvider<Double> maxoutput_low = new MissingValue<Double>();
	private ValueProvider<Double> maxoutput_high = new MissingValue<Double>();
	private ValueProvider<Double> endthreshold;

	private double setpoint = Double.NaN;

	private boolean needsUpdate = true;

	private double cur_error = 0;
	private double prev_error = 0;
	private double error_rate = 0;
	private double total_error = 0;

	private double output_value = 0;
	
	TimeProviderI timeProvider;
	private double lastTime;

	public static PIDController loadFromConfig(String configPrefix) {
		if (!configPrefix.endsWith(".")) {
			configPrefix += ".";
		}
		return new PIDController(
			ConfigFileReader.getInstance().getDouble(configPrefix + "pGain"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "iGain"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "dGain"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "ffGain"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "outputMaxLow"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "outputMaxHigh"),
			ConfigFileReader.getInstance().getDouble(configPrefix + "threshold"));
	}
	
	/**
	 * 
	 * @param P
	 *            P constant
	 * @param I
	 *            I constant
	 * @param D
	 *            D constant
	 * @param outputmax_low
	 *            Largest output in the negative direction
	 * @param outputmax_high
	 *            Largest output in the positive direction
	 * @param threshold
	 *            threshold for declaring the PID 'done'
	 */
	public PIDController(double P, double I, double D, double outputmax_low,
	                     double outputmax_high, double threshold) {
		Kp = new ConstantValueProvider<Double>(P);
		Ki = new ConstantValueProvider<Double>(I);
		Kd = new ConstantValueProvider<Double>(D);
		Kff = new MissingValue<Double>();
		maxoutput_low = new ConstantValueProvider<Double>(outputmax_low);
		maxoutput_high = new ConstantValueProvider<Double>(outputmax_high);
		endthreshold = new ConstantValueProvider<Double>(threshold);
		setTimeProvider(RobotProvider.getTimeProvider());
	}

	public PIDController(double P, double I, double D, double FF, double outputmax_low,
	                     double outputmax_high, double threshold) {
		this(P,I,D,outputmax_low,outputmax_high,threshold);
		Kff = new ConstantValueProvider<Double>(FF);
	}

	private void setTimeProvider(TimeProviderI timeProvider) {
		this.timeProvider = timeProvider;
		lastTime = timeProvider.get();
	}

	public PIDController(
			ValueProvider<Double> P,
			ValueProvider<Double> I,
			ValueProvider<Double> D,
			ValueProvider<Double> FF,
			ValueProvider<Double> outputmax_low,
			ValueProvider<Double> outputmax_high,
			ValueProvider<Double> threshold) {
		Kp = P;
		Ki = I;
		Kd = D;
		Kff = FF;
		maxoutput_low = outputmax_low;
		maxoutput_high = outputmax_high;
		endthreshold = threshold;
		setTimeProvider(RobotProvider.getTimeProvider());
	}

	/**
	 * Constructs a PID controller, with the specified P,I,D values, along with the end threshold.
	 * @param P Proportional value used in the PID controller
	 * @param I Integral value used in the PID controller
	 * @param D Derivative value used in the PID controller
	 * @param threshold the end threshold for declaring the PID 'done'
	 * @param timeProvider
	 */
	public PIDController(double P, double I, double D, double threshold, TimeProviderI timeProvider) {
		Kp = new ConstantValueProvider<Double>(P);
		Ki = new ConstantValueProvider<Double>(I);
		Kd = new ConstantValueProvider<Double>(D);
		Kff = new MissingValue<Double>();
		maxoutput_low = new MissingValue<Double>();
		maxoutput_high = new MissingValue<Double>();
		endthreshold = new ConstantValueProvider<Double>(threshold);
		setTimeProvider(timeProvider);
	}

	public PIDController(double P, double I, double D, double FF, double threshold, TimeProviderI timeProvider) {
		Kp = new ConstantValueProvider<Double>(P);
		Ki = new ConstantValueProvider<Double>(I);
		Kd = new ConstantValueProvider<Double>(D);
		Kff = new ConstantValueProvider<Double>(FF);
		maxoutput_low = new MissingValue<Double>();
		maxoutput_high = new MissingValue<Double>();
		endthreshold = new ConstantValueProvider<Double>(threshold);
		setTimeProvider(timeProvider);
	}

	/**
	 * We may want to use same PID object, but with different setpoints, so this
	 * is separated from constructor
	 * 
	 * @param set
	 *            Target point to match
	 */
	public void setSetpoint(double set) {
		setpoint = set;
		total_error = 0;
		needsUpdate = true;
	}

	public void disable() {
		setpoint = Double.NaN;
		total_error = 0;
		needsUpdate = true;
	}

	/**
	 * If we want to set values, such as with SmartDash
	 * 
	 * @param P Proportional value used in the PID controller
	 * @param I	Integral value used in the PID controller
	 * @param D Derivative value used in the PID controller
	 */
	public void setConstants(double P, double I, double D) {
		Kp = new ConstantValueProvider<Double>(P);
		Ki = new ConstantValueProvider<Double>(I);
		Kd = new ConstantValueProvider<Double>(D);
		needsUpdate = true;
	}
	
	/** Same as calculate() except that it prints debugging information
	 * 
	 * @param cur_input The current input to be plugged into the PID controller
	 * @param smart True if you want the output to be dynamically adjusted to the motor controller
	 */
	public void calculateDebug(double cur_input) {
		print = true;
		calculate(cur_input);
	}

	/**
	 * Calculate PID value. Run only once per loop. Use getOutput to get output.
	 * 
	 * @param cur_input Input value from sensor
	 */
	public void calculate(double cur_input) {
		if (Double.isNaN(setpoint)) {
			// Setpoint has not been set yet.
			output_value = 0;
			return;
		}

		cur_error = (setpoint - cur_input);
		/*
		if (isDone()) {
			output_value = 0;
			pr("pid done");
			return;
		}
		*/
		
		double delta_time = timeProvider.get() - lastTime;

		error_rate = (cur_error - prev_error) / delta_time;

		total_error += cur_error * delta_time;

		double ki = Ki.valueOr(0.0);
		if ((total_error * ki) > 1) {
			total_error = 1 / ki;
		} else if ((total_error * ki) < -1) {
			total_error = -1 / ki;
		}

		double out =
				Kp.valueOr(0.0) * cur_error +
				Ki.valueOr(0.0) * total_error +
				Kd.valueOr(0.0) * error_rate +
				Kff.valueOr(0.0) * setpoint;
		prev_error = cur_error;

		pr("Pre-clip output: " + out);
		
		output_value = clip(out);
		
		needsUpdate = false;

		lastTime = timeProvider.get();
		
		pr("	Total Error: " + total_error + "		Current Error: " + cur_error +
		   "	Output: " + output_value + " 	Setpoint: " + setpoint);
	}

	public double getOutput() {
		return output_value;
	}

	public boolean isDone() {
		final double TIME_HORIZON = 0.5;
		return !needsUpdate &&
			Math.abs(cur_error) < endthreshold.get() &&
			Math.abs(cur_error + error_rate * TIME_HORIZON) < endthreshold.get();
	}

	/**
	 * Reset all accumulated errors
	 */
	public void reset() {
		cur_error = 0;
		prev_error = 0;
		total_error = 0;
	}

	/**
	 * Clips value for sending to motor controllers. This deals with if you
	 * don't want to run an arm or wheels at full speed under PID.
	 * 
	 * @param clipped
	 * @return clipped value, safe for setting to controllers
	 */
	private double clip(double clipped) {
		double out = clipped;
		if (maxoutput_high.hasValue() && out > maxoutput_high.get()) {
			out = maxoutput_high.get();
		}
		if (maxoutput_low.hasValue() && out < maxoutput_low.get()) {
			out = maxoutput_low.get();
		}
		return out;
	}

	public double getError() {
		return total_error;
	}

	public double getCurrentError() {
		return cur_error;
	}

	public void setMaxoutputHigh(double in) {
		maxoutput_high = new ConstantValueProvider<Double>(in);
	}

	public void setMaxoutputLow(double in) {
		maxoutput_low = new ConstantValueProvider<Double>(in);
	}
	
	public double getSetpoint(){
		return setpoint;
	}

	private void pr(Object text) {
		if (print && printCounter > 0){
			Logger.get(Category.PID_CONTROLLER).logRaw(Severity.DEBUG, "PID: " + text);
			printCounter = 0;
		}
		printCounter++;
	}

}
