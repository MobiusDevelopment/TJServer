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
package tera.gameserver.model.equipment;

/**
 * @author Ronn
 */
public enum SlotType
{
	
	NONE,
	
	SLOT_HAT,
	
	SLOT_MASK,
	
	SLOT_SHIRT,
	
	SLOT_GLOVES,
	
	SLOT_BOOTS,
	
	SLOT_ARMOR,
	
	SLOT_WEAPON,
	
	SLOT_EARRING,
	
	SLOT_RING,
	
	SLOT_NECKLACE,
	
	SLOT_REMODELABLE_WEAPON,
	
	SLOT_ARMOR_REMODEL;
	
	public static final int SIZE = values().length;
}
