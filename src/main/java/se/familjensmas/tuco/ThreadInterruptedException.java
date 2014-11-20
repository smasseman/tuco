package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
@SuppressWarnings("serial")
public class ThreadInterruptedException extends RuntimeException {

	public ThreadInterruptedException(InterruptedException e) {
		super(e);
	}
}
