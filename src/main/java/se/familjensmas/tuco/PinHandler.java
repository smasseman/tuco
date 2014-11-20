package se.familjensmas.tuco;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.familjensmas.tuco.pi4j.PI4JUtil;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * @author jorgen.smas@entercash.com
 */
@Service
public class PinHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private GpioController gpio;

	public void registerPinListener(HitListener hitListener, int pinNumber) {
		Pin x = PI4JUtil.getPin(pinNumber);
		GpioPinDigitalInput input = gpio.provisionDigitalInputPin(x, x.getName(), PinPullResistance.PULL_UP);
		GpioPinListenerDigital listener = new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				logger.debug("Got event " + event.getState());
				if (event.getState() == PinState.HIGH) {
					hitListener.trigger();
					logger.debug("Pin {} triggered. Notify {}", pinNumber, hitListener);
				}
			}
		};
		input.addListener(listener);
		logger.debug("{} is added as listener on pin {}.", hitListener, x);
	}

	public GpioController getGpio() {
		return gpio;
	}

	public void setGpio(GpioController gpio) {
		this.gpio = gpio;
	}
}
