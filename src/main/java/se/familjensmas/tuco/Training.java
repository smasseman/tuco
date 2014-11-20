package se.familjensmas.tuco;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author jorgen.smas@entercash.com
 */
@Service
public class Training {

	@Resource
	private Config config;
	private boolean running;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<Thread> threads = new LinkedList<>();

	@PostConstruct
	public void init() {
		for (Target target : config.allActiveTargets()) {
			Thread thread = new Thread(() -> {
				try {
					while (true) {
						target.getHitListener().waitForHit();
						if (running) {
							logger.info("Target down. Put up again.");
							target.up();
						}
					}
				} catch (Exception ignore) {
				}
			});
			threads.add(thread);
			thread.start();
		}
	}

	@PreDestroy
	public void destroy() {
		threads.forEach(thread -> {
			thread.interrupt();
		});
	}

	public void start() {
		running = true;
		config.allActiveTargets().forEach(target -> target.up());
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}
}