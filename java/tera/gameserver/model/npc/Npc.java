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
package tera.gameserver.model.npc;

import java.util.Comparator;

import tera.Config;
import tera.gameserver.IdFactory;
import tera.gameserver.manager.EventManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.manager.RandomManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.Party;
import tera.gameserver.model.World;
import tera.gameserver.model.WorldRegion;
import tera.gameserver.model.ai.CharacterAI;
import tera.gameserver.model.geom.Geom;
import tera.gameserver.model.geom.NpcGeom;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.interaction.DialogData;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.playable.NpcAppearance;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.NpcIconType;
import tera.gameserver.model.quests.QuestData;
import tera.gameserver.model.quests.QuestType;
import tera.gameserver.model.regenerations.NpcRegenHp;
import tera.gameserver.model.regenerations.NpcRegenMp;
import tera.gameserver.model.regenerations.Regen;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillGroup;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.NameColor;
import tera.gameserver.network.serverpackets.NpcInfo;
import tera.gameserver.network.serverpackets.NpcNotice;
import tera.gameserver.network.serverpackets.QuestNpcNotice;
import tera.gameserver.network.serverpackets.TargetHp;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.taskmanager.RegenTaskManager;
import tera.gameserver.tasks.EmotionTask;
import tera.gameserver.tasks.TurnTask;
import tera.gameserver.templates.NpcTemplate;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;
import rlib.util.random.Random;

/**
 * @author Ronn
 */
public abstract class Npc extends Character implements Foldable
{
	public static final float[] PENALTY_EXP =
	{
		1F, // 0
		1F, // 1
		1F, // 2
		1F, // 3
		1F, // 4
		1F, // 5
		0.5F, // 6
		0.4F, // 7
		0.3F, // 8
		0.2F, // 9
		0.1F, // 10
		0F, // 12
	};
	public static final int INTERACT_RANGE = 200;
	private static final Comparator<AggroInfo> AGGRO_COMPORATOR = (info, next) ->
	{
		if (info == null)
		{
			return 1;
		}
		
		if (next == null)
		{
			return -1;
		}
		
		return next.compareTo(info);
	};
	
	/**
	 * Method spawnDropItems.
	 * @param character Character
	 * @param items ItemInstance[]
	 * @param length int
	 */
	public static void spawnDropItems(Character character, ItemInstance[] items, int length)
	{
		if (length < 1)
		{
			return;
		}
		
		final RandomManager randManager = RandomManager.getInstance();
		final Random random = randManager.getDropItemPointRandom();
		final float x = character.getX();
		final float y = character.getY();
		final float z = character.getZ();
		final int continentId = character.getContinentId();
		final GeoManager geoManager = GeoManager.getInstance();
		
		for (int i = 1; i <= length; i++)
		{
			final float radians = Angles.headingToRadians(random.nextInt(0, Short.MAX_VALUE * 2));
			final int distance = random.nextInt(40, 80);
			final float newX = Coords.calcX(x, distance, radians);
			final float newY = Coords.calcY(y, distance, radians);
			final float newZ = geoManager.getHeight(continentId, newX, newY, z);
			final ItemInstance item = items[i - 1];
			item.setContinentId(continentId);
			item.spawnMe(newX, newY, newZ, 0);
		}
	}
	
	protected final FoldablePool<AggroInfo> aggroInfoPool;
	protected final Array<AggroInfo> aggroList;
	protected final TurnTask turnTask;
	protected Spawn spawn;
	protected Location spawnLoc;
	protected Skill[][] skills;
	protected volatile boolean aggroSorted;
	
	/**
	 * Constructor for Npc.
	 * @param objectId int
	 * @param template NpcTemplate
	 */
	public Npc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		aggroInfoPool = Pools.newConcurrentFoldablePool(AggroInfo.class);
		aggroList = Arrays.toConcurrentArray(AggroInfo.class);
		turnTask = new TurnTask(this);
		final SkillTemplate[][] temps = template.getSkills();
		skills = new Skill[temps.length][];
		
		for (int i = 0, length = temps.length; i < length; i++)
		{
			final SkillTemplate[] list = temps[i];
			
			if (list == null)
			{
				continue;
			}
			
			skills[i] = SkillTable.create(list);
			addSkills(skills[i], false);
		}
		
