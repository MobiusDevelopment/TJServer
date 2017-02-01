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

import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.SayType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;

/**
 * @author Ronn
 * @created 12.04.2012
 */
public interface AI
{
	/**
	 * Method notifyAgression.
	 * @param attacker Character
	 * @param aggro long
	 */
	void notifyAgression(Character attacker, long aggro);
	
	/**
	 * Method notifyAppliedEffect.
	 * @param effect Effect
	 */
	void notifyAppliedEffect(Effect effect);
	
	void notifyArrived();
	
	void notifyArrivedBlocked();
	
	/**
	 * Method notifyArrivedTarget.
	 * @param target TObject
	 */
	void notifyArrivedTarget(TObject target);
	
	/**
	 * Method notifyAttack.
	 * @param attacked Character
	 * @param skill Skill
	 * @param damage int
	 */
	void notifyAttack(Character attacked, Skill skill, int damage);
	
	/**
	 * Method notifyAttacked.
	 * @param attacker Character
	 * @param skill Skill
	 * @param damage int
	 */
	void notifyAttacked(Character attacker, Skill skill, int damage);
	
	/**
	 * Method notifyClanAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 */
	void notifyClanAttacked(Character attackedMember, Character attacker, int damage);
	
	/**
	 * Method notifyCollectResourse.
	 * @param resourse ResourseInstance
	 */
	void notifyCollectResourse(ResourseInstance resourse);
	
	/**
	 * Method notifyDead.
	 * @param killer Character
	 */
	void notifyDead(Character killer);
	
	/**
	 * Method notifyFinishCasting.
	 * @param skill Skill
	 */
	void notifyFinishCasting(Skill skill);
	
	/**
	 * Method notifyPartyAttacked.
	 * @param attackedMember Character
	 * @param attacker Character
	 * @param damage int
	 */
	void notifyPartyAttacked(Character attackedMember, Character attacker, int damage);
	
	/**
	 * Method notifyPickUpItem.
	 * @param item ItemInstance
	 */
	void notifyPickUpItem(ItemInstance item);
	
	void notifySpawn();
	
	/**
	 * Method notifyStartCasting.
	 * @param skill Skill
	 */
	void notifyStartCasting(Skill skill);
	
	/**
	 * Method notifyStartDialog.
	 * @param player Player
	 */
	void notifyStartDialog(Player player);
	
	/**
	 * Method notifyStopDialog.
	 * @param player Player
	 */
	void notifyStopDialog(Player player);
	
	/**
	 * Method startAction.
	 * @param action Action
	 */
	void startAction(Action action);
	
	void startActive();
	
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
	 */
	void startCast(float startX, float startY, float startZ, Skill skill, int state, int heading, float targetX, float targetY, float targetZ);
	
	/**
	 * Method startCast.
	 * @param skill Skill
	 * @param heading int
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 */
	void startCast(Skill skill, int heading, float targetX, float targetY, float targetZ);
	
	/**
	 * Method startCollectResourse.
	 * @param resourse ResourseInstance
	 */
	void startCollectResourse(ResourseInstance resourse);
	
	/**
	 * Method startDressItem.
	 * @param index int
	 * @param itemId int
	 */
	void startDressItem(int index, int itemId);
	
	/**
	 * Method startEmotion.
	 * @param type EmotionType
	 */
	void startEmotion(EmotionType type);
	
	/**
	 * Method startItemPickUp.
	 * @param item ItemInstance
	 */
	void startItemPickUp(ItemInstance item);
	
	/**
	 * Method startIteract.
	 * @param object Character
	 */
	void startIteract(Character object);
	
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
	 */
	void startMove(float startX, float startY, float startZ, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket);
	
	/**
	 * Method startMove.
	 * @param heading int
	 * @param type MoveType
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param broadCastMove boolean
	 * @param sendSelfPacket boolean
	 */
	void startMove(int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket);
	
	/**
	 * Method startNpcSpeak.
	 * @param npc Npc
	 */
	void startNpcSpeak(Npc npc);
	
	void startRest();
	
	/**
	 * Method startSay.
	 * @param text String
	 * @param type SayType
	 */
	void startSay(String text, SayType type);
	
	/**
	 * Method startUseItem.
	 * @param item ItemInstance
	 * @param heading int
	 * @param isHeb boolean
	 */
	void startUseItem(ItemInstance item, int heading, boolean isHeb);
}
