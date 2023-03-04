package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.logging.Severity;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.util.Color8Bit;

import com.team766.hal.MotorController;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.imageio.ImageIO;

import com.ctre.phoenix.led.*;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class CANdleMech extends Mechanism {

	private final CANdle m_candle = new CANdle(5);

	public CANdleMech() {
		
	}

	public void setColor(short r, short g, short b, int index, int count) {
		checkContextOwnership();
		m_candle.setLEDs(r / 5, g / 5, b / 5, 0, index, count);
	}

	public void setColor(int r, int g, int b) {
		checkContextOwnership();
		m_candle.setLEDs(r / 5, g / 5, b / 5);
	}
	
}