		final Formulas formulas = Formulas.getInstance();
		formulas.addFuncsToNewNpc(this);
		final RegenTaskManager regenManager = RegenTaskManager.getInstance();
		regenManager.addCharacter(this);
	}
	
	/**
	 * Method addAggro.
	 * @param aggressor Character
	 * @param aggro long
	 * @param damage boolean
	 */
	public void addAggro(Character aggressor, long aggro, boolean damage)
	{
		if (aggro < 1)
		{
			return;
		}
		
		aggressor.addHated(this);
		aggro *= aggressor.calcStat(StatType.AGGRO_MOD, 1, this, null);
		final Array<AggroInfo> aggroList = getAggroList();
		aggroList.writeLock();
		
		try
		{
			final int index = aggroList.indexOf(aggressor);
			
			if (index < 0)
			{
				aggroList.add(newAggroInfo(aggressor, aggro, damage ? aggro : 0));
			}
			else
			{
				final AggroInfo info = aggroList.get(index);
				info.addAggro(aggro);
				
				if (damage)
				{
					info.addDamage(Math.min(aggro, getCurrentHp()));
				}
			}
			
			setAggroSorted(index == 0);
		}
		
		finally
		{
			aggroList.writeUnlock();
		}
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyAgression(this, aggressor, aggro);
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(NpcInfo.getInstance(this, player), true);
		
		if (isBattleStanced())
		{
			PacketManager.showBattleStance(player, this, getEnemy());
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyAddNpc(player, this);
		super.addMe(player);
	}
	
	/**
	 * Method calculateRewards.
	 * @param killer Character
	 */
	protected void calculateRewards(Character killer)
	{
		Character top = getMostDamager();
		
		if (top == null)
		{
			top = killer;
		}
		
		if (top.isPK())
		{
			return;
		}
		
		if (top.isSummon())
		{
			top = top.getOwner();
		}
		
		if ((top == null) || !top.isPlayer())
		{
			return;
		}
		
		final NpcTemplate template = getTemplate();
		final int exp = (int) (template.getExp() * Config.SERVER_RATE_EXP);
		final Player player = top.getPlayer();
		
		if (exp > 0)
		{
			final Party party = player.getParty();
			
			if (party != null)
			{
				party.addExp(exp, player, this);
			}
			else
			{
				float reward = exp;
				final int diff = Math.abs(player.getLevel() - getLevel());
				
				if (diff >= PENALTY_EXP.length)
				{
					reward *= 0F;
				}
				else if (diff > 5)
				{
					reward *= PENALTY_EXP[diff];
				}
				
				if (Config.ACCOUNT_PREMIUM_EXP && player.hasPremium())
				{
					reward *= Config.ACCOUNT_PREMIUM_EXP_RATE;
				}
				
				player.addExp((int) reward, this, getName());
			}
		}
		
		if (template.isCanDrop())
		{
			final LocalObjects local = LocalObjects.get();
			final Array<ItemInstance> items = template.getDrop(local.getNextItemList(), this, player);
			
			if (items != null)
			{
				final Party party = player.getParty();
				final ItemInstance[] array = items.array();
				
				for (int i = 0, length = items.size(); i < length; i++)
				{
					final ItemInstance item = array[i];
					item.setDropper(this);
					item.setTempOwner(player);
					item.setTempOwnerParty(party);
				}
				
				spawnDropItems(this, array, items.size());
			}
		}
	}
	
	/**
	 * Method checkInteraction.
	 * @param player Player
	 * @return boolean
	 */
	public boolean checkInteraction(Player player)
	{
		return isInRange(player, INTERACT_RANGE);
	}
	
	/**
	 * Method checkTarget.
	 * @param target Character
	 * @return boolean
	 */
	@Override
	public boolean checkTarget(Character target)
	{
		return true;
	}
	
	public void clearAggroList()
	{
		final Array<AggroInfo> aggroList = getAggroList();
		final FoldablePool<AggroInfo> pool = getAggroInfoPool();
		aggroList.writeLock();
		
		try
		{
			final AggroInfo[] array = aggroList.array();
			
			for (int i = 0, length = aggroList.size(); i < length; i++)
			{
				final AggroInfo info = array[i];
				final Character aggressor = info.getAggressor();
				aggressor.removeHate(this);
				pool.put(info);
			}
			
			aggroList.clear();
		}
		
		finally
		{
			aggroList.writeUnlock();
		}
		setAggroSorted(true);
	}
	
	/**
	 * Method decayMe.
	 * @param type int
	 */
	@Override
	public void decayMe(int type)
	{
		super.decayMe(type);
		clearAggroList();
	}
	
	@Override
	public void deleteMe()
	{
		final CharacterAI ai = getAI();
		
		if (ai != null)
		{
			ai.stopAITask();
		}
		
		super.deleteMe();
	}
	
	/**
	 * Method addCounter.
	 * @param attacker Character
	 */
	protected void addCounter(Character attacker)
	{
		World.addKilledNpc();
		
		if (attacker != null)
		{
			attacker.addPvECount();
			
			if (attacker.isPK() && attacker.isPlayer())
			{
				final Player player = attacker.getPlayer();
				player.clearKarma(this);
			}
		}
	}
	
	/**
	 * Method doDie.
	 * @param attacker Character
	 */
	@Override
	public void doDie(Character attacker)
	{
		addCounter(attacker);
		synchronized (this)
		{
			if (isSpawned())
			{
				calculateRewards(attacker);
			}
			
			super.doDie(attacker);
			deleteMe(DeleteCharacter.DEAD);
		}
		final Spawn spawn = getSpawn();
		
		if (spawn != null)
		{
			spawn.doDie(this);
		}
	}
	
	/**
	 * Method doOwerturn.
	 * @param attacker Character
	 */
	@Override
	public void doOwerturn(Character attacker)
	{
		if (isOwerturned())
		{
			return;
		}
		
		super.doOwerturn(attacker);
		final float radians = Angles.degreeToRadians(Angles.headingToDegree(heading) + 180);
		final NpcTemplate template = getTemplate();
		final int distance = template.getOwerturnDist();
		final float newX = Coords.calcX(x, distance, radians);
		final float newY = Coords.calcY(y, distance, radians);
		final GeoManager geoManager = GeoManager.getInstance();
		final float newZ = geoManager.getHeight(getContinentId(), newX, newY, getZ());
		setXYZ(newX, newY, newZ);
		owerturnTask.nextOwerturn(template.getOwerturnTime());
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
	}
	
	/**
	 * Method getAggro.
	 * @param aggressor Character
	 * @return long
	 */
	public long getAggro(Character aggressor)
	{
		final Array<AggroInfo> aggroList = getAggroList();
		aggroList.writeLock();
		
		try
		{
			final int index = aggroList.indexOf(aggressor);
			
			if (index < 0)
			{
				return -1;
			}
			
			final AggroInfo info = aggroList.get(index);
			return info.getAggro();
		}
		
		finally
		{
			aggroList.writeUnlock();
		}
	}
	
	/**
	 * Method getAggroInfoPool.
	 * @return FoldablePool<AggroInfo>
	 */
	protected FoldablePool<AggroInfo> getAggroInfoPool()
	{
		return aggroInfoPool;
	}
	
	/**
	 * Method getAggroList.
	 * @return Array<AggroInfo>
	 */
	public final Array<AggroInfo> getAggroList()
	{
		return aggroList;
	}
	
	/**
	 * Method getAggroRange.
	 * @return int
	 */
	public final int getAggroRange()
	{
		return getTemplate().getAggro();
	}
	
	/**
	 * Method getAI.
	 * @return CharacterAI
	 */
	@Override
	public final CharacterAI getAI()
	{
		return (CharacterAI) ai;
	}
	
	/**
	 * Method getAttack.
	 * @param attacked Character
	 * @param skill Skill
	 * @return int
	 */
	@Override
	public final int getAttack(Character attacked, Skill skill)
	{
		return (int) calcStat(StatType.ATTACK, getTemplate().getAttack(), attacked, skill);
	}
	
	/**
	 * Method getAutoEmotions.
	 * @return EmotionType[]
	 */
	@Override
	protected EmotionType[] getAutoEmotions()
	{
		return EmotionTask.MONSTER_TYPES;
	}
	
	/**
	 * Method getBalance.
	 * @param attacker Character
	 * @param skill Skill
	 * @return int
	 */
	@Override
	public final int getBalance(Character attacker, Skill skill)
	{
		return (int) calcStat(StatType.BALANCE, getTemplate().getBalance(), attacker, skill);
	}
	
	/**
	 * Method getDefense.
	 * @param attacker Character
	 * @param skill Skill
	 * @return int
	 */
	@Override
	public final int getDefense(Character attacker, Skill skill)
	{
		return (int) calcStat(StatType.DEFENSE, getTemplate().getDefense(), attacker, skill);
	}
	
	/**
	 * Method getEndHeading.
	 * @return int
	 */
	public final int getEndHeading()
	{
		return turnTask.getEndHeading();
	}
	
	/**
	 * Method getExp.
	 * @return int
	 */
	public final int getExp()
	{
		return getTemplate().getExp();
	}
	
	/**
	 * Method getFraction.
	 * @return String
	 */
	public final String getFraction()
	{
		return getTemplate().getFactionId();
	}
	
	/**
	 * Method getFractionRange.
	 * @return int
	 */
	public final int getFractionRange()
	{
		return getTemplate().getFactionRange();
	}
	
	/**
	 * Method getImpact.
	 * @param attacked Character
	 * @param skill Skill
	 * @return int
	 */
	@Override
	public final int getImpact(Character attacked, Skill skill)
	{
		return (int) calcStat(StatType.IMPACT, getTemplate().getImpact(), attacked, skill);
	}
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	@Override
	public int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * Method getLinks.
	 * @param player Player
	 * @return Array<Link>
	 */
	public final Array<Link> getLinks(Player player)
	{
		final NpcTemplate template = getTemplate();
		final LocalObjects local = LocalObjects.get();
		final Array<Link> links = local.getNextLinkList();
		final EventManager eventManager = EventManager.getInstance();
		eventManager.addLinks(links, this, player);
		final DialogData dialog = template.getDialog();
		
		if (dialog != null)
		{
			dialog.addLinks(links, this, player);
		}
		
		final QuestData quests = template.getQuests();
		quests.addLinks(links, this, player);
		return links;
	}
	
	/**
	 * Method getMinionLeader.
	 * @return MinionLeader
	 */
	public MinionLeader getMinionLeader()
	{
		return null;
	}
	
	/**
	 * Method getMostDamager.
	 * @return Character
	 */
	public Character getMostDamager()
	{
		final Array<AggroInfo> aggroList = getAggroList();
		
		if (aggroList.isEmpty())
		{
			return null;
		}
		
		Character top = null;
		aggroList.readLock();
		
		try
		{
			final AggroInfo[] array = aggroList.array();
			long damage = -1;
			
			for (int i = 0, length = aggroList.size(); i < length; i++)
			{
				final AggroInfo info = array[i];
				
				if (info == null)
				{
					continue;
				}
				
				if (info.getDamage() > damage)
				{
					top = info.getAggressor();
					damage = info.getDamage();
				}
			}
		}
		
		finally
		{
			aggroList.readUnlock();
		}
		return top;
	}
	
	/**
	 * Method getMostHated.
	 * @return Character
	 */
	public Character getMostHated()
	{
		final Array<AggroInfo> aggroList = getAggroList();
		
		if (aggroList.isEmpty())
		{
			return null;
		}
		
		if (!isAggroSorted())
		{
			aggroList.sort(AGGRO_COMPORATOR);
			setAggroSorted(true);
		}
		
		final AggroInfo top = aggroList.first();
		return top != null ? top.getAggressor() : null;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	@Override
	public final String getName()
	{
		return getTemplate().getName();
	}
	
	/**
	 * Method getNpc.
	 * @return Npc
	 */
	@Override
	public Npc getNpc()
	{
		return this;
	}
	
	/**
	 * Method getNpcType.
	 * @return NpcType
	 */
	public final NpcType getNpcType()
	{
		return getTemplate().getNpcType();
	}
	
	/**
	 * Method getOwerturnId.
	 * @return int
	 */
	@Override
	public int getOwerturnId()
	{
		return 0x482DEB16;
	}
	
	/**
	 * Method getRandomSkill.
	 * @param group SkillGroup
	 * @return Skill
	 */
	public Skill getRandomSkill(SkillGroup group)
	{
		final Skill[] list = skills[group.ordinal()];
		return (list == null) || (list.length < 1) ? null : list[Rnd.nextInt(0, list.length - 1)];
	}
	
	/**
	 * Method getFirstEnabledSkill.
	 * @param group SkillGroup
	 * @return Skill
	 */
	public Skill getFirstEnabledSkill(SkillGroup group)
	{
		final Skill[] array = skills[group.ordinal()];
		
		if (array.length > 0)
		{
			for (Skill skill : array)
			{
				if (!isSkillDisabled(skill))
				{
					return skill;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Method getSpawn.
	 * @return Spawn
	 */
	public final Spawn getSpawn()
	{
		return spawn;
	}
	
	/**
	 * Method getSpawnLoc.
	 * @return Location
	 */
	public final Location getSpawnLoc()
	{
		return spawnLoc;
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	@Override
	public final int getSubId()
	{
		return Config.SERVER_NPC_SUB_ID;
	}
	
	/**
	 * Method getTemplate.
	 * @return NpcTemplate
	 */
	@Override
	public final NpcTemplate getTemplate()
	{
		return (NpcTemplate) template;
	}
	
	/**
	 * Method hasDialog.
	 * @return boolean
	 */
	public final boolean hasDialog()
	{
		return getTemplate().getDialog() != null;
	}
	
	/**
	 * Method isAggressive.
	 * @return boolean
	 */
	public final boolean isAggressive()
	{
		return getTemplate().getAggro() > 0;
	}
	
	/**
	 * Method isAggroSorted.
	 * @return boolean
	 */
	public final boolean isAggroSorted()
	{
		return aggroSorted;
	}
	
	/**
	 * Method isFriendNpc.
	 * @return boolean
	 */
	public boolean isFriendNpc()
	{
		return false;
	}
	
	/**
	 * Method isGuard.
	 * @return boolean
	 */
	public boolean isGuard()
	{
		return false;
	}
	
	/**
	 * Method isMinion.
	 * @return boolean
	 */
	public boolean isMinion()
	{
		return false;
	}
	
	/**
	 * Method isMinionLeader.
	 * @return boolean
	 */
	public boolean isMinionLeader()
	{
		return false;
	}
	
	/**
	 * Method isMonster.
	 * @return boolean
	 */
	public boolean isMonster()
	{
		return false;
	}
	
	/**
	 * Method isNpc.
	 * @return boolean
	 */
	@Override
	public final boolean isNpc()
	{
		return true;
	}
	
	/**
	 * Method isRaidBoss.
	 * @return boolean
	 */
	public boolean isRaidBoss()
	{
		return false;
	}
	
	/**
	 * Method isTurner.
	 * @return boolean
	 */
	public boolean isTurner()
	{
		return turnTask.isTurner();
	}
	
	/**
	 * Method newAggroInfo.
	 * @param aggressor Character
	 * @param aggro long
	 * @param damage long
	 * @return AggroInfo
	 */
	protected AggroInfo newAggroInfo(Character aggressor, long aggro, long damage)
	{
		AggroInfo info = aggroInfoPool.take();
		
		if (info == null)
		{
			info = new AggroInfo();
		}
		
		info.setAggressor(aggressor);
		info.setAggro(aggro);
		info.setDamage(damage);
		return info;
	}
	
	/**
	 * Method newGeomCharacter.
	 * @return Geom
	 */
	@Override
	protected Geom newGeomCharacter()
	{
		final NpcTemplate template = getTemplate();
		return new NpcGeom(this, template.getGeomHeight(), template.getGeomRadius());
	}
	
	/**
	 * Method newRegenHp.
	 * @return Regen
	 */
	@Override
	protected Regen newRegenHp()
	{
		return new NpcRegenHp(this);
	}
	
	/**
	 * Method newRegenMp.
	 * @return Regen
	 */
	@Override
	protected Regen newRegenMp()
	{
		return new NpcRegenMp(this);
	}
	
	/**
	 * Method nextTurn.
	 * @param newHeading int
	 */
	public void nextTurn(int newHeading)
	{
		turnTask.nextTurn(newHeading);
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
		final IdFactory idFactory = IdFactory.getInstance();
		objectId = idFactory.getNextNpcId();
	}
	
	/**
	 * Method removeAggro.
	 * @param agressor Character
	 */
	public void removeAggro(Character agressor)
	{
		final Array<AggroInfo> aggroList = getAggroList();
		aggroList.writeLock();
		
		try
		{
			final int index = aggroList.indexOf(agressor);
			
			if (index >= 0)
			{
				final AggroInfo aggroInfo = aggroList.get(index);
				final long aggro = aggroInfo.getAggro();
				agressor.removeHate(this);
				aggroInfoPool.put(aggroInfo);
				aggroList.fastRemove(index);
				setAggroSorted(index != 0);
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyAgression(this, agressor, -aggro);
			}
		}
		
		finally
		{
			aggroList.writeUnlock();
		}
	}
	
	/**
	 * Method removeMe.
	 * @param player Player
	 * @param type int
	 */
	@Override
	public void removeMe(Player player, int type)
	{
		player.sendPacket(DeleteCharacter.getInstance(this, type), true);
	}
	
	/**
	 * Method setAggroSorted.
	 * @param aggroSorted boolean
	 */
	public final void setAggroSorted(boolean aggroSorted)
	{
		this.aggroSorted = aggroSorted;
	}
	
	/**
	 * Method setSpawn.
	 * @param spawn Spawn
	 */
	public final void setSpawn(Spawn spawn)
	{
		this.spawn = spawn;
	}
	
	/**
	 * Method setSpawnLoc.
	 * @param spawnLoc Location
	 */
	public final void setSpawnLoc(Location spawnLoc)
	{
		this.spawnLoc = spawnLoc;
	}
	
	@Override
	public void spawnMe()
	{
		super.spawnMe();
		World.addSpawnedNpc();
	}
	
	/**
	 * Method spawnMe.
	 * @param loc Location
	 */
	@Override
	public void spawnMe(Location loc)
	{
		setSpawnLoc(loc);
		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());
		super.spawnMe(loc);
		final WorldRegion region = getCurrentRegion();
		
		if ((region != null) && region.isActive())
		{
			getAI().startAITask();
			emotionTask.start();
		}
	}
	
	/**
	 * Method startBattleStance.
	 * @param enemy Character
	 * @return boolean
	 */
	@Override
	public boolean startBattleStance(Character enemy)
	{
		if (((enemy != null) && (enemy != getEnemy())) || ((enemy == null) && isBattleStanced()))
		{
			PacketManager.showBattleStance(this, enemy);
		}
		
		setBattleStanced(enemy != null);
		setEnemy(enemy);
		return true;
	}
	
	@Override
	public void stopBattleStance()
	{
		setBattleStanced(false);
		broadcastPacketToOthers(NpcNotice.getInstance(this, 0, 0));
	}
	
	/**
	 * Method subAggro.
	 * @param aggressor Character
	 * @param aggro long
	 */
	public void subAggro(Character aggressor, long aggro)
	{
		final Array<AggroInfo> aggroList = getAggroList();
		aggroList.writeLock();
		
		try
		{
			final int index = aggroList.indexOf(aggressor);
			
			if (index > -1)
			{
				final AggroInfo info = aggroList.get(index);
				info.subAggro(aggro);
				
				if (info.getAggro() < 1)
				{
					aggroList.fastRemove(index);
					aggressor.removeHate(this);
					aggroInfoPool.put(info);
				}
				
				setAggroSorted(index != 0);
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyAgression(this, aggressor, aggro * -1);
			}
		}
		
		finally
		{
			aggroList.writeUnlock();
		}
	}
	
	/**
	 * Method teleToLocation.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 */
	@Override
	public void teleToLocation(int continentId, float x, float y, float z, int heading)
	{
		decayMe(DeleteCharacter.DISAPPEARS);
		super.teleToLocation(continentId, x, y, z, heading);
		spawnMe(getSpawnLoc());
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "NpcInstance  id = " + getTemplateId() + ", type = " + getTemplateType();
	}
	
	@Override
	public void updateHp()
	{
		final TargetHp packet = TargetHp.getInstance(this, TargetHp.RED);
		final Array<AggroInfo> aggroList = getAggroList();
		aggroList.readLock();
		
		try
		{
			final AggroInfo[] array = aggroList.array();
			
			for (int i = 0, length = aggroList.size(); i < length; i++)
			{
				final Character aggressor = array[i].getAggressor();
				
				if ((aggressor != null) && (aggressor.isPlayer() || aggressor.isSummon()))
				{
					packet.increaseSends();
				}
			}
			
			for (int i = 0, length = aggroList.size(); i < length; i++)
			{
				final Character aggressor = array[i].getAggressor();
				
				if (aggressor != null)
				{
					if (aggressor.isPlayer())
					{
						aggressor.sendPacket(packet, false);
					}
					else if (aggressor.isSummon() && (aggressor.getOwner() != null))
					{
						aggressor.getOwner().sendPacket(packet, false);
					}
				}
			}
		}
		
		finally
		{
			aggroList.readUnlock();
		}
	}
	
	/**
	 * Method updateQuestInteresting.
	 * @param player Player
	 * @param delete boolean
	 */
	public void updateQuestInteresting(Player player, boolean delete)
	{
		if (player == null)
		{
			log.warning(this, new Exception("not found player"));
			return;
		}
		
		final QuestData quests = getTemplate().getQuests();
		final QuestType type = quests.hasQuests(this, player);
		
		if ((type == null) && delete)
		{
			player.sendPacket(QuestNpcNotice.getInstance(this, NpcIconType.NONE), true);
		}
		else if (type != null)
		{
			switch (type)
			{
				case STORY_QUEST:
					player.sendPacket(QuestNpcNotice.getInstance(this, NpcIconType.RED_NOTICE), true);
					break;
				
				case LEVEL_UP_QUEST:
				case ZONE_QUEST:
					player.sendPacket(QuestNpcNotice.getInstance(this, NpcIconType.YELLOW_NOTICE), true);
					break;
				
				case GUILD_QUEST:
					player.sendPacket(QuestNpcNotice.getInstance(this, NpcIconType.BLUE_NOTICE), true);
					break;
				
				case DEALY_QUEST:
					player.sendPacket(QuestNpcNotice.getInstance(this, NpcIconType.GREEN_NOTICE), true);
					break;
			}
		}
	}
	
	/**
	 * Method isFriend.
	 * @param player Player
	 * @return boolean
	 */
	public boolean isFriend(Player player)
	{
		return isFriendNpc();
	}
	
	/**
	 * Method getKarmaMod.
	 * @return int
	 */
	public int getKarmaMod()
	{
		return 1;
	}
	
	/**
	 * Method isOwerturnImmunity.
	 * @return boolean
	 */
	@Override
	public boolean isOwerturnImmunity()
	{
		return getTemplate().isOwerturnImmunity();
	}
	
	/**
	 * Method isInTurnFront.
	 * @param target Character
	 * @return boolean
	 */
	public boolean isInTurnFront(Character target)
	{
		if (target == null)
		{
			return false;
		}
		
		final float dx = target.getX() - getX();
		final float dy = target.getY() - getY();
		int head = (int) ((Math.atan2(-dy, -dx) * HEADINGS_IN_PI) + 32768);
		head = turnTask.getEndHeading() - head;
		
		if (head < 0)
		{
			head = (head + 1 + Integer.MAX_VALUE) & 0xFFFF;
		}
		else if (head > 0xFFFF)
		{
			head &= 0xFFFF;
		}
		
		return ((head != -1) && (head <= 8192)) || (head >= 57344);
	}
	
	/**
	 * Method getAppearance.
	 * @return NpcAppearance
	 */
	public NpcAppearance getAppearance()
	{
		return null;
	}
	
	/**
	 * Method getNameColor.
	 * @return int
	 */
	public int getNameColor()
	{
		return NameColor.COLOR_NORMAL;
	}
	
	public void finishDead()
	{
	}
	
	/**
	 * Method isBroadcastEndSkillForCollision.
	 * @return boolean
	 */
	@Override
	public boolean isBroadcastEndSkillForCollision()
	{
		return true;
	}
	
	/**
	 * Method getRoute.
	 * @return Location[]
	 */
	public Location[] getRoute()
	{
		return getSpawn().getRoute();
	}
}