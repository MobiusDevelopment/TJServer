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
package tera.gameserver.model.skillengine.funcs.chance;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.funcs.Func;

/**
 * @author Ronn
 */
public interface ChanceFunc extends Func
{
	/**
	 * Method apply.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @return boolean
	 */
	boolean apply(Character attacker, Character attacked, Skill skill);
	
	/**
	 * Method getChance.
	 * @return int
	 */
	int getChance();
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	Skill getSkill();
	
	/**
	 * Method isOnAttack.
	 * @return boolean
	 */
	boolean isOnAttack();
	
	/**
	 * Method isOnAttacked.
	 * @return boolean
	 */
	boolean isOnAttacked();
	
	/**
	 * Method isOnCritAttack.
	 * @return boolean
	 */
	boolean isOnCritAttack();
	
	/**
	 * Method isOnCritAttacked.
	 * @return boolean
	 */
	boolean isOnCritAttacked();
	
	/**
	 * Method isOnOwerturn.
	 * @return boolean
	 */
	boolean isOnOwerturn();
	
	/**
	 * Method isOnOwerturned.
	 * @return boolean
	 */
	boolean isOnOwerturned();
	
	/**
	 * Method isOnShieldBlocked.
	 * @return boolean
	 */
	boolean isOnShieldBlocked();
	
	void prepare();
}