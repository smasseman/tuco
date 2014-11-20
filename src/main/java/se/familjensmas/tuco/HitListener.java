package se.familjensmas.tuco;

import java.util.concurrent.TimeUnit;

/**
 * @author jorgen.smas@entercash.com
 */
public class HitListener {

	private long timestamp;

	public void trigger() {
		synchronized (this) {
			timestamp = System.currentTimeMillis();
			this.notifyAll();
		}
	}

	public void waitForHit() throws InterruptedException {
		synchronized (this) {
			this.wait();
		}
	}

	public boolean waitForHit(int duration, TimeUnit unit) throws InterruptedException {
		synchronized (this) {
			long ts = timestamp;
			this.wait(unit.toMillis(duration));
			return ts != timestamp;
		}
	}
}
