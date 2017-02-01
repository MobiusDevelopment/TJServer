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
package tera.gameserver.model.skillengine.shots;

import tera.gameserver.model.Character;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public interface Shot extends Foldable, Runnable
{
	/**
	 * Method getObjectId.
	 * @return int
	 */
	int getObjectId();
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	int getSubId();
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	Character getTarget();
	
	/**
	 * Method getTargetX.
	 * @return float
	 */
	float getTargetX();
	
	/**
	 * Method getTargetY.
	 * @return float
	 */
	float getTargetY();
	
	/**
	 * Method getTargetZ.
	 * @return float
	 */
	float getTargetZ();
	
	/**
	 * Method getType.
	 * @return ShotType
	 */
	ShotType getType();
	
	/**
	 * Method isAuto.
	 * @return boolean
	 */
	boolean isAuto();
	
	void start();
	
	void stop();
}
