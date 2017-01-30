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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ronn
 */
public class GuildIcon
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH_mm_ss");
	private String name;
	private byte[] icon;
	
	/**
	 * Constructor for GuildIcon.
	 * @param name String
	 * @param icon byte[]
	 */
	public GuildIcon(String name, byte[] icon)
	{
		this.name = name;
		this.icon = icon;
	}
	
	/**
	 * Method getIcon.
	 * @return byte[]
	 */
	public byte[] getIcon()
	{
		return icon;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Method hasIcon.
	 * @return boolean
	 */
	public boolean hasIcon()
	{
		return (icon != null) && (icon.length > 4);
	}
	
	/**
	 * Method setIcon.
	 * @param guild Guild
	 * @param icon byte[]
	 */
	public void setIcon(Guild guild, byte[] icon)
	{
		this.icon = icon;
		name = "guildlogo_" + guild.getId() + "_" + timeFormat.format(new Date());
	}
}