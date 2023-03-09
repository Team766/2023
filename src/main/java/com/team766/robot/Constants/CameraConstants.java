package com.team766.robot.Constants;

public class CameraConstants {

  //Camera names
  public static String FRONT_LEFT_CAMERA_NAME = "LeftCamera";
  public static String FRONT_RIGHT_CAMERA_NAME = "RightCamera";

  //Camera weights
  public static double FRONT_LEFT_CAMERA_WEIGHT = 1.0;
  public static double FRONT_RIGHT_CAMERA_WEIGHT = 1.0;

  //Camera offsets
  public static double FRONT_LEFT_CAMERA_OFFSET_X = 0.022;
  public static double FRONT_LEFT_CAMERA_OFFSET_Y = 0.358;
  public static double FRONT_LEFT_CAMERA_OFFSET_Z = 0.838;
  public static double FRONT_LEFT_CAMERA_OFFSET_PITCH = 15;
  public static double FRONT_LEFT_CAMERA_OFFSET_YAW = 45;
  public static double FRONT_LEFT_CAMERA_OFFSET_ROLL = 0;

  public static double FRONT_RIGHT_CAMERA_OFFSET_X = 0.022;
  public static double FRONT_RIGHT_CAMERA_OFFSET_Y = -0.358;
  public static double FRONT_RIGHT_CAMERA_OFFSET_Z = 0.838;
  public static double FRONT_RIGHT_CAMERA_OFFSET_PITCH = 15;
  public static double FRONT_RIGHT_CAMERA_OFFSET_YAW = -45;
  public static double FRONT_RIGHT_CAMERA_OFFSET_ROLL = 0;

  //Camera field layout
  public static String FIELD_LAYOUT_FILE = "Field.json";
}
