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
package tera.gameserver.model.npc.spawn;

import org.w3c.dom.Node;

import tera.gameserver.manager.BossSpawnManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class BossSpawn extends NpcSpawn
{
	private long respawnDate;
	
	/**
	 * Constructor for BossSpawn.
	 * @param node Node
	 * @param vars VarTable
	 * @param template NpcTemplate
	 * @param location Location
	 * @param respawn int
	 * @param random int
	 * @param minRadius int
	 * @param maxRadius int
	 * @param config ConfigAI
	 * @param aiClass NpcAIClass
	 */
	public BossSpawn(Node node, VarTable vars, NpcTemplate template, Location location, int respawn, int random, int minRadius, int maxRadius, ConfigAI config, NpcAIClass aiClass)
	{
		super(node, vars, template, location, respawn, random, minRadius, maxRadius, config, aiClass);
		final BossSpawnManager spawnManager = BossSpawnManager.getInstance();
		
		if (!spawnManager.addSpawn(this))
		{
			throw new IllegalArgumentException("found duplicate boss spawn");
		}
		
		setRespawnDate(spawnManager.getSpawn(getTemplate()));
	}
	
	/**
	 * Method doDie.
	 * @param npc Npc
	 * @see tera.gameserver.model.npc.spawn.Spawn#doDie(Npc)
	 */
	@Override
	public synchronized void doDie(Npc npc)
	{
		int time = 0;
		final int randomTime = getRandomTime();
		final int respawnTime = getRespawnTime();
		
		if (randomTime == 0)
		{
			time = respawnTime * 1000;
		}
		else
		{
			time = getRandom().nextInt(Math.max(0, respawnTime - randomTime), respawnTime + randomTime) * 1000;
		}
		
		setRespawnDate(System.currentTimeMillis() + time);
		final BossSpawnManager spawnManager = BossSpawnManager.getInstance();
		spawnManager.updateSpawn(getTemplate(), getRespawnTime());
		super.doDie(npc);
	}
	
	@Override
	public synchronized void doRespawn()
	{
		if (isStoped())
		{
			return;
		}
		
		if (schedule != null)
		{
			return;
		}
		
		final long current = System.currentTimeMillis();
		final long respawnDate = getRespawnDate();
		
		if ((respawnDate == -1) || (respawnDate < current))
		{
			doSpawn();
			return;
		}
		
		final ExecutorManager executor = ExecutorManager.getInstance();
		schedule = executor.scheduleGeneral(this, respawnDate - current);
	}
	
	/**
	 * Method getRespawnDate.
	 * @return long
	 */
	public long getRespawnDate()
	{
		return respawnDate;
	}
	
	/**
	 * Method setRespawnDate.
	 * @param respawnDate long
	 */
	public void setRespawnDate(long respawnDate)
	{
		this.respawnDate = respawnDate;
	}
	
	/**
	 * Method start.
	 * @see tera.gameserver.model.npc.spawn.Spawn#start()
	 */
	@Override
	public void start()
	{
		setStoped(false);
		doRespawn();
	}
}