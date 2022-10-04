package com.team766.simulator;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import com.team766.simulator.elements.AirCompressor;
import com.team766.simulator.elements.AirReservoir;
import com.team766.simulator.mechanisms.WestCoastDrive;
import com.team766.simulator.ui.Metrics;
import com.team766.simulator.ui.Trajectory;

public class Simulator implements Runnable {
	private ElectricalSystem electricalSystem = new ElectricalSystem();
	private PneumaticsSystem pneumaticsSystem = new PneumaticsSystem();
	private WestCoastDrive drive = new WestCoastDrive(electricalSystem);
	private AirCompressor compressor = new AirCompressor();
	
	private double time;
	private ArrayList<Double[]> metrics = new ArrayList<Double[]>();
	private ArrayList<Double[]> trajectory = new ArrayList<Double[]>();
	
	public Simulator() {
		electricalSystem.addDevice(compressor);
		pneumaticsSystem.addDevice(compressor, 120 * PneumaticsSystem.PSI_TO_PASCALS);
		pneumaticsSystem.addDevice(new AirReservoir(0.000574), 120 * PneumaticsSystem.PSI_TO_PASCALS); // 574 mL
		pneumaticsSystem.addDevice(new AirReservoir(0.000574), 120 * PneumaticsSystem.PSI_TO_PASCALS); // 574 mL
	}
	
	public void step() {
		time += Parameters.TIME_STEP;
		ProgramInterface.simulationTime = time;
		
		electricalSystem.step();
		pneumaticsSystem.step();
		drive.step();
		
		if (ProgramInterface.program != null) {
			ProgramInterface.program.step();
		}
		
		metrics.add(new Double[] {
				time,
				drive.getPosition().getX(),
				drive.getPosition().getY(),
				Math.toDegrees(drive.getRotation().getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR)[2]),
				drive.getLinearVelocity().getX(),
				drive.getLinearAcceleration().getX(),
				electricalSystem.getSystemVoltage(),
				pneumaticsSystem.getSystemPressure() / PneumaticsSystem.PSI_TO_PASCALS,
			});
		trajectory.add(new Double[] {
				drive.getPosition().getX(),
				drive.getPosition().getY(),
				drive.getRotation().getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR)[2],
				drive.getLinearVelocity().getX(),
				drive.getLinearVelocity().getY(),
			});
	}
	
	public void run() {
		metrics.clear();
		time = 0.0;
		while (time <= Parameters.DURATION) {
			step();
			if (Math.abs(time - 3.0) < Parameters.TIME_STEP) {
				pneumaticsSystem.ventPressure();
			}
		}
		Trajectory.makePlotFrame(trajectory);
		Metrics.makePlotFrame(metrics, new String[] {
				"X Position (m)",
				"Y Position (m)",
				"Rotation (deg)",
				"Velocity (m/s)",
				"Acceleration (m/s^2)",
				"System Voltage (V)",
				"System Pressure (PSI)",
			});
	}
}
