package se.familjensmas.tuco.pi4j.mock;

import com.pi4j.io.gpio.GpioProviderBase;
import com.pi4j.io.gpio.RaspiGpioProvider;

/**
 * @author jorgen.smas@entercash.com
 */
public class GpioProviderMock extends GpioProviderBase {

	@Override
	public String getName() {
		return RaspiGpioProvider.NAME;
	}

}