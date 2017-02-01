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

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentAnnounce;
import tera.gameserver.model.SayType;
import tera.gameserver.model.listeners.PlayerSelectListener;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.tasks.AnnounceTask;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 29.03.2012
 */
public final class AnnounceManager implements PlayerSelectListener
{
	private static final Logger log = Loggers.getLogger(AnnounceManager.class);
	private static AnnounceManager instance;
	
	/**
	 * Method getInstance.
	 * @return AnnounceManager
	 */
	public static AnnounceManager getInstance()
	{
		if (instance == null)
		{
			instance = new AnnounceManager();
		}
		
		return instance;
	}
	
	private final Array<AnnounceTask> runingAnnouncs;
	private final Array<String> startAnnouncs;
	private final DocumentAnnounce document;
	
	private AnnounceManager()
	{
		runingAnnouncs = Arrays.toArray(AnnounceTask.class);
		startAnnouncs = Arrays.toArray(String.class);
		document = new DocumentAnnounce(new File(Config.SERVER_DIR + "/data/announces.xml"));
		document.setRuningAnnouncs(runingAnnouncs);
		document.setStartAnnouncs(startAnnouncs);
		document.parse();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.addPlayerSelectListener(this);
		log.info("loaded " + startAnnouncs.size() + " start announces and " + runingAnnouncs.size() + " running announces.");
	}
	
	/**
	 * Method getRuningAnnouncs.
	 * @return Array<AnnounceTask>
	 */
	public final Array<AnnounceTask> getRuningAnnouncs()
	{
		return runingAnnouncs;
	}
	
	/**
	 * Method getStartAnnouncs.
	 * @return Array<String>
	 */
	public final Array<String> getStartAnnouncs()
	{
		return startAnnouncs;
	}
	
	public final synchronized void save()
	{
		document.save();
	}
	
	/**
	 * Method showStartAnnounce.
	 * @param player Player
	 */
	public final void showStartAnnounce(Player player)
	{
		final Array<String> startAnnouncs = getStartAnnouncs();
		final String[] array = startAnnouncs.array();
		
		for (int i = 0, length = startAnnouncs.size(); i < length; i++)
		{
			player.sendPacket(CharSay.getInstance(Strings.EMPTY, array[i], SayType.NOTICE_CHAT, 0, 0), true);
		}
	}
	
	/**
	 * Method onSelect.
	 * @param player Player
	 * @see tera.gameserver.model.listeners.PlayerSelectListener#onSelect(Player)
	 */
	@Override
	public void onSelect(Player player)
	{
		showStartAnnounce(player);
	}
}