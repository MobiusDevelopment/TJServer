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
import rlib.util.wraps.Wrap;

/**
 * @author Ronn
 */
public class ActionUpdateCounter extends AbstractQuestAction
{
	private final String[] vars;
	private final int[] counts;
	private final int[] complete;
	
	/**
	 * Constructor for ActionUpdateCounter.
	 * @param type QuestActionType
	 * @param quest Quest
	 * @param condition Condition
	 * @param node Node
	 */
	public ActionUpdateCounter(QuestActionType type, Quest quest, Condition condition, Node node)
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
		
		vars = new String[varArray.size()];
		counts = new int[vars.length];
		complete = new int[vars.length];
		
		for (int i = 0, length = varArray.size(); i < length; i++)
		{
			final String[] vals = varArray.get(i).split("[|]");
			vars[i] = vals[0];
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
		
		boolean doned = true;
		
		for (int i = 0, length = vars.length; i < length; i++)
		{
			final String var = vars[i];
			final Wrap wrap = state.getVar(var);
			
			if (wrap == null)
			{
				counts[i] = 0;
			}
			else
			{
				counts[i] = wrap.getInt();
			}
			
			if (doned && (counts[i] < complete[i]))
			{
				doned = false;
			}
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
		return "ActionUpdateCounter vars = " + java.util.Arrays.toString(vars) + ", counts = " + java.util.Arrays.toString(counts);
	}
}