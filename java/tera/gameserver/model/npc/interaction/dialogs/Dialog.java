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
package tera.gameserver.model.npc.interaction.dialogs;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;

import rlib.util.pools.Foldable;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public interface Dialog extends Foldable
{
	/**
	 * Method apply.
	 * @return boolean
	 */
	boolean apply();
	
	/**
	 * Method close.
	 * @return boolean
	 */
	boolean close();
	
	/**
	 * Method getNpc.
	 * @return Npc
	 */
	Npc getNpc();
	
	/**
	 * Method getPlayer.
	 * @return Player
	 */
	Player getPlayer();
	
	/**
	 * Method getType.
	 * @return DialogType
	 */
	DialogType getType();
	
	/**
	 * Method init.
	 * @return boolean
	 */
	boolean init();
}
