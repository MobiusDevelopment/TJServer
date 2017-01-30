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
package tera.remotecontrol.handlers;

import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.StatType;
import tera.remotecontrol.Packet;
import tera.remotecontrol.PacketHandler;
import tera.remotecontrol.PacketType;

/**
 * @author Ronn
 */
public class UpdatePlayerStatInfoHandler implements PacketHandler
{
	public static final UpdatePlayerStatInfoHandler instance = new UpdatePlayerStatInfoHandler();
	
	/**
	 * Method processing.
	 * @param packet Packet
	 * @return Packet
	 * @see tera.remotecontrol.PacketHandler#processing(Packet)
	 */
	@Override
	public Packet processing(Packet packet)
	{
		final Player player = World.getPlayer(packet.nextString());
		
		if (player == null)
		{
			return null;
		}
		
		final int attack = player.getAttack(null, null);
		final int defense = player.getDefense(null, null);
		final int impact = player.getImpact(null, null);
		final int balance = player.getBalance(null, null);
		final int critRcpt = (int) player.getCritRateRcpt(null, null);
		final int critRate = (int) player.getCritRate(null, null);
		final int powerFactor = player.getPowerFactor();
		final int defenseFactor = player.getDefenseFactor();
		final int impactFactor = player.getImpactFactor();
		final int balanceFactor = player.getBalanceFactor();
		final int attackSpeed = player.getAtkSpd();
		final int moveSpeed = player.getRunSpeed();
		final int weakRcpt = (int) player.calcStat(StatType.WEAK_RECEPTIVE, 0, null, null);
		final int damageRcpt = (int) player.calcStat(StatType.DAMAGE_RECEPTIVE, 0, null, null);
		final int stunRcpt = (int) player.calcStat(StatType.STUN_RECEPTIVE, 0, null, null);
		final float critDmg = player.getCritDamage(null, null);
		return new Packet(PacketType.RESPONSE, attack, defense, impact, balance, critRcpt, critRate, critDmg, powerFactor, defenseFactor, impactFactor, balanceFactor, attackSpeed, moveSpeed, weakRcpt, damageRcpt, stunRcpt);
	}
}
