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

import tera.gameserver.model.Account;
import tera.gameserver.model.AttackInfo;
import tera.gameserver.model.Character;
import tera.gameserver.model.EmotionType;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.TObject;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.dialogs.ShopDialog;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Effect;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.model.UserClient;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelOwerturn;
import tera.gameserver.network.serverpackets.CancelTargetHp;
import tera.gameserver.network.serverpackets.CharShieldBlock;
import tera.gameserver.network.serverpackets.CharTurn;
import tera.gameserver.network.serverpackets.Damage;
import tera.gameserver.network.serverpackets.Emotion;
import tera.gameserver.network.serverpackets.GuildBank;
import tera.gameserver.network.serverpackets.InventoryInfo;
import tera.gameserver.network.serverpackets.NotifyCharacter;
import tera.gameserver.network.serverpackets.NotifyCharacter.NotifyType;
import tera.gameserver.network.serverpackets.NpcNotice;
import tera.gameserver.network.serverpackets.PlayerBank;
import tera.gameserver.network.serverpackets.PlayerEquipment;
import tera.gameserver.network.serverpackets.PlayerList;
import tera.gameserver.network.serverpackets.ShopTradePacket;
import tera.gameserver.network.serverpackets.SkillLockTarget;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.network.serverpackets.TargetHp;
import tera.gameserver.network.serverpackets.UpdateStamina;

/**
 * @author Ronn
 */
public final class PacketManager
{
	/**
	 * Method showTurnCharacter.
	 * @param character Character
	 * @param newHeading int
	 * @param time int
	 */
	public static void showTurnCharacter(Character character, int newHeading, int time)
	{
		character.broadcastPacket(CharTurn.getInstance(character, newHeading, time));
	}
	
	/**
	 * Method addToFriend.
	 * @param player Player
	 * @param added Player
	 */
	public static void addToFriend(Player player, Player added)
	{
		SystemMessage message = SystemMessage.getInstance(MessageType.ADDED_USER_NAME_TO_YOU_FRIEND_LIST);
		message.addUserName(added.getName());
		player.sendPacket(message, true);
		message = SystemMessage.getInstance(MessageType.YOU_VE_BEEN_ADDED_TO_USER_NAME_FRIENDS_LIST);
		message.addUserName(player.getName());
		added.sendPacket(message, true);
	}
	
	/**
	 * Method cancelTargetHp.
	 * @param actor Character
	 * @param enemy Character
	 */
	public static void cancelTargetHp(Character actor, Character enemy)
	{
		actor.sendPacket(CancelTargetHp.getInstance(enemy), true);
	}
	
	/**
	 * Method removeToFriend.
	 * @param player Player
	 * @param removedName String
	 * @param removed Player
	 */
	public static void removeToFriend(Player player, String removedName, Player removed)
	{
		SystemMessage message = SystemMessage.getInstance(MessageType.YOU_REMOVED_USER_NAME_FROM_YOU_FRIENDS_LIST);
		message.addUserName(removedName);
		player.sendPacket(message, true);
		
		if (removed != null)
		{
			message = SystemMessage.getInstance(MessageType.USER_NAME_REMOVED_UYOU_FROM_THEIR_FRIENDS_LIST);
			message.addUserName(player.getName());
			removed.sendPacket(message, true);
		}
	}
	
	/**
	 * Method showAddGold.
	 * @param actor Character
	 * @param count int
	 */
	public static void showAddGold(Character actor, int count)
	{
		actor.sendPacket(SystemMessage.getInstance(MessageType.ADD_MONEY).addMoney(actor.getName(), count), true);
	}
	
	/**
	 * Method showBattleStance.
	 * @param npc Npc
	 * @param enemy TObject
	 */
	public static void showBattleStance(Npc npc, TObject enemy)
	{
		npc.broadcastPacketToOthers(NpcNotice.getInstance(npc, enemy == null ? 0 : enemy.getObjectId(), enemy == null ? 0 : enemy.getSubId()));
	}
	
	/**
	 * Method showBattleStance.
	 * @param player Player
	 * @param npc Npc
	 * @param enemy TObject
	 */
	public static void showBattleStance(Player player, Npc npc, TObject enemy)
	{
		player.sendPacket(NpcNotice.getInstance(npc, enemy == null ? 0 : enemy.getObjectId(), enemy == null ? 0 : enemy.getSubId()), true);
	}
	
	/**
	 * Method showCharacterOwerturn.
	 * @param character Character
	 */
	public static void showCharacterOwerturn(Character character)
	{
		character.broadcastPacket(CancelOwerturn.getInstance(character));
	}
	
	/**
	 * Method showDamage.
	 * @param attacker Character
	 * @param attacked Character
	 * @param info AttackInfo
	 * @param skill Skill
	 * @param type int
	 */
	public static void showDamage(Character attacker, Character attacked, AttackInfo info, Skill skill, int type)
	{
		attacked.broadcastPacket(Damage.getInstance(attacker, attacked, info, skill, type));
	}
	
