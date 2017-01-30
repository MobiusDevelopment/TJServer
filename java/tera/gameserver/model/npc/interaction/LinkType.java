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
package tera.gameserver.model.npc.interaction;

/**
 * @author Ronn
 */
public enum LinkType
{
	QUEST(24),
	DIALOG(0),
	TRADE(26),
	SHOP(28),
	FLY_PEGAS(26),;
	private int id;
	
	/**
	 * Constructor for LinkType.
	 * @param id int
	 */
	private LinkType(int id)
	{
		this.id = id;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public final int getId()
	{
		return id;
	}
}