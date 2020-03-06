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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.manager.EventManager;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.World;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.quests.QuestList;
import tera.gameserver.model.skillengine.Calculator;
import tera.gameserver.model.skillengine.StatType;
import tera.gameserver.model.skillengine.funcs.StatFunc;
import tera.gameserver.model.skillengine.funcs.stat.MathFunc;
import tera.gameserver.model.skillengine.lambdas.FloatMul;
import tera.gameserver.model.skillengine.lambdas.FloatSet;
import tera.gameserver.network.serverpackets.CharState;
import tera.gameserver.network.serverpackets.EventMessage;
import tera.gameserver.network.serverpackets.SeverDeveloperPacket;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.tables.NpcDialogTable;
import tera.gameserver.tables.SkillTable;
import tera.remotecontrol.handlers.LoadChatHandler;

import rlib.geoengine.GeoQuard;
import rlib.logging.GameLoggers;
import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public class DeveloperCommand extends AbstractCommand
{
	private static final StatFunc SPEED = new MathFunc(StatType.RUN_SPEED, 0x45, null, new FloatSet(500));
	private static final StatFunc ATTACK = new MathFunc(StatType.ATTACK, 0x30, null, new FloatMul(50));
	
	/**
	 * Constructor for DeveloperCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public DeveloperCommand(int access, String[] commands)
	{
		super(access, commands);
	}
	
	/**
	 * Method execution.
	 * @param command String
	 * @param player Player
	 * @param values String
	 * @see tera.gameserver.scripts.commands.Command#execution(String, Player, String)
	 */
	@Override
	public void execution(String command, Player player, String values)
	{
		switch (command)
		{
			case "event_reg_all_players":
			{
				final EventManager eventManager = EventManager.getInstance();
				final Array<Player> players = World.getPlayers();
				players.readLock();
				
				try
				{
					for (Player target : players.array())
					{
						if (target == null)
						{
							break;
						}
						
						eventManager.registerPlayer(values, target);
					}
				}
				
				finally
				{
					players.readUnlock();
				}
				break;
			}
			
			case "zone":
			{
				player.sendMessage("ZONE :" + player.getZoneId());
				break;
			}
			
			case "change_class":
			{
				final DataBaseManager dbManager = DataBaseManager.getInstance();
				final PlayerClass cs = PlayerClass.valueOf(values);
				
				if (cs == player.getPlayerClass())
				{
					return;
				}
				
				dbManager.updatePlayerClass(player.getObjectId(), cs);
				player.sendMessage("The class has been changed to " + cs);
				break;
			}
			
			case "kick":
			{
				final Player target = World.getPlayer(values);
				
				if (target == null)
				{
					return;
				}
				
				player.sendMessage("Player \"" + target.getName() + "\" was kicked.");
				target.getClient().close();
				return;
			}
			
			case "start_gc":
			{
				System.gc();
				break;
			}
			
			case "start_event":
			{
				final EventManager eventManager = EventManager.getInstance();
				eventManager.startEvent(values);
				break;
			}
			
			case "a":
			{
				World.sendAnnounce(values);
				break;
			}
			
			case "reload_dialogs":
			{
				final NpcDialogTable dialogTable = NpcDialogTable.getInstance();
				dialogTable.reload();
				break;
			}
			
			case "gm_speed":
			{
				player.addStatFunc(SPEED);
				player.updateInfo();
				break;
			}
			
			case "check_geo":
			{
				final GeoManager geoManager = GeoManager.getInstance();
				final GeoQuard[] quards = geoManager.getQuards(player.getContinentId(), player.getX(), player.getY());
				player.sendMessage("geo : " + Arrays.toString(quards));
				break;
			}
			
			case "send_state":
			{
				final int val = Integer.parseInt(values);
				player.sendPacket(CharState.getInstance(player.getObjectId(), player.getSubId(), val), true);
				player.sendMessage("send state " + val);
				break;
			}
			
			case "add_attack":
			{
				ATTACK.addFuncTo(player);
				player.updateInfo();
				break;
			}
			
			case "send_system":
			{
				player.sendPacket(SystemMessage.getInstance(values.replace('&', (char) 0x0B)), true);
				break;
			}
			
			case "send_event":
			{
				player.sendPacket(EventMessage.getInstance(values, "", ""), true);
				break;
			}
			
			case "sub_attack":
			{
				ATTACK.removeFuncTo(player);
				player.updateInfo();
				break;
			}
			
			case "save_all":
			{
				final DataBaseManager dbManager = DataBaseManager.getInstance();
				
				for (Player member : World.getPlayers())
				{
					dbManager.fullStore(member);
					final QuestList questList = member.getQuestList();
					questList.save();
					player.sendMessage("Saved character \"" + member.getName() + "\"");
				}
				
				GameLoggers.finish();
				break;
			}
			
			case "save_point":
			{
				final String point = "<point x=\"" + (int) player.getX() + "\" y=\"" + (int) player.getY() + "\" z=\"" + (int) player.getZ() + "\" heading=\"" + player.getHeading() + "\" />";
				LoadChatHandler.add(point);
				System.out.println(point);
				break;
			}
			
			case "get_my_id":
			{
				final ByteBuffer buffer = ByteBuffer.wrap(new byte[4]).order(ByteOrder.LITTLE_ENDIAN);
				buffer.clear();
				buffer.putInt(player.getObjectId());
				buffer.flip();
				final StringBuilder text = new StringBuilder();
				
				for (byte byt : buffer.array())
				{
					text.append(Integer.toHexString(byt & 0xFF)).append(" ");
				}
				
				player.sendMessage("Server: your object id " + text.toString());
				break;
			}
			
			case "my_funcs":
			{
				final Calculator[] calcs = player.getCalcs();
				final StringBuilder text = new StringBuilder("Funcs: ");
				
				for (Calculator calc : calcs)
				{
					if ((calc != null) && (calc.getFuncs() != null) && (calc.getFuncs().size() > 0))
					{
						text.append(calc.getFuncs()).append(", ");
					}
				}
				
				player.sendMessage(text.toString());
				break;
			}
			
			case "invul":
			{
				player.setInvul(!player.isInvul());
				break;
			}
			
			case "send_bytes":
			{
				try
				{
					final String[] strBytes = values.split(" ");
					final List<Short> list = new ArrayList<>();
					
					for (String strByte : strBytes)
					{
						list.add(Short.parseShort(strByte, 16));
					}
					
					player.sendPacket(SeverDeveloperPacket.getInstance(list), true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				break;
			}
			
			case "send_file":
			{
				for (int i = 0; i < 10; i++)
				{
					final File file = new File("./data/packets/packet" + (i == 0 ? "" : String.valueOf(i)) + ".txt");
					
					try (Scanner in = new Scanner(file))
					{
						final List<Short> list = new ArrayList<>();
						
						if (in.hasNext())
						{
							for (String str = in.next(); in.hasNext(); str = in.next())
							{
								list.add(Short.parseShort(str, 16));
							}
						}
						
						player.sendPacket(SeverDeveloperPacket.getInstance(list), true);
					}
					catch (IOException e)
					{
						break;
					}
				}
				
				for (int i = 0; i < 10; i++)
				{
					final File file = new File("./data/packet" + (i == 0 ? "" : String.valueOf(i)) + ".txt");
					
					try (Scanner in = new Scanner(file))
					{
						final List<Short> list = new ArrayList<>();
						
						if (in.hasNext())
						{
							for (String str = in.next(); in.hasNext(); str = in.next())
							{
								list.add(Short.parseShort(str, 16));
							}
						}
						
						player.sendPacket(SeverDeveloperPacket.getInstance(list), true);
					}
					catch (IOException e)
					{
						break;
					}
				}
				
				break;
			}
			
			case "reload_skills":
			{
				final SkillTable skillTable = SkillTable.getInstance();
				skillTable.reload();
				break;
			}
			
			case "set_level":
			{
				try
				{
					final String[] vals = values.split(" ");
					
					if (vals.length < 1)
					{
						return;
					}
					
					final byte level = Byte.parseByte(vals[0]);
					Player target = player;
					
					if (vals.length > 1)
					{
						target = World.getAroundByName(Player.class, player, vals[1]);
					}
					
					if (target == null)
					{
						return;
					}
					
					if (level > target.getLevel())
					{
						for (int i = target.getLevel(); i < level; i++)
						{
							target.increaseLevel();
						}
					}
					else
					{
						target.setLevel(level);
						target.updateInfo();
					}
					
					final DataBaseManager dbManager = DataBaseManager.getInstance();
					dbManager.updatePlayerLevel(target);
				}
				catch (NumberFormatException e)
				{
					Loggers.warning(getClass(), "error parsing " + values);
				}
				
				break;
			}
			
			case "set_access_level":
			{
				try
				{
					final int val = Integer.parseInt(values);
					player.setAccessLevel(val);
				}
				catch (NumberFormatException e)
				{
					Loggers.warning(getClass(), "error parsing " + values);
				}
				
				break;
			}
			
			case "get_access_level":
			{
				player.sendMessage(String.valueOf(player.getAccessLevel()));
				break;
			}
			
			case "set_heart":
			{
				try
				{
					player.setStamina(Integer.parseInt(values));
					player.updateInfo();
				}
				catch (NumberFormatException e)
				{
					Loggers.warning(getClass(), "error parsing " + values);
				}
			}
		}
	}
}
