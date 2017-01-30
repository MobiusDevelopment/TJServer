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

import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.quests.actions.ActionAddExp;
import tera.gameserver.model.quests.actions.ActionAddItem;

import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class Reward
{
	private ActionAddItem[] items;
	private QuestAction[] actions;
	private int exp;
	private int money;
	
	/**
	 * Method addReward.
	 * @param action QuestAction
	 */
	public void addReward(QuestAction action)
	{
		if (action == null)
		{
			return;
		}
		
		setActions(Arrays.addToArray(getActions(), action, QuestAction.class));
		
		if (action.getType() == QuestActionType.ADD_EXP)
		{
			exp = ((ActionAddExp) action).getExp();
		}
		else if (action.getType() == QuestActionType.ADD_ITEM)
		{
			final ActionAddItem item = (ActionAddItem) action;
			
			if (item.getItemId() == Inventory.MONEY_ITEM_ID)
			{
				money = item.getItemCount();
			}
			else
			{
				setItems(Arrays.addToArray(getItems(), item, ActionAddItem.class));
			}
		}
	}
	
	/**
	 * Method getActions.
	 * @return QuestAction[]
	 */
	private final QuestAction[] getActions()
	{
		return actions;
	}
	
	/**
	 * Method getExp.
	 * @return int
	 */
	public int getExp()
	{
		return exp;
	}
	
	/**
	 * Method getItems.
	 * @return ActionAddItem[]
	 */
	public ActionAddItem[] getItems()
	{
		return items;
	}
	
	/**
	 * Method getMoney.
	 * @return int
	 */
	public int getMoney()
	{
		return money;
	}
	
	/**
	 * Method giveReward.
	 * @param event QuestEvent
	 */
	public void giveReward(QuestEvent event)
	{
		final QuestAction[] actions = getActions();
		
		if (actions == null)
		{
			return;
		}
		
		for (QuestAction action : actions)
		{
			if (action.test(event.getNpc(), event.getPlayer()))
			{
				action.apply(event);
			}
		}
	}
	
	/**
	 * Method setActions.
	 * @param actions QuestAction[]
	 */
	private final void setActions(QuestAction[] actions)
	{
		this.actions = actions;
	}
	
	/**
	 * Method setItems.
	 * @param items ActionAddItem[]
	 */
	private final void setItems(ActionAddItem[] items)
	{
		this.items = items;
	}
}