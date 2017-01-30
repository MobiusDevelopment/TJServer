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
package tera.gameserver.network.clientpackets;

import tera.Config;
import tera.gameserver.network.serverpackets.ResultCheckName;

/**
 * @author Ronn
 */
public class CanBeUsedName extends ClientPacket
{
	public static final int CREATE_PLAYER = 1;
	public static final int CREATE_GUILD = 2;
	public static final int INPUT_GUILD_TITLE = 13;
	public static final int INPUT_GUILD_MESSAGE = 12;
	public static final int INPUT_PLAYER_TITLE = 11;
	public static final int INPUT_RANG_NAME = 17;
	public static final int CHANGE_RANG_NAME = 6;
	
	private String name;
	
	private int type;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
	}
	
	@Override
	protected void readImpl()
	{
		readInt();
		readInt();
		readInt();
		readShort();
		type = readInt();
		name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		switch (type)
		{
			case CHANGE_RANG_NAME:
			case CREATE_GUILD:
			case CREATE_PLAYER:
			case INPUT_GUILD_TITLE:
			case INPUT_PLAYER_TITLE:
			case INPUT_RANG_NAME:
			{
				if (!Config.checkName(name))
				{
					return;
				}
			}
		}
		
		owner.sendPacket(ResultCheckName.getInstance(name, type), true);
	}
}
