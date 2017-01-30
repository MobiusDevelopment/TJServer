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
package tera;

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jolbox.bonecp.BoneCPConfig;

import tera.gameserver.document.DocumentConfig;

import rlib.geoengine.GeoConfig;
import rlib.logging.Loggers;
import rlib.util.Files;
import rlib.util.Strings;
import rlib.util.Util;
import rlib.util.VarTable;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 11.03.2012
 */
public final class Config
{
	public static int ACCOUNT_MIN_ACCESS_LEVEL;
	
	public static float ACCOUNT_PREMIUM_EXP_RATE;
	public static float ACCOUNT_PREMIUM_MONEY_RATE;
	public static float ACCOUNT_PREMIUM_DROP_RATE;
	public static float ACCOUNT_PREMIUM_QUEST_RATE;
	
	public static boolean ACCOUNT_AUTO_CREATE;
	public static boolean ACCOUNT_ONLY_PAID;
	public static boolean ACCOUNT_PREMIUM_EXP;
	public static boolean ACCOUNT_PREMIUM_MONEY;
	public static boolean ACCOUNT_PREMIUM_DROP;
	public static boolean ACCOUNT_PREMIUM_QUEST;
	
	public static String SERVER_NAME_TEMPLATE;
	public static String SERVER_VERSION = "Mobius v1";
	public static String SERVER_ONLINE_FILE;
	
	public static int SERVER_PLAYER_SUB_ID;
	public static int SERVER_NPC_SUB_ID;
	public static int SERVER_ITEM_SUB_ID;
	public static int SERVER_SHOT_SUB_ID;
	public static int SERVER_OBJECT_SUB_ID;
	public static int SERVER_TRAP_SUB_ID;
	public static int SERVER_RESOURSE_SUB_ID;
	public static int SERVER_PORT;
	
	public static float SERVER_RATE_EXP;
	public static float SERVER_PARTY_RATE_EXP;
	public static float SERVER_RATE_MONEY;
	public static float SERVER_RATE_DROP_ITEM;
	public static float SERVER_RATE_QUEST_REWARD;
	public static float SERVER_ONLINE_FAKE;
	
	public static boolean SERVER_USE_SNIFFER_OPCODE;
	public static boolean SERVER_DROP_REAL_RANDOM;
	public static boolean SERVER_CRIT_REAL_RANDOM;
	public static boolean SERVER_EFFECT_REAL_RANDOM;
	public static boolean SERVER_FUNC_REAL_RANDOM;
	public static boolean SERVER_DAMAGE_REAL_RANDOM;
	public static boolean SERVER_OWERTURN_REAL_RANDOM;
	
	public static int GEO_ENGINE_OFFSET_X;
	public static int GEO_ENGINE_OFFSET_Y;
	public static int GEO_ENGINE_QUARD_SIZE;
	public static int GEO_ENGINE_QUARD_HEIGHT;
	
	public static int WORLD_LIFE_TIME_DROP_ITEM;
	public static int WORLD_BLOCK_TIME_DROP_ITEM;
	public static int WORLD_MIN_ACCESS_LEVEL;
	public static int WORLD_PLAYER_THRESHOLD_ATTACKS;
	public static int WORLD_PLAYER_THRESHOLD_BLOOKS;
	public static int WORLD_PLAYER_TIME_BATTLE_STANCE;
	public static int WORLD_MAX_DIFF_LEVEL_ON_DROP;
	public static int WORLD_WIDTH_REGION;
	public static int WORLD_HEIGHT_REGION;
	public static int WORLD_MAXIMUM_ONLINE;
	public static int WORLD_BANK_MAX_SIZE;
	public static int WORLD_GUILD_BANK_MAX_SIZE;
	public static int WORLD_PLAYER_MAX_LEVEL;
	public static int WORLD_CHANCE_DELETE_CRYSTAL;
	public static int WORLD_MAX_COLLECT_LEVEL;
	public static int WORLD_CONTINENT_COUNT;
	public static int WORLD_TRADE_MAX_RANGE;
	public static int WORLD_DUEL_MAX_RANGE;
	public static int WORLD_GUILD_INVITE_MAX_RANGE;
	public static int WORLD_MAX_SKILL_DESYNC;
	public static int WORLD_ENCHANT_ITEM_CHANCE;
	public static int WORLD_MIN_TARGET_LEVEL_FOR_PK;
	public static float WORLD_SHORT_SKILL_REUSE_MOD;
	public static float WORLD_RANGE_SKILL_REUSE_MOD;
	public static float WORLD_OTHER_SKILL_REUSE_MOD;
	public static int[] WORLD_DONATE_ITEMS;
	public static float WORLD_SHOP_PRICE_MOD;
	public static boolean WORLD_AUTO_LEARN_SKILLS;
	public static boolean WORLD_PK_AVAILABLE;
	public static boolean WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS;
	
