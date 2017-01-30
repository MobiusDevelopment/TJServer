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
package tera.gameserver.model;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.FriendListInfo;
import tera.gameserver.network.serverpackets.FriendListState;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class FriendList implements Foldable
{
	private static final FoldablePool<FriendList> pool = Pools.newConcurrentFoldablePool(FriendList.class);
	public static final int FRIEND_LIST_LIMIT = 10;
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @return FriendList
	 */
	public static FriendList getInstance(Player player)
	{
		FriendList list = pool.take();
		
		if (list == null)
		{
			list = new FriendList();
		}
		
		list.setOwner(player);
		return list;
	}
	
	private final FoldablePool<FriendInfo> infoPool;
	private final Array<FriendInfo> friends;
	private final Array<Player> players;
	private Player owner;
	
	private FriendList()
	{
		infoPool = Pools.newFoldablePool(FriendInfo.class);
		friends = Arrays.toArray(FriendInfo.class);
		players = Arrays.toArray(Player.class);
	}
	
	/**
	 * Method addFriend.
	 * @param info FriendInfo
	 */
	public void addFriend(FriendInfo info)
	{
		friends.add(info);
	}
	
	/**
	 * Method addFriend.
	 * @param player Player
	 * @return boolean
	 */
	public synchronized boolean addFriend(Player player)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			Loggers.warning(this, "not found owner");
			return false;
		}
		
		if (size() >= FRIEND_LIST_LIMIT)
		{
			player.sendMessage(MessageType.CANT_ADD_FRIEND_EITHER_YOUR_FRIENDS_LIST_OR_THEIRS_IS_FULL);
			return false;
		}
		
		final String name = player.getName();
		final FriendInfo[] friends = getFriends();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			if (name.equalsIgnoreCase(friends[i].getName()))
			{
				return true;
			}
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final FriendInfo info = newFriendInfo();
		info.setObjectId(player.getObjectId());
		info.setClassId(player.getClassId());
		info.setLevel(player.getLevel());
		info.setName(player.getName());
		info.setRaceId(player.getRaceId());
		players.add(player);
		addFriend(info);
		dbManager.insertFriend(owner.getObjectId(), player.getObjectId());
		owner.sendPacket(FriendListInfo.getInstance(owner), true);
		owner.sendPacket(FriendListState.getInstance(owner), true);
		return true;
	}
	
	/**
	 * Method addFriend.
	 * @param name String
	 */
	public synchronized void addFriend(String name)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			Loggers.warning(this, "not found owner");
			return;
		}
		
		if (size() >= FRIEND_LIST_LIMIT)
		{
			owner.sendMessage(MessageType.CANT_ADD_FRIEND_EITHER_YOUR_FRIENDS_LIST_OR_THEIRS_IS_FULL);
			return;
		}
		
		final FriendInfo[] friends = getFriends();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			if (name.equalsIgnoreCase(friends[i].getName()))
			{
				owner.sendMessage(MessageType.THAT_PLAYER_IS_ALREADY_ON_YOUR_FRIENDS_LIST);
				return;
			}
		}
		
		final Player target = World.getPlayer(name);
		
		if (target == null)
		{
			owner.sendMessage(MessageType.PLAYER_MUST_BE_ONLINE_TO_BE_ADDED_TO_YOUR_LIST);
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final FriendList targetList = target.getFriendList();
		
		if (!targetList.addFriend(owner))
		{
			return;
		}
		
		final FriendInfo info = newFriendInfo();
		info.setObjectId(target.getObjectId());
		info.setClassId(target.getClassId());
		info.setLevel(target.getLevel());
		info.setName(target.getName());
		info.setRaceId(target.getRaceId());
		players.add(target);
		addFriend(info);
		dbManager.insertFriend(owner.getObjectId(), target.getObjectId());
		owner.sendPacket(FriendListInfo.getInstance(owner), true);
		owner.sendPacket(FriendListState.getInstance(owner), true);
		PacketManager.addToFriend(owner, target);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		final FriendInfo[] array = friends.array();
		
		for (int i = 0, length = friends.size(); i < length; i++)
		{
			infoPool.put(array[i]);
		}
		
		friends.clear();
		players.clear();
		owner = null;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getFriends.
	 * @return FriendInfo[]
	 */
	public FriendInfo[] getFriends()
	{
		return friends.array();
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
	 * Method getPlayers.
	 * @return Player[]
	 */
	public Player[] getPlayers()
	{
		return players.array();
	}
	
	/**
	 * Method newFriendInfo.
	 * @return FriendInfo
	 */
	public FriendInfo newFriendInfo()
	{
		FriendInfo info = infoPool.take();
		
		if (info == null)
		{
			info = new FriendInfo();
		}
		
		return info;
	}
	
	/**
	 * Method onEnterGame.
	 * @param player Player
	 */
	public synchronized void onEnterGame(Player player)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			Loggers.warning(this, "not found owner");
			return;
		}
		
		final FriendInfo[] friends = getFriends();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			final FriendInfo info = friends[i];
			
			if (info.getObjectId() == player.getObjectId())
			{
				players.add(player);
				PacketManager.showEnterFriend(owner, player);
				return;
			}
		}
	}
	
	/**
	 * Method onExitGame.
	 * @param player Player
	 */
	public synchronized void onExitGame(Player player)
	{
		final FriendInfo[] friends = getFriends();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			final FriendInfo info = friends[i];
			
			if (info.getObjectId() == player.getObjectId())
			{
				players.fastRemove(i);
				info.setLevel(player.getLevel());
				return;
			}
		}
	}
	
	/**
	 * Method online.
	 * @return int
	 */
	public int online()
	{
		return players.size();
	}
	
	public void prepare()
	{
		final FriendInfo[] friends = getFriends();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			final FriendInfo info = friends[i];
			final Player player = World.getPlayer(info.getObjectId());
			
			if (player != null)
			{
				players.add(player);
			}
		}
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
	 * Method removeFriend.
	 * @param objectId int
	 */
	public synchronized void removeFriend(int objectId)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			Loggers.warning(this, "not found owner");
			return;
		}
		
		final FriendInfo[] array = getFriends();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			final FriendInfo info = array[i];
			
			if (info.getObjectId() != objectId)
			{
				continue;
			}
			
			dbManager.removeFriend(owner.getObjectId(), objectId);
			friends.fastRemove(i);
			owner.sendPacket(FriendListInfo.getInstance(owner), true);
			owner.sendPacket(FriendListState.getInstance(owner), true);
			final Player target = World.getPlayer(info.getName());
			
			if (target == null)
			{
				dbManager.removeFriend(objectId, owner.getObjectId());
			}
			else
			{
				final FriendList targetList = target.getFriendList();
				targetList.removeFriend(owner);
			}
			
			PacketManager.removeToFriend(owner, info.getName(), target);
			infoPool.put(info);
			return;
		}
		
		owner.sendMessage(MessageType.THAT_PLAYER_IS_NOT_ON_YOUR_FRIENDS_LIST);
	}
	
	/**
	 * Method removeFriend.
	 * @param player Player
	 */
	public synchronized void removeFriend(Player player)
	{
		final Player owner = getOwner();
		
		if (owner == null)
		{
			Loggers.warning(this, "not found owner");
			return;
		}
		
		final int objectId = player.getObjectId();
		final FriendInfo[] array = getFriends();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		for (int i = 0, length = size(); i < length; i++)
		{
			final FriendInfo info = array[i];
			
			if (info.getObjectId() != objectId)
			{
				continue;
			}
			
			dbManager.removeFriend(owner.getObjectId(), objectId);
			friends.fastRemove(i);
			owner.sendPacket(FriendListInfo.getInstance(owner), true);
			owner.sendPacket(FriendListState.getInstance(owner), true);
			infoPool.put(info);
			return;
		}
	}
	
	/**
	 * Method setOwner.
	 * @param owner Player
	 */
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	public int size()
	{
		return friends.size();
	}
}