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
package tera.gameserver.network.clientpackets;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.World;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.Tp1;
import tera.gameserver.network.serverpackets.WorldZone;
import tera.gameserver.tables.WorldZoneTable;
import tera.util.Location;

public class RequestPlayerUnstuck extends ClientPacket
{
	private Player player;
	
	@Override
	public void finalyze()
	{
		player = null;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public void readImpl()
	{
		player = owner.getOwner();
		readInt();
		readInt();
	}
	
	@Override
	public void runImpl()
	{
		if ((player == null) || !player.isResurrected())
		{
			return;
		}
		
		Summon summon = player.getSummon();
		
		if (summon != null)
		{
			summon.remove();
		}
		
		player.decayMe(DeleteCharacter.DISAPPEARS);
		WorldZoneTable zoneTable = WorldZoneTable.getInstance();
		Location point = zoneTable.getRespawn(player);
		
		if (point == null)
		{
			log.warning(this, "not found respawn for " + player.getLoc());
			return;
		}
		
		player.setLoc(point);
		
		int zoneId = World.getRegion(player).getZoneId(player);
		
		if (zoneId < 1)
		{
			zoneId = player.getContinentId() + 1;
		}
		
		player.setZoneId(zoneId);
		ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyChangedZoneId(player);
		
		if (!player.isInBattleTerritory())
		{
			player.setStamina(120);
		}
		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		
		player.sendPacket(Tp1.getInstance(player), true);
		player.sendPacket(WorldZone.getInstance(player), true);
	}
}