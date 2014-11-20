package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
public class Status<S> {

	private int modcount;
	private S data;

	public int getModcount() {
		return modcount;
	}

	public void setModcount(int modcount) {
		this.modcount = modcount;
	}

	public S getData() {
		return data;
	}

	public void setData(S data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append("[modcount=");
		builder.append(modcount);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}
