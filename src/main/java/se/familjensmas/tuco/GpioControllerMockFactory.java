package se.familjensmas.tuco;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.familjensmas.tuco.pi4j.mock.GpioProviderMock;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * @author jorgen.smas@entercash.com
 */
@Configuration
@Profile("default")
public class GpioControllerMockFactory extends GpioControllerRpiFactory {

	@Override
	@Bean
	public GpioController gpio() {
		return super.gpio();
	}

	@Override
	@PostConstruct
	public void init() {
		LoggerFactory.getLogger(getClass()).info("Mocking gpio stuff.");
		GpioFactory.setDefaultProvider(new GpioProviderMock());
		super.init();
	}
}
