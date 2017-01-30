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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.playable.EventEpicBattleNpc;
import tera.gameserver.model.npc.playable.PlayerKiller;
import tera.gameserver.model.npc.spawn.BossSpawn;
import tera.gameserver.model.npc.spawn.NpcSpawn;
import tera.gameserver.model.npc.spawn.RegionWarSpawn;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.npc.summons.DefaultSummon;
import tera.gameserver.model.npc.summons.PlayerSummon;
import tera.gameserver.model.npc.summons.SmokeSummon;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.logging.Loggers;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public enum NpcType
{
	/** --------------------------- OTHER NPC ------------------------- */
	
	FRIENDLY(FriendNpc.class),
	GUARD(Guard.class),
	BATTLE_GUARD(BattleGuard.class),
	EVENT_MONSTER(EventMonster.class),
	EPIC_BATTLE_NPC(EventEpicBattleNpc.class),
	NPC_OBJECT(NpcObject.class),
	
	/** --------------------------- MONSTERS ------------------------- */
	
	MONSTER(Monster.class),
	SOCIAL_MONSTER(SocialMonster.class),
	ELITE(EliteMonster.class),
	RAID_BOSS(RaidBoss.class),
	MINION(Minion.class),
	MINION_LEADER(MinionLeader.class),
	
	/** --------------------------- REGION NPC ------------------------- */
	
	REGION_WAR_CONTROL(RegionWarControl.class),
	REGION_WAR_SHOP(RegionWarShop.class),
	REGION_WAR_DEFENSE(RegionWarDefense.class),
	REGION_WAR_BARRIER(RegionWarBarrier.class),
	
	/** --------------------------- PLAYABLE NPC ------------------------- */
	
	PLAYER_KILLER(PlayerKiller.class),
	
	/** --------------------------- SUMMONS ------------------------- */
	
	DEFAULT_SUMMON(DefaultSummon.class),
	PLAYER_SUMMON(PlayerSummon.class),
	SMOKE_SUMMON(SmokeSummon.class),;
	private Constructor<? extends Npc> constructor;
	
	/**
	 * Constructor for NpcType.
	 * @param type Class<? extends Npc>
	 */
	private NpcType(Class<? extends Npc> type)
	{
		try
		{
			constructor = type.getConstructor(int.class, NpcTemplate.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @param template NpcTemplate
	 * @return Npc
	 */
	public Npc newInstance(int objectId, NpcTemplate template)
	{
		try
		{
			return constructor.newInstance(objectId, template);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method newSpawn.
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
	 * @return Spawn
	 */
	public Spawn newSpawn(Node node, VarTable vars, NpcTemplate template, Location location, int respawn, int random, int minRadius, int maxRadius, ConfigAI config, NpcAIClass aiClass)
	{
		switch (this)
		{
			case RAID_BOSS:
				return new BossSpawn(node, vars, template, location, respawn, random, minRadius, maxRadius, config, aiClass);
			
			case REGION_WAR_SHOP:
			case REGION_WAR_DEFENSE:
			case REGION_WAR_BARRIER:
			case REGION_WAR_CONTROL:
				return new RegionWarSpawn(node, vars, template, location, respawn, random, minRadius, maxRadius, config, aiClass);
			
			default:
				return new NpcSpawn(node, vars, template, location, respawn, random, minRadius, maxRadius, config, aiClass);
		}
	}
}