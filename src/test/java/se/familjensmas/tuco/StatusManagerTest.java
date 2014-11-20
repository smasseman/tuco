package se.familjensmas.tuco;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jorgen.smas@entercash.com
 */
public class StatusManagerTest {

	public class CallInfo {
		Status<Integer> status;
		long callDuration;
	}

	ExecutorService executor = Executors.newFixedThreadPool(2);

	@Test
	public void test() throws Exception {
		StatusManager<Integer> mgr = new StatusManager<>(7);
		mgr.setTimeout(3);
		FutureTask<CallInfo> callInfo = getStatus(mgr, -1);
		expectDuration(callInfo, 0, 300);

		callInfo = getStatus(mgr, callInfo.get().status.getModcount());
		expectDuration(callInfo, 2000, 5000);

		callInfo = getStatus(mgr, callInfo.get().status.getModcount());
		Thread.sleep(2000);
		mgr.update(99);
		expectDuration(callInfo, 2000, 3000);
	}

	private CallInfo expectDuration(FutureTask<CallInfo> callInfo, long min, long max) throws InterruptedException,
			ExecutionException, TimeoutException {
		CallInfo info = callInfo.get(10, TimeUnit.SECONDS);
		if (info.callDuration < min)
			Assert.fail("Min duration was " + min + " but actual duration was " + info.callDuration);
		if (info.callDuration > max)
			Assert.fail("Max duration was " + max + " but actual duration was " + info.callDuration);
		return info;
	}

	private FutureTask<CallInfo> getStatus(final StatusManager<Integer> mgr, final int modcount) {
		Callable<CallInfo> callable = new Callable<CallInfo>() {

			@Override
			public CallInfo call() throws Exception {
				CallInfo c = new CallInfo();
				long start = System.currentTimeMillis();
				c.status = mgr.getStatus(modcount);
				long stop = System.currentTimeMillis();
				c.callDuration = stop - start;
				return c;
			}
		};
		FutureTask<CallInfo> task = new FutureTask<CallInfo>(callable);
		executor.execute(task);
		return task;
	}
}
