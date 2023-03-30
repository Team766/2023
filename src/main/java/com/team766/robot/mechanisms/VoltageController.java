package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VoltageController extends Mechanism {
	private double voltage;
	private String voltageReport;
	private PowerDistribution powerDist;

	public VoltageController(){
		powerDist = new PowerDistribution(1, ModuleType.kRev);
		voltage = powerDist.getVoltage();
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

	public void displayPower(){
		voltage = powerDist.getVoltage();
		SmartDashboard.putNumber("Voltage", voltage);
	}


}
