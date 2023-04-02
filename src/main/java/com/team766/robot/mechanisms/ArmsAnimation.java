package com.team766.robot.mechanisms;

import java.util.HashMap;

public class ArmsAnimation {

	public enum AnimationState {
		DISABLED,				// valid next states (depending on STOW or scoring): UNSTOWED or PREP
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
	private AnimationState targetAnimationState = AnimationState.DISABLED;

	public final HashMap<AnimationState, Waypoint> waypoints = new HashMap<AnimationState, Waypoint>();
	
	public ArmsAnimation() {
		// TODO: these are just estimates, calibrate on before using otherwise something may break :D
		//waypoints.put(AnimationState.DISABLED,				null);
		//waypoints.put(AnimationState.STOWED, 				new Waypoint(0, -150, ProceedCondition.BOTH_JOINTS_REACHED, 500 ));
		waypoints.put(AnimationState.UNSTOWED, 				new Waypoint(17.269, -144.387,ProceedCondition.JOINT_ONE_REACHED_ONLY, 3000));
		waypoints.put(AnimationState.PREP, 					new Waypoint(17.269, -90, ProceedCondition.BOTH_JOINTS_REACHED, 4000));
		waypoints.put(AnimationState.PLAYER_STATION_CONE, 	new Waypoint(22.73, -69.664, ProceedCondition.BOTH_JOINTS_REACHED, 3000));
		waypoints.put(AnimationState.MID_GOAL, 				new Waypoint(7.7765, -88.703, ProceedCondition.BOTH_JOINTS_REACHED, 3000));
		waypoints.put(AnimationState.HIGH_GOAL, 			new Waypoint(-17.379, -66.61, ProceedCondition. BOTH_JOINTS_REACHED, 3000));
	}

	public void update() {
		// TODO
		// ensure that position for desired state is kept
	}

	public void changeState(AnimationState targetState) {
		// valid states: DISABLED, STOWED, PLAYER_STATION_CONE, MID_GOAL, HIGH_GOAL
		// switch(targetState) {
		// 	case
		// }

		// TODO : ensure that state transitions are allowed
		// TODO : special case for stowing, to keep applying power
	}
}