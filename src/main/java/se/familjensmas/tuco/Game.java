package se.familjensmas.tuco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author jorgen.smas@entercash.com
 */
@Service
public class Game {

	public class TargetThread extends Thread {

		private long delay;
		private Target target;
		private volatile long duration;

		public TargetThread(long delay, Target t) {
			this.delay = delay;
			this.target = t;
		}

		public long getDuration() {
			return duration;
		}

		@Override
		public void run() {
			try {
				sleep(delay);
				long startTS = target.up();
				logger.debug("Show target " + target.getId() + "and wait for hit.");
				int upSeconds = 5;
				if (target.getHitListener().waitForHit(upSeconds, TimeUnit.SECONDS)) {
					long stopTS = System.currentTimeMillis();
					duration = stopTS - startTS;
					send("Träff! Efter " + formatDuration(duration) + " sekunder.");
				} else {
					duration = upSeconds * 1000;
					logger.debug("No hit after " + formatDuration(duration) + " seconds.");
					send("Ingen träff efter " + upSeconds + " sekunder.");
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
					logger.debug("Start new game. Reset all servos.");
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
		send("Redo!");
		List<Target> servoList = getTargetsInRandomOrder();
		long delay = random(config.getLong("game_random_sleep_min"), config.getLong("game_random_sleep_max"));
		long delaySum = 0;
		List<TargetThread> threads = new ArrayList<>();
		for (Target t : servoList) {
			TargetThread thread = new TargetThread(delay, t);
			threads.add(thread);
			delaySum += delay;
			delay = delaySum + config.getLong("game_next_target_delay");
		}
		threads.forEach(thread -> thread.start());
		threads.forEach(thread -> join(thread));
		for (TargetThread t : threads) {
			logger.debug("Duration " + t.getDuration());
		}
		long duration = threads.stream().mapToLong(TargetThread::getDuration).sum();
		send("Total tid: " + formatDuration(duration) + " sekunder.");
		send("Game over.");
		status.setDone(true);
		statusManager.update(status);
	}

	private void join(Thread t) {
		try {
			t.join();
		} catch (InterruptedException ignored) {
		}
	}

	private List<Target> getTargetsInRandomOrder() {
		List<Target> targets = new ArrayList<>();
		config.allActiveTargets().stream().forEach(t -> targets.add(t));
		Collections.shuffle(targets);
		return targets;
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
