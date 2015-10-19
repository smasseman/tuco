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
	private GpioPinDigitalOutput pin;

	@PostConstruct
	public void init() throws Exception {
		Pin p = PI4JUtil.getPin(pinNumber);
		pin = gpio.provisionDigitalOutputPin(p, null, PinState.LOW);
		playStartTrudelutt();
	}

	private void playStartTrudelutt() {
		logger.debug("Going to beep...");
		new Thread(() -> {
			try {
				logger.debug("Beep on " + pin);
				beepOn();
				Thread.sleep(500);
				beepOff();
				Thread.sleep(200);
				beepOn();
				Thread.sleep(500);
				beepOff();
				logger.debug("Beep off.");
			} catch (Exception e) {
				logger.warn("Faild to beep.", e);
			}
		}).start();
	}

	public void beepOff() {
		pin.low();
	}

	public void beepOn() {
		pin.high();
	}

	public static void main(String[] args) throws Exception {
		java.util.logging.Logger.getLogger("com.pi4j").setLevel(Level.FINEST);
		StartupBeep b = new StartupBeep();
		b.pinNumber = Integer.parseInt(args[0]);
		b.gpio = GpioFactory.getInstance();
		b.init();
		Thread.sleep(5000);
	}
}
