package com.team766.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import com.team766.simulator.elements.*;
import com.team766.simulator.mechanisms.*;
import com.team766.simulator.ui.*;

public class Simulator implements Runnable {
	private ElectricalSystem electricalSystem = new ElectricalSystem();
	private PneumaticsSystem pneumaticsSystem = new PneumaticsSystem();
	private WestCoastDrive drive = new WestCoastDrive(electricalSystem);
	private AirCompressor compressor = new AirCompressor();
	private DoubleJointedArm arm = new DoubleJointedArm(electricalSystem);
	
	private double time;
	private double nextLogTime;

	private final Metrics metrics = new Metrics();
	private final Metrics.Series xPositionSeries = metrics.addSeries("X Position (m)", false);
	private final Metrics.Series yPositionSeries = metrics.addSeries("Y Position (m)", false);
	private final Metrics.Series rotationSeries = metrics.addSeries("Rotation (deg)", false);
	private final Metrics.Series velocitySeries = metrics.addSeries("Velocity (m/s)", false);
	private final Metrics.Series accelerationSeries = metrics.addSeries("Acceleration (m/s^2)", false);
	private final Metrics.Series armAngle1Series = metrics.addSeries("Arm angle 1");
	private final Metrics.Series armAngle2Series = metrics.addSeries("Arm angle 2");
	private final Metrics.Series armVelocity1Series = metrics.addSeries("Arm velocity 1");
	private final Metrics.Series armVelocity2Series = metrics.addSeries("Arm velocity 2");
	private final Metrics.Series systemVoltageSeries = metrics.addSeries("System Voltage (V)", false);
	private final Metrics.Series systemPressureSeries = metrics.addSeries("System Pressure (PSI)", false);
	private final HashMap<String, Metrics.Series> branchCurrentSeries = new HashMap<>();

	private ArrayList<Double[]> driveTrajectory = new ArrayList<Double[]>();
	private ArrayList<Double[]> armTrajectory = new ArrayList<Double[]>();

	public Simulator() {
		// NOTE(rcahoon, 2023-02-09): Disabled the compressor so it won't affect the battery voltage
		// available to the arm's motors. This should be re-enabled if we want to simulate
		// pneumatics again.
		//electricalSystem.addDevice(compressor);
		pneumaticsSystem.addDevice(compressor, 120 * PneumaticsSystem.PSI_TO_PASCALS);
		pneumaticsSystem.addDevice(new AirReservoir(0.000574), 120 * PneumaticsSystem.PSI_TO_PASCALS); // 574 mL
		pneumaticsSystem.addDevice(new AirReservoir(0.000574), 120 * PneumaticsSystem.PSI_TO_PASCALS); // 574 mL

		for (String branchName : electricalSystem.getBranchCurrents().keySet()) {
			branchCurrentSeries.put(branchName, metrics.addSeries(branchName, false));
		}
	}
	
	public void step() {
		double dt = Parameters.TIME_STEP;
		time += dt;
		ProgramInterface.simulationTime = time;
		
		electricalSystem.step(dt);
		pneumaticsSystem.step(dt);
		drive.step(dt);
		arm.step(dt);
		
		if (ProgramInterface.program != null) {
			ProgramInterface.program.step(dt);
		}
		
		if (nextLogTime <= time) {
			nextLogTime += Parameters.LOGGING_PERIOD;

			var metricsPoint = metrics.add(time);
			metricsPoint.set(xPositionSeries, drive.getPosition().getX());
			metricsPoint.set(yPositionSeries, drive.getPosition().getY());
			metricsPoint.set(rotationSeries, Math.toDegrees(drive.getRotation().getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR)[2]));
			metricsPoint.set(velocitySeries, drive.getLinearVelocity().getX());
			metricsPoint.set(accelerationSeries, drive.getLinearAcceleration().getX());
			metricsPoint.set(armAngle1Series, arm.getJ1Angle());
			metricsPoint.set(armAngle2Series, arm.getJ2Angle());
			metricsPoint.set(armVelocity1Series, arm.getJ1Velocity());
			metricsPoint.set(armVelocity2Series, arm.getJ2Velocity());
			metricsPoint.set(systemVoltageSeries, electricalSystem.getSystemVoltage());
			metricsPoint.set(systemPressureSeries, pneumaticsSystem.getSystemPressure() / PneumaticsSystem.PSI_TO_PASCALS);
			for (var branch : electricalSystem.getBranchCurrents().entrySet()) {
				metricsPoint.set(branchCurrentSeries.get(branch.getKey()), branch.getValue());
			}

			driveTrajectory.add(new Double[] {
				time,
				drive.getPosition().getX(),
				drive.getPosition().getY(),
				drive.getRotation().getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR)[2],
				drive.getLinearVelocity().getX(),
				drive.getLinearVelocity().getY(),
			});

			armTrajectory.add(new Double[] {
				time,
				arm.getJ1Position().get(0, 0),
				arm.getJ1Position().get(1, 0),
				arm.getJ2Position().get(0, 0),
				arm.getJ2Position().get(1, 0)
			});
		}
	}
	
	public void run() {
		metrics.clear();
		time = 0.0;
		nextLogTime = 0.0;
		while (time <= Parameters.DURATION) {
			step();
			if (Math.abs(time - 3.0) < Parameters.TIME_STEP) {
				pneumaticsSystem.ventPressure();
			}
		}

		var playbackTimer = new PlaybackTimer(time);

		// var trajectoryPanel = new Trajectory(driveTrajectory, playbackTimer);
		// new UIFrame("Trajectory", trajectoryPanel);

		var armTrajectoryPanel = new ArmTrajectory(armTrajectory, playbackTimer);
		new UIFrame("ArmTrajectory", armTrajectoryPanel);

		var metricsPanel = new MetricsPlot(metrics, playbackTimer);
		new UIFrame("Metrics", metricsPanel);
	}
}
