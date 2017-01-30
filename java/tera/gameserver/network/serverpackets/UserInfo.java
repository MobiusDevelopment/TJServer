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

import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.network.ServerPacketType;
import tera.gameserver.templates.PlayerTemplate;

/**
 * @author Ronn
 */
public class UserInfo extends ServerPacket
{
	private static final ServerPacket instance = new UserInfo();
	
	public static UserInfo getInstance(Player player)
	{
		UserInfo packet = (UserInfo) instance.newInstance();
		
		ByteBuffer buffer = packet.getPrepare();
		
		try
		{
			PlayerTemplate template = player.getTemplate();
			
			int attack = player.getAttack(null, null);
			int baseAttack = player.getBaseAttack();
			
			int defense = player.getDefense(null, null);
			int baseDefense = player.getBaseDefense();
			
			int impact = player.getImpact(null, null);
			int baseImpact = player.getBaseImpact();
			
			int balance = player.getBalance(null, null);
			int baseBalance = player.getBaseBalance();
			
			float weakResist = player.calcStat(StatType.WEAK_RECEPTIVE, 0, 0x20, null, null);
			float stunResist = player.calcStat(StatType.STUN_RECEPTIVE, 0, 0x20, null, null);
			float dmgResist = player.calcStat(StatType.DAMAGE_RECEPTIVE, 0, 0x20, null, null);
			
			packet.writeInt(buffer, player.getCurrentHp());
			packet.writeInt(buffer, player.getCurrentMp());
			
			packet.writeInt(buffer, 0);
			
			packet.writeInt(buffer, player.getMaxHp());
			packet.writeInt(buffer, player.getMaxMp());
			packet.writeInt(buffer, template.getPowerFactor());
			packet.writeInt(buffer, template.getDefenseFactor());
			packet.writeShort(buffer, template.getImpactFactor());
			packet.writeShort(buffer, template.getBalanceFactor());
			packet.writeShort(buffer, template.getRunSpd());
			packet.writeShort(buffer, template.getAtkSpd());
			
			packet.writeFloat(buffer, template.getCritRate());
			packet.writeFloat(buffer, template.getCritRcpt());
			packet.writeFloat(buffer, 2);
			packet.writeInt(buffer, baseAttack);
			packet.writeInt(buffer, baseAttack);
			packet.writeInt(buffer, baseDefense);
			packet.writeShort(buffer, baseImpact);
			packet.writeShort(buffer, baseBalance);
			packet.writeFloat(buffer, weakResist);
			packet.writeFloat(buffer, dmgResist);
			packet.writeFloat(buffer, stunResist);
			packet.writeInt(buffer, player.getPowerFactor() - template.getPowerFactor());
			packet.writeInt(buffer, player.getDefenseFactor() - template.getDefenseFactor());
			packet.writeShort(buffer, player.getImpactFactor() - template.getImpactFactor());
			packet.writeShort(buffer, player.getBalanceFactor() - template.getBalanceFactor());
			packet.writeShort(buffer, player.getRunSpeed() - template.getRunSpd());
			packet.writeShort(buffer, player.getAtkSpd() - template.getAtkSpd());
			
			packet.writeFloat(buffer, player.getCritRate(null, null) - template.getCritRate());
			packet.writeFloat(buffer, player.getCritRateRcpt(null, null) - template.getCritRcpt());
			packet.writeFloat(buffer, player.getCritDamage(null, null) - 2);
			packet.writeInt(buffer, attack - baseAttack);
			packet.writeInt(buffer, attack - baseAttack);
			packet.writeInt(buffer, defense - baseDefense);
			packet.writeShort(buffer, impact - baseImpact);
			packet.writeShort(buffer, balance - baseBalance);
			
			packet.writeFloat(buffer, player.calcStat(StatType.WEAK_RECEPTIVE, 0, null, null) - weakResist);
			packet.writeFloat(buffer, player.calcStat(StatType.DAMAGE_RECEPTIVE, 0, null, null) - dmgResist);
			packet.writeFloat(buffer, player.calcStat(StatType.STUN_RECEPTIVE, 0, null, null) - stunResist);
			
			packet.writeShort(buffer, player.getLevel());
			packet.writeShort(buffer, player.isBattleStanced() ? 1 : 0);
			packet.writeShort(buffer, 4);
			packet.writeByte(buffer, 1);
			packet.writeInt(buffer, player.getMaxHp() - player.getBaseMaxHp());
			packet.writeInt(buffer, player.getMaxMp() - player.getBaseMaxMp());
			packet.writeInt(buffer, player.getStamina());
			packet.writeInt(buffer, player.getMaxStamina());
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			
			packet.writeInt(buffer, 0);
			packet.writeInt(buffer, 0);
			packet.writeLong(buffer, 0);
			packet.writeInt(buffer, 8000);
			packet.writeInt(buffer, 1);
			
			return packet;
		}
		finally
		{
			buffer.flip();
		}
	}
	
	private final ByteBuffer prepare;
	
	public UserInfo()
	{
		this.prepare = ByteBuffer.allocate(20480).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void finalyze()
	{
		prepare.clear();
	}
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.USER_INFO;
	}
	
	public ByteBuffer getPrepare()
	{
		return prepare;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		
		ByteBuffer prepare = getPrepare();
		
		buffer.put(prepare.array(), 0, prepare.limit());
	}
}