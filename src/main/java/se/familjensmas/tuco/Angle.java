package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
public class Angle {

	private int angle;

	public static Angle fromInt(int a) {
		Angle angle = new Angle();
		angle.angle = a;
		return angle;
	}

	@Override
	public String toString() {
		return String.valueOf(angle);
	}

	public int intValue() {
		return angle;
	}
}
