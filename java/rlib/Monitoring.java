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
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.util.Date;

public abstract class Monitoring
{
	private static MemoryMXBean memoryMXBean;
	private static OperatingSystemMXBean operatingSystemMXBean;
	private static RuntimeMXBean runtimeMxBean;
	private static ThreadMXBean threadMXBean;
	
	public static final int getDeamonThreadCount()
	{
		return threadMXBean.getDaemonThreadCount();
	}
	
	public static final String getJavaVersion()
	{
		return runtimeMxBean.getSpecVersion();
	}
	
	public static final int getProcessorCount()
	{
		return operatingSystemMXBean.getAvailableProcessors();
	}
	
	public static final String getStartDate()
	{
		return DateFormat.getInstance().format(new Date(runtimeMxBean.getStartTime()));
	}
	
	public static final long getStartTime()
	{
		return runtimeMxBean.getStartTime();
	}
	
	public static final String getSystemArch()
	{
		return operatingSystemMXBean.getArch();
	}
	
	public static final double getSystemLoadAverage()
	{
		return operatingSystemMXBean.getSystemLoadAverage();
	}
	
	public static final String getSystemName()
	{
		return operatingSystemMXBean.getName();
	}
	
	public static final String getSystemVersion()
	{
		return operatingSystemMXBean.getVersion();
	}
	
	public static final int getThreadCount()
	{
		return threadMXBean.getThreadCount();
	}
	
	public static final long getUpTime()
	{
		return runtimeMxBean.getUptime();
	}
	
	public static final int getUsedMemory()
	{
		return (int) (memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024);
	}
	
	public static final String getVMName()
	{
		return runtimeMxBean.getVmName();
	}
	
	public static final void init()
	{
		memoryMXBean = ManagementFactory.getMemoryMXBean();
		operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		threadMXBean = ManagementFactory.getThreadMXBean();
	}
}
