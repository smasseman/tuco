package se.familjensmas.tuco;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import se.familjensmas.tuco.web.SettingsResult;
import se.familjensmas.util.event.Listener;
import se.familjensmas.util.event.ListenerManager;

/**
 * @author jorgen.smas@entercash.com
 */
@Configuration
@Repository
public class Config {

	private ArrayList<Target> targets;
	private List<TargetListener> targetListeners = new ArrayList<TargetListener>();
	@Resource
	private PinHandler pinHandler;

	@PostConstruct
	public void init() {
		readMap();
		createTargets();
	}

	public Map<String, String> toMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<>(map);
		result.keySet().removeIf(k -> k.startsWith("#"));
		return result;
	}

	protected LinkedHashMap<String, String> map;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ListenerManager<SettingsResult> listeners = new ListenerManager<>();

	public void addListener(Listener<SettingsResult> listener) {
		listeners.addListener(listener);
	}

	protected void readMap() {
		try {
			map = new LinkedHashMap<>();
			try (Scanner scan = new Scanner(getFile())) {
				scan.forEachRemaining(line -> {
					String[] split = line.split("=");
					String key = split[0];
					String value;
					if (split.length == 1) {
						value = "";
					} else {
						value = split[1];
					}
					map.put(key, value);
					logger.debug("Config: " + key + "=" + value);
				});
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected File getBaseDirectory() {
		return new File(".").getAbsoluteFile();
	}

	protected void writeMap() {
		try {
			try (PrintWriter w = new PrintWriter(getFile())) {
				map.forEach((k, v) -> {
					w.println(k + "=" + v);
				});
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Long getLong(String key) {
		return new Long(map.get(key));
	}

	public void addTargetListener(TargetListener l) {
		targetListeners.add(l);
		targets.forEach(t -> t.addTargetListener(l));
		logger.debug("Added target listener " + l);
	}

	private void createTargets() {
		targets = new ArrayList<>();
		for (int i = 1; i < 1 + getNumberOfUnits(); i++) {
			Target t = new Target();
			t.setId(TargetId.fromInt(i));
			t.setConfig(this);
			t.setActive("true".equals(map.get("active_target_" + i)));
			HitListener hitListener = new HitListener();
			t.setHitListener(hitListener);
			pinHandler.registerPinListener(hitListener, Integer.parseInt(map.get("hit_listener_pin_" + i)));
			Servo servo;// TODO fix this whit configuration object.
			if ("rpi".equals(System.getProperty("spring.profiles.active")))
				servo = new BlasterServo(Integer.parseInt(map.get("servo_pin_" + i)));
			else
				servo = new Servo();
			t.setServo(servo);
			targetListeners.forEach(listener -> t.addTargetListener(listener));
			targets.add(t);
		}
	}

	private void store(String key, Integer value) {
		store(key, value.toString());
	}

	private void store(String key, String value) {
		set(key, value);
	}

	public void remove(String key) {
		map.remove(key);
		writeMap();
	}

	public synchronized SettingsResult set(String key, String value) {
		String oldValue = map.put(key, value);
		SettingsResult result = new SettingsResult(key, value);
		if (!value.equals(oldValue)) {
			listeners.notifyListeners(result);
		}
		writeMap();
		return result;
	}

	protected File getFile() {
		return new File(getBaseDirectory(), "conf.cfg");
	}

	public void setUpAngle(TargetId id, Angle angle) {
		store("up_angle_" + id.intValue(), angle.intValue());
	}

	public Angle getUpAngle(TargetId id) {
		return Angle.fromInt(getLong("up_angle_" + id.intValue()).intValue());
	}

	public void setCenterAngle(TargetId id, Angle angle) {
		store("center_angle_" + id.intValue(), angle.intValue());
	}

	public Angle getCenterAngle(TargetId id) {
		return Angle.fromInt(Integer.parseInt(map.get("center_angle_" + id.intValue())));
	}

	public void setDownAngle(TargetId id, Angle angle) {
		store("down_angle_" + id.intValue(), angle.intValue());
	}

	public Angle getDownAngle(TargetId id) {
		return Angle.fromInt(Integer.parseInt(map.get("down_angle_" + id.intValue())));
	}

	public void setNumberOfUnits(int units) {
		store("targets", units);
	}

	public int getNumberOfUnits() {
		return Integer.parseInt(map.get("targets"));
	}

	public Collection<Target> allTargets() {
		return targets;
	}

	public Collection<Target> allActiveTargets() {
		return allTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList());
	}

	public Target getTarget(TargetId sid) {
		return targets.stream().filter(t -> t.getId().equals(sid)).findAny().get();
	}

	public long getDownDuration() {
		return Long.parseLong(map.get("down_duration"));
	}

	public void setDownDuration(long duration) {
		store("down_duration", String.valueOf(duration));
	}

	public long getUpDuration() {
		return Long.parseLong(map.get("up_duration"));
	}

	public void setUpDuration(long duration) {
		store("up_duration", String.valueOf(duration));
	}

	public long getCenterFromUpDuration() {
		return Long.parseLong(map.get("center_from_up_duration"));
	}

	public void setCenterFromUpDuration(long duration) {
		store("center_from_up_duration", String.valueOf(duration));
	}

	public long getCenterFromDownDuration() {
		return Long.parseLong(map.get("center_from_down_duration"));
	}

	public void setCenterFromDownDuration(long duration) {
		store("center_from_down_duration", String.valueOf(duration));
	}

	public void refresh() {
		readMap();
	}
}
