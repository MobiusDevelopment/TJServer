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

import tera.gameserver.IdFactory;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.QuestCompleteList;
import tera.gameserver.network.serverpackets.QuestSplit;
import tera.gameserver.network.serverpackets.QuestStarted;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.array.FuncElement;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;
import rlib.util.table.FuncValue;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public final class QuestList implements Foldable
{
	private static final Logger log = Loggers.getLogger(QuestList.class);
	private static final FoldablePool<QuestList> pool = Pools.newConcurrentFoldablePool(QuestList.class);
	
	/**
	 * Method newInstance.
	 * @param owner Player
	 * @return QuestList
	 */
	public static QuestList newInstance(Player owner)
	{
		QuestList list = pool.take();
		
		if (list == null)
		{
			list = new QuestList();
		}
		
		list.owner = owner;
		return list;
	}
	
	private final FoldablePool<QuestDate> questDatePool;
	private final FoldablePool<QuestState> questStatePool;
	private final Table<IntKey, QuestDate> completed;
	private final FuncValue<QuestDate> questDateFunc;
	private final Array<QuestState> active;
	private final FuncElement<QuestState> questStateFunc;
	private Player owner;
	
	public QuestList()
	{
		questDatePool = Pools.newConcurrentFoldablePool(QuestDate.class);
		questStatePool = Pools.newConcurrentFoldablePool(QuestState.class);
		completed = Tables.newConcurrentIntegerTable();
		active = Arrays.toConcurrentArray(QuestState.class);
		questDateFunc = value -> questDatePool.put(value);
		questStateFunc = element -> questStatePool.put(element);
	}
	
	/**
	 * Method addActiveQuest.
	 * @param state QuestState
	 */
	public void addActiveQuest(QuestState state)
	{
		active.add(state);
	}
	
	/**
	 * Method addCompleteQuest.
	 * @param date QuestDate
	 */
	public void addCompleteQuest(QuestDate date)
	{
		completed.put(date.getQuestId(), date);
	}
	
	/**
	 * Method complete.
	 * @param quest Quest
	 */
	public void complete(Quest quest)
	{
		final QuestDate old = completed.get(quest.getId());
		final long time = System.currentTimeMillis();
		
		if (old != null)
		{
			old.setTime(time);
		}
		else
		{
			completed.put(quest.getId(), newQuestDate(time, quest));
		}
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		owner = null;
		completed.apply(questDateFunc);
		completed.clear();
		active.apply(questStateFunc);
		active.clear();
	}
	
	/**
	 * Method finishQuest.
	 * @param quest Quest
	 * @param state QuestState
	 * @param canceled boolean
	 */
	public void finishQuest(Quest quest, QuestState state, boolean canceled)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			log.warning("not found owner.");
			return;
		}
		
		if (state == null)
		{
			log.warning("not found quest " + quest);
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		if (canceled)
		{
			dbManager.removeQuest(owner, quest);
		}
		else
		{
			dbManager.finishQuest(owner, completed.get(quest.getId()));
		}
		
		active.fastRemove(state);
		state.finish();
		questStatePool.put(state);
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getActiveQuests.
	 * @return Array<QuestState>
	 */
	public Array<QuestState> getActiveQuests()
	{
		return active;
	}
	
	/**
	 * Method getOwner.
	 * @return Player
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * Method getQuestDate.
	 * @param questId int
	 * @return QuestDate
	 */
	public QuestDate getQuestDate(int questId)
	{
		return completed.get(questId);
	}
	
	/**
	 * Method getQuestState.
	 * @param objectId int
	 * @return QuestState
	 */
	public QuestState getQuestState(int objectId)
	{
		final Array<QuestState> active = getActiveQuests();
		active.readLock();
		
		try
		{
			final QuestState[] array = active.array();
			
			for (int i = 0, length = active.size(); i < length; i++)
			{
				final QuestState state = array[i];
				
				if (state.getObjectId() == objectId)
				{
					return state;
				}
			}
			
			return null;
		}
		
		finally
		{
			active.readUnlock();
		}
	}
	
	/**
	 * Method getQuestState.
	 * @param quest Quest
	 * @return QuestState
	 */
	public QuestState getQuestState(Quest quest)
	{
		final Array<QuestState> active = getActiveQuests();
		active.readLock();
		
		try
		{
			final QuestState[] array = active.array();
			
			for (int i = 0, length = active.size(); i < length; i++)
			{
				final QuestState state = array[i];
				
				if (state.getQuestId() == quest.getId())
				{
					return state;
				}
			}
			
			return null;
		}
		
		finally
		{
			active.readUnlock();
		}
	}
	
	/**
	 * Method hasActiveQuest.
	 * @return boolean
	 */
	public boolean hasActiveQuest()
	{
		return !active.isEmpty();
	}
	
	/**
	 * Method isCompleted.
	 * @param questId int
	 * @return boolean
	 */
	public boolean isCompleted(int questId)
	{
		return completed.containsKey(questId);
	}
	
	/**
	 * Method isCompleted.
	 * @param quest Quest
	 * @return boolean
	 */
	public boolean isCompleted(Quest quest)
	{
		return completed.containsKey(quest.getId());
	}
	
	/**
	 * Method newQuestDate.
	 * @param time long
	 * @param quest Quest
	 * @return QuestDate
	 */
	public QuestDate newQuestDate(long time, Quest quest)
	{
		QuestDate date = questDatePool.take();
		
		if (date == null)
		{
			date = new QuestDate();
		}
		
		date.setTime(time);
		date.setQuest(quest);
		return date;
	}
	
	/**
	 * Method newQuestState.
	 * @param owner Player
	 * @param quest Quest
	 * @param stage int
	 * @return QuestState
	 */
	public QuestState newQuestState(Player owner, Quest quest, int stage)
	{
		QuestState state = questStatePool.take();
		
		if (state == null)
		{
			state = new QuestState();
		}
		
		state.setState(stage);
		state.setPlayer(owner);
		state.setQuest(quest);
		final IdFactory idFactory = IdFactory.getInstance();
		state.setObjectId(idFactory.getNextQuestId());
		return state;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method removeQuestComplete.
	 * @param id int
	 */
	public void removeQuestComplete(int id)
	{
		completed.remove(id);
	}
	
	public void save()
	{
		final Array<QuestState> active = getActiveQuests();
		active.readLock();
		
		try
		{
			final QuestState[] array = active.array();
			
			for (int i = 0, length = active.size(); i < length; i++)
			{
				array[i].save();
			}
		}
		
		finally
		{
			active.readUnlock();
		}
	}
	
	/**
	 * Method startQuest.
	 * @param quest Quest
	 * @return QuestState
	 */
	public QuestState startQuest(Quest quest)
	{
		final QuestState state = newQuestState(owner, quest, 1);
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.createQuest(state);
		active.add(state);
		return state;
	}
	
	public void updateQuestList()
	{
		final Player owner = getOwner();
		final Array<QuestState> active = getActiveQuests();
		active.readLock();
		
		try
		{
			final QuestState[] array = active.array();
			
			for (int i = 0, length = active.size(); i < length; i++)
			{
				owner.sendPacket(QuestStarted.getInstance(array[i], 0, 0, 0, 0, 0), true);
			}
			
			owner.sendPacket(QuestSplit.getInstance(), true);
			owner.sendPacket(QuestCompleteList.getInstance(completed), true);
		}
		
		finally
		{
			active.readUnlock();
		}
	}
}