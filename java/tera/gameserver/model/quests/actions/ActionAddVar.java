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

import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;

import rlib.util.VarTable;
import rlib.util.wraps.Wrap;
import rlib.util.wraps.Wraps;

/**
 * @author Ronn
 */
public class ActionAddVar extends AbstractQuestAction
{
	private String name;
	private int value;
	
	/**
	 * Constructor for ActionAddVar.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionAddVar(QuestActionType type, Quest quest, Condition condition, Node node)
	{
		super(type, quest, condition, node);
		
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			name = vars.getString("var");
			value = vars.getInteger("value");
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
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			log.warning(this, "not found questList");
			return;
		}
		
		final QuestState state = questList.getQuestState(quest);
		
		if (state == null)
		{
			log.warning(this, "not found quest state");
			return;
		}
		
		final Wrap wrap = state.getVar(name);
		
		if (wrap != null)
		{
			wrap.setInt(wrap.getInt() + value);
		}
		else
		{
			state.setVar(name, Wraps.newIntegerWrap(value, true));
		}
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "AddVar name = " + name + ", value = " + value;
	}
}