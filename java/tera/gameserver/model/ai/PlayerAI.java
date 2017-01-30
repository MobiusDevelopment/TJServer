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

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MoveType;
import tera.gameserver.model.Party;
import tera.gameserver.model.SayType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.actions.Action;
import tera.gameserver.model.actions.dialogs.ActionDialog;
import tera.gameserver.model.equipment.Equipment;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.summons.Summon;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.model.skillengine.SkillType;

/**
 * @author Ronn
 */
public class PlayerAI extends AbstractCharacterAI<Player>
{
	
	private static boolean checkStartSkill(Player actor, Skill skill)
	{
		if ((actor.isOnMount() && (skill != actor.getMountSkill())) || actor.isAttackBlocking() || (actor.isRooted() && (skill.getMoveDistance() != 0)) || (actor.isOwerturned() && (skill.getSkillType() != SkillType.OWERTURNED_STRIKE)))
		{
			final Skill chargeSkill = actor.getChargeSkill();
			
			if (chargeSkill != null)
			{
				actor.setChargeSkill(null);
				actor.setChargeLevel(0);
				chargeSkill.endSkill(actor, actor.getX(), actor.getY(), actor.getZ(), true);
			}
			
			return false;
		}
		
		return true;
	}
	
	public PlayerAI(Player actor)
	{
		super(actor);
	}
	
	@Override
	public void notifyAttacked(Character attacker, Skill skill, int damage)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		final Summon summon = actor.getSummon();
		
		if (summon != null)
		{
			summon.getAI().notifyAttacked(attacker, skill, damage);
		}
	}
	
	@Override
	public void startAction(Action action)
	{
		if (action == null)
		{
			return;
		}
		
		if (!action.test())
		{
			action.cancel(null);
			return;
		}
		
		action.invite();
	}
	
	@Override
	public synchronized void startCast(float startX, float startY, float startZ, Skill skill, int state, int heading, float targetX, float targetY, float targetZ)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		if (actor.isCollecting())
		{
			actor.abortCollect();
		}
		
		if (!checkStartSkill(actor, skill))
		{
			return;
		}
		
		actor.doCast(startX, startY, startZ, skill, state, heading, targetX, targetY, targetZ);
	}
	
	@Override
	public void startDressItem(int index, int itemId)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		if (actor.isCollecting())
		{
			return;
		}
		
		final Equipment equipment = actor.getEquipment();
		final Inventory inventory = actor.getInventory();
		
		if ((equipment == null) || (inventory == null))
		{
			return;
		}
		
		if (index < 20)
		{
			equipment.shootItem(inventory, index - 1, itemId);
		}
		else
		{
			equipment.dressItem(inventory, inventory.getCell(index - 20));
		}
	}
	
	@Override
	public void startItemPickUp(ItemInstance item)
	{
		if (item == null)
		{
			return;
		}
		
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		if (actor.isStuned() || actor.isCollecting())
		{
			return;
		}
		
		if (actor.isOnMount())
		{
			actor.sendMessage("You cannot pickup while mounted.");
			return;
		}
		
		final Party party = item.getTempOwnerParty();
		
		if (party != null)
		{
			if (actor.getParty() != party)
			{
				actor.sendMessage("Not in your party.");
			}
			else
			{
				item.pickUpMe(actor);
			}
			
			return;
		}
		
		final TObject owner = item.getTempOwner();
		
		if (owner != null)
		{
			if (owner != actor)
			{
				actor.sendMessage("You need to target this.");
			}
			else
			{
				item.pickUpMe(actor);
			}
			
			return;
		}
		
		item.pickUpMe(actor);
	}
	
	@Override
	public void startMove(float startX, float startY, float startZ, int heading, MoveType type, float targetX, float targetY, float targetZ, boolean broadCastMove, boolean sendSelfPacket)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		if (actor.isCollecting())
		{
			actor.abortCollect();
		}
		
		super.startMove(startX, startY, startZ, heading, type, targetX, targetY, targetZ, broadCastMove, sendSelfPacket);
		final ActionDialog dialog = actor.getLastActionDialog();
		
		if (dialog != null)
		{
			dialog.cancel(actor);
		}
	}
	
	@Override
	public void startSay(String text, SayType type)
	{
		final Player actor = getActor();
		
		if (actor == null)
		{
			log.warning(this, new Exception("not found actor"));
			return;
		}
		
		boolean banned = false;
		
		if (actor.getEndChatBan() > 0)
		{
			if (actor.getEndChatBan() > System.currentTimeMillis())
			{
				banned = true;
			}
			else
			{
				actor.setEndChatBan(0);
			}
		}
		
		if (banned && ((type == SayType.MAIN_CHAT) || (type == SayType.LOOKING_FOR_GROUP) || (type == SayType.SHAUT_CHAT) || (type == SayType.TRADE_CHAT) || (type == SayType.LOOKING_FOR_GROUP)))
		{
			actor.sendMessage("Your have been blocked.");
			return;
		}
		
		super.startSay(text, type);
	}
	
	@Override
	public void startUseItem(ItemInstance item, int heading, boolean isHerb)
	{
		final Player actor = getActor();
		
		if ((actor == null) || (item == null) || !item.isCommon() || (item.getItemLevel() > actor.getLevel()) || actor.isOwerturned())
		{
			return;
		}
		
		if (actor.isCollecting())
		{
			actor.abortCollect();
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		eventManager.notifyUseItem(item, actor);
		final Skill skill = item.getActiveSkill();
		
		if (skill == null)
		{
			return;
		}
		
		actor.doCast(skill, heading, item);
	}
	
	@Override
	public void notifyArrived()
	{
	}
	
	@Override
	public void notifyAppliedEffect(Effect effect)
	{
	}
	
	@Override
	public void notifySpawn()
	{
	}
	
	@Override
	public void notifyAttack(Character attacked, Skill skill, int damage)
	{
	}
	
	@Override
	public void notifyPickUpItem(ItemInstance item)
	{
	}
	
	@Override
	public void notifyDead(Character killer)
	{
	}
	
	@Override
	public void notifyCollectResourse(ResourseInstance resourse)
	{
	}
}
