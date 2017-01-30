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

import tera.gameserver.model.playable.Player;

/**
 */
@SuppressWarnings("unused")
public class RequestSkillAction extends ClientPacket
{
	public static enum ActionType
	{
		NONE,
		NONE1,
		CANCEL;
		
		/**
		 * Method valueOf.
		 * @param index int
		 * @return ActionType
		 */
		public static ActionType valueOf(int index)
		{
			final ActionType[] values = values();
			
			if (index >= values.length)
			{
				return NONE;
			}
			
			return values[index];
		}
	}
	
	private Player player;
	private ActionType type;
	private int skillId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		skillId = 0;
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public final Player getPlayer()
	{
		return player;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		skillId = readInt();
		type = ActionType.valueOf(readInt());
	}
	
	@Override
	public void runImpl()
	{
	}
}