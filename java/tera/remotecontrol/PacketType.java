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
package tera.remotecontrol;

/**
 * @author Ronn
 * @created 26.03.2012
 */
public enum PacketType
{
	AUTH,
	REQUEST_AUTH,
	STATUS_SERVER,
	REQUEST_STATUS_SERVER,
	LOAD_ANNOUNCES,
	REQUEST_LOAD_ANNOUBCES,
	APPLY_ANNOUNCES,
	SEND_ANNOUNCE,
	LOAD_PLAYER_CHAT,
	REQUEST_PLAYER_CHAT,
	SEND_PLAYER_MESSAGE,
	LOAD_PLAYERS,
	REQUEST_LOAD_PLAYERS,
	UPDATE_PLAYER_INFO,
	REQUEST_UPDATE_PLAYER_INFO,
	GET_STATIC_INFO,
	GET_DYNAMIC_INFO,
	GET_GAME_INFO,
	REQUEST_STATIC_INFO,
	REQUEST_DYNAMIC_INFO,
	REQUEST_GAME_INFO,
	SERVER_RESTART,
	REQUEST_SERVER_CONSOLE,
	REQUEST_PLAYERS_SAVE,
	REQUEST_START_GC,
	REQUEST_START_RESTART,
	REQUEST_START_SHUTDOWN,
	REQUEST_CANCEL_SHUTDOWN,
	REQUEST_PLAYERS_CHAT,
	REQUEST_PLAYER_LIST,
	REQUEST_PLAYER_MAIN_INFO,
	REQUEST_PLAYER_STAT_INFO,
	REQUEST_PLAYER_INVENTORY,
	REQUEST_PLAYER_EQUIPMENT,
	REQUEST_ITEM_INFO,
	REQUEST_APPLY_ITEM_CHANGED,
	REQUEST_REMOVE_ITEM,
	REQUEST_ADD_ITEM,
	REQUEST_GET_ACCOUNT,
	REQUEST_SET_ACCOUNT,
	REQUEST_UPDATE_ACCOUNT,
	RESPONSE;
}