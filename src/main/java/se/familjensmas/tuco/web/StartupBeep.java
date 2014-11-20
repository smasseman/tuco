package se.familjensmas.tuco.web;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.familjensmas.tuco.pi4j.PI4JUtil;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * @author jorgen.smas@entercash.com
 */
@Service
public class StartupBeep {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private GpioController gpio;
	private int pinNumber = 6;

	@PostConstruct
	public void beep() throws Exception {
		logger.debug("Going to beep...");
		new Thread(() -> {
			try {
				Pin p = PI4JUtil.getPin(pinNumber);
				GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(p, null, PinState.LOW);
				logger.debug("Beep on " + pin);
				pin.high();
				Thread.sleep(500);
				pin.low();
				Thread.sleep(200);
				pin.high();
				Thread.sleep(500);
				pin.low();
				logger.debug("Beep off.");
			} catch (Exception e) {
				logger.warn("Faild to beep.", e);
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		java.util.logging.Logger.getLogger("com.pi4j").setLevel(Level.FINEST);
		StartupBeep b = new StartupBeep();
		b.pinNumber = Integer.parseInt(args[0]);
		b.gpio = GpioFactory.getInstance();
		b.beep();
		Thread.sleep(5000);
	}
}
