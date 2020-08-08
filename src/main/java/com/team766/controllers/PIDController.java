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
 * the output directly to speed controller, etc.
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
	private ValueProvider<Double> maxoutput_low = new MissingValue<Double>();
	private ValueProvider<Double> maxoutput_high = new MissingValue<Double>();
	private ValueProvider<Double> endthreshold;

	private double setpoint = 0;

	private double cur_error = 0;
	private double prev_error = 0;
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
		maxoutput_low = new ConstantValueProvider<Double>(outputmax_low);
		maxoutput_high = new ConstantValueProvider<Double>(outputmax_high);
		endthreshold = new ConstantValueProvider<Double>(threshold);
		setTimeProvider(RobotProvider.getTimeProvider(), timeProvider.get());
	}

	private void setTimeProvider(TimeProviderI timeProvider, double v) {
		this.timeProvider = timeProvider;
		lastTime = v;
	}

	public PIDController(
			ValueProvider<Double> P,
			ValueProvider<Double> I,
			ValueProvider<Double> D,
			ValueProvider<Double> outputmax_low,
			ValueProvider<Double> outputmax_high,
			ValueProvider<Double> threshold) {
		Kp = P;
		Ki = I;
		Kd = D;
		maxoutput_low = outputmax_low;
		maxoutput_high = outputmax_high;
		endthreshold = threshold;
		setTimeProvider(RobotProvider.getTimeProvider(), timeProvider.get());
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
		maxoutput_low = new MissingValue<Double>();
		maxoutput_high = new MissingValue<Double>();
		endthreshold = new ConstantValueProvider<Double>(threshold);
		setTimeProvider(timeProvider, timeProvider.get());
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
	}
	
	/** Same as calculate() except that it prints debugging information
	 * 
	 * @param cur_input The current input to be plugged into the PID controller
	 * @param smart True if you want the output to be dynamically adjusted to the speed controller
	 */
	public void calculateDebug(double cur_input, boolean smart) {
		print = true;
		calculate(cur_input, smart);
	}

	/**
	 * Calculate PID value. Run only once per loop. Use getOutput to get output.
	 * 
	 * @param cur_input Input value from sensor
	 * @param clamp True if you want the output to be clamped
	 */
	public void calculate(double cur_input, boolean clamp) {
		cur_error = (setpoint - cur_input);
		/*
		if (isDone()) {
			output_value = 0;
			pr("pid done");
			return;
		}
		*/
		
		double delta_time = timeProvider.get() - lastTime;

		total_error += cur_error * delta_time;

		if ((total_error * Ki.get()) > 1) {
			total_error = 1 / Ki.get();
		} else {
			if ((total_error * Ki.get()) < -1)
				total_error = -1 / Ki.get();
		}

		double out =
				Kp.get() * cur_error +
				Ki.get() * total_error +
				Kd.get() * ((cur_error - prev_error) / delta_time);
		prev_error = cur_error;

		pr("Pre-clip output: " + out);
		
		if (clamp)
			output_value = clip(out);
		else
			output_value = out;

		lastTime = timeProvider.get();
		
		pr("	Total Error: " + total_error + "		Current Error: " + cur_error +
		   "	Output: " + output_value + " 	Setpoint: " + setpoint);
	}

	public double getOutput() {
		return output_value;
	}

	public boolean isDone() {
		return Math.abs(cur_error) < endthreshold.get();
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
	 * Clips value for sending to speed controllers. This deals with if you
	 * don't want to run an arm or wheels at full speed under PID.
	 * 
	 * @param clipped
	 * @return clipped value, safe for setting to controllers
	 */
	private double clip(double clipped) {
		double out = clipped;
		double outputMaxLow = -1;
		double outputMaxHigh = 1;
		if (maxoutput_low.hasValue()) {
			outputMaxLow = maxoutput_low.get();
		}
		if (maxoutput_high.hasValue()) {
			outputMaxHigh = maxoutput_high.get();
		}
		if (out > outputMaxHigh) {
			out = outputMaxHigh;
		}
		if (out < outputMaxLow) {
			out = outputMaxLow;
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
