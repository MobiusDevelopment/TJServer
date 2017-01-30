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

import tera.gameserver.model.actions.dialogs.EnchantItemDialog;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class EnchatItemInfo extends ServerPacket
{
	private static final ServerPacket instance = new EnchatItemInfo();
	
	/**
	 * Method getInstance.
	 * @param dialog EnchantItemDialog
	 * @return EnchatItemInfo
	 */
	public static EnchatItemInfo getInstance(EnchantItemDialog dialog)
	{
		final EnchatItemInfo packet = (EnchatItemInfo) instance.newInstance();
		final ByteBuffer buffer = packet.prepare;
		int n = 8;
		packet.writeShort(buffer, 3);
		packet.writeShort(buffer, n);
		
		for (int i = 0, length = EnchantItemDialog.ITEM_COUNTER; i <= length; i++)
		{
			packet.writeShort(buffer, n);
			
			if (i != length)
			{
				packet.writeShort(buffer, n += 126);
			}
			else
			{
				packet.writeShort(buffer, 0);
			}
			
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, i);
			packet.writeInt(buffer, dialog.getItemId(i));
			packet.writeLong(buffer, dialog.getObjectId(i));
			packet.writeLong(buffer, 14408);
			packet.writeLong(buffer, 76);
			packet.writeInt(buffer, dialog.getNeedItemCount(i));
			packet.writeInt(buffer, dialog.getNeedItemCount(i));
			packet.writeInt(buffer, dialog.getEnchantLevel(i));
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, dialog.isEnchantItem(i) ? 1 : 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeShort(buffer, 0);
		}
		
		buffer.flip();
		return packet;
	}
	
	private final ByteBuffer prepare;
	
	public EnchatItemInfo()
	{
		prepare = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
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
		return ServerPacketType.ENCHANT_ITEM_DIALOG_INFO;
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
	 * Method getPrepare.
	 * @return ByteBuffer
	 */
	private ByteBuffer getPrepare()
	{
		return prepare;
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
}