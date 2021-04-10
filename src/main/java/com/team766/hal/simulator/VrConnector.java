package com.team766.hal.simulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.simulator.ProgramInterface;

public class VrConnector implements Runnable {
	private static class PortMapping {
		public final int messageDataIndex;
		public final int robotPortIndex;

		public PortMapping(int messageIndex, int robotIndex) {
			this.messageDataIndex = messageIndex;
			this.robotPortIndex = robotIndex;
		}
	}

	/// Command indexes

	private static final int MAX_COMMANDS = 64;

	private static final int RESET_SIM_CHANNEL = 0;

	private static final List<PortMapping> PWM_CHANNELS = Arrays.asList(
		new PortMapping(10, 6), // Left motor
		new PortMapping(11, 4), // Right motor
		new PortMapping(12, 0)  // Intake
	);
	private static final List<PortMapping> SOLENOID_CHANNELS = Arrays.asList(
		new PortMapping(15, 0), // Intake arm
		new PortMapping(13, 1)  // Catapult launch
	);
	private static final List<PortMapping> RELAY_CHANNELS = Arrays.asList();

	/// Feedback indexes

	private static final int TIMESTAMP_CHANNEL = 5;

	private static final int ROBOT_MODE_CHANNEL = 3;
	private static final Map<Integer, ProgramInterface.RobotMode> ROBOT_MODES = Map.of(
		0, ProgramInterface.RobotMode.DISABLED,
		1, ProgramInterface.RobotMode.AUTON,
    	2, ProgramInterface.RobotMode.TELEOP
	);

	private static final int ROBOT_X_CHANNEL = 8;
	private static final int ROBOT_Y_CHANNEL = 9;

	private static final List<PortMapping> ENCODER_CHANNELS = Arrays.asList(
		new PortMapping(10, 0), // Left encoder
		new PortMapping(11, 2), // Right encoder
		new PortMapping(13, 4)  // Mechanism encoder
	);
	private static final int GYRO_CHANNEL = 15;
	private static final int GYRO_RATE_CHANNEL = 16;
	private static final List<PortMapping> DIGITAL_CHANNELS = Arrays.asList(
		new PortMapping(13, 0), // Intake state
		new PortMapping(14, 1), // Ball presence
		new PortMapping(17, 2), // Line Sensor 1
		new PortMapping(18, 3), // Line Sensor 2
		new PortMapping(19, 4)  // Line Sensor 3
	);
	private static final List<PortMapping> ANALOG_CHANNELS = Arrays.asList();

	private static final int NUM_JOYSTICK = 4;
    private static final int JOYSTICK_AXIS_START = 20;
    private static final int AXES_PER_JOYSTICK = 4;
    private static final int JOYSTICK_BUTTON_START = 40;
    private static final int BUTTONS_PER_JOYSTICK = 8;

	/// Socket Communication

	private static final int commandsPort = 7661;
	private static final int feedbackPort = 7662;
	private static final int BUF_SZ = 1024;

	Selector selector;
	InetSocketAddress sendAddr;
	ByteBuffer feedback = ByteBuffer.allocate(BUF_SZ);
	ByteBuffer commands = ByteBuffer.allocate(BUF_SZ);

	private double lastGyroValue = Double.NaN;
	private long[] lastEncoderValue = new long[12];

	private int getFeedback(int index) {
		return feedback.getInt(index * 4);
	}

	private void putCommand(int index, int value) {
		commands.putInt(index * 4, value);
	}

	private void putCommandFloat(int index, double value) {
		putCommand(index, (int) (value * 512.0));
	}

	private void putCommandTristate(int index, int value) {
		if (value == 0)
			putCommand(index, 0);
		else if (value > 0)
			putCommand(index, 511);
		else
			putCommand(index, -512);
	}

	private void putCommandBool(int index, boolean value) {
		putCommand(index, value ? 511 : -512);
	}

