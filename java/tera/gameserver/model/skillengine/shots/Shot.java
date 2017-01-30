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
	public int getObjectId();
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	public int getSubId();
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	public Character getTarget();
	
	/**
	 * Method getTargetX.
	 * @return float
	 */
	public float getTargetX();
	
	/**
	 * Method getTargetY.
	 * @return float
	 */
	public float getTargetY();
	
	/**
	 * Method getTargetZ.
	 * @return float
	 */
	public float getTargetZ();
	
	/**
	 * Method getType.
	 * @return ShotType
	 */
	public ShotType getType();
	
	/**
	 * Method isAuto.
	 * @return boolean
	 */
	public boolean isAuto();
	
	public void start();
	
	public void stop();
}
