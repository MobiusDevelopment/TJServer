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
package tera.gameserver.manager;

import tera.Config;
import tera.gameserver.IdFactory;
import tera.gameserver.model.Account;
import tera.gameserver.model.Guild;
import tera.gameserver.model.World;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.PlayerEquipment;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.inventory.PlayerInventory;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.playable.PlayerAppearance;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.CharDead;
import tera.gameserver.network.serverpackets.CreatePlayerResult;
import tera.gameserver.network.serverpackets.FFStructure;
import tera.gameserver.network.serverpackets.FriendListInfo;
import tera.gameserver.network.serverpackets.HotKey;
import tera.gameserver.network.serverpackets.InventoryInfo;
import tera.gameserver.network.serverpackets.NameColor;
import tera.gameserver.network.serverpackets.PlayerDeadWindow;
import tera.gameserver.network.serverpackets.PlayerDeleteResult;
import tera.gameserver.network.serverpackets.PlayerEntered;
import tera.gameserver.network.serverpackets.PlayerSelected;
import tera.gameserver.network.serverpackets.Settings;
import tera.gameserver.network.serverpackets.SkillListInfo;
import tera.gameserver.network.serverpackets.SpawnChar;
import tera.gameserver.network.serverpackets.Structure;
import tera.gameserver.network.serverpackets.Test2;
import tera.gameserver.network.serverpackets.Test23;
import tera.gameserver.network.serverpackets.Test26;
import tera.gameserver.network.serverpackets.Test4;
import tera.gameserver.network.serverpackets.Test5;
import tera.gameserver.network.serverpackets.Test6;
import tera.gameserver.network.serverpackets.Test7;
import tera.gameserver.network.serverpackets.UserInfo;
import tera.gameserver.network.serverpackets.WorldZone;
import tera.gameserver.tables.PlayerTable;
import tera.gameserver.taskmanager.RegenTaskManager;
import tera.gameserver.templates.PlayerTemplate;
import tera.util.Location;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class PlayerManager
{
	private static final Logger log = Loggers.getLogger(PlayerManager.class);
	private static final Location BASE_POSITION = new Location(93545, -88207, -4524, 0, 0);
	private static final int BASE_WORLD_ID = 13;
	
	public static Location getBasePosition()
	{
		return BASE_POSITION;
	}
	
	private static PlayerManager instance;
	
	public static PlayerManager getInstance()
	{
		if (instance == null)
		{
			instance = new PlayerManager();
		}
		
		return instance;
	}
	
	private final Array<String> playerNames;
	
	private PlayerManager()
	{
		playerNames = Arrays.toArray(String.class);
		log.info("initialized.");
	}
	
	public synchronized void createPlayer(UserClient client, PlayerAppearance appearance, String name, PlayerClass playerClass, Race race, Sex sex)
	{
		final Account account = client.getAccount();
		
		if (account == null)
		{
			log.warning("not found account.");
			client.sendPacket(CreatePlayerResult.getInstance(), true);
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		if (!dbManager.isFreeName(name))
		{
			return;
		}
		
		final IdFactory idFactory = IdFactory.getInstance();
		int objectId = idFactory.getNextPlayerId();
		
		for (int i = 0; (i < 10) && !dbManager.isFreePlayerId(objectId); i++)
		{
			objectId = idFactory.getNextPlayerId();
		}
		
		if (!dbManager.isFreePlayerId(objectId))
		{
			log.warning("incorrect player id.");
			client.sendPacket(CreatePlayerResult.getInstance(), true);
			return;
		}
		
		appearance.setObjectId(objectId);
		
		final PlayerTable playerTable = PlayerTable.getInstance();
		final PlayerTemplate template = playerTable.getTemplate(playerClass, race, sex);
		
		if (template == null)
		{
			log.warning("not found template.");
			client.sendPacket(CreatePlayerResult.getInstance(), true);
			return;
		}
		
		final Player player = new Player(objectId, template);
		player.setHeading(31912);
		player.setOnlineTime(0);
		player.setCreateTime(System.currentTimeMillis());
		player.setTitle(Strings.EMPTY);
		player.setGuildId(0);
		player.setAccessLevel(0);
		player.setLevel(1);
		player.setExp(0);
		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		player.setX(93545);
		player.setY(-88207);
		player.setZ(-4524);
		player.setStamina(120);
		player.setName(name);
		player.setPlantLevel(1);
		player.setEnergyLevel(1);
		player.setMiningLevel(1);
		player.setZoneId(13);
		player.setAppearance(appearance, false);
		
		if (!dbManager.createPlayer(player, account.getName()))
		{
			log.warning("not ctreated player.");
			client.sendPacket(CreatePlayerResult.getInstance(), true);
			return;
		}
		
		dbManager.insertPlayerAppearance(appearance);
		
		final Inventory inventory = PlayerInventory.newInstance(player, 1);
		final Equipment equipment = PlayerEquipment.newInstance(player);
		player.setInventory(inventory);
		player.setEquipment(equipment);
		template.giveItems(inventory);
		final Cell[] cells = inventory.getCells();
		
		for (Cell cell : cells)
		{
			if (!cell.isEmpty())
			{
				equipment.dressItem(inventory, cell);
			}
		}
		
		template.giveSkills(player);
		client.sendPacket(CreatePlayerResult.getInstance(), true);
		dbManager.updatePlayerZoneId(player);
		player.deleteMe();
	}
	
	public void enterInWorld(UserClient client)
	{
		if (client == null)
		{
			log.warning("not found client.");
			return;
		}
		
		final Player player = client.getOwner();
		
		if (player == null)
		{
			log.warning("not found player.");
			return;
		}
		
		player.sendPacket(SpawnChar.getInstance(player), true);
		player.spawnMe();
		player.sendPacket(UserInfo.getInstance(player), true);
		player.sendReuseSkills();
		player.sendReuseItems();
		player.sendEffects();
		
		if (player.isDead())
		{
			player.broadcastPacket(CharDead.getInstance(player, true));
			player.sendPacket(PlayerDeadWindow.getInstance(), true);
		}
		
		if (player.isPvPMode())
		{
			player.sendPacket(NameColor.getInstance(NameColor.COLOR_RED, player), true);
		}
	}
	
	public synchronized void removePlayer(UserClient client, int objectId)
	{
		if (client == null)
		{
			log.warning(this, "not found client.");
			return;
		}
		
		final Account account = client.getAccount();
		
		if (account == null)
		{
			log.warning(this, "not found account.");
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final int result = dbManager.deletePlayer(objectId, account.getName());
		client.sendPacket(PlayerDeleteResult.getInstance(result), true);
	}
	
	public void selectPlayer(UserClient client, int objectId)
	{
		if (World.online() > Config.WORLD_MAXIMUM_ONLINE)
		{
			return;
		}
		
		if (client == null)
		{
			log.warning("not found client.");
			return;
		}
		
		final Account account = client.getAccount();
		
		if (account == null)
		{
			log.warning(new Exception("not found account."));
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		final Player player = dbManager.fullRestore(objectId, account);
		
		if (player == null)
		{
			log.warning("incorrect restore player " + objectId);
			return;
		}
		
		if (player.getAccessLevel() < Config.WORLD_MIN_ACCESS_LEVEL)
		{
			return;
		}
		
		final Player old = World.getPlayer(player.getName());
		
		if (old != null)
		{
			final UserClient con = old.getClient();
			
			if (con != null)
			{
				con.close();
			}
		}
		
		client.setOwner(player);
		player.setClient(client);
		final RegenTaskManager regenManager = RegenTaskManager.getInstance();
		regenManager.addCharacter(player);
		World.addNewPlayer(player);
		final Guild guild = player.getGuild();
		
		if (guild != null)
		{
			guild.enterInGame(player);
		}
		
		player.sendPacket(PlayerSelected.getInstance(), true);
		player.sendPacket(PlayerEntered.getInstance(player), true);
		player.sendPacket(InventoryInfo.getInstance(player), true);
		player.sendPacket(SkillListInfo.getInstance(player), true);
		player.sendPacket(Test2.getInstance(), false);
		
		QuestList questList = player.getQuestList();
		
		if (questList != null)
		{
			questList.updateQuestList();
		}
		
		player.sendPacket(Structure.getInstance(), false);
		player.sendPacket(FFStructure.getInstance(), false);
		player.sendPacket(Test4.getInstance(), false);
		player.sendPacket(Test5.getInstance(), false);
		player.sendPacket(Test6.getInstance(), false);
		player.sendPacket(Test7.getInstance(), false);
		
		int zoneId = player.getZoneId();
		
		if (zoneId < 1)
		{
			player.setZoneId(player.getContinentId() + 1);
		}
		
		player.sendPacket(WorldZone.getInstance(player), true);
		player.sendPacket(Test23.getInstance(), false);
		player.sendPacket(Test26.getInstance(), false);
		
		player.sendPacket(FriendListInfo.getInstance(player), true);
		
		if (player.hasSettings())
		{
			player.sendPacket(Settings.getInstance(player), true);
		}
		
		if (player.hasHotKey())
		{
			player.sendPacket(HotKey.getInstance(player), true);
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyPlayerSelect(player);
	}
	
	public synchronized void restoreCharacters(Player player)
	{
		final Account account = player.getAccount();
		
		if (account == null)
		{
			return;
		}
		
		final Array<String> playerNames = getPlayerNames();
		playerNames.clear();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.restorePlayerNames(playerNames, account.getName());
		final String playerName = player.getName();
		final String[] array = playerNames.array();
		
		for (int i = 0, length = playerNames.size(); i < length; i++)
		{
			final String name = array[i];
			
			if (name.equals(playerName))
			{
				continue;
			}
			
			dbManager.updatePlayerLocation(BASE_POSITION, name, BASE_WORLD_ID);
			player.sendMessage("Character \"" + name + "\" has been moved to the starting position.");
		}
	}
	
	public Array<String> getPlayerNames()
	{
		return playerNames;
	}
}