	public VrConnector() throws IOException {
		selector = Selector.open();
		DatagramChannel channel = DatagramChannel.open();
		InetSocketAddress receiveAddr = new InetSocketAddress(feedbackPort);
		channel.bind(receiveAddr);
		sendAddr = new InetSocketAddress(InetAddress.getLoopbackAddress(), commandsPort);
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
		commands.limit(MAX_COMMANDS * 4);
		commands.order(ByteOrder.LITTLE_ENDIAN);
		feedback.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void process() throws IOException {
		for (PortMapping m : PWM_CHANNELS) {
			putCommandFloat(m.messageDataIndex, ProgramInterface.pwmChannels[m.robotPortIndex]);
		}
		for (PortMapping m : SOLENOID_CHANNELS) {
			putCommandBool(m.messageDataIndex, ProgramInterface.solenoidChannels[m.robotPortIndex]);
		}
		for (PortMapping m : RELAY_CHANNELS) {
			putCommandTristate(m.messageDataIndex, ProgramInterface.relayChannels[m.robotPortIndex]);
		}

		selector.selectedKeys().clear();
		selector.selectNow();
		boolean newData = false;
		for (SelectionKey key : selector.selectedKeys()) {
			if (!key.isValid()) {
				continue;
			}

			DatagramChannel chan = (DatagramChannel) key.channel();
			if (key.isReadable()) {
				feedback.clear();
				chan.receive(feedback);
				newData = true;
				key.interestOps(SelectionKey.OP_WRITE);
			} else if (key.isWritable()) {
				chan.send(commands.duplicate(), sendAddr);
				putCommand(RESET_SIM_CHANNEL, 0);
				key.interestOps(SelectionKey.OP_READ);
			}
		}

		if (newData) {
			// Time is sent in milliseconds
			ProgramInterface.simulationTime = getFeedback(TIMESTAMP_CHANNEL) * 0.001;

			ProgramInterface.robotMode = ROBOT_MODES.get(getFeedback(ROBOT_MODE_CHANNEL));

			final double gyroValue = getFeedback(GYRO_CHANNEL) / 10.0;
			if (Double.isNaN(lastGyroValue)) {
				lastGyroValue = gyroValue;
			}
			ProgramInterface.gyro.angle += gyroValue - lastGyroValue;
			lastGyroValue = gyroValue;

			ProgramInterface.robotPosition.x = getFeedback(ROBOT_X_CHANNEL) / 1000.0;
			ProgramInterface.robotPosition.y = getFeedback(ROBOT_Y_CHANNEL) / 1000.0;
			ProgramInterface.robotPosition.heading = gyroValue;

			ProgramInterface.gyro.rate = getFeedback(GYRO_RATE_CHANNEL) / 100.0;

			for (PortMapping m : ENCODER_CHANNELS) {
				long value = getFeedback(m.messageDataIndex);
				ProgramInterface.encoderChannels[m.robotPortIndex] += value - lastEncoderValue[m.robotPortIndex];
				lastEncoderValue[m.robotPortIndex] = value;
			}
			for (PortMapping m : DIGITAL_CHANNELS) {
				ProgramInterface.digitalChannels[m.robotPortIndex] = getFeedback(m.messageDataIndex) > 0;
			}
			for (PortMapping m : ANALOG_CHANNELS) {
				ProgramInterface.analogChannels[m.robotPortIndex] = getFeedback(m.messageDataIndex) * 5.0 / 1024.0;
			}
			for (int j = 0; j < NUM_JOYSTICK; ++j) {
				for (int a = 0; a < AXES_PER_JOYSTICK; ++a) {
					ProgramInterface.joystickChannels[j].setAxisValue(a, getFeedback(j * AXES_PER_JOYSTICK + a + JOYSTICK_AXIS_START) / 100.0);
				}
				for (int b = 0; b < BUTTONS_PER_JOYSTICK; ++b) {
					ProgramInterface.joystickChannels[j].setButton(b + 1, getFeedback(j * BUTTONS_PER_JOYSTICK + b + JOYSTICK_BUTTON_START) > 0);
				}
			}

			++ProgramInterface.driverStationUpdateNumber;
		}
	}

	public void run() {
		boolean started = false;
		while (true) {
			try {
				process();
			} catch (Exception e) {
				e.printStackTrace();
				LoggerExceptionUtils.logException(e);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {}
			}
			if (ProgramInterface.simulationTime == 0) {
				// Wait for a connection to the simulator before starting to run the robot code.
				continue;
			}
			if (!started) {
				System.out.println("Starting simulation");
				started = true;
			}
			if (ProgramInterface.programStep != null) {
				ProgramInterface.programStep.run();
			}
		}
	}
}
