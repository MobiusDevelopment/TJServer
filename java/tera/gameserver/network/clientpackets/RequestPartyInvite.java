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
package tera.gameserver.network.clientpackets;

import tera.gameserver.model.actions.ActionType;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.ActionStart;

/**
 * @author Ronn
 */
public class RequestPartyInvite extends ClientPacket
{
	private int actionId;
	private String name;
	private Player player;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		name = null;
	}
	
	@Override
	protected void readImpl()
	{
		player = owner.getOwner();
		readInt();
		readShort();
		actionId = readByte();
		readLong();
		readInt();
		readShort();
		readByte();
		name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		if ((player.getParty() != null) && !player.getParty().isLeader(player))
		{
			return;
		}
		
		final ActionType type = ActionType.valueOf(actionId);
		player.sendPacket(ActionStart.getInstance(type), true);
		
		if (!type.isImplemented() || player.hasLastAction())
		{
			return;
		}
		
		player.getAI().startAction(type.newInstance(player, name));
	}
}