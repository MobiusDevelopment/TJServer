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
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.ActionStart;

/**
 * @author Ronn
 */
public class RequestActionInvite extends ClientPacket
{
	
	private String name;
	
	private ActionType actionType;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
	}
	
	@Override
	protected void readImpl()
	{
		readInt();
		readShort();
		actionType = ActionType.valueOf(readByte());
		readLong();
		readInt();
		readShort();
		readByte();
		
		switch (actionType)
		{
			case CREATE_GUILD:
			{
				readShort();
				name = readString();
				break;
			}
			
			case BIND_ITEM:
			{
				readShort();
				name = String.valueOf(readInt());
				break;
			}
			
			default:
				name = readString();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final UserClient client = getOwner();
		
		if (client == null)
		{
			return;
		}
		
		final Player actor = client.getOwner();
		
		if ((actor == null) || actor.getName().equals(name))
		{
			return;
		}
		
		final ActionType actionType = getActionType();
		actor.sendPacket(ActionStart.getInstance(actionType), true);
		
		if (!actionType.isImplemented() || actor.hasLastAction())
		{
			return;
		}
		
		actor.getAI().startAction(actionType.newInstance(actor, name));
	}
	
	/**
	 * Method getActionType.
	 * @return ActionType
	 */
	private ActionType getActionType()
	{
		return actionType;
	}
}
