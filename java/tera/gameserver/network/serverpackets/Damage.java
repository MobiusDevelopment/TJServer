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

import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.ServerPacketType;

/**
 * @author Ronn
 */
public class Damage extends ServerPacket
{
	public static final int BLOCK = 0;
	public static final int DAMAGE = 1;
	public static final int HEAL = 2;
	public static final int MANAHEAL = 3;
	public static final int EFFECT = 4;
	private static final ServerPacket instance = new Damage();
	
	/**
	 * Method getInstance.
	 * @param attacker Character
	 * @param attacked Character
	 * @param info AttackInfo
	 * @param skill Skill
	 * @param type int
	 * @return Damage
	 */
	public static Damage getInstance(Character attacker, Character attacked, AttackInfo info, Skill skill, int type)
	{
		final Damage packet = (Damage) instance.newInstance();
		
		if ((attacker == null) || (attacked == null))
		{
			log.warning(packet, new Exception("not found attacker or attacked"));
		}
		
		packet.attacker = attacker;
		packet.attacked = attacked;
		packet.damage = info.getDamage();
		packet.crit = info.isCrit();
		packet.owerturned = info.isOwerturn();
		packet.type = type;
		packet.damageId = skill.getDamageId();
		return packet;
	}
	
	/**
	 * Method getInstance.
	 * @param attacker Character
	 * @param attacked Character
	 * @param damageId int
	 * @param damage int
	 * @param crit boolean
	 * @param owerturned boolean
	 * @param type int
	 * @return Damage
	 */
	public static Damage getInstance(Character attacker, Character attacked, int damageId, int damage, boolean crit, boolean owerturned, int type)
	{
		final Damage packet = (Damage) instance.newInstance();
		
		if ((attacker == null) || (attacked == null))
		{
			log.warning(packet, new Exception("not found attacker or attacked"));
		}
		
		packet.attacker = attacker;
		packet.attacked = attacked;
		packet.damageId = damageId;
		packet.damage = damage;
		packet.crit = crit;
		packet.owerturned = owerturned;
		packet.type = type;
		return packet;
	}
	
	private Character attacker;
	private Character attacked;
	private int damageId;
	private int damage;
	private int type;
	private boolean crit;
	private boolean owerturned;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		attacker = null;
		attacked = null;
	}
	
	/**
	 * Method getPacketType.
	 * @return ServerPacketType
	 */
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.DAMAGE;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(owerturned ? 0x00540003 : 0);
		writeInt(attacker.getObjectId());
		writeInt(attacker.getSubId());
		writeInt(attacked.getObjectId());
		writeInt(attacked.getSubId());
		writeInt(attacker.getModelId());
		writeInt(damageId);
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(damage);
		writeShort(type);
		writeByte(crit ? 1 : 0);
		writeByte(0);
		writeByte(owerturned ? 1 : 0);
		writeByte(owerturned ? 1 : 0);
		
		if (owerturned)
		{
			writeFloat(attacked.getX());
			writeFloat(attacked.getY());
			writeFloat(attacked.getZ());
			writeShort(attacked.getHeading());
			writeInt(attacked.getOwerturnId());
			writeInt(0);
			writeInt(0);
		}
		else
		{
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeShort(0);
		}
	}
}