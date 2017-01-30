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
package tera.gameserver.network.clientpackets;

import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestPanelState;
import tera.gameserver.model.quests.QuestState;

/**
 * @author Ronn
 */
public class RequestUpdateQuestPanel extends ClientPacket
{
	private Player player;
	private QuestPanelState panelState;
	private int objectId;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		objectId = readInt();
		readByte();
		panelState = QuestPanelState.valueOf(readShort());
	}
	
	@Override
	public void runImpl()
	{
		if (player == null)
		{
			return;
		}
		
		final QuestList questList = player.getQuestList();
		
		if (questList == null)
		{
			return;
		}
		
		final QuestState quest = questList.getQuestState(objectId);
		
		if (quest == null)
		{
			return;
		}
		
		if (quest.getPanelState() == panelState)
		{
			return;
		}
		
		player.updateQuestInPanel(quest, panelState);
	}
}