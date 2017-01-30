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
package tera.gameserver.network;

import java.util.HashSet;

import tera.gameserver.network.clientpackets.AssentTrade;
import tera.gameserver.network.clientpackets.CanBeUsedName;
import tera.gameserver.network.clientpackets.CancelTrade;
import tera.gameserver.network.clientpackets.ClientKey;
import tera.gameserver.network.clientpackets.ClientPacket;
import tera.gameserver.network.clientpackets.EnchantFinish;
import tera.gameserver.network.clientpackets.EnteredWorld;
import tera.gameserver.network.clientpackets.NameChange;
import tera.gameserver.network.clientpackets.PlayerClimb;
import tera.gameserver.network.clientpackets.PlayerMove;
import tera.gameserver.network.clientpackets.PlayerMoveOnSkill;
import tera.gameserver.network.clientpackets.PlayerSay;
import tera.gameserver.network.clientpackets.PrivateSay;
import tera.gameserver.network.clientpackets.QuestMovieEnded;
import tera.gameserver.network.clientpackets.RequestActionAgree;
import tera.gameserver.network.clientpackets.RequestActionCancel;
import tera.gameserver.network.clientpackets.RequestActionInvite;
import tera.gameserver.network.clientpackets.RequestAddEnchantItem;
import tera.gameserver.network.clientpackets.RequestAuthLogin;
import tera.gameserver.network.clientpackets.RequestBankAdd;
import tera.gameserver.network.clientpackets.RequestBankChangeTab;
import tera.gameserver.network.clientpackets.RequestBankMovingItem;
import tera.gameserver.network.clientpackets.RequestBankSub;
import tera.gameserver.network.clientpackets.RequestCancelQuest;
import tera.gameserver.network.clientpackets.RequestClientClose;
import tera.gameserver.network.clientpackets.RequestCollectResourse;
import tera.gameserver.network.clientpackets.RequestConfirmServer;
import tera.gameserver.network.clientpackets.RequestCreatePlayer;
import tera.gameserver.network.clientpackets.RequestDeleteItem;
import tera.gameserver.network.clientpackets.RequestDeletePlayer;
import tera.gameserver.network.clientpackets.RequestDialogCancel;
import tera.gameserver.network.clientpackets.RequestDressingItem;
import tera.gameserver.network.clientpackets.RequestDuelCancel;
import tera.gameserver.network.clientpackets.RequestFriendAdd;
import tera.gameserver.network.clientpackets.RequestFriendList;
import tera.gameserver.network.clientpackets.RequestFriendRemove;
import tera.gameserver.network.clientpackets.RequestGuildChangeRank;
import tera.gameserver.network.clientpackets.RequestGuildCreateRank;
import tera.gameserver.network.clientpackets.RequestGuildExclude;
import tera.gameserver.network.clientpackets.RequestGuildIcon;
import tera.gameserver.network.clientpackets.RequestGuildInfo;
import tera.gameserver.network.clientpackets.RequestGuildLeave;
import tera.gameserver.network.clientpackets.RequestGuildLoadIcon;
import tera.gameserver.network.clientpackets.RequestGuildMakeLeader;
import tera.gameserver.network.clientpackets.RequestGuildRemoveRank;
import tera.gameserver.network.clientpackets.RequestGuildUpdateMessage;
import tera.gameserver.network.clientpackets.RequestGuildUpdateNote;
import tera.gameserver.network.clientpackets.RequestGuildUpdateRank;
import tera.gameserver.network.clientpackets.RequestGuildUpdateTitle;
import tera.gameserver.network.clientpackets.RequestInventoryInfo;
import tera.gameserver.network.clientpackets.RequestInventoryInfoItem;
import tera.gameserver.network.clientpackets.RequestInventoryMovingItem;
import tera.gameserver.network.clientpackets.RequestItemTemplateInfo;
import tera.gameserver.network.clientpackets.RequestLocalTeleport;
import tera.gameserver.network.clientpackets.RequestLockOnTarget;
import tera.gameserver.network.clientpackets.RequestNpcAddBuyShop;
import tera.gameserver.network.clientpackets.RequestNpcAddSellShop;
import tera.gameserver.network.clientpackets.RequestNpcConfirmShop;
import tera.gameserver.network.clientpackets.RequestNpcConfirmSkillShop;
import tera.gameserver.network.clientpackets.RequestNpcInteraction;
import tera.gameserver.network.clientpackets.RequestNpcLink;
import tera.gameserver.network.clientpackets.RequestNpcStartPegasFly;
import tera.gameserver.network.clientpackets.RequestNpcSubBuyShop;
import tera.gameserver.network.clientpackets.RequestNpcSubSellShop;
import tera.gameserver.network.clientpackets.RequestPartyChange;
import tera.gameserver.network.clientpackets.RequestPartyDisband;
import tera.gameserver.network.clientpackets.RequestPartyInvite;
import tera.gameserver.network.clientpackets.RequestPartyKick;
import tera.gameserver.network.clientpackets.RequestPartyLeave;
import tera.gameserver.network.clientpackets.RequestPartyMakeLeader;
import tera.gameserver.network.clientpackets.RequestPickUpItem;
import tera.gameserver.network.clientpackets.RequestPlayerList;
import tera.gameserver.network.clientpackets.RequestRessurect;
import tera.gameserver.network.clientpackets.RequestRestart;
import tera.gameserver.network.clientpackets.RequestServerCheck;
import tera.gameserver.network.clientpackets.RequestSkillAction;
import tera.gameserver.network.clientpackets.RequestSortInventory;
import tera.gameserver.network.clientpackets.RequestStartClimb;
import tera.gameserver.network.clientpackets.RequestStartEmotion;
import tera.gameserver.network.clientpackets.RequestState;
import tera.gameserver.network.clientpackets.RequestTradeAddItem;
import tera.gameserver.network.clientpackets.RequestTradeLock;
import tera.gameserver.network.clientpackets.RequestUpdateQuestPanel;
import tera.gameserver.network.clientpackets.RequestUseDefenseSkill;
import tera.gameserver.network.clientpackets.RequestUseItem;
import tera.gameserver.network.clientpackets.RequestUseQueueSkill;
import tera.gameserver.network.clientpackets.RequestUseRangeSkill;
import tera.gameserver.network.clientpackets.RequestUseRushSkill;
import tera.gameserver.network.clientpackets.RequestUseScroll;
import tera.gameserver.network.clientpackets.RequestUseShortSkill;
import tera.gameserver.network.clientpackets.RequestWorldZone;
import tera.gameserver.network.clientpackets.SelectSkillLearn;
import tera.gameserver.network.clientpackets.SelectedPlayer;
import tera.gameserver.network.clientpackets.UpdateClientSetting;
import tera.gameserver.network.clientpackets.UpdateHotKey;
import tera.gameserver.network.clientpackets.UpdateTitle;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public enum ClientPacketType
{
	PLAYER_MOVE(0x5A4B, new PlayerMove()),
	PLAYER_CLIMB(0xEDC9, new PlayerClimb()),
	REQUESTPLAYER_CLIMB(0xBB06, new RequestStartClimb()),
	PLAYER_SKILL_MOVE(0x91A5, new PlayerMoveOnSkill()),
	PLAYER_SAY(0xAE9B, new PlayerSay()),
	PLAYER_PRIVATE_SAY(0xCADA, new PrivateSay()),
	UPDATE_TITLE(0x9CD4, new UpdateTitle()),
	REQUEST_WORLD_ZONE(0x5E43, new RequestWorldZone()),
	REQUEST_STATE(0xAB5E, new RequestState()),
	REQUEST_INVENTORY_INFO(0xEEF6, new RequestInventoryInfo()),
	REQUEST_DRESSING_ITEM(0xC7B5, new RequestDressingItem()),
	REQUEST_TAKING_ITEM(0xB983, new RequestDressingItem()),
	REQUEST_INVENTORY_MOVING_ITEM(0x9BDA, new RequestInventoryMovingItem()),
	REQUEST_BANK_MOVING_ITEM(0xEFC9, new RequestBankMovingItem()),
	REQUEST_BANK_CHANGE_TAB(0xDBB0, new RequestBankChangeTab()),
	REQUEST_SORT_INVENTORY(0xA92D, new RequestSortInventory()), //
	REQUEST_DELETE_ITEM(0xD9EC, new RequestDeleteItem()), // 14 00 35 F2 96 5F 00 00 00 00 00 00 04 00 00 00
	REQUEST_INVENTORY_ITEM_INFO(0xBC87, new RequestInventoryInfoItem()), // 28 00 F4 C4 1E 00 13 00 00 00 C4 71 00 00 00 00
	REQUEST_PICK_UP_ITEM(0x4E2D, new RequestPickUpItem()),
	REQUEST_USE_ITEM(0xE307, new RequestUseItem()), // 3A 00 F6 B0 AB 76 00 00 00 00 00 00 47 1F 00 00
	REQUEST_USE_SCROLL(0x8386, new RequestUseScroll()),
	REQUEST_ITEM_TEMPLATE_INFO(0xA9DA, new RequestItemTemplateInfo()),
	REQUEST_START_EMOTION(0xA2F1, new RequestStartEmotion()),
	REQUEST_GUILD_INFO(0x81A4, new RequestGuildInfo()),
	REQUEST_GUILD_LEAVE(0x9EBD, new RequestGuildLeave()),
	REQUEST_GUILD_EXLUDE(0x8EB7, new RequestGuildExclude()),
	REQUEST_GUILD_UPDATE_RANK(0xFF13, new RequestGuildUpdateRank()),
	REQUEST_GUILD_REMOVE_RANK(0x81BD, new RequestGuildRemoveRank()),
	REQUEST_GUILD_CHANGE_RANK(0xD3D0, new RequestGuildChangeRank()),
	REQUEST_GUILD_CREATE_RANK(0x7A56, new RequestGuildCreateRank()),
	REQUEST_GUIL_LOAD_ICON(0xF883, new RequestGuildLoadIcon()),
	REQUEST_GUILD_ICON_INFO(0xAC93, new RequestGuildIcon()),
	REQUEST_GUILD_MAKE_LEADER(0xAD5E, new RequestGuildMakeLeader()),
	REQUEST_GUILD_UPDATE_TITLE(0xBACE, new RequestGuildUpdateTitle()),
	REQUEST_GUILD_UPDATE_MESSAGE(0xFDC7, new RequestGuildUpdateMessage()),
	REQUEST_GUILD_UPDATE_NOTE(0x4FE9, new RequestGuildUpdateNote()),
	REQUEST_USE_SHORT_SKILL(0xCF34, new RequestUseShortSkill()),
	REQUEST_USE_QUEUE_SKILL(0xCED8, new RequestUseQueueSkill()),
	REQUEST_USE_RANGE_SKILL(0x7017, new RequestUseRangeSkill()),
	REQUEST_USE_RUSH_SKILL(0xBF14, new RequestUseRushSkill()),
	REQUEST_USE_DEFENSE_SKILL(0x57E6, new RequestUseDefenseSkill()),
	REQUEST_LOCK_ON_TARGET(0xD1F2, new RequestLockOnTarget()),
	REQUEST_SKILL_ACTION(0xA6B8, new RequestSkillAction()),
	UPDATE_HOT_KEY(0xB6BA, new UpdateHotKey()),
	UPDATE_CLIENT_SETTING(0xCEF8, new UpdateClientSetting()),
	REQUEST_RESSURECT(0x50D1, new RequestRessurect()), // 0C 00 20 E0 00 00 00 00 FF FF FF FF
	REQUEST_CONFIRM_SERVER(0x9FCA, new RequestConfirmServer()),
	REQUEST_CHECK_SERVER(0xD6C0, new RequestServerCheck()),
	REQUEST_RESTART(0xF8B1, new RequestRestart()),
	REQUEST_CLIENT_CLOSE(0xC873, new RequestClientClose()),
	REQUEST_LOCAL_TELEPORT(0xCADC, new RequestLocalTeleport()),
	REQUEST_DUEL_CANCEL(0xF0AE, new RequestDuelCancel()),
	REQUEST_NPC_INTERACTION(0xB6AE, new RequestNpcInteraction()), // 10 00 66 C3 1D F0 D4 1E 00 00 00 00 00 00 00 00
	REQUEST_NPC_LINK(0xBF7B, new RequestNpcLink()), // 14 00 20 D9 40 7B D5 1E 01 00 00 00 FF FF FF FF
	REQUEST_NPC_ADD_BUY_SHOP(0xF406, new RequestNpcAddBuyShop()), // 1C 00 CC EB F5 5F 00 00 00 00 00 00 65 B1 B6 48
	REQUEST_NPC_SUB_BUY_SHOP(0xA087, new RequestNpcSubBuyShop()), // 20 00 12 CC F5 5F 00 00 00 00 00 00 65 B1 B6 48
	REQUEST_NPC_ADD_SELL_SHOP(0x8EC1, new RequestNpcAddSellShop()), // 18 00 5E F5 F5 5F 00 00 00 00 00 00 65 B1 B6 48
	REQUEST_NPC_SUB_SELL_SHOP(0x51D3, new RequestNpcSubSellShop()), // 18 00 74 D1 F5 5F 00 00 00 00 00 00 65 B1 B6 48
	REQUEST_NPC_CONFIRM_SHOP(0x8082, new RequestNpcConfirmShop()), // 10 00 77 65 F5 5F 00 00 00 00 00 00 65 B1 B6 48
	REQUEST_NPC_CONFIRM_SKILL_SHOP(0xC456, new RequestNpcConfirmSkillShop()),
	REQUEST_NPC_BANK_ADD(0xF25D, new RequestBankAdd()),
	REQUEST_NPC_BANK_SUB(0xEF1D, new RequestBankSub()),
	REQUEST_QUEST_PANEL(0xB1B2, new RequestUpdateQuestPanel()),
	REQUEST_QUEST_CANCEL(0x59AE, new RequestCancelQuest()),
	CLIENT_SELECT_SKILL_LEARN(0xE7E0, new SelectSkillLearn()),
	REQUEST_NPC_START_PEGAS_FLY(0xC762, new RequestNpcStartPegasFly()), // 08 00 62 C7 08 00 00 00
	REQUEST_DIALOG_CANCEL(0xBFB9, new RequestDialogCancel()), // 0C 00 31 C5 09 00 00 00 65 B1 B6 48
	REQUEST_TRADE_LOCK(0x637C, new RequestTradeLock()),
	REQUEST_TRADE_ADD_ITEM(0x683A, new RequestTradeAddItem()),
	ASSENT_TRADE(0x65A6, new AssentTrade()), // A6 65 03 00
	CANCEL_TRADE(0x72D6, new CancelTrade()), // D6 72 03 00 00 00 00 00 00 00
	QUEST_MOVIE_ENDED(0x920B, new QuestMovieEnded()),
	REQUEST_ACTION_INVITE(0xADE2, new RequestActionInvite()),
	REQUEST_ACTION_AGREE(0xA05D, new RequestActionAgree()),
	REQUEST_ACTION_CANCEL(0xE58F, new RequestActionCancel()),
	REQUEST_PARTY_INVITE(0x0111, new RequestPartyInvite()),
	REQUEST_PARTY_LEAVE(0x8002, new RequestPartyLeave()),
	REQUEST_PARTY_CHANGE(0x784F, new RequestPartyChange()),
	REQUEST_PARTY_MAKE_LEADER(0xB14B, new RequestPartyMakeLeader()),
	REQUEST_PARTY_KICK(0xFD15, new RequestPartyKick()),
	REQUEST_PARTY_DISBAND(0xC041, new RequestPartyDisband()),
	REQUEST_COLLECT_RESOURSE(0x94FD, new RequestCollectResourse()),
	REQUEST_FRIEND_LIST(0xAB9A, new RequestFriendList()),
	REQUEST_FRIEND_ADD(0xDE4A, new RequestFriendAdd()),
	REQUEST_FRIEND_REMOVE(0x5FA8, new RequestFriendRemove()),
	REQUEST_AUTH_LOGIN(0x4DBC, new RequestAuthLogin()),
	REQUEST_CREATE_PLAYER(0x8CE9, new RequestCreatePlayer()),
	REQUEST_DELETE_PLAYER(0xD5F7, new RequestDeletePlayer()),
	REQUEST_PLAYER_LIST(0xBCB9, new RequestPlayerList()),
	CAN_BE_USED_NAME(0x56E3, new CanBeUsedName()),
	NAME_CHANGED(0xBB7A, new NameChange()),
	CLIENT_KEY(0xFFFF, new ClientKey()),
	REQUEST_ADD_ENCHANT_ITEM(0x5E4E, new RequestAddEnchantItem()),
	ENCHANT_FINISH(0xDDB4, new EnchantFinish()),
	PLAYER_SELECTED_PACKET(0xE3A4, new SelectedPlayer()),
	PLAYER_ENTERED_PACKET(0x8E3C, new EnteredWorld());
	
	private static final Logger log = Loggers.getLogger(ClientPacketType.class);
	private static ClientPacket[] packets;
	
	/**
	 * Method createPacket.
	 * @param opcode int
	 * @return ClientPacket
	 */
	public static ClientPacket createPacket(int opcode)
	{
		final ClientPacket packet = packets[opcode];
		return packet == null ? null : packet.newInstance();
	}
	
	public static void init()
	{
		final HashSet<Integer> set = new HashSet<>();
		final ClientPacketType[] elements = values();
		
		for (ClientPacketType packet : elements)
		{
			final int index = packet.getOpcode();
			
			if (set.contains(index))
			{
				log.warning("found duplicate opcode " + index + " or " + Integer.toHexString(packet.getOpcode()) + " for " + packet + "!");
			}
			
			set.add(index);
		}
		
		set.clear();
		packets = new ClientPacket[(Short.MAX_VALUE * 2) + 2];
		
		for (ClientPacketType element : elements)
		{
			packets[element.getOpcode()] = element.getPacket();
		}
		
		log.info("client packets prepared.");
	}
	
	private final FoldablePool<ClientPacket> pool;
	
	private final ClientPacket packet;
	
	private int opcode;
	
	/**
	 * Constructor for ClientPacketType.
	 * @param opcode int
	 * @param packet ClientPacket
	 */
	private ClientPacketType(int opcode, ClientPacket packet)
	{
		this.opcode = opcode;
		this.packet = packet;
		this.packet.setPacketType(this);
		pool = Pools.newConcurrentFoldablePool(ClientPacket.class);
	}
	
	/**
	 * Method getOpcode.
	 * @return int
	 */
	public final int getOpcode()
	{
		return opcode;
	}
	
	/**
	 * Method getPacket.
	 * @return ClientPacket
	 */
	public final ClientPacket getPacket()
	{
		return packet;
	}
	
	/**
	 * Method getPool.
	 * @return FoldablePool<ClientPacket>
	 */
	public final FoldablePool<ClientPacket> getPool()
	{
		return pool;
	}
	
	/**
	 * Method setOpcode.
	 * @param opcode int
	 */
	public final void setOpcode(int opcode)
	{
		this.opcode = opcode;
	}
}