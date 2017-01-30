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
package tera.util;

import tera.gameserver.ServerThread;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.TObject;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.AggroInfo;
import tera.gameserver.model.npc.Minion;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestEvent;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class LocalObjects
{
	private static final int DEFAULT_BUFFER_SIZE = 20;
	
	/**
	 * Method get.
	 * @return LocalObjects
	 */
	public static LocalObjects get()
	{
		return ServerThread.currentThread().getLocal();
	}
	
	private final Array<Player>[] playerLists;
	private final Array<Minion>[] minionLists;
	private final Array<ItemInstance>[] itemLists;
	private final Array<Npc>[] npcLists;
	private final Array<Character>[] charLists;
	private final Array<TObject>[] objectLists;
	private final Array<Link>[] linkLists;
	private final Array<AggroInfo>[] aggroInfoLists;
	private final QuestEvent[] questEvents;
	private final AttackInfo[] attackInfos;
	private int playerListIndex;
	private int minionListIndex;
	private int itemListIndex;
	private int npcListIndex;
	private int charListIndex;
	private int objectListIndex;
	private int linkListIndex;
	private int aggroInfoListIndex;
	private int questEventIndex;
	private int attackInfotIndex;
	
	@SuppressWarnings("unchecked")
	public LocalObjects()
	{
		playerLists = new Array[DEFAULT_BUFFER_SIZE];
		minionLists = new Array[DEFAULT_BUFFER_SIZE];
		itemLists = new Array[DEFAULT_BUFFER_SIZE];
		npcLists = new Array[DEFAULT_BUFFER_SIZE];
		charLists = new Array[DEFAULT_BUFFER_SIZE];
		objectLists = new Array[DEFAULT_BUFFER_SIZE];
		linkLists = new Array[DEFAULT_BUFFER_SIZE];
		aggroInfoLists = new Array[DEFAULT_BUFFER_SIZE];
		questEvents = new QuestEvent[DEFAULT_BUFFER_SIZE];
		attackInfos = new AttackInfo[DEFAULT_BUFFER_SIZE];
		
		for (int i = 0, length = DEFAULT_BUFFER_SIZE; i < length; i++)
		{
			playerLists[i] = Arrays.toArray(Player.class);
			minionLists[i] = Arrays.toArray(Minion.class);
			itemLists[i] = Arrays.toArray(ItemInstance.class);
			npcLists[i] = Arrays.toArray(Npc.class);
			charLists[i] = Arrays.toArray(Character.class);
			objectLists[i] = Arrays.toArray(TObject.class);
			linkLists[i] = Arrays.toArray(Link.class);
			aggroInfoLists[i] = Arrays.toArray(AggroInfo.class);
			questEvents[i] = new QuestEvent();
			attackInfos[i] = new AttackInfo();
		}
	}
	
	/**
	 * Method getNextAggroInfoList.
	 * @return Array<AggroInfo>
	 */
	public Array<AggroInfo> getNextAggroInfoList()
	{
		if (aggroInfoListIndex == DEFAULT_BUFFER_SIZE)
		{
			aggroInfoListIndex = 0;
		}
		
		return aggroInfoLists[aggroInfoListIndex++].clear();
	}
	
	/**
	 * Method getNextAttackInfo.
	 * @return AttackInfo
	 */
	public AttackInfo getNextAttackInfo()
	{
		if (attackInfotIndex == DEFAULT_BUFFER_SIZE)
		{
			attackInfotIndex = 0;
		}
		
		return attackInfos[attackInfotIndex++].clear();
	}
	
	/**
	 * Method getNextCharList.
	 * @return Array<Character>
	 */
	public Array<Character> getNextCharList()
	{
		if (charListIndex == DEFAULT_BUFFER_SIZE)
		{
			charListIndex = 0;
		}
		
		return charLists[charListIndex++].clear();
	}
	
	/**
	 * Method getNextItemList.
	 * @return Array<ItemInstance>
	 */
	public Array<ItemInstance> getNextItemList()
	{
		if (itemListIndex == DEFAULT_BUFFER_SIZE)
		{
			itemListIndex = 0;
		}
		
		return itemLists[itemListIndex++].clear();
	}
	
	/**
	 * Method getNextLinkList.
	 * @return Array<Link>
	 */
	public Array<Link> getNextLinkList()
	{
		if (linkListIndex == DEFAULT_BUFFER_SIZE)
		{
			linkListIndex = 0;
		}
		
		return linkLists[linkListIndex++].clear();
	}
	
	/**
	 * Method getNextMinionList.
	 * @return Array<Minion>
	 */
	public Array<Minion> getNextMinionList()
	{
		if (minionListIndex == DEFAULT_BUFFER_SIZE)
		{
			minionListIndex = 0;
		}
		
		return minionLists[minionListIndex++].clear();
	}
	
	/**
	 * Method getNextNpcList.
	 * @return Array<Npc>
	 */
	public Array<Npc> getNextNpcList()
	{
		if (npcListIndex == DEFAULT_BUFFER_SIZE)
		{
			npcListIndex = 0;
		}
		
		return npcLists[npcListIndex++].clear();
	}
	
	/**
	 * Method getNextObjectList.
	 * @return Array<TObject>
	 */
	public Array<TObject> getNextObjectList()
	{
		if (objectListIndex == DEFAULT_BUFFER_SIZE)
		{
			objectListIndex = 0;
		}
		
		return objectLists[objectListIndex++].clear();
	}
	
	/**
	 * Method getNextPlayerList.
	 * @return Array<Player>
	 */
	public Array<Player> getNextPlayerList()
	{
		if (playerListIndex == DEFAULT_BUFFER_SIZE)
		{
			playerListIndex = 0;
		}
		
		return playerLists[playerListIndex++].clear();
	}
	
	/**
	 * Method getNextQuestEvent.
	 * @return QuestEvent
	 */
	public QuestEvent getNextQuestEvent()
	{
		if (questEventIndex == DEFAULT_BUFFER_SIZE)
		{
			questEventIndex = 0;
		}
		
		return questEvents[questEventIndex++].clear();
	}
}