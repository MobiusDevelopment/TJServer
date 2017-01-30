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
package tera.gameserver.model.worldobject;

import tera.Config;
import tera.gameserver.model.TObject;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DeleteWorldObject;
import tera.gameserver.network.serverpackets.WorldObjectInfo;

/**
 * @author Ronn
 */
public abstract class WorldObject extends TObject
{
	/**
	 * Constructor for WorldObject.
	 * @param objectId int
	 */
	public WorldObject(int objectId)
	{
		super(objectId);
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(WorldObjectInfo.getInstance(this), true);
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	@Override
	public int getSubId()
	{
		return Config.SERVER_OBJECT_SUB_ID;
	}
	
	/**
	 * Method isWorldObject.
	 * @return boolean
	 */
	@Override
	public final boolean isWorldObject()
	{
		return true;
	}
	
	/**
	 * Method removeMe.
	 * @param player Player
	 * @param type int
	 */
	@Override
	public void removeMe(Player player, int type)
	{
		player.sendPacket(DeleteWorldObject.getInstance(this), true);
	}
}
