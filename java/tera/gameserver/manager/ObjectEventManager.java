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

import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.Guild;
import tera.gameserver.model.Party;
import tera.gameserver.model.SkillLearn;
import tera.gameserver.model.TObject;
import tera.gameserver.model.ai.AI;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.listeners.DeleteListener;
import tera.gameserver.model.listeners.DieListener;
import tera.gameserver.model.listeners.LevelUpListener;
import tera.gameserver.model.listeners.PlayerSelectListener;
import tera.gameserver.model.listeners.PlayerSpawnListener;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Playable;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestEvent;
import tera.gameserver.model.quests.QuestEventType;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.quests.QuestUtils;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.ChanceType;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;
import tera.util.LocalObjects;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ObjectEventManager
{
	private static final Logger log = Loggers.getLogger(ObjectEventManager.class);
	
	private static ObjectEventManager instance;
	
	/**
	 * Method getInstance.
	 * @return ObjectEventManager
	 */
	public static ObjectEventManager getInstance()
	{
		if (instance == null)
		{
			instance = new ObjectEventManager();
		}
		
		return instance;
	}
	
	private final Array<DieListener> dieListeners;
	
	private final Array<DeleteListener> deleteListeners;
	
	private final Array<LevelUpListener> levelUpListeners;
	
	private final Array<PlayerSpawnListener> playerSpawnListeners;
	
	private final Array<PlayerSelectListener> playerSelectListeners;
	
	private ObjectEventManager()
	{
		dieListeners = Arrays.toConcurrentArray(DieListener.class);
		deleteListeners = Arrays.toConcurrentArray(DeleteListener.class);
		levelUpListeners = Arrays.toConcurrentArray(LevelUpListener.class);
		playerSpawnListeners = Arrays.toConcurrentArray(PlayerSpawnListener.class);
		playerSelectListeners = Arrays.toConcurrentArray(PlayerSelectListener.class);
		log.info("initialized.");
	}
	
	/**
	 * Method getPlayerSelectListeners.
	 * @return Array<PlayerSelectListener>
	 */
	public Array<PlayerSelectListener> getPlayerSelectListeners()
	{
		return playerSelectListeners;
	}
	
	/**
	 * Method addDeleteListener.
	 * @param listener DeleteListener
	 */
	public final void addDeleteListener(DeleteListener listener)
	{
		deleteListeners.add(listener);
	}
	
	/**
	 * Method addDieListener.
	 * @param listener DieListener
	 */
	public final void addDieListener(DieListener listener)
	{
		dieListeners.add(listener);
	}
	
	/**
	 * Method addLevelUpListener.
	 * @param listener LevelUpListener
	 */
	public final void addLevelUpListener(LevelUpListener listener)
	{
		levelUpListeners.add(listener);
	}
	
	/**
	 * Method addPlayerSpawnListener.
	 * @param listener PlayerSpawnListener
	 */
	public final void addPlayerSpawnListener(PlayerSpawnListener listener)
	{
		playerSpawnListeners.add(listener);
	}
	
	/**
	 * Method addPlayerSelectListener.
	 * @param listener PlayerSelectListener
	 */
	public final void addPlayerSelectListener(PlayerSelectListener listener)
	{
		playerSelectListeners.add(listener);
	}
	
	/**
	 * Method getDeleteListeners.
	 * @return Array<DeleteListener>
	 */
	public Array<DeleteListener> getDeleteListeners()
	{
		return deleteListeners;
	}
	
	/**
	 * Method getDieListeners.
	 * @return Array<DieListener>
	 */
	public Array<DieListener> getDieListeners()
	{
		return dieListeners;
	}
	
	/**
	 * Method getLevelUpListeners.
	 * @return Array<LevelUpListener>
	 */
	public Array<LevelUpListener> getLevelUpListeners()
	{
		return levelUpListeners;
	}
	
	/**
	 * Method getPlayerSpawnListeners.
	 * @return Array<PlayerSpawnListener>
	 */
	public Array<PlayerSpawnListener> getPlayerSpawnListeners()
	{
		return playerSpawnListeners;
	}
	
	/**
	 * Method notifyAddNpc.
	 * @param player Player
	 * @param npc Npc
	 */
	public void notifyAddNpc(Player player, Npc npc)
	{
		npc.updateQuestInteresting(player, false);
		final QuestList questList = player.getQuestList();
		
		if ((questList != null) && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setPlayer(player);
			event.setNpc(npc);
			event.setType(QuestEventType.ADD_NPC);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifyAgression.
	 * @param object Character
	 * @param attacker Character
	 * @param aggro long
	 */
	public final void notifyAgression(Character object, Character attacker, long aggro)
	{
		object.getAI().notifyAgression(attacker, aggro);
	}
	
	/**
	 * Method notifyAppliedEffect.
	 * @param effected Character
	 * @param effect Effect
	 */
	public final void notifyAppliedEffect(Character effected, Effect effect)
	{
		effected.getAI().notifyAppliedEffect(effect);
	}
	
	/**
	 * Method notifyArrived.
	 * @param object Character
	 */
	public final void notifyArrived(Character object)
	{
		object.getAI().notifyArrived();
	}
	
	/**
	 * Method notifyArrivedBlocked.
	 * @param object Character
	 */
	public final void notifyArrivedBlocked(Character object)
	{
		object.getAI().notifyArrivedBlocked();
	}
	
	/**
	 * Method notifyArrivedTarget.
	 * @param object Character
	 * @param target TObject
	 */
	public final void notifyArrivedTarget(Character object, TObject target)
	{
		object.getAI().notifyArrivedTarget(target);
	}
	
	/**
	 * Method notifyAttack.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 * @param damage int
	 * @param crit boolean
	 */
	public final void notifyAttack(Character attacker, Character attacked, Skill skill, int damage, boolean crit)
	{
		attacker.getAI().notifyAttack(attacked, skill, damage);
		final EffectList effectList = attacker.getEffectList();
		effectList.exitNoAttackEffects();
		
		if (attacker.isOnMount())
		{
			attacker.getOffMount();
		}
		
		final Character owner = attacker.getOwner();
		
		if (owner != null)
		{
			if (owner.isOnMount())
			{
				owner.getOffMount();
			}
			
			owner.startBattleStance(attacker);
		}
		
		attacker.startBattleStance(attacked);
		attacker.applyChanceFunc(crit ? ChanceType.ON_CRIT_ATTACK : ChanceType.ON_ATTACK, attacked, skill);
	}
	
	/**
	 * Method notifyAttacked.
	 * @param attacked Character
	 * @param attacker Character
	 * @param skill Skill
	 * @param damage int
	 * @param crit boolean
	 */
	public final void notifyAttacked(Character attacked, Character attacker, Skill skill, int damage, boolean crit)
	{
		attacked.getAI().notifyAttacked(attacker, skill, damage);
		final EffectList effectList = attacked.getEffectList();
		effectList.exitNoAttackedEffects();
		
		if (attacked.isOnMount())
		{
			attacked.getOffMount();
		}
		
		final Character owner = attacked.getOwner();
		
		if (owner != null)
		{
			if (owner.isOnMount())
			{
				owner.getOffMount();
			}
			
			owner.startBattleStance(attacker);
		}
		
		attacked.startBattleStance(attacker);
		attacked.applyChanceFunc(crit ? ChanceType.ON_CRIT_ATTACKED : ChanceType.ON_ATTACKED, attacker, skill);
	}
	
	/**
	 * Method notifyChangedLevel.
	 * @param playable Playable
	 */
	public final void notifyChangedLevel(Playable playable)
	{
		if (playable.isPlayer())
		{
			final Player player = playable.getPlayer();
			final Guild guild = playable.getGuild();
			
			if (guild != null)
			{
				guild.updateMember(player);
			}
			
			player.updateInfo();
			final DataBaseManager manager = DataBaseManager.getInstance();
			manager.updatePlayerLevel(player);
			final Array<LevelUpListener> listeners = getLevelUpListeners();
			
			if (listeners.isEmpty())
			{
				return;
			}
			
			listeners.readLock();
			
			try
			{
				final LevelUpListener[] array = listeners.array();
				
				for (int i = 0, length = listeners.size(); i < length; i++)
				{
					array[i].onLevelUp(player);
				}
			}
			
			finally
			{
				listeners.readUnlock();
			}
		}
	}
	
	/**
	 * Method notifyChangedZoneId.
	 * @param playable Playable
	 */
	public final void notifyChangedZoneId(Playable playable)
	{
		if (playable.isPlayer())
		{
			final Player player = playable.getPlayer();
			final Guild guild = playable.getGuild();
			
			if (guild != null)
			{
				guild.updateMember(player);
			}
			
			final DataBaseManager manager = DataBaseManager.getInstance();
			manager.updatePlayerZoneId(player);
		}
	}
	
	/**
	 * Method notifyCollect.
	 * @param resourse ResourseInstance
	 * @param collector Character
	 */
	public final void notifyCollect(ResourseInstance resourse, Character collector)
	{
		if (resourse == null)
		{
			log.warning(new Exception("not found resourse"));
			return;
		}
		
		if (collector == null)
		{
			log.warning(new Exception("not found collector"));
			return;
		}
		
		final AI ai = collector.getAI();
		
		if (ai != null)
		{
			ai.notifyCollectResourse(resourse);
		}
		
		final Player player = collector.getPlayer();
		
		if (player != null)
		{
			final QuestList questList = player.getQuestList();
			
			if ((questList != null) && questList.hasActiveQuest())
			{
				final LocalObjects local = LocalObjects.get();
				final QuestEvent event = local.getNextQuestEvent();
				event.setPlayer(player);
				event.setType(QuestEventType.COLLECT_RESOURSE);
				event.setResourse(resourse);
				QuestUtils.notifyQuests(questList.getActiveQuests(), event);
			}
		}
	}
	
	/**
	 * Method notifyDead.
	 * @param character Character
	 * @param killer Character
	 */
	public final void notifyDead(Character character, Character killer)
	{
		character.getAI().notifyDead(killer);
		
		if (character.isNpc())
		{
			final Npc npc = character.getNpc();
			final Character damager = npc.getMostDamager();
			
			if (damager != null)
			{
				final Character questor = damager.isSummon() ? damager.getOwner() : damager;
				QuestList questList = questor == null ? null : questor.getQuestList();
				
				if ((questList != null) && questList.hasActiveQuest() && (questor != null))
				{
					final LocalObjects local = LocalObjects.get();
					final QuestEvent event = local.getNextQuestEvent();
					event.setNpc(npc);
					event.setPlayer(questor.getPlayer());
					event.setType(QuestEventType.KILL_NPC);
					QuestUtils.notifyQuests(questList.getActiveQuests(), event);
					final Party party = questor.getParty();
					
					if (party != null)
					{
						final Array<Player> members = party.getMembers();
						members.readLock();
						
						try
						{
							final Player[] array = members.array();
							
							for (int i = 0, length = members.size(); i < length; i++)
							{
								final Player member = array[i];
								
								if ((member == null) || (member == questor))
								{
									continue;
								}
								
								questList = member.getQuestList();
								
								if ((questList != null) && questList.hasActiveQuest())
								{
									event.setNpc(npc);
									event.setPlayer(member);
									event.setType(QuestEventType.KILL_NPC);
									QuestUtils.notifyQuests(questList.getActiveQuests(), event);
								}
							}
						}
						
						finally
						{
							members.readUnlock();
						}
					}
				}
			}
		}
		
		final Array<DieListener> listeners = getDieListeners();
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			final DieListener[] array = listeners.array();
			
			for (int i = 0, length = listeners.size(); i < length; i++)
			{
				array[i].onDie(killer, character);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method notifyDelete.
	 * @param object TObject
	 */
	public final void notifyDelete(TObject object)
	{
		final Array<DeleteListener> listeners = getDeleteListeners();
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			final DeleteListener[] array = listeners.array();
			
			for (int i = 0, length = listeners.size(); i < length; i++)
			{
				array[i].onDelete(object);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method notifyEquipmentChanged.
	 * @param owner Character
	 */
	public final void notifyEquipmentChanged(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		if (owner.isPlayer())
		{
			PacketManager.updateEquip(owner);
			PacketManager.updateInventory(owner.getPlayer());
		}
	}
	
	/**
	 * Method notifyFinishCasting.
	 * @param object Character
	 * @param skill Skill
	 */
	public final void notifyFinishCasting(Character object, Skill skill)
	{
		object.setLastSkillName(skill.getSkillName());
		object.setLastCast(System.currentTimeMillis());
		object.getAI().notifyFinishCasting(skill);
	}
	
	/**
	 * Method notifyFinishCollect.
	 * @param object Character
	 * @param resourse ResourseInstance
	 */
	public final void notifyFinishCollect(Character object, ResourseInstance resourse)
	{
		object.getAI().notifyCollectResourse(resourse);
	}
	
	/**
	 * Method notifyGuildBankChanged.
	 * @param owner Character
	 * @param startCell int
	 */
	public final void notifyGuildBankChanged(Character owner, int startCell)
	{
		if ((owner == null) || !owner.isPlayer())
		{
			return;
		}
		
		PacketManager.updateGuildBank(owner.getPlayer(), startCell);
	}
	
	/**
	 * Method notifyHpChanged.
	 * @param owner Character
	 */
	public final void notifyHpChanged(Character owner)
	{
		owner.updateHp();
	}
	
	/**
	 * Method notifyInventoryAddItem.
	 * @param object Character
	 * @param item ItemInstance
	 */
	public final void notifyInventoryAddItem(Character object, ItemInstance item)
	{
		if ((object == null) || (item == null))
		{
			log.warning(new Exception("not found object or item"));
			return;
		}
		
		final QuestList questList = object.getQuestList();
		
		if ((questList != null) && object.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setItem(item);
			event.setPlayer(object.getPlayer());
			event.setType(QuestEventType.INVENTORY_ADD_ITEM);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifyInventoryChanged.
	 * @param owner Character
	 */
	public final void notifyInventoryChanged(Character owner)
	{
		if ((owner == null) || !owner.isPlayer())
		{
			return;
		}
		
		PacketManager.updateInventory(owner.getPlayer());
	}
	
	/**
	 * Method notifyInventoryRemoveItem.
	 * @param object Character
	 * @param item ItemInstance
	 */
	public final void notifyInventoryRemoveItem(Character object, ItemInstance item)
	{
		if ((object == null) || (item == null))
		{
			log.warning(new Exception("not found object or item"));
			return;
		}
		
		final QuestList questList = object.getQuestList();
		
		if ((questList != null) && object.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setItem(item);
			event.setPlayer(object.getPlayer());
			event.setType(QuestEventType.INVENTORY_REMOVE_ITEM);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifyMpChanged.
	 * @param owner Character
	 */
	public final void notifyMpChanged(Character owner)
	{
		owner.updateMp();
	}
	
	/**
	 * Method notifyOwerturn.
	 * @param attacker Character
	 * @param attacked Character
	 * @param skill Skill
	 */
	public final void notifyOwerturn(Character attacker, Character attacked, Skill skill)
	{
		attacker.applyChanceFunc(ChanceType.ON_OWERTURN, attacked, skill);
	}
	
	/**
	 * Method notifyOwerturned.
	 * @param attacked Character
	 * @param attacker Character
	 * @param skill Skill
	 */
	public final void notifyOwerturned(Character attacked, Character attacker, Skill skill)
	{
		attacked.applyChanceFunc(ChanceType.ON_OWERTURNED, attacker, skill);
	}
	
	/**
	 * Method notifyPickUpItem.
	 * @param object Character
	 * @param item ItemInstance
	 */
	public final void notifyPickUpItem(Character object, ItemInstance item)
	{
		if ((object == null) || (item == null))
		{
			log.warning(new Exception("not found object or item"));
			return;
		}
		
		object.getAI().notifyPickUpItem(item);
		final QuestList questList = object.getQuestList();
		
		if ((questList != null) && object.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setItem(item);
			event.setPlayer(object.getPlayer());
			event.setType(QuestEventType.PICK_UP_ITEM);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifyPlayerBankChanged.
	 * @param owner Character
	 * @param startCell int
	 */
	public final void notifyPlayerBankChanged(Character owner, int startCell)
	{
		if ((owner == null) || !owner.isPlayer())
		{
			return;
		}
		
		PacketManager.updatePlayerBank(owner.getPlayer(), startCell);
	}
	
	/**
	 * Method notifyQuestMovieEnded.
	 * @param character Player
	 * @param movieId int
	 * @param force boolean
	 */
	public final void notifyQuestMovieEnded(Player character, int movieId, boolean force)
	{
		final QuestList questList = character.getQuestList();
		
		if (questList == null)
		{
			log.warning(new Exception("not found quest list for player " + character.getName()));
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		final QuestEvent event = local.getNextQuestEvent();
		event.setPlayer(character);
		event.setType(QuestEventType.QUEST_MOVIE_ENDED);
		event.setValue(movieId);
		QuestUtils.notifyQuests(questList.getActiveQuests(), event);
	}
	
	/**
	 * Method notifyShieldBlocked.
	 * @param attacked Character
	 * @param attacker Character
	 * @param skill Skill
	 */
	public final void notifyShieldBlocked(Character attacked, Character attacker, Skill skill)
	{
		attacked.addDefenseCounter();
		attacked.applyChanceFunc(ChanceType.ON_SHIELD_BLOCK, attacker, skill);
	}
	
	/**
	 * Method notifySkillLearned.
	 * @param character Character
	 * @param learn SkillLearn
	 */
	public final void notifySkillLearned(Character character, SkillLearn learn)
	{
		if ((character == null) || (learn == null))
		{
			log.warning(new Exception("not found object or item"));
			return;
		}
		
		final QuestList questList = character.getQuestList();
		
		if ((questList != null) && character.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setPlayer(character.getPlayer());
			event.setType(QuestEventType.SKILL_LEARNED);
			event.setValue(learn.getId());
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifySpawn.
	 * @param object TObject
	 */
	public final void notifySpawn(TObject object)
	{
		if (object == null)
		{
			return;
		}
		
		final AI ai = object.getAI();
		
		if (ai != null)
		{
			ai.notifySpawn();
		}
		
		final Player player = object.getPlayer();
		
		if (player != null)
		{
			final QuestList questList = player.getQuestList();
			
			if ((questList != null) && questList.hasActiveQuest())
			{
				final LocalObjects local = LocalObjects.get();
				final QuestEvent event = local.getNextQuestEvent();
				event.setPlayer(player);
				event.setType(QuestEventType.PLAYER_SPAWN);
				QuestUtils.notifyQuests(questList.getActiveQuests(), event);
			}
			
			final Array<PlayerSpawnListener> listeners = getPlayerSpawnListeners();
			
			if (listeners.isEmpty())
			{
				return;
			}
			
			listeners.readLock();
			
			try
			{
				final PlayerSpawnListener[] array = listeners.array();
				
				for (int i = 0, length = listeners.size(); i < length; i++)
				{
					array[i].onSpawn(player);
				}
			}
			
			finally
			{
				listeners.readUnlock();
			}
		}
	}
	
	/**
	 * Method notifyPlayerSelect.
	 * @param player Player
	 */
	public final void notifyPlayerSelect(Player player)
	{
		final Array<PlayerSelectListener> listeners = getPlayerSelectListeners();
		
		if (listeners.isEmpty())
		{
			return;
		}
		
		listeners.readLock();
		
		try
		{
			final PlayerSelectListener[] array = listeners.array();
			
			for (int i = 0, length = listeners.size(); i < length; i++)
			{
				array[i].onSelect(player);
			}
		}
		
		finally
		{
			listeners.readUnlock();
		}
	}
	
	/**
	 * Method notifyStaminaChanged.
	 * @param owner Character
	 */
	public final void notifyStaminaChanged(Character owner)
	{
		owner.updateStamina();
		final QuestList questList = owner.getQuestList();
		
		if ((questList != null) && owner.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setPlayer(owner.getPlayer());
			event.setType(QuestEventType.CHANGED_HEART);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method notifyStartCasting.
	 * @param object Character
	 * @param skill Skill
	 */
	public final void notifyStartCasting(Character object, Skill skill)
	{
		if (object == null)
		{
			return;
		}
		
		final AI ai = object.getAI();
		
		if (ai != null)
		{
			ai.notifyStartCasting(skill);
		}
	}
	
	/**
	 * Method notifyStartDialog.
	 * @param object Character
	 * @param player Player
	 */
	public final void notifyStartDialog(Character object, Player player)
	{
		if (object == null)
		{
			return;
		}
		
		final AI ai = object.getAI();
		
		if (ai != null)
		{
			ai.notifyStartDialog(player);
		}
	}
	
	/**
	 * Method notifyStatChanged.
	 * @param owner Character
	 */
	public final void notifyStatChanged(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		owner.updateInfo();
	}
	
	/**
	 * Method notifyStopDialog.
	 * @param object Character
	 * @param player Player
	 */
	public final void notifyStopDialog(Character object, Player player)
	{
		if (object == null)
		{
			return;
		}
		
		final AI ai = object.getAI();
		
		if (ai != null)
		{
			ai.notifyStopDialog(player);
		}
	}
	
	/**
	 * Method notifyUseItem.
	 * @param item ItemInstance
	 * @param owner Character
	 */
	public final void notifyUseItem(ItemInstance item, Character owner)
	{
		final QuestList questList = owner.getQuestList();
		
		if (owner.isPlayer() && questList.hasActiveQuest())
		{
			final LocalObjects local = LocalObjects.get();
			final QuestEvent event = local.getNextQuestEvent();
			event.setItem(item);
			event.setPlayer(owner.getPlayer());
			event.setType(QuestEventType.USE_ITEM);
			QuestUtils.notifyQuests(questList.getActiveQuests(), event);
		}
	}
	
	/**
	 * Method removeDeleteListener.
	 * @param listener DeleteListener
	 */
	public final void removeDeleteListener(DeleteListener listener)
	{
		deleteListeners.fastRemove(listener);
	}
	
	/**
	 * Method removeDieListener.
	 * @param listener DieListener
	 */
	public final void removeDieListener(DieListener listener)
	{
		dieListeners.fastRemove(listener);
	}
	
	/**
	 * Method removeLevelUpListener.
	 * @param listener LevelUpListener
	 */
	public void removeLevelUpListener(LevelUpListener listener)
	{
		levelUpListeners.fastRemove(listener);
	}
	
	/**
	 * Method removePlayerSpawnListener.
	 * @param listener PlayerSpawnListener
	 */
	public void removePlayerSpawnListener(PlayerSpawnListener listener)
	{
		playerSpawnListeners.fastRemove(listener);
	}
	
	/**
	 * Method removePlayerSelectListener.
	 * @param listener PlayerSelectListener
	 */
	public void removePlayerSelectListener(PlayerSelectListener listener)
	{
		playerSelectListeners.fastRemove(listener);
	}
}
