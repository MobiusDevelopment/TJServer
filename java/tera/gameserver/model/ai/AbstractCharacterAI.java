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
package tera.gameserver.model.ai;

import tera.Config;
import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.Guild;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.Party;
import tera.gameserver.model.SayType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillType;
import tera.gameserver.network.serverpackets.CharSay;
import tera.gameserver.network.serverpackets.Emotion;
import tera.gameserver.network.serverpackets.NpcDialogWindow;
import tera.gameserver.network.serverpackets.NpcSpeak;
import tera.gameserver.network.serverpackets.RequestNpcInteractionSuccess;
import tera.util.LocalObjects;

import rlib.util.array.Array;

/**
 * @author Ronn
 * @created 12.04.2012
 * @param <E>
 */
public abstract class AbstractCharacterAI<E extends Character> extends AbstractAI<E> implements CharacterAI
{
	/**
	 * Constructor for AbstractCharacterAI.
	 * @param actor E
	 */
	public AbstractCharacterAI(E actor)
	{
		super(actor);
	}
	
	/**
	 * Method isGlobalAI.
	 * @return boolean
	 * @see tera.gameserver.model.ai.CharacterAI#isGlobalAI()
	 */
	@Override
	public boolean isGlobalAI()
	{
		return false;
	}
	
	/**
	 * Method notifyAgression.
	 * @param attacker Character
	 * @param aggro long
	 * @see tera.gameserver.model.ai.AI#notifyAgression(Character, long)
	 */
	@Override
	public void notifyAgression(Character attacker, long aggro)
	{
		log.warning(this, "notifyAgression() not supported method.");
	}
	
	/**
	 * Method notifyAppliedEffect.
	 * @param effect Effect
	 * @see tera.gameserver.model.ai.AI#notifyAppliedEffect(Effect)
	 */
	@Override
	public void notifyAppliedEffect(Effect effect)
	{
		log.warning(this, "notifyAppliedEffect() not supported method.");
	}
	
	/**
	 * Method notifyArrived.
	 * @see tera.gameserver.model.ai.AI#notifyArrived()
	 */
	@Override
	public void notifyArrived()
	{
		log.warning(this, "notifyArrived() not supported method.");
	}
	
	/**
	 * Method notifyArrivedBlocked.
	 * @see tera.gameserver.model.ai.AI#notifyArrivedBlocked()
	 */
	@Override
	public void notifyArrivedBlocked()
	{
		log.warning(this, "notifyArrivedBlocked() not supported method.");
	}
	
	/**
	 * Method notifyArrivedTarget.
	 * @param target TObject
	 * @see tera.gameserver.model.ai.AI#notifyArrivedTarget(TObject)
	 */
	@Override
	public void notifyArrivedTarget(TObject target)
	{
		log.warning(this, "not supported method.");
	}
	
	/**
	 * Method notifyAttack.
	 * @param attacked Character
	 * @param skill Skill
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyAttack(Character, Skill, int)
	 */
	@Override
	public void notifyAttack(Character attacked, Skill skill, int damage)
	{
		log.warning(this, "notifyAttack() not supported method.");
	}
	
	/**
	 * Method notifyAttacked.
	 * @param attacker Character
	 * @param skill Skill
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyAttacked(Character, Skill, int)
	 */
	@Override
	public void notifyAttacked(Character attacker, Skill skill, int damage)
	{
		log.warning(this, "notifyAttacked() not supported method.");
	}
	
	/**
	 * Method notifyClanAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyClanAttacked(Character, Character, int)
	 */
	@Override
	public void notifyClanAttacked(Character attackedMember, Character attacker, int damage)
	{
		log.warning(this, "notifyClanAttacked() not supported method.");
	}
	
	/**
	 * Method notifyCollectResourse.
	 * @param resourse ResourseInstance
	 * @see tera.gameserver.model.ai.AI#notifyCollectResourse(ResourseInstance)
	 */
	@Override
	public void notifyCollectResourse(ResourseInstance resourse)
	{
		log.warning(this, "notifyCollectResourse() not supported method.");
	}
	
