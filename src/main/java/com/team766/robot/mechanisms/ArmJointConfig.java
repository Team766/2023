package com.team766.robot.mechanisms;

public class ArmJointConfig {

	public double angleMin;
	public double angleMax;
	public double p;
	public double i;
	public double d;
	public double ff;
	public double powerMin;
	public double powerMax;
	public double outputVelocityMin;
	public double velocityMax;
	public double accelMax;
	
	public ArmJointConfig(
		double angleMin,
		double angleMax,
		double p,
		double i,
		double d,
		double ff,
		double powerMin,
		double powerMax,
		double outputVelocityMin,
		double velocityMax,
		double accelMax) {
			this.angleMin = angleMin;
			this.angleMax = angleMax;
			this.p = p;
			this.i = i;
			this.d = d;
			this.powerMax = powerMax;
			this.powerMin = powerMin;
			this.outputVelocityMin = outputVelocityMin;
			this.velocityMax = velocityMax;
			this.accelMax = accelMax;
		}
}
