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
package tera.gameserver.model.quests.actions;

import org.w3c.dom.Node;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ActionRemoveItem extends AbstractQuestAction
{
	private int id;
	private long count;
	
	/**
	 * Constructor for ActionRemoveItem.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionRemoveItem(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			id = vars.getInteger("id");
			count = vars.getInteger("count");
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
	}
	
	/**
	 * Method apply.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.QuestAction#apply(QuestEvent)
	 */
	@Override
	public void apply(QuestEvent event)
	{
		final Player player = event.getPlayer();
		
		if (player == null)
		{
			log.warning(this, "not found player");
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, "not found inventory");
			return;
		}
		
		if (count == -1)
		{
			if (inventory.removeItem(id))
			{
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(player);
			}
		}
		else if (inventory.removeItem(id, count))
		{
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyInventoryChanged(player);
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionRemoveItem id = " + id + ", count = " + count;
	}
}