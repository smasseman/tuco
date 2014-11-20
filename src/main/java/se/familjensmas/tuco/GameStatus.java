package se.familjensmas.tuco;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jorgen.smas@entercash.com
 */
public class GameStatus {

	private List<String> messages = new LinkedList<>();
	private boolean done;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[done=");
		builder.append(done);
		builder.append(", messages=");
		builder.append(messages);
		builder.append("]");
		return builder.toString();
	}

	public boolean isDone() {
		return done;
	}

	public synchronized void setDone(boolean done) {
		this.done = done;
	}

	public synchronized void addMessage(String message) {
		messages.add(message);
	}

	public synchronized void reset() {
		done = false;
		messages.clear();
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
