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
package tera.gameserver.model.resourse;

import tera.gameserver.model.playable.Player;
import tera.gameserver.templates.ResourseTemplate;

/**
 * @author Ronn
 */
public class QuestResourse extends ResourseInstance
{
	/**
	 * Constructor for QuestResourse.
	 * @param objectId int
	 * @param template ResourseTemplate
	 */
	public QuestResourse(int objectId, ResourseTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * Method checkCondition.
	 * @param collector Player
	 * @return boolean
	 */
	@Override
	public boolean checkCondition(Player collector)
	{
		return true;
	}
	
	/**
	 * Method getChanceFor.
	 * @param player Player
	 * @return int
	 */
	@Override
	public int getChanceFor(Player player)
	{
		return 100;
	}
	
	/**
	 * Method increaseReq.
	 * @param player Player
	 */
	@Override
	public void increaseReq(Player player)
	{
	}
}