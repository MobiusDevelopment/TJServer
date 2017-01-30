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
package tera.gameserver.manager;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentSkillLearn;
import tera.gameserver.model.SkillLearn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.Search;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 */
public final class SkillLearnManager
{
	
	private static final Logger log = Loggers.getLogger(SkillLearnManager.class);
	
	private static final Search<SkillLearn> SEARCH_REPLACED_SKILL = (required, target) -> required.getId() == target.getReplaceId();
	
	private static final Search<SkillLearn> SEARCH_REPLACEABLE_SKILL = (required, target) -> required.getReplaceId() == target.getId();
	
	private static SkillLearnManager instance;
	
	/**
	 * Method getInstance.
	 * @return SkillLearnManager
	 */
	public static SkillLearnManager getInstance()
	{
		if (instance == null)
		{
			instance = new SkillLearnManager();
		}
		
		return instance;
	}
	
	/**
	 * Method isAvailable.
	 * @param learns Array<SkillLearn>
	 * @param learn SkillLearn
	 * @param skills Table<Integer,Skill>
	 * @return boolean
	 */
	public static boolean isAvailable(Array<SkillLearn> learns, SkillLearn learn, Table<Integer, Skill> skills)
	{
		for (int i = 0, length = learns.size(); i < length; i++)
		{
			final SkillLearn replaceable = learns.search(learn, SEARCH_REPLACEABLE_SKILL);
			
			if (replaceable == null)
			{
				return false;
			}
			
			if (skills.containsKey(replaceable.getUseId()))
			{
				return true;
			}
			
			learn = replaceable;
		}
		
		return false;
	}
	
	/**
	 * Method isReplaced.
	 * @param learns Array<SkillLearn>
	 * @param learn SkillLearn
	 * @param skills Table<IntKey,Skill>
	 * @return boolean
	 */
	private static boolean isReplaced(Array<SkillLearn> learns, SkillLearn learn, Table<IntKey, Skill> skills)
	{
		for (int i = 0, length = learns.size(); i < length; i++)
		{
			final SkillLearn replaced = learns.search(learn, SEARCH_REPLACED_SKILL);
			
			if (replaced == null)
			{
				return false;
			}
			
			if (skills.containsKey(replaced.getUseId()))
			{
				return true;
			}
			
			learn = replaced;
		}
		
		return false;
	}
	
	private final Array<SkillLearn>[] learns;
	
	@SuppressWarnings("unchecked")
	private SkillLearnManager()
	{
		learns = new Array[8];
		
		for (int i = 0; i < 8; i++)
		{
			learns[i] = Arrays.toArray(SkillLearn.class);
		}
		
		final Array<SkillLearn> result = new DocumentSkillLearn(new File(Config.SERVER_DIR + "/data/skill_learns.xml")).parse();
		final SkillTable skillTable = SkillTable.getInstance();
		
		for (SkillLearn learn : result)
		{
			final SkillTemplate[] skill = skillTable.getSkills(learn.getClassId(), learn.getUseId());
			
			if ((skill == null) || (skill.length < 1) || (Config.WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS && !skill[0].isImplemented()))
			{
				continue;
			}
			
			learns[learn.getClassId()].add(learn);
		}
		
		int counter = 0;
		
		for (Array<SkillLearn> list : learns)
		{
			if (list == null)
			{
				continue;
			}
			
			list.trimToSize();
			counter += list.size();
		}
		
		log.info("loaded " + counter + " skill learn's for " + learns.length + " clases.");
	}
	
	/**
	 * Method addAvailableSkills.
	 * @param result Array<SkillLearn>
	 * @param player Player
	 */
	public final void addAvailableSkills(Array<SkillLearn> result, Player player)
	{
		final Array<SkillLearn> learnList = learns[player.getClassId()];
		
		if (learnList == null)
		{
			log.warning("classId " + player.getClassId() + "  not has been found available skills.");
			return;
		}
		
		final Table<IntKey, Skill> currentSkills = player.getSkills();
		final SkillLearn[] array = learnList.array();
		
		for (int i = 0, length = learnList.size(); i < length; i++)
		{
			final SkillLearn temp = array[i];
			
			if (currentSkills.containsKey(temp.getUseId()) || isReplaced(learnList, temp, currentSkills))
			{
				continue;
			}
			
			result.add(temp);
		}
	}
	
	/**
	 * Method isLearned.
	 * @param skillId int
	 * @param player Player
	 * @return boolean
	 */
	public boolean isLearned(int skillId, Player player)
	{
		final Array<SkillLearn> learnList = learns[player.getClassId()];
		
		if (learnList == null)
		{
			return false;
		}
		
		final SkillLearn[] array = learnList.array();
		final Table<IntKey, Skill> currentSkills = player.getSkills();
		
		for (int i = 0, length = learnList.size(); i < length; i++)
		{
			final SkillLearn learn = array[i];
			
			if ((learn.getId() == skillId) && (currentSkills.containsKey(learn.getUseId()) || isReplaced(learnList, learn, currentSkills)))
			{
				return true;
			}
		}
		
		return false;
	}
}
