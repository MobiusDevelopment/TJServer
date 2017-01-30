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

import tera.gameserver.document.DocumentQuestCondition;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Condition;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.links.QuestLink;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.Quest;
import tera.gameserver.model.quests.QuestAction;
import tera.gameserver.model.quests.QuestActionType;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestEventListener;
import tera.gameserver.model.quests.QuestEventType;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestPanelState;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.model.quests.QuestType;
import tera.gameserver.model.quests.Reward;
import tera.gameserver.network.serverpackets.QuestCompleted;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.templates.NpcTemplate;
import tera.util.LocalObjects;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Objects;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class AbstractQuest implements Quest
{
	protected static final Logger log = Loggers.getLogger(Quest.class);
	protected static final DocumentQuestCondition conditionParser = DocumentQuestCondition.getInstance();
	protected String name;
	protected QuestType type;
	protected Reward reward;
	protected QuestEventListener[] events;
	protected Link[] links;
	protected int id;
	protected boolean cancelable;
	
	/**
	 * Constructor for AbstractQuest.
	 * @param type QuestType
	 * @param node Node
	 */
	public AbstractQuest(QuestType type, Node node)
	{
		try
		{
			final VarTable vars = VarTable.newInstance(node);
			this.type = type;
			name = vars.getString("name");
			id = vars.getInteger("id");
			
			if (name.isEmpty())
			{
				System.out.println("found empty name for quest " + id);
			}
			
			cancelable = vars.getBoolean("cancelable", type.isCancelable());
			reward = new Reward();
			final Array<Link> links = Arrays.toArray(Link.class);
			final Array<QuestEventListener> events = Arrays.toArray(QuestEventListener.class);
			final NpcTable npcTable = NpcTable.getInstance();
			
			for (Node nd = node.getFirstChild(); nd != null; nd = nd.getNextSibling())
			{
				switch (nd.getNodeName())
				{
					case "npcs":
					{
						for (Node npc = nd.getFirstChild(); npc != null; npc = npc.getNextSibling())
						{
							if (!"npc".equals(npc.getNodeName()))
							{
								continue;
							}
							
							vars.parse(npc);
							final NpcTemplate template = npcTable.getTemplate(vars.getInteger("id"), vars.getInteger("type"));
							
							if (template != null)
							{
								template.addQuest(this);
							}
						}
						
						break;
					}
					
					case "rewards":
					{
						for (Node act = nd.getFirstChild(); act != null; act = act.getNextSibling())
						{
							if ("action".equals(act.getNodeName()))
							{
								reward.addReward(parseAction(act));
							}
						}
						
						break;
					}
					
					case "links":
					{
						for (Node link = nd.getFirstChild(); link != null; link = link.getNextSibling())
						{
							if ("link".equals(link.getNodeName()))
							{
								links.add(parseLink(link));
							}
						}
						
						break;
					}
					
					case "events":
					{
						for (Node evt = nd.getFirstChild(); evt != null; evt = evt.getNextSibling())
						{
							if ("event".equals(evt.getNodeName()))
							{
								events.add(parseEvent(evt));
							}
						}
						
						break;
					}
				}
			}
			
			links.trimToSize();
			events.trimToSize();
			this.links = links.array();
			this.events = events.array();
		}
		catch (Exception e)
		{
			log.warning(e);
		}
	}
	
	/**
	 * Method addLinks.
	 * @param container Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	@Override
	public final void addLinks(Array<Link> container, Npc npc, Player player)
	{
		final Link[] links = getLinks();
		
		for (Link link2 : links)
		{
			final Link link = link2;
			
			if (!link.test(npc, player))
			{
				continue;
			}
			
			container.add(link);
		}
	}
	
	/**
	 * Method cancel.
	 * @param event QuestEvent
	 * @param force boolean
	 * @see tera.gameserver.model.quests.Quest#cancel(QuestEvent, boolean)
	 */
	@Override
	public void cancel(QuestEvent event, boolean force)
	{
		final Player player = event.getPlayer();
		final Npc npc = event.getNpc();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		if (!force && !cancelable)
		{
			player.sendPacket(SystemMessage.getInstance(MessageType.QUEST_NAME_CANT_BE_ABANDONED).addQuestName(name), true);
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final QuestEvent newEvent = local.getNextQuestEvent();
		newEvent.setType(QuestEventType.CANCELED_QUEST);
		newEvent.setPlayer(player);
		newEvent.setNpc(npc);
		newEvent.setQuest(this);
		notifyQuest(newEvent);
		final QuestList questList = player.getQuestList();
		final QuestState questState = questList.getQuestState(this);
		player.sendPacket(QuestCompleted.getInstance(questState, true), true);
		player.updateQuestInPanel(questState, QuestPanelState.REMOVED);
		questList.finishQuest(this, questState, true);
		
		if (npc != null)
		{
			npc.updateQuestInteresting(player, true);
		}
		
		player.sendPacket(SystemMessage.getInstance(MessageType.ABANDONED_QUEST_NAME).addQuestName(name), true);
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeQuestLog(player.getName() + " cancel quest [id = " + getId() + ", name = " + getName() + "]");
	}
	
	/**
	 * Method finish.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.Quest#finish(QuestEvent)
	 */
	@Override
	public final void finish(QuestEvent event)
	{
		final Player player = event.getPlayer();
		final Npc npc = event.getNpc();
		
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final QuestEvent newEvent = local.getNextQuestEvent();
		newEvent.setType(QuestEventType.FINISHED_QUEST);
		newEvent.setPlayer(player);
		newEvent.setNpc(npc);
		newEvent.setQuest(this);
		notifyQuest(newEvent);
		final QuestList questList = player.getQuestList();
		final QuestState questState = questList.getQuestState(this);
		player.updateQuestInPanel(questState, QuestPanelState.REMOVED);
		questList.complete(this);
		questList.finishQuest(this, questList.getQuestState(this), false);
		reward.giveReward(event);
		
		if (npc != null)
		{
			npc.updateQuestInteresting(player, true);
		}
		
		player.sendPacket(SystemMessage.getInstance(MessageType.CONGRATULATIONS_QUEST_NAME_COMPLETED).addQuestName(id), true);
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeQuestLog(player.getName() + " finish quest [id = " + getId() + ", name = " + getName() + "]");
	}
	
	/**
	 * Method getEvents.
	 * @return QuestEventListener[]
	 */
	protected final QuestEventListener[] getEvents()
	{
		return events;
	}
	
	/**
	 * Method getId.
	 * @return int
	 * @see tera.gameserver.model.quests.Quest#getId()
	 */
	@Override
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Method getLinks.
	 * @return Link[]
	 */
	protected final Link[] getLinks()
	{
		return links;
	}
	
	/**
	 * Method getName.
	 * @return String
	 * @see tera.gameserver.model.quests.Quest#getName()
	 */
	@Override
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getReward.
	 * @return Reward
	 * @see tera.gameserver.model.quests.Quest#getReward()
	 */
	@Override
	public final Reward getReward()
	{
		return reward;
	}
	
	/**
	 * Method getType.
	 * @return QuestType
	 * @see tera.gameserver.model.quests.Quest#getType()
	 */
	@Override
	public final QuestType getType()
	{
		return type;
	}
	
	/**
	 * Method isAvailable.
	 * @param npc Npc
	 * @param player Player
	 * @return boolean
	 * @see tera.gameserver.model.quests.Quest#isAvailable(Npc, Player)
	 */
	@Override
	public final boolean isAvailable(Npc npc, Player player)
	{
		final Link[] links = getLinks();
		
		for (Link link : links)
		{
			if (link.getId() == 1)
			{
				return link.test(npc, player);
			}
		}
		
		return false;
	}
	
	/**
	 * Method notifyQuest.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.Quest#notifyQuest(QuestEvent)
	 */
	@Override
	public final void notifyQuest(QuestEvent event)
	{
		notifyQuest(event.getType(), event);
	}
	
	/**
	 * Method notifyQuest.
	 * @param type QuestEventType
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.Quest#notifyQuest(QuestEventType, QuestEvent)
	 */
	@Override
	public final void notifyQuest(QuestEventType type, QuestEvent event)
	{
		final QuestEventListener[] events = getEvents();
		
		for (QuestEventListener listener : events)
		{
			if (listener.getType() == type)
			{
				listener.notifyQuest(event);
			}
		}
	}
	
	/**
	 * Method parseAction.
	 * @param node Node
	 * @return QuestAction
	 */
	private QuestAction parseAction(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		QuestActionType actionType;
		
		try
		{
			actionType = vars.getEnum("name", QuestActionType.class);
		}
		catch (Exception e)
		{
			return null;
		}
		
		final Condition condition = conditionParser.parseCondition(node, this);
		final QuestAction action = actionType.newInstance(this, condition, node);
		return action;
	}
	
	/**
	 * Method parseEvent.
	 * @param node Node
	 * @return QuestEventListener
	 */
	private QuestEventListener parseEvent(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final Array<QuestAction> actions = Arrays.toArray(QuestAction.class);
		
		for (Node act = node.getFirstChild(); act != null; act = act.getNextSibling())
		{
			if ("action".equals(act.getNodeName()))
			{
				actions.add(parseAction(act));
			}
		}
		
		actions.trimToSize();
		final QuestEventType eventType = vars.getEnum("name", QuestEventType.class);
		return eventType.newInstance(this, actions.array(), node);
	}
	
	/**
	 * Method parseLink.
	 * @param node Node
	 * @return Link
	 */
	private Link parseLink(Node node)
	{
		final VarTable vars = VarTable.newInstance(node);
		final String name = vars.getString("name");
		final int id = vars.getInteger("id");
		final IconType icon = vars.getEnum("icon", IconType.class);
		final Condition condition = conditionParser.parseCondition(node, this);
		return new QuestLink(name, icon, id, this, condition);
	}
	
	/**
	 * Method reload.
	 * @param update Quest
	 */
	@Override
	public final void reload(Quest update)
	{
		Objects.reload((Quest) this, update);
	}
	
	/**
	 * Method reply.
	 * @param npc Npc
	 * @param player Player
	 * @param link Link
	 * @see tera.gameserver.model.npc.interaction.replyes.Reply#reply(Npc, Player, Link)
	 */
	@Override
	public final void reply(Npc npc, Player player, Link link)
	{
		final LocalObjects local = LocalObjects.get();
		final QuestEvent event = local.getNextQuestEvent();
		final Link lastLink = player.getLastLink();
		
		if (lastLink != null)
		{
			event.setType(QuestEventType.SELECT_LINK);
			event.setLink(link);
			event.setNpc(npc);
			event.setPlayer(player);
			event.setQuest(this);
		}
		else
		{
			event.setType(QuestEventType.SELECT_BUTTON);
			event.setLink(link);
			event.setNpc(npc);
			event.setPlayer(player);
			event.setQuest(this);
		}
		
		notifyQuest(event);
	}
	
	/**
	 * Method start.
	 * @param event QuestEvent
	 * @see tera.gameserver.model.quests.Quest#start(QuestEvent)
	 */
	@Override
	public final void start(QuestEvent event)
	{
		final Player player = event.getPlayer();
		final Npc npc = event.getNpc();
		
		if (player == null)
		{
			return;
		}
		
		final QuestList questList = player.getQuestList();
		final QuestState quest = questList.startQuest(this);
		final LocalObjects local = LocalObjects.get();
		final QuestEvent newEvent = local.getNextQuestEvent();
		newEvent.setType(QuestEventType.ACCEPTED_QUEST);
		newEvent.setPlayer(player);
		newEvent.setNpc(npc);
		newEvent.setQuest(this);
		
		if (npc != null)
		{
			npc.updateQuestInteresting(player, true);
		}
		
		notifyQuest(newEvent);
		player.updateQuestInPanel(quest, QuestPanelState.ACCEPTED);
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeQuestLog(player.getName() + " accepted quest [id = " + getId() + ", name = " + getName() + "]");
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "AbstractQuest name = " + name + ", type = " + type + ", reward = " + reward + ", events = " + Arrays.toString(events) + ", links = " + Arrays.toString(links) + ", id = " + id;
	}
}