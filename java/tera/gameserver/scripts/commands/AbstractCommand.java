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
package tera.gameserver.scripts.commands;

import java.util.Arrays;

/**
 * @author Ronn
 * @created 13.04.2012
 */
public abstract class AbstractCommand implements Command
{
	
	private final String[] commands;
	
	private final int access;
	
	/**
	 * Constructor for AbstractCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public AbstractCommand(int access, String[] commands)
	{
		this.commands = commands;
		this.access = access;
	}
	
	/**
	 * Method getAccess.
	 * @return int
	 * @see tera.gameserver.scripts.commands.Command#getAccess()
	 */
	@Override
	public int getAccess()
	{
		return access;
	}
	
	/**
	 * Method getCommands.
	 * @return String[]
	 * @see tera.gameserver.scripts.commands.Command#getCommands()
	 */
	@Override
	public String[] getCommands()
	{
		return commands;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "  " + (commands != null ? "commands = " + Arrays.toString(commands) + ", " : "") + "access = " + access;
	}
}
