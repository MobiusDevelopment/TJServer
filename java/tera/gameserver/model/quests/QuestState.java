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

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.table.FuncValue;
import rlib.util.table.Table;
import rlib.util.table.Tables;
import rlib.util.wraps.Wrap;

/**
 * @author Ronn
 */
public final class QuestState implements Foldable
{
	private static final FuncValue<Wrap> FOLD_WRAPS = value -> value.fold();
	private final Table<String, Wrap> variables;
	private final Array<String> varNames;
	private Player player;
	private Quest quest;
	private QuestPanelState panelState;
	private int objectId;
	private int state;
	
	public QuestState()
	{
		panelState = QuestPanelState.REMOVED;
		variables = Tables.newConcurrentObjectTable();
		varNames = Arrays.toArray(String.class);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
		quest = null;
		variables.apply(FOLD_WRAPS);
		variables.clear();
	}
	
	public void finish()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.clearQuestVar(this);
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method getPanelState.
	 * @return QuestPanelState
	 */
	public QuestPanelState getPanelState()
	{
		return panelState;
	}
	
	/**
	 * Method getPanelStateId.
	 * @return int
	 */
	public int getPanelStateId()
	{
		return panelState.ordinal();
	}
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Method getQuest.
	 * @return Quest
	 */
	public Quest getQuest()
	{
		return quest;
	}
	
	/**
	 * Method getQuestId.
	 * @return int
	 */
	public int getQuestId()
	{
		return quest.getId();
	}
	
	/**
	 * Method getState.
	 * @return int
	 */
	public int getState()
	{
		return state;
	}
	
	/**
	 * Method getVar.
	 * @param name String
	 * @return Wrap
	 */
	public Wrap getVar(String name)
	{
		return variables.get(name);
	}
	
	/**
	 * Method removeWar.
	 * @param name String
	 */
	public void removeWar(String name)
	{
		final Wrap wrap = variables.remove(name);
		
		if (wrap != null)
		{
			wrap.fold();
		}
	}
	
	/**
	 * Method getVariables.
	 * @return Table<String,Wrap>
	 */
	public Table<String, Wrap> getVariables()
	{
		return variables;
	}
	
	/**
	 * Method getVarNames.
	 * @return Array<String>
	 */
	public Array<String> getVarNames()
	{
		return varNames;
	}
	
	public void prepare()
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.restoreQuestVar(this);
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	public void save()
	{
		final Table<String, Wrap> variables = getVariables();
		
		if (variables.isEmpty())
		{
			return;
		}
		
		variables.readLock();
		
		try
		{
			final Array<String> names = getVarNames();
			names.clear();
			variables.keyArray(names);
			final String[] array = names.array();
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			
			for (int i = 0, length = names.size(); i < length; i++)
			{
				final String name = array[i];
				dbManager.storyQuestVar(this, name, variables.get(name));
			}
		}
		
		finally
		{
			variables.readUnlock();
		}
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method setPanelState.
	 * @param panelState QuestPanelState
	 */
	public void setPanelState(QuestPanelState panelState)
	{
		this.panelState = panelState;
	}
	
	/**
	 * Method setPlayer.
	 * @param player Player
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Method setQuest.
	 * @param quest Quest
	 */
	public void setQuest(Quest quest)
	{
		if (quest == null)
		{
			new Exception("not found quest").printStackTrace();
		}
		
		this.quest = quest;
	}
	
	/**
	 * Method setState.
	 * @param state int
	 */
	public void setState(int state)
	{
		this.state = state;
	}
	
	/**
	 * Method setVar.
	 * @param name String
	 * @param wrap Wrap
	 */
	public void setVar(String name, Wrap wrap)
	{
		variables.put(name, wrap);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "QuestState  variables = " + variables + ", player = " + player + ", quest = " + quest + ", objectId = " + objectId + ", state = " + state;
	}
}