	public static final BoneCPConfig DATA_BASE_CONFIG = new BoneCPConfig();
	public static String DATA_BASE_DRIVER;
	public static String DATA_BASE_URL;
	public static String DATA_BASE_LOGIN;
	public static String DATA_BASE_PASSWORD;
	public static int DATA_BASE_MAX_CONNECTIONS;
	public static int DATA_BASE_MAX_STATEMENTS;
	public static boolean DATA_BASE_CLEANING_START;
	public static int THREAD_POOL_SIZE_GENERAL;
	public static int THREAD_POOL_SIZE_MOVE;
	public static int THREAD_POOL_SIZE_AI;
	public static int THREAD_POOL_SIZE_SKILL_USE;
	public static int THREAD_POOL_SIZE_SKILL_CAST;
	public static int THREAD_POOL_SIZE_SKILL_MOVE;
	public static int THREAD_POOL_PACKET_RUNNER;
	
	public static int DEVELOPER_FORCE_ATTACK_SPEED;
	public static boolean DEVELOPER_DEBUG_CLIENT_PACKETS;
	public static boolean DEVELOPER_DEBUG_SERVER_PACKETS;
	public static boolean DEVELOPER_MAIN_DEBUG;
	public static boolean DEVELOPER_DEBUG_TARGET_TYPE;
	public static boolean DEVELOPER_DEBUG_CASTING_SKILL;
	public static boolean DEVELOPER_DEBUG_MOVING_PLAYER;
	public static boolean DEVELOPER_DEBUG_MOVING_NPC;
	public static boolean DEVELOPER_GEO_LOGING;
	
	public static int AI_MAX_ACTIVE_RANGE;
	public static int AI_MAX_REACTION_RANGE;
	public static int AI_MIN_RANDOM_WALK_RANGE;
	public static int AI_MAX_RANDOM_WALK_RANGE;
	public static int AI_MIN_RANDOM_WALK_DELAY;
	public static int AI_MAX_RANDOM_WALK_DELAY;
	public static int AI_TASK_DELAY;
	public static int AI_ATTACK_RATE;
	public static int AI_BUFF_RATE;
	public static int AI_DEBUFF_RATE;
	public static int AI_DEFENSE_RATE;
	public static int AI_JUMP_RATE;
	public static int AI_ULTIMATE_RATE;
	public static int AI_SIDE_RATE;
	public static int AI_SPRINT_RATE;
	
	public static int NETWORK_GROUP_SIZE;
	public static int NETWORK_THREAD_PRIORITY;
	public static int NETWORK_READ_BUFFER_SIZE;
	public static int NETWORK_WRITE_BUFFER_SIZE;
	public static int NETWORK_MAXIMUM_PACKET_CUT;
	public static boolean NETWORK_VISIBLE_READ_EXCEPTION;
	public static boolean NETWORK_VISIBLE_WRITE_EXCEPTION;
	
	public static int EVENT_MIN_TIMEOUT;
	public static int EVENT_MAX_TIMEOUT;
	public static int EVENT_TVT_REGISTER_TIME;
	public static int EVENT_TVT_BATTLE_TIME;
	public static int EVENT_TVT_MIN_PLAYERS;
	public static int EVENT_TVT_MAX_PLAYERS;
	public static int EVENT_TVT_MIN_LEVEL;
	public static int EVENT_TVT_MAX_LEVEL;
	public static int EVENT_LH_REGISTER_TIME;
	public static int EVENT_LH_BATTLE_TIME;
	public static int EVENT_LH_MIN_PLAYERS;
	public static int EVENT_LH_MAX_PLAYERS;
	public static int EVENT_LH_MIN_LEVEL;
	public static int EVENT_LH_MAX_LEVEL;
	public static int EVENT_TMT_REGISTER_TIME;
	public static int EVENT_TMT_BATTLE_TIME;
	public static int EVENT_TMT_MIN_TEAMS;
	public static int EVENT_TMT_MAX_TEAMS;
	public static int EVENT_TMT_MIN_TEAM_SIZE;
	public static int EVENT_TMT_MAX_TEAM_SIZE;
	public static int EVENT_TMT_MIN_LEVEL;
	public static int EVENT_TMT_MAX_LEVEL;
	public static int EVENT_EB_REGISTER_TIME;
	public static int EVENT_EB_BATTLE_TIME;
	public static int EVENT_EB_MIN_PLAYERS;
	public static int EVENT_EB_MAX_PLAYERS;
	public static int EVENT_EB_MIN_LEVEL;
	public static int EVENT_EB_MAX_LEVEL;
	public static int EVENT_TDM_REGISTER_TIME;
	public static int EVENT_TDM_BATTLE_TIME;
	public static int EVENT_TDM_MIN_PLAYERS;
	public static int EVENT_TDM_MAX_PLAYERS;
	public static int EVENT_TDM_MIN_LEVEL;
	public static int EVENT_TDM_MAX_LEVEL;
	public static int EVENT_HERO_POINT_TO_GOLD;
	public static String EVENT_TEAM_VS_TEAM_NAME;
	public static int EVENT_TEAM_VS_TEAM_MIN_COUNT_PLAYERS;
	public static int EVENT_TEAM_VS_TEAM_MAX_COUNT_PLAYERS;
	
