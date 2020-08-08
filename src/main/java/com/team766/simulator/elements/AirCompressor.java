package com.team766.simulator.elements;

import java.util.Arrays;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import com.team766.simulator.Parameters;
import com.team766.simulator.PneumaticsSystem;
import com.team766.simulator.interfaces.ElectricalDevice;
import com.team766.simulator.interfaces.PneumaticDevice;

public class AirCompressor implements ElectricalDevice, PneumaticDevice {
	private static final double CFM_TO_M3_PER_SEC = 0.000471947443;
	
	// Values for http://www.andymark.com/product-p/am-2005.htm
	private static final double NOMINAL_VOLTAGE = 12;
	private static final double[] PRESSURES_PSI = { 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., 100., 110. };
	private static final double[] FLOW_RATES_CFM = { 0.88, 0.50, 0.43, 0.36, 0.30, 0.27, 0.25, 0.24, 0.24, 0.23, 0.22, 0.22 };
	private static final double[] CURRENTS = { 7., 8., 8., 9., 9., 9., 10., 10., 10., 11., 11., 10. };
	private static final double[] PRESSURES = Arrays.stream(PRESSURES_PSI).map(psi -> psi * PneumaticsSystem.PSI_TO_PASCALS).toArray();
	private static final double[] FLOW_RATES = Arrays.stream(FLOW_RATES_CFM).map(cfm -> cfm * CFM_TO_M3_PER_SEC).toArray();
	
	PolynomialSplineFunction currentFunction = new LinearInterpolator().interpolate(PRESSURES, CURRENTS);
	PolynomialSplineFunction flowRateFunction = new LinearInterpolator().interpolate(PRESSURES, FLOW_RATES);

	private boolean isOn = true;
	
	private ElectricalDevice.Input electricalState = new ElectricalDevice.Input(0);
	private PneumaticDevice.Input pneumaticState = new PneumaticDevice.Input(0);

	public void setIsOn(boolean on) {
		isOn = on;
	}

	@Override
	public ElectricalDevice.Output step(ElectricalDevice.Input input) {
		electricalState = input;
		if (isOn) {
			double nominalCurrent = currentFunction.value(pneumaticState.pressure);
			double adjustedCurrent = nominalCurrent * electricalState.voltage / NOMINAL_VOLTAGE;
			return new ElectricalDevice.Output(adjustedCurrent);
		} else {
			return new ElectricalDevice.Output(0);
		}
	}

	@Override
	public PneumaticDevice.Output step(PneumaticDevice.Input input) {
		pneumaticState = input;
		double nominalFlowRate = flowRateFunction.value(pneumaticState.pressure);
		double adjustedFlowRate = nominalFlowRate * electricalState.voltage / NOMINAL_VOLTAGE;
		double flowVolume = adjustedFlowRate * Parameters.TIME_STEP;
		return new PneumaticDevice.Output(flowVolume, 0);
	}
}
