package com.team766.robot.mechanisms;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public class MultiplexedColorSensorV3 extends ColorMatchMech {
  private final int kMultiplexerAddress = 0x70;

  // The multiplexer I2C is static because it needs to be used for ALL of the multiplexer sensors,
  // and so by making it static all sensors can access it.
  private static I2C multiplexer;
  // The actual sensor. All of the methods call this sensor to get the data.
  private ColorSensorV3 sensor;
  // What port on the multiplexer the color sensor is plugged into.
  private final int port;

  public MultiplexedColorSensorV3 (I2C.Port i2cPort, int port) {
    if (multiplexer == null) {
      multiplexer = new I2C(i2cPort, kMultiplexerAddress);
    }
    this.port = port;
    setChannel();
    sensor = new ColorSensorV3(i2cPort);
  }

  /**
   * Helper method. This just sets the multiplexer to the correct port before
   * using the color sensor.
   */
  private void setChannel() {
    multiplexer.write(kMultiplexerAddress, 1 << port);
  }

  /*-----------------------------------------------------------------------*/
  /* Below are all of the methods used for the color sensor. */
  /* All this does is set the channel, then run the command on the sensor. */
  /*-----------------------------------------------------------------------*/

  @Override

  public String checkColor(){
	setChannel();
	super();
  }



}
