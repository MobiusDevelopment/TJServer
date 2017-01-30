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
package tera.gameserver.model.base;

/**
 * @author Ronn
 */
public enum PlayerClass
{
	
	WARRIOR(0, false, false, 1.057F),
	
	LANCER(1, false, false, 1.057F),
	
	SLAYER(2, false, false, 1.057F),
	
	BERSERKER(3, false, false, 1.0575F),
	
	SORCERER(4, true, true, 1.058F),
	
	ARCHER(5, false, true, 1.057F),
	
	PRIEST(6, true, true, 1.0581F),
	
	MYSTIC(7, true, true, 1.0581F);
	
	public static final PlayerClass[] values = values();
	
	public static final int length = values.length;
	
	/**
	 * Method getClassById.
	 * @param id int
	 * @return PlayerClass
	 */
	public static PlayerClass getClassById(int id)
	{
		if ((id < 0) || (id >= length))
		{
			return null;
		}
		
		return values[id];
	}
	
	private final int id;
	
	private final float hpMod;
	
	private final boolean mage;
	
	private final boolean range;
	
	/**
	 * Constructor for PlayerClass.
	 * @param id int
	 * @param mage boolean
	 * @param range boolean
	 * @param hpMod float
	 */
	private PlayerClass(int id, boolean mage, boolean range, float hpMod)
	{
		this.id = id;
		this.mage = mage;
		this.range = range;
		this.hpMod = hpMod;
	}
	
	/**
	 * Method getHpMod.
	 * @return float
	 */
	public float getHpMod()
	{
		return hpMod;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method isMage.
	 * @return boolean
	 */
	public final boolean isMage()
	{
		return mage;
	}
	
	/**
	 * Method isRange.
	 * @return boolean
	 */
	public final boolean isRange()
	{
		return range;
	}
}