	public static String DIST_CONTROL_LOGIN;
	public static String DIST_CONTROL_PASSWORD;
	public static int DIST_CONTROL_PORT;
	public static int DIST_CONTROL_CLIENT_INTERVAL;
	public static boolean DIST_CONTROL_ENABLED;
	
	public static String SERVER_DIR;
	
	public static GeoConfig GEO_CONFIG = new GeoConfig()
	{
		@Override
		public int getOffsetX()
		{
			return GEO_ENGINE_OFFSET_X;
		}
		
		@Override
		public int getOffsetY()
		{
			return GEO_ENGINE_OFFSET_Y;
		}
		
		@Override
		public int getQuardHeight()
		{
			return GEO_ENGINE_QUARD_HEIGHT;
		}
		
		@Override
		public int getQuardSize()
		{
			return GEO_ENGINE_QUARD_SIZE;
		}
		
		@Override
		public int getSplit()
		{
			return 0;
		}
	};
	
	private static Pattern namePattern;
	
	/**
	 * Method checkName.
	 * @param name String
	 * @return boolean
	 */
	public static final boolean checkName(String name)
	{
		final Matcher metcher = namePattern.matcher(name);
		return metcher.matches();
	}
	
	public static void init()
	{
		SERVER_DIR = Util.getRootPath();
		SERVER_DIR = ".";
		final VarTable vars = VarTable.newInstance();
		final File[] files = Files.getFiles(new File(SERVER_DIR + "/config"));
		parseFiles(files, vars);
		ACCOUNT_MIN_ACCESS_LEVEL = vars.getInteger("ACCOUNT_MIN_ACCESS_LEVEL");
		ACCOUNT_PREMIUM_EXP_RATE = vars.getFloat("ACCOUNT_PREMIUM_EXP_RATE");
		ACCOUNT_PREMIUM_MONEY_RATE = vars.getFloat("ACCOUNT_PREMIUM_MONEY_RATE");
		ACCOUNT_PREMIUM_DROP_RATE = vars.getFloat("ACCOUNT_PREMIUM_DROP_RATE");
		ACCOUNT_PREMIUM_QUEST_RATE = vars.getFloat("ACCOUNT_PREMIUM_QUEST_RATE");
		ACCOUNT_AUTO_CREATE = vars.getBoolean("ACCOUNT_AUTO_CREATE");
		ACCOUNT_ONLY_PAID = vars.getBoolean("ACCOUNT_ONLY_PAID");
		ACCOUNT_PREMIUM_EXP = vars.getBoolean("ACCOUNT_PREMIUM_EXP");
		ACCOUNT_PREMIUM_MONEY = vars.getBoolean("ACCOUNT_PREMIUM_MONEY");
		ACCOUNT_PREMIUM_DROP = vars.getBoolean("ACCOUNT_PREMIUM_DROP");
		ACCOUNT_PREMIUM_QUEST = vars.getBoolean("ACCOUNT_PREMIUM_QUEST");
		SERVER_NAME_TEMPLATE = vars.getString("SERVER_NAME_TEMPLATE");
		SERVER_ONLINE_FILE = vars.getString("SERVER_ONLINE_FILE", Strings.EMPTY);
		SERVER_PLAYER_SUB_ID = vars.getInteger("SERVER_PLAYER_SUB_ID");
		SERVER_NPC_SUB_ID = vars.getInteger("SERVER_NPC_SUB_ID");
		SERVER_ITEM_SUB_ID = vars.getInteger("SERVER_ITEM_SUB_ID");
		SERVER_SHOT_SUB_ID = vars.getInteger("SERVER_SHOT_SUB_ID");
		SERVER_OBJECT_SUB_ID = vars.getInteger("SERVER_OBJECT_SUB_ID");
		SERVER_TRAP_SUB_ID = vars.getInteger("SERVER_TRAP_SUB_ID");
		SERVER_RESOURSE_SUB_ID = vars.getInteger("SERVER_RESOURSE_SUB_ID");
		SERVER_PORT = vars.getInteger("SERVER_PORT");
		GEO_ENGINE_OFFSET_X = vars.getInteger("GEO_ENGINE_OFFSET_X");
		GEO_ENGINE_OFFSET_Y = vars.getInteger("GEO_ENGINE_OFFSET_Y");
		GEO_ENGINE_QUARD_SIZE = vars.getInteger("GEO_ENGINE_QUARD_SIZE");
		GEO_ENGINE_QUARD_HEIGHT = vars.getInteger("GEO_ENGINE_QUARD_HEIGHT");
		SERVER_RATE_EXP = vars.getFloat("SERVER_RATE_EXP");
		SERVER_PARTY_RATE_EXP = vars.getFloat("SERVER_PARTY_RATE_EXP");
		SERVER_RATE_MONEY = vars.getFloat("SERVER_RATE_MONEY");
		SERVER_RATE_DROP_ITEM = vars.getFloat("SERVER_RATE_DROP_ITEM");
		SERVER_RATE_QUEST_REWARD = vars.getFloat("SERVER_RATE_QUEST_REWARD");
		SERVER_ONLINE_FAKE = vars.getFloat("SERVER_ONLINE_FAKE");
		SERVER_USE_SNIFFER_OPCODE = vars.getBoolean("SERVER_USE_SNIFFER_OPCODE");
		SERVER_DROP_REAL_RANDOM = vars.getBoolean("SERVER_DROP_REAL_RANDOM");
		SERVER_CRIT_REAL_RANDOM = vars.getBoolean("SERVER_CRIT_REAL_RANDOM");
		SERVER_EFFECT_REAL_RANDOM = vars.getBoolean("SERVER_EFFECT_REAL_RANDOM");
		SERVER_FUNC_REAL_RANDOM = vars.getBoolean("SERVER_FUNC_REAL_RANDOM");
		SERVER_DAMAGE_REAL_RANDOM = vars.getBoolean("SERVER_DAMAGE_REAL_RANDOM");
		SERVER_OWERTURN_REAL_RANDOM = vars.getBoolean("SERVER_OWERTURN_REAL_RANDOM");
		WORLD_LIFE_TIME_DROP_ITEM = vars.getInteger("WORLD_LIFE_TIME_DROP_ITEM");
		WORLD_BLOCK_TIME_DROP_ITEM = vars.getInteger("WORLD_BLOCK_TIME_DROP_ITEM");
		WORLD_MIN_ACCESS_LEVEL = vars.getInteger("WORLD_MIN_ACCESS_LEVEL");
		WORLD_PLAYER_THRESHOLD_ATTACKS = vars.getInteger("WORLD_PLAYER_THRESHOLD_ATTACKS");
		WORLD_PLAYER_THRESHOLD_BLOOKS = vars.getInteger("WORLD_PLAYER_THRESHOLD_BLOOKS");
		WORLD_PLAYER_TIME_BATTLE_STANCE = vars.getInteger("WORLD_PLAYER_TIME_BATTLE_STANCE") * 1000;
		WORLD_MAX_DIFF_LEVEL_ON_DROP = vars.getInteger("WORLD_MAX_DIFF_LEVEL_ON_DROP");
		WORLD_WIDTH_REGION = vars.getInteger("WORLD_WIDTH_REGION");
		WORLD_HEIGHT_REGION = vars.getInteger("WORLD_HEIGHT_REGION");
		WORLD_MAXIMUM_ONLINE = vars.getInteger("WORLD_MAXIMUM_ONLINE");
		WORLD_BANK_MAX_SIZE = vars.getInteger("WORLD_BANK_MAX_SIZE");
		WORLD_GUILD_BANK_MAX_SIZE = vars.getInteger("WORLD_GUILD_BANK_MAX_SIZE");
		WORLD_MAX_COLLECT_LEVEL = vars.getInteger("WORLD_MAX_COLLECT_LEVEL");
		WORLD_CONTINENT_COUNT = vars.getInteger("WORLD_CONTINENT_COUNT");
		WORLD_TRADE_MAX_RANGE = vars.getInteger("WORLD_TRADE_MAX_RANGE");
		WORLD_DUEL_MAX_RANGE = vars.getInteger("WORLD_DUEL_MAX_RANGE");
		WORLD_GUILD_INVITE_MAX_RANGE = vars.getInteger("WORLD_GUILD_INVITE_MAX_RANGE");
		WORLD_MAX_SKILL_DESYNC = vars.getInteger("WORLD_MAX_SKILL_DESYNC") * vars.getInteger("WORLD_MAX_SKILL_DESYNC");
		WORLD_PLAYER_MAX_LEVEL = vars.getInteger("WORLD_PLAYER_MAX_LEVEL");
		WORLD_CHANCE_DELETE_CRYSTAL = vars.getInteger("WORLD_CHANCE_DELETE_CRYSTAL", 13);
		WORLD_MIN_TARGET_LEVEL_FOR_PK = vars.getInteger("WORLD_MIN_TARGET_LEVEL_FOR_PK");
		WORLD_SHORT_SKILL_REUSE_MOD = vars.getFloat("WORLD_SHORT_SKILL_REUSE_MOD");
		WORLD_RANGE_SKILL_REUSE_MOD = vars.getFloat("WORLD_RANGE_SKILL_REUSE_MOD");
		WORLD_OTHER_SKILL_REUSE_MOD = vars.getFloat("WORLD_OTHER_SKILL_REUSE_MOD");
		WORLD_DONATE_ITEMS = vars.getIntegerArray("WORLD_DONATE_ITEMS", ",", Arrays.toIntegerArray());
		WORLD_SHOP_PRICE_MOD = vars.getFloat("WORLD_SHOP_PRICE_MOD");
		WORLD_AUTO_LEARN_SKILLS = vars.getBoolean("WORLD_AUTO_LEARN_SKILLS");
		WORLD_PK_AVAILABLE = vars.getBoolean("WORLD_PK_AVAILABLE");
		WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS = vars.getBoolean("WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS");
		DATA_BASE_DRIVER = vars.getString("DATA_BASE_DRIVER");
		DATA_BASE_URL = vars.getString("DATA_BASE_URL");
		DATA_BASE_LOGIN = vars.getString("DATA_BASE_LOGIN");
		DATA_BASE_PASSWORD = vars.getString("DATA_BASE_PASSWORD");
		DATA_BASE_MAX_CONNECTIONS = vars.getInteger("DATA_BASE_MAX_CONNECTIONS");
		DATA_BASE_MAX_STATEMENTS = vars.getInteger("DATA_BASE_MAX_STATEMENTS");
		DATA_BASE_CLEANING_START = vars.getBoolean("DATA_BASE_CLEANING_START");
		THREAD_POOL_SIZE_GENERAL = vars.getInteger("THREAD_POOL_SIZE_GENERAL");
		THREAD_POOL_SIZE_MOVE = vars.getInteger("THREAD_POOL_SIZE_MOVE");
		THREAD_POOL_SIZE_AI = vars.getInteger("THREAD_POOL_SIZE_AI");
		THREAD_POOL_PACKET_RUNNER = vars.getInteger("THREAD_POOL_PACKET_RUNNER");
		THREAD_POOL_SIZE_SKILL_USE = vars.getInteger("THREAD_POOL_SIZE_SKILL_USE");
		THREAD_POOL_SIZE_SKILL_CAST = vars.getInteger("THREAD_POOL_SIZE_SKILL_CAST");
		THREAD_POOL_SIZE_SKILL_MOVE = vars.getInteger("THREAD_POOL_SIZE_SKILL_MOVE");
		DEVELOPER_FORCE_ATTACK_SPEED = vars.getInteger("DEVELOPER_FORCE_ATTACK_SPEED");
		DEVELOPER_DEBUG_CLIENT_PACKETS = vars.getBoolean("DEVELOPER_DEBUG_CLIENT_PACKETS");
		DEVELOPER_DEBUG_SERVER_PACKETS = vars.getBoolean("DEVELOPER_DEBUG_SERVER_PACKETS");
		DEVELOPER_MAIN_DEBUG = vars.getBoolean("DEVELOPER_MAIN_DEBUG");
		DEVELOPER_DEBUG_TARGET_TYPE = vars.getBoolean("DEVELOPER_DEBUG_TARGET_TYPE");
		DEVELOPER_DEBUG_CASTING_SKILL = vars.getBoolean("DEVELOPER_DEBUG_CASTING_SKILL");
		DEVELOPER_DEBUG_MOVING_PLAYER = vars.getBoolean("DEVELOPER_DEBUG_MOVING_PLAYER");
		DEVELOPER_DEBUG_MOVING_NPC = vars.getBoolean("DEVELOPER_DEBUG_MOVING_NPC");
		DEVELOPER_GEO_LOGING = vars.getBoolean("DEVELOPER_GEO_LOGING");
		EVENT_MIN_TIMEOUT = vars.getInteger("EVENT_MIN_TIMEOUT");
		EVENT_MAX_TIMEOUT = vars.getInteger("EVENT_MAX_TIMEOUT");
		EVENT_TVT_REGISTER_TIME = vars.getInteger("EVENT_TVT_REGISTER_TIME");
		EVENT_TVT_BATTLE_TIME = vars.getInteger("EVENT_TVT_BATTLE_TIME");
		EVENT_TVT_MIN_PLAYERS = vars.getInteger("EVENT_TVT_MIN_PLAYERS");
		EVENT_TVT_MAX_PLAYERS = vars.getInteger("EVENT_TVT_MAX_PLAYERS");
		EVENT_TVT_MIN_LEVEL = vars.getInteger("EVENT_TVT_MIN_LEVEL");
		EVENT_TVT_MAX_LEVEL = vars.getInteger("EVENT_TVT_MAX_LEVEL");
		EVENT_LH_REGISTER_TIME = vars.getInteger("EVENT_LH_REGISTER_TIME");
		EVENT_LH_BATTLE_TIME = vars.getInteger("EVENT_LH_BATTLE_TIME");
		EVENT_LH_MIN_PLAYERS = vars.getInteger("EVENT_LH_MIN_PLAYERS");
		EVENT_LH_MAX_PLAYERS = vars.getInteger("EVENT_LH_MAX_PLAYERS");
		EVENT_LH_MIN_LEVEL = vars.getInteger("EVENT_LH_MIN_LEVEL");
		EVENT_LH_MAX_LEVEL = vars.getInteger("EVENT_LH_MAX_LEVEL");
		EVENT_TMT_REGISTER_TIME = vars.getInteger("EVENT_TMT_REGISTER_TIME");
		EVENT_TMT_BATTLE_TIME = vars.getInteger("EVENT_TMT_BATTLE_TIME");
		EVENT_TMT_MIN_TEAMS = vars.getInteger("EVENT_TMT_MIN_TEAMS");
		EVENT_TMT_MAX_TEAMS = vars.getInteger("EVENT_TMT_MAX_TEAMS");
		EVENT_TMT_MIN_TEAM_SIZE = vars.getInteger("EVENT_TMT_MIN_TEAM_SIZE");
		EVENT_TMT_MAX_TEAM_SIZE = vars.getInteger("EVENT_TMT_MAX_TEAM_SIZE");
		EVENT_TMT_MIN_LEVEL = vars.getInteger("EVENT_TMT_MIN_LEVEL");
		EVENT_TMT_MAX_LEVEL = vars.getInteger("EVENT_TMT_MAX_LEVEL");
		EVENT_EB_REGISTER_TIME = vars.getInteger("EVENT_EB_REGISTER_TIME");
		EVENT_EB_BATTLE_TIME = vars.getInteger("EVENT_EB_BATTLE_TIME");
		EVENT_EB_MIN_PLAYERS = vars.getInteger("EVENT_EB_MIN_PLAYERS");
		EVENT_EB_MAX_PLAYERS = vars.getInteger("EVENT_EB_MAX_PLAYERS");
		EVENT_EB_MIN_LEVEL = vars.getInteger("EVENT_EB_MIN_LEVEL");
		EVENT_EB_MAX_LEVEL = vars.getInteger("EVENT_EB_MAX_LEVEL");
		EVENT_TDM_REGISTER_TIME = vars.getInteger("EVENT_TDM_REGISTER_TIME");
		EVENT_TDM_BATTLE_TIME = vars.getInteger("EVENT_TDM_BATTLE_TIME");
		EVENT_TDM_MIN_PLAYERS = vars.getInteger("EVENT_TDM_MIN_PLAYERS");
		EVENT_TDM_MAX_PLAYERS = vars.getInteger("EVENT_TDM_MAX_PLAYERS");
		EVENT_TDM_MIN_LEVEL = vars.getInteger("EVENT_TDM_MIN_LEVEL");
		EVENT_TDM_MAX_LEVEL = vars.getInteger("EVENT_TDM_MAX_LEVEL");
		EVENT_HERO_POINT_TO_GOLD = vars.getInteger("EVENT_HERO_POINT_TO_GOLD");
		AI_MAX_ACTIVE_RANGE = vars.getInteger("AI_MAX_ACTIVE_RANGE");
		AI_MAX_REACTION_RANGE = vars.getInteger("AI_MAX_REACTION_RANGE");
		AI_MIN_RANDOM_WALK_RANGE = vars.getInteger("AI_MIN_RANDOM_WALK_RANGE");
		AI_MAX_RANDOM_WALK_RANGE = vars.getInteger("AI_MAX_RANDOM_WALK_RANGE");
		AI_MIN_RANDOM_WALK_DELAY = vars.getInteger("AI_MIN_RANDOM_WALK_DELAY");
		AI_MAX_RANDOM_WALK_DELAY = vars.getInteger("AI_MAX_RANDOM_WALK_DELAY");
		AI_TASK_DELAY = vars.getInteger("AI_TASK_DELAY");
		AI_ATTACK_RATE = vars.getInteger("AI_ATTACK_RATE");
		AI_BUFF_RATE = vars.getInteger("AI_BUFF_RATE");
		AI_DEBUFF_RATE = vars.getInteger("AI_DEBUFF_RATE");
		AI_DEFENSE_RATE = vars.getInteger("AI_DEFENSE_RATE");
		AI_JUMP_RATE = vars.getInteger("AI_JUMP_RATE");
		AI_ULTIMATE_RATE = vars.getInteger("AI_ULTIMATE_RATE");
		AI_SIDE_RATE = vars.getInteger("AI_SIDE_RATE");
		AI_SPRINT_RATE = vars.getInteger("AI_SPRINT_RATE", 50);
		NETWORK_GROUP_SIZE = vars.getInteger("NETWORK_GROUP_SIZE");
		NETWORK_READ_BUFFER_SIZE = vars.getInteger("NETWORK_READ_BUFFER_SIZE");
		NETWORK_THREAD_PRIORITY = vars.getInteger("NETWORK_THREAD_PRIORITY");
		NETWORK_WRITE_BUFFER_SIZE = vars.getInteger("NETWORK_WRITE_BUFFER_SIZE");
		NETWORK_MAXIMUM_PACKET_CUT = vars.getInteger("NETWORK_MAXIMUM_PACKET_CUT");
		DIST_CONTROL_LOGIN = vars.getString("DIST_CONTROL_LOGIN");
		DIST_CONTROL_PASSWORD = vars.getString("DIST_CONTROL_PASSWORD");
		DIST_CONTROL_PORT = vars.getInteger("DIST_CONTROL_PORT");
		DIST_CONTROL_CLIENT_INTERVAL = vars.getInteger("DIST_CONTROL_CLIENT_INTERVAL");
		DIST_CONTROL_ENABLED = vars.getBoolean("DIST_CONTROL_ENABLED");
		DATA_BASE_CONFIG.setJdbcUrl(DATA_BASE_URL);
		DATA_BASE_CONFIG.setUsername(DATA_BASE_LOGIN);
		DATA_BASE_CONFIG.setPassword(DATA_BASE_PASSWORD);
		DATA_BASE_CONFIG.setAcquireRetryAttempts(0);
		DATA_BASE_CONFIG.setAcquireIncrement(5);
		DATA_BASE_CONFIG.setReleaseHelperThreads(0);
		DATA_BASE_CONFIG.setMinConnectionsPerPartition(2);
		DATA_BASE_CONFIG.setMaxConnectionsPerPartition(DATA_BASE_MAX_CONNECTIONS);
		DATA_BASE_CONFIG.setStatementsCacheSize(DATA_BASE_MAX_STATEMENTS);
		final Properties properties = new Properties();
		properties.setProperty("useUnicode", "true");
		properties.setProperty("characterEncoding", "UTF-8");
		DATA_BASE_CONFIG.setDriverProperties(properties);
		namePattern = Pattern.compile(SERVER_NAME_TEMPLATE);
		Loggers.info("Config", "initialized.");
	}
	
