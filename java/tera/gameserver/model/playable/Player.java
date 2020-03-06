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
package tera.gameserver.model.playable;

import tera.Config;
import tera.gameserver.events.global.regionwars.Region;
import tera.gameserver.events.global.regionwars.RegionState;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.manager.GuildManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.model.Account;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Bonfire;
import tera.gameserver.model.Character;
import tera.gameserver.model.Duel;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.FriendList;
import tera.gameserver.model.Guild;
import tera.gameserver.model.GuildIcon;
import tera.gameserver.model.GuildRank;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.Party;
import tera.gameserver.model.ReuseSkill;
import tera.gameserver.model.Route;
import tera.gameserver.model.SayType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.actions.dialogs.ActionDialog;
import tera.gameserver.model.ai.PlayerAI;
import tera.gameserver.model.base.Experience;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.PlayerGeomTable;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.equipment.Slot;
import tera.gameserver.model.geom.Geom;
import tera.gameserver.model.geom.PlayerGeom;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Cell;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.CrystalList;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.ItemLocation;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.dialogs.Dialog;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestPanelState;
import tera.gameserver.model.quests.QuestState;
import tera.gameserver.model.regenerations.PlayerNegativeRegenMp;
import tera.gameserver.model.regenerations.PlayerPositiveRegenMp;
import tera.gameserver.model.regenerations.PlayerRegenHp;
import tera.gameserver.model.regenerations.Regen;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.EffectType;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.territory.RegionTerritory;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.AddExp;
import tera.gameserver.network.serverpackets.AppledCharmEffect;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelTargetHp;
import tera.gameserver.network.serverpackets.CharDead;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.GuildInfo;
import tera.gameserver.network.serverpackets.GuildLogs;
import tera.gameserver.network.serverpackets.GuildMembers;
import tera.gameserver.network.serverpackets.IncreaseLevel;
import tera.gameserver.network.serverpackets.ItemReuse;
import tera.gameserver.network.serverpackets.MountOff;
import tera.gameserver.network.serverpackets.NameColor;
import tera.gameserver.network.serverpackets.PlayerCurrentHp;
import tera.gameserver.network.serverpackets.PlayerCurrentMp;
import tera.gameserver.network.serverpackets.PlayerDeadWindow;
import tera.gameserver.network.serverpackets.PlayerInfo;
import tera.gameserver.network.serverpackets.PlayerMove;
import tera.gameserver.network.serverpackets.PlayerPvPOff;
import tera.gameserver.network.serverpackets.PlayerPvPOn;
import tera.gameserver.network.serverpackets.QuestMoveToPanel;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.network.serverpackets.SkillListInfo;
import tera.gameserver.network.serverpackets.SkillReuse;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.network.serverpackets.TargetHp;
import tera.gameserver.network.serverpackets.Tp1;
import tera.gameserver.network.serverpackets.UserInfo;
import tera.gameserver.network.serverpackets.WorldZone;
import tera.gameserver.tables.TerritoryTable;
import tera.gameserver.taskmanager.RegenTaskManager;
import tera.gameserver.tasks.BattleStanceTask;
import tera.gameserver.tasks.EmotionTask;
import tera.gameserver.tasks.ResourseCollectTask;
import tera.gameserver.templates.PlayerTemplate;
import tera.gameserver.templates.SkillTemplate;
import tera.util.ExtUtils;
import tera.util.Identified;
import tera.util.LocalObjects;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.idfactory.IdGenerator;
import rlib.idfactory.IdGenerators;
import rlib.util.Nameable;
import rlib.util.Rnd;
import rlib.util.SafeTask;
import rlib.util.Strings;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.FuncKeyValue;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;
import rlib.util.wraps.Wrap;
import rlib.util.wraps.WrapType;
import rlib.util.wraps.Wraps;

/**
 * @author Ronn
 */
public final class Player extends Playable implements Nameable, Identified
{
	private static final IdGenerator ID_FACTORY = IdGenerators.newSimpleIdGenerator(0, 300000);
	
	private static final int MAXIMUM_QUEST_IN_PANEL = 7;
	
	private final Array<Character> lockOnTargets;
	
	private final Array<QuestState> questInPanel;
	
	private final Table<IntKey, Territory> storedTerrs;
	
	private final Table<String, Wrap> variables;
	
	private final ResourseCollectTask collectTask;
	
	private final BattleStanceTask battleStanceTask;
	
	private final FuncKeyValue<String, Wrap> saveVarFunc;
	
	private volatile UserClient client;
	
	private volatile Account account;
	
	private volatile Party party;
	
	private volatile Guild guild;
	
	private volatile GuildRank guildRank;
	
	private volatile Route route;
	
	private volatile Npc lastNpc;
	
	private volatile Dialog lastDialog;
	
	private volatile Action lastAction;
	
	private volatile ActionDialog lastActionDialog;
	
	private volatile Skill mountSkill;
	
	private volatile Duel duel;
	
	private volatile Bonfire bonfire;
	
	private volatile Link lastLink;
	
	private volatile FriendList friendList;
	
	private volatile QuestList questList;
	
	private String guildNote;
	
	private PlayerAppearance appearance;
	
	private final Array<Link> lastLinks;
	
	private byte[] settings;
	
	private byte[] hotkey;
	
	private long createTime;
	
	private long onlineTime;
	
	private long onlineBeginTime;
	
	private long endBan;
	
	private long endChatBan;
	
	private long lastBlock;
	
	private int fraction;
	
	private int accessLevel;
	
	private int stamina;
	
	private int pvpCount;
	
	private int pveCount;
	
	private int attackCounter;
	
	private int karma;
	
	private int mountId;
	
	private int energyLevel;
	
	private int miningLevel;
	
	private int plantLevel;
	
	private boolean connected;
	
	private boolean event;
	
	private boolean attacking;
	
	private boolean pvpMode;
	
	private boolean changedSettings;
	
	private boolean changeHotkey;
	
	private boolean changedFace;
	
	private boolean resurrected;
	
	public Player(int objectId, PlayerTemplate template)
	{
		super(objectId, template);
		saveVarFunc = (key, value) ->
		{
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updatePlayerVar(getObjectId(), key, value.toString());
		};
		battleStanceTask = new BattleStanceTask(this);
		lastLinks = Arrays.toConcurrentArray(Link.class);
		lockOnTargets = Arrays.toConcurrentArray(Character.class);
		questInPanel = Arrays.toConcurrentArray(QuestState.class);
		collectTask = new ResourseCollectTask(this);
		storedTerrs = Tables.newConcurrentIntegerTable();
		variables = Tables.newConcurrentObjectTable();
		final Formulas formulas = Formulas.getInstance();
		formulas.addFuncsToNewPlayer(this);
		resurrected = true;
	}
	
	@Override
	public void abortCollect()
	{
		collectTask.cancel(true);
	}
	
	@Override
	public void addAttackCounter()
	{
		attackCounter++;
		
		if (attackCounter >= Config.WORLD_PLAYER_THRESHOLD_ATTACKS)
		{
			subHeart();
			attackCounter = 0;
		}
	}
	
