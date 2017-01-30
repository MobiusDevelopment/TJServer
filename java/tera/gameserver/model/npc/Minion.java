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

import tera.gameserver.model.Character;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;

/**
 * @author Ronn
 */
public class Minion extends Monster
{
	private static final IdGenerator ID_FACTORY = IdGenerators.newSimpleIdGenerator(600001, 800000);
	private MinionLeader leader;
	
	/**
	 * Constructor for Minion.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public Minion(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method doDie.
	 * @param attacker Character
	 */
	@Override
	public void doDie(Character attacker)
	{
		final MinionLeader leader = getMinionLeader();
		
		if (leader != null)
		{
			leader.onDie(this);
		}
		
		super.doDie(attacker);
	}
	
	/**
	 * Method isMinion.
	 * @return boolean
	 */
	@Override
	public boolean isMinion()
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
	
	/**
	 * Method setLeader.
	 * @param leader MinionLeader
	 */
	public final void setLeader(MinionLeader leader)
	{
		this.leader = leader;
	}
	
	/**
	 * Method spawnMe.
	 * @param spawnLoc Location
	 * @param leader MinionLeader
	 */
	public void spawnMe(Location spawnLoc, MinionLeader leader)
	{
		setLeader(leader);
		spawnMe(spawnLoc);
		getSpawnLoc().set(leader.getSpawnLoc());
	}
	
	/**
	 * Method getKarmaMod.
	 * @return int
	 */
	@Override
	public int getKarmaMod()
	{
		return 0;
	}
	
	/**
	 * Method getMinionLeader.
	 * @return MinionLeader
	 */
	@Override
	public MinionLeader getMinionLeader()
	{
		return leader;
	}
}