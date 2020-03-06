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
package tera.gameserver.scripts.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import rlib.logging.Loggers;

/**
 * @author Ronn
 * @created 04.03.2012
 */
public enum CommandType
{
	RESOURSE_COMMANDS(ResourseCommand.class, 100, "around_resourse", "spawn_resourse", "set_export_file", "add_resourse", "export_resourse"),
	
	CONFIG_COMMANDS(ConfigCommand.class, 100, "config_reload", "config_set"),
	
	WORLD_COMMANDS(WorldCommand.class, 100, "loc", "region", "territory", "goto", "recall"),
	
	SKILL_COMMANDS(SkillCommand.class, 100, "start_skill", "add_skills", "learn_next_skills", "reload_skills", "clear_skills", "get_base_skills", "effect", "charm"),
	
	HEAL_COMMANDS(HealCommand.class, 100, "set_hp", "set_mp", "heal"),
	
	DEVELOPER_COMMANDS(
		DeveloperCommand.class,
		100,
		"event_reg_all_players",
		"zone",
		"change_class",
		"kick",
		"check_geo",
		"send_event",
		"send_system",
		"send_state",
		"add_attack",
		"start_event",
		"sub_attack",
		"start_gc",
		"reload_dialogs",
		"send_packet",
		"set_access_level",
		"get_access_level",
		"set_heart",
		"set_level",
		"send_bytes",
		"send_file",
		"get_my_id",
		"invul",
		"set_ower_dist",
		"my_funcs",
		"save_point",
		"a",
		"save_all",
		"gm_speed"),
	
	ITEM_COMMANDS(ItemCommand.class, 100, "item_info", "create_item", "spawn_item", "reload_items"),
	
	USER_COMMANDS(UserCommand.class, 0, "event_reg", "restore_characters", "player_info", "help", "version", "end_pay", "time", "kill_me", "online", "restore_skills"),
	
	CENSORE_COMMANDS(CensoreCommand.class, 40, "chat_ban", "chat_unban"),
	
	SUMMON_COMMANDS(SummonCommand.class, 100, "reload_summons", "summon_cast", "around_summon_cast"),
	
	QUEST_COMMANDS(QuestCommand.class, 100, "quest_cond", "quest_state", "quest_cancel", "quest_remove", "quest_accept", "quest_movie", "quest_reload", "quest_start", "quest_info"),
	
	VAR_COMMANDS(VariablesCommand.class, 100, "set_player_var", "get_player_var"),
	
	NPC_COMMANDS(NpcCommands.class, 100, "go_to_npc", "send_dialog", "test_spawn", "stop_spawns", "start_spawns", "npc_cast", "around_npc_spawn", "around_npc", "reload_npcs", "reload_spawns", "spawn", "around_npc_cast", "around_npc_long_cast");
	
	private String[] commands;
	
	private int access;
	
	private Constructor<? extends Command> constructor;
	
	/**
	 * Constructor for CommandType.
	 * @param type Class<? extends Command>
	 * @param access int
	 * @param commands String[]
	 */
	private CommandType(Class<? extends Command> type, int access, String... commands)
	{
		this.commands = commands;
		this.access = access;
		
		try
		{
			constructor = type.getConstructor(int.class, String[].class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method getAccess.
	 * @return int
	 */
	public int getAccess()
	{
		return access;
	}
	
	/**
	 * Method getConstructor.
	 * @return Constructor<? extends Command>
	 */
	public Constructor<? extends Command> getConstructor()
	{
		return constructor;
	}
	
	/**
	 * Method getCount.
	 * @return int
	 */
	public int getCount()
	{
		return commands.length;
	}
	
	/**
	 * Method newInstance.
	 * @return Command
	 */
	public Command newInstance()
	{
		try
		{
			return constructor.newInstance(access, commands);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}
