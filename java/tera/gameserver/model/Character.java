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
package tera.gameserver.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import tera.Config;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.PacketManager;
import tera.gameserver.manager.RandomManager;
import tera.gameserver.model.ai.AI;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.geom.Geom;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.listeners.DamageListener;
import tera.gameserver.model.listeners.DieListener;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.regenerations.Regen;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Calculator;
import tera.gameserver.model.skillengine.ChanceType;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.model.skillengine.OperateType;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillName;
import tera.gameserver.model.skillengine.SkillType;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.gameserver.model.skillengine.funcs.chance.ChanceFunc;
import tera.gameserver.network.serverpackets.CharMove;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.Damage;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.tasks.EmotionTask;
import tera.gameserver.tasks.MoveNextTask;
import tera.gameserver.tasks.OwerturnTask;
import tera.gameserver.tasks.SkillCastTask;
import tera.gameserver.tasks.SkillMoveTask;
import tera.gameserver.tasks.SkillUseTask;
import tera.gameserver.templates.CharTemplate;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.SkillTemplate;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.concurrent.Locks;
import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.geom.Geometry;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Strings;
import rlib.util.Synchronized;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.random.Random;
import rlib.util.table.FuncValue;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;
import rlib.util.wraps.Wrap;

/**
 * @author Ronn
 */
public abstract class Character extends TObject implements Synchronized
{
	
	protected static final Logger log = Loggers.getLogger(Character.class);
	
	protected static final FuncValue<ReuseSkill> FUNC_REUSE_SKILL_FOLD = value -> value.fold();
	
	protected static final FuncValue<Wrap> FUNC_SKILL_VAR_FOLD = value -> value.fold();
	
	protected final Lock charLock;
	protected final Lock readStatLock;
	protected final Lock writeStatLock;
	
	protected final CharTemplate template;
	
	protected String name;
	
	protected String title;
	
	protected int castId;
	
	protected int chargeLevel;
	
	protected volatile int currentHp;
	
	protected volatile int currentMp;
	
	protected long lastCast;
	
	protected volatile boolean defenseStance;
	
	protected volatile boolean mounted;
	
	protected volatile boolean invul;
	
	protected volatile boolean moving;
	
	protected volatile boolean battleStanced;
	
	protected volatile boolean owerturned;
	
	protected volatile boolean stuned;
	
	protected volatile boolean rooted;
	
	protected volatile boolean skillMoved;
	
	protected volatile boolean skillBlocking;
	
	protected volatile boolean flyingPegas;
	
	protected volatile boolean spawned;
	
	protected AI ai;
	
	protected volatile TObject enemy;
	
	protected volatile Character target;
	
	protected volatile Summon summon;
	
	protected final Geom geom;
	
	protected final Regen regenHp;
	protected final Regen regenMp;
	
	protected final EmotionTask emotionTask;
	
	protected final MoveNextTask moveNextTask;
	
	protected final SkillUseTask skillUseTask;
	
	protected final SkillCastTask skillCastTask;
	
	protected final SkillMoveTask skillMoveTask;
	
	protected final OwerturnTask owerturnTask;
	
	protected final EffectList effectList;
	
	protected final Calculator[] calcs;
	
	protected final Table<IntKey, Skill> skills;
	
	protected final Table<IntKey, ReuseSkill> reuseSkills;
	
	protected final Array<Npc> hateList;
	
	protected final Array<ChanceFunc> chanceFuncs;
	
	protected final Array<DamageListener> damageListeners;
	
	protected final Array<DieListener> dieListeners;
	
	protected volatile Table<IntKey, Wrap> skillVariables;
	
	protected volatile Skill castingSkill;
	protected volatile Skill activateSkill;
	protected volatile Skill lockOnSkill;
	protected volatile Skill chargeSkill;
	
	protected SkillName lastSkillName;
	
	/**
	 * Constructor for Character.
	 * @param objectId int
	 * @param template CharTemplate
	 */
	public Character(int objectId, CharTemplate template)
	{
		super(objectId);
		this.template = template;
		currentHp = 1;
		currentMp = 1;
		heading = 0;
		name = Strings.EMPTY;
		title = Strings.EMPTY;
		geom = newGeomCharacter();
		regenHp = newRegenHp();
		regenMp = newRegenMp();
		calcs = new Calculator[StatType.SIZE];
		moveNextTask = new MoveNextTask(this);
		skillUseTask = new SkillUseTask(this);
		skillCastTask = new SkillCastTask(this);
		skillMoveTask = new SkillMoveTask(this);
		emotionTask = new EmotionTask(this, getAutoEmotions());
		owerturnTask = new OwerturnTask(this);
		final ReadWriteLock lock = Locks.newRWLock();
		readStatLock = lock.readLock();
		writeStatLock = lock.writeLock();
		charLock = Locks.newLock();
		effectList = EffectList.newInstance(this);
		hateList = Arrays.toConcurrentArray(Npc.class);
		chanceFuncs = Arrays.toConcurrentArray(ChanceFunc.class);
		damageListeners = Arrays.toConcurrentArray(DamageListener.class);
		dieListeners = Arrays.toConcurrentArray(DieListener.class);
		reuseSkills = Tables.newConcurrentIntegerTable();
		skills = Tables.newConcurrentIntegerTable();
		final Formulas formulas = Formulas.getInstance();
		formulas.addFuncsToNewCharacter(this);
		final Func[] funcs = template.getFuncs();
		
		for (Func func : funcs)
		{
			func.addFuncTo(this);
		}
	}
	
	/**
	 * Method abortCast.
	 * @param force boolean
	 */
	public void abortCast(boolean force)
	{
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			sendMessage("abort cast skill");
		}
		
		skillUseTask.cancel(force);
		skillCastTask.cancel(force);
		skillMoveTask.cancel(force);
		castingSkill = null;
		
