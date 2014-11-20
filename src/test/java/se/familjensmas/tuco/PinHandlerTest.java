package se.familjensmas.tuco;

import org.junit.Test;

import se.familjensmas.tuco.pi4j.mock.GpioProviderMock;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;

/**
 * @author jorgen.smas@entercash.com
 */
public class PinHandlerTest {

	@Test
	public void test() {
		PinHandler h = new PinHandler();
		// GpioController gpio = new
		// com.pi4j.io.gpio.impl.GpioControllerImpl(new GpioControllerMock());
		GpioProvider provider = new GpioProviderMock();
		GpioFactory.setDefaultProvider(provider);
		GpioController gpio = GpioFactory.getInstance();
		h.setGpio(gpio);
		HitListener listener = new HitListener();
		h.registerPinListener(listener, 1);

	}
}
