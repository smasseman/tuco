package se.familjensmas.tuco.web;

/**
 * @author jorgen.smas@entercash.com
 */
public class ServerEvent {

	private String event;
	private String data;
	private Long id;

	public ServerEvent(String data) {
		super();
		this.data = data;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return (event == null ? "" : event) + ":" + data;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
