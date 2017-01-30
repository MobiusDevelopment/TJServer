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
package tera.gameserver.model.skillengine;

import org.w3c.dom.Node;

import tera.gameserver.model.Character;

/**
 * @author Ronn
 */
public interface Condition
{
	public static final Condition[] EMPTY_CONDITIONS = new Condition[0];
	
	/**
	 * Method getMsg.
	 * @return String
	 */
	public String getMsg();
	
	/**
	 * Method setMsg.
	 * @param msg Node
	 * @return Condition
	 */
	public Condition setMsg(Node msg);
	
	/**
	 * Method test.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param val float
	 * @return boolean
	 */
	public boolean test(Character attacker, Character attacked, Skill skill, float val);
}
