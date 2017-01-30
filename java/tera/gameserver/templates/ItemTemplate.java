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
package tera.gameserver.templates;

import tera.gameserver.IdFactory;
import tera.gameserver.manager.DataBaseManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.equipment.SlotType;
import tera.gameserver.model.items.ArmorKind;
import tera.gameserver.model.items.BindType;
import tera.gameserver.model.items.ItemClass;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.items.Rank;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.funcs.Func;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.Objects;
import rlib.util.Reloadable;
import rlib.util.VarTable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public abstract class ItemTemplate implements Reloadable<ItemTemplate>
{
	protected static final Logger log = Loggers.getLogger(ItemTemplate.class);
	
	public static final byte CLASS_ID_ITEM_SKILL = -9;
	
	protected final FoldablePool<ItemInstance> itemPool;
	
	protected String name;
	
	protected int itemId;
	
	protected int itemLevel;
	
	protected int buyPrice;
	
	protected int sellPrice;
	
	protected SlotType slotType;
	
	protected Rank rank;
	
	protected ItemClass itemClass;
	
	protected SkillTemplate[] skills;
	
	protected Func[] funcs;
	
	protected boolean stackable;
	
	protected boolean sellable;
	
	protected boolean bank;
	
	protected boolean guildBank;
	
	protected boolean tradable;
	
	protected boolean deletable;
	
	protected Enum<?> type;
	
	/**
	 * Constructor for ItemTemplate.
	 * @param type Enum<?>
	 * @param vars VarTable
	 */
	public ItemTemplate(Enum<?> type, VarTable vars)
	{
		this.type = type;
		
		try
		{
			name = vars.getString("name");
			itemId = vars.getInteger("id");
			itemLevel = vars.getInteger("itemLevel", 1);
			buyPrice = vars.getInteger("buyPrice", 0);
			sellPrice = vars.getInteger("sellPrice", 0);
			rank = Rank.valueOfXml(vars.getString("rank", "common"));
			itemClass = vars.getEnum("class", ItemClass.class);
			itemPool = Pools.newConcurrentFoldablePool(ItemInstance.class);
			stackable = vars.getBoolean("stackable", true);
			sellable = vars.getBoolean("sellable", true);
			bank = vars.getBoolean("bank", true);
			tradable = vars.getBoolean("tradable", true);
			guildBank = vars.getBoolean("guildBank", true);
			deletable = vars.getBoolean("deletable", true);
		}
		catch (Exception e)
		{
			log.warning(this, e);
			throw e;
		}
	}
	
	/**
	 * Method addFuncsTo.
	 * @param character Character
	 */
	public void addFuncsTo(Character character)
	{
		final Func[] funcs = getFuncs();
		
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.addFuncTo(character);
		}
	}
	
	/**
	 * Method checkClass.
	 * @param player Player
	 * @return boolean
	 */
	public boolean checkClass(Player player)
	{
		return true;
	}
	
	/**
	 * Method getActiveSkill.
	 * @return SkillTemplate
	 */
	public SkillTemplate getActiveSkill()
	{
		return null;
	}
	
	/**
	 * Method getAttack.
	 * @return int
	 */
	public int getAttack()
	{
		return 0;
	}
	
	/**
	 * Method getBalance.
	 * @return int
	 */
	public int getBalance()
	{
		return 0;
	}
	
	/**
	 * Method getBindType.
	 * @return BindType
	 */
	public BindType getBindType()
	{
		return BindType.NONE;
	}
	
	/**
	 * Method getBuyPrice.
	 * @return int
	 */
	public final int getBuyPrice()
	{
		return buyPrice;
	}
	
	/**
	 * Method getClassIdItemSkill.
	 * @return int
	 */
	public int getClassIdItemSkill()
	{
		return CLASS_ID_ITEM_SKILL;
	}
	
	/**
	 * Method getDefence.
	 * @return int
	 */
	public int getDefence()
	{
		return 0;
	}
	
	/**
	 * Method getExtractable.
	 * @return int
	 */
	public int getExtractable()
	{
		return 0;
	}
	
	/**
	 * Method getFuncs.
	 * @return Func[]
	 */
	public final Func[] getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method getImpact.
	 * @return int
	 */
	public int getImpact()
	{
		return 0;
	}
	
	/**
	 * Method getItemClass.
	 * @return ItemClass
	 */
	public final ItemClass getItemClass()
	{
		return itemClass;
	}
	
	/**
	 * Method getItemId.
	 * @return int
	 */
	public final int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Method getItemLevel.
	 * @return int
	 */
	public final int getItemLevel()
	{
		return itemLevel;
	}
	
	/**
	 * Method getItemPool.
	 * @return FoldablePool<ItemInstance>
	 */
	public final FoldablePool<ItemInstance> getItemPool()
	{
		return itemPool;
	}
	
	/**
	 * Method getKind.
	 * @return ArmorKind
	 */
	public ArmorKind getKind()
	{
		return ArmorKind.CLOTH;
	}
	
	/**
	 * Method getName.
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Method getRank.
	 * @return Rank
	 */
	public Rank getRank()
	{
		return rank;
	}
	
	/**
	 * Method getRequiredLevel.
	 * @return int
	 */
	public int getRequiredLevel()
	{
		return 0;
	}
	
	/**
	 * Method getSellPrice.
	 * @return int
	 */
	public final int getSellPrice()
	{
		return sellPrice;
	}
	
	/**
	 * Method getSkills.
	 * @return SkillTemplate[]
	 */
	public final SkillTemplate[] getSkills()
	{
		return skills;
	}
	
	/**
	 * Method getSlotType.
	 * @return SlotType
	 */
	public final SlotType getSlotType()
	{
		return slotType;
	}
	
	/**
	 * Method getSockets.
	 * @return int
	 */
	public int getSockets()
	{
		return 0;
	}
	
	/**
	 * Method getType.
	 * @return Enum<?>
	 */
	public Enum<?> getType()
	{
		return type;
	}
	
	/**
	 * Method isBank.
	 * @return boolean
	 */
	public final boolean isBank()
	{
		return bank;
	}
	
	/**
	 * Method isDeletable.
	 * @return boolean
	 */
	public final boolean isDeletable()
	{
		return deletable;
	}
	
	/**
	 * Method isEnchantable.
	 * @return boolean
	 */
	public boolean isEnchantable()
	{
		return false;
	}
	
	/**
	 * Method isGuildBank.
	 * @return boolean
	 */
	public final boolean isGuildBank()
	{
		return guildBank;
	}
	
	/**
	 * Method isRemodelable.
	 * @return boolean
	 */
	public boolean isRemodelable()
	{
		return false;
	}
	
	/**
	 * Method isSellable.
	 * @return boolean
	 */
	public final boolean isSellable()
	{
		return sellable;
	}
	
	/**
	 * Method isStackable.
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return stackable;
	}
	
	/**
	 * Method isTradable.
	 * @return boolean
	 */
	public final boolean isTradable()
	{
		return tradable;
	}
	
	/**
	 * Method newInstance.
	 * @return ItemInstance
	 */
	public ItemInstance newInstance()
	{
		ItemInstance item = itemPool.take();
		final IdFactory idFactory = IdFactory.getInstance();
		final int objectId = idFactory.getNextItemId();
		
		if (item == null)
		{
			item = itemClass.newInstance(objectId, this);
		}
		
		item.setObjectId(idFactory.getNextItemId());
		final DataBaseManager dbManager = DataBaseManager.getInstance();
		
		if (!dbManager.createItem(item))
		{
			return null;
		}
		
		return item;
	}
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @return ItemInstance
	 */
	public ItemInstance newInstance(int objectId)
	{
		ItemInstance item = itemPool.take();
		
		if (item == null)
		{
			item = itemClass.newInstance(objectId, this);
		}
		
		item.setObjectId(objectId);
		return item;
	}
	
	/**
	 * Method put.
	 * @param item ItemInstance
	 */
	public void put(ItemInstance item)
	{
		itemPool.put(item);
	}
	
	/**
	 * Method reload.
	 * @param update ItemTemplate
	 */
	@Override
	public void reload(ItemTemplate update)
	{
		if (getClass() != update.getClass())
		{
			return;
		}
		
		Objects.reload(this, update);
	}
	
	/**
	 * Method removeFuncsTo.
	 * @param character Character
	 */
	public void removeFuncsTo(Character character)
	{
		final Func[] funcs = getFuncs();
		
		if (funcs.length < 1)
		{
			return;
		}
		
		for (Func func : funcs)
		{
			func.removeFuncTo(character);
		}
	}
	
	/**
	 * Method setFuncs.
	 * @param funcs Func[]
	 */
	public final void setFuncs(Func[] funcs)
	{
		this.funcs = funcs;
	}
	
	/**
	 * Method setSellPrice.
	 * @param sellPrice int
	 */
	public void setSellPrice(int sellPrice)
	{
		this.sellPrice = sellPrice;
	}
	
	/**
	 * Method setSkills.
	 * @param skills SkillTemplate[]
	 */
	public final void setSkills(SkillTemplate[] skills)
	{
		this.skills = skills;
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ItemTemplate  name = " + name + ", itemId = " + itemId + ", buyPrice = " + buyPrice + ", sellPrice = " + sellPrice + ", type = " + type;
	}
}
