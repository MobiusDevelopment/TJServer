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
package tera.gameserver.events;

/**
 * @author Ronn
 * @created 04.03.2012
 */
public interface Event
{
	/**
	 * Method getName.
	 * @return String
	 */
	public String getName();
	
	/**
	 * Method getType.
	 * @return EventType
	 */
	public EventType getType();
	
	/**
	 * Method isAuto.
	 * @return boolean
	 */
	public boolean isAuto();
	
	/**
	 * Method onLoad.
	 * @return boolean
	 */
	public boolean onLoad();
	
	/**
	 * Method onReload.
	 * @return boolean
	 */
	public boolean onReload();
	
	/**
	 * Method onSave.
	 * @return boolean
	 */
	public boolean onSave();
	
	/**
	 * Method start.
	 * @return boolean
	 */
	public boolean start();
	
	/**
	 * Method stop.
	 * @return boolean
	 */
	public boolean stop();
}
