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
 * @created 26.04.2012
 */
public class RequestGuildInvite extends ClientPacket
{
	
	private String name;
	
	private Player actor;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		actor = null;
		name = null;
	}
	
	@Override
	protected void readImpl()
	{
		actor = owner.getOwner();
		readInt();
		readLong();
		readLong();
		readShort();
		readByte();
		name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final ActionType actionType = ActionType.INVITE_GUILD;
		actor.sendPacket(ActionStart.getInstance(actionType), true);
		actor.getAI().startAction(actionType.newInstance(actor, name));
	}
}
