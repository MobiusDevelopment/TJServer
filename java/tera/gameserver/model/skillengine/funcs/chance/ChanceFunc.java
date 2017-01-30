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
	public boolean apply(Character attacker, Character attacked, Skill skill);
	
	/**
	 * Method getChance.
	 * @return int
	 */
	public int getChance();
	
	/**
	 * Method getSkill.
	 * @return Skill
	 */
	public Skill getSkill();
	
	/**
	 * Method isOnAttack.
	 * @return boolean
	 */
	public boolean isOnAttack();
	
	/**
	 * Method isOnAttacked.
	 * @return boolean
	 */
	public boolean isOnAttacked();
	
	/**
	 * Method isOnCritAttack.
	 * @return boolean
	 */
	public boolean isOnCritAttack();
	
	/**
	 * Method isOnCritAttacked.
	 * @return boolean
	 */
	public boolean isOnCritAttacked();
	
	/**
	 * Method isOnOwerturn.
	 * @return boolean
	 */
	public boolean isOnOwerturn();
	
	/**
	 * Method isOnOwerturned.
	 * @return boolean
	 */
	public boolean isOnOwerturned();
	
	/**
	 * Method isOnShieldBlocked.
	 * @return boolean
	 */
	public boolean isOnShieldBlocked();
	
	public void prepare();
}