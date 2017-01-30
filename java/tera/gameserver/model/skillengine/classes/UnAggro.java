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

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class UnAggro extends Strike
{
	/**
	 * Constructor for UnAggro.
	 * @param template SkillTemplate
	 */
	public UnAggro(SkillTemplate template)
	{
		super(template);
	}
	
	/**
	 * Method applySkill.
	 * @param attacker Character
	 * @param target Character
	 * @return AttackInfo
	 * @see tera.gameserver.model.skillengine.Skill#applySkill(Character, Character)
	 */
	@Override
	public AttackInfo applySkill(Character attacker, Character target)
	{
		final LocalObjects local = LocalObjects.get();
		final AttackInfo info = local.getNextAttackInfo();
		Array<Npc> hateList = local.getNextNpcList();
		hateList = attacker.getLocalHateList(hateList);
		final Npc[] array = hateList.array();
		
		for (int i = 0, length = hateList.size(); i < length; i++)
		{
			array[i].removeAggro(attacker);
		}
		
		addEffects(attacker, target);
		return info;
	}
}
