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
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.LocalObjects;

import rlib.util.Rnd;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class ActionDropItem extends AbstractQuestAction
{
	private int id;
	private int min;
	private int max;
	private int chance;
	
	/**
	 * Constructor for ActionDropItem.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionDropItem(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			id = vars.getInteger("id");
			min = vars.getInteger("min");
			max = vars.getInteger("max");
			chance = vars.getInteger("chance");
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
		final Npc npc = event.getNpc();
		
		if (npc == null)
		{
			log.warning(this, "not found npc");
			return;
		}
		
		if (Arrays.contains(Config.WORLD_DONATE_ITEMS, id))
		{
			log.warning(this, new Exception("not create donate item for id " + id));
			return;
		}
		
		if (!Rnd.chance(chance))
		{
			return;
		}
		
		final ItemTable itemTable = ItemTable.getInstance();
		final ItemTemplate template = itemTable.getItem(id);
		
		if (template == null)
		{
			log.warning(this, "not found item template");
			return;
		}
		
		final ItemInstance item = template.newInstance();
		
		if (item.isStackable())
		{
			item.setItemCount(Math.max(Rnd.nextInt(min, max), 1));
		}
		
		final LocalObjects local = LocalObjects.get();
		final Array<ItemInstance> items = local.getNextItemList();
		items.add(item);
		Npc.spawnDropItems(npc, items.array(), items.size());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ActionDropItem id = " + id + ", min = " + min + ", max = " + max + ", chance = " + chance;
	}
}