	/**
	 * Method notifyDead.
	 * @param killer Character
	 * @see tera.gameserver.model.ai.AI#notifyDead(Character)
	 */
	@Override
	public void notifyDead(Character killer)
	{
		log.warning(this, "notifyDead() not supported method.");
	}
	
	/**
	 * Method notifyFinishCasting.
	 * @param skill Skill
	 * @see tera.gameserver.model.ai.AI#notifyFinishCasting(Skill)
	 */
	@Override
	public void notifyFinishCasting(Skill skill)
	{
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			actor.sendMessage("end cast skill.");
		}
	}
	
	/**
	 * Method notifyPartyAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 * @see tera.gameserver.model.ai.AI#notifyPartyAttacked(Character, Character, int)
	 */
	@Override
	public void notifyPartyAttacked(Character attackedMember, Character attacker, int damage)
	{
		log.warning(this, "notifyPartyAttacked() not supported method.");
	}
	
	/**
	 * Method notifyPickUpItem.
	 * @param item ItemInstance
	 * @see tera.gameserver.model.ai.AI#notifyPickUpItem(ItemInstance)
	 */
	@Override
	public void notifyPickUpItem(ItemInstance item)
	{
		log.warning(this, "notifyPickUpItem() not supported method.");
	}
	
	/**
	 * Method notifySpawn.
	 * @see tera.gameserver.model.ai.AI#notifySpawn()
	 */
	@Override
	public void notifySpawn()
	{
		log.warning(this, "notifySpawn() not supported method.");
	}
	
	/**
	 * Method notifyStartCasting.
	 * @param skill Skill
	 * @see tera.gameserver.model.ai.AI#notifyStartCasting(Skill)
	 */
	@Override
	public void notifyStartCasting(Skill skill)
	{
		if (Config.DEVELOPER_MAIN_DEBUG)
		{
			actor.sendMessage("start cast skill.");
		}
	}
	
	/**
	 * Method notifyStartDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStartDialog(Player)
	 */
	@Override
	public void notifyStartDialog(Player player)
	{
		log.warning(this, "notifyStartDialog() not supported method.");
	}
	
	/**
	 * Method notifyStopDialog.
	 * @param player Player
	 * @see tera.gameserver.model.ai.AI#notifyStopDialog(Player)
	 */
	@Override
	public void notifyStopDialog(Player player)
	{
		log.warning(this, "notifyStopDialog() not supported method.");
	}
	
	/**
	 * Method startAction.
	 * @param action Action
	 * @see tera.gameserver.model.ai.AI#startAction(Action)
	 */
	@Override
	public void startAction(Action action)
	{
		log.warning(this, "startAction() not supported method.");
	}
	
	/**
	 * Method startActive.
	 * @see tera.gameserver.model.ai.AI#startActive()
	 */
	@Override
	public void startActive()
	{
		log.warning(this, "startActive() not supported method.");
	}
	
	/**
	 * Method startAITask.
	 * @see tera.gameserver.model.ai.CharacterAI#startAITask()
	 */
	@Override
	public void startAITask()
	{
		log.warning(this, "startAITask() not supported method.");
	}
	
	/**
	 * Method startCast.
	 * @param startX float
	 * @param startY float
	 * @param startZ float
	 * @param skill Skill
	 * @param state int
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.ai.AI#startCast(float, float, float, Skill, int, int, float, float, float)
	 */
	@Override
	public void startCast(float startX, float startY, float startZ, Skill skill, int state, int heading, float targetX, float targetY, float targetZ)
	{
		if (actor.isAttackBlocking() || (actor.isOwerturned() && (skill.getSkillType() != SkillType.OWERTURNED_STRIKE)))
		{
			return;
		}
		
		actor.doCast(startX, startY, startZ, skill, state, heading, targetX, targetY, targetZ);
	}
	
	/**
	 * Method startCast.
	 * @param skill Skill
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @see tera.gameserver.model.ai.AI#startCast(Skill, int, float, float, float)
	 */
	@Override
	public void startCast(Skill skill, int heading, float targetX, float targetY, float targetZ)
	{
		startCast(actor.getX(), actor.getY(), actor.getZ(), skill, 0, heading, targetX, targetY, targetZ);
	}
	
	/**
	 * Method startCollectResourse.
	 * @param resourse ResourseInstance
	 * @see tera.gameserver.model.ai.AI#startCollectResourse(ResourseInstance)
	 */
	@Override
	public void startCollectResourse(ResourseInstance resourse)
	{
		if (resourse == null)
		{
			log.warning(this, new Exception("not found resourse"));
			return;
		}
		
		final E actor = getActor();
		
		if (actor.isOnMount())
		{
			actor.getOffMount();
			return;
		}
		
		resourse.collectMe(actor);
	}
	
	/**
	 * Method startDressItem.
	 * @param index int
	 * @param itemId int
	 * @see tera.gameserver.model.ai.AI#startDressItem(int, int)
	 */
	@Override
	public void startDressItem(int index, int itemId)
	{
		log.warning(this, "not supported method.");
	}
	
	/**
	 * Method startEmotion.
	 * @param type EmotionType
	 * @see tera.gameserver.model.ai.AI#startEmotion(EmotionType)
	 */
	@Override
	public void startEmotion(EmotionType type)
	{
		final E actor = getActor();
		
		if ((actor == null) || actor.isBattleStanced() || actor.isCollecting() || actor.isMoving() || actor.isCastingNow() || actor.isAllBlocking() || actor.isFlyingPegas())
		{
			return;
		}
		
		actor.broadcastPacket(Emotion.getInstance(actor, type));
	}
	
	/**
	 * Method startItemPickUp.
	 * @param item ItemInstance
	 * @see tera.gameserver.model.ai.AI#startItemPickUp(ItemInstance)
	 */
	@Override
	public void startItemPickUp(ItemInstance item)
	{
		log.warning(this, "startItemPickUp() not supported method.");
	}
	
	/**
	 * Method startIteract.
	 * @param object Character
	 * @see tera.gameserver.model.ai.AI#startIteract(Character)
	 */
	@Override
	public void startIteract(Character object)
	{
		log.warning(this, "startIteract() not supported method.");
	}
	
	/**
	 * Method startMove.
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
	 * @see tera.gameserver.model.ai.AI#startMove(float, float, float, int, MoveType, float, float, float, boolean, boolean)
	 */
	@Override
	public void startMove(float startX, float startY, float startZ, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket)
	{
		final E actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		if (actor.isMovementDisabled())
		{
			return;
		}
		
		actor.moveToLocation(startX, startY, startZ, heading, type, targetX, targetY, targetZ, broadCastMove, sendSelfPacket);
	}
	
	/**
	 * Method startMove.
	 * @param heading int
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param broadCastMove boolean
	 * @param sendSelfPacket boolean
	 * @see tera.gameserver.model.ai.AI#startMove(int, MoveType, float, float, float, boolean, boolean)
	 */
	@Override
	public void startMove(int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket)
	{
		startMove(actor.getX(), actor.getY(), actor.getZ(), heading, type, targetX, targetY, targetZ, broadCastMove, sendSelfPacket);
	}
	
	/**
	 * Method startNpcSpeak.
	 * @param npc Npc
	 * @see tera.gameserver.model.ai.AI#startNpcSpeak(Npc)
	 */
	@Override
	public void startNpcSpeak(Npc npc)
	{
		final E actor = getActor();
		
		if ((npc == null) || (actor == null) || !actor.isPlayer())
		{
			return;
		}
		
		final Player player = actor.getPlayer();
		player.clearLinks();
		
		if (!npc.checkInteraction(player))
		{
			player.sendPacket(RequestNpcInteractionSuccess.getInstance(RequestNpcInteractionSuccess.NOT_SUCCESS), true);
		}
		else
		{
			player.setLastNpc(npc);
			player.sendPacket(RequestNpcInteractionSuccess.getInstance(RequestNpcInteractionSuccess.SUCCEESS), true);
			player.sendPacket(NpcSpeak.getInstance(player, npc), true);
			player.sendPacket(NpcDialogWindow.getInstance(npc, player, npc.getLinks(player)), true);
		}
	}
	
	/**
	 * Method startRest.
	 * @see tera.gameserver.model.ai.AI#startRest()
	 */
	@Override
	public void startRest()
	{
		log.warning(this, "startRest() not supported method.");
	}
	
	/**
	 * Method startSay.
	 * @param text String
	 * @param type SayType
	 * @see tera.gameserver.model.ai.AI#startSay(String, SayType)
	 */
	@Override
	public void startSay(String text, SayType type)
	{
		final E actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		final LocalObjects local = LocalObjects.get();
		
		switch (type)
		{
			case MAIN_CHAT:
			{
				final Array<Player> around = World.getAround(Player.class, local.getNextPlayerList(), actor, 300);
				final Player[] array = around.array();
				final CharSay say = CharSay.getInstance(actor.getName(), text, type, actor.getObjectId(), actor.getSubId());
				
				for (int i = 0, length = around.size(); i < length; i++)
				{
					say.increaseSends();
				}
				
				say.increaseSends();
				
				for (int i = 0, length = around.size(); i < length; i++)
				{
					array[i].sendPacket(say, false);
				}
				
				actor.sendPacket(say, false);
				break;
			}
			
			case PARTY_CHAT:
			{
				final Party party = actor.getParty();
				
				if (party != null)
				{
					party.sendMessage((Player) actor, text);
				}
				
				break;
			}
			
			case GUILD_CHAT:
			{
				final Guild guild = actor.getGuild();
				
				if (guild != null)
				{
					guild.sendMessage((Player) actor, text);
				}
				
				break;
			}
			
			case SHAUT_CHAT:
				actor.broadcastPacket(CharSay.getInstance(actor.getName(), text, type, actor.getObjectId(), actor.getSubId()));
				break;
			
			case TRADE_CHAT:
			case LOOKING_FOR_GROUP:
			{
				final CharSay say = CharSay.getInstance(actor.getName(), text, type, actor.getObjectId(), actor.getSubId());
				final Array<Player> players = World.getPlayers();
				players.readLock();
				
				try
				{
					for (int i = 0, length = players.size(); i < length; i++)
					{
						say.increaseSends();
					}
					
					final Player[] array = players.array();
					
					for (int i = 0, length = players.size(); i < length; i++)
					{
						array[i].sendPacket(say, false);
					}
				}
				
				finally
				{
					players.readUnlock();
				}
				break;
			}
		}
	}
	
	/**
	 * Method startUseItem.
	 * @param item ItemInstance
	 * @param heading int
	 * @param isHeb boolean
	 * @see tera.gameserver.model.ai.AI#startUseItem(ItemInstance, int, boolean)
	 */
	@Override
	public void startUseItem(ItemInstance item, int heading, boolean isHeb)
	{
		log.warning(this, "startUseItem() not supported method.");
	}
	
	/**
	 * Method stopAITask.
	 * @see tera.gameserver.model.ai.CharacterAI#stopAITask()
	 */
	@Override
	public void stopAITask()
	{
		log.warning(this, "stopAITask() not supported method.");
	}
	
	/**
	 * Method clearTaskList.
	 * @see tera.gameserver.model.ai.CharacterAI#clearTaskList()
	 */
	@Override
	public void clearTaskList()
	{
		log.warning(this, "clearTaskList() not supported method.");
	}
	
	/**
	 * Method abortAttack.
	 * @see tera.gameserver.model.ai.CharacterAI#abortAttack()
	 */
	@Override
	public void abortAttack()
	{
		log.warning(this, "abortAttack() not supported method.");
	}
	
	/**
	 * Method startAttack.
	 * @param target Character
	 * @see tera.gameserver.model.ai.CharacterAI#startAttack(Character)
	 */
	@Override
	public void startAttack(Character target)
	{
		log.warning(this, "startAttack() not supported method.");
	}
}