	/**
	 * Method parseFiles.
	 * @param files File[]
	 * @param vars VarTable
	 */
	private static void parseFiles(File[] files, VarTable vars)
	{
		for (File file : files)
		{
			if (file.isHidden())
			{
				continue;
			}
			
			if (file.isDirectory() && !file.getName().contains("defaults"))
			{
				parseFiles(file.listFiles(), vars);
				continue;
			}
			
			if (file.getName().endsWith(".xml"))
			{
				vars.set(new DocumentConfig(file).parse());
			}
		}
	}
	
	public static void reload()
	{
		final VarTable vars = VarTable.newInstance();
		final File[] files = Files.getFiles(new File(SERVER_DIR + "/config"));
		parseFiles(files, vars);
		ACCOUNT_MIN_ACCESS_LEVEL = vars.getInteger("ACCOUNT_MIN_ACCESS_LEVEL");
		ACCOUNT_PREMIUM_EXP_RATE = vars.getFloat("ACCOUNT_PREMIUM_EXP_RATE");
		ACCOUNT_PREMIUM_MONEY_RATE = vars.getFloat("ACCOUNT_PREMIUM_MONEY_RATE");
		ACCOUNT_PREMIUM_DROP_RATE = vars.getFloat("ACCOUNT_PREMIUM_DROP_RATE");
		ACCOUNT_PREMIUM_QUEST_RATE = vars.getFloat("ACCOUNT_PREMIUM_QUEST_RATE");
		ACCOUNT_AUTO_CREATE = vars.getBoolean("ACCOUNT_AUTO_CREATE");
		ACCOUNT_ONLY_PAID = vars.getBoolean("ACCOUNT_ONLY_PAID");
		ACCOUNT_PREMIUM_EXP = vars.getBoolean("ACCOUNT_PREMIUM_EXP");
		ACCOUNT_PREMIUM_MONEY = vars.getBoolean("ACCOUNT_PREMIUM_MONEY");
		ACCOUNT_PREMIUM_DROP = vars.getBoolean("ACCOUNT_PREMIUM_DROP");
		ACCOUNT_PREMIUM_QUEST = vars.getBoolean("ACCOUNT_PREMIUM_QUEST");
		SERVER_NAME_TEMPLATE = vars.getString("SERVER_NAME_TEMPLATE");
		SERVER_ONLINE_FILE = vars.getString("SERVER_ONLINE_FILE", Strings.EMPTY);
		SERVER_PLAYER_SUB_ID = vars.getInteger("SERVER_PLAYER_SUB_ID");
		SERVER_NPC_SUB_ID = vars.getInteger("SERVER_NPC_SUB_ID");
		SERVER_ITEM_SUB_ID = vars.getInteger("SERVER_ITEM_SUB_ID");
		SERVER_SHOT_SUB_ID = vars.getInteger("SERVER_SHOT_SUB_ID");
		SERVER_OBJECT_SUB_ID = vars.getInteger("SERVER_OBJECT_SUB_ID");
		SERVER_TRAP_SUB_ID = vars.getInteger("SERVER_TRAP_SUB_ID");
		SERVER_RESOURSE_SUB_ID = vars.getInteger("SERVER_RESOURSE_SUB_ID");
		SERVER_PORT = vars.getInteger("SERVER_PORT");
		SERVER_RATE_EXP = vars.getFloat("SERVER_RATE_EXP");
		SERVER_PARTY_RATE_EXP = vars.getFloat("SERVER_PARTY_RATE_EXP");
		SERVER_RATE_MONEY = vars.getFloat("SERVER_RATE_MONEY");
		SERVER_RATE_DROP_ITEM = vars.getFloat("SERVER_RATE_DROP_ITEM");
		SERVER_ONLINE_FAKE = vars.getFloat("SERVER_ONLINE_FAKE");
		SERVER_USE_SNIFFER_OPCODE = vars.getBoolean("SERVER_USE_SNIFFER_OPCODE", false);
		WORLD_LIFE_TIME_DROP_ITEM = vars.getInteger("WORLD_LIFE_TIME_DROP_ITEM");
		WORLD_BLOCK_TIME_DROP_ITEM = vars.getInteger("WORLD_BLOCK_TIME_DROP_ITEM");
		WORLD_MIN_ACCESS_LEVEL = vars.getInteger("WORLD_MIN_ACCESS_LEVEL");
		WORLD_PLAYER_THRESHOLD_ATTACKS = vars.getInteger("WORLD_PLAYER_THRESHOLD_ATTACKS");
		WORLD_PLAYER_THRESHOLD_BLOOKS = vars.getInteger("WORLD_PLAYER_THRESHOLD_BLOOKS");
		WORLD_PLAYER_TIME_BATTLE_STANCE = vars.getInteger("WORLD_PLAYER_TIME_BATTLE_STANCE") * 1000;
		WORLD_MAX_DIFF_LEVEL_ON_DROP = vars.getInteger("WORLD_MAX_DIFF_LEVEL_ON_DROP");
		WORLD_WIDTH_REGION = vars.getInteger("WORLD_WIDTH_REGION");
		WORLD_HEIGHT_REGION = vars.getInteger("WORLD_HEIGHT_REGION");
		WORLD_MAXIMUM_ONLINE = vars.getInteger("WORLD_MAXIMUM_ONLINE");
		WORLD_BANK_MAX_SIZE = vars.getInteger("WORLD_BANK_MAX_SIZE");
		WORLD_GUILD_BANK_MAX_SIZE = vars.getInteger("WORLD_GUILD_BANK_MAX_SIZE");
		WORLD_MAX_COLLECT_LEVEL = vars.getInteger("WORLD_MAX_COLLECT_LEVEL");
		WORLD_PLAYER_MAX_LEVEL = vars.getInteger("WORLD_PLAYER_MAX_LEVEL");
		WORLD_CHANCE_DELETE_CRYSTAL = vars.getInteger("WORLD_CHANCE_DELETE_CRYSTAL", 13);
		WORLD_SHOP_PRICE_MOD = vars.getFloat("WORLD_SHOP_PRICE_MOD");
		WORLD_AUTO_LEARN_SKILLS = vars.getBoolean("WORLD_AUTO_LEARN_SKILLS");
		WORLD_PK_AVAILABLE = vars.getBoolean("WORLD_PK_AVAILABLE");
		WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS = vars.getBoolean("WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS");
		DEVELOPER_FORCE_ATTACK_SPEED = vars.getInteger("DEVELOPER_FORCE_ATTACK_SPEED");
		DEVELOPER_DEBUG_CLIENT_PACKETS = vars.getBoolean("DEVELOPER_DEBUG_CLIENT_PACKETS");
		DEVELOPER_DEBUG_SERVER_PACKETS = vars.getBoolean("DEVELOPER_DEBUG_SERVER_PACKETS");
		DEVELOPER_MAIN_DEBUG = vars.getBoolean("DEVELOPER_MAIN_DEBUG");
		DEVELOPER_DEBUG_TARGET_TYPE = vars.getBoolean("DEVELOPER_DEBUG_TARGET_TYPE");
		DEVELOPER_DEBUG_CASTING_SKILL = vars.getBoolean("DEVELOPER_DEBUG_CASTING_SKILL");
		DEVELOPER_DEBUG_MOVING_PLAYER = vars.getBoolean("DEVELOPER_DEBUG_MOVING_PLAYER");
		DEVELOPER_DEBUG_MOVING_NPC = vars.getBoolean("DEVELOPER_DEBUG_MOVING_NPC");
		DEVELOPER_GEO_LOGING = vars.getBoolean("DEVELOPER_GEO_LOGING");
		Loggers.info("Config", "reloaded.");
	}
	
	private Config()
	{
		throw new IllegalArgumentException();
	}
}
