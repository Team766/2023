# MultiplexedColorSensorV3 and SensorResults

## What are MultiplexedColorSensorV3 and SensorResults?

The MultiplexedColorSensorV3 class is a mechanism that uses the data provided by REV Color Sensors to determine whether a sensor is looking at a cone, cube, or other object. It also determines whether an object is within close proximity of a sensor. The SensorResults class determines what piece is in the storage and its orientation.

## How do MultiplexedColorSensorV3 and SensorResults work?

### MultiplexedColorSensorV3

By initializing multiple instances of the MultiplexedColorSensorV3 class and specifying which of the multiplexer's ports each color sensor is attached to, the class can alternate which sensor it records data from. It does this using the setChannel() method, which writes a sensor's port to the multiplexer, prompting the multiplexer to switch to the correct port. This method is used any time before the class collects data. The getPiece() method gets the color values that a sensor is detecting and matches them to a color from the range created in makeColorMatches(). Based on the matched color, getPiece() determines which piece (if any) a sensor is detecting. It also returns whether a sensor is within ~4 cm of the object it is detecting. Unfortunately, the sensors cannot accurately determine proximities greater than ~4 cm.

The SensorResults class uses the sensed colors and proximities of two instances of the MultiplexedColorSensorV3 class

