package com.team766.library;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.junit.Test;

public class LossyPriorityQueueTest {
	@Test
	public void test() throws InterruptedException {
		final int N = 100;
		var queue =
			new LossyPriorityQueue<Integer>(N, Comparator.naturalOrder());
		var producerThread = new Thread(() -> {
			for (Integer i = 0; i < N; ++i) {
				queue.add(i);
			}
		});
		producerThread.start();
		for (Integer i = 0; i < N; ++i) {
			assertEquals(i, queue.poll());
		}
		producerThread.join();
	}
}