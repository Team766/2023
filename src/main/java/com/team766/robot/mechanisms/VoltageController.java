package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VoltageController extends Mechanism {
	private double voltage;
	private String voltageReport;
	private PowerDistribution powerDist;
	private double current;
	private String currentReport;

	public VoltageController(){
		powerDist = new PowerDistribution(1, ModuleType.kRev);
		voltage = powerDist.getVoltage();
		current = powerDist.getTotalCurrent();
	}

	public void powerWarning(){
		voltage = powerDist.getVoltage();
		if(voltage <= 8.5){
			voltageReport = "VOLTAGE TOO LOW!!!";
		} else {
			voltageReport = "voltage is normal :-)";
		}
		SmartDashboard.putString("Voltage Warning", voltageReport);
	}

	public void currentWarning(){
		current = powerDist.getTotalCurrent();
		if(current >= 140.0){
			currentReport = "WARNING!!! USING TOO MUCH POWER!";
		} else {
			currentReport = "power levels are safe :-)";
		}
		SmartDashboard.putString("Power Warning", currentReport);
	}

	public void displayPower(){
		voltage = powerDist.getVoltage();
		current = powerDist.getTotalCurrent();
		SmartDashboard.putNumber("Voltage", voltage);
		SmartDashboard.putNumber("Total Current", current);
	}


}
