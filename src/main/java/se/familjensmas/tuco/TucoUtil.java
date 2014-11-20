package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
public class TucoUtil {

	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			throw new ThreadInterruptedException(e);
		}
	}

}
