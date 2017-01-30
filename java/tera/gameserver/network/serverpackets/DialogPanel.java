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
package tera.gameserver.network.serverpackets;

import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class DialogPanel extends ServerPacket
{
	/**
	 */
	public static enum PanelType
	{
		SKILL_LEARN(27),
		ENCHANT_ITEM(34);
		private final int id;
		
		/**
		 * Constructor for PanelType.
		 * @param id int
		 */
		private PanelType(int id)
		{
			this.id = id;
		}
		
		/**
		 * Method getId.
		 * @return int
		 */
		public int getId()
		{
			return id;
		}
	}
	
	private static final ServerPacket instance = new DialogPanel();
	
	/**
	 * Method getInstance.
	 * @param player Player
	 * @param type PanelType
	 * @return DialogPanel
	 */
	public static DialogPanel getInstance(Player player, PanelType type)
	{
		final DialogPanel packet = (DialogPanel) instance.newInstance();
		packet.player = player;
		packet.type = type;
		return packet;
	}
	
	private Player player;
	private PanelType type;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_DIALOG_SKILL_LEARN_PANEL;
	}
	
	@Override
	protected void writeImpl()
	{
		writeOpcode();
		writeShort(44);
		writeShort(46 + (player.getName().length() * 2));
		writeShort(48 + (player.getName().length() * 2));
		writeShort(0);
		writeInt(player.getObjectId());
		writeInt(player.getSubId());
		writeLong(0);
		writeInt(type.getId());
		writeInt(0x000E10EA);
		writeInt(0);
		writeShort(0);
		writeByte(0);
		writeString(player.getName());
		writeShort(0);
	}
}