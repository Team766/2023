package com.team766.math;

import java.util.ArrayList;
import java.util.Hashtable;

public class TransformTree {
	private ArrayList<TransformTree> tree;
	
	private final String name;
	private TransformTree parent;
	private IsometricTransform transform;
	
	public TransformTree(String rootName) {
		this.name = rootName;
	}
	
	private TransformTree(String name, TransformTree parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public TransformTree addSubordinateTransform(String name) {
		TransformTree subtree = new TransformTree(name, this);
		tree.add(subtree);
		return subtree;
	}
	
	public void setLocalTransform(IsometricTransform xf) {
		transform = xf;
	}
	public IsometricTransform getLocalTransform() {
		return transform;
	}
	
	public IsometricTransform getTransformRelativeTo(String name) {
		TransformTree other = getRoot().findTransform(name);
		if (other == null) {
			throw new IllegalArgumentException("Can't find a transform named " + name);
		}
		return getTransformRelativeTo(other);
	}
	public IsometricTransform getTransformRelativeTo(TransformTree other) {
		Hashtable<String, IsometricTransform> parentChain = new Hashtable<String, IsometricTransform>();
		IsometricTransform xf = transform;
		parentChain.put(name, xf);
		TransformTree iterator = this.parent;
		while (iterator != null) {
			xf = iterator.transform.compose(xf);
			parentChain.put(iterator.name, xf);
			iterator = iterator.parent;
		}
		iterator = other;
		xf = other.transform;
		while (iterator != null) {
			IsometricTransform first = parentChain.get(iterator.name);
			if (first != null) {
				return first.compose(xf.invert());
			}
		}
		throw new IllegalArgumentException("Transforms aren't part of the same tree");
	}
	
	public TransformTree findTransform(String name) {
		if (this.name == name) {
			return this;
		}
		for (TransformTree sub : tree) {
			TransformTree subResult = sub.findTransform(name);
			if (subResult != null) {
				return subResult;
			}
		}
		return null;
	}
	
	private TransformTree getRoot() {
		TransformTree iterator = this;
		while (iterator.parent != null) {
			iterator = iterator.parent;
		}
		return iterator;
	}
}
