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
package tera.gameserver.model.ai.npc;

import tera.gameserver.model.Character;
import tera.gameserver.model.NpcAIState;
import tera.gameserver.model.ai.CharacterAI;
import tera.gameserver.model.ai.npc.taskfactory.TaskFactory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.skillengine.Skill;
import tera.util.LocalObjects;
import tera.util.Location;

/**
 * @author Ronn
 * @param <T>
 */
public interface NpcAI<T extends Npc> extends CharacterAI
{
	/**
	 * Method addCastMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 */
	void addCastMoveTask(float x, float y, float z, Skill skill, Character target);
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 */
	void addCastTask(Skill skill, Character target);
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param heading int
	 */
	void addCastTask(Skill skill, Character target, int heading);
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param heading int
	 * @param message String
	 */
	void addCastTask(Skill skill, Character target, int heading, String message);
	
	/**
	 * Method addCastTask.
	 * @param skill Skill
	 * @param target Character
	 * @param message String
	 */
	void addCastTask(Skill skill, Character target, String message);
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param update boolean
	 */
	void addMoveTask(float x, float y, float z, boolean update);
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param update boolean
	 * @param message String
	 */
	void addMoveTask(float x, float y, float z, boolean update, String message);
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 */
	void addMoveTask(float x, float y, float z, Skill skill, Character target);
	
	/**
	 * Method addMoveTask.
	 * @param x float
	 * @param y float
	 * @param z float
	 * @param skill Skill
	 * @param target Character
	 * @param message String
	 */
	void addMoveTask(float x, float y, float z, Skill skill, Character target, String message);
	
	/**
	 * Method addMoveTask.
	 * @param loc Location
	 * @param update boolean
	 */
	void addMoveTask(Location loc, boolean update);
	
	/**
	 * Method addMoveTask.
	 * @param loc Location
	 * @param update boolean
	 * @param message String
	 */
	void addMoveTask(Location loc, boolean update, String message);
	
	/**
	 * Method addNoticeTask.
	 * @param target Character
	 * @param fast boolean
	 */
	void addNoticeTask(Character target, boolean fast);
	
	/**
	 * Method addNoticeTask.
	 * @param target Character
	 * @param fast boolean
	 * @param message String
	 */
	void addNoticeTask(Character target, boolean fast, String message);
	
	/**
	 * Method addTask.
	 * @param task Task
	 */
	void addTask(Task task);
	
	/**
	 * Method checkAggression.
	 * @param target Character
	 * @return boolean
	 */
	boolean checkAggression(Character target);
	
	/**
	 * Method doTask.
	 * @param actor T
	 * @param currentTime long
	 * @param local LocalObjects
	 * @return boolean
	 */
	boolean doTask(T actor, long currentTime, LocalObjects local);
	
	/**
	 * Method getAttackedCount.
	 * @return int
	 */
	int getAttackedCount();
	
	/**
	 * Method getAttackHeading.
	 * @return Integer
	 */
	Integer getAttackHeading();
	
	/**
	 * Method getClearAggro.
	 * @return long
	 */
	long getClearAggro();
	
	/**
	 * Method getCurrentFactory.
	 * @return TaskFactory
	 */
	TaskFactory getCurrentFactory();
	
	/**
	 * Method getCurrentState.
	 * @return NpcAIState
	 */
	NpcAIState getCurrentState();
	
	/**
	 * Method getLastAttacked.
	 * @return long
	 */
	long getLastAttacked();
	
	/**
	 * Method getLastMessage.
	 * @return long
	 */
	long getLastMessage();
	
	/**
	 * Method getLastNotifyIcon.
	 * @return long
	 */
	long getLastNotifyIcon();
	
	/**
	 * Method getNextRandomWalk.
	 * @return long
	 */
	long getNextRandomWalk();
	
	/**
	 * Method getNextRoutePoint.
	 * @return long
	 */
	long getNextRoutePoint();
	
	/**
	 * Method getRouteIndex.
	 * @return int
	 */
	int getRouteIndex();
	
	/**
	 * Method getTarget.
	 * @return Character
	 */
	Character getTarget();
	
	/**
	 * Method isActiveDialog.
	 * @return boolean
	 */
	boolean isActiveDialog();
	
	/**
	 * Method isWaitingTask.
	 * @return boolean
	 */
	boolean isWaitingTask();
	
	/**
	 * Method removeTask.
	 * @param task Task
	 */
	void removeTask(Task task);
	
	/**
	 * Method setAttackedCount.
	 * @param count int
	 */
	void setAttackedCount(int count);
	
	/**
	 * Method setAttackHeading.
	 * @param attackHeading Integer
	 */
	void setAttackHeading(Integer attackHeading);
	
	/**
	 * Method setClearAggro.
	 * @param clearAggro long
	 */
	void setClearAggro(long clearAggro);
	
	/**
	 * Method setLastAttacked.
	 * @param lastAttacked long
	 */
	void setLastAttacked(long lastAttacked);
	
	/**
	 * Method setLastMessage.
	 * @param time long
	 */
	void setLastMessage(long time);
	
	/**
	 * Method setLastNotifyIcon.
	 * @param lastNotifyIcon long
	 */
	void setLastNotifyIcon(long lastNotifyIcon);
	
	/**
	 * Method setNewState.
	 * @param state NpcAIState
	 */
	void setNewState(NpcAIState state);
	
	/**
	 * Method setNextRandomWalk.
	 * @param nextRandomWalk long
	 */
	void setNextRandomWalk(long nextRandomWalk);
	
	/**
	 * Method setNextRoutePoint.
	 * @param nextRoutePoint long
	 */
	void setNextRoutePoint(long nextRoutePoint);
	
	/**
	 * Method setRouteIndex.
	 * @param routeIndex int
	 */
	void setRouteIndex(int routeIndex);
	
	/**
	 * Method setTarget.
	 * @param target Character
	 */
	void setTarget(Character target);
}