package se.familjensmas.tuco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jorgen.smas@entercash.com
 */
public class Servo {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void set(Angle a) {
		logger.debug("Set angle to " + a);
		setAngle(a.intValue());
	}

	protected void setAngle(int intValue) {
	}

	public void off() {
		logger.debug("Turn off servo.");
	}
}