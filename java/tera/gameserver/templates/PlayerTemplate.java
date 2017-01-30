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
package tera.gameserver.templates;

import java.lang.reflect.Field;

import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.tables.ItemTable;

import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class PlayerTemplate extends CharTemplate
{
	
	protected PlayerClass playerClass;
	
	protected Race race;
	
	protected Sex sex;
	
	protected SkillTemplate[][] skills;
	
	protected int id;
	
	protected int owerturnId;
	
	protected int[][] items;
	
	/**
	 * Constructor for PlayerTemplate.
	 * @param vars VarTable
	 * @param funcs Func[]
	 * @param classType PlayerClass
	 * @param race Race
	 * @param sex Sex
	 * @param id int
	 * @param items int[][]
	 * @param skills SkillTemplate[][]
	 */
	public PlayerTemplate(VarTable vars, Func[] funcs, PlayerClass classType, Race race, Sex sex, int id, int[][] items, SkillTemplate[][] skills)
	{
		super(vars, funcs);
		playerClass = classType;
		this.race = race;
		this.id = id;
		this.items = items;
		this.skills = skills;
		this.sex = sex;
		owerturnId = (id + 1000000) | 0x08000000;
	}
	
	public void applyRace()
	{
		final Race race = getRace();
		final Field[] fields = race.getClass().getDeclaredFields();
		final Class<CharTemplate> cs = CharTemplate.class;
		
		try
		{
			for (Field field : fields)
			{
				if (field.getType() != float.class)
				{
					continue;
				}
				
				final String name = field.getName();
				
				if ("regMp".equals(name))
				{
					continue;
				}
				
				final boolean old = field.isAccessible();
				field.setAccessible(true);
				final Field target = cs.getDeclaredField(name);
				final boolean targetOld = target.isAccessible();
				target.setAccessible(true);
				target.setInt(this, (int) (target.getInt(this) * field.getFloat(race)));
				target.setAccessible(targetOld);
				field.setAccessible(old);
			}
		}
		catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException e)
		{
			log.warning(this, e);
		}
		
		if (regMp < 0)
		{
			regMp *= (2F - race.getRegMp());
		}
		else
		{
			regMp *= race.getRegMp();
		}
		
		if (race.getFuncs() == null)
		{
			return;
		}
		
		final Func[] funcs = getFuncs();
		final Array<Func> array = Arrays.toArray(Func.class);
		array.addAll(funcs);
		array.addAll(race.getFuncs());
		array.trimToSize();
		this.funcs = array.array();
	}
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public final int getClassId()
	{
		return playerClass.getId();
	}
	
	/**
	 * Method getPlayerClass.
	 * @return PlayerClass
	 */
	public final PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Method getItems.
	 * @return int[][]
	 */
	public final int[][] getItems()
	{
		return items;
	}
	
	/**
	 * Method getModelId.
	 * @return int
	 */
	@Override
	public int getModelId()
	{
		return id;
	}
	
	/**
	 * Method getOwerturnId.
	 * @return int
	 */
	public final int getOwerturnId()
	{
		return owerturnId;
	}
	
	/**
	 * Method getRace.
	 * @return Race
	 */
	public final Race getRace()
	{
		return race;
	}
	
	/**
	 * Method getRaceId.
	 * @return int
	 */
	public final int getRaceId()
	{
		return race.getId();
	}
	
	/**
	 * Method getSex.
	 * @return Sex
	 */
	public final Sex getSex()
	{
		return sex;
	}
	
	/**
	 * Method getSkills.
	 * @return SkillTemplate[][]
	 */
	public final SkillTemplate[][] getSkills()
	{
		return skills;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		return id;
	}
	
	/**
	 * Method giveItems.
	 * @param inventory Inventory
	 */
	public void giveItems(Inventory inventory)
	{
		if (inventory == null)
		{
			return;
		}
		
		final ItemTable itemTable = ItemTable.getInstance();
		
		for (int[] item : items)
		{
			final ItemTemplate template = itemTable.getItem(item[0]);
			
			if (template == null)
			{
				continue;
			}
			
			if (template.isStackable())
			{
				inventory.addItem(item[0], item[1], "CreatePlayer");
			}
			else
			{
				for (int j = 0, size = item[1]; j < size; j++)
				{
					inventory.addItem(item[0], 1, "CreatePlayer");
				}
			}
		}
	}
	
	/**
	 * Method giveSkills.
	 * @param player Player
	 */
	public void giveSkills(Player player)
	{
		for (SkillTemplate[] skill : skills)
		{
			player.addSkills(skill, false);
		}
	}
}
