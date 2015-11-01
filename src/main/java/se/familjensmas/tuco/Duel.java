package se.familjensmas.tuco;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.familjensmas.tuco.web.StartupBeep;

/**
 * @author jorgen.smas@entercash.com
 */
@Service
public class Duel {

	@Resource
	private StartupBeep beep;

	private final int upSeconds = 5;

	public class TargetThread extends Thread {

		private final Target target;
		private final String outputPrefix;
		private volatile long duration;

		public TargetThread(String outputPrefix, Target t) {
			this.target = t;
			this.outputPrefix = outputPrefix;
		}

		public long getDuration() {
			return duration;
		}

		@Override
		public void run() {
			try {
				long startTS = target.up();
				logger.debug("Show target " + target.getId() + " and wait for hit.");
				if (target.getHitListener().waitForHit(upSeconds, TimeUnit.SECONDS)) {
					long stopTS = System.currentTimeMillis();
					duration = stopTS - startTS;
					send(outputPrefix + "Träff! " + formatDuration(duration) + " sekunder.");
				} else {
					duration = upSeconds * 1000;
					logger.debug("No hit after " + formatDuration(duration) + " seconds.");
					send(outputPrefix + "Ingen träff efter " + upSeconds + " sekunder.");
				}
			} catch (InterruptedException e) {
				logger.debug("Interrupte.");
			}
			target.down();
		}
	}

	private String formatDuration(long duration) {
		return new DecimalFormat("0.00").format(duration / 1000.0);
	}

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private Config config;
	private GameStatus status = new GameStatus();
	private StatusManager<GameStatus> statusManager = new StatusManager<>(status);

	private Thread thread;

	public void start() {
		status.reset();
		statusManager.update(status);
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					logger.debug("Start new duel. Reset all servos.");
					allDown();
					Thread.sleep(200);
					gameOn();
				} catch (InterruptedException e) {
					return;
				} finally {
					allDown();
					logger.debug("Game thread down.");
				}
			}
		});
		thread.start();
	}

	public void stop() {
		logger.debug("Stop game.");
		if (thread != null) {
			thread.interrupt();
		}
	}

	private void allDown() {
		config.allActiveTargets().stream().forEach(t -> t.down());
	}

	private void gameOn() throws InterruptedException {
		beep.beepOn();
		send("Redo!");
		Thread.sleep(1000);
		beep.beepOff();

		Iterator<Target> iter = config.allTargets().iterator();
		Target t1 = iter.next();
		Target t2 = iter.next();
		TargetThread tA = new TargetThread("A: ", t1);
		TargetThread tB = new TargetThread("B: ", t2);

		long delay = random(config.getLong("game_random_sleep_min"), config.getLong("game_random_sleep_max"));
		Thread.sleep(delay);

		tA.start();
		tB.start();

		tA.join();
		tB.join();

		if (tA.getDuration() < tB.getDuration() && tA.getDuration() < upSeconds * 1000) {
			send("Vinnare blev A.");
		} else if (tA.getDuration() > tB.getDuration() && tB.getDuration() < upSeconds * 1000) {
			send("Vinnare blev B.");
		} else {
			send("Båda var för långsamma.");
		}
		send("Game over.");
		status.setDone(true);
		statusManager.update(status);
	}

	private synchronized void send(String message) {
		status.addMessage(message);
		statusManager.update(status);
	}

	private long random(long minDuration, long maxDuration) throws InterruptedException {
		return new Random().nextInt((int) (maxDuration - minDuration)) + minDuration;
	}

	public StatusManager<GameStatus> getStatusManager() {
		return statusManager;
	}
}
