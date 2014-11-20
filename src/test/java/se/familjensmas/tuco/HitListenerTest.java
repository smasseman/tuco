package se.familjensmas.tuco;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jorgen.smas@entercash.com
 */
public class HitListenerTest {

	@Test
	public void testOK() throws InterruptedException {
		final HitListener l = new HitListener();
		delayedTrigger(l, 2000);
		Assert.assertTrue(l.waitForHit(4, TimeUnit.SECONDS));
	}

	@Test
	public void testTimeout() throws InterruptedException {
		final HitListener l = new HitListener();
		delayedTrigger(l, 2000);
		Assert.assertFalse(l.waitForHit(1, TimeUnit.SECONDS));
	}

	private void delayedTrigger(HitListener l, long delay) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					return;
				}
				l.trigger();
			}
		}).start();
	}
}
