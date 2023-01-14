package com.team766.math;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class IsometricTransform {
	public final Rotation rotation;
	public final Vector3D translation;
	
	public static final IsometricTransform IDENTITY =
		new IsometricTransform(Rotation.IDENTITY, Vector3D.ZERO);
	
	public IsometricTransform(Rotation rotation, Vector3D translation) {
		this.rotation = rotation;
		this.translation = translation;
	}
	
	Vector3D applyInverseTo(Vector3D u) {
		return rotation.applyInverseTo(u.subtract(translation));
	}
	
	Vector3D applyTo(Vector3D u) {
		return rotation.applyTo(u).add(translation);
	}
	
	IsometricTransform compose(IsometricTransform other) {
		return new IsometricTransform(rotation.compose(other.rotation, RotationConvention.VECTOR_OPERATOR),
		                              rotation.applyTo(other.translation).add(translation));
	}
	
	IsometricTransform composeInverse(IsometricTransform other) {
		return new IsometricTransform(rotation.composeInverse(other.rotation, RotationConvention.VECTOR_OPERATOR),
		                              rotation.applyInverseTo(other.translation).subtract(translation));
	}
	
	IsometricTransform invert() {
		return new IsometricTransform(rotation.revert(), rotation.applyInverseTo(translation));
	}
}
