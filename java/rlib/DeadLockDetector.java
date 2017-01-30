/*
 * This file is part of TJServer.
 * 
 * TJServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TJServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package rlib;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

public class DeadLockDetector extends SafeTask
{
	private static final Logger log = Loggers.getLogger(DeadLockDetector.class);
	private final Array<DeadLockListener> listeners;
	private final ThreadMXBean mxThread;
	private final ScheduledExecutorService executor;
	private volatile ScheduledFuture<?> schedule;
	private final int interval;
	
	public DeadLockDetector(int interval)
	{
		if (interval < 1)
		{
			throw new IllegalArgumentException("negative interval.");
		}
		listeners = Arrays.toConcurrentArray(DeadLockListener.class);
		mxThread = ManagementFactory.getThreadMXBean();
		executor = Executors.newSingleThreadScheduledExecutor();
		this.interval = interval;
	}
	
	@Override
	protected void runImpl()
	{
		long[] threadIds = mxThread.findDeadlockedThreads();
		if (threadIds.length < 1)
		{
			return;
		}
		int i = 0;
		int length = threadIds.length;
		while (i < length)
		{
			ThreadInfo info;
			long id = threadIds[i];
			info = mxThread.getThreadInfo(id);
			if (!listeners.isEmpty())
			{
				listeners.readLock();
				try
				{
					DeadLockListener[] array = listeners.array();
					int g = 0;
					int size = listeners.size();
					while (g < size)
					{
						array[g].onDetected(info);
						++g;
					}
				}
				finally
				{
					listeners.readUnlock();
				}
			}
			log.warning("DeadLock detected! : " + info);
			++i;
		}
	}
	
	public synchronized void start()
	{
		if (schedule != null)
		{
			return;
		}
		schedule = executor.scheduleAtFixedRate(this, interval, interval, TimeUnit.MILLISECONDS);
	}
	
	public synchronized void stop()
	{
		if (schedule == null)
		{
			return;
		}
		schedule.cancel(false);
		schedule = null;
	}
	
	public void addListener(DeadLockListener listener)
	{
		listeners.add(listener);
	}
}
