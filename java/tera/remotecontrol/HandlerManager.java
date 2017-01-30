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
package tera.remotecontrol;

import tera.remotecontrol.handlers.AddPlayerItemHandler;
import tera.remotecontrol.handlers.AnnounceApplyHandler;
import tera.remotecontrol.handlers.AnnounceLoadHandler;
import tera.remotecontrol.handlers.AuthHandler;
import tera.remotecontrol.handlers.CancelShutdownHandler;
import tera.remotecontrol.handlers.DynamicInfoHandler;
import tera.remotecontrol.handlers.GameInfoHandler;
import tera.remotecontrol.handlers.GetAccountHandler;
import tera.remotecontrol.handlers.LoadChatHandler;
import tera.remotecontrol.handlers.PlayerMessageHandler;
import tera.remotecontrol.handlers.RemovePlayerItemHandler;
import tera.remotecontrol.handlers.SavePlayersHandler;
import tera.remotecontrol.handlers.SendAnnounceHandler;
import tera.remotecontrol.handlers.ServerConsoleHandler;
import tera.remotecontrol.handlers.ServerRestartHandler;
import tera.remotecontrol.handlers.ServerStatusHandler;
import tera.remotecontrol.handlers.SetAccountHandler;
import tera.remotecontrol.handlers.StartGCHandler;
import tera.remotecontrol.handlers.StartRestartHandler;
import tera.remotecontrol.handlers.StartShutdownHandler;
import tera.remotecontrol.handlers.StaticInfoHandler;
import tera.remotecontrol.handlers.UpdateAccountHandler;
import tera.remotecontrol.handlers.UpdateEquipmentItemsHandler;
import tera.remotecontrol.handlers.UpdateInventoryItemsHandler;
import tera.remotecontrol.handlers.UpdatePlayerInfoHandler;
import tera.remotecontrol.handlers.UpdatePlayerItemHandler;
import tera.remotecontrol.handlers.UpdatePlayerMainInfoHandler;
import tera.remotecontrol.handlers.UpdatePlayerStatInfoHandler;
import tera.remotecontrol.handlers.UpdatePlayersHandler;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public abstract class HandlerManager
{
	private static final PacketHandler[] handlers =
	{
		new AuthHandler(),
		null,
		new ServerStatusHandler(),
		null,
		new AnnounceLoadHandler(),
		null,
		new AnnounceApplyHandler(),
		new SendAnnounceHandler(),
		new LoadChatHandler(),
		null,
		new PlayerMessageHandler(),
		new UpdatePlayersHandler(),
		null,
		new UpdatePlayerInfoHandler(),
		null,
		new StaticInfoHandler(),
		new DynamicInfoHandler(),
		new GameInfoHandler(),
		null,
		null,
		null,
		new ServerRestartHandler(),
		ServerConsoleHandler.instance,
		SavePlayersHandler.instance,
		StartGCHandler.instance,
		StartRestartHandler.instance,
		StartShutdownHandler.instance,
		CancelShutdownHandler.instance,
		LoadChatHandler.instance,
		UpdatePlayersHandler.instance,
		UpdatePlayerMainInfoHandler.instance,
		UpdatePlayerStatInfoHandler.instance,
		UpdateInventoryItemsHandler.instance,
		UpdateEquipmentItemsHandler.instance,
		null,
		UpdatePlayerItemHandler.instance,
		RemovePlayerItemHandler.instance,
		AddPlayerItemHandler.instance,
		GetAccountHandler.instance,
		SetAccountHandler.instance,
		UpdateAccountHandler.instance,
	};
	
	/**
	 * Method getHandler.
	 * @param type PacketType
	 * @return PacketHandler
	 */
	public static PacketHandler getHandler(PacketType type)
	{
		if (!ServerControl.authed && (type != PacketType.AUTH))
		{
			return null;
		}
		
		return handlers[type.ordinal()];
	}
}