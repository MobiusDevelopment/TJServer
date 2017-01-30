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

import tera.gameserver.model.Character;
import tera.gameserver.network.serverpackets.SkillLockAttack;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class LockOnStrike extends Strike
{
	protected final Array<Character> targets;
	
	/**
	 * Constructor for LockOnStrike.
	 * @param template SkillTemplate
	 */
	public LockOnStrike(SkillTemplate template)
	{
		super(template);
		targets = Arrays.toConcurrentArray(Character.class);
	}
	
	/**
	 * Method endSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param force boolean
	 * @see tera.gameserver.model.skillengine.Skill#endSkill(Character, float, float, float, boolean)
	 */
	@Override
	public void endSkill(Character attacker, float targetX, float targetY, float targetZ, boolean force)
	{
		super.endSkill(attacker, targetX, targetY, targetZ, force);
		targets.clear();
	}
	
	/**
	 * Method startSkill.
	 * @param attacker Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#startSkill(Character, float, float, float)
	 */
	@Override
	public void startSkill(Character attacker, float targetX, float targetY, float targetZ)
	{
		super.startSkill(attacker, targetX, targetY, targetZ);
		addTargets(targets, attacker, targetX, targetY, targetZ);
	}
	
	/**
	 * Method useSkill.
	 * @param character Character
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.skillengine.Skill#useSkill(Character, float, float, float)
	 */
	@Override
	public void useSkill(Character character, float targetX, float targetY, float targetZ)
	{
		if (applyOrder == 0)
		{
			character.broadcastPacket(SkillLockAttack.getInstance(character, targets, this, castId));
		}
		else if (applyOrder == 1)
		{
			targets.writeLock();
			
			try
			{
				final Character[] array = targets.array();
				
				for (int i = 0, length = targets.size(); i < length; i++)
				{
					final Character target = array[i];
					
					if ((target == null) || target.isDead() || target.isInvul() || target.isEvasioned())
					{
						continue;
					}
					
					applySkill(character, target);
				}
			}
			
			finally
			{
				targets.writeUnlock();
			}
		}
		
		applyOrder++;
	}
}