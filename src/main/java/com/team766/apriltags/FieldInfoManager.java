package com.team766.apriltags;

import java.util.HashMap;
import java.util.Map;

public class FieldInfoManager {

	enum Mode {
		TEST1,
		COMPETITION
	}

	private static final Map<Integer,AprilTag> fieldTags = new HashMap<Integer, AprilTag>();
	
	public FieldInfoManager(Mode mode) {
		switch (mode) {
			case TEST1:
			// do stuff
			addTag(new AprilTag(1 /* id */, 2 /* x */, 1 /* y */, .2 /* z */, 0 /* rotation */));

			break;
			case COMPETITION:
			// insert AprilTag data for competition field
			addTag(new AprilTag(1 /* id */, 15.51 /* x */, 1.07 /* y */, 0.46 /* z */, 180 /* rotation */));
			addTag(new AprilTag(2 /* id */, 15.51 /* x */, 2.75 /* y */, 0.46 /* z */, 180 /* rotation */));
			addTag(new AprilTag(3 /* id */, 15.51 /* x */, 4.42 /* y */, 0.46 /* z */, 180 /* rotation */));
			addTag(new AprilTag(4 /* id */, 16.17 /* x */, 6.75 /* y */, 0.69 /* z */, 180 /* rotation */));
			addTag(new AprilTag(5 /* id */, 0.36 /* x */, 6.75 /* y */, 0.69 /* z */, 0 /* rotation */));
			addTag(new AprilTag(6 /* id */, 1.02 /* x */, 4.42 /* y */, 0.46 /* z */, 0 /* rotation */));
			addTag(new AprilTag(7 /* id */, 1.02 /* x */, 2.75 /* y */, 0.46 /* z */, 0 /* rotation */));
			addTag(new AprilTag(8 /* id */, 1.02 /* x */, 1.07 /* y */, 0.46 /* z */, 0 /* rotation */));

			break;
		}
	}

	private static void addTag(AprilTag tag) {
		fieldTags.put(tag.getId(), tag);
	}

	public static AprilTag getTagForID(int id) {
		return fieldTags.get(id);
	}
}