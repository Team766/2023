package com.team766.library;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LossyPriorityQueue<E> {
	private final Lock m_lock = new ReentrantLock();
	private final Condition m_empty = m_lock.newCondition();
	private final Condition m_notEmpty = m_lock.newCondition();

	private final PriorityQueue<E> m_items;
	private final int m_capacity;

	public LossyPriorityQueue(int capacity, Comparator<E> comparator) {
		m_capacity = capacity;
		m_items = new PriorityQueue<E>(m_capacity, comparator);
	}

	public void add(E element) {
		m_lock.lock();
		try {
			while (m_items.size() > m_capacity - 1) {
				m_items.poll();
			}
			m_items.add(element);
			m_notEmpty.signal();
		} finally {
			m_lock.unlock();
		}
	}

	public E poll() throws InterruptedException {
		m_lock.lock();
		try {
			while (m_items.size() == 0) {
				m_notEmpty.await();
			}
			E element = m_items.poll();
			if (m_items.size() == 0) {
				m_empty.signal();
			}
			return element;
		} finally {
			m_lock.unlock();
		}
	}
	
	public void waitForEmpty() throws InterruptedException {
		m_lock.lock();
		try {
			while (!m_items.isEmpty()) {
				m_empty.await();
			}
		} finally {
			m_lock.unlock();
		}
	}
}