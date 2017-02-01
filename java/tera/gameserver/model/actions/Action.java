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
package tera.gameserver.model.actions;

import tera.gameserver.model.playable.Player;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 26.04.2012
 */
public interface Action extends Foldable
{
	/**
	 * Method assent.
	 * @param player Player
	 */
	void assent(Player player);
	
	/**
	 * Method cancel.
	 * @param player Player
	 */
	void cancel(Player player);
	
	/**
	 * Method getActor.
	 * @return Player
	 */
	Player getActor();
	
	/**
	 * Method getId.
	 * @return int
	 */
	int getId();
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	int getObjectId();
	
	/**
	 * Method getTarget.
	 * @return Object
	 */
	Object getTarget();
	
	/**
	 * Method getType.
	 * @return ActionType
	 */
	ActionType getType();
	
	/**
	 * Method init.
	 * @param actor Player
	 * @param name String
	 */
	void init(Player actor, String name);
	
	void invite();
	
	/**
	 * Method setActor.
	 * @param actor Player
	 */
	void setActor(Player actor);
	
	/**
	 * Method setTarget.
	 * @param target Object
	 */
	void setTarget(Object target);
	
	/**
	 * Method test.
	 * @return boolean
	 */
	boolean test();
}
