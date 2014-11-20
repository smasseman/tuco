package se.familjensmas.tuco.pi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * @author jorgen.smas@entercash.com
 */
public class Blink {

	public static void main(String[] args) throws InterruptedException {
		final GpioController gpio = GpioFactory.getInstance();
		Pin rpiPpin = PI4JUtil.getPin(Integer.parseInt(args[0]));
		final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(rpiPpin, "MyLED", PinState.LOW);
		System.out.println("ON " + rpiPpin);
		pin.high();
		Thread.sleep(1000);
		pin.low();
		System.out.println("OFF");
		gpio.shutdown();
	}
}