	/**
	 * Method showDamage.
	 * @param attacker Character
	 * @param attacked Character
	 * @param damageId int
	 * @param damage int
	 * @param crit boolean
	 * @param owerturned boolean
	 * @param type int
	 */
	public static void showDamage(Character attacker, Character attacked, int damageId, int damage, boolean crit, boolean owerturned, int type)
	{
		attacked.broadcastPacket(Damage.getInstance(attacker, attacked, damageId, damage, crit, owerturned, type));
	}
	
	/**
	 * Method showEffect.
	 * @param actor Character
	 * @param effect Effect
	 */
	public static void showEffect(Character actor, Effect effect)
	{
		actor.sendPacket(AppledEffect.getInstance(effect.getEffector(), effect.getEffected(), effect), true);
	}
	
	/**
	 * Method showEmotion.
	 * @param actor Character
	 * @param type EmotionType
	 */
	public static void showEmotion(Character actor, EmotionType type)
	{
		actor.broadcastPacket(Emotion.getInstance(actor, type));
	}
	
	/**
	 * Method showEnterFriend.
	 * @param player Player
	 * @param friend Player
	 */
	public static void showEnterFriend(Player player, Player friend)
	{
		final SystemMessage message = SystemMessage.getInstance(MessageType.USER_NAME_HAS_COME_ONLINE);
		message.addUserName(friend.getName());
		player.sendPacket(message, true);
	}
	
	/**
	 * Method showLockTarget.
	 * @param caster Character
	 * @param target Character
	 * @param skill Skill
	 */
	public static void showLockTarget(Character caster, Character target, Skill skill)
	{
		caster.sendPacket(SkillLockTarget.getInstance(target, skill, true), true);
	}
	
	/**
	 * Method showNotifyIcon.
	 * @param sender Character
	 * @param type NotifyType
	 */
	public static void showNotifyIcon(Character sender, NotifyType type)
	{
		sender.broadcastPacket(NotifyCharacter.getInstance(sender, type));
	}
	
	/**
	 * Method showPaidGold.
	 * @param actor Character
	 * @param count int
	 */
	public static void showPaidGold(Character actor, int count)
	{
		actor.sendPacket(SystemMessage.getInstance(MessageType.PAID_AMOUNT_MONEY).addPaidMoney(count), true);
	}
	
	/**
	 * Method showPlayerList.
	 * @param client UserClient
	 * @param account Account
	 */
	public static void showPlayerList(UserClient client, Account account)
	{
		client.sendPacket(PlayerList.getInstance(account.getName()), true);
	}
	
	/**
	 * Method showShieldBlocked.
	 * @param actor Character
	 */
	public static void showShieldBlocked(Character actor)
	{
		actor.broadcastPacket(CharShieldBlock.getInstance(actor));
	}
	
	/**
	 * Method showShopDialog.
	 * @param player Player
	 * @param dialog ShopDialog
	 */
	public static void showShopDialog(Player player, ShopDialog dialog)
	{
		player.sendPacket(ShopTradePacket.getInstance(dialog), true);
	}
	
	/**
	 * Method showTargetHp.
	 * @param actor Character
	 * @param enemy Character
	 */
	public static void showTargetHp(Character actor, Character enemy)
	{
		actor.sendPacket(TargetHp.getInstance(enemy, actor.checkTarget(enemy) ? TargetHp.RED : TargetHp.BLUE), true);
	}
	
	/**
	 * Method showUseItem.
	 * @param actor Character
	 * @param id int
	 * @param count int
	 */
	public static void showUseItem(Character actor, int id, int count)
	{
		actor.sendPacket(SystemMessage.getInstance(MessageType.ITEM_USE).addItem(id, count), true);
	}
	
	/**
	 * Method updateEquip.
	 * @param player Character
	 */
	public static void updateEquip(Character player)
	{
		player.broadcastPacket(PlayerEquipment.getInstance(player));
	}
	
	/**
	 * Method updateGuildBank.
	 * @param player Player
	 * @param startCell int
	 */
	public static void updateGuildBank(Player player, int startCell)
	{
		player.sendPacket(GuildBank.getInstance(player, startCell), true);
	}
	
	/**
	 * Method updateInventory.
	 * @param player Player
	 */
	public static void updateInventory(Player player)
	{
		player.sendPacket(InventoryInfo.getInstance(player), true);
	}
	
	/**
	 * Method updatePlayerBank.
	 * @param player Player
	 * @param startCell int
	 */
	public static void updatePlayerBank(Player player, int startCell)
	{
		player.sendPacket(PlayerBank.getInstance(player, startCell), true);
	}
	
	/**
	 * Method updateStamina.
	 * @param player Player
	 */
	public static void updateStamina(Player player)
	{
		player.sendPacket(UpdateStamina.getInstance(player), true);
	}
	
	/**
	 * Method showDeleteItem.
	 * @param character Character
	 * @param item ItemInstance
	 */
	public static void showDeleteItem(Character character, ItemInstance item)
	{
		character.sendPacket(SystemMessage.getInstance(MessageType.ITEM_NAME_DESTROYED).addItemName(item.getItemId()), true);
	}
	
	private PacketManager()
	{
		throw new IllegalArgumentException();
	}
}