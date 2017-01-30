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
public enum GuildRankLaw
{
	MEMBER,
	LINE_UP,
	BANK,
	LINE_UP_BANK,
	TITLE,
	LINE_UP_TITLE,
	BANK_TITLE,
	LINE_UP_BANK_TITLE,
	UNKNOW1,
	UNKNOW2,
	UNKNOW3,
	UNKNOW4,
	UNKNOW5,
	UNKNOW6,
	UNKNOW7,
	UNKNOW8,
	GVG,
	LINE_UP_GVG,
	UNKNOW9,
	LINE_UP_BANK_GVG,
	TITLE_GVG,
	LINE_UP_TITLE_GVG,
	BANK_TITLE_GVG,
	LINE_UP_BANK_TITLE_GVG,
	GUILD_MASTER;
	public static final GuildRankLaw[] VALUES = values();
	
	/**
	 * Method valueOf.
	 * @param index int
	 * @return GuildRankLaw
	 */
	public static GuildRankLaw valueOf(int index)
	{
		if ((index < 0) || (index >= VALUES.length))
		{
			return MEMBER;
		}
		
		final GuildRankLaw rank = VALUES[index];
		
		if (rank.name().contains("UNKNOW"))
		{
			return MEMBER;
		}
		
		return rank;
	}
}