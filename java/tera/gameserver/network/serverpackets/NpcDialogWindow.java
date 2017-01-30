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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;
import tera.gameserver.templates.NpcTemplate;

import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class NpcDialogWindow extends ServerPacket
{
	private static final ServerPacket instance = new NpcDialogWindow();
	
	/**
	 * Method getInstance.
	 * @param npc Npc
	 * @param player Player
	 * @param links Array<Link>
	 * @return NpcDialogWindow
	 */
	public static NpcDialogWindow getInstance(Npc npc, Player player, Array<Link> links)
	{
		final NpcDialogWindow packet = (NpcDialogWindow) instance.newInstance();
		final ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			final NpcTemplate template = npc == null ? null : npc.getTemplate();
			int startLink = 64;
			int startNameLink = 0;
			packet.writeShort(buffer, 2);
			packet.writeShort(buffer, startLink);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, npc == null ? 0 : npc.getObjectId());
			packet.writeInt(buffer, npc == null ? 0 : npc.getSubId());
			packet.writeInt(buffer, 1);
			packet.writeInt(buffer, (npc == null) || (template == null) ? 0 : template.getIconId());
			packet.writeInt(buffer, npc == null ? 0 : npc.getTemplateType());
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 1);
			packet.writeInt(buffer, 0xBCB8A17B);
			packet.writeInt(buffer, 0);
			packet.writeByte(buffer, 0);
			packet.writeInt(buffer, 1);
			packet.writeInt(buffer, 0);
			packet.writeShort(buffer, 0);
			packet.writeByte(buffer, 0);
			packet.writeInt(buffer, 0xFFFFFFFF);
			
			if ((links != null) && !links.isEmpty())
			{
				final Link last = links.last();
				final Link[] array = links.array();
				
				for (int i = 0, length = links.size(); i < length; i++)
				{
					final Link link = array[i];
					player.addLink(link);
					packet.writeShort(buffer, startLink);
					startNameLink = startLink + 14;
					final String name = link.getName();
					startLink += ((name.length() * 2) + 17);
					
					if (link == last)
					{
						packet.writeShort(buffer, 0);
					}
					else
					{
						packet.writeShort(buffer, startLink);
					}
					
					packet.writeShort(buffer, startNameLink);
					packet.writeInt(buffer, i + 1);
					packet.writeShort(buffer, link.getIconId());
					packet.writeByte(buffer, 0);
					packet.writeByte(buffer, 0);
					packet.writeString(buffer, name);
					packet.writeByte(buffer, 0);
				}
			}
			
			return packet;
		}
		
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public NpcDialogWindow()
	{
		prepare = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.NPC_DIALOG_WINDOW;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.SendablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	/**
	 * Method writeImpl.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		final ByteBuffer prepare = getPrepare();
		buffer.put(prepare.array(), 0, prepare.limit());
	}
	
	/**
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
}