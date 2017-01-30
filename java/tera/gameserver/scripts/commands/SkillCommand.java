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
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.AppledCharmEffect;
import tera.gameserver.network.serverpackets.AppledEffect;
import tera.gameserver.network.serverpackets.CancelEffect;
import tera.gameserver.network.serverpackets.SkillEnd;
import tera.gameserver.network.serverpackets.SkillListInfo;
import tera.gameserver.network.serverpackets.SkillStart;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Loggers;
import rlib.util.SafeTask;

/**
 * @author Ronn
 */
public class SkillCommand extends AbstractCommand
{
	/**
	 * Constructor for SkillCommand.
	 * @param access int
	 * @param commands String[]
	 */
	public SkillCommand(int access, String[] commands)
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
	public void execution(String command, final Player player, String values)
	{
		final ExecutorManager executor = ExecutorManager.getInstance();
		final SkillTable skillTable = SkillTable.getInstance();
		
		switch (command)
		{
			case "start_skill":
			{
				final int id = Integer.parseInt(values);
				player.sendPacket(SkillStart.getInstance(player, id, 0, 0), true);
				final SafeTask task = new SafeTask()
				{
					@Override
					protected void runImpl()
					{
						player.sendPacket(SkillEnd.getInstance(player, 0, id), true);
					}
				};
				executor.scheduleGeneral(task, 3000);
				break;
			}
			
			case "effect":
			{
				final int id = Integer.parseInt(values);
				player.sendPacket(AppledEffect.getInstance(player, player, id, 10000), true);
				final SafeTask task = new SafeTask()
				{
					@Override
					protected void runImpl()
					{
						player.sendPacket(CancelEffect.getInstance(player, id), true);
					}
				};
				executor.scheduleGeneral(task, 10000);
				break;
			}
			
			case "charm":
			{
				final int id = Integer.parseInt(values);
				player.sendPacket(AppledCharmEffect.getInstance(player, id, 10000), true);
				final SafeTask task = new SafeTask()
				{
					@Override
					protected void runImpl()
					{
						player.sendPacket(CancelEffect.getInstance(player, id), true);
					}
				};
				executor.scheduleGeneral(task, 10000);
				break;
			}
			
			case "clear_skills":
			{
				player.getSkills().clear();
				player.sendPacket(SkillListInfo.getInstance(player), true);
				break;
			}
			
			case "get_base_skills":
			{
				player.getTemplate().giveSkills(player);
				player.sendPacket(SkillListInfo.getInstance(player), true);
				break;
			}
			
			case "reload_skills":
			{
				try
				{
					skillTable.reload();
					player.sendMessage("skills reloaded.");
				}
				catch (Exception e)
				{
					Loggers.warning(this, e);
				}
				
				break;
			}
			
			case "add_skills":
			{
				try
				{
					final String[] vals = values.split(" ", 2);
					
					if (vals.length < 2)
					{
						return;
					}
					
					final byte classId = Byte.parseByte(vals[0]);
					final int skillId = Integer.parseInt(vals[1]);
					final SkillTemplate[] skills = skillTable.getSkills(classId, skillId);
					Player target = player;
					
					if (vals.length > 2)
					{
						target = World.getAroundByName(Player.class, player, vals[2]);
					}
					
					target.addSkills(skills, true);
				}
				catch (NumberFormatException e)
				{
					Loggers.warning(getClass(), "error " + command + " vals " + values + " " + e.getMessage());
				}
				
				break;
			}
		}
	}
}
