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

import tera.gameserver.model.Character;

/**
 * @author Ronn
 * @param <E>
 */
public abstract class AbstractGeom<E extends Character> implements Geom
{
	
	protected E character;
	
	protected float radius;
	
	protected float height;
	
	/**
	 * Constructor for AbstractGeom.
	 * @param character E
	 * @param height float
	 * @param radius float
	 */
	public AbstractGeom(E character, float height, float radius)
	{
		this.character = character;
		this.height = height;
		this.radius = radius;
	}
	
	/**
	 * Method getCharacter.
	 * @return E
	 */
	protected E getCharacter()
	{
		return character;
	}
	
	/**
	 * Method getHeight.
	 * @return float
	 * @see tera.gameserver.model.geom.Geom#getHeight()
	 */
	@Override
	public float getHeight()
	{
		return height;
	}
	
	/**
	 * Method getRadius.
	 * @return float
	 * @see tera.gameserver.model.geom.Geom#getRadius()
	 */
	@Override
	public float getRadius()
	{
		return radius;
	}
}
