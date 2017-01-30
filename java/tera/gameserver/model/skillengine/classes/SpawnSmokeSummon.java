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
package tera.gameserver.model.skillengine.classes;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.spawn.SummonSpawn;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.templates.NpcTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.VarTable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class SpawnSmokeSummon extends AbstractSkill
{
	private volatile SummonSpawn spawn;
	
	public SpawnSmokeSummon(SkillTemplate template)
	{
		super(template);
	}
	
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		super.useSkill(character, targetX, targetY, targetZ);
		
		if (!character.isPlayer())
		{
			return;
		}
		
		final int consume = (character.getMaxHp() * 20) / 100;
		
		if (character.getCurrentHp() <= (consume + 2))
		{
			character.sendMessage("You do not have enough HP.");
			return;
		}
		
		final SummonSpawn spawn = getspawn();
		
		if (spawn == null)
		{
			character.sendMessage("This summon is not implemented.");
			return;
		}
		
		Summon summon = character.getSummon();
		
		if (summon != null)
		{
			summon.remove();
		}
		
		spawn.setOwner(character);
		spawn.getLocation().setXYZHC(character.getX(), character.getY(), character.getZ(), character.getHeading(), character.getContinentId());
		spawn.start();
		summon = character.getSummon();
		character.setCurrentHp(character.getCurrentHp() - consume);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyHpChanged(character);
		final Array<Npc> hateList = character.getLocalHateList();
		
		for (Npc npc : hateList.array())
		{
			if (npc == null)
			{
				break;
			}
			
			final long hate = Math.max(npc.getAggro(character), 1);
			npc.removeAggro(character);
			npc.addAggro(summon, hate, false);
		}
	}
	
	protected SummonSpawn getspawn()
	{
		if (spawn == null)
		{
			synchronized (this)
			{
				if (spawn == null)
				{
					final NpcTable npcTable = NpcTable.getInstance();
					final NpcTemplate temp = npcTable.getTemplate(template.getSummonId(), template.getSummonType());
					
					if (temp == null)
					{
						return null;
					}
					
					final ConfigAITable configTable = ConfigAITable.getInstance();
					final VarTable vars = template.getVars();
					final ConfigAI configAI = configTable.getConfig(vars.getString("configAI", null));
					
					if (configAI == null)
					{
						return null;
					}
					
					spawn = new SummonSpawn(temp, configAI, vars.getEnum("aiClass", NpcAIClass.class), template.getLifeTime());
				}
			}
		}
		
		return spawn;
	}
}