		if (activateSkill != null)
		{
			activateSkill.endSkill(this, x, y, z, force);
			activateSkill = null;
		}
	}
	
	public void abortCollect()
	{
		log.warning(this, new Exception("unsupperted method."));
	}
	
	public void addAttackCounter()
	{
	}
	
	/**
	 * Method addChanceFunc.
	 * @param func ChanceFunc
	 */
	public void addChanceFunc(ChanceFunc func)
	{
		chanceFuncs.add(func);
	}
	
	/**
	 * Method addDamageListener.
	 * @param listener DamageListener
	 */
	public void addDamageListener(DamageListener listener)
	{
		damageListeners.add(listener);
	}
	
	public void addDefenseCounter()
	{
	}
	
	/**
	 * Method addDieListener.
	 * @param listener DieListener
	 */
	public void addDieListener(DieListener listener)
	{
		dieListeners.add(listener);
	}
	
	/**
	 * Method addEffect.
	 * @param effect Effect
	 */
	public final void addEffect(Effect effect)
	{
		if (!effectList.addEffect(effect))
		{
			effect.fold();
		}
		else
		{
			final ObjectEventManager manager = ObjectEventManager.getInstance();
			manager.notifyAppliedEffect(this, effect);
		}
	}
	
	/**
	 * Method addExp.
	 * @param added int
	 * @param object TObject
	 * @param creator String
	 */
	public void addExp(int added, TObject object, String creator)
	{
	}
	
	/**
	 * Method addHated.
	 * @param hated Npc
	 */
	public void addHated(Npc hated)
	{
		if (!hateList.contains(hated))
		{
			hateList.add(hated);
		}
	}
	
	/**
	 * Method addLockOnTarget.
	 * @param target Character
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean addLockOnTarget(Character target, Skill skill)
	{
		return false;
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		moveNextTask.update(player);
	}
	
	public void addPvECount()
	{
	}
	
	public void addPvPCount()
	{
	}
	
	/**
	 * Method addSkill.
	 * @param skill Skill
	 * @param sendPacket boolean
	 * @return boolean
	 */
	public boolean addSkill(Skill skill, boolean sendPacket)
	{
		if (skill == null)
		{
			return false;
		}
		
		final Table<IntKey, Skill> current = getSkills();
		
		if (current.containsKey(skill.getId()))
		{
			return false;
		}
		
		if (!skill.isToggle())
		{
			skill.getTemplate().addPassiveFuncs(this);
		}
		
		current.put(skill.getId(), skill);
		return true;
	}
	
	/**
	 * Method addSkill.
	 * @param template SkillTemplate
	 * @param sendPacket boolean
	 * @return boolean
	 */
	public boolean addSkill(SkillTemplate template, boolean sendPacket)
	{
		if (template == null)
		{
			return false;
		}
		
		final Table<IntKey, Skill> current = getSkills();
		
		if (current.containsKey(template.getId()))
		{
			return false;
		}
		
		final Skill skill = template.newInstance();
		
		if (!skill.isToggle())
		{
			template.addPassiveFuncs(this);
		}
		
		current.put(skill.getId(), skill);
		return true;
	}
	
	/**
	 * Method addSkills.
	 * @param skills Skill[]
	 * @param sendPacket boolean
	 * @return boolean
	 */
	public boolean addSkills(Skill[] skills, boolean sendPacket)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return false;
		}
		
		for (Skill skill : skills)
		{
			addSkill(skill, false);
		}
		
		return true;
	}
	
	/**
	 * Method addSkills.
	 * @param templates SkillTemplate[]
	 * @param sendPacket boolean
	 * @return boolean
	 */
	public boolean addSkills(SkillTemplate[] templates, boolean sendPacket)
	{
		if ((templates == null) || (templates.length == 0))
		{
			return false;
		}
		
		final Table<IntKey, Skill> current = getSkills();
		
		for (SkillTemplate template : templates)
		{
			if (current.containsKey(template.getId()))
			{
				continue;
			}
			
			final Skill skill = template.newInstance();
			
			if (!skill.isToggle())
			{
				template.addPassiveFuncs(this);
			}
			
			current.put(skill.getId(), skill);
			final DataBaseManager dbManager = DataBaseManager.getInstance();
			dbManager.createSkill(this, skill);
		}
		
		return true;
	}
	
	/**
	 * Method addStatFunc.
	 * @param func StatFunc
	 */
	public final void addStatFunc(StatFunc func)
	{
		if (func == null)
		{
			return;
		}
		
		final int ordinal = func.getStat().ordinal();
		writeStatLock.lock();
		
		try
		{
			Calculator calc = calcs[ordinal];
			
			if (calc == null)
			{
				calc = new Calculator();
				calcs[ordinal] = calc;
			}
			
			calc.addFunc(func);
		}
		
		finally
		{
			writeStatLock.unlock();
		}
	}
	
	/**
	 * Method addStatFuncs.
	 * @param funcs StatFunc[]
	 */
	public final void addStatFuncs(StatFunc[] funcs)
	{
		writeStatLock.lock();
		
		try
		{
			for (StatFunc func : funcs)
			{
				if (func == null)
				{
					continue;
				}
				
				final int ordinal = func.getStat().ordinal();
				Calculator calc = calcs[ordinal];
				
				if (calc == null)
				{
					calc = new Calculator();
					calcs[ordinal] = calc;
				}
				
				calc.addFunc(func);
			}
		}
		
		finally
		{
			writeStatLock.unlock();
		}
	}
	
	/**
	 * Method addVisibleObject.
	 * @param object TObject
	 */
	public void addVisibleObject(TObject object)
	{
	}
	
	/**
	 * Method applyChanceFunc.
	 * @param type ChanceType
	 * @param target Character
	 * @param skill Skill
	 */
	public void applyChanceFunc(ChanceType type, Character target, Skill skill)
	{
		if (chanceFuncs.isEmpty())
		{
			return;
		}
		
		chanceFuncs.readLock();
		
		try
		{
			final ChanceFunc[] array = chanceFuncs.array();
			
			for (int i = 0, length = chanceFuncs.size(); i < length; i++)
			{
				final ChanceFunc func = array[i];
				
				if ((type == ChanceType.ON_ATTACK) && !func.isOnAttack())
				{
					continue;
				}
				else if ((type == ChanceType.ON_ATTACKED) && !func.isOnAttacked())
				{
					continue;
				}
				else if ((type == ChanceType.ON_OWERTURNED) && !func.isOnOwerturned())
				{
					continue;
				}
				else if ((type == ChanceType.ON_SHIELD_BLOCK) && !func.isOnShieldBlocked())
				{
					continue;
				}
				else if ((type == ChanceType.ON_OWERTURN) && !func.isOnOwerturn())
				{
					continue;
				}
				else if ((type == ChanceType.ON_CRIT_ATTACK) && !func.isOnCritAttack())
				{
					continue;
				}
				else if ((type == ChanceType.ON_CRIT_ATTACKED) && !func.isOnCritAttacked())
				{
					continue;
				}
				
				final RandomManager randManager = RandomManager.getInstance();
				final Random rand = randManager.getFuncRandom();
				
				if (!rand.chance(func.getChance()))
				{
					continue;
				}
				
				func.apply(this, target, skill);
			}
		}
		
		finally
		{
			chanceFuncs.readUnlock();
		}
	}
	
	/**
	 * Method broadcastMove.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param sendSelfPacket boolean
	 */
	public void broadcastMove(float x, float y, float z, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean sendSelfPacket)
	{
		broadcastPacket(getMovePacket(x, y, z, heading, type, targetX, targetY, targetZ));
	}
	
	/**
	 * Method broadcastPacket.
	 * @param packet ServerPacket
	 */
	public void broadcastPacket(ServerPacket packet)
	{
		broadcastPacketToOthers(packet);
	}
	
	/**
	 * Method broadcastPacketToOthers.
	 * @param packet ServerPacket
	 */
	public final void broadcastPacketToOthers(ServerPacket packet)
	{
		final WorldRegion region = getCurrentRegion();
		
		if (region == null)
		{
			return;
		}
		
		final WorldRegion[] regions = region.getNeighbors();
		packet.increaseSends();
		
		for (WorldRegion region2 : regions)
		{
			region2.sendPacket(this, packet);
		}
		
		packet.complete();
	}
	
	/**
	 * Method calcHeading.
	 * @param targetX float
	 * @param targetY float
	 * @return int
	 */
	public final int calcHeading(float targetX, float targetY)
	{
		return (int) (Math.atan2(y - targetY, x - targetX) * HEADINGS_IN_PI) + 32768;
	}
	
	/**
	 * Method calcHeading.
	 * @param loc Location
	 * @return int
	 */
	public final int calcHeading(Location loc)
	{
		return loc == null ? 0 : calcHeading(loc.getX(), loc.getY());
	}
	
	/**
	 * Method calcStat.
	 * @param stat StatType
	 * @param init int
	 * @param target Character
	 * @param skill Skill
	 * @return float
	 */
	public final float calcStat(StatType stat, int init, Character target, Skill skill)
	{
		if (stat == null)
		{
			return init;
		}
		
		readStatLock.lock();
		
		try
		{
			final Calculator calc = calcs[stat.ordinal()];
			
			if ((calc == null) || (calc.size() == 0))
			{
				return init;
			}
			
			return calc.calc(this, target, skill, init);
		}
		
		finally
		{
			readStatLock.unlock();
		}
	}
	
	/**
	 * Method calcStat.
	 * @param stat StatType
	 * @param init int
	 * @param order int
	 * @param target Character
	 * @param skill Skill
	 * @return float
	 */
	public final float calcStat(StatType stat, int init, int order, Character target, Skill skill)
	{
		readStatLock.lock();
		
		try
		{
			final Calculator calc = calcs[stat.ordinal()];
			
			if ((calc == null) || (calc.size() == 0))
			{
				return init;
			}
			
			return calc.calcToOrder(this, target, skill, init, order);
		}
		
		finally
		{
			readStatLock.unlock();
		}
	}
	
	public void cancelOwerturn()
	{
		boolean send = false;
		
		if (isOwerturned())
		{
			synchronized (this)
			{
				if (isOwerturned())
				{
					setOwerturned(false);
					send = true;
				}
			}
		}
		
		if (send)
		{
			PacketManager.showCharacterOwerturn(this);
		}
	}
	
	/**
	 * Method causingDamage.
	 * @param skill Skill
	 * @param info AttackInfo
	 * @param attacker Character
	 */
	public void causingDamage(Skill skill, AttackInfo info, Character attacker)
	{
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			sayMessage("id = " + getTemplateId() + ", class = " + getClass().getSimpleName());
		}
		
		if (isDead())
		{
			return;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		if (!attacker.isAttacking())
		{
			attacker.setAttacking(true);
			attacker.addAttackCounter();
		}
		
		if (isDefenseStance() && info.isBlocked())
		{
			PacketManager.showShieldBlocked(this);
			eventManager.notifyShieldBlocked(this, attacker, skill);
		}
		
		int abs = (int) attacker.calcStat(StatType.ATTACK_ABSORPTION_MP, 0, this, skill);
		
		if (abs > 0)
		{
			attacker.setCurrentMp(attacker.getCurrentMp() + abs);
			eventManager.notifyMpChanged(attacker);
		}
		
		int type = Damage.DAMAGE;
		final int damage = info.getDamage();
		boolean hpChanged = false;
		boolean mpChanged = false;
		charLock.lock();
		
		try
		{
			if (!info.isBlocked())
			{
				if (damage > 0)
				{
					setCurrentHp(getCurrentHp() - damage);
					hpChanged = true;
				}
			}
			
			abs = (int) calcStat(StatType.DEFENSE_ABSORPTION_MP, 0, attacker, skill);
			
			if (abs > 0)
			{
				setCurrentMp(getCurrentMp() + abs);
				mpChanged = true;
			}
			
			if (info.isBlocked() || (damage < 1))
			{
				type = Damage.BLOCK;
			}
		}
		
		finally
		{
			charLock.unlock();
		}
		
		if (info.isOwerturn())
		{
			doOwerturn(attacker);
		}
		
		PacketManager.showDamage(attacker, this, info, skill, type);
		eventManager.notifyAttacked(this, attacker, skill, damage, info.isCrit());
		eventManager.notifyAttack(attacker, this, skill, damage, info.isCrit());
		
		if (hpChanged)
		{
			eventManager.notifyHpChanged(this);
		}
		
		if (mpChanged)
		{
			eventManager.notifyMpChanged(this);
		}
		
		updateDefense();
		
		if (isDead())
		{
			doDie(attacker);
		}
	}
	
	/**
	 * Method causingManaDamage.
	 * @param skill Skill
	 * @param info AttackInfo
	 * @param attacker Character
	 */
	public void causingManaDamage(Skill skill, AttackInfo info, Character attacker)
	{
	}
	
	/**
	 * Method checkBarriers.
	 * @param barriers Array<Character>
	 * @param distance int
	 * @param radians float
	 * @return boolean
	 */
	public final boolean checkBarriers(Array<Character> barriers, int distance, float radians)
	{
		if (barriers.isEmpty())
		{
			return true;
		}
		
		final Character[] array = barriers.array();
		final float newX = Coords.calcX(x, distance, radians);
		final float newY = Coords.calcY(y, distance, radians);
		final boolean isNpc = isNpc();
		
		for (int i = 0, length = barriers.size(); i < length; i++)
		{
			final Character target = array[i];
			
			if ((target == null) || target.isDead() || (isNpc && !checkTarget(target)) || ((Geometry.getDistanceToLine(x, y, newX, newY, target.getX(), target.getY()) - (target.getGeomRadius() + getGeomRadius())) > 10))
			{
				continue;
			}
			
			if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
			{
				Location[] locs = Coords.circularCoords(Location.class, target.getX(), target.getY(), target.getZ(), (int) target.getGeomRadius(), 10);
				final ItemTable itemTable = ItemTable.getInstance();
				ItemTemplate template = itemTable.getItem(125);
				
				for (int g = 0; g < 10; g++)
				{
					final ItemInstance item = template.newInstance();
					item.setItemCount(1);
					item.setTempOwner(this);
					final Location loc = locs[g];
					loc.setContinentId(continentId);
					item.spawnMe(loc);
				}
				
				locs = Coords.circularCoords(Location.class, x, y, z, (int) getGeomRadius(), 10);
				template = itemTable.getItem(127);
				
				for (int g = 0; g < 10; g++)
				{
					final ItemInstance item = template.newInstance();
					item.setItemCount(1);
					item.setTempOwner(this);
					final Location loc = locs[g];
					loc.setContinentId(continentId);
					item.spawnMe(loc);
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method checkTarget.
	 * @param target Character
	 * @return boolean
	 */
	public boolean checkTarget(Character target)
	{
		return true;
	}
	
	/**
	 * Method decayMe.
	 * @param type int
	 */
	@Override
	public void decayMe(int type)
	{
		super.decayMe(type);
		spawned = false;
	}
	
	@Override
	public void deleteMe()
	{
		if (isDeleted())
		{
			return;
		}
		
		final Array<Npc> hateList = getHateList();
		
		if (!hateList.isEmpty())
		{
			final Npc[] array = hateList.array();
			
			for (int i = 0, length = hateList.size(); i < length; i++)
			{
				final Npc npc = array[i];
				
				if (npc == null)
				{
					continue;
				}
				
				npc.removeAggro(this);
				i--;
				length--;
			}
			
			hateList.clear();
		}
		
		effectList.clear();
		final Table<IntKey, ReuseSkill> reuseSkills = getReuseSkills();
		
		if (!reuseSkills.isEmpty())
		{
			reuseSkills.apply(FUNC_REUSE_SKILL_FOLD);
			reuseSkills.clear();
		}
		
		final Table<IntKey, Wrap> skillVariables = getSkillVariables();
		
		if (skillVariables != null)
		{
			skillVariables.apply(FUNC_SKILL_VAR_FOLD);
			skillVariables.clear();
		}
		
		emotionTask.stop();
		moveNextTask.stopTask();
		super.deleteMe();
	}
	
	/**
	 * Method disableItem.
	 * @param skill Skill
	 * @param item ItemInstance
	 * @return boolean
	 */
	public boolean disableItem(Skill skill, ItemInstance item)
	{
		final int reuseDelay = skill.getReuseDelay(this);
		
		if (reuseDelay < 1)
		{
			return false;
		}
		
		final ReuseSkill reuse = reuseSkills.get(skill.getReuseId());
		
		if (reuse == null)
		{
			reuseSkills.put(skill.getReuseId(), ReuseSkill.newInstance(skill.getReuseId(), reuseDelay).setItemId(item.getItemId()));
		}
		else
		{
			reuse.setEndTime(System.currentTimeMillis() + reuseDelay);
		}
		
		return true;
	}
	
	/**
	 * Method disableSkill.
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean disableSkill(Skill skill)
	{
		int reuseDelay = skill.getReuseDelay(this);
		
		if (reuseDelay < 1)
		{
			return false;
		}
		
		final Table<IntKey, ReuseSkill> reuseSkills = getReuseSkills();
		ReuseSkill reuse = reuseSkills.get(skill.getReuseId());
		
		if (reuse == null)
		{
			reuseSkills.put(skill.getReuseId(), ReuseSkill.newInstance(skill.getReuseId(), reuseDelay));
		}
		else
		{
			reuse.setEndTime(System.currentTimeMillis() + reuseDelay);
		}
		
		updateReuse(skill, reuseDelay);
		final int[] reuseIds = skill.getReuseIds();
		
		if (reuseIds != null)
		{
			for (int id : reuseIds)
			{
				skill = skills.get(id);
				
				if ((skill == null) || isSkillDisabled(skill))
				{
					continue;
				}
				
				reuseDelay = skill.getReuseDelay(this);
				
				if (reuseDelay < 1)
				{
					continue;
				}
				
				reuse = reuseSkills.get(id);
				
				if (reuse == null)
				{
					reuseSkills.put(id, ReuseSkill.newInstance(id, reuseDelay));
				}
				else
				{
					reuse.setEndTime(System.currentTimeMillis() + reuseDelay);
				}
				
				updateReuse(skill, reuseDelay);
			}
		}
		
		return true;
	}
	
	/**
	 * Method doCast.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param skill Skill
	 * @param state int
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	public void doCast(float startX, float startY, float startZ, Skill skill, int state, int heading, float targetX, float targetY, float targetZ)
	{
		doCast(startX, startY, startZ, updateResultSkill(skill), state, heading, targetX, targetY, targetZ, null);
	}
	
	/**
	 * Method updateResultSkill.
	 * @param skill Skill
	 * @return Skill
	 */
	protected Skill updateResultSkill(Skill skill)
	{
		if (skill.isHasFast() && ((System.currentTimeMillis() - lastCast) < 1500) && skill.hasPrevSkillName(lastSkillName))
		{
			final Skill fast = getSkill(skill.getId() + 1);
			
			if (fast != null)
			{
				skill = fast;
			}
		}
		
		return skill;
	}
	
	/**
	 * Method doCast.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param skill Skill
	 * @param state int
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param item ItemInstance
	 */
	public void doCast(float startX, float startY, float startZ, Skill skill, int state, int heading, final float targetX, final float targetY, final float targetZ, ItemInstance item)
	{
		if (skill.isPassive())
		{
			return;
		}
		
		if (Config.DEVELOPER_DEBUG_CASTING_SKILL)
		{
			sendMessage(" skill id " + skill.getId() + ", delay = " + skill.getDelay());
		}
		
		final OperateType operateType = skill.getOperateType();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		switch (operateType)
		{
			case LOCK_ON:
			case ACTIVE:
			{
				if (!skill.checkCondition(this, targetX, targetY, targetZ))
				{
					return;
				}
				
				final Skill castingSkill = getCastingSkill();
				
				if (castingSkill != null)
				{
					if (castingSkill.getOperateType() == OperateType.LOCK_ON)
					{
						abortCast(false);
					}
					else if (skill.isForceCast())
					{
						abortCast(true);
						skill = updateResultSkill(skill);
					}
					else
					{
						return;
					}
				}
				
				final Skill activateSkill = getActivateSkill();
				
				if (activateSkill != null)
				{
					if (skill.isForceCast())
					{
						abortCast(false);
					}
					else
					{
						return;
					}
				}
				
				if (skill.isBlockingMove())
				{
					stopMove();
				}
				
				if (operateType == OperateType.LOCK_ON)
				{
					final Array<Character> targets = getLockOnTargets();
					
					if (targets != null)
					{
						targets.clear();
					}
				}
				
				setXYZ(startX, startY, startZ);
				setHeading(heading + skill.getCastHeading());
				skill.startSkill(this, targetX, targetY, targetZ);
				skillMoveTask.nextTask(skill, targetX, targetY, targetZ);
				eventManager.notifyStartCasting(this, skill);
				disableSkill(skill);
				
				if (skill.isCastToMove())
				{
					skillUseTask.setTarget(targetX, targetY, targetZ);
				}
				else
				{
					skillUseTask.nextUse(skill, targetX, targetY, targetZ);
				}
				
				if (skill.getMpConsume() > 0)
				{
					final int resultMp = getCurrentMp() - skill.getMpConsume();
					setCurrentMp(resultMp);
					eventManager.notifyMpChanged(this);
				}
				
				if (skill.getHpConsume() > 0)
				{
					final int resultHp = getCurrentHp() - skill.getHpConsume();
					setCurrentHp(resultHp);
					eventManager.notifyHpChanged(this);
				}
				
				if (skill.getItemIdConsume() != 0)
				{
					getInventory().removeItem(skill.getItemIdConsume(), skill.getItemCountConsume());
					eventManager.notifyInventoryChanged(this);
					PacketManager.showUseItem(this, skill.getItemIdConsume(), (int) skill.getItemCountConsume());
				}
				
				skillCastTask.nextTask(skill, targetX, targetY, targetZ);
				final int chargetLevel = getChargeLevel();
				
				if (chargetLevel > 0)
				{
					setChargeLevel(0);
					setChargeSkill(null);
				}
				
				break;
			}
			
			case ACTIVATE:
			{
				final Skill activateSkill = getActivateSkill();
				
				if ((activateSkill != null) && (state == 0) && skill.isCanceable())
				{
					skillUseTask.cancel(false);
					
					if (skillCastTask.isRunning())
					{
						skillCastTask.cancel(false);
					}
					else
					{
						activateSkill.endSkill(this, x, y, z, true);
						eventManager.notifyFinishCasting(this, activateSkill);
					}
					
					setActivateSkill(null);
				}
				else if ((activateSkill == null) && (state == 1))
				{
					if (!skill.checkCondition(this, targetX, targetY, targetZ))
					{
						return;
					}
					
					if (isCastingNow())
					{
						abortCast(true);
					}
					
					setActivateSkill(skill);
					
					if (skill.isBlockingMove())
					{
						stopMove();
					}
					
					setXYZ(startX, startY, startZ);
					setHeading(heading);
					eventManager.notifyStartCasting(this, skill);
					disableSkill(skill);
					skill.startSkill(this, targetX, targetY, targetZ);
					
					if (skill.getHitTime() != 0)
					{
						skillUseTask.nextUse(skill, targetX, targetY, targetZ);
						skillCastTask.nextTask(skill, targetX, targetY, targetZ);
					}
					
					if (skill.getMpConsume() > 0)
					{
						final int resultMp = getCurrentMp() - skill.getMpConsume();
						setCurrentMp(resultMp);
						eventManager.notifyMpChanged(this);
					}
					
					if (skill.getHpConsume() > 0)
					{
						final int resultHp = getCurrentHp() - skill.getHpConsume();
						setCurrentHp(resultHp);
						eventManager.notifyHpChanged(this);
					}
					
					if (skill.getItemIdConsume() != 0)
					{
						getInventory().removeItem(skill.getItemIdConsume(), skill.getItemCountConsume());
						eventManager.notifyInventoryChanged(this);
						PacketManager.showUseItem(this, skill.getItemIdConsume(), (int) skill.getItemCountConsume());
					}
				}
				
				final int chargetLevel = getChargeLevel();
				
				if (chargetLevel > 0)
				{
					setChargeLevel(0);
					setChargeSkill(null);
				}
				
				break;
			}
			
			case CAST_ITEM:
			{
				if (!skill.checkCondition(this, targetX, targetY, targetZ))
				{
					return;
				}
				
				if (isCastingNow() && !skill.isAltCast())
				{
					return;
				}
				
				if (skill.isBlockingMove())
				{
					stopMove();
				}
				
				setHeading(heading);
				
				if (!skill.isAltCast())
				{
					skill.startSkill(this, targetX, targetY, targetZ);
				}
				
				disableItem(skill, item);
				
				if (skill.isAltCast())
				{
					skill.useSkill(Character.this, targetX, targetY, targetZ);
				}
				else
				{
					skillUseTask.nextUse(skill, targetX, targetY, targetZ);
				}
				
				if (skill.getMpConsume() > 0)
				{
					final int resultMp = getCurrentMp() - skill.getMpConsume();
					setCurrentMp(resultMp);
					eventManager.notifyMpChanged(this);
				}
				
				if (skill.getHpConsume() > 0)
				{
					final int resultHp = getCurrentHp() - skill.getHpConsume();
					setCurrentHp(resultHp);
					eventManager.notifyHpChanged(this);
				}
				
				if (skill.getItemIdConsume() != 0)
				{
					getInventory().removeItem(skill.getItemIdConsume(), skill.getItemCountConsume());
					eventManager.notifyInventoryChanged(this);
					final SystemMessage packet = SystemMessage.getInstance(MessageType.ITEM_USE).addItem(skill.getItemIdConsume(), (int) skill.getItemCountConsume());
					sendPacket(packet, true);
				}
				
				if (!skill.isAltCast())
				{
					skillCastTask.nextTask(skill, targetX, targetY, targetZ);
				}
				
				break;
			}
			
			case CHARGE:
			{
				final Skill castingSkill = getCastingSkill();
				
				if (state == 1)
				{
					if (!skill.checkCondition(this, targetX, targetY, targetZ))
					{
						return;
					}
					
					if (castingSkill != null)
					{
						if (castingSkill.getOperateType() == OperateType.LOCK_ON)
						{
							abortCast(false);
						}
						else if (skill.isForceCast())
						{
							abortCast(true);
						}
						else
						{
							return;
						}
					}
					
					stopMove();
					setXYZ(startX, startY, startZ);
					setHeading(heading);
					skill.startSkill(this, targetX, targetY, targetZ);
					skillMoveTask.nextTask(skill, targetX, targetY, targetZ);
					eventManager.notifyStartCasting(this, skill);
					
					if (skill.isCastToMove())
					{
						skillUseTask.setTarget(targetX, targetY, targetZ);
					}
					else
					{
						skillUseTask.nextUse(skill, targetX, targetY, targetZ);
					}
					
					skillConsume(skill);
					skillCastTask.nextTask(skill, targetX, targetY, targetZ);
					break;
				}
				
				if ((castingSkill != null) && (castingSkill.getSkillType() == SkillType.CHARGE))
				{
					abortCast(false);
				}
				
				break;
			}
			
			default:
				break;
		}
	}
	
	/**
	 * Method skillConsume.
	 * @param skill Skill
	 */
	protected void skillConsume(Skill skill)
	{
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		if (skill.getMpConsume() > 0)
		{
			final int resultMp = getCurrentMp() - skill.getMpConsume();
			setCurrentMp(resultMp);
			eventManager.notifyMpChanged(this);
		}
		
		if (skill.getHpConsume() > 0)
		{
			final int resultHp = getCurrentHp() - skill.getHpConsume();
			setCurrentHp(resultHp);
			eventManager.notifyHpChanged(this);
		}
		
		if (skill.getItemIdConsume() != 0)
		{
			getInventory().removeItem(skill.getItemIdConsume(), skill.getItemCountConsume());
			eventManager.notifyInventoryChanged(this);
			final SystemMessage packet = SystemMessage.getInstance(MessageType.ITEM_USE).addItem(skill.getItemIdConsume(), (int) skill.getItemCountConsume());
			sendPacket(packet, true);
		}
	}
	
	/**
	 * Method doCast.
	 * @param skill Skill
	 * @param heading int
	 * @param item ItemInstance
	 */
	public void doCast(Skill skill, int heading, ItemInstance item)
	{
		doCast(x, y, z, skill, 0, heading, x, y, z, item);
	}
	
	/**
	 * Method doCollect.
	 * @param resourse ResourseInstance
	 */
	public void doCollect(ResourseInstance resourse)
	{
		log.warning(this, new Exception("unsupperted method."));
	}
	
	/**
	 * Method doDie.
	 * @param killer Character
	 */
	public void doDie(Character killer)
	{
		abortCast(true);
		stopMove();
		stopBattleStance();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyDead(this, killer);
		
		if (!dieListeners.isEmpty())
		{
			dieListeners.readLock();
			
			try
			{
				final DieListener[] array = dieListeners.array();
				
				for (int i = 0, length = dieListeners.size(); i < length; i++)
				{
					array[i].onDie(killer, this);
				}
			}
			
			finally
			{
				dieListeners.readUnlock();
			}
		}
	}
	
	/**
	 * Method doFall.
	 * @param startZ float
	 * @param endZ float
	 * @return int
	 */
	public int doFall(float startZ, float endZ)
	{
		final int damage = (int) Math.abs(startZ - endZ) * 7;
		
		if (damage > 0)
		{
			setCurrentHp(Math.max(2, currentHp - damage));
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyHpChanged(this);
		}
		
		return damage;
	}
	
	/**
	 * Method doOwerturn.
	 * @param attacker Character
	 */
	public void doOwerturn(Character attacker)
	{
		effectList.exitNoOwerturnEffects();
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyOwerturn(attacker, this, null);
		eventManager.notifyOwerturned(this, attacker, null);
		setOwerturned(true);
		abortCast(true);
		stopMove();
		setHeading(calcHeading(attacker.getX(), attacker.getY()));
	}
	
	public void doRegen()
	{
		if (isDead())
		{
			return;
		}
		
		if (regenHp.checkCondition())
		{
			regenHp.doRegen();
		}
		
		if (regenMp.checkCondition())
		{
			regenMp.doRegen();
		}
	}
	
	/**
	 * Method effectHealHp.
	 * @param heal int
	 * @param healer Character
	 */
	public void effectHealHp(int heal, Character healer)
	{
		if (isDead() || (heal < 1))
		{
			return;
		}
		
		charLock.lock();
		
		try
		{
			setCurrentHp(getCurrentHp() + heal);
		}
		
		finally
		{
			charLock.unlock();
		}
	}
	
	/**
	 * Method effectHealMp.
	 * @param heal int
	 * @param healer Character
	 */
	public void effectHealMp(int heal, Character healer)
	{
		if (isDead() || (heal < 1))
		{
			return;
		}
		
		charLock.lock();
		
		try
		{
			setCurrentMp(getCurrentMp() + heal);
		}
		
		finally
		{
			charLock.unlock();
		}
	}
	
	/**
	 * Method enableSkill.
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean enableSkill(Skill skill)
	{
		if (skill == null)
		{
			return false;
		}
		
		final Table<IntKey, ReuseSkill> reuseSkills = getReuseSkills();
		final ReuseSkill reuse = reuseSkills.get(skill.getReuseId());
		
		if (reuse != null)
		{
			reuse.setEndTime(0);
		}
		
		updateReuse(skill, 0);
		return true;
	}
	
	/**
	 * Method getActivateSkill.
	 * @return Skill
	 */
	public final Skill getActivateSkill()
	{
		return activateSkill;
	}
	
	/**
	 * Method getAI.
	 * @return AI
	 */
	@Override
	public AI getAI()
	{
		return ai;
	}
	
	/**
	 * Method getAtkSpd.
	 * @return int
	 */
	public final int getAtkSpd()
	{
		if (Config.DEVELOPER_FORCE_ATTACK_SPEED > 0)
		{
			return Config.DEVELOPER_FORCE_ATTACK_SPEED;
		}
		
		return (int) calcStat(StatType.ATTACK_SPEED, template.getAtkSpd(), null, null);
	}
	
	/**
	 * Method getAttack.
	 * @param attacked Character
	 * @param skill Skill
	 * @return int
	 */
	public int getAttack(Character attacked, Skill skill)
	{
		return (int) calcStat(StatType.ATTACK, 0, attacked, skill);
	}
	
	/**
	 * Method getAutoEmotions.
	 * @return EmotionType[]
	 */
	protected EmotionType[] getAutoEmotions()
	{
		return Arrays.toGenericArray();
	}
	
	/**
	 * Method getBalance.
	 * @param attacker Character
	 * @param skill Skill
	 * @return int
	 */
	public int getBalance(Character attacker, Skill skill)
	{
		return (int) calcStat(StatType.BALANCE, 0, attacker, skill);
	}
	
	/**
	 * Method getBalanceFactor.
	 * @return int
	 */
	public final int getBalanceFactor()
	{
		return (int) calcStat(StatType.BALANCE_FACTOR, template.getBalanceFactor(), this, null);
	}
	
	/**
	 * Method getBank.
	 * @return Bank
	 */
	public Bank getBank()
	{
		return null;
	}
	
	/**
	 * Method getBaseMaxHp.
	 * @return int
	 */
	public final int getBaseMaxHp()
	{
		return (int) calcStat(StatType.MAX_HP, template.getMaxHp(), 0x20, null, null);
	}
	
	/**
	 * Method getBaseMaxMp.
	 * @return int
	 */
	public final int getBaseMaxMp()
	{
		return (int) calcStat(StatType.MAX_MP, template.getMaxMp(), 0x40, null, null);
	}
	
	/**
	 * Method getCalcs.
	 * @return Calculator[]
	 */
	public final Calculator[] getCalcs()
	{
		return calcs;
	}
	
	/**
	 * Method getCastId.
	 * @return int
	 */
	public final int getCastId()
	{
		return castId;
	}
	
	/**
	 * Method getCastingSkill.
	 * @return Skill
	 */
	public final Skill getCastingSkill()
	{
		return castingSkill;
	}
	
	/**
	 * Method getCharacter.
	 * @return Character
	 */
	@Override
	public final Character getCharacter()
	{
		return this;
	}
	
	/**
	 * Method getChargeLevel.
	 * @return int
	 */
	public final int getChargeLevel()
	{
		return chargeLevel;
	}
	
	/**
	 * Method getClassId.
	 * @return int
	 */
	public int getClassId()
	{
		return -1;
	}
	
	/**
	 * Method getCritDamage.
	 * @param attacker Character
	 * @param skill Skill
	 * @return float
	 */
	public final float getCritDamage(Character attacker, Skill skill)
	{
		return calcStat(StatType.CRITICAL_DAMAGE, 2, attacker, skill);
	}
	
	/**
	 * Method getCritRate.
	 * @param attacker Character
	 * @param skill Skill
	 * @return float
	 */
	public final float getCritRate(Character attacker, Skill skill)
	{
		return calcStat(StatType.CRITICAL_RATE, template.getCritRate(), attacker, skill);
	}
	
	/**
	 * Method getCritRateRcpt.
	 * @param attacker Character
	 * @param skill Skill
	 * @return float
	 */
	public final float getCritRateRcpt(Character attacker, Skill skill)
	{
		return calcStat(StatType.CRIT_CHANCE_RECEPTIVE, template.getCritRcpt(), attacker, skill);
	}
	
	/**
	 * Method getCurrentHp.
	 * @return int
	 */
	public final int getCurrentHp()
	{
		return currentHp;
	}
	
	/**
	 * Method getCurrentHpPercent.
	 * @return int
	 */
	public final int getCurrentHpPercent()
	{
		return Math.max(Math.min((currentHp * 100) / getMaxHp(), 100), 0);
	}
	
	/**
	 * Method getCurrentMp.
	 * @return int
	 */
	public final int getCurrentMp()
	{
		return currentMp;
	}
	
	/**
	 * Method getCurrentMpPercent.
	 * @return int
	 */
	public final int getCurrentMpPercent()
	{
		return Math.max(Math.min((currentMp * 100) / getMaxMp(), 100), 0);
	}
	
	/**
	 * Method getDefense.
	 * @param attacker Character
	 * @param skill Skill
	 * @return int
	 */
	public int getDefense(Character attacker, Skill skill)
	{
		return (int) calcStat(StatType.DEFENSE, 0, attacker, skill);
	}
	
	/**
	 * Method getDefenseFactor.
	 * @return int
	 */
	public final int getDefenseFactor()
	{
		return (int) calcStat(StatType.DEFENSE_FACTOR, template.getDefenseFactor(), this, null);
	}
	
	/**
	 * Method getDuel.
	 * @return Duel
	 */
	public Duel getDuel()
	{
		return null;
	}
	
	/**
	 * Method getEffectList.
	 * @return EffectList
	 */
	public final EffectList getEffectList()
	{
		return effectList;
	}
	
	/**
	 * Method getEnemy.
	 * @return TObject
	 */
	public final TObject getEnemy()
	{
		return enemy;
	}
	
	/**
	 * Method getEnergyLevel.
	 * @return int
	 */
	public int getEnergyLevel()
	{
		return 0;
	}
	
	/**
	 * Method getEquipment.
	 * @return Equipment
	 */
	public Equipment getEquipment()
	{
		return null;
	}
	
	/**
	 * Method getGeom.
	 * @return Geom
	 */
	public final Geom getGeom()
	{
		return geom;
	}
	
	/**
	 * Method getGeomDistance.
	 * @param target Character
	 * @return float
	 */
	public float getGeomDistance(Character target)
	{
		return target.getGeomDistance(x, y) - geom.getRadius();
	}
	
	/**
	 * Method getGeomDistance.
	 * @param x float
	 * @param y float
	 * @return float
	 */
	@Override
	public float getGeomDistance(float x, float y)
	{
		return getDistance(x, y) - geom.getRadius();
	}
	
	/**
	 * Method getGeomHeight.
	 * @return float
	 */
	public float getGeomHeight()
	{
		return geom.getHeight();
	}
	
	/**
	 * Method getGeomRadius.
	 * @return float
	 */
	public float getGeomRadius()
	{
		return geom.getRadius();
	}
	
	/**
	 * Method getGuild.
	 * @return Guild
	 */
	public Guild getGuild()
	{
		return null;
	}
	
	/**
	 * Method getHateList.
	 * @return Array<Npc>
	 */
	public final Array<Npc> getHateList()
	{
		return hateList;
	}
	
	/**
	 * Method getImpact.
	 * @param attacked Character
	 * @param skill Skill
	 * @return int
	 */
	public int getImpact(Character attacked, Skill skill)
	{
		return (int) calcStat(StatType.IMPACT, 0, attacked, skill);
	}
	
	/**
	 * Method getImpactFactor.
	 * @return int
	 */
	public final int getImpactFactor()
	{
		return (int) calcStat(StatType.IMPACT_FACTOR, template.getImpactFactor(), this, null);
	}
	
	/**
	 * Method getInventory.
	 * @return Inventory
	 */
	public Inventory getInventory()
	{
		return null;
	}
	
	/**
	 * Method getKarma.
	 * @return int
	 */
	public int getKarma()
	{
		return 0;
	}
	
	/**
	 * Method getLastCast.
	 * @return long
	 */
	public long getLastCast()
	{
		return lastCast;
	}
	
	/**
	 * Method getLastSkillName.
	 * @return SkillName
	 */
	public SkillName getLastSkillName()
	{
		return lastSkillName;
	}
	
	/**
	 * Method getLevel.
	 * @return int
	 */
	public int getLevel()
	{
		return 0;
	}
	
	/**
	 * Method getLocalHateList.
	 * @return Array<Npc>
	 */
	public final Array<Npc> getLocalHateList()
	{
		final LocalObjects local = LocalObjects.get();
		final Array<Npc> npcs = local.getNextNpcList();
		final Array<Npc> hateList = getHateList();
		hateList.readLock();
		
		try
		{
			npcs.addAll(hateList);
		}
		
		finally
		{
			hateList.readUnlock();
		}
		return npcs;
	}
	
	/**
	 * Method getLocalHateList.
	 * @param container Array<Npc>
	 * @return Array<Npc>
	 */
	public final Array<Npc> getLocalHateList(Array<Npc> container)
	{
		final Array<Npc> hateList = getHateList();
		hateList.readLock();
		
		try
		{
			container.addAll(hateList);
		}
		
		finally
		{
			hateList.readUnlock();
		}
		return container;
	}
	
	/**
	 * Method getLockOnSkill.
	 * @return Skill
	 */
	public Skill getLockOnSkill()
	{
		return lockOnSkill;
	}
	
	/**
	 * Method getLockOnTargets.
	 * @return Array<Character>
	 */
	public Array<Character> getLockOnTargets()
	{
		return null;
	}
	
	/**
	 * Method getMaxHp.
	 * @return int
	 */
	public final int getMaxHp()
	{
		return (int) calcStat(StatType.MAX_HP, template.getMaxHp(), null, null);
	}
	
	/**
	 * Method getMaxMp.
	 * @return int
	 */
	public final int getMaxMp()
	{
		return (int) calcStat(StatType.MAX_MP, template.getMaxMp(), null, null);
	}
	
	/**
	 * Method getMiningLevel.
	 * @return int
	 */
	public int getMiningLevel()
	{
		return 0;
	}
	
	/**
	 * Method getModelId.
	 * @return int
	 */
	public int getModelId()
	{
		return template.getModelId();
	}
	
	/**
	 * Method getMovePacket.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return ServerPacket
	 */
	public ServerPacket getMovePacket(float x, float y, float z, int heading, MoveType type, float targetX, float targetY, float targetZ)
	{
		return CharMove.getInstance(this, type, x, y, z, heading, targetX, targetY, targetZ);
	}
	
	/**
	 * Method getMovePacket.
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @return ServerPacket
	 */
	public ServerPacket getMovePacket(MoveType type, float targetX, float targetY, float targetZ)
	{
		return getMovePacket(x, y, z, heading, type, targetX, targetY, targetZ);
	}
	
	/**
	 * Method getMoveTickInterval.
	 * @return int
	 */
	public final int getMoveTickInterval()
	{
		return 100;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	public void getOffMount()
	{
	}
	
	/**
	 * Method getOwerturnId.
	 * @return int
	 */
	public int getOwerturnId()
	{
		return 0;
	}
	
	/**
	 * Method getOwerturnTime.
	 * @return int
	 */
	public int getOwerturnTime()
	{
		return 3000;
	}
	
	/**
	 * Method getOwner.
	 * @return Character
	 */
	public Character getOwner()
	{
		return null;
	}
	
	/**
	 * Method getParty.
	 * @return Party
	 */
	public Party getParty()
	{
		return null;
	}
	
	/**
	 * Method getPlantLevel.
	 * @return int
	 */
	public int getPlantLevel()
	{
		return 0;
	}
	
	/**
	 * Method getPowerFactor.
	 * @return int
	 */
	public int getPowerFactor()
	{
		return (int) calcStat(StatType.POWER_FACTOR, template.getPowerFactor(), this, null);
	}
	
	/**
	 * Method getQuestList.
	 * @return QuestList
	 */
	public QuestList getQuestList()
	{
		return null;
	}
	
	/**
	 * Method getRegenHp.
	 * @return int
	 */
	public final int getRegenHp()
	{
		return (int) calcStat(StatType.REGEN_HP, template.getRegHp(), this, null);
	}
	
	/**
	 * Method getRegenMp.
	 * @return int
	 */
	public final int getRegenMp()
	{
		return (int) calcStat(StatType.REGEN_MP, template.getRegMp(), this, null);
	}
	
	/**
	 * Method getReuseSkill.
	 * @param id int
	 * @return ReuseSkill
	 */
	public final ReuseSkill getReuseSkill(int id)
	{
		if (reuseSkills.isEmpty())
		{
			return null;
		}
		
		return reuseSkills.get(id);
	}
	
	/**
	 * Method getReuseSkills.
	 * @return Table<IntKey,ReuseSkill>
	 */
	public final Table<IntKey, ReuseSkill> getReuseSkills()
	{
		return reuseSkills;
	}
	
	/**
	 * Method getRunSpeed.
	 * @return int
	 */
	public final int getRunSpeed()
	{
		return Math.min((int) calcStat(StatType.RUN_SPEED, template.getRunSpd(), this, null), 500);
	}
	
	/**
	 * Method getSkill.
	 * @param skillId int
	 * @return Skill
	 */
	public final Skill getSkill(int skillId)
	{
		if (skills.isEmpty())
		{
			return null;
		}
		
		return skills.get(skillId);
	}
	
	/**
	 * Method getSkills.
	 * @return Table<IntKey,Skill>
	 */
	public final Table<IntKey, Skill> getSkills()
	{
		return skills;
	}
	
	/**
	 * Method getSkillVariables.
	 * @return Table<IntKey,Wrap>
	 */
	public Table<IntKey, Wrap> getSkillVariables()
	{
		if (skillVariables == null)
		{
			synchronized (this)
			{
				if (skillVariables == null)
				{
					skillVariables = Tables.newConcurrentIntegerTable();
				}
			}
		}
		
		return skillVariables;
	}
	
	/**
	 * Method getSummon.
	 * @return Summon
	 */
	public Summon getSummon()
	{
		return summon;
	}
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	public Character getTarget()
	{
		return target;
	}
	
	/**
	 * Method getTemplate.
	 * @return CharTemplate
	 */
	public CharTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		return template.getTemplateId();
	}
	
	/**
	 * Method getTemplateType.
	 * @return int
	 */
	@Override
	public int getTemplateType()
	{
		return template.getTemplateType();
	}
	
	/**
	 * Method getTitle.
	 * @return String
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Method hasAI.
	 * @return boolean
	 */
	@Override
	public final boolean hasAI()
	{
		return ai != null;
	}
	
	/**
	 * Method hasEffects.
	 * @return boolean
	 */
	public boolean hasEffects()
	{
		return effectList.size() > 0;
	}
	
	/**
	 * Method hasGuild.
	 * @return boolean
	 */
	public boolean hasGuild()
	{
		return false;
	}
	
	/**
	 * Method hasParty.
	 * @return boolean
	 */
	public boolean hasParty()
	{
		return false;
	}
	
	/**
	 * Method hasQuests.
	 * @return boolean
	 */
	public boolean hasQuests()
	{
		return false;
	}
	
	/**
	 * Method isAllBlocking.
	 * @return boolean
	 */
	public final boolean isAllBlocking()
	{
		return stuned || owerturned || flyingPegas;
	}
	
	/**
	 * Method isAttackBlocking.
	 * @return boolean
	 */
	public final boolean isAttackBlocking()
	{
		return stuned || skillBlocking;
	}
	
	/**
	 * Method isAttacking.
	 * @return boolean
	 */
	public boolean isAttacking()
	{
		return false;
	}
	
	/**
	 * Method isBattleStanced.
	 * @return boolean
	 */
	public final boolean isBattleStanced()
	{
		return battleStanced;
	}
	
	/**
	 * Method isBehindTarget.
	 * @param target TObject
	 * @return boolean
	 */
	public final boolean isBehindTarget(TObject target)
	{
		if (target == null)
		{
			return false;
		}
		
		if (target.isCharacter())
		{
			final int head = getHeadingTo(target, true);
			return (head != -1) && ((head <= 10430) || (head >= 55105));
		}
		
		return false;
	}
	
	/**
	 * Method isBlocked.
	 * @param attacker Character
	 * @param impactX float
	 * @param impactY float
	 * @param skill Skill
	 * @return boolean
	 */
	public final boolean isBlocked(Character attacker, float impactX, float impactY, Skill skill)
	{
		if (!isDefenseStance() || skill.isShieldIgnore())
		{
			return false;
		}
		
		return isInFront(attacker);
	}
	
	/**
	 * Method isCastingNow.
	 * @return boolean
	 */
	public final boolean isCastingNow()
	{
		return castingSkill != null;
	}
	
	/**
	 * Method isCharacter.
	 * @return boolean
	 */
	@Override
	public final boolean isCharacter()
	{
		return true;
	}
	
	/**
	 * Method isCollecting.
	 * @return boolean
	 */
	public boolean isCollecting()
	{
		return false;
	}
	
	/**
	 * Method isDead.
	 * @return boolean
	 */
	public final boolean isDead()
	{
		return currentHp < 1;
	}
	
	/**
	 * Method isDefenseStance.
	 * @return boolean
	 */
	public final boolean isDefenseStance()
	{
		return defenseStance;
	}
	
	/**
	 * Method isEvasioned.
	 * @return boolean
	 */
	public final boolean isEvasioned()
	{
		final Skill skill = getCastingSkill();
		
		if (skill == null)
		{
			return false;
		}
		
		return skill.isEvasion();
	}
	
	/**
	 * Method isFlyingPegas.
	 * @return boolean
	 */
	public final boolean isFlyingPegas()
	{
		return flyingPegas;
	}
	
	/**
	 * Method isGM.
	 * @return boolean
	 */
	public boolean isGM()
	{
		return false;
	}
	
	/**
	 * Method isHit.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param height float
	 * @param radius float
	 * @return boolean
	 */
	public boolean isHit(float startX, float startY, float startZ, float height, float radius)
	{
		if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
		{
			final Location[] locs = Coords.circularCoords(Location.class, x, y, z, (int) geom.getRadius(), 10);
			final ItemTable itemTable = ItemTable.getInstance();
			final ItemTemplate template = itemTable.getItem(125);
			
			for (int i = 0; i < 10; i++)
			{
				final ItemInstance item = template.newInstance();
				item.setItemCount(1);
				item.setTempOwner(this);
				final Location loc = locs[i];
				loc.setContinentId(continentId);
				item.spawnMe(loc);
			}
		}
		
		return geom.isHit(startX, startY, startZ, height, radius);
	}
	
	/**
	 * Method isHit.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param endX float
	 * @param endY float
	 * @param endZ float
	 * @param radius float
	 * @param checkHeight boolean
	 * @return boolean
	 */
	@Override
	public boolean isHit(float startX, float startY, float startZ, float endX, float endY, float endZ, float radius, boolean checkHeight)
	{
		if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
		{
			final Location[] locs = Coords.circularCoords(Location.class, x, y, z, (int) geom.getRadius(), 10);
			final ItemTable itemTable = ItemTable.getInstance();
			final ItemTemplate template = itemTable.getItem(125);
			
			for (int i = 0; i < 10; i++)
			{
				final ItemInstance item = template.newInstance();
				item.setItemCount(1);
				item.setTempOwner(this);
				final Location loc = locs[i];
				loc.setContinentId(continentId);
				item.spawnMe(loc);
			}
		}
		
		return geom.isHit(startX, startY, startZ, endX, endY, endZ, radius, checkHeight);
	}
	
	/**
	 * Method isInBattleTerritory.
	 * @return boolean
	 */
	public boolean isInBattleTerritory()
	{
		return false;
	}
	
	/**
	 * Method isInBehind.
	 * @param target Character
	 * @return boolean
	 */
	public final boolean isInBehind(Character target)
	{
		if (target == null)
		{
			return false;
		}
		
		final int head = getHeadingTo(target, false);
		return (head != -1) && (head >= 24576) && (head <= 40960);
	}
	
	/**
	 * Method isInBonfireTerritory.
	 * @return boolean
	 */
	public boolean isInBonfireTerritory()
	{
		return false;
	}
	
	/**
	 * Method isInDegree.
	 * @param target Character
	 * @param degree int
	 * @param width int
	 * @return boolean
	 */
	public final boolean isInDegree(Character target, int degree, int width)
	{
		int angle = (int) Angles.headingToDegree(getHeadingTo(target, false));
		int min = degree - width;
		int max = degree + width;
		
		if (min < 0)
		{
			min += 360;
		}
		
		if (max < 0)
		{
			max += 360;
		}
		
		final boolean flag = (angle - degree) > 180;
		
		if (flag)
		{
			angle -= 360;
		}
		
		if (angle > max)
		{
			return false;
		}
		
		angle += 360;
		return angle > min;
	}
	
	/**
	 * Method isInDegree.
	 * @param targetX float
	 * @param targetY float
	 * @param degree int
	 * @param width int
	 * @return boolean
	 */
	public final boolean isInDegree(float targetX, float targetY, int degree, int width)
	{
		int angle = (int) Angles.headingToDegree(getHeadingTo(targetX, targetY));
		int min = degree - width;
		int max = degree + width;
		
		if (min < 0)
		{
			min += 360;
		}
		
		if (max < 0)
		{
			max += 360;
		}
		
		final boolean flag = (angle - degree) > 180;
		
		if (flag)
		{
			angle -= 360;
		}
		
		if (angle > max)
		{
			return false;
		}
		
		angle += 360;
		return angle > min;
	}
	
	/**
	 * Method isInFront.
	 * @param target Character
	 * @return boolean
	 */
	public final boolean isInFront(Character target)
	{
		if (target == null)
		{
			return false;
		}
		
		final int head = getHeadingTo(target, false);
		return ((head != -1) && (head <= 8192)) || (head >= 57344);
	}
	
	/**
	 * Method isInPeaceTerritory.
	 * @return boolean
	 */
	public boolean isInPeaceTerritory()
	{
		return false;
	}
	
	/**
	 * Method isInRange.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param range float
	 * @return boolean
	 */
	@Override
	public boolean isInRange(float x, float y, float z, float range)
	{
		return (getDistance(x, y, z) - geom.getRadius()) <= range;
	}
	
	/**
	 * Method isInSide.
	 * @param target Character
	 * @return boolean
	 */
	public final boolean isInSide(Character target)
	{
		if (target == null)
		{
			return false;
		}
		
		final int head = getHeadingTo(target, false);
		return (head != -1) && (((head >= 8192) && (head <= 24576)) || ((head >= 40960) && (head <= 57344)));
	}
	
	/**
	 * Method isInvul.
	 * @return boolean
	 */
	public boolean isInvul()
	{
		return invul;
	}
	
	/**
	 * Method isMoveBlocked.
	 * @return boolean
	 */
	public final boolean isMoveBlocked()
	{
		return stuned || rooted;
	}
	
	/**
	 * Method isMovementDisabled.
	 * @return boolean
	 */
	public final boolean isMovementDisabled()
	{
		if (isDead() || defenseStance || rooted || owerturned || skillMoved || stuned || (getRunSpeed() < 1))
		{
			return true;
		}
		
		final Skill castingSkill = getCastingSkill();
		
		if ((castingSkill != null) && castingSkill.isBlockingMove())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method isMoving.
	 * @return boolean
	 */
	public final boolean isMoving()
	{
		return moving;
	}
	
	/**
	 * Method isOnMount.
	 * @return boolean
	 */
	public boolean isOnMount()
	{
		return false;
	}
	
	/**
	 * Method isOwerturned.
	 * @return boolean
	 */
	public final boolean isOwerturned()
	{
		return owerturned;
	}
	
	/**
	 * Method isOwerturnImmunity.
	 * @return boolean
	 */
	public boolean isOwerturnImmunity()
	{
		return false;
	}
	
	/**
	 * Method isLeashImmunity.
	 * @return boolean
	 */
	public boolean isLeashImmunity()
	{
		return false;
	}
	
	/**
	 * Method isPvPMode.
	 * @return boolean
	 */
	public boolean isPvPMode()
	{
		return false;
	}
	
	/**
	 * Method isSideTarget.
	 * @param target TObject
	 * @return boolean
	 */
	public final boolean isSideTarget(TObject target)
	{
		if (target == null)
		{
			return false;
		}
		
		if (target.isCharacter())
		{
			final int head = getHeadingTo(target, true);
			return (head != -1) && ((head <= 22337) || (head >= 43197));
		}
		
		return false;
	}
	
	/**
	 * Method isSkillBlocking.
	 * @return boolean
	 */
	public boolean isSkillBlocking()
	{
		return skillBlocking;
	}
	
	/**
	 * Method isSkillDisabled.
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean isSkillDisabled(Skill skill)
	{
		final ReuseSkill reuse = reuseSkills.get(skill.getReuseId());
		
		if (reuse == null)
		{
			return false;
		}
		
		return System.currentTimeMillis() < reuse.getEndTime();
	}
	
	/**
	 * Method isSkillMoved.
	 * @return boolean
	 */
	public final boolean isSkillMoved()
	{
		return skillMoved;
	}
	
	/**
	 * Method isSleepImmunity.
	 * @return boolean
	 */
	public boolean isSleepImmunity()
	{
		return false;
	}
	
	/**
	 * Method isSpawned.
	 * @return boolean
	 */
	public boolean isSpawned()
	{
		return spawned;
	}
	
	/**
	 * Method isStuned.
	 * @return boolean
	 */
	public final boolean isStuned()
	{
		return stuned;
	}
	
	/**
	 * Method isStunImmunity.
	 * @return boolean
	 */
	public boolean isStunImmunity()
	{
		return false;
	}
	
	/**
	 * Method lock.
	 * @see rlib.util.Synchronized#lock()
	 */
	@Override
	public void lock()
	{
		Locks.lock(effectList, charLock);
	}
	
	/**
	 * Method moveToLocation.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param heading int
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param broadCastMove boolean
	 * @param sendSelfPacket boolean
	 */
	public void moveToLocation(float startX, float startY, float startZ, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket)
	{
		if ((type == MoveType.JUMP_FALL) || (type == MoveType.RUN_FALL))
		{
			doFall(z, targetZ);
		}
		else if (isOnMount() && ((type == MoveType.SWIM_RUN) || (type == MoveType.SWIM_STOP)))
		{
			getOffMount();
		}
		else if (type == MoveType.RUN)
		{
			updateCoords();
		}
		
		setHeading(heading);
		moveNextTask.nextTask(startX, startY, startZ, type, targetX, targetY, targetZ);
		
		if (broadCastMove)
		{
			broadcastMove(x, y, z, heading, type, targetX, targetY, targetZ, false);
		}
	}
	
	/**
	 * Method newGeomCharacter.
	 * @return Geom
	 */
	protected Geom newGeomCharacter()
	{
		throw new IllegalArgumentException("unsupported method.");
	}
	
	/**
	 * Method newRegenHp.
	 * @return Regen
	 */
	protected Regen newRegenHp()
	{
		throw new IllegalArgumentException("unsupported method.");
	}
	
	/**
	 * Method newRegenMp.
	 * @return Regen
	 */
	protected Regen newRegenMp()
	{
		throw new IllegalArgumentException("unsupported method.");
	}
	
	/**
	 * Method nextCastId.
	 * @return int
	 */
	public int nextCastId()
	{
		return 0;
	}
	
	/**
	 * Method nextUse.
	 * @param skill Skill
	 */
	public final void nextUse(Skill skill)
	{
		skillUseTask.nextUse(skill);
	}
	
	/**
	 * Method onDamage.
	 * @param attacker Character
	 * @param skill Skill
	 * @param info AttackInfo
	 */
	public void onDamage(Character attacker, Skill skill, AttackInfo info)
	{
		final Array<DamageListener> listeners = getDamageListeners();
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			for (DamageListener listener : listeners.array())
			{
				if (listener == null)
				{
					break;
				}
				
				listener.onDamage(attacker, this, info, skill);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method getDamageListeners.
	 * @return Array<DamageListener>
	 */
	public Array<DamageListener> getDamageListeners()
	{
		return damageListeners;
	}
	
	/**
	 * Method onShield.
	 * @param attacker Character
	 * @param skill Skill
	 * @param info AttackInfo
	 */
	public void onShield(Character attacker, Skill skill, AttackInfo info)
	{
		if (isBlocked(attacker, skill.getImpactX(), skill.getImpactY(), skill))
		{
			final int limit = (int) calcStat(StatType.MAX_DAMAGE_DEFENSE, 0, attacker, skill);
			int mpConsume = 0;
			final Skill defense = getActivateSkill();
			
			if (defense != null)
			{
				mpConsume = defense.blockMpConsume(info.getDamage());
			}
			
			if (mpConsume > 0)
			{
				setCurrentMp(getCurrentMp() - mpConsume);
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyMpChanged(this);
			}
			
			info.setDamage(Math.max(0, info.getDamage() - limit));
			info.setBlocked(info.isNoDamage());
		}
	}
	
	/**
	 * Method removeChanceFunc.
	 * @param func ChanceFunc
	 */
	public void removeChanceFunc(ChanceFunc func)
	{
		chanceFuncs.fastRemove(func);
	}
	
	/**
	 * Method removeDamageListener.
	 * @param listener DamageListener
	 */
	public void removeDamageListener(DamageListener listener)
	{
		damageListeners.fastRemove(listener);
	}
	
	/**
	 * Method removeDieListener.
	 * @param listener DieListener
	 */
	public void removeDieListener(DieListener listener)
	{
		dieListeners.fastRemove(listener);
	}
	
	/**
	 * Method removeEffect.
	 * @param effect Effect
	 */
	public final void removeEffect(Effect effect)
	{
		effectList.removeEffect(effect);
	}
	
	/**
	 * Method removeHate.
	 * @param npc Npc
	 */
	public final void removeHate(Npc npc)
	{
		if ((npc == null) || hateList.isEmpty())
		{
			return;
		}
		
		hateList.fastRemove(npc);
	}
	
	/**
	 * Method removeSkill.
	 * @param skillId int
	 * @param sendPacket boolean
	 */
	public void removeSkill(int skillId, boolean sendPacket)
	{
		final Table<IntKey, Skill> current = getSkills();
		final Skill old = current.remove(skillId);
		
		if (old != null)
		{
			old.getTemplate().removePassiveFuncs(this);
			old.fold();
		}
	}
	
	/**
	 * Method removeSkill.
	 * @param skill Skill
	 * @param sendPacket boolean
	 */
	public void removeSkill(Skill skill, boolean sendPacket)
	{
		final Table<IntKey, Skill> current = getSkills();
		final Skill old = current.remove(skill.getId());
		
		if (old != null)
		{
			old.getTemplate().removePassiveFuncs(this);
			old.fold();
		}
	}
	
	/**
	 * Method removeSkill.
	 * @param template SkillTemplate
	 * @param sendPacket boolean
	 */
	public void removeSkill(SkillTemplate template, boolean sendPacket)
	{
		final Table<IntKey, Skill> current = getSkills();
		final Skill old = current.remove(template.getId());
		
		if (old != null)
		{
			old.getTemplate().removePassiveFuncs(this);
			old.fold();
		}
	}
	
	/**
	 * Method removeSkills.
	 * @param templates SkillTemplate[]
	 * @param sendPacket boolean
	 */
	public void removeSkills(SkillTemplate[] templates, boolean sendPacket)
	{
		final Table<IntKey, Skill> current = getSkills();
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		for (SkillTemplate template2 : templates)
		{
			final Skill skill = current.remove(template2.getId());
			
			if (skill == null)
			{
				continue;
			}
			
			dbManager.deleteSkill(this, skill);
			skill.getTemplate().removePassiveFuncs(this);
			skill.fold();
		}
	}
	
	/**
	 * Method removeStatFunc.
	 * @param func StatFunc
	 */
	public final void removeStatFunc(StatFunc func)
	{
		if (func == null)
		{
			return;
		}
		
		final int ordinal = func.getStat().ordinal();
		writeStatLock.lock();
		
		try
		{
			if (calcs[ordinal] == null)
			{
				return;
			}
			
			calcs[ordinal].removeFunc(func);
		}
		
		finally
		{
			writeStatLock.unlock();
		}
	}
	
	/**
	 * Method removeStatFuncs.
	 * @param funcs StatFunc[]
	 */
	public final void removeStatFuncs(StatFunc[] funcs)
	{
		charLock.lock();
		
		try
		{
			for (StatFunc func : funcs)
			{
				if (func == null)
				{
					continue;
				}
				
				final int ordinal = func.getStat().ordinal();
				final Calculator calc = calcs[ordinal];
				
				if (calc == null)
				{
					continue;
				}
				
				calc.removeFunc(func);
			}
		}
		
		finally
		{
			charLock.unlock();
		}
	}
	
	/**
	 * Method removeVisibleObject.
	 * @param object TObject
	 * @param type int
	 */
	public void removeVisibleObject(TObject object, int type)
	{
	}
	
	/**
	 * Method sayMessage.
	 * @param message String
	 */
	public void sayMessage(String message)
	{
		sayMessage(getName(), message);
	}
	
	/**
	 * Method sayMessage.
	 * @param name String
	 * @param message String
	 */
	public final void sayMessage(String name, String message)
	{
		final LocalObjects local = LocalObjects.get();
		final Array<Player> players = World.getAround(Player.class, local.getNextPlayerList(), this, 300);
		
		if (players.isEmpty())
		{
			return;
		}
		
		final Player[] array = players.array();
		final CharSay packet = CharSay.getInstance(name, message, SayType.MAIN_CHAT, objectId, getSubId());
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			packet.increaseSends();
		}
		
		sendPacket(packet, true);
		
		for (int i = 0, length = players.size(); i < length; i++)
		{
			array[i].sendPacket(packet, false);
		}
	}
	
	/**
	 * Method sendMessage.
	 * @param type MessageType
	 */
	public void sendMessage(MessageType type)
	{
	}
	
	/**
	 * Method sendMessage.
	 * @param message String
	 */
	public void sendMessage(String message)
	{
	}
	
	/**
	 * Method sendPacket.
	 * @param packet ServerPacket
	 * @param increaseSends boolean
	 */
	public void sendPacket(ServerPacket packet, boolean increaseSends)
	{
	}
	
	/**
	 * Method setActivateSkill.
	 * @param activateSkill Skill
	 */
	public final void setActivateSkill(Skill activateSkill)
	{
		this.activateSkill = activateSkill;
	}
	
	/**
	 * Method setAi.
	 * @param ai AI
	 */
	public void setAi(AI ai)
	{
		this.ai = ai;
	}
	
	/**
	 * Method setAttacking.
	 * @param attacking boolean
	 */
	public void setAttacking(boolean attacking)
	{
	}
	
	/**
	 * Method setBattleStanced.
	 * @param battleStanced boolean
	 */
	public final void setBattleStanced(boolean battleStanced)
	{
		this.battleStanced = battleStanced;
	}
	
	/**
	 * Method setCastId.
	 * @param castId int
	 */
	public final void setCastId(int castId)
	{
		this.castId = castId;
	}
	
	/**
	 * Method setCastingSkill.
	 * @param castingSkill Skill
	 */
	public final void setCastingSkill(Skill castingSkill)
	{
		this.castingSkill = castingSkill;
	}
	
	/**
	 * Method setChargeLevel.
	 * @param chargeLevel int
	 */
	public final void setChargeLevel(int chargeLevel)
	{
		this.chargeLevel = chargeLevel;
	}
	
	/**
	 * Method setCurrentHp.
	 * @param currentHp int
	 */
	public void setCurrentHp(int currentHp)
	{
		if (currentHp > getMaxHp())
		{
			currentHp = getMaxHp();
		}
		
		if (currentHp < 0)
		{
			currentHp = 0;
		}
		
		this.currentHp = currentHp;
	}
	
	/**
	 * Method setCurrentMp.
	 * @param currentMp int
	 */
	public void setCurrentMp(int currentMp)
	{
		if (currentMp > getMaxMp())
		{
			currentMp = getMaxMp();
		}
		
		if (currentMp < 0)
		{
			currentMp = 0;
		}
		
		this.currentMp = currentMp;
	}
	
	/**
	 * Method setDefenseStance.
	 * @param defenseStance boolean
	 */
	public final void setDefenseStance(boolean defenseStance)
	{
		this.defenseStance = defenseStance;
	}
	
	/**
	 * Method setEnemy.
	 * @param enemy TObject
	 */
	public final void setEnemy(TObject enemy)
	{
		this.enemy = enemy;
	}
	
	/**
	 * Method setEquipment.
	 * @param equipment Equipment
	 */
	public void setEquipment(Equipment equipment)
	{
	}
	
	/**
	 * Method setFlyingPegas.
	 * @param flyingPegas boolean
	 */
	public final void setFlyingPegas(boolean flyingPegas)
	{
		this.flyingPegas = flyingPegas;
	}
	
	/**
	 * Method setInventory.
	 * @param inventory Inventory
	 */
	public void setInventory(Inventory inventory)
	{
	}
	
	/**
	 * Method setInvul.
	 * @param invul boolean
	 */
	public final void setInvul(boolean invul)
	{
		this.invul = invul;
	}
	
	/**
	 * Method setKarma.
	 * @param karma int
	 */
	public void setKarma(int karma)
	{
	}
	
	/**
	 * Method setLastCast.
	 * @param lastCast long
	 */
	public void setLastCast(long lastCast)
	{
		this.lastCast = lastCast;
	}
	
	/**
	 * Method setLastSkillName.
	 * @param lastSkillName SkillName
	 */
	public void setLastSkillName(SkillName lastSkillName)
	{
		this.lastSkillName = lastSkillName;
	}
	
	/**
	 * Method setLockOnSkill.
	 * @param lockOnSkill Skill
	 */
	public void setLockOnSkill(Skill lockOnSkill)
	{
		this.lockOnSkill = lockOnSkill;
	}
	
	/**
	 * Method setMoving.
	 * @param moving boolean
	 */
	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}
	
	/**
	 * Method setName.
	 * @param name String
	 */
	public final void setName(String name)
	{
		if (name == null)
		{
			return;
		}
		
		this.name = name;
	}
	
	/**
	 * Method setOwerturned.
	 * @param owerturned boolean
	 */
	public final void setOwerturned(boolean owerturned)
	{
		this.owerturned = owerturned;
	}
	
	/**
	 * Method setOwner.
	 * @param owner Character
	 */
	public void setOwner(Character owner)
	{
		log.warning(getClass(), "unsupported invoke \"setOwner\".");
	}
	
	/**
	 * Method setPvPMode.
	 * @param pvpMode boolean
	 */
	public void setPvPMode(boolean pvpMode)
	{
	}
	
	/**
	 * Method setRooted.
	 * @param rooted boolean
	 */
	public final void setRooted(boolean rooted)
	{
		if (rooted)
		{
			stopMove();
		}
		
		this.rooted = rooted;
	}
	
	/**
	 * Method setSkillBlocking.
	 * @param skillBlocking boolean
	 */
	public void setSkillBlocking(boolean skillBlocking)
	{
		this.skillBlocking = skillBlocking;
	}
	
	/**
	 * Method setSkillMoved.
	 * @param skillMoved boolean
	 */
	public final void setSkillMoved(boolean skillMoved)
	{
		this.skillMoved = skillMoved;
	}
	
	/**
	 * Method setSkills.
	 * @param sks Skill[]
	 */
	public final void setSkills(Skill[] sks)
	{
		for (Skill sk : sks)
		{
			final Skill skill = sk;
			
			if (!skills.containsKey(skill.getId()))
			{
				skills.put(skill.getId(), skill);
			}
		}
	}
	
	/**
	 * Method setStuned.
	 * @param stuned boolean
	 */
	public final void setStuned(boolean stuned)
	{
		if (stuned)
		{
			abortCast(true);
			stopMove();
		}
		
		this.stuned = stuned;
	}
	
	/**
	 * Method setTarget.
	 * @param target Character
	 */
	public void setTarget(Character target)
	{
		this.target = target;
	}
	
	/**
	 * Method setTitle.
	 * @param title String
	 */
	public final void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Method skillHealHp.
	 * @param damageId int
	 * @param heal int
	 * @param healer Character
	 */
	public void skillHealHp(int damageId, int heal, Character healer)
	{
		if (isDead() || (heal < 1))
		{
			return;
		}
		
		charLock.lock();
		
		try
		{
			setCurrentHp(getCurrentHp() + heal);
		}
		
		finally
		{
			charLock.unlock();
		}
		PacketManager.showDamage(healer, this, damageId, heal, false, false, Damage.HEAL);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyHpChanged(this);
	}
	
	/**
	 * Method skillHealMp.
	 * @param damageId int
	 * @param heal int
	 * @param healer Character
	 */
	public void skillHealMp(int damageId, int heal, Character healer)
	{
		if (isDead() || (heal < 1))
		{
			return;
		}
		
		charLock.lock();
		
		try
		{
			setCurrentMp(getCurrentMp() + heal);
		}
		
		finally
		{
			charLock.unlock();
		}
		PacketManager.showDamage(healer, this, damageId, heal, false, false, Damage.MANAHEAL);
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyMpChanged(this);
	}
	
	@Override
	public void spawnMe()
	{
		super.spawnMe();
		moveNextTask.startTask();
		spawned = true;
	}
	
	/**
	 * Method startBattleStance.
	 * @param enemy Character
	 * @return boolean
	 */
	public boolean startBattleStance(Character enemy)
	{
		return true;
	}
	
	public void startEmotions()
	{
		emotionTask.start();
	}
	
	public void stopBattleStance()
	{
	}
	
	public void stopEmotions()
	{
		emotionTask.stop();
	}
	
	public final void stopMove()
	{
		final boolean moving = (isMoving() || isCastingNow()) && !owerturned;
		moveNextTask.stopMove();
		
		if (moving)
		{
			broadcastMove(x, y, z, heading, MoveType.STOP, x, y, z, true);
		}
	}
	
	public void stopSkillMove()
	{
		skillMoveTask.cancel(true);
	}
	
	/**
	 * Method teleToLocation.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 */
	public void teleToLocation(int continentId, float x, float y, float z)
	{
		teleToLocation(continentId, x, y, z, heading);
	}
	
	/**
	 * Method teleToLocation.
	 * @param continentId int
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param heading int
	 */
	public void teleToLocation(int continentId, float x, float y, float z, int heading)
	{
		if (isOnMount())
		{
			getOffMount();
		}
		
		stopMove();
		setContinentId(continentId);
		setXYZ(x, y, z);
		setHeading(heading);
	}
	
	/**
	 * Method teleToLocation.
	 * @param location Location
	 */
	public void teleToLocation(Location location)
	{
		teleToLocation(location.getContinentId(), location.getX(), location.getY(), location.getZ(), location.getHeading() != 0 ? location.getHeading() : heading);
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
	
	/**
	 * Method unlock.
	 * @see rlib.util.Synchronized#unlock()
	 */
	@Override
	public void unlock()
	{
		Locks.unlock(effectList, charLock);
	}
	
	public void updateCoords()
	{
	}
	
	public void updateDefense()
	{
		if (!isDefenseStance() || (activateSkill == null) || (activateSkill.getSkillType() != SkillType.DEFENSE))
		{
			return;
		}
		
		if (getCurrentMp() < 1)
		{
			abortCast(true);
		}
	}
	
	public void updateEffects()
	{
	}
	
	public void updateHp()
	{
	}
	
	public void updateInfo()
	{
		moveNextTask.update();
	}
	
	public void updateMp()
	{
	}
	
	/**
	 * Method updateReuse.
	 * @param skill Skill
	 * @param reuseDelay int
	 */
	public void updateReuse(Skill skill, int reuseDelay)
	{
	}
	
	public void updateStamina()
	{
	}
	
	/**
	 * Method isRooted.
	 * @return boolean
	 */
	public boolean isRooted()
	{
		return rooted;
	}
	
	/**
	 * Method setSummon.
	 * @param summon Summon
	 */
	public void setSummon(Summon summon)
	{
		this.summon = summon;
	}
	
	/**
	 * Method getChargeSkill.
	 * @return Skill
	 */
	public Skill getChargeSkill()
	{
		return chargeSkill;
	}
	
	/**
	 * Method setChargeSkill.
	 * @param chargeSkill Skill
	 */
	public void setChargeSkill(Skill chargeSkill)
	{
		this.chargeSkill = chargeSkill;
	}
	
	/**
	 * Method isRangeClass.
	 * @return boolean
	 */
	public boolean isRangeClass()
	{
		return false;
	}
	
	/**
	 * Method isBroadcastEndSkillForCollision.
	 * @return boolean
	 */
	public boolean isBroadcastEndSkillForCollision()
	{
		return false;
	}
	
	/**
	 * Method containsEffect.
	 * @param skill Skill
	 * @return boolean
	 */
	public boolean containsEffect(Skill skill)
	{
		final EffectList effectList = getEffectList();
		
		if (effectList == null)
		{
			return false;
		}
		
		return effectList.contains(skill);
	}
	
	/**
	 * Method isPK.
	 * @return boolean
	 */
	public boolean isPK()
	{
		return false;
	}
}
