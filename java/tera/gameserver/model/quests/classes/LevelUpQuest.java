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
package tera.gameserver.model.quests.classes;

import org.w3c.dom.Node;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.listeners.LevelUpListener;
import tera.gameserver.model.listeners.PlayerSpawnListener;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestType;
import tera.util.LocalObjects;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class LevelUpQuest extends AbstractQuest implements LevelUpListener, PlayerSpawnListener
{
	private final int startLevel;
	private final int prev;
	
	/**
	 * Constructor for LevelUpQuest.
	 * @param type QuestType
	 * @param node Node
	 */
	public LevelUpQuest(QuestType type, Node node)
	{
		super(type, node);
		final VarTable vars = VarTable.newInstance(node);
		startLevel = vars.getInteger("startLevel", -1);
		prev = vars.getInteger("prev", 0);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.addLevelUpListener(this);
		eventManager.addPlayerSpawnListener(this);
	}
	
	/**
	 * Method onLevelUp.
	 * @param player Player
	 * @see tera.gameserver.model.listeners.LevelUpListener#onLevelUp(Player)
	 */
	@Override
	public void onLevelUp(Player player)
	{
		if ((player == null) || (player.getLevel() < startLevel))
		{
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if ((questList == null) || questList.isCompleted(this) || (questList.getQuestState(this) != null))
		{
			return;
		}
		
		if ((prev != 0) && !questList.isCompleted(prev))
		{
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final QuestEvent event = local.getNextQuestEvent();
		event.setPlayer(player);
		event.setQuest(this);
		start(event);
	}
	
	/**
	 * Method onSpawn.
	 * @param player Player
	 * @see tera.gameserver.model.listeners.PlayerSpawnListener#onSpawn(Player)
	 */
	@Override
	public void onSpawn(Player player)
	{
		onLevelUp(player);
	}
}