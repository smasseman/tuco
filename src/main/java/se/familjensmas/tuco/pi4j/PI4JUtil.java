package se.familjensmas.tuco.pi4j;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author jorgen.smas@entercash.com
 */
public class PI4JUtil {

	public static Pin getPin(int pinNumber) {
		try {
			String name;
			if (pinNumber < 10)
				name = "GPIO_0" + pinNumber;
			else
				name = "GPIO_" + pinNumber;
			return (Pin) RaspiPin.class.getField(name).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
