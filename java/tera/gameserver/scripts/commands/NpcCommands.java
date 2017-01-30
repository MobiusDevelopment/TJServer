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

import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.model.World;
import tera.gameserver.model.ai.npc.NpcAIClass;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.IconType;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.npc.interaction.LinkType;
import tera.gameserver.model.npc.interaction.links.NpcLink;
import tera.gameserver.model.npc.spawn.NpcSpawn;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.NpcDialogWindow;
import tera.gameserver.network.serverpackets.NpcState;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.tables.ConfigAITable;
import tera.gameserver.tables.NpcTable;
import tera.gameserver.tables.SpawnTable;
import tera.gameserver.templates.NpcTemplate;
import tera.util.Location;

import rlib.logging.Loggers;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public class NpcCommands extends AbstractCommand
{
	/**
	 * Constructor for NpcCommands.
	 * @param access int
	 * @param commands String[]
	 */
	public NpcCommands(int access, String[] commands)
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
		final SpawnTable spawnTable = SpawnTable.getInstance();
		
		switch (command)
		{
			case "stop_spawns":
				spawnTable.stopSpawns();
				break;
			
			case "start_spawns":
				spawnTable.startSpawns();
				break;
			
			case "send_dialog":
			{
				final Array<Link> array = Arrays.toArray(Link.class);
				final int id = Integer.parseInt(values);
				
				for (int i = id, length = id + 10; i < length; i++)
				{
					array.add(new NpcLink("@npc:" + i, LinkType.DIALOG, IconType.DIALOG, null));
				}
				
				player.sendPacket(NpcDialogWindow.getInstance(null, player, array), true);
				break;
			}
			
			case "npc_cast":
			{
				final int skillId = Integer.parseInt(values);
				final Array<Npc> npcs = World.getAround(Npc.class, player, 300F);
				
				for (Npc npc : npcs)
				{
					final Skill skill = npc.getSkill(skillId);
					
					if (skill == null)
					{
						continue;
					}
					
					if (npc.isCastingNow())
					{
						npc.abortCast(true);
					}
					
					npc.setTarget(player);
					npc.getAI().startCast(skill, npc.calcHeading(player.getX(), player.getY()), player.getX(), player.getY(), player.getZ());
				}
				
				break;
			}
			
			case "around_npc_spawn":
			{
				final Array<Npc> npcs = World.getAround(Npc.class, player, 100F);
				final StringBuilder text = new StringBuilder("Spawns:");
				
				for (Npc npc : npcs)
				{
					if ((npc == null) || !(npc.getSpawn() instanceof NpcSpawn))
					{
						continue;
					}
					
					final NpcSpawn spawn = (NpcSpawn) npc.getSpawn();
					text.append(" [id = ").append(spawn.getTemplateId()).append(", ").append(spawn.getLocation()).append("], ");
				}
				
				if (text.length() > 7)
				{
					text.replace(text.length() - 2, text.length(), ".");
					player.sendMessage(text.toString());
				}
				else
				{
					player.sendMessage("no npcs.");
				}
				
				break;
			}
			
			case "reload_spawns":
			{
				try
				{
					spawnTable.reload();
				}
				catch (Exception e)
				{
					Loggers.warning(getClass(), e);
				}
				
				break;
			}
			
			case "around_npc_cast":
			{
				final int id = Integer.decode(values);
				final Array<Npc> npcs = World.getAround(Npc.class, player, 150F);
				
				for (Npc npc : npcs)
				{
					npc.broadcastPacket(SkillStart.getInstance(npc, id, 1, 0));
					player.sendMessage("cast " + id + " to " + npc);
					final Runnable run = () -> npc.broadcastPacket(SkillEnd.getInstance(npc, 1, id));
					final ExecutorManager executor = ExecutorManager.getInstance();
					executor.scheduleGeneral(run, 1000);
				}
				
				break;
			}
			
			case "around_npc_long_cast":
			{
				final int id = Integer.parseInt(values);
				final Array<Npc> npcs = World.getAround(Npc.class, player, 100F);
				
				for (Npc npc : npcs)
				{
					npc.broadcastPacket(SkillStart.getInstance(npc, id, 0, 0));
					final Runnable run = () -> npc.broadcastPacket(SkillEnd.getInstance(npc, 0, id));
					final ExecutorManager executor = ExecutorManager.getInstance();
					executor.scheduleGeneral(run, 4000);
				}
				
				break;
			}
			
			case "reload_npcs":
			{
				final NpcTable npcTable = NpcTable.getInstance();
				npcTable.reload();
				player.sendMessage("NPC table was reloaded.");
				break;
			}
			
			case "spawn":
			{
				final String vals[] = values.split(" ");
				final int id = Integer.parseInt(vals[0]);
				final int type = Integer.parseInt(vals[1]);
				final NpcTable npcTable = NpcTable.getInstance();
				final NpcTemplate template = npcTable.getTemplate(id, type);
				
				if (template == null)
				{
					player.sendMessage("No NPC found.");
					return;
				}
				
				final String aiConfig = vals.length > 2 ? vals[2] : "DefaultMonster";
				final ConfigAITable configTable = ConfigAITable.getInstance();
				final NpcSpawn spawn = new NpcSpawn(null, null, template, player.getLoc(), 45, 0, 120, 0, configTable.getConfig(aiConfig), NpcAIClass.DEFAULT);
				final int respawnTime = Integer.MAX_VALUE / 2000;
				spawn.setRespawnTime(respawnTime);
				spawn.start();
				break;
			}
			
			case "go_to_npc":
			{
				final String[] vals = values.split(" ");
				final Location loc = spawnTable.getNpcSpawnLoc(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
				
				if (loc != null)
				{
					player.teleToLocation(loc);
				}
				
				break;
			}
			
			case "around_npc":
			{
				try
				{
					final Array<Npc> npcs = World.getAround(Npc.class, player, 150F);
					final StringBuilder text = new StringBuilder("Npcs:");
					
					for (Npc npc : npcs)
					{
						if (npc == null)
						{
							continue;
						}
						
						text.append(" id = ").append(npc.getTemplateId()).append(", type = ").append(npc.getTemplateType()).append(", objectId = ").append(npc.getObjectId()).append(", ");
						npc.broadcastPacket(NpcState.getInstance(player, npc, 1));
					}
					
					if (text.length() > 5)
					{
						text.replace(text.length() - 2, text.length(), ".");
						player.sendMessage(text.toString());
					}
					else
					{
						player.sendMessage("no npcs.");
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