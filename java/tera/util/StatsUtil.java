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
package tera.util;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public final class StatsUtil
{
	private static final MemoryMXBean memMXbean = ManagementFactory.getMemoryMXBean();
	private static final ThreadMXBean threadMXbean = ManagementFactory.getThreadMXBean();
	
	/**
	 * Method getBasicThreadStats.
	 * @return CharSequence
	 */
	public static CharSequence getBasicThreadStats()
	{
		return getThreadStats(false, false, false);
	}
	
	/**
	 * Method getFullThreadStats.
	 * @return CharSequence
	 */
	public static CharSequence getFullThreadStats()
	{
		return getThreadStats(true, true, true);
	}
	
	/**
	 * Method getMemFree.
	 * @return long
	 */
	public static long getMemFree()
	{
		final MemoryUsage heapMemoryUsage = memMXbean.getHeapMemoryUsage();
		return (heapMemoryUsage.getMax() - heapMemoryUsage.getUsed());
	}
	
	/**
	 * Method getMemFreeMb.
	 * @return String
	 */
	public static String getMemFreeMb()
	{
		return new StringBuilder().append(getMemFree() / 1048576L).append(" Mb").toString();
	}
	
	/**
	 * Method getMemMax.
	 * @return long
	 */
	public static long getMemMax()
	{
		return memMXbean.getHeapMemoryUsage().getMax();
	}
	
	/**
	 * Method getMemMaxMb.
	 * @return String
	 */
	public static String getMemMaxMb()
	{
		return new StringBuilder().append(getMemMax() / 1048576L).append(" Mb").toString();
	}
	
	/**
	 * Method getMemUsage.
	 * @return CharSequence
	 */
	public static CharSequence getMemUsage()
	{
		final double maxMem = memMXbean.getHeapMemoryUsage().getMax() / 1024 / 1024;
		final double allocatedMem = memMXbean.getHeapMemoryUsage().getCommitted() / 1024 / 1024;
		final double usedMem = memMXbean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
		final double nonAllocatedMem = maxMem - allocatedMem;
		final double cachedMem = allocatedMem - usedMem;
		final double useableMem = maxMem - usedMem;
		final StringBuilder list = new StringBuilder();
		list.append("Available memory: ........... ").append((int) maxMem).append(" MB").append("\n");
		list.append("     Allocated: .......... ").append((int) allocatedMem).append(" MB (").append(Math.round((allocatedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("\n");
		list.append("     Not distributed: ...... ").append((int) nonAllocatedMem).append(" MB (").append(Math.round((nonAllocatedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("\n");
		list.append("Allocated memory: ......... ").append((int) allocatedMem).append(" MB").append("\n");
		list.append("     Used: ............... ").append((int) usedMem).append(" MB (").append(Math.round((usedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("\n");
		list.append("     Not used (cache): .... ").append((int) cachedMem).append(" MB (").append(Math.round((cachedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("\n");
		list.append("Ready to use: ........... ").append((int) useableMem).append(" MB (").append(Math.round((useableMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("\n");
		return list;
	}
	
	/**
	 * Method getMemUsageAdm.
	 * @return CharSequence
	 */
	public static CharSequence getMemUsageAdm()
	{
		final double maxMem = memMXbean.getHeapMemoryUsage().getMax() / 1024 / 1024;
		final double allocatedMem = memMXbean.getHeapMemoryUsage().getCommitted() / 1024 / 1024;
		final double usedMem = memMXbean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
		final double nonAllocatedMem = maxMem - allocatedMem;
		final double cachedMem = allocatedMem - usedMem;
		final double useableMem = maxMem - usedMem;
		final StringBuilder list = new StringBuilder();
		list.append("<tr><td><center>Available memory: ........... ").append((int) maxMem).append(" MB").append("</center></td></tr>");
		list.append("<tr><td>     Allocated: .......... ").append((int) allocatedMem).append(" KB (").append(Math.round((allocatedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("</td></tr>");
		list.append("<tr><td>     Retained: ...... ").append((int) nonAllocatedMem).append(" MB (").append(Math.round((nonAllocatedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("</td></tr>");
		list.append("<tr><td><center>The allocated memory: ......... ").append((int) allocatedMem).append(" MB").append("</center></td></tr>");
		list.append("<tr><td>     Use: ............... ").append((int) usedMem).append(" MB (").append(Math.round((usedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("</td></tr>");
		list.append("<tr><td>     Not used (cached): .... ").append((int) cachedMem).append(" MB (").append(Math.round((cachedMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("</td></tr>");
		list.append("<tr><td>Living memory: ........... ").append((int) useableMem).append(" MB (").append(Math.round((useableMem / maxMem) * 1000000.0D) / 10000.0D).append("%)").append("</td></tr>");
		return list;
	}
	
	/**
	 * Method getMemUsed.
	 * @return long
	 */
	public static long getMemUsed()
	{
		return memMXbean.getHeapMemoryUsage().getUsed();
	}
	
	/**
	 * Method getMemUsedMb.
	 * @return String
	 */
	public static String getMemUsedMb()
	{
		return new StringBuilder().append(getMemUsed() / 1048576L).append(" Mb").toString();
	}
	
	/**
	 * Method getThreadStats.
	 * @param lockedMonitors boolean
	 * @param lockedSynchronizers boolean
	 * @param stackTrace boolean
	 * @return CharSequence
	 */
	public static CharSequence getThreadStats(boolean lockedMonitors, boolean lockedSynchronizers, boolean stackTrace)
	{
		final StringBuilder list = new StringBuilder();
		final int threadCount = threadMXbean.getThreadCount();
		final int daemonCount = threadMXbean.getThreadCount();
		final int nonDaemonCount = threadCount - daemonCount;
		final int peakCount = threadMXbean.getPeakThreadCount();
		final long totalCount = threadMXbean.getTotalStartedThreadCount();
		list.append("Live: .................... ").append(threadCount).append(" threads").append("\n");
		list.append("     Non-Daemon: ......... ").append(nonDaemonCount).append(" threads").append("\n");
		list.append("     Daemon: ............. ").append(daemonCount).append(" threads").append("\n");
		list.append("Peak: .................... ").append(peakCount).append(" threads").append("\n");
		list.append("Total started: ........... ").append(totalCount).append(" threads").append("\n");
		list.append("=================================================").append("\n");
		
		for (ThreadInfo info : threadMXbean.dumpAllThreads(lockedMonitors, lockedSynchronizers))
		{
			list.append("Thread #").append(info.getThreadId()).append(" (").append(info.getThreadName()).append(")").append("\n");
			list.append("=================================================\n");
			list.append("\tgetThreadState: ...... ").append(info.getThreadState()).append("\n");
			list.append("\tgetWaitedTime: ....... ").append(info.getWaitedTime()).append("\n");
			list.append("\tgetBlockedTime: ...... ").append(info.getBlockedTime()).append("\n");
			
			for (MonitorInfo monitorInfo : info.getLockedMonitors())
			{
				list.append("\tLocked monitor: ....... ").append(monitorInfo).append("\n");
				list.append("\t\t[").append(monitorInfo.getLockedStackDepth()).append(".]: at ").append(monitorInfo.getLockedStackFrame()).append("\n");
			}
			
			for (LockInfo lockInfo : info.getLockedSynchronizers())
			{
				list.append("\tLocked synchronizer: ...").append(lockInfo).append("\n");
			}
			
			if (stackTrace)
			{
				list.append("\tgetStackTace: ..........\n");
				
				for (StackTraceElement trace : info.getStackTrace())
				{
					list.append("\t\tat ").append(trace).append("\n");
				}
			}
			
			list.append("=================================================\n");
		}
		
		return list;
	}
}