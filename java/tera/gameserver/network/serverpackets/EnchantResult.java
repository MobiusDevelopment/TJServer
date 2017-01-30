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

import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class EnchantResult extends ServerConstPacket
{
	private static final EnchantResult SUCCESSFUL = new EnchantResult(1);
	private static final EnchantResult FAIL = new EnchantResult(0);
	
	/**
	 * Method getSuccessful.
	 * @return EnchantResult
	 */
	public static final EnchantResult getSuccessful()
	{
		return SUCCESSFUL;
	}
	
	/**
	 * Method getFail.
	 * @return EnchantResult
	 */
	public static final EnchantResult getFail()
	{
		return FAIL;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.ENCHANT_RESULT;
	}
	
	private final int result;
	
	/**
	 * Constructor for EnchantResult.
	 * @param result int
	 */
	public EnchantResult(int result)
	{
		this.result = result;
	}
	
	public EnchantResult()
	{
		result = 0;
	}
	
	/**
	 * Method writeImpl.
	 * @param buffer ByteBuffer
	 */
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, result);
	}
}
