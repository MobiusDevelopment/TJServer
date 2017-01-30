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
package tera.gameserver.model.ai;

import tera.gameserver.model.Character;

/**
 * @author Ronn
 */
public interface CharacterAI extends AI
{
	/**
	 * Method isGlobalAI.
	 * @return boolean
	 */
	public boolean isGlobalAI();
	
	public void startAITask();
	
	public void stopAITask();
	
	public void clearTaskList();
	
	public void abortAttack();
	
	/**
	 * Method startAttack.
	 * @param target Character
	 */
	public void startAttack(Character target);
}
