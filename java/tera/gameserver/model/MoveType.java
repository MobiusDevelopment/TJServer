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
package tera.gameserver.model;

/**
 * @author Ronn
 */
public enum MoveType
{
	
	RUN,
	
	RUN_FALL(true, false),
	
	SPRINT,
	
	NONE3,
	
	JUMP(false, true),
	
	NONE5,
	
	STOP,
	
	SWIM_RUN,
	
	SWIM_STOP,
	
	JUMP_FALL(true, true),
	
	NONE10,
	
	NONE11,
	
	NONE12,
	
	NONE13,;
	
	public static final int count = values().length;
	
	public static final MoveType[] values = values();
	
	/**
	 * Method getId.
	 * @param type MoveType
	 * @return int
	 */
	public static int getId(MoveType type)
	{
		return type == null ? 0 : type.ordinal();
	}
	
	/**
	 * Method valueOf.
	 * @param id int
	 * @return MoveType
	 */
	public static MoveType valueOf(int id)
	{
		if ((id < 0) || (id >= count))
		{
			return RUN;
		}
		
		return values[id];
	}
	
	private boolean fall;
	
	private boolean jump;
	
	private MoveType()
	{
	}
	
	/**
	 * Constructor for MoveType.
	 * @param fall boolean
	 * @param jump boolean
	 */
	private MoveType(boolean fall, boolean jump)
	{
		this.fall = fall;
		this.jump = jump;
	}
	
	/**
	 * Method isFall.
	 * @return boolean
	 */
	public boolean isFall()
	{
		return fall;
	}
	
	/**
	 * Method isJump.
	 * @return boolean
	 */
	public boolean isJump()
	{
		return jump;
	}
}
