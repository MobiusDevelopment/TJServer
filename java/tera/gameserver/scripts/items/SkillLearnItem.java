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
package tera.gameserver.scripts.items;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.table.IntKey;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 */
public class SkillLearnItem extends AbstractItemExecutor
{
	private final Table<IntKey, SkillTemplate[]> skillTable;
	
	/**
	 * Constructor for SkillLearnItem.
	 * @param itemIds int[]
	 * @param access int
	 */
	public SkillLearnItem(int[] itemIds, int access)
	{
		super(itemIds, access);
		skillTable = Tables.newIntegerTable();
		
		try
		{
			skillTableInit();
		}
		catch (Exception e)
		{
			log.warning(this, e);
		}
	}
	
	/**
	 * Method execution.
	 * @param item ItemInstance
	 * @param player Player
	 * @see tera.gameserver.scripts.items.ItemExecutor#execution(ItemInstance, Player)
	 */
	@Override
	public void execution(ItemInstance item, Player player)
	{
		final SkillTemplate[] template = skillTable.get(item.getItemId());
		
		if ((template == null) || (template.length < 1))
		{
			return;
		}
		
		final SkillTemplate first = template[0];
		
		if (player.getSkill(first.getId()) != null)
		{
			return;
		}
		
		final Inventory inventory = player.getInventory();
		
		if ((inventory != null) && inventory.removeItem(item.getItemId(), 1L))
		{
			player.addSkills(template, true);
			player.sendPacket(SystemMessage.getInstance(MessageType.YOUVE_LEARNED_SKILL_NAME).addSkillName(template[0].getName()), true);
			player.sendPacket(SystemMessage.getInstance(MessageType.ITEM_USE).addItem(item.getItemId(), 1), true);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyInventoryChanged(player);
		}
	}
	
	private void skillTableInit()
	{
		// Get table skills
		final SkillTable table = SkillTable.getInstance();
		
		// book for learning of the first mount
		skillTable.put(20, table.getSkills(-15, 67219975));
		
		// book for learning of other mounts
		skillTable.put(21, table.getSkills(-15, 67219976));
		skillTable.put(41, table.getSkills(-15, 67219978));
		skillTable.put(166, table.getSkills(-15, 67219991));
		skillTable.put(167, table.getSkills(-15, 67219980));
		skillTable.put(168, table.getSkills(-15, 67219981));
		skillTable.put(169, table.getSkills(-15, 67219982));
		skillTable.put(170, table.getSkills(-15, 67219983));
		skillTable.put(306, table.getSkills(-15, 67219985));
		skillTable.put(307, table.getSkills(-15, 67219986));
		skillTable.put(336, table.getSkills(-15, 67220054));
		skillTable.put(350, table.getSkills(-15, 67219988));
		skillTable.put(351, table.getSkills(-15, 67219989));
		skillTable.put(384, table.getSkills(-15, 67220061));
		skillTable.put(385, table.getSkills(-15, 67220062));
		skillTable.put(412, table.getSkills(-15, 67219990));
		skillTable.put(413, table.getSkills(-15, 67219991));
		skillTable.put(414, table.getSkills(-15, 67219992));
		skillTable.put(415, table.getSkills(-15, 67219981));
		skillTable.put(416, table.getSkills(-15, 67219982));
		skillTable.put(417, table.getSkills(-15, 67219996));
		skillTable.put(425, table.getSkills(-15, 67220056));
	}
}