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
package tera.gameserver.model.items;

import java.util.concurrent.ScheduledFuture;

import tera.Config;
import tera.gameserver.manager.ExecutorManager;
import tera.gameserver.manager.GameLogManager;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.Party;
import tera.gameserver.model.TObject;
import tera.gameserver.model.World;
import tera.gameserver.model.equipment.SlotType;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Skill;
import tera.gameserver.network.serverpackets.CharPickUpItem;
import tera.gameserver.network.serverpackets.DeleteCharacter;
import tera.gameserver.network.serverpackets.DeleteItem;
import tera.gameserver.network.serverpackets.ItemInfo;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.network.serverpackets.ServerPacket;
import tera.gameserver.network.serverpackets.SystemMessage;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.templates.SkillTemplate;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.Strings;
import rlib.util.pools.Foldable;

/**
 * @author Ronn
 */
public abstract class ItemInstance extends TObject implements Foldable
{
	static final Logger log = Loggers.getLogger(ItemInstance.class);
	protected long itemCount;
	protected long spawnTime;
	protected int ownerId;
	protected int bonusId;
	protected int enchantLevel;
	protected int index;
	protected ItemLocation location;
	protected String autor;
	protected ItemTemplate template;
	protected TObject dropper;
	protected TObject tempOwner;
	protected Party tempOwnerParty;
	protected Skill[] skills;
	protected SafeTask lifeTask;
	protected SafeTask blockTask;
	protected ScheduledFuture<SafeTask> lifeSchedule;
	protected ScheduledFuture<SafeTask> blockSchedule;
	
