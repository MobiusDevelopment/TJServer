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
package tera.gameserver;

import java.util.concurrent.ScheduledExecutorService;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ExecutorManager;

import rlib.database.ConnectFactory;
import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;
import rlib.logging.Logger;
import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public final class IdFactory
{
	private static final Logger log = Loggers.getLogger(IdFactory.class);
	
	private static final String[][] itemTable =
	{
		{
			"items",
			"object_id"
		},
	};
	
	private static final String[][] playerTable =
	{
		{
			"characters",
			"object_id"
		},
	};
	
	private static final String[][] clanTable =
	{
		{
			"guilds",
			"id"
		},
	};
	
	private static IdFactory instance;
	
	/**
	 * Method getInstance.
	 * @return IdFactory
	 */
	public static IdFactory getInstance()
	{
		if (instance == null)
		{
			instance = new IdFactory();
		}
		
		return instance;
	}
	
	private final IdGenerator npcIds;
	private final IdGenerator playerIds;
	private final IdGenerator itemIds;
	private final IdGenerator objectIds;
	private final IdGenerator guildIds;
	
	private final IdGenerator actionIds;
	private final IdGenerator shotIds;
	private final IdGenerator questIds;
	private final IdGenerator trapIds;
	private final IdGenerator resourseIds;
	
	public IdFactory()
	{
		final DataBaseManager manager = DataBaseManager.getInstance();
		final ConnectFactory connectFactory = manager.getConnectFactory();
		final ExecutorManager executorManager = ExecutorManager.getInstance();
		final ScheduledExecutorService executor = executorManager.getIdFactoryExecutor();
		log.info("prepare npc ids...");
		npcIds = IdGenerators.newBitSetIdGeneratoe(connectFactory, executor, null);
		npcIds.prepare();
		log.info("prepare objects ids...");
		objectIds = IdGenerators.newBitSetIdGeneratoe(connectFactory, executor, null);
		objectIds.prepare();
		log.info("prepare players ids...");
		playerIds = IdGenerators.newBitSetIdGeneratoe(connectFactory, executor, playerTable);
		playerIds.prepare();
		log.info("prepare guilds ids...");
		guildIds = IdGenerators.newBitSetIdGeneratoe(connectFactory, executor, clanTable);
		guildIds.prepare();
		log.info("prepare item ids...");
		itemIds = IdGenerators.newBitSetIdGeneratoe(connectFactory, executor, itemTable);
		itemIds.prepare();
		actionIds = IdGenerators.newSimpleIdGenerator(1, Integer.MAX_VALUE);
		shotIds = IdGenerators.newSimpleIdGenerator(1, Integer.MAX_VALUE);
		questIds = IdGenerators.newSimpleIdGenerator(1, Integer.MAX_VALUE);
		trapIds = IdGenerators.newSimpleIdGenerator(1, Integer.MAX_VALUE);
		resourseIds = IdGenerators.newSimpleIdGenerator(1, Integer.MAX_VALUE);
	}
	
	/**
	 * Method getNextActionId.
	 * @return int
	 */
	public final int getNextActionId()
	{
		return actionIds.getNextId();
	}
	
	/**
	 * Method getNextGuildId.
	 * @return int
	 */
	public final int getNextGuildId()
	{
		return guildIds.getNextId();
	}
	
	/**
	 * Method getNextItemId.
	 * @return int
	 */
	public final int getNextItemId()
	{
		return itemIds.getNextId();
	}
	
	/**
	 * Method getNextNpcId.
	 * @return int
	 */
	public final int getNextNpcId()
	{
		return npcIds.getNextId();
	}
	
	/**
	 * Method getNextObjectId.
	 * @return int
	 */
	public final int getNextObjectId()
	{
		return objectIds.getNextId();
	}
	
	/**
	 * Method getNextPlayerId.
	 * @return int
	 */
	public final int getNextPlayerId()
	{
		return playerIds.getNextId();
	}
	
	/**
	 * Method getNextQuestId.
	 * @return int
	 */
	public final int getNextQuestId()
	{
		return questIds.getNextId();
	}
	
	/**
	 * Method getNextResourseId.
	 * @return int
	 */
	public final int getNextResourseId()
	{
		return resourseIds.getNextId();
	}
	
	/**
	 * Method getNextShotId.
	 * @return int
	 */
	public final int getNextShotId()
	{
		return shotIds.getNextId();
	}
	
	/**
	 * Method getNextTrapId.
	 * @return int
	 */
	public final int getNextTrapId()
	{
		return trapIds.getNextId();
	}
}
