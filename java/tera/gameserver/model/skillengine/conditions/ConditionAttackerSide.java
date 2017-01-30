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
package tera.gameserver.model.skillengine.conditions;

import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;

/**
 * @author Ronn
 * @created 29.03.2012
 */
public class ConditionAttackerSide extends AbstractCondition
{
	/**
	 */
	private static enum Side
	{
		FRONT,
		BEHIND,
		SIDE;
		/**
		 * Method isInSide.
		 * @param attacker Character
		 * @param attacked Character
		 * @return boolean
		 */
		protected boolean isInSide(Character attacker, Character attacked)
		{
			if ((attacker == null) || (attacked == null))
			{
				return false;
			}
			
			switch (this)
			{
				case SIDE:
					return attacker.isInSide(attacked);
				
				case FRONT:
					return attacker.isInFront(attacked);
				
				case BEHIND:
					return attacker.isInBehind(attacked);
			}
			
			return false;
		}
	}
	
	private final Side side;
	
	/**
	 * Constructor for ConditionAttackerSide.
	 * @param side String
	 */
	public ConditionAttackerSide(String side)
	{
		this.side = Side.valueOf(side);
	}
	
	/**
	 * Method test.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param val float
	 * @return boolean
	 * @see tera.gameserver.model.skillengine.Condition#test(Character, Character, Skill, float)
	 */
	@Override
	public boolean test(Character attacker, Character attacked, Skill skill, float val)
	{
		return side.isInSide(attacker, attacked);
	}
}