package com.team766.robot.mechanisms;

import java.util.HashMap;
import com.team766.framework.Mechanism;
import com.team766.logging.Category;

public class ArmsAnimation extends Mechanism {

	//
	// Inner Enums
	//

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

	//
	// Inner Classes
	//

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

	//
	// Vars
	//

	private AnimationState currentAnimationState = AnimationState.DISABLED;
	private AnimationState targetAnimationState = AnimationState.DISABLED;

	private final HashMap<AnimationState, Waypoint> waypoints = new HashMap<AnimationState, Waypoint>();
	private final Arms parent;
	
	public ArmsAnimation(Arms parent) {
		this.parent = parent;

		// TODO: these are just estimates, calibrate on before using otherwise something may break :D
		waypoints.put(AnimationState.DISABLED,				null);
		//waypoints.put(AnimationState.STOWED, 				new Waypoint(0, -150, ProceedCondition.BOTH_JOINTS_REACHED, 500 ));
		waypoints.put(AnimationState.UNSTOWED, 				new Waypoint(17.269, -144.387,ProceedCondition.JOINT_ONE_REACHED_ONLY, 3000));
		waypoints.put(AnimationState.PREP, 					new Waypoint(17.269, -90, ProceedCondition.BOTH_JOINTS_REACHED, 4000));
		waypoints.put(AnimationState.PLAYER_STATION_CONE, 	new Waypoint(22.73, -69.664, ProceedCondition.BOTH_JOINTS_REACHED, 3000));
		waypoints.put(AnimationState.MID_GOAL, 				new Waypoint(7.7765, -88.703, ProceedCondition.BOTH_JOINTS_REACHED, 3000));
		waypoints.put(AnimationState.HIGH_GOAL, 			new Waypoint(-17.379, -66.61, ProceedCondition. BOTH_JOINTS_REACHED, 3000));
	}

	// update once per Arms update loop
	public void update() {
		
		// Executes the state machine as long as target state is not reached
		switch(currentAnimationState) {
			case DISABLED:
				if(targetAnimationState != currentAnimationState) {
					// TODO: resume could've happened at any angle, we need to find nearest state before pathing to target state
				}
				break;

			case UNSTOWED:
				// decide if we need to move to next state yet
				if(targetAnimationState != currentAnimationState) {
					if(targetAnimationState == AnimationState.STOWED) {
						// Invalid, no STOWED state for now	
						log("Invalid state, no STOWED state yet");
					} else {
						// Everything else goes to PREP first
						switchState(AnimationState.PREP);
					}
				}
				break;

			case PLAYER_STATION_CONE:
				if(targetAnimationState != currentAnimationState) {
					// TODO: figure out next state if we're moving out of unstowed
				}
				break;

			case MID_GOAL:
				if(targetAnimationState != currentAnimationState) {
					// TODO: figure out next state if we're moving out of unstowed
				}
				break;

			case HIGH_GOAL:
				if(targetAnimationState != currentAnimationState) {
					// TODO: figure out next state if we're moving out of unstowed
				}
				break;

			default:
				log("Invalid state " + currentAnimationState);
				return;
		}


		// TODO
		// ensure that position for desired state is kept
	}

	


	/**
	 * Externally/OI requested target state
	 * @param targetState
	 */
	public void changeState(AnimationState targetState) {
		// valid states: DISABLED, STOWED, PLAYER_STATION_CONE, MID_GOAL, HIGH_GOAL
		switch(targetState) {
		case DISABLED:
			targetAnimationState = AnimationState.DISABLED;
			break;

		case UNSTOWED:
			targetAnimationState = AnimationState.UNSTOWED;
			break;

		case PLAYER_STATION_CONE:
			targetAnimationState = AnimationState.PLAYER_STATION_CONE;
			break;

		case MID_GOAL:
			targetAnimationState = AnimationState.MID_GOAL;
			break;

		case HIGH_GOAL:
			targetAnimationState = AnimationState.HIGH_GOAL;
			break;

		default:
			log("Invalid state " + targetState);
			return;
		}

		// TODO future : special case for stowing, to keep applying power
	}

	/**
	 * Actually switch currentAnimationState to the next state and desired motor actions
	 * @param targetState
	 */
	private void switchState(AnimationState targetState) {
		Waypoint currentWaypoint = waypoints.getOrDefault(targetState, null);
		if(currentWaypoint == null) {
			log("Errorneous state " + targetState);
			return;
		}

		// Set motor stuff
		parent.pidForArmOne(currentWaypoint.jointOneAngleDegrees, currentWaypoint.entryVelocity);
		parent.pidForArmTwo(currentWaypoint.jointTwoAngleDegrees, currentWaypoint.entryVelocity);
		currentAnimationState = targetState;
	}

	private boolean isCurrentAnimationStateReached() {
		Waypoint currentWaypoint = waypoints.getOrDefault(currentAnimationState, null);
		if(currentWaypoint == null) {
			log("Errorneous state " + currentAnimationState);
			return false;
		}

		switch(currentWaypoint.proceedCondition) {
			case BOTH_JOINTS_REACHED:
				return !parent.isFirstJointPidding() && !parent.isSecondJointPidding();

			case JOINT_ONE_REACHED_ONLY:
				return !parent.isFirstJointPidding();

			case JOINT_TWO_REACHED_ONLY:
				return !parent.isSecondJointPidding();

			default:
				log("Errorneous proceedCondition " + currentWaypoint.proceedCondition);
				return false;
		}
	}
}