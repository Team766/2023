
public class ArmsAnimation {

	public enum AnimationState {
		DISABLED = 0,			// valid next states (depending on STOW or scoring): UNSTOWED or PREP
		STOWED,					// valid next states: UNSTOWED
		UNSTOWED,				// valid next states: STOWED, PREP
		PREP,					// valid next states: UNSTOWED, PLAYER_STATION_CONE, MID_GOAL, HIGH_GOAL
		PLAYER_STATION_CONE,	// valid next states: PREP
		MID_GOAL,				// valid next states: PREP
		HIGH_GOAL				// valid next states: PREP
	}

	public enum ProceedCondition {
		BOTH_JOINTS_REACHED,
		JOINT_ONE_REACHED_ONLY,
		JOINT_TWO_REACHED_ONLY
	}

	public class Waypoint {
		public double jointOneAngleDegrees;
		public double jointTwoAngleDegrees;
		public ProceedCondition proceedCondition;
		public double entryVelocity; 

		public Waypoint(double jointOneAngleDegrees, double jointTwoAngleDegrees, ProceedCondition proceedCondition, double entryVelocity) {
			this.jointOneAngleDegrees = jointOneAngleDegrees;
			this.jointTwoAngleDegrees = jointTwoAngleDegrees;
			this.proceedCondition = proceedCondition;
			this.entryVelocity = entryVelocity;
		}
	}

	private AnimationState currentAnimationState = AnimationState.DISABLED;

	public final HashMap<AnimationState, Waypoint> waypoints = new HashMap<AnimationState, WaypointDefinition>();
	
	public ArmsAnimation() {
		// TODO: these are just estimates, calibrate on before using otherwise something may break :D
		waypoints.put(DISABLED, null);
		waypoints.put(STOWED, 				new Waypoint(0, -150, BOTH_JOINTS_REACHED, 500 ));
		waypoints.put(UNSTOWED, 			new Waypoint(10, -140, JOINT_ONE_REACHED_ONLY, 3000));
		waypoints.put(PREP, 				new Waypoint(10, -90, BOTH_JOINTS_REACHED, 4000));
		waypoints.put(PLAYER_STATION_CONE, 	new Waypoint(-20, -80, BOTH_JOINTS_REACHED, 3000));
		waypoints.put(MID_GOAL, 			new Waypoint(-20, -110, BOTH_JOINTS_REACHED, 3000));
		waypoints.put(HIGH_GOAL 			new Waypoint(-20, -50, BOTH_JOINTS_REACHED, 3000));
	}

	public void update() {
		// TODO
		// ensure that position for desired state is kept
	}

	public void changeState(AnimationState targetState) {
		// valid states: DISABLED, STOWED, PLAYER_STATION_CONE, MID_GOAL, HIGH_GOAL

		// TODO : ensure that state transitions are allowed
		// TODO : special case for stowing, to keep applying power
	}
}