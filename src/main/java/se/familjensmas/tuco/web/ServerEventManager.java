package se.familjensmas.tuco.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.familjensmas.util.event.Listener;
import se.familjensmas.util.event.ListenerManager;

/**
 * @author jorgen.smas@entercash.com
 */
public class ServerEventManager {

	public class EventListener implements Listener<ServerEvent> {

		private List<ServerEvent> events = new LinkedList<>();

		@Override
		public void eventNotification(ServerEvent event) {
			synchronized (events) {
				events.add(event);
				events.notifyAll();
			}
		}

		public ServerEvent getNextEvent() throws InterruptedException {
			synchronized (events) {
				if (events.isEmpty()) {
					events.wait(15 * 1000);
					if (events.isEmpty())
						return null;
				}
				return events.remove(0);
			}
		}
	}

	private Logger logger = LoggerFactory.getLogger(getClass());
	private ListenerManager<ServerEvent> listeners = new ListenerManager<>();

	public void pushEvent(ServerEvent event) {
		if (event == null)
			throw new NullPointerException();
		logger.debug("Add new event: " + event);
		listeners.notifyListeners(event);
	}

	public void get(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		EventListener listener = new EventListener();
		listeners.addListener(listener);
		try {
			while (true) {
				ServerEvent e = listener.getNextEvent();
				if (e == null)
					sendKeepAliveMessage(response);
				else {
					sendEvent(e, response);
				}
				response.getWriter().flush();
				if (response.getWriter().checkError()) {
					logger.debug("Drop this connection since there was an error while writing.");
					return;
				}
			}
		} finally {
			listeners.removeListener(listener);
		}
	}

	private void sendKeepAliveMessage(HttpServletResponse response) throws IOException {
		response.getWriter().write(":\n");
		logger.debug("Sent keep alive message.");
	}

	private void sendEvent(ServerEvent e, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		if (e.getEvent() != null) {
			writer.write(e.getEvent() + "\n");
		}
		String data = e.getData().trim();
		try (Scanner s = new Scanner(data)) {
			s.useDelimiter("\n");
			while (s.hasNext()) {
				writer.write("data: " + s.next() + "\n");
			}
		}
		writer.write("\n\n");
		logger.debug("Written event: " + e);
	}
}
