# Intake

## What is Intake?

The `Intake` mechanism controls the motors on the robot used to pick up game pieces.

## How does mechanism work?

The intake extends and retracts using two pneumatics. There is a motor connected to each set of wheels on the intake as well as the conveyor belt. The motors rotate the wheels and conveyor belt in order to pull in/push out game pieces.

## Benefits of intake

* **Fast -** The intake can quickly pick up and push out pieces.
* **Wide range -** Game pieces do not have to be in an ultra-specific area to be picked up by the intake.

## Limitations of intake

* **Hardware -** Intakes are pretty bulky. Enough said.
* **Non-specific Placement -** It is difficult to precisely place pieces using the intake.

## How to use intake

The intake can be controlled with buttons on the operator interface by using the following code in `OI.java`:

	if (joystick0.getButton(inButton)) {
			Robot.intake.startIntake();
			Robot.storage.beltIn();
	} else if (joystick0.getButton(outButton)) {
			Robot.intake.reverseIntake();
			Robot.storage.beltOut();
	} else {
			Robot.intake.stopIntake();
			Robot.storage.beltIdle();
	}

### Initialization

`Intake` and `Storage` are initialized in `Robot.java` with the following code:

	public static Intake intake; 
	public static Storage storage;

	public static void robotInit() {
		intake = new Intake();
		storage = new Storage();
	}

### Running intake

The intake runs in `OI.java` with buttons as inputs. No procedures are needed to use the mechanism.