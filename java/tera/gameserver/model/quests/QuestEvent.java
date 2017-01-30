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
package tera.gameserver.model.quests;

import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;

/**
 * @author Ronn
 */
public final class QuestEvent
{
	private QuestEventType type;
	private Link link;
	private Quest quest;
	private Player player;
	private Npc npc;
	private ItemInstance item;
	private ResourseInstance resourse;
	private int value;
	
	/**
	 * Method clear.
	 * @return QuestEvent
	 */
	public QuestEvent clear()
	{
		link = null;
		player = null;
		quest = null;
		type = null;
		item = null;
		resourse = null;
		value = 0;
		return this;
	}
	
	/**
	 * Method getItem.
	 * @return ItemInstance
	 */
	public ItemInstance getItem()
	{
		return item;
	}
	
	/**
	 * Method getLink.
	 * @return Link
	 */
	public Link getLink()
	{
		return link;
	}
	
	/**
	 * Method getNpc.
	 * @return Npc
	 */
	public Npc getNpc()
	{
		return npc;
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Method getQuest.
	 * @return Quest
	 */
	public Quest getQuest()
	{
		return quest;
	}
	
	/**
	 * Method getResourse.
	 * @return ResourseInstance
	 */
	public final ResourseInstance getResourse()
	{
		return resourse;
	}
	
	/**
	 * Method getType.
	 * @return QuestEventType
	 */
	public QuestEventType getType()
	{
		return type;
	}
	
	/**
	 * Method getValue.
	 * @return int
	 */
	public final int getValue()
	{
		return value;
	}
	
	/**
	 * Method setItem.
	 * @param item ItemInstance
	 */
	public void setItem(ItemInstance item)
	{
		this.item = item;
	}
	
	/**
	 * Method setLink.
	 * @param link Link
	 */
	public void setLink(Link link)
	{
		this.link = link;
	}
	
	/**
	 * Method setNpc.
	 * @param npc Npc
	 */
	public void setNpc(Npc npc)
	{
		this.npc = npc;
	}
	
	/**
	 * Method setPlayer.
	 * @param player Player
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Method setQuest.
	 * @param quest Quest
	 */
	public void setQuest(Quest quest)
	{
		this.quest = quest;
	}
	
	/**
	 * Method setResourse.
	 * @param resourse ResourseInstance
	 */
	public final void setResourse(ResourseInstance resourse)
	{
		this.resourse = resourse;
	}
	
	/**
	 * Method setType.
	 * @param type QuestEventType
	 */
	public void setType(QuestEventType type)
	{
		this.type = type;
	}
	
	/**
	 * Method setValue.
	 * @param value int
	 */
	public final void setValue(int value)
	{
		this.value = value;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "QuestEvent type = " + type + ", link = " + link + ", quest = " + quest + ", player = " + player + ", npc = " + npc + ", item = " + item + ", resourse = " + resourse;
	}
}