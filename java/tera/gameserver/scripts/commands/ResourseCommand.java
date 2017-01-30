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
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import tera.Config;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.resourse.ResourseInstance;
import tera.gameserver.model.resourse.ResourseSpawn;
import tera.gameserver.tables.ResourseTable;
import tera.util.Location;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public class ResourseCommand extends AbstractCommand
{
	private final Array<ResourseSpawn> waitSpawns;
	private String fileName;
	
	/**
	 * Constructor for ResourseCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public ResourseCommand(int access, String[] commands)
	{
		super(access, commands);
		waitSpawns = Arrays.toArray(ResourseSpawn.class);
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
		final ResourseTable resourseTable = ResourseTable.getInstance();
		
		switch (command)
		{
			case "spawn_resourse":
			{
				final int templateId = Integer.parseInt(values);
				final ResourseSpawn spawn = new ResourseSpawn(resourseTable.getTemplate(templateId), player.getLoc(), 30, 0, 0, 0);
				spawn.start();
				break;
			}
			
			case "set_export_file":
			{
				final File file = new File(Config.SERVER_DIR + "/" + values);
				
				if (file.exists())
				{
					player.sendMessage("The file already exists.");
					return;
				}
				
				fileName = values;
				break;
			}
			
			case "add_resourse":
			{
				final int templateId = Integer.parseInt(values);
				final ResourseSpawn spawn = new ResourseSpawn(resourseTable.getTemplate(templateId), player.getLoc(), 30, 0, 0, 0);
				spawn.start();
				waitSpawns.add(spawn);
				break;
			}
			
			case "export_resourse":
			{
				if (fileName == null)
				{
					player.sendMessage("You did not specify a file for export.");
					return;
				}
				
				final File file = new File(Config.SERVER_DIR + "/" + fileName);
				
				if (file.canWrite())
				{
					player.sendMessage("Unable to write to the file.");
					return;
				}
				
				player.sendMessage("export to " + file);
				
				try (PrintWriter out = new PrintWriter(file))
				{
					out.println("<?xml version='1.0' encoding='utf-8'?>");
					out.println("<list>");
					final Table<IntKey, Array<ResourseSpawn>> spawnTable = Tables.newIntegerTable();
					
					for (ResourseSpawn spawn : waitSpawns)
					{
						Array<ResourseSpawn> list = spawnTable.get(spawn.getTemplateId());
						
						if (list == null)
						{
							list = Arrays.toArray(ResourseSpawn.class);
							spawnTable.put(spawn.getTemplateId(), list);
						}
						
						list.add(spawn);
					}
					
					for (Array<ResourseSpawn> spawns : spawnTable)
					{
						final ResourseSpawn first = spawns.first();
						out.println("	<resourse id=\"" + first.getTemplateId() + "\" >");
						out.println("		<time respawn=\"45\" >");
						
						for (ResourseSpawn spawn : spawns)
						{
							final Location loc = spawn.getLoc();
							out.println("			<point x=\"" + loc.getX() + "\" y=\"" + loc.getY() + "\" z=\"" + loc.getZ() + "\" />");
						}
						
						out.println("		</time>");
						out.println("	</resourse>");
					}
					
					out.println("</list>");
				}
				catch (FileNotFoundException e)
				{
					player.sendMessage("An error occurred during export, change the file name.");
				}
				
				waitSpawns.clear();
				fileName = null;
				return;
			}
			
			case "around_resourse":
			{
				try
				{
					final Array<ResourseInstance> resourses = World.getAround(ResourseInstance.class, player, 150F);
					final StringBuilder text = new StringBuilder("Resourses:");
					
					for (ResourseInstance resourse : resourses)
					{
						if (resourse == null)
						{
							continue;
						}
						
						text.append(" id = ").append(resourse.getTemplateId()).append(", loc = ").append(resourse.getSpawn().getLoc()).append("; ");
					}
					
					if (text.length() > 5)
					{
						text.replace(text.length() - 2, text.length(), ".");
						player.sendMessage(text.toString());
					}
					else
					{
						player.sendMessage("no resourses.");
					}
				}
				catch (Exception e)
				{
					Loggers.warning(getClass(), "error " + command + " vals " + values + " " + e.getMessage());
				}
				
				break;
			}
		}
	}
}