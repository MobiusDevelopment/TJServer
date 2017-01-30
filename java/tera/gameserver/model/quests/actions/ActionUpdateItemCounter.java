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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestPanelState;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.serverpackets.QuestUpdateCounter;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class ActionUpdateItemCounter extends AbstractQuestAction
{
	private final int[] ids;
	private final int[] counts;
	private final int[] complete;
	
	/**
	 * Constructor for ActionUpdateItemCounter.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionUpdateItemCounter(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		final Array<String> varArray = Arrays.toArray(String.class);
		final NamedNodeMap attrs = node.getAttributes();
		
		for (int i = 0, length = attrs.getLength(); i < length; i++)
		{
			final Node item = attrs.item(i);
			
			if ("name".equals(item.getNodeName()))
			{
				continue;
			}
			
			varArray.add(item.getNodeValue());
		}
		
		ids = new int[varArray.size()];
		counts = new int[ids.length];
		complete = new int[ids.length];
		
		for (int i = 0, length = varArray.size(); i < length; i++)
		{
			final String[] vals = varArray.get(i).split("[|]");
			ids[i] = Integer.parseInt(vals[0]);
			complete[i] = vals.length > 1 ? Integer.parseInt(vals[1]) : Integer.MAX_VALUE;
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
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, "not found quest list");
			return;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state == null)
		{
			log.warning(this, "not found quest state");
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if (inventory == null)
		{
			log.warning(this, new Exception("not found inventory."));
			return;
		}
		
		boolean doned = true;
		inventory.lock();
		
		try
		{
			for (int i = 0, length = ids.length; i < length; i++)
			{
				counts[i] = inventory.getItemCount(ids[i]);
				
				if (doned && (counts[i] < complete[i]))
				{
					doned = false;
				}
			}
		}
		
		finally
		{
			inventory.unlock();
		}
		player.sendPacket(QuestUpdateCounter.getInstance(state, counts, doned), true);
		player.updateQuestInPanel(state, QuestPanelState.UPDATE);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionUpdateCounter vars = " + ids + ", counts = " + java.util.Arrays.toString(counts);
	}
}