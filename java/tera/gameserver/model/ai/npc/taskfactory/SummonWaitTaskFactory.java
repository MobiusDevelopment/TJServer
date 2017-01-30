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

import tera.gameserver.manager.GeoManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.MessagePackage;
import tera.gameserver.model.ai.npc.NpcAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.gameserver.tables.MessagePackageTable;
import tera.util.LocalObjects;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.Strings;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class SummonWaitTaskFactory extends AbstractTaskFactory
{
	protected final MessagePackage walkMessage;
	protected final MessagePackage followMessage;
	protected final int randomWalkMinRange;
	protected final int randomWalkMaxRange;
	protected final int randomWalkMinDelay;
	protected final int randomWalkMaxDelay;
	protected final int messageInterval;
	
	/**
	 * Constructor for SummonWaitTaskFactory.
	 * @param node Node
	 */
	public SummonWaitTaskFactory(Node node)
	{
		super(node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node, "set", "name", "val");
			messageInterval = vars.getInteger("messageInterval", 120000);
			randomWalkMinRange = vars.getInteger("randomWalkMinRange", ConfigAI.DEFAULT_RANDOM_MIN_WALK_RANGE);
			randomWalkMaxRange = vars.getInteger("randomWalkMaxRange", ConfigAI.DEFAULT_RANDOM_MAX_WALK_RANGE);
			randomWalkMinDelay = vars.getInteger("randomWalkMinDelay", ConfigAI.DEFAULT_RANDOM_MIN_WALK_DELAY);
			randomWalkMaxDelay = vars.getInteger("randomWalkMaxDelay", ConfigAI.DEFAULT_RANDOM_MAX_WALK_DELAY);
			final MessagePackageTable messageTable = MessagePackageTable.getInstance();
			followMessage = messageTable.getPackage(vars.getString("followMessage", Strings.EMPTY));
			walkMessage = messageTable.getPackage(vars.getString("walkMessage", Strings.EMPTY));
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
		
		if (actor.isBattleStanced())
		{
			actor.stopBattleStance();
		}
		
		final int randomWalkMaxRange = getRandomWalkMaxRange();
		
		if (!owner.isInRange(actor, randomWalkMaxRange))
		{
			final int minDist = (int) (actor.getGeomRadius() + owner.getGeomRadius()) + 20;
			final float radians = Angles.headingToRadians(Rnd.nextInt(65000));
			final float newX = Coords.calcX(owner.getX(), minDist, radians);
			final float newY = Coords.calcY(owner.getY(), minDist, radians);
			final GeoManager geoManager = GeoManager.getInstance();
			final float newZ = geoManager.getHeight(actor.getContinentId(), newX, newY, actor.getZ());
			String message = Strings.EMPTY;
			final MessagePackage followMessage = getFollowMessage();
			
			if ((followMessage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
			{
				message = followMessage.getRandomMessage();
				ai.setLastMessage(currentTime + getMessageInterval());
			}
			
			ai.addMoveTask(newX, newY, newZ, true, message);
			return;
		}
		
		if ((randomWalkMaxRange > 0) && (currentTime > ai.getNextRandomWalk()))
		{
			if ((currentTime - ai.getLastNotifyIcon()) > 5000)
			{
				PacketManager.showNotifyIcon(actor, NotifyType.NOTICE_THINK);
				ai.setLastNotifyIcon(currentTime);
			}
			
			ai.setNextRandomWalk(currentTime + Rnd.nextInt(getRandomWalkMinDelay(), getRandomWalkMaxDelay()));
			final int distance = Rnd.nextInt(getRandomWalkMinRange(), getRandomWalkMaxRange());
			final int newHeading = Rnd.nextInt(65000);
			final float newX = Coords.calcX(owner.getX(), distance, newHeading);
			final float newY = Coords.calcY(owner.getY(), distance, newHeading);
			final GeoManager geoManager = GeoManager.getInstance();
			final float newZ = geoManager.getHeight(actor.getContinentId(), newX, newY, owner.getZ());
			String message = Strings.EMPTY;
			final MessagePackage walkMessage = getWalkMessage();
			
			if ((walkMessage != null) && ((currentTime - ai.getLastMessage()) > getMessageInterval()))
			{
				message = walkMessage.getRandomMessage();
				ai.setLastMessage(currentTime + getMessageInterval());
			}
			
			ai.addMoveTask(newX, newY, newZ, true, message);
		}
	}
	
	/**
	 * Method getRandomWalkMaxDelay.
	 * @return int
	 */
	protected final int getRandomWalkMaxDelay()
	{
		return randomWalkMaxDelay;
	}
	
	/**
	 * Method getRandomWalkMaxRange.
	 * @return int
	 */
	protected final int getRandomWalkMaxRange()
	{
		return randomWalkMaxRange;
	}
	
	/**
	 * Method getRandomWalkMinDelay.
	 * @return int
	 */
	protected final int getRandomWalkMinDelay()
	{
		return randomWalkMinDelay;
	}
	
	/**
	 * Method getRandomWalkMinRange.
	 * @return int
	 */
	protected final int getRandomWalkMinRange()
	{
		return randomWalkMinRange;
	}
	
	/**
	 * Method getFollowMessage.
	 * @return MessagePackage
	 */
	public MessagePackage getFollowMessage()
	{
		return followMessage;
	}
	
	/**
	 * Method getMessageInterval.
	 * @return int
	 */
	public int getMessageInterval()
	{
		return messageInterval;
	}
	
	/**
	 * Method getWalkMessage.
	 * @return MessagePackage
	 */
	public MessagePackage getWalkMessage()
	{
		return walkMessage;
	}
}