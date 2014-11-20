package se.familjensmas.tuco;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * @author jorgen.smas@entercash.com
 */
@Configuration
@Profile("rpi")
public class GpioControllerRpiFactory {

	private GpioController gpio;

	@Bean
	public GpioController gpio() {
		return gpio;
	}

	@PostConstruct
	public void init() {
		gpio = GpioFactory.getInstance();
		LoggerFactory.getLogger(getClass()).info("Inited.");
	}

	@PreDestroy
	public void destroy() {
		gpio.shutdown();
	}
}
