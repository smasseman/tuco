package se.familjensmas.tuco.pi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * @author jorgen.smas@entercash.com
 */
public class Input {

	public static void main(String[] args) throws Exception {
		Integer pinNumber = Integer.parseInt(args[0]);
		String pinName = pinNumber.toString();
		if (pinName.length() == 1)
			pinName = "0" + pinName;
		pinName = "GPIO_" + pinName;
		PinPullResistance pull = (PinPullResistance) PinPullResistance.class.getField(args[1]).get(null);
		final GpioController gpio = GpioFactory.getInstance();
		Pin x = (Pin) RaspiPin.class.getField(pinName).get(null);

		GpioPinDigitalInput input = gpio.provisionDigitalInputPin(x, x.getName(), pull);
		GpioPinListenerDigital listener = new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println(event.getState());
			}
		};
		input.addListener(listener);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown.");
				gpio.shutdown();
			}
		});
		Thread.sleep(Long.MAX_VALUE);
	}
}
