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
package tera.gameserver.manager;

import tera.Config;
import tera.gameserver.events.Event;
import tera.gameserver.events.EventType;
import tera.gameserver.events.NpcInteractEvent;
import tera.gameserver.events.Registered;
import tera.gameserver.model.npc.Npc;
import tera.gameserver.model.npc.interaction.Link;
import tera.gameserver.model.playable.Player;

import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.SafeTask;
import rlib.util.array.Array;
import rlib.util.array.Arrays;
import rlib.util.random.Random;
import rlib.util.random.Randoms;
import rlib.util.table.Table;
import rlib.util.table.Tables;

/**
 * @author Ronn
 * @created 04.03.2012
 */
public final class EventManager
{
	private static final Logger log = Loggers.getLogger(EventManager.class);
	
	private static EventManager instance;
	
	/**
	 * Method getInstance.
	 * @return EventManager
	 */
	public static EventManager getInstance()
	{
		if (instance == null)
		{
			instance = new EventManager();
		}
		
		return instance;
	}
	
	final Random rand;
	
	private final SafeTask NEXT_EVENT = new SafeTask()
	{
		@Override
		protected void runImpl()
		{
			final Event event = autoable.get(rand.nextInt(0, autoable.size() - 1));
			
			if ((event == null) || (runAutoEvent != null) || !event.start())
			{
				final ExecutorManager executor = ExecutorManager.getInstance();
				executor.scheduleGeneral(this, rand.nextInt(Config.EVENT_MIN_TIMEOUT, Config.EVENT_MAX_TIMEOUT) * 60 * 1000);
			}
		}
	};
	
	private final Table<String, Event> nameEvents;
	
	private final Table<String, Registered> registeredEvents;
	
	private final Array<Event> events;
	
	final Array<Event> autoable;
	
	private final Array<NpcInteractEvent> npcInteractEvents;
	
	volatile Event runAutoEvent;
	
	private EventManager()
	{
		rand = Randoms.newRealRandom();
		nameEvents = Tables.newObjectTable();
		registeredEvents = Tables.newObjectTable();
		events = Arrays.toArray(Event.class);
		autoable = Arrays.toArray(Event.class);
		npcInteractEvents = Arrays.toArray(NpcInteractEvent.class);
		
		for (EventType type : EventType.values())
		{
			final Event example = type.get();
			
			if ((example == null) || !example.onLoad())
			{
				continue;
			}
			
			events.add(example);
			
			if (example.isAuto())
			{
				autoable.add(example);
			}
			
			nameEvents.put(example.getName(), example);
			
			if (example instanceof Registered)
			{
				registeredEvents.put(example.getName(), (Registered) example);
			}
			
			if (example instanceof NpcInteractEvent)
			{
				npcInteractEvents.add((NpcInteractEvent) example);
			}
		}
		
		log.info("loaded " + events.size() + " events.");
		
		if (!autoable.isEmpty())
		{
			final int time = rand.nextInt(Config.EVENT_MIN_TIMEOUT, Config.EVENT_MAX_TIMEOUT) * 60 * 1000;
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.scheduleGeneral(NEXT_EVENT, time);
			log.info("the nearest event in " + (time / 1000 / 60) + " minutes.");
		}
	}
	
	/**
	 * Method addLinks.
	 * @param links Array<Link>
	 * @param npc Npc
	 * @param player Player
	 */
	public void addLinks(Array<Link> links, Npc npc, Player player)
	{
		final NpcInteractEvent[] array = npcInteractEvents.array();
		
		for (int i = 0, length = npcInteractEvents.size(); i < length; i++)
		{
			array[i].addLinks(links, npc, player);
		}
	}
	
	/**
	 * Method finish.
	 * @param event Event
	 */
	public void finish(Event event)
	{
		if (event == null)
		{
			return;
		}
		
		if (event.isAuto())
		{
			setRunAutoEvent(null);
			final ExecutorManager executor = ExecutorManager.getInstance();
			executor.scheduleGeneral(NEXT_EVENT, rand.nextInt(Config.EVENT_MIN_TIMEOUT, Config.EVENT_MAX_TIMEOUT) * 60 * 1000);
		}
	}
	
	/**
	 * Method registerPlayer.
	 * @param eventName String
	 * @param player Player
	 */
	public void registerPlayer(String eventName, Player player)
	{
		final Registered event = registeredEvents.get(eventName);
		
		if (event != null)
		{
			event.registerPlayer(player);
		}
	}
	
	/**
	 * Method setRunAutoEvent.
	 * @param event Event
	 */
	public void setRunAutoEvent(Event event)
	{
		runAutoEvent = event;
	}
	
	/**
	 * Method start.
	 * @param event Event
	 */
	public void start(Event event)
	{
		if (event.isAuto())
		{
			setRunAutoEvent(event);
		}
	}
	
	/**
	 * Method startEvent.
	 * @param eventName String
	 */
	public void startEvent(String eventName)
	{
		final Event event = nameEvents.get(eventName);
		
		if ((event == null) || (event.isAuto() && (runAutoEvent != null)))
		{
			return;
		}
		
		event.start();
	}
	
	/**
	 * Method stopEvent.
	 * @param eventName String
	 */
	public void stopEvent(String eventName)
	{
		final Event event = nameEvents.get(eventName);
		
		if (event == null)
		{
			return;
		}
		
		event.stop();
	}
	
	/**
	 * Method unregisterPlayer.
	 * @param eventName String
	 * @param player Player
	 */
	public void unregisterPlayer(String eventName, Player player)
	{
		final Registered event = registeredEvents.get(eventName);
		
		if (event != null)
		{
			event.unregisterPlayer(player);
		}
	}
}
