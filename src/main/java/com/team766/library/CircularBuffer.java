package com.team766.library;

import java.util.AbstractCollection;
import java.util.Iterator;

public class CircularBuffer<E> extends AbstractCollection<E> {
	private Object[] buffer;
	private int count = 0;
	private int headIndex = 0;
	
	public CircularBuffer(int bufferLength) {
		buffer = new Object[bufferLength];
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < count;
			}

			@SuppressWarnings("unchecked")
			@Override
			public E next() {
				return (E) buffer[(headIndex + index++) % buffer.length];
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public E peek() {
		if (count == 0) {
			return null;
		}
		return (E) buffer[headIndex];
	}
	
	public E poll() {
		if (count == 0) {
			return null;
		}
		E element = peek();
		headIndex = (headIndex + 1) % buffer.length;
		--count;
		return element;
	}
	
	@Override
	public boolean add(E element) {
		if (count < buffer.length) {
			buffer[(headIndex + count) % buffer.length] = element;
			++count;
		} else {
			buffer[headIndex] = element;
			headIndex = (headIndex + 1) % buffer.length;
		}
		return true;
	}
	
	@Override
	public int size() {
		return count;
	}
	
	@Override
	public void clear() {
		count = 0;
	}
}
