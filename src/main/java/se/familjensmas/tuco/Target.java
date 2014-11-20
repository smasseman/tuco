package se.familjensmas.tuco;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jorgen.smas@entercash.com
 */
public class Target {

	private TargetId id;
	private Servo servo;
	private HitListener hitListener;
	private Config config;
	private List<TargetListener> listeners = new ArrayList<>();
	private TargetState state = TargetState.DOWN;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private boolean active;

	@Override
	public String toString() {
		return "[" + id + "=" + state + "]";
	}

	public void down() {
		state = TargetState.DOWN;
		notifyListeners();
		servo.set(config.getDownAngle(id));
		TucoUtil.sleep(config.getDownDuration());
		servo.set(config.getCenterAngle(id));
		TucoUtil.sleep(config.getCenterFromDownDuration());
		servo.off();
	}

	public long up() {
		state = TargetState.UP;
		notifyListeners();
		servo.set(config.getUpAngle(id));
		TucoUtil.sleep(config.getUpDuration());
		long ts = System.currentTimeMillis();
		servo.set(config.getCenterAngle(id));
		TucoUtil.sleep(config.getCenterFromUpDuration());
		servo.off();
		return ts;
	}

	private void notifyListeners() {
		listeners.forEach(l -> {
			l.targetUpdated(this);
			logger.debug("Notified " + l);
		});
	}

	@JsonIgnore
	public HitListener getHitListener() {
		return hitListener;
	}

	@JsonIgnore
	public Servo getServo() {
		return servo;
	}

	public TargetId getId() {
		return id;
	}

	public void setId(TargetId id) {
		this.id = id;
	}

	public void setServo(Servo servo) {
		this.servo = servo;
	}

	public void setHitListener(HitListener hitListener) {
		this.hitListener = hitListener;
	}

	@JsonIgnore
	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void addTargetListener(TargetListener listener) {
		listeners.add(listener);
		logger.debug("Added target listener " + listener);
	}

	public void removeTargetListener(TargetListener listener) {
		listeners.remove(listener);
	}

	public TargetState getState() {
		return state;
	}

	public void setState(TargetState state) {
		this.state = state;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
