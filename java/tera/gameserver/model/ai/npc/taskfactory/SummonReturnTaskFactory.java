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
package tera.gameserver.model.ai.npc.taskfactory;

import org.w3c.dom.Node;

import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.MessagePackage;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.util.Strings;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class SummonReturnTaskFactory extends AbstractTaskFactory
{
	private final MessagePackage returnMessage;
	private final int messageInterval;
	
	/**
	 * Constructor for SummonReturnTaskFactory.
	 * @param node Node
	 */
	public SummonReturnTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			messageInterval = vars.getInteger("messageInterval", 30000);
			final MessagePackageTable messageTable = MessagePackageTable.getInstance();
			returnMessage = messageTable.getPackage(vars.getString("returnMessage", Strings.EMPTY));
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method addNewTask.
	 * @param ai NpcAI<A>
	 * @param actor A
	 * @param local LocalObjects
	 * @param config ConfigAI
	 * @param currentTime long
	 */
	@Override
	public <A extends Npc> void addNewTask(NpcAI<A> ai, A actor, LocalObjects local, ConfigAI config, long currentTime)
	{
		final Character owner = actor.getOwner();
		
		if (owner == null)
		{
			return;
		}
		
		final MessagePackage messages = getReturnMessage();
		
		if (actor.getRunSpeed() > 10)
		{
			String message = Strings.EMPTY;
			
			if ((messages != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
			{
				message = messages.getRandomMessage();
				ai.setLastMessage(currentTime + getMessageInterval());
			}
			
			ai.addMoveTask(owner.getX(), owner.getY(), owner.getZ(), true, message);
		}
	}
	
	/**
	 * Method getReturnMessage.
	 * @return MessagePackage
	 */
	public MessagePackage getReturnMessage()
	{
		return returnMessage;
	}
	
	/**
	 * Method getMessageInterval.
	 * @return int
	 */
	public int getMessageInterval()
	{
		return messageInterval;
	}
}