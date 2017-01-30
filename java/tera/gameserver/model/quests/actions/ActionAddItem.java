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

import tera.Config;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;

import rlib.util.VarTable;

/**
 * @author Ronn
 */
public class ActionAddItem extends AbstractQuestAction
{
	private int itemId;
	private int itemCount;
	private boolean reward;
	
	/**
	 * Constructor for ActionAddItem.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionAddItem(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			reward = vars.getBoolean("reward", false);
			itemId = vars.getInteger("id");
			itemCount = (int) (vars.getInteger("count") * (isReward() ? Config.SERVER_RATE_QUEST_REWARD : 1));
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Method isReward.
	 * @return boolean
	 */
	public boolean isReward()
	{
		return reward;
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
		
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(itemId);
		
		if (template == null)
		{
			log.warning(this, "not found item for " + itemId);
			return;
		}
		
		int itemCount = getItemCount();
		
		if (Config.ACCOUNT_PREMIUM_QUEST && player.hasPremium())
		{
			itemCount *= Config.ACCOUNT_PREMIUM_QUEST_RATE;
		}
		
		if (!template.isStackable())
		{
			itemCount = 1;
		}
		
		if (!inventory.forceAddItem(itemId, itemCount, quest.getName()))
		{
			return;
		}
		
		if (itemId != Inventory.MONEY_ITEM_ID)
		{
			player.sendPacket(MessageAddedItem.getInstance(player.getName(), itemId, itemCount), true);
		}
		else
		{
			PacketManager.showAddGold(player, itemCount);
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeItemLog("Quest [id = " + quest.getId() + ", name = " + quest.getName() + "] added item [id = " + itemId + ", count = " + itemCount + ", name = " + template.getName() + "] to " + player.getName());
		eventManager.notifyInventoryChanged(player);
	}
	
	/**
	 * Method getItemCount.
	 * @return int
	 */
	public int getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "AddItem itemId = " + itemId + ", itemCount = " + itemCount;
	}
}