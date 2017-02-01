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
public enum SayType
{
	
	MAIN_CHAT,
	
	PARTY_CHAT,
	
	GUILD_CHAT,
	
	SHAUT_CHAT,
	
	TRADE_CHAT,
	
	GROUP_CHAT,
	CLUB_CHAT,
	
	PRIVATE_CHAT,
	
	WHISHPER_CHAT,
	UNKNOWN_1,
	UNKNOWN_2,
	CANAL_1_CHAT,
	CANAL_2_CHAT,
	CANAL_3_CHAT,
	CANAL_4_CHAT,
	CANAL_5_CHAT,
	CANAL_6_CHAT,
	CANAL_7_CHAT,
	CANAL_8_CHAT,
	CANAL_9_CHAT,
	
	LOOKING_FOR_GROUP,
	
	NOTICE_CHAT,
	ALARM_CHAT,
	
	ADMIN_CHAT,
	
	SYSTEM_CHAT,
	
	SIMPLE_SYSTEM_CHAT;
	
	public static final SayType[] VALUES = values();
	
	/**
	 * Method valueOf.
	 * @param index int
	 * @return SayType
	 */
	public static SayType valueOf(int index)
	{
		if ((index < 0) || (index >= VALUES.length))
		{
			return SayType.MAIN_CHAT;
		}
		
		return VALUES[index];
	}
}
