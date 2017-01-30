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
package tera.remotecontrol.handlers;

import java.util.ArrayList;
import java.util.List;

import tera.gameserver.manager.AnnounceManager;
import tera.gameserver.tasks.AnnounceTask;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;

import rlib.util.array.Array;

/**
 * @author Ronn
 * @created 08.04.2012
 */
public class AnnounceApplyHandler implements PacketHandler
{
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Packet processing(Packet packet)
	{
		final AnnounceManager announceManager = AnnounceManager.getInstance();
		{
			final List<String> startingAnnounces = packet.next(ArrayList.class);
			final Array<String> current = announceManager.getStartAnnouncs();
			current.clear();
			
			for (String announce : startingAnnounces)
			{
				current.add(announce);
			}
			
			current.trimToSize();
		}
		{
			final List<Object[]> runningAnnounces = packet.next(ArrayList.class);
			final Array<AnnounceTask> current = announceManager.getRuningAnnouncs();
			
			for (AnnounceTask task : current)
			{
				task.cancel();
			}
			
			current.clear();
			
			for (Object[] announce : runningAnnounces)
			{
				current.add(new AnnounceTask(announce[0].toString(), (int) announce[1]));
			}
		}
		announceManager.save();
		return null;
	}
}
