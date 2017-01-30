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
package tera.gameserver.manager;

import tera.Config;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.random.Random;
import rlib.util.random.Randoms;

/**
 * @author Ronn
 */
public final class RandomManager
{
	private static final Logger log = Loggers.getLogger(RandomManager.class);
	private static RandomManager instance;
	
	/**
	 * Method getInstance.
	 * @return RandomManager
	 */
	public static RandomManager getInstance()
	{
		if (instance == null)
		{
			instance = new RandomManager();
		}
		
		return instance;
	}
	
	private final Random dropRandom;
	private final Random critRandom;
	private final Random damageRandom;
	private final Random owerturnRandom;
	private final Random effectRandom;
	private final Random funcRandom;
	private final Random dropItemPointRandom;
	private final Random npcSpawnRandom;
	
	private RandomManager()
	{
		dropRandom = Config.SERVER_DROP_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		critRandom = Config.SERVER_CRIT_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		effectRandom = Config.SERVER_EFFECT_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		funcRandom = Config.SERVER_FUNC_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		damageRandom = Config.SERVER_DAMAGE_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		owerturnRandom = Config.SERVER_OWERTURN_REAL_RANDOM ? Randoms.newRealRandom() : Randoms.newFastRandom();
		dropItemPointRandom = Randoms.newFastRandom();
		npcSpawnRandom = Randoms.newFastRandom();
		log.info("initialized.");
	}
	
	/**
	 * Method getCritRandom.
	 * @return Random
	 */
	public Random getCritRandom()
	{
		return critRandom;
	}
	
	/**
	 * Method getDamageRandom.
	 * @return Random
	 */
	public Random getDamageRandom()
	{
		return damageRandom;
	}
	
	/**
	 * Method getDropItemPointRandom.
	 * @return Random
	 */
	public Random getDropItemPointRandom()
	{
		return dropItemPointRandom;
	}
	
	/**
	 * Method getDropRandom.
	 * @return Random
	 */
	public Random getDropRandom()
	{
		return dropRandom;
	}
	
	/**
	 * Method getEffectRandom.
	 * @return Random
	 */
	public Random getEffectRandom()
	{
		return effectRandom;
	}
	
	/**
	 * Method getFuncRandom.
	 * @return Random
	 */
	public Random getFuncRandom()
	{
		return funcRandom;
	}
	
	/**
	 * Method getOwerturnRandom.
	 * @return Random
	 */
	public Random getOwerturnRandom()
	{
		return owerturnRandom;
	}
	
	/**
	 * Method getNpcSpawnRandom.
	 * @return Random
	 */
	public Random getNpcSpawnRandom()
	{
		return npcSpawnRandom;
	}
}