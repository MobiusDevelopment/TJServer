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
package tera.gameserver.model.geom;

/**
 * @author Ronn
 */
public interface Geom
{
	/**
	 * Method getHeight.
	 * @return float
	 */
	float getHeight();
	
	/**
	 * Method getRadius.
	 * @return float
	 */
	float getRadius();
	
	/**
	 * Method isHit.
	 * @param attackerX float
	 * @param attackerY float
	 * @param attackerZ float
	 * @param attackerHeight float
	 * @param attackerRadius float
	 * @return boolean
	 */
	boolean isHit(float attackerX, float attackerY, float attackerZ, float attackerHeight, float attackerRadius);
	
	/**
	 * Method isHit.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param endX float
	 * @param endY float
	 * @param endZ float
	 * @param attackerRadius float
	 * @param checkHeight boolean
	 * @return boolean
	 */
	boolean isHit(float startX, float startY, float startZ, float endX, float endY, float endZ, float attackerRadius, boolean checkHeight);
}
