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

    private static final List<PortMapping> ENCODER_CHANNELS = Arrays.asList(
        new PortMapping(10, 0), // Left encoder
        new PortMapping(11, 2)  // Right encoder
    );
    private static final int GYRO_CHANNEL = 12;
    private static final List<PortMapping> DIGITAL_CHANNELS = Arrays.asList(
        new PortMapping(13, 0), // Intake state
        new PortMapping(14, 1)  // Ball presence
    );
    private static final List<PortMapping> ANALOG_CHANNELS = Arrays.asList();

    /// Socket Communication

    private static final int commandsPort = 7661;
    private static final int feedbackPort = 7662;
    private static final int BUF_SZ = 1024;

    Selector selector;
    InetSocketAddress sendAddr;
    ByteBuffer feedback = ByteBuffer.allocate(BUF_SZ);
    ByteBuffer commands = ByteBuffer.allocate(BUF_SZ);

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

            ProgramInterface.gyro.angle = getFeedback(GYRO_CHANNEL);

            for (PortMapping m : ENCODER_CHANNELS) {
                ProgramInterface.encoderChannels[m.robotPortIndex] = getFeedback(m.messageDataIndex);
            }
            for (PortMapping m : DIGITAL_CHANNELS) {
                ProgramInterface.digitalChannels[m.robotPortIndex] = getFeedback(m.messageDataIndex) > 0;
            }
            for (PortMapping m : ANALOG_CHANNELS) {
                ProgramInterface.analogChannels[m.robotPortIndex] = getFeedback(m.messageDataIndex) * 5.0 / 1024.0;
            }
        }
    }

    public void run() {
        boolean started = false;
        while (true) {
            try {
                process();
            } catch (Exception e) {
                e.printStackTrace();
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
