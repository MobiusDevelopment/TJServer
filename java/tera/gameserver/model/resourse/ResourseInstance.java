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
package tera.gameserver.model.resourse;

import tera.Config;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.MessageType;
import tera.gameserver.model.Party;
import tera.gameserver.model.TObject;
import tera.gameserver.model.drop.ResourseDrop;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.serverpackets.DeleteResourse;
import tera.gameserver.network.serverpackets.MessageAddedItem;
import tera.gameserver.network.serverpackets.ResourseInfo;
import tera.gameserver.templates.ResourseTemplate;
import tera.util.LocalObjects;

import rlib.geom.Coords;
import rlib.util.Rnd;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public abstract class ResourseInstance extends TObject
{
	protected final Array<Character> collectors;
	protected final ResourseTemplate template;
	protected ResourseSpawn spawn;
	protected volatile Party party;
	protected volatile boolean lock;
	
	/**
	 * Constructor for ResourseInstance.
	 * @param objectId int
	 * @param template ResourseTemplate
	 */
	public ResourseInstance(int objectId, ResourseTemplate template)
	{
		super(objectId);
		collectors = Arrays.toArray(Character.class);
		this.template = template;
	}
	
	/**
	 * Method addMe.
	 * @param player Player
	 */
	@Override
	public void addMe(Player player)
	{
		player.sendPacket(ResourseInfo.getInstance(this), true);
	}
	
	/**
	 * Method checkCondition.
	 * @param collector Player
	 * @return boolean
	 */
	public boolean checkCondition(Player collector)
	{
		return true;
	}
	
	/**
	 * Method collectMe.
	 * @param actor Character
	 */
	public void collectMe(Character actor)
	{
		if (!actor.isPlayer())
		{
			return;
		}
		
		final Player collector = actor.getPlayer();
		
		if (!checkCondition(collector))
		{
			collector.sendMessage(MessageType.YOU_CANT_DO_THAT_RIGHT_NOW_TRY_AGAINT_IN_A_MOMENT);
			return;
		}
		
		synchronized (this)
		{
			if (isLock())
			{
				return;
			}
			
			final Party party = getParty();
			
			if ((party == null) && !collectors.isEmpty())
			{
				actor.sendMessage(MessageType.ANOTHER_PLAYER_IS_ALREADY_GATHERING_THAT);
				return;
			}
			
			if ((party != null) && (actor.getParty() != party))
			{
				actor.sendMessage(MessageType.ANOTHER_PLAYER_IS_ALREADY_GATHERING_THAT);
				return;
			}
			
			if (collectors.contains(actor))
			{
				actor.sendMessage(MessageType.YOU_CANT_DO_THAT_RIGHT_NOW_TRY_AGAINT_IN_A_MOMENT);
				return;
			}
			
			if (party == null)
			{
				setParty(actor.getParty());
			}
		}
		collectors.add(actor);
		actor.doCollect(this);
	}
	
	@Override
	public void deleteMe()
	{
		collectors.clear();
		super.deleteMe();
		spawn.onCollected(this);
	}
	
	/**
	 * Method getChanceFor.
	 * @param player Player
	 * @return int
	 */
	public int getChanceFor(Player player)
	{
		return 80;
	}
	
	/**
	 * Method getCollectors.
	 * @return Array<Character>
	 */
	public final Array<Character> getCollectors()
	{
		return collectors;
	}
	
	/**
	 * Method getParty.
	 * @return Party
	 */
	public final Party getParty()
	{
		return party;
	}
	
	/**
	 * Method getResourse.
	 * @return ResourseInstance
	 */
	@Override
	public ResourseInstance getResourse()
	{
		return this;
	}
	
	/**
	 * Method getSpawn.
	 * @return ResourseSpawn
	 */
	public final ResourseSpawn getSpawn()
	{
		return spawn;
	}
	
	/**
	 * Method getSubId.
	 * @return int
	 */
	@Override
	public int getSubId()
	{
		return Config.SERVER_RESOURSE_SUB_ID;
	}
	
	/**
	 * Method getTemplate.
	 * @return ResourseTemplate
	 */
	public ResourseTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Method getTemplateId.
	 * @return int
	 */
	@Override
	public int getTemplateId()
	{
		return template.getId();
	}
	
	/**
	 * Method increaseReq.
	 * @param player Player
	 */
	public void increaseReq(Player player)
	{
		log.warning(this, new Exception("unsupported method"));
	}
	
	/**
	 * Method isLock.
	 * @return boolean
	 */
	public boolean isLock()
	{
		return lock;
	}
	
	/**
	 * Method isResourse.
	 * @return boolean
	 */
	@Override
	public boolean isResourse()
	{
		return true;
	}
	
	/**
	 * Method onCollected.
	 * @param collector Player
	 * @param cancel boolean
	 */
	public void onCollected(Player collector, boolean cancel)
	{
		if (isDeleted())
		{
			return;
		}
		
		setLock(true);
		boolean finish = false;
		synchronized (this)
		{
			if (collectors.isEmpty())
			{
				log.warning(this, new Exception("found incorrect finish collect."));
				return;
			}
			
			collectors.fastRemove(collector);
			finish = collectors.isEmpty();
		}
		
		if (!cancel)
		{
			collector.addExp(template.getExp(), null, getName());
			final ResourseTemplate template = getTemplate();
			final ResourseDrop drop = template.getDrop();
			
			if (drop != null)
			{
				final LocalObjects local = LocalObjects.get();
				final Array<ItemInstance> items = local.getNextItemList();
				drop.addDrop(items, this, collector);
				
				if (!items.isEmpty())
				{
					final Inventory inventory = collector.getInventory();
					
					if (inventory == null)
					{
						log.warning(this, new Exception("not found inventiry"));
					}
					else
					{
						final ItemInstance[] array = items.array();
						
						for (int i = 0, length = items.size(); i < length; i++)
						{
							final ItemInstance item = array[i];
							
							if (!inventory.putItem(item))
							{
								collector.sendMessage(MessageType.INVENTORY_IS_FULL);
								item.setContinentId(collector.getContinentId());
								final int heading = Rnd.nextInt(0, 32000) + collector.getHeading();
								final int dist = Rnd.nextInt(30, 60);
								final float x = Coords.calcX(collector.getX(), dist, heading);
								final float y = Coords.calcY(collector.getY(), dist, heading);
								item.setTempOwner(collector);
								item.setDropper(collector);
								item.spawnMe(x, y, collector.getZ(), 0);
							}
							else
							{
								collector.sendPacket(MessageAddedItem.getInstance(collector.getName(), item.getItemId(), (int) item.getItemCount()), true);
								
								if (!item.hasOwner())
								{
									item.deleteMe();
								}
							}
						}
					}
				}
			}
			
			increaseReq(collector);
			final ObjectEventManager eventManager = ObjectEventManager.getInstance();
			eventManager.notifyCollect(this, collector);
		}
		
		if (finish)
		{
			deleteMe();
		}
	}
	
	/**
	 * Method removeMe.
	 * @param player Player
	 * @param type int
	 */
	@Override
	public void removeMe(Player player, int type)
	{
		player.sendPacket(DeleteResourse.getInstance(this, type), true);
	}
	
	/**
	 * Method setLock.
	 * @param lock boolean
	 */
	public void setLock(boolean lock)
	{
		this.lock = lock;
	}
	
	/**
	 * Method setParty.
	 * @param party Party
	 */
	public final void setParty(Party party)
	{
		this.party = party;
	}
	
	/**
	 * Method setSpawn.
	 * @param spawn ResourseSpawn
	 */
	public final void setSpawn(ResourseSpawn spawn)
	{
		this.spawn = spawn;
	}
	
	@Override
	public void spawnMe()
	{
		setLock(lock);
		super.spawnMe();
	}
	
	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ResourseInstance [getTemplateId()=" + getTemplateId() + "]";
	}
}