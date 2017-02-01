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
package tera.gameserver.model.npc.interaction.dialogs;

import java.util.Comparator;

import tera.Config;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.manager.SkillLearnManager;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.SkillLearn;
import tera.gameserver.model.inventory.Bank;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.DialogPanel;
import tera.gameserver.network.serverpackets.DialogPanel.PanelType;
import tera.gameserver.network.serverpackets.HotKeyChanger;
import tera.gameserver.network.serverpackets.HotKeyChanger.ChangeType;
import tera.gameserver.network.serverpackets.SkillShopList;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.tables.SkillTable;
import tera.gameserver.templates.SkillTemplate;

import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.Arrays;
import rlib.util.table.IntKey;
import rlib.util.table.Table;

/**
 * @author Ronn
 * @created 25.02.2012
 */
public class SkillShopDialog extends AbstractDialog
{
	private static final Comparator<SkillLearn> comparator = new ArrayComparator<SkillLearn>()
	{
		@Override
		protected int compareImpl(SkillLearn first, SkillLearn second)
		{
			return first.getMinLevel() - second.getMinLevel();
		}
	};
	
	public static boolean isLearneableSkill(Player player, Table<IntKey, Skill> currentSkills, SkillLearn learn, boolean showMessage)
	{
		if (learn.getMinLevel() > player.getLevel())
		{
			if (showMessage)
			{
				player.sendMessage(MessageType.YOU_CANT_LEARN_SKILLS_AT_THIS_LEVEL);
			}
			
			return false;
		}
		
		if (currentSkills.containsKey(learn.getUseId()))
		{
			if (showMessage)
			{
				player.sendMessage("This skill has been learned.");
			}
			
			return false;
		}
		
		if ((learn.getReplaceId() != 0) && !currentSkills.containsKey(learn.getReplaceUseId()))
		{
			if (showMessage)
			{
				player.sendMessage(MessageType.YOU_NEED_TO_LEARN_ANOTHER_SKILL_FRST_BEFORE_YOU_CAN_LEARN_THIS_SKILL);
			}
			
			return false;
		}
		
		return true;
	}
	
	public static SkillShopDialog newInstance(Npc npc, Player player, Bank bank, float resultTax)
	{
		final SkillShopDialog dialog = (SkillShopDialog) DialogType.SKILL_SHOP.newInstance();
		dialog.npc = npc;
		dialog.player = player;
		dialog.bank = bank;
		dialog.resultTax = resultTax;
		return dialog;
	}
	
	private final Array<SkillLearn> learns;
	private Bank bank;
	private float resultTax;
	
	public SkillShopDialog()
	{
		learns = Arrays.toArray(SkillLearn.class);
	}
	
	@Override
	public synchronized boolean apply()
	{
		player.sendPacket(SkillShopList.getInstance(learns, player), true);
		return true;
	}
	
	@Override
	public void finalyze()
	{
		learns.clear();
		super.finalyze();
	}
	
	@Override
	public DialogType getType()
	{
		return DialogType.SKILL_SHOP;
	}
	
	@Override
	public synchronized boolean init()
	{
		if (!super.init())
		{
			return false;
		}
		
		final SkillLearnManager learnManager = SkillLearnManager.getInstance();
		learnManager.addAvailableSkills(learns, player);
		learns.sort(comparator);
		player.sendPacket(DialogPanel.getInstance(player, PanelType.SKILL_LEARN), true);
		player.sendPacket(SkillShopList.getInstance(learns, player), true);
		return true;
	}
	
	public final void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public synchronized boolean studySkill(int skillId)
	{
		final Player player = getPlayer();
		final Npc npc = getNpc();
		
		if ((player == null) || (npc == null))
		{
			return false;
		}
		
		if (!npc.isInRange(player, 200))
		{
			close();
			return false;
		}
		
		SkillLearn learn = null;
		final Array<SkillLearn> learns = getLearns();
		final SkillLearn[] array = learns.array();
		
		for (int i = 0, length = learns.size(); i < length; i++)
		{
			final SkillLearn temp = array[i];
			
			if (temp.getId() == skillId)
			{
				learn = temp;
				break;
			}
		}
		
		if (learn == null)
		{
			return false;
		}
		
		final Table<IntKey, Skill> skills = player.getSkills();
		
		if (!isLearneableSkill(player, skills, learn, true))
		{
			return false;
		}
		
		final Inventory inventory = player.getInventory();
		final Bank bank = getBank();
		
		if (!learn(bank, inventory, player, learn, resultTax))
		{
			return false;
		}
		
		learns.slowRemove(learn);
		return true;
	}
	
	public Array<SkillLearn> getLearns()
	{
		return learns;
	}
	
	public Bank getBank()
	{
		return bank;
	}
	
	private boolean learn(Bank bank, Inventory inventory, Player player, SkillLearn learn, float tax)
	{
		final long price = (long) (learn.getPrice() * tax);
		lock(bank);
		
		try
		{
			inventory.lock();
			
			try
			{
				if (inventory.getMoney() < price)
				{
					player.sendMessage(MessageType.YOU_DONT_HAVE_ENOUGH_GOLD_TO_LEARN_THAT_SKILL);
					return false;
				}
				
				if (!learn(player, learn))
				{
					return false;
				}
				
				inventory.subMoney(price);
				
				if (bank != null)
				{
					bank.addMoney(price - learn.getPrice());
				}
				
				final GameLogManager gameLogger = GameLogManager.getInstance();
				gameLogger.writeItemLog(player.getName() + " buy skill for " + price + " gold");
				final ObjectEventManager eventManager = ObjectEventManager.getInstance();
				eventManager.notifyInventoryChanged(player);
				return true;
			}
			
			finally
			{
				inventory.unlock();
			}
		}
		
		finally
		{
			unlock(bank);
		}
	}
	
	private boolean learn(Player player, SkillLearn learn)
	{
		final SkillTable skillTable = SkillTable.getInstance();
		final SkillTemplate[] skill = skillTable.getSkills(learn.getClassId(), learn.getUseId());
		
		if ((skill == null) || (skill.length < 1))
		{
			return false;
		}
		
		if (Config.WORLD_LEARN_ONLY_IMPLEMENTED_SKILLS && !skill[0].isImplemented())
		{
			return false;
		}
		
		final ObjectEventManager eventManager = ObjectEventManager.getInstance();
		
		if (learn.getReplaceId() > 0)
		{
			final SkillTemplate[] removed = skillTable.getSkills(learn.getClassId(), learn.getReplaceUseId());
			
			if (removed != null)
			{
				player.removeSkills(removed, false);
				player.sendPacket(HotKeyChanger.getInstance(ChangeType.REPLACE, removed[0].getId() - 67108864, skill[0].getId() - 67108864), true);
			}
		}
		
		player.addSkills(skill, true);
		player.sendPacket(SystemMessage.getInstance(MessageType.YOUVE_LEARNED_SKILL_NAME).addSkillName(skill[0].getName()), true);
		eventManager.notifySkillLearned(player, learn);
		return true;
	}
	
	public void lock(Bank bank)
	{
		if (bank != null)
		{
			bank.lock();
		}
	}
	
	public void unlock(Bank bank)
	{
		if (bank != null)
		{
			bank.unlock();
		}
	}
}