	public ItemInstance(int objectId, ItemTemplate template)
	{
		super(objectId);
		this.template = template;
		ownerId = 0;
		itemCount = 1;
		bonusId = 0;
		enchantLevel = 0;
		index = 0;
		autor = Strings.EMPTY;
		location = ItemLocation.NONE;
		final SkillTemplate[] templates = template.getSkills();
		skills = new Skill[templates.length];
		
		for (int i = 0, length = templates.length; i < length; i++)
		{
			skills[i] = templates[i].newInstance();
		}
		
		lifeTask = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				if ((System.currentTimeMillis() - spawnTime) < ((Config.WORLD_LIFE_TIME_DROP_ITEM * 100) - 1000))
				{
					log.warning(this, new Exception("it's fast despawn"));
				}
				
				deleteMe();
			}
		};
		blockTask = new SafeTask()
		{
			@Override
			protected void runImpl()
			{
				tempOwner = null;
				tempOwnerParty = null;
			}
		};
	}
	
	public final void addFuncsTo(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		template.addFuncsTo(owner);
		final CrystalList crystals = getCrystals();
		
		if ((crystals == null) || crystals.isEmpty())
		{
			return;
		}
		
		crystals.addFuncs(owner);
	}
	
	public final void addItemCount(long count)
	{
		itemCount += count;
	}
	
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(ItemInfo.getInstance(this), true);
	}
	
	public boolean checkClass(Player player)
	{
		return template.checkClass(player);
	}
	
	public boolean checkCrystal(CrystalInstance crystal)
	{
		return false;
	}
	
	@Override
	public void deleteMe()
	{
		super.deleteMe();
		synchronized (this)
		{
			if (lifeSchedule != null)
			{
				lifeSchedule.cancel(false);
				lifeSchedule = null;
			}
			
			if (blockSchedule != null)
			{
				blockSchedule.cancel(false);
				blockSchedule = null;
			}
			
			if (isDeleted())
			{
				return;
			}
			
			template.put(this);
		}
	}
	
	public boolean equipmentd(Character character, boolean showMessage)
	{
		return true;
	}
	
	@Override
	public void finalyze()
	{
		tempOwner = null;
		dropper = null;
		tempOwnerParty = null;
		autor = Strings.EMPTY;
		bonusId = 0;
		dropper = null;
		enchantLevel = 0;
		itemCount = 1;
		ownerId = 0;
		objectId = 0;
		index = 0;
		location = ItemLocation.NONE;
	}
	
	public Skill getActiveSkill()
	{
		return null;
	}
	
	public ArmorInstance getArmor()
	{
		return null;
	}
	
	public int getAttack()
	{
		return template.getAttack();
	}
	
	public final String getAutor()
	{
		return autor;
	}
	
	public int getBalance()
	{
		return template.getBalance();
	}
	
	public final int getBonusId()
	{
		return bonusId;
	}
	
	public BindType getBoundType()
	{
		return template.getBindType();
	}
	
	public final int getBuyPrice()
	{
		return template.getBuyPrice();
	}
	
	public int getClassIdItemSkill()
	{
		return template.getClassIdItemSkill();
	}
	
	public CommonInstance getCommon()
	{
		return null;
	}
	
	public CrystalInstance getCrystal()
	{
		return null;
	}
	
	public CrystalList getCrystals()
	{
		return null;
	}
	
	public int getDefence()
	{
		return template.getDefence();
	}
	
	public final TObject getDropper()
	{
		return dropper;
	}
	
	public final int getEnchantLevel()
	{
		return enchantLevel;
	}
	
	public int getExtractable()
	{
		return template.getExtractable();
	}
	
	public int getImpact()
	{
		return template.getImpact();
	}
	
	public final int getIndex()
	{
		return index;
	}
	
	public final ItemClass getItemClass()
	{
		return template.getItemClass();
	}
	
	public final long getItemCount()
	{
		return itemCount;
	}
	
	public final int getItemId()
	{
		return template.getItemId();
	}
	
	public final int getItemLevel()
	{
		return template.getItemLevel();
	}
	
	public final ItemLocation getLocation()
	{
		return location;
	}
	
	public final int getLocationId()
	{
		return location.ordinal();
	}
	
	@Override
	public final String getName()
	{
		return template.getName();
	}
	
	public final int getOwnerId()
	{
		return ownerId;
	}
	
	public String getOwnerName()
	{
		return Strings.EMPTY;
	}
	
	public Rank getRank()
	{
		return template.getRank();
	}
	
	public int getRequiredLevel()
	{
		return template.getRequiredLevel();
	}
	
	public final int getSellPrice()
	{
		return template.getSellPrice();
	}
	
	public final SkillTemplate[] getSkills()
	{
		return template.getSkills();
	}
	
	public final SlotType getSlotType()
	{
		return template.getSlotType();
	}
	
	public int getSockets()
	{
		return template.getSockets();
	}
	
	@Override
	public final int getSubId()
	{
		return Config.SERVER_ITEM_SUB_ID;
	}
	
	public ItemTemplate getTemplate()
	{
		return template;
	}
	
	public final TObject getTempOwner()
	{
		return tempOwner;
	}
	
	public final Party getTempOwnerParty()
	{
		return tempOwnerParty;
	}
	
	public Enum<?> getType()
	{
		return template.getType();
	}
	
	public WeaponInstance getWeapon()
	{
		return null;
	}
	
	public boolean hasCrystals()
	{
		final CrystalList crystals = getCrystals();
		return (crystals != null) && !crystals.isEmpty();
	}
	
	public boolean hasOwner()
	{
		return ownerId > 0;
	}
	
	public boolean isArmor()
	{
		return false;
	}
	
	public final boolean isBank()
	{
		return template.isBank();
	}
	
	public boolean isBinded()
	{
		return false;
	}
	
	public boolean isCommon()
	{
		return false;
	}
	
	public boolean isCrystal()
	{
		return false;
	}
	
	public final boolean isDeletable()
	{
		return template.isDeletable();
	}
	
	public boolean isEnchantable()
	{
		return template.isEnchantable();
	}
	
	public final boolean isGuildBank()
	{
		return template.isGuildBank();
	}
	
	public boolean isHerb()
	{
		return false;
	}
	
	@Override
	public final boolean isItem()
	{
		return true;
	}
	
	public boolean isRemodelable()
	{
		return template.isRemodelable();
	}
	
	public final boolean isSellable()
	{
		return template.isSellable();
	}
	
	public boolean isStackable()
	{
		return template.isStackable();
	}
	
	public final boolean isTradable()
	{
		return template.isTradable();
	}
	
	public boolean isWeapon()
	{
		return false;
	}
	
	@Override
	public boolean pickUpMe(TObject target)
	{
		if (!isVisible())
		{
			return false;
		}
		
		if (target == null)
		{
			log.warning(this, new Exception("not found target"));
			return false;
		}
		
		final Character character = target.getCharacter();
		
		if (character == null)
		{
			log.warning(this, new Exception("not found character"));
			return false;
		}
		
		if (character.isMoving())
		{
			character.sendMessage("You cannot pickup while moving.");
			return false;
		}
		
		final Party party = character.getParty();
		boolean pickUped = false;
		synchronized (this)
		{
			if (!isVisible())
			{
				return false;
			}
			
			try
			{
				if (party != null)
				{
					pickUped = party.pickUpItem(this, character);
				}
				else
				{
					final Inventory inventory = character.getInventory();
					
					if (inventory == null)
					{
						log.warning(this, new Exception("not found inventory"));
						return false;
					}
					
					if (!isVisible())
					{
						return false;
					}
					
					final long itemCount = getItemCount();
					
					if (inventory.putItem(this))
					{
						final GameLogManager gameLogger = GameLogManager.getInstance();
						gameLogger.writeItemLog(character.getName() + " pick up item [id = " + getItemId() + ", count = " + itemCount + ", name = " + template.getName() + "]");
						ServerPacket packet = null;
						
						if (template.getItemId() != Inventory.MONEY_ITEM_ID)
						{
							packet = MessageAddedItem.getInstance(character.getName(), template.getItemId(), (int) itemCount);
						}
						else
						{
							packet = SystemMessage.getInstance(MessageType.ADD_MONEY).addMoney(character.getName(), (int) itemCount);
						}
						
						character.sendPacket(packet, true);
						pickUped = true;
						return true;
					}
					else if (character.isPlayer())
					{
						character.sendMessage(MessageType.INVENTORY_IS_FULL);
					}
				}
			}
			
			finally
			{
				if (pickUped)
				{
					final ObjectEventManager eventManager = ObjectEventManager.getInstance();
					eventManager.notifyInventoryChanged(character);
					eventManager.notifyPickUpItem(character, this);
					character.broadcastPacket(CharPickUpItem.getInstance(character, this));
					
					if (hasOwner())
					{
						decayMe(DeleteCharacter.DISAPPEARS);
					}
					else
					{
						deleteMe();
					}
					
					if (lifeSchedule != null)
					{
						lifeSchedule.cancel(true);
						lifeSchedule = null;
					}
					
					if (blockSchedule != null)
					{
						blockSchedule.cancel(true);
						blockSchedule = null;
					}
					
					tempOwner = null;
					tempOwnerParty = null;
				}
			}
		}
		return false;
	}
	
	@Override
	public void reinit()
	{
	}
	
	public final void removeFuncsTo(Character owner)
	{
		if (owner == null)
		{
			return;
		}
		
		template.removeFuncsTo(owner);
		final CrystalList crystals = getCrystals();
		
		if ((crystals == null) || crystals.isEmpty())
		{
			return;
		}
		
		crystals.removeFuncs(owner);
	}
	
	@Override
	public void removeMe(Player player, int type)
	{
		player.sendPacket(DeleteItem.getInstance(this), true);
	}
	
	public final void setAutor(String autor)
	{
		this.autor = autor;
	}
	
	public final void setBonusId(int bonusId)
	{
		this.bonusId = bonusId;
	}
	
	public final void setDropper(TObject dropper)
	{
		this.dropper = dropper;
	}
	
	public void setEnchantLevel(int enchantLevel)
	{
		this.enchantLevel = enchantLevel;
	}
	
	public final void setIndex(int index)
	{
		this.index = index;
	}
	
	public final void setItemCount(long itemCount)
	{
		this.itemCount = itemCount;
	}
	
	public final void setLocation(ItemLocation location)
	{
		this.location = location;
	}
	
	@Override
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	public final void setOwnerId(int ownerId)
	{
		this.ownerId = ownerId;
	}
	
	public void setOwnerName(String ownerName)
	{
	}
	
	public final void setTempOwner(TObject tempOwner)
	{
		this.tempOwner = tempOwner;
	}
	
	public final void setTempOwnerParty(Party tempOwnerParty)
	{
		this.tempOwnerParty = tempOwnerParty;
	}
	
	@Override
	public void spawnMe()
	{
		synchronized (this)
		{
			if (isVisible())
			{
				return;
			}
			
			spawnTime = System.currentTimeMillis();
			World.addDroppedItems();
			final ExecutorManager executor = ExecutorManager.getInstance();
			lifeSchedule = executor.scheduleGeneral(lifeTask, Config.WORLD_LIFE_TIME_DROP_ITEM * 1000);
			
			if ((tempOwner != null) || (tempOwnerParty != null))
			{
				blockSchedule = executor.scheduleGeneral(blockTask, Config.WORLD_BLOCK_TIME_DROP_ITEM * 1000);
			}
		}
		super.spawnMe();
	}
	
	public final void subItemCount(long count)
	{
		itemCount -= count;
		
		if (itemCount < 0)
		{
			itemCount = 0;
		}
	}
	
	@Override
	public String toString()
	{
		return "ItemInstance type = " + template.getType() + ", name = " + template.getName() + ", itemCount = " + itemCount + ", ownerId = " + ownerId + ", objectId = " + objectId + ", location = " + location;
	}
}