	public Race getRace()
	{
		return getTemplate().getRace();
	}
	
	public boolean addBonfire(Bonfire newBonfire)
	{
		if (bonfire == null)
		{
			synchronized (this)
			{
				if (bonfire == null)
				{
					bonfire = newBonfire;
					sendMessage(MessageType.YOU_ARE_RECHARGING_STAMINE);
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void addDefenseCounter()
	{
		attackCounter += 3;
		lastBlock = System.currentTimeMillis();
		
		if (attackCounter >= Config.WORLD_PLAYER_THRESHOLD_BLOOKS)
		{
			subHeart();
			attackCounter = 0;
		}
	}
	
	@Override
	public void addExp(int added, TObject object, String creator)
	{
		if (added < 1)
		{
			return;
		}
		
		final GameLogManager gameLogger = GameLogManager.getInstance();
		gameLogger.writeExpLog(getName() + " added " + added + " exp " + (object == null ? " by " + creator : " by object [" + creator + "]"));
		exp += added;
		int next = Experience.getNextExperience(level);
		
		if (exp > next)
		{
			synchronized (this)
			{
				while (exp > next)
				{
					exp -= next;
					increaseLevel();
					next = Experience.LEVEL[level + 1];
				}
			}
		}
		
		sendPacket(AddExp.getInstance(exp, added, next, object != null ? object.getObjectId() : 0, object != null ? object.getSubId() : 0), true);
	}
	
	public void addLink(Link link)
	{
		lastLinks.add(link);
	}
	
	@Override
	public boolean addLockOnTarget(Character target, Skill skill)
	{
		if ((skill.getMaxTargets() <= lockOnTargets.size()) || !skill.getTargetType().check(this, target) || lockOnTargets.contains(target) || !target.isInRange(this, skill.getRange()))
		{
			return false;
		}
		
		lockOnTargets.add(target);
		PacketManager.showLockTarget(this, target, skill);
		return true;
	}
	
	@Override
	public void addMe(Player player)
	{
		try
		{
			player.sendPacket(PlayerInfo.getInstance(this, player), true);
			super.addMe(player);
			
			if (isPvPMode() && !isDead())
			{
				player.sendPacket(TargetHp.getInstance(this, TargetHp.RED), true);
			}
		}
		catch (NullPointerException e)
		{
			log.warning(this, e);
		}
	}
	
	@Override
	public void addPvECount()
	{
		pveCount += 1;
	}
	
	@Override
	public void addPvPCount()
	{
		pvpCount += 1;
	}
	
	@Override
	public boolean addSkill(Skill skill, boolean sendPacket)
	{
		if (super.addSkill(skill, sendPacket))
		{
			if (sendPacket)
			{
				sendPacket(SkillListInfo.getInstance(this), true);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean addSkill(SkillTemplate template, boolean sendPacket)
	{
		if (super.addSkill(template, sendPacket))
		{
			if (sendPacket)
			{
				sendPacket(SkillListInfo.getInstance(this), true);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean addSkills(Skill[] skills, boolean sendPacket)
	{
		if (super.addSkills(skills, sendPacket))
		{
			if (sendPacket)
			{
				sendPacket(SkillListInfo.getInstance(this), true);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean addSkills(SkillTemplate[] templates, boolean sendPacket)
	{
		if (super.addSkills(templates, sendPacket))
		{
			if (sendPacket)
			{
				sendPacket(SkillListInfo.getInstance(this), true);
			}
			
			return true;
		}
		
		return false;
	}
	
	public void addStamina()
	{
		setStamina(stamina + 1);
	}
	
	@Override
	public void addVisibleObject(TObject object)
	{
		if ((object == null) || (object.getObjectId() == objectId) || !object.isVisible())
		{
			return;
		}
		
		object.addMe(this);
	}
	
	@Override
	public void broadcastMove(float x, float y, float z, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean selfPacket)
	{
		final ServerPacket packet = getMovePacket(x, y, z, heading, type, targetX, targetY, targetZ);
		
		if (selfPacket)
		{
			broadcastPacket(packet);
		}
		else
		{
			broadcastPacketToOthers(packet);
		}
	}
	
	@Override
	public final void broadcastPacket(ServerPacket packet)
	{
		final UserClient client = getClient();
		
		if (client == null)
		{
			return;
		}
		
		packet.increaseSends();
		broadcastPacketToOthers(packet);
		client.sendPacket(packet);
	}
	
	@Override
	public void causingDamage(Skill skill, AttackInfo info, Character attacker)
	{
		Duel duel = getDuel();
		
		if (duel != null)
		{
			synchronized (this)
			{
				duel = getDuel();
				
				if ((duel != null) && duel.update(skill, info, attacker, this))
				{
					return;
				}
			}
		}
		
		if (attacker.isPlayer() && isPvPMode() && !attacker.isPvPMode())
		{
			attacker.setPvPMode(true);
		}
		
		super.causingDamage(skill, info, attacker);
	}
	
	@Override
	public boolean checkTarget(Character target)
	{
		if ((target == null) || (target == this))
		{
			return false;
		}
		
		if (target.isSummon())
		{
			return checkTarget(target.getOwner());
		}
		
		final Duel duel = getDuel();
		
		if (duel != null)
		{
			if (!target.isPlayer() || (target.getDuel() != duel))
			{
				return false;
			}
			
			return true;
		}
		
		final Player player = target.getPlayer();
		
		if (player != null)
		{
			if (!isGM() && (isInPeaceTerritory() || player.isInPeaceTerritory()))
			{
				return false;
			}
			
			if (isInBattleTerritory() != player.isInBattleTerritory())
			{
				return false;
			}
			
			if ((fractionId != 0) && (player.getFractionId() == fractionId))
			{
				return false;
			}
			
			if (isInBattleTerritory())
			{
				return true;
			}
			
			if ((party != null) && (party == player.getParty()))
			{
				return false;
			}
			
			if ((guild != null) && (target.getGuild() == guild))
			{
				return false;
			}
			
			return (isPvPMode() && (target.getLevel() > Config.WORLD_MIN_TARGET_LEVEL_FOR_PK) && !player.hasPremium()) || player.isPvPMode();
		}
		
		final Npc npc = target.getNpc();
		
		if ((npc != null) && npc.isFriendNpc())
		{
			return false;
		}
		
		return true;
	}
	
	public void clearLinks()
	{
		lastLinks.clear();
	}
	
	public void closeConnection()
	{
		if (client != null)
		{
			connected = false;
			client.close();
		}
	}
	
	@Override
	public void decayMe(int type)
	{
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		territoryTable.onExitWorld(this);
		super.decayMe(type);
	}
	
	public void setQuestList(QuestList questList)
	{
		this.questList = questList;
	}
	
	public void setFriendList(FriendList friendList)
	{
		this.friendList = friendList;
	}
	
	@Override
	public void deleteMe()
	{
		if (isDeleted())
		{
			return;
		}
		
		abortCast(true);
		abortCollect();
		setLastNpc(null);
		final Dialog lastDialog = getLastDialog();
		
		if (lastDialog != null)
		{
			lastDialog.close();
			setLastDialog(null);
		}
		
		final Action lastAction = getLastAction();
		
		if (lastAction != null)
		{
			lastAction.cancel(this);
			setLastAction(null);
		}
		
		final ActionDialog lastActionDialog = getLastActionDialog();
		
		if (lastActionDialog != null)
		{
			lastActionDialog.cancel(this);
			setLastActionDialog(null);
		}
		
		final Summon summon = getSummon();
		
		if (summon != null)
		{
			summon.remove();
			setSummon(null);
		}
		
		final Party party = getParty();
		
		if (party != null)
		{
			party.removePlayer(this);
		}
		
		World.removeOldPlayer(this);
		final RegenTaskManager regenManager = RegenTaskManager.getInstance();
		regenManager.removeCharacter(this);
		synchronized (this)
		{
			final QuestList questList = getQuestList();
			
			if (questList != null)
			{
				questList.save();
				questList.fold();
				setQuestList(null);
			}
			
			store(true);
			final Guild guild = getGuild();
			
			if (guild != null)
			{
				guild.exitOutGame(this);
				setGuild(null);
			}
			
			final Table<IntKey, Skill> skills = getSkills();
			skills.apply(ExtUtils.FOLD_SKILL_TABLE_FUNC);
			skills.clear();
			final Table<String, Wrap> variables = getVariables();
			variables.apply(ExtUtils.FOLD_WRAP_TABLE_FUNC);
			variables.clear();
			final Inventory inventory = getInventory();
			
			if (inventory != null)
			{
				inventory.fold();
				setInventory(null);
			}
			
			final Equipment equipment = getEquipment();
			
			if (equipment != null)
			{
				equipment.fold();
				setEquipment(null);
			}
			
			final Bank bank = getBank();
			
			if (bank != null)
			{
				bank.fold();
				setBank(null);
			}
			
			final PlayerAppearance appearance = getAppearance();
			
			if (appearance != null)
			{
				appearance.fold();
				setAppearance(null, false);
			}
			
			final FriendList friendList = getFriendList();
			
			if (friendList != null)
			{
				friendList.fold();
				setFriendList(null);
			}
			
			setClient(null);
		}
		super.deleteMe();
	}
	
	public void setAccount(Account account)
	{
		this.account = account;
	}
	
	@Override
	public boolean disableItem(Skill skill, ItemInstance item)
	{
		if (super.disableItem(skill, item))
		{
			sendPacket(ItemReuse.getInstance(item.getItemId(), skill.getReuseDelay(this) / 1000), true);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void doCast(float startX, float startY, float startZ, Skill skill, int state, int heading, float targetX, float targetY, float targetZ)
	{
		attacking = false;
		super.doCast(startX, startY, startZ, skill, state, heading, targetX, targetY, targetZ);
	}
	
	@Override
	public void doCollect(ResourseInstance resourse)
	{
		collectTask.nextTask(resourse);
	}
	
	@Override
	public void doDie(Character attacker)
	{
		if (isOnMount())
		{
			getOffMount();
		}
		
		final Party party = getParty();
		
		if (party != null)
		{
			final SystemMessage message = SystemMessage.getInstance(MessageType.PARTY_PLAYER_NAME_IS_DEAD);
			message.add("PartyPlayerName", getName());
			party.sendPacket(this, message);
		}
		
		if ((attacker != this) && attacker.isPlayer())
		{
			final Player killer = attacker.getPlayer();
			attacker.sendPacket(SystemMessage.getInstance(MessageType.YOU_KILLED_PLAYER).addPlayer(getName()), true);
			sendPacket(SystemMessage.getInstance(MessageType.PLAYER_KILLED_YOU).addPlayer(killer.getName()), true);
			checkPK(killer);
			
			if (!killer.isEvent())
			{
				killer.addPvPCount();
			}
			
			World.addKilledPlayers();
		}
		
		destroyCrystals(attacker);
		
		if (isPK() && (attacker != this))
		{
			dropItems();
		}
		
		broadcastPacket(CharDead.getInstance(this, true));
		sendPacket(PlayerDeadWindow.getInstance(), true);
		super.doDie(attacker);
	}
	
	@Override
	public int doFall(float startZ, float endZ)
	{
		final int damage = super.doFall(startZ, endZ);
		
		if ((damage > 0) && Config.SERVER_FALLING_DAMAGE)
		{
			sendMessage("Received " + damage + " damage by falling.");
		}
		
		return damage;
	}
	
	@Override
	public void doOwerturn(Character attacker)
	{
		if (isOwerturned())
		{
			return;
		}
		
		super.doOwerturn(attacker);
		final float radians = Angles.headingToRadians(heading + 32500);
		final float newX = Coords.calcX(x, 90, radians);
		final float newY = Coords.calcY(y, 90, radians);
		final GeoManager geoManager = GeoManager.getInstance();
		setXYZ(newX, newY, geoManager.getHeight(continentId, newX, newY, z));
		final SafeTask task = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				cancelOwerturn();
			}
		};
		final ExecutorManager executor = ExecutorManager.getInstance();
		executor.scheduleGeneral(task, 3000);
		broadcastMove(x, y, z, heading, MoveType.STOP, x, y, z, true);
	}
	
	@Override
	public void effectHealHp(int heal, Character healer)
	{
		int add = getCurrentHp();
		super.effectHealHp(heal, healer);
		add = getCurrentHp() - add;
		sendPacket(PlayerCurrentHp.getInstance(this, healer, add, PlayerCurrentHp.INCREASE_PLUS), true);
	}
	
	@Override
	public void effectHealMp(int heal, Character healer)
	{
		int add = getCurrentMp();
		super.effectHealMp(heal, healer);
		add = getCurrentMp() - add;
		sendPacket(PlayerCurrentMp.getInstance(this, healer, add, PlayerCurrentMp.INCREASE_PLUS), true);
	}
	
	public int getAccessLevel()
	{
		return accessLevel;
	}
	
	public Account getAccount()
	{
		return account;
	}
	
	@Override
	public PlayerAI getAI()
	{
		if (ai == null)
		{
			ai = new PlayerAI(this);
		}
		
		return (PlayerAI) ai;
	}
	
	public int getAttackCounter()
	{
		return attackCounter;
	}
	
	@Override
	protected EmotionType[] getAutoEmotions()
	{
		return EmotionTask.PLAYER_TYPES;
	}
	
	public int getBaseAttack()
	{
		return (int) calcStat(StatType.ATTACK, 0, 0x20, null, null);
	}
	
	public int getBaseBalance()
	{
		return (int) calcStat(StatType.BALANCE, 0, 0x20, null, null);
	}
	
	public int getBaseDefense()
	{
		return (int) calcStat(StatType.DEFENSE, 0, 0x20, null, null);
	}
	
	public int getBaseImpact()
	{
		return (int) calcStat(StatType.IMPACT, 0, 0x20, null, null);
	}
	
	@Override
	public int getClassId()
	{
		return getTemplate().getClassId();
	}
	
	public UserClient getClient()
	{
		return client;
	}
	
	public int getColor(Player target)
	{
		if (fractionId != 0)
		{
			return fractionId != target.getFractionId() ? NameColor.COLOR_RED_PVP : NameColor.COLOR_NORMAL;
		}
		else if ((duel != null) && (duel == target.duel))
		{
			return NameColor.COLOR_RED_PVP;
		}
		else if ((party != null) && (party == target.party))
		{
			return NameColor.COLOR_BLUE;
		}
		else if ((guild != null) && (guild == target.getGuild()))
		{
			return NameColor.COLOR_GREEN;
		}
		
		return isPvPMode() || isPK() || target.isPK() || target.isPvPMode() ? NameColor.COLOR_RED_PVP : NameColor.COLOR_NORMAL;
	}
	
	public int getColor()
	{
		if (isPvPMode() || isPK())
		{
			return NameColor.COLOR_RED;
		}
		return NameColor.COLOR_NORMAL;
	}
	
	public long getCreateTime()
	{
		return createTime;
	}
	
	@Override
	public Duel getDuel()
	{
		return duel;
	}
	
	public final long getEndBan()
	{
		return endBan;
	}
	
	public final long getEndChatBan()
	{
		return endChatBan;
	}
	
	@Override
	public final int getEnergyLevel()
	{
		return energyLevel;
	}
	
	public long getExp()
	{
		return exp;
	}
	
	public PlayerAppearance getAppearance()
	{
		return appearance;
	}
	
	public final int getFraction()
	{
		return fraction;
	}
	
	public FriendList getFriendList()
	{
		if (friendList == null)
		{
			synchronized (this)
			{
				if (friendList == null)
				{
					friendList = FriendList.getInstance(this);
				}
			}
		}
		
		return friendList;
	}
	
	@Override
	public Guild getGuild()
	{
		return guild;
	}
	
	public int getGuildId()
	{
		return guild == null ? 0 : guild.getId();
	}
	
	public String getGuildName()
	{
		return guild == null ? null : guild.getName();
	}
	
	public String getGuildTitle()
	{
		return guild == null ? null : guild.getTitle();
	}
	
	public String getGuildNote()
	{
		return guildNote;
	}
	
	public final GuildRank getGuildRank()
	{
		return guildRank;
	}
	
	public final int getGuildRankId()
	{
		return guildRank == null ? 0 : guildRank.getIndex();
	}
	
	public String getGuildIconName()
	{
		if (guild == null)
		{
			return null;
		}
		
		final GuildIcon icon = guild.getIcon();
		return icon == null ? null : icon.getName();
	}
	
	public byte[] getHotkey()
	{
		return hotkey;
	}
	
	@Override
	public int getKarma()
	{
		return karma;
	}
	
	public Action getLastAction()
	{
		return lastAction;
	}
	
	public ActionDialog getLastActionDialog()
	{
		return lastActionDialog;
	}
	
	public final long getLastBlock()
	{
		return lastBlock;
	}
	
	public Dialog getLastDialog()
	{
		return lastDialog;
	}
	
	public Link getLastLink()
	{
		return lastLink;
	}
	
	public Npc getLastNpc()
	{
		return lastNpc;
	}
	
	@Override
	public int getLevel()
	{
		return level;
	}
	
	public Link getLink(int index)
	{
		lastLinks.writeLock();
		
		try
		{
			if ((index >= lastLinks.size()) || (index < 0))
			{
				return null;
			}
			
			return lastLinks.get(index);
		}
		
		finally
		{
			lastLinks.writeUnlock();
		}
	}
	
	@Override
	public Array<Character> getLockOnTargets()
	{
		return lockOnTargets;
	}
	
	public int getMaxStamina()
	{
		return (int) calcStat(StatType.BASE_HEART, 120, this, null);
	}
	
	@Override
	public final int getMiningLevel()
	{
		return miningLevel;
	}
	
	public int getMinStamina()
	{
		return (int) ((calcStat(StatType.BASE_HEART, 120, this, null) / 100) * calcStat(StatType.MIN_HEART_PERCENT, 1, this, null));
	}
	
	public final int getMountId()
	{
		return mountId;
	}
	
	public final Skill getMountSkill()
	{
		return mountSkill;
	}
	
	@Override
	public ServerPacket getMovePacket(float x, float y, float z, int heading, MoveType type, float targetX, float targetY, float targetZ)
	{
		return PlayerMove.getInstance(this, type, x, y, z, heading, targetX, targetY, targetZ);
	}
	
	@Override
	public void getOffMount()
	{
		final Skill skill = getMountSkill();
		
		if (skill == null)
		{
			return;
		}
		
		final SkillTemplate template = skill.getTemplate();
		template.removePassiveFuncs(this);
		setMountId(0);
		broadcastPacket(MountOff.getInstance(this, skill.getIconId()));
		setMountSkill(null);
		updateInfo();
	}
	
	public long getOnlineBeginTime()
	{
		return onlineBeginTime;
	}
	
	public long getOnlineTime()
	{
		return onlineTime + (System.currentTimeMillis() - onlineBeginTime);
	}
	
	@Override
	public int getOwerturnId()
	{
		return 0x080F6C72;
	}
	
	@Override
	public Party getParty()
	{
		return party;
	}
	
	@Override
	public final int getPlantLevel()
	{
		return plantLevel;
	}
	
	@Override
	public Player getPlayer()
	{
		return this;
	}
	
	public PlayerClass getPlayerClass()
	{
		return getTemplate().getPlayerClass();
	}
	
	public final int getPveCount()
	{
		return pveCount;
	}
	
	public final int getPvpCount()
	{
		return pvpCount;
	}
	
	@Override
	public QuestList getQuestList()
	{
		if (questList == null)
		{
			synchronized (this)
			{
				if (questList == null)
				{
					questList = QuestList.newInstance(this);
				}
			}
		}
		
		return questList;
	}
	
	public int getRaceId()
	{
		return getTemplate().getRaceId();
	}
	
	public Route getRoute()
	{
		return route;
	}
	
	public byte[] getSettings()
	{
		return settings;
	}
	
	public Sex getSex()
	{
		return getTemplate().getSex();
	}
	
	public int getSexId()
	{
		return getTemplate().getSex().ordinal();
	}
	
	public int getStamina()
	{
		return stamina;
	}
	
	@Override
	public int getSubId()
	{
		return Config.SERVER_PLAYER_SUB_ID;
	}
	
	@Override
	public PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) template;
	}
	
	@Override
	public String getTitle()
	{
		return title;
	}
	
	public final Wrap getVar(String name)
	{
		return variables.get(name);
	}
	
	public final int getVar(String name, int def)
	{
		final Table<String, Wrap> variables = getVariables();
		synchronized (variables)
		{
			final Wrap wrap = variables.get(name);
			
			if (wrap == null)
			{
				return def;
			}
			
			if (wrap.getWrapType() == WrapType.INTEGER)
			{
				return wrap.getInt();
			}
		}
		return def;
	}
	
	public final Table<String, Wrap> getVariables()
	{
		return variables;
	}
	
	public boolean hasDuel()
	{
		return duel != null;
	}
	
	@Override
	public boolean hasGuild()
	{
		return guild != null;
	}
	
	public boolean hasHotKey()
	{
		return hotkey != null;
	}
	
	public boolean hasLastAction()
	{
		return lastAction != null;
	}
	
	public boolean hasLastActionDialog()
	{
		return lastActionDialog != null;
	}
	
	public boolean hasNetConnection()
	{
		return client != null;
	}
	
	@Override
	public boolean hasParty()
	{
		return party != null;
	}
	
	@Override
	public boolean hasPremium()
	{
		final Account account = getAccount();
		
		if (account == null)
		{
			return false;
		}
		
		return System.currentTimeMillis() < account.getEndPay();
	}
	
	public boolean hasSettings()
	{
		return settings != null;
	}
	
	public void increaseLevel()
	{
		if ((level + 1) > Config.WORLD_PLAYER_MAX_LEVEL)
		{
			return;
		}
		
		level += 1;
		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());
		broadcastPacket(IncreaseLevel.getInstance(this));
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyChangedLevel(this);
	}
	
	@Override
	public boolean isAttacking()
	{
		return attacking;
	}
	
	public final boolean isChangedFace()
	{
		return changedFace;
	}
	
	public final boolean isChangedSettings()
	{
		return changedSettings;
	}
	
	public final boolean isChangeHotkey()
	{
		return changeHotkey;
	}
	
	@Override
	public boolean isCollecting()
	{
		return collectTask.isRunning();
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public final boolean isEvent()
	{
		return event;
	}
	
	@Override
	public boolean isGM()
	{
		return accessLevel > 100;
	}
	
	@Override
	public boolean isOnMount()
	{
		return mountId != 0;
	}
	
	@Override
	public final boolean isPlayer()
	{
		return true;
	}
	
	@Override
	public boolean isPvPMode()
	{
		return pvpMode;
	}
	
	public final boolean isResurrected()
	{
		return resurrected;
	}
	
	public boolean isWhetherIn(Territory territory)
	{
		return storedTerrs.containsKey(territory.getId());
	}
	
	public void loadVariables()
	{
	}
	
	@Override
	protected Geom newGeomCharacter()
	{
		return new PlayerGeom(this, PlayerGeomTable.getHeight(getRaceId(), getSexId()), PlayerGeomTable.getRadius(getRaceId(), getSexId()));
	}
	
	@Override
	protected Regen newRegenHp()
	{
		return new PlayerRegenHp(this);
	}
	
	@Override
	protected Regen newRegenMp()
	{
		if ((getClassId() == 2) || (getClassId() == 3))
		{
			return new PlayerNegativeRegenMp(this);
		}
		
		return new PlayerPositiveRegenMp(this);
	}
	
	@Override
	public int nextCastId()
	{
		return ID_FACTORY.getNextId();
	}
	
	public void removeBonfire(Bonfire oldBonfire)
	{
		if (bonfire == oldBonfire)
		{
			synchronized (this)
			{
				if (bonfire == oldBonfire)
				{
					bonfire = null;
					sendMessage(MessageType.YOU_ARE_NO_LONGER_RECHARGING_STAMINA);
				}
			}
		}
	}
	
	@Override
	public void removeMe(Player player, int type)
	{
		Duel duel = getDuel();
		
		if (duel != null)
		{
			synchronized (this)
			{
				duel = getDuel();
				
				if ((duel != null) && (player.getDuel() == duel))
				{
					duel.cancel(false, true);
				}
			}
		}
		
		player.sendPacket(DeleteCharacter.getInstance(this, type), true);
	}
	
	@Override
	public void removeSkill(int skillId, boolean sendPacket)
	{
		super.removeSkill(skillId, sendPacket);
		
		if (sendPacket)
		{
			sendPacket(SkillListInfo.getInstance(this), true);
		}
	}
	
	@Override
	public void removeSkill(Skill skill, boolean sendPacket)
	{
		super.removeSkill(skill, sendPacket);
		
		if (sendPacket)
		{
			sendPacket(SkillListInfo.getInstance(this), true);
		}
	}
	
	@Override
	public void removeSkill(SkillTemplate template, boolean sendPacket)
	{
		super.removeSkill(template, sendPacket);
		
		if (sendPacket)
		{
			sendPacket(SkillListInfo.getInstance(this), true);
		}
	}
	
	@Override
	public void removeSkills(SkillTemplate[] templates, boolean sendPacket)
	{
		super.removeSkills(templates, sendPacket);
		
		if (sendPacket)
		{
			sendPacket(SkillListInfo.getInstance(this), true);
		}
	}
	
	@Override
	public void removeVisibleObject(TObject object, int type)
	{
		if ((object == null) || (object.getObjectId() == objectId))
		{
			return;
		}
		
		object.removeMe(this, type);
	}
	
	public void saveVars()
	{
		final Table<String, Wrap> variables = getVariables();
		synchronized (variables)
		{
			variables.apply(saveVarFunc);
		}
	}
	
	public void sendEffects()
	{
		if ((effectList == null) || (effectList.size() < 1))
		{
			return;
		}
		
		effectList.lock();
		
		try
		{
			final Array<Effect> effects = effectList.getEffects();
			final Effect[] array = effects.array();
			
			for (int i = 0, length = effects.size(); i < length; i++)
			{
				final Effect effect = array[i];
				
				if ((effect == null) || effect.isEnded())
				{
					continue;
				}
				
				if (effect.getEffectType() == EffectType.CHARM_BUFF)
				{
					sendPacket(AppledCharmEffect.getInstance(this, effect.getEffectId(), effect.getTimeEnd() * 1000), true);
					continue;
				}
				
				sendPacket(AppledEffect.getInstance(effect.getEffector(), effect.getEffected(), effect.getEffectId(), effect.getTimeEnd() * 1000), true);
			}
		}
		
		finally
		{
			effectList.unlock();
		}
	}
	
	@Override
	public void sendMessage(MessageType type)
	{
		sendPacket(SystemMessage.getInstance(type), true);
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(CharSay.getInstance(Strings.EMPTY, message, SayType.SYSTEM_CHAT, 0, 0), true);
	}
	
	@Override
	public void sendPacket(ServerPacket packet, boolean increaseSends)
	{
		if (packet == null)
		{
			return;
		}
		
		final UserClient client = getClient();
		
		if (client == null)
		{
			return;
		}
		
		client.sendPacket(packet, increaseSends);
	}
	
	public void sendReuseItems()
	{
		final Table<IntKey, ReuseSkill> reuses = getReuseSkills();
		
		for (ReuseSkill reuse : reuses)
		{
			if (reuse.isItemReuse())
			{
				sendPacket(ItemReuse.getInstance(reuse.getItemId(), (int) Math.max(0, (reuse.getEndTime() - System.currentTimeMillis()) / 1000)), true);
			}
		}
	}
	
	public void sendReuseSkills()
	{
		final Table<IntKey, ReuseSkill> reuses = getReuseSkills();
		
		for (ReuseSkill reuse : reuses)
		{
			if (!reuse.isItemReuse())
			{
				sendPacket(SkillReuse.getInstance(reuse.getSkillId(), (int) Math.max(0, reuse.getEndTime() - System.currentTimeMillis())), true);
			}
		}
	}
	
	public void setAccessLevel(int level)
	{
		accessLevel = level;
	}
	
	public void setAttackCounter(int attackCounter)
	{
		this.attackCounter = attackCounter;
	}
	
	@Override
	public void setAttacking(boolean attacking)
	{
		this.attacking = attacking;
	}
	
	public final void setChangedFace(boolean changedFace)
	{
		this.changedFace = changedFace;
	}
	
	public final void setChangedSettings(boolean changedSettings)
	{
		this.changedSettings = changedSettings;
	}
	
	public final void setChangeHotkey(boolean changeHotkey)
	{
		this.changeHotkey = changeHotkey;
	}
	
	public void setClient(UserClient client)
	{
		this.client = client;
		account = client != null ? client.getAccount() : null;
		connected = (client != null) && client.isConnected();
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
	public void setCreateTime(final long createTime)
	{
		this.createTime = createTime;
	}
	
	public void setDuel(Duel duel)
	{
		this.duel = duel;
	}
	
	public final void setEndBan(long endBan)
	{
		this.endBan = endBan;
	}
	
	public final void setEndChatBan(long endChatBan)
	{
		this.endChatBan = endChatBan;
	}
	
	public final void setEnergyLevel(int energyLevel)
	{
		this.energyLevel = Math.min(energyLevel, 300);
	}
	
	public final void setEvent(boolean event)
	{
		this.event = event;
	}
	
	public void setExp(int exp)
	{
		this.exp = Math.max(exp, 0);
	}
	
	public void setAppearance(PlayerAppearance appearance, boolean isNew)
	{
		this.appearance = appearance;
		
		if (isNew)
		{
			setChangedFace(true);
		}
	}
	
	public final void setFraction(int fraction)
	{
		this.fraction = fraction;
	}
	
	public void setGuild(Guild guild)
	{
		this.guild = guild;
	}
	
	public void setGuildId(int id)
	{
		final GuildManager guildManager = GuildManager.getInstance();
		guild = guildManager.getGuild(id);
	}
	
	public void setGuildNote(String guildNote)
	{
		if (guildNote.isEmpty())
		{
			guildNote = Strings.EMPTY;
		}
		
		this.guildNote = guildNote;
	}
	
	public final void setGuildRank(GuildRank guildRank)
	{
		this.guildRank = guildRank;
	}
	
	public void setHotkey(byte[] hotkey, boolean isNew)
	{
		this.hotkey = hotkey;
		
		if (isNew)
		{
			changeHotkey = true;
		}
	}
	
	@Override
	public void setKarma(int karma)
	{
		this.karma = Math.max(karma, 0);
	}
	
	public void setLastAction(Action lastAction)
	{
		this.lastAction = lastAction;
	}
	
	public void setLastActionDialog(ActionDialog lastActionDialog)
	{
		this.lastActionDialog = lastActionDialog;
	}
	
	public void setLastDialog(Dialog lastDialog)
	{
		this.lastDialog = lastDialog;
	}
	
	public void setLastLink(Link lastLink)
	{
		this.lastLink = lastLink;
	}
	
	public void setLastNpc(Npc lastNpc)
	{
		this.lastNpc = lastNpc;
	}
	
	public boolean setLevel(int newLevel)
	{
		if (newLevel > Config.WORLD_PLAYER_MAX_LEVEL)
		{
			level = Config.WORLD_PLAYER_MAX_LEVEL;
		}
		else if (newLevel < 1)
		{
			level = 1;
		}
		else
		{
			level = newLevel;
		}
		
		return level == newLevel;
	}
	
	public final void setMiningLevel(int miningLevel)
	{
		this.miningLevel = Math.min(miningLevel, 300);
	}
	
	public final void setMountId(int mountId)
	{
		this.mountId = mountId;
	}
	
	public final void setMountSkill(Skill mountSkill)
	{
		this.mountSkill = mountSkill;
	}
	
	public void setOnlineBeginTime(long onlineBeginTime)
	{
		this.onlineBeginTime = onlineBeginTime;
	}
	
	public void setOnlineTime(final long time)
	{
		onlineTime = time;
		setOnlineBeginTime(System.currentTimeMillis());
	}
	
	public void setParty(Party party)
	{
		this.party = party;
	}
	
	public final void setPlantLevel(int plantLevel)
	{
		this.plantLevel = Math.min(plantLevel, 300);
	}
	
	public final void setPvECount(int pveCount)
	{
		this.pveCount = pveCount;
	}
	
	public final void setPvPCount(int pvpCount)
	{
		this.pvpCount = pvpCount;
	}
	
	@Override
	public void setPvPMode(boolean pvpMode)
	{
		this.pvpMode = pvpMode;
		
		if (isSpawned())
		{
			final LocalObjects local = LocalObjects.get();
			final Array<Player> players = World.getAround(Player.class, local.getNextPlayerList(), this);
			final Player[] array = players.array();
			ServerPacket hp = null;
			ServerPacket pvp = null;
			
			if (pvpMode)
			{
				hp = TargetHp.getInstance(this, TargetHp.RED);
				pvp = PlayerPvPOn.getInstance(this);
			}
			else
			{
				hp = CancelTargetHp.getInstance(this);
				pvp = PlayerPvPOff.getInstance(this);
			}
			
			hp.increaseSends();
			pvp.increaseSends();
			
			for (int i = 0, length = players.size(); i < length; i++)
			{
				final Player player = array[i];
				
				if (player == null)
				{
					continue;
				}
				
				player.sendPacket(pvp, true);
				player.sendPacket(hp, true);
			}
			
			sendPacket(pvp, true);
			sendPacket(NameColor.getInstance(getColor(), this), true);
			updateColor(players);
			hp.complete();
			pvp.complete();
		}
	}
	
	public void updateColor(Array<Player> players)
	{
		final Player[] array = players.array();
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			final Player player = array[i];
			player.updateColor(this);
			updateColor(player);
		}
	}
	
	public void updateColor()
	{
		final LocalObjects local = LocalObjects.get();
		final Array<Player> players = World.getAround(Player.class, local.getNextPlayerList(), this);
		updateColor(players);
	}
	
	public final void setResurrected(boolean resurrected)
	{
		this.resurrected = resurrected;
	}
	
	public void setRoute(Route route)
	{
		this.route = route;
	}
	
	public void setSettings(byte[] settings, boolean isNew)
	{
		this.settings = settings;
		
		if (isNew)
		{
			changedSettings = true;
		}
	}
	
	public void setStamina(int stamina)
	{
		final int maxStamina = getMaxStamina();
		final int minStamina = getMinStamina();
		
		if (stamina > maxStamina)
		{
			stamina = maxStamina;
		}
		
		if (stamina < minStamina)
		{
			stamina = minStamina;
		}
		
		this.stamina = stamina;
		int current = getCurrentHp();
		int max = getMaxHp();
		boolean updateUserInfo = false;
		
		if (current > max)
		{
			setCurrentHp(max);
			updateUserInfo = true;
		}
		
		current = getCurrentMp();
		max = getMaxMp();
		
		if (current > max)
		{
			setCurrentMp(max);
			updateUserInfo = true;
		}
		
		if (updateUserInfo)
		{
			updateInfo();
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyStaminaChanged(this);
	}
	
	public void setVar(String name, int val)
	{
		final Table<String, Wrap> variables = getVariables();
		synchronized (variables)
		{
			final Wrap wrap = variables.get(name);
			
			if (wrap == null)
			{
				variables.put(name, Wraps.newIntegerWrap(val, true));
				final DataBaseManager dbManager = DataBaseManager.getInstance();
				dbManager.insertPlayerVar(objectId, name, String.valueOf(val));
			}
			else if (wrap.getWrapType() == WrapType.INTEGER)
			{
				wrap.setInt(val);
			}
			else
			{
				variables.put(name, Wraps.newIntegerWrap(val, true));
			}
		}
	}
	
	public void setVar(String name, String value)
	{
	}
	
	@Override
	public void setXYZ(float x, float y, float z)
	{
		super.setXYZ(x, y, z);
		updateTerritories();
	}
	
	@Override
	public void spawnMe()
	{
		super.spawnMe();
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		territoryTable.onEnterWorld(this);
		updateGuild();
		emotionTask.start();
	}
	
	@Override
	public boolean startBattleStance(Character enemy)
	{
		if (isBattleStanced())
		{
			battleStanceTask.update();
			return false;
		}
		
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			sendMessage("start battle stance.");
		}
		
		battleStanceTask.now();
		battleStanced = true;
		sendMessage(MessageType.BATTLE_STANCE_ON);
		updateInfo();
		return true;
	}
	
	@Override
	public void stopBattleStance()
	{
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			sendMessage("stop battle stance.");
		}
		
		battleStanceTask.stop();
		battleStanced = false;
		sendMessage(MessageType.BATTLE_STANCE_OFF);
		updateInfo();
	}
	
	public synchronized void store(boolean deleted)
	{
		if (isDeleted())
		{
			return;
		}
		
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.fullStore(this);
	}
	
	public void storeTerritory(Territory territory, boolean inDB)
	{
		storedTerrs.put(territory.getId(), territory);
		
		if (inDB)
		{
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.storeTerritory(this, territory);
		}
	}
	
	public void subHeart()
	{
		setStamina(stamina - 1);
	}
	
	@Override
	public void teleToLocation(int continentId, float x, float y, float z, int heading)
	{
		decayMe(DeleteCharacter.DISAPPEARS);
		final int current = getContinentId();
		super.teleToLocation(continentId, x, y, z, heading);
		
		if (current != continentId)
		{
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.updatePlayerContinentId(this);
		}
		
		broadcastPacket(Tp1.getInstance(this));
		int zoneId = World.getRegion(this).getZoneId(this);
		
		if (zoneId < 1)
		{
			zoneId = getContinentId() + 1;
		}
		
		setZoneId(zoneId);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyChangedZoneId(this);
		sendPacket(WorldZone.getInstance(this), true);
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	public void unsetVar(String name)
	{
		if (name == null)
		{
			return;
		}
	}
	
	public void updateColor(Player target)
	{
		sendPacket(NameColor.getInstance(getColor(target), target), true);
		target.sendPacket(NameColor.getInstance(target.getColor(this), this), true);
	}
	
	@Override
	public void updateCoords()
	{
		if (party != null)
		{
			party.updateCoords(this);
		}
	}
	
	@Override
	public void updateEffects()
	{
		if ((effectList == null) || (effectList.size() < 1))
		{
			return;
		}
		
		final Array<Effect> effects = effectList.getEffects();
		final Effect[] array = effects.array();
		
		for (int i = 0, length = effects.size(); i < length; i++)
		{
			final Effect effect = array[i];
			
			if ((effect == null) || effect.isEnded())
			{
				continue;
			}
			
			broadcastPacket(AppledEffect.getInstance(effect.getEffector(), effect.getEffected(), effect));
		}
	}
	
	public void updateGuild()
	{
		sendPacket(GuildInfo.getInstance(this), true);
		
		if (guild == null)
		{
			return;
		}
		
		sendPacket(GuildMembers.getInstance(this), true);
		sendPacket(GuildLogs.getInstance(this), true);
	}
	
	@Override
	public void updateHp()
	{
		sendPacket(PlayerCurrentHp.getInstance(this, null, 0, PlayerCurrentHp.INCREASE), true);
		final Party party = getParty();
		
		if (party != null)
		{
			party.updateStat(this);
		}
		
		final Duel duel = getDuel();
		
		if (duel != null)
		{
			final Player enemy = duel.getEnemy(this);
			
			if (enemy != null)
			{
				enemy.sendPacket(TargetHp.getInstance(this, TargetHp.RED), true);
			}
		}
		
		if (pvpMode)
		{
			broadcastPacketToOthers(TargetHp.getInstance(this, TargetHp.RED));
		}
	}
	
	@Override
	public void updateInfo()
	{
		sendPacket(UserInfo.getInstance(this), true);
	}
	
	@Override
	public void updateMp()
	{
		sendPacket(PlayerCurrentMp.getInstance(this, null, 0, PlayerCurrentMp.INCREASE), true);
		
		if (party != null)
		{
			party.updateStat(this);
		}
	}
	
	public void updateOtherInfo()
	{
		final LocalObjects local = LocalObjects.get();
		final Array<Player> players = World.getAround(Player.class, local.getNextPlayerList(), this);
		final Player[] array = players.array();
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			final Player target = array[i];
			target.removeMe(this, DeleteCharacter.DISAPPEARS);
			removeMe(target, DeleteCharacter.DISAPPEARS);
			target.addMe(this);
			addMe(target);
		}
	}
	
	public void updateQuestInPanel(QuestState quest, QuestPanelState panelState)
	{
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		questInPanel.writeLock();
		
		try
		{
			switch (panelState)
			{
				case REMOVED:
				{
					final int index = questInPanel.indexOf(quest);
					
					if (index < 0)
					{
						return;
					}
					
					questInPanel.fastRemove(index);
					quest.setPanelState(QuestPanelState.REMOVED);
					dbManager.updateQuest(quest);
					break;
				}
				
				case ADDED:
				{
					if (questInPanel.contains(quest))
					{
						return;
					}
					
					if (questInPanel.size() >= MAXIMUM_QUEST_IN_PANEL)
					{
						sendMessage(MessageType.QUEST_TRACKER_DISPLAYS_UP_TO_7_QUESTS);
						return;
					}
					
					questInPanel.add(quest);
					quest.setPanelState(QuestPanelState.ADDED);
					sendPacket(QuestMoveToPanel.getInstance(quest), true);
					dbManager.updateQuest(quest);
					break;
				}
				
				case ACCEPTED:
				{
					if (questInPanel.size() >= MAXIMUM_QUEST_IN_PANEL)
					{
						quest.setPanelState(QuestPanelState.REMOVED);
					}
					else
					{
						quest.setPanelState(QuestPanelState.ADDED);
						
						if (!questInPanel.contains(quest))
						{
							questInPanel.add(quest);
						}
						
						sendPacket(QuestMoveToPanel.getInstance(quest), true);
					}
					
					dbManager.updateQuest(quest);
					break;
				}
				
				case UPDATE:
				{
					if (questInPanel.contains(quest))
					{
						return;
					}
					
					if (questInPanel.size() >= MAXIMUM_QUEST_IN_PANEL)
					{
						return;
					}
					
					questInPanel.add(quest);
					quest.setPanelState(QuestPanelState.ADDED);
					sendPacket(QuestMoveToPanel.getInstance(quest), true);
					dbManager.updateQuest(quest);
					break;
				}
				
				case NONE:
					break;
				
				default:
					log.warning(this, new Exception("incorrect panel state."));
			}
		}
		
		finally
		{
			questInPanel.writeUnlock();
		}
	}
	
	public void updateQuestPanel()
	{
	}
	
	@Override
	public void updateReuse(Skill skill, int reuseDelay)
	{
		sendPacket(SkillReuse.getInstance(skill.getReuseId(), reuseDelay), true);
	}
	
	@Override
	public void updateStamina()
	{
		updateInfo();
		
		if (party != null)
		{
			party.updateStat(this);
		}
	}
	
	public void destroyCrystals(Character killer)
	{
		if ((killer == this) || isInBattleTerritory())
		{
			return;
		}
		
		final Equipment equipment = getEquipment();
		
		if (equipment != null)
		{
			equipment.lock();
			
			try
			{
				final Slot[] slots = equipment.getSlots();
				boolean changed = false;
				
				for (Slot slot : slots)
				{
					final ItemInstance item = slot.getItem();
					
					if (item == null)
					{
						continue;
					}
					
					final CrystalList crystals = item.getCrystals();
					
					if ((crystals == null) || crystals.isEmpty())
					{
						continue;
					}
					
					if (changed)
					{
						crystals.destruction(this);
					}
					else
					{
						changed = crystals.destruction(this);
					}
				}
				
				if (changed)
				{
					final ObjectEventManager eventManager = ObjectEventManager.getInstance();
					eventManager.notifyInventoryChanged(this);
				}
			}
			
			finally
			{
				equipment.unlock();
			}
		}
	}
	
	public void checkPK(Player player)
	{
		if (!player.isPvPMode() || player.isEvent() || isPvPMode() || ((player.getLevel() - getLevel()) < 5))
		{
			return;
		}
		
		final boolean updateColor = !player.isPK();
		final Array<Territory> territories = player.getTerritories();
		territories.readLock();
		
		try
		{
			final Territory[] array = territories.array();
			
			for (int i = 0, length = territories.size(); i < length; i++)
			{
				final Territory territory = array[i];
				
				switch (territory.getType())
				{
					case BATTLE_TERRITORY:
						return;
					
					case REGION_TERRITORY:
					{
						final RegionTerritory regionTerritory = (RegionTerritory) territory;
						final Region region = regionTerritory.getRegion();
						
						if (region.getState() == RegionState.PREPARE_END_WAR)
						{
							return;
						}
						
						break;
					}
					
					default:
						continue;
				}
			}
		}
		
		finally
		{
			territories.readUnlock();
		}
		final int karma = 100 * (player.getLevel() - getLevel());
		synchronized (player)
		{
			player.setKarma(player.getKarma() + karma);
		}
		player.sendMessage("You received " + karma + " karma, your total amount is: " + player.getKarma());
		
		if (updateColor)
		{
			player.updateColor();
			player.sendPacket(NameColor.getInstance(player.getColor(), player), true);
		}
	}
	
	public void dropItems()
	{
		final LocalObjects local = LocalObjects.get();
		final Array<ItemInstance> itemList = local.getNextItemList();
		final int chance = 5;
		final int[] donat = Config.WORLD_DONATE_ITEMS;
		final Equipment equipment = getEquipment();
		boolean equipDrop = false;
		
		if (equipment != null)
		{
			equipment.lock();
			
			try
			{
				final Slot[] slots = equipment.getSlots();
				
				for (Slot slot : slots)
				{
					final ItemInstance item = slot.getItem();
					
					if ((item == null) || Arrays.contains(donat, item.getItemId()) || !Rnd.chance(chance))
					{
						continue;
					}
					
					slot.setItem(null);
					itemList.add(item);
					equipDrop = true;
				}
			}
			
			finally
			{
				equipment.unlock();
			}
		}
		
		final Inventory inventory = getInventory();
		boolean inventoryDrop = false;
		
		if (inventory != null)
		{
			inventory.lock();
			
			try
			{
				final Cell[] cells = inventory.getCells();
				
				for (Cell cell : cells)
				{
					final ItemInstance item = cell.getItem();
					
					if ((item == null) || item.isStackable() || Arrays.contains(donat, item.getItemId()) || !Rnd.chance(chance))
					{
						continue;
					}
					
					cell.setItem(null);
					itemList.add(item);
					inventoryDrop = true;
				}
			}
			
			finally
			{
				inventory.unlock();
			}
		}
		
		if (!itemList.isEmpty())
		{
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			final ItemInstance[] array = itemList.array();
			
			for (int i = 0, length = itemList.size(); i < length; i++)
			{
				final ItemInstance item = array[i];
				item.setOwnerId(0);
				item.setLocation(ItemLocation.NONE);
				item.setDropper(this);
				dbManager.updateLocationItem(item);
			}
			
			Npc.spawnDropItems(this, array, itemList.size());
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			
			if (equipDrop)
			{
				eventManager.notifyEquipmentChanged(this);
			}
			else if (inventoryDrop)
			{
				eventManager.notifyInventoryChanged(this);
			}
		}
	}
	
	public void clearKarma(Npc npc)
	{
		if (npc.getExp() < 20)
		{
			return;
		}
		
		int clear = Math.max(Math.min(getLevel() - npc.getLevel(), 5), 0);
		clear = Math.min((5 - clear) * 50 * npc.getKarmaMod(), getKarma());
		
		if (clear > 0)
		{
			setKarma(getKarma() - clear);
			sendMessage("You cleared " + clear + " karma, you now have " + getKarma() + ".");
		}
		
		if (!isPK())
		{
			updateColor();
			sendPacket(NameColor.getInstance(getColor(), this), true);
		}
	}
	
	@Override
	public boolean isRangeClass()
	{
		return getPlayerClass().isRange();
	}
	
	@Override
	public boolean isPK()
	{
		return getKarma() > 0;
	}
}