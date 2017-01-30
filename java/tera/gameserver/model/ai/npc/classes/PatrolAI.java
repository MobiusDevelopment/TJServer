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
package tera.gameserver.model.ai.npc.classes;

import tera.gameserver.model.ai.npc.ConfigAI;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @param <T>
 */
public class PatrolAI<T extends Npc> extends AbstractNpcAI<T>
{
	protected final Array<Player> dialogs;
	
	/**
	 * Constructor for PatrolAI.
	 * @param actor T
	 * @param config ConfigAI
	 */
	public PatrolAI(T actor, ConfigAI config)
	{
		super(actor, config);
		this.dialogs = Arrays.toConcurrentArray(Player.class);
	}
	
	/**
	 * Method notifyStartDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStartDialog(Player)
	 */
	@Override
	public void notifyStartDialog(Player player)
	{
		super.notifyStartDialog(player);
	}
	
	/**
	 * Method notifyStopDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStopDialog(Player)
	 */
	@Override
	public void notifyStopDialog(Player player)
	{
		super.notifyStopDialog(player);
	}
}