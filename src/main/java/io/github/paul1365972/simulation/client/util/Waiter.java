package io.github.paul1365972.simulation.client.util;

public class Waiter {
	
	private final Object lock = new Object();
	private volatile boolean blocked = true;
	
	public void await() throws InterruptedException {
		if (blocked) {
			synchronized (lock) {
				while (blocked) {
					lock.wait();
				}
			}
		}
	}
	
	public void unblock() {
		if (blocked) {
			blocked = false;
			synchronized (lock) {
				lock.notify();
			}
		}
	}
}
