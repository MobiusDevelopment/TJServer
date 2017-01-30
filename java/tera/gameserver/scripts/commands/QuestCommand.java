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
package tera.gameserver.scripts.commands;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.QuestManager;
import tera.gameserver.model.World;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.network.serverpackets.QuestInfo;
import tera.gameserver.network.serverpackets.QuestMoveToPanel;
import tera.gameserver.network.serverpackets.QuestStarted;
import tera.gameserver.network.serverpackets.QuestVideo;
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class QuestCommand extends AbstractCommand
{
	/**
	 * Constructor for QuestCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public QuestCommand(int access, String[] commands)
	{
		super(access, commands);
	}
	
	/**
	 * Method execution.
	 * @param command String
	 * @param player Player
	 * @param values String
	 * @see tera.gameserver.scripts.commands.Command#execution(String, Player, String)
	 */
	@Override
	public void execution(String command, Player player, String values)
	{
		final QuestManager questManager = QuestManager.getInstance();
		
		switch (command)
		{
			case "quest_reload":
			{
				questManager.reload();
				player.sendMessage("quests reloaded.");
				break;
			}
			
			case "quest_remove":
			{
				final QuestList questList = player.getQuestList();
				questList.removeQuestComplete(Integer.parseInt(values));
				final DataBaseManager dbManager = DataBaseManager.getInstance();
				dbManager.removeQuest(player, questManager.getQuest(Integer.parseInt(values)));
				player.sendMessage("Completed quest is removed.");
				break;
			}
			
			case "quest_state":
			{
				final String[] vals = values.split(" ");
				final QuestList questList = player.getQuestList();
				final Quest quest = questManager.getQuest(Integer.parseInt(vals[0]));
				final QuestState questState = questList.getQuestState(quest);
				questState.setState(Integer.parseInt(vals[1]));
				player.sendPacket(QuestStarted.getInstance(questState, 0, 0, 0, 0, 0), true);
				player.sendPacket(QuestMoveToPanel.getInstance(questState), true);
				player.sendMessage("Quest \"" + quest.getName() + "\" transferred to " + questState.getState() + " step.");
				break;
			}
			
			case "quest_cond":
			{
				final String text = "<FONT FACE=\"$ChatFont\" SIZE=\"18\" COLOR=\"#F6DA21\" KERNING=\"0\"><A HREF=\"asfunction:chatLinkAction,2#####" + values + "`15\">&lt;quest info&gt;</A></FONT><FONT>";
				player.sendMessage(text);
				break;
			}
			
			case "quest_cancel":
			{
				final Quest quest = questManager.getQuest(Integer.parseInt(values));
				
				if (quest == null)
				{
					player.sendMessage("Quest doesn't exist.");
					return;
				}
				
				final LocalObjects local = LocalObjects.get();
				final QuestEvent event = local.getNextQuestEvent();
				event.setQuest(quest);
				event.setPlayer(player);
				quest.cancel(event, true);
				break;
			}
			
			case "quest_movie":
			{
				player.sendPacket(QuestVideo.getInstance(Integer.parseInt(values)), true);
				break;
			}
			
			case "quest_accept":
			{
				final Quest quest = questManager.getQuest(Integer.parseInt(values));
				
				if (quest == null)
				{
					player.sendMessage("Quest doesn't exist.");
					return;
				}
				
				final LocalObjects local = LocalObjects.get();
				final QuestEvent event = local.getNextQuestEvent();
				event.setQuest(quest);
				event.setPlayer(player);
				quest.start(event);
				player.sendMessage("Quest " + quest.getName() + "accepted.");
				break;
			}
			
			case "quest_start":
			{
				final String[] vals = values.split(" ");
				final int id = Integer.parseInt(vals[0]);
				final int state = Integer.parseInt(vals[1]);
				final QuestList questList = player.getQuestList();
				final QuestState qs = questList.newQuestState(player, questManager.getQuest(id), state);
				player.sendPacket(QuestStarted.getInstance(qs, 0, 0, 0, 0, 0), true);
				player.sendPacket(QuestMoveToPanel.getInstance(qs), true);
				break;
			}
			
			case "quest_info":
			{
				final LocalObjects local = LocalObjects.get();
				final Array<Npc> npcs = World.getAround(Npc.class, local.getNextNpcList(), player);
				
				if (npcs.isEmpty())
				{
					return;
				}
				
				final String[] vals = values.split(" ");
				final Quest quest = questManager.getQuest(Integer.parseInt(vals[0]));
				player.sendPacket(QuestInfo.getInstance(npcs.first(), player, quest, Integer.parseInt(vals[2]), Integer.parseInt(vals[1]), "quest_info"), true);
				break;
			}
		}
	}
}