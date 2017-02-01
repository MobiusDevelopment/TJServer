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

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.replyes.Reply;
import tera.gameserver.model.playable.Player;

import rlib.util.Reloadable;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public interface Quest extends Reply, Reloadable<Quest>
{
	/**
	 * Method addLinks.
	 * @param container Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	void addLinks(Array<Link> container, Npc npc, Player player);
	
	/**
	 * Method cancel.
	 * @param event QuestEvent
	 * @param force boolean
	 */
	void cancel(QuestEvent event, boolean force);
	
	/**
	 * Method finish.
	 * @param event QuestEvent
	 */
	void finish(QuestEvent event);
	
	/**
	 * Method getId.
	 * @return int
	 */
	int getId();
	
	/**
	 * Method getName.
	 * @return String
	 */
	String getName();
	
	/**
	 * Method getReward.
	 * @return Reward
	 */
	Reward getReward();
	
	/**
	 * Method getType.
	 * @return QuestType
	 */
	QuestType getType();
	
	/**
	 * Method isAvailable.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 */
	boolean isAvailable(Npc npc, Player player);
	
	/**
	 * Method notifyQuest.
	 * @param event QuestEvent
	 */
	void notifyQuest(QuestEvent event);
	
	/**
	 * Method notifyQuest.
	 * @param type QuestEventType
	 * @param event QuestEvent
	 */
	void notifyQuest(QuestEventType type, QuestEvent event);
	
	/**
	 * Method start.
	 * @param event QuestEvent
	 */
	void start(QuestEvent event);
}