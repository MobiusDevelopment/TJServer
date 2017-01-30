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

import tera.gameserver.model.playable.Player;

import rlib.geom.Geometry;

/**
 * @author Ronn
 */
public final class PlayerGeom extends AbstractGeom<Player>
{
	/**
	 * Constructor for PlayerGeom.
	 * @param character Player
	 * @param height float
	 * @param radius float
	 */
	public PlayerGeom(Player character, float height, float radius)
	{
		super(character, height, radius);
	}
	
	/**
	 * Method isHit.
	 * @param attackerX float
	 * @param attackerY float
	 * @param attackerZ float
	 * @param attackerHeight float
	 * @param attackerRadius float
	 * @return boolean
	 * @see tera.gameserver.model.geom.Geom#isHit(float, float, float, float, float)
	 */
	@Override
	public boolean isHit(float attackerX, float attackerY, float attackerZ, float attackerHeight, float attackerRadius)
	{
		final float diff = Math.abs(character.getZ() - attackerZ) - height;
		
		if (diff > attackerHeight)
		{
			return false;
		}
		
		return attackerRadius >= (character.getDistance(attackerX, attackerY, attackerZ) - radius);
	}
	
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
	 * @see tera.gameserver.model.geom.Geom#isHit(float, float, float, float, float, float, float, boolean)
	 */
	@Override
	public boolean isHit(float startX, float startY, float startZ, float endX, float endY, float endZ, float attackerRadius, boolean checkHeight)
	{
		final Player character = getCharacter();
		final float x = character.getX();
		final float y = character.getY();
		final float z = character.getZ();
		final float baseDistance = Geometry.getDistanceToLine(startX, startY, endX, endY, x, y) - attackerRadius;
		
		if (baseDistance > radius)
		{
			return false;
		}
		
		if (checkHeight)
		{
			final float height = getHeight();
			
			if ((Geometry.getDistanceToLine(startX, startY, startZ, endX, endY, endZ, x, y, z) - attackerRadius - baseDistance) > height)
			{
				return false;
			}
			
			if ((Geometry.getDistanceToLine(startX, startY, startZ, endX, endY, endZ, x, y, z + height) - attackerRadius - baseDistance) > height)
			{
				return false;
			}
		}
		
		return true;
	}
}
