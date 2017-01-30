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
package tera.gameserver.manager;

import tera.gameserver.model.World;
import tera.gameserver.tasks.ShutdownTask;

/**
 * @author Ronn
 * @created 25.04.2012
 */
public final class ShutdownManager
{
	
	private static final ShutdownTask task = new ShutdownTask();
	
	public static final void cancel()
	{
		if (task.cancel())
		{
			World.sendAnnounce("Shutting down the server is been interrupted.");
		}
	}
	
	public static final void restart(long delay)
	{
		task.next(true, Math.max(delay, 120000));
	}
	
	public static final void shutdown(long delay)
	{
		task.next(false, Math.max(delay, 120000));
	}
	
	private ShutdownManager()
	{
		throw new IllegalArgumentException();
	}
}
