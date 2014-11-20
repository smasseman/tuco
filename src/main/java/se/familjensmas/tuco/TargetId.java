package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
public class TargetId {

	private int id;

	public static TargetId fromInt(int tid) {
		TargetId id = new TargetId();
		id.id = tid;
		return id;
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	public int intValue() {
		return id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetId other = (TargetId) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
