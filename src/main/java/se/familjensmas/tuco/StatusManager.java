package se.familjensmas.tuco;

/**
 * @author jorgen.smas@entercash.com
 */
public class StatusManager<S> {

	private S status;
	private Object lock = new Object();
	private int modCount;
	private int timeout = 20;

	public StatusManager(S status) {
		this.status = status;
	}

	public Status<S> getStatus(Integer modcount) throws InterruptedException {
		if (modcount == null)
			modcount = -1;
		synchronized (lock) {
			if (modcount.intValue() == modCount) {
				lock.wait(timeout * 1000);
			}
			Status<S> s = new Status<S>();
			s.setData(status);
			s.setModcount(modCount);
			return s;
		}
	}

	public void update(S status) {
		synchronized (lock) {
			this.status = status;
			modCount++;
			lock.notifyAll();
		}
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
