package se.familjensmas.tuco.web;

import java.util.ArrayList;
import java.util.List;

import se.familjensmas.tuco.Target;

/**
 * @author jorgen.smas@entercash.com
 */
public class SystemStatus {

	private List<Target> targets = new ArrayList<Target>();

	public List<Target> getTargets() {
		return targets;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	@Override
	public String toString() {
		return targets.toString();
	}
}
