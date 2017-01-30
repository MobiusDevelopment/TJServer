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
import tera.gameserver.model.TownInfo;
import tera.gameserver.tables.TownTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.Strings;
import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class TeleportTown extends Buff
{
	private final TownInfo town;
	
	/**
	 * Constructor for TeleportTown.
	 * @param template SkillTemplate
	 */
	public TeleportTown(SkillTemplate template)
	{
		super(template);
		final VarTable vars = template.getVars();
		final TownTable townTable = TownTable.getInstance();
		town = townTable.getTown(vars.getString("town", Strings.EMPTY));
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
		final AttackInfo info = super.applySkill(attacker, target);
		System.out.println("tele to town " + town);
		
		if (town != null)
		{
			target.stopMove();
			target.teleToLocation(town.getCenter());
		}
		
		return info;
	}
}