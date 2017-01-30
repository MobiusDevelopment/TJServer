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
package tera.gameserver.model.ai.npc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import tera.gameserver.model.ai.npc.classes.BattleGuardAI;
import tera.gameserver.model.ai.npc.classes.DefaultNpcAI;
import tera.gameserver.model.ai.npc.classes.DefaultSummonAI;
import tera.gameserver.model.ai.npc.classes.EpicBattleAI;
import tera.gameserver.model.ai.npc.classes.EventMonsterAI;
import tera.gameserver.model.ai.npc.classes.RegionWarDefenseAI;
import tera.gameserver.model.npc.BattleGuard;
import tera.gameserver.model.npc.EventMonster;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.RegionWarDefense;
import tera.gameserver.model.npc.playable.EventEpicBattleNpc;
import tera.gameserver.model.npc.summons.Summon;

/**
 * @author Ronn
 */
public enum NpcAIClass
{
	EVENT_MOSTER(EventMonsterAI.class, EventMonster.class),
	BATTLE_GUARD(BattleGuardAI.class, BattleGuard.class),
	REGION_WAR_DEFENSE(RegionWarDefenseAI.class, RegionWarDefense.class),
	EPIC_BATTLE_EVENT(EpicBattleAI.class, EventEpicBattleNpc.class),
	DEFAULT_SUMMON(DefaultSummonAI.class, Summon.class),
	DEFAULT(DefaultNpcAI.class, Npc.class);
	private Constructor<? extends NpcAI<? extends Npc>> constructor;
	
	/**
	 * Constructor for NpcAIClass.
	 * @param type Class<? extends NpcAI<?>>
	 * @param actorType Class<? extends Npc>
	 */
	private NpcAIClass(Class<? extends NpcAI<?>> type, Class<? extends Npc> actorType)
	{
		try
		{
			constructor = type.getConstructor(actorType, ConfigAI.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method newInstance.
	 * @param <T>
	 * @param npc T
	 * @param config ConfigAI
	 * @return NpcAI<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Npc> NpcAI<T> newInstance(T npc, ConfigAI config)
	{
		try
		{
			return (NpcAI<T>) constructor.newInstance(npc, config);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}