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
package tera.gameserver.model.npc;

import tera.gameserver.model.EmotionType;
import tera.gameserver.tasks.EmotionTask;
import tera.gameserver.templates.NpcTemplate;

import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;

/**
 * @author Ronn
 */
public class FriendNpc extends Npc
{
	private static final IdGenerator ID_FACTORY = IdGenerators.newSimpleIdGenerator(1000001, 1300000);
	
	/**
	 * Constructor for FriendNpc.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public FriendNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method getAutoEmotions.
	 * @return EmotionType[]
	 */
	@Override
	protected EmotionType[] getAutoEmotions()
	{
		return EmotionTask.NPC_TYPES;
	}
	
	/**
	 * Method isFriendNpc.
	 * @return boolean
	 */
	@Override
	public boolean isFriendNpc()
	{
		return true;
	}
	
	/**
	 * Method isInvul.
	 * @return boolean
	 */
	@Override
	public boolean isInvul()
	{
		return true;
	}
	
	/**
	 * Method nextCastId.
	 * @return int
	 */
	@Override
	public int nextCastId()
	{
		return ID_FACTORY.getNextId();
	}
}