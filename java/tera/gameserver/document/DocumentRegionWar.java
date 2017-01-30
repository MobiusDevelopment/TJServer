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
package tera.gameserver.document;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tera.Config;
import tera.gameserver.events.global.regionwars.Region;
import tera.gameserver.events.global.regionwars.RegionWars;
import tera.gameserver.model.npc.spawn.Spawn;
import tera.gameserver.model.skillengine.funcs.Func;
import tera.gameserver.model.territory.RegionTerritory;
import tera.gameserver.parser.FuncParser;
import tera.gameserver.tables.TerritoryTable;

import rlib.data.AbstractDocument;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 * @created 09.03.2012
 */
public final class DocumentRegionWar extends AbstractDocument<Array<Region>>
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private final RegionWars event;
	
	/**
	 * Constructor for DocumentRegionWar.
	 * @param file File
	 * @param regionWars RegionWars
	 */
	public DocumentRegionWar(File file, RegionWars regionWars)
	{
		super(file);
		event = regionWars;
	}
	
	/**
	 * Method create.
	 * @return Array<Region>
	 */
	@Override
	protected Array<Region> create()
	{
		return Arrays.toArray(Region.class);
	}
	
	/**
	 * Method parse.
	 * @param doc Document
	 */
	@Override
	protected void parse(Document doc)
	{
		final TerritoryTable territoryTable = TerritoryTable.getInstance();
		
		for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
		{
			if ("list".equals(list.getNodeName()))
			{
				for (Node node = list.getFirstChild(); node != null; node = node.getNextSibling())
				{
					if ("region".equals(node.getNodeName()))
					{
						final VarTable vars = VarTable.newInstance(node);
						final int id = vars.getInteger("id");
						final RegionTerritory territory = (RegionTerritory) territoryTable.getTerritory(id);
						
						if (territory == null)
						{
							log.warning(this, "not found territory for " + id);
							continue;
						}
						
						final Region region = new Region(event, territory);
						region.setInterval(vars.getLong("interval") * 60 * 60 * 1000);
						region.setBattleTime(vars.getLong("battleTime") * 60 * 1000);
						
						try
						{
							region.setStartTime(DATE_FORMAT.parse(vars.getString("startTime")).getTime());
						}
						catch (ParseException e)
						{
							log.warning(this, e);
						}
						
						region.setTax(vars.getInteger("tax"));
						
						for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
						{
							if (child.getNodeType() != Node.ELEMENT_NODE)
							{
								continue;
							}
							
							final String name = child.getNodeName();
							
							switch (name)
							{
								case "spawns":
									parseSpawns(region, child);
									break;
							}
						}
						
						parseFuncs(region, node);
						result.add(region);
					}
				}
			}
		}
	}
	
	/**
	 * Method parseFuncs.
	 * @param region Region
	 * @param node Node
	 */
	private void parseFuncs(Region region, Node node)
	{
		final Array<Func> positive = Arrays.toArray(Func.class);
		final Array<Func> negative = Arrays.toArray(Func.class);
		final FuncParser parser = FuncParser.getInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			
			if ("positive".equals(child.getNodeName()))
			{
				parser.parse(child, positive, file);
			}
			else if ("negative".equals(child.getNodeName()))
			{
				parser.parse(child, negative, file);
			}
		}
		
		positive.trimToSize();
		negative.trimToSize();
		region.setNegative(negative.array());
		region.setPositive(positive.array());
	}
	
	/**
	 * Method parseSpawns.
	 * @param region Region
	 * @param node Node
	 */
	private void parseSpawns(Region region, Node node)
	{
		final Array<Spawn> defense = Arrays.toArray(Spawn.class);
		final Array<Spawn> barriers = Arrays.toArray(Spawn.class);
		final Array<Spawn> control = Arrays.toArray(Spawn.class);
		final Array<Spawn> manager = Arrays.toArray(Spawn.class);
		final Array<Spawn> shops = Arrays.toArray(Spawn.class);
		final VarTable vars = VarTable.newInstance();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if ((child.getNodeType() != Node.ELEMENT_NODE) || !"spawn".equals(child.getNodeName()))
			{
				continue;
			}
			
			vars.parse(child);
			final File file = new File(Config.SERVER_DIR + vars.getString("filepath"));
			
			switch (vars.getString("name"))
			{
				case "control":
					control.addAll(new DocumentNpcSpawn(file).parse());
					break;
				
				case "defense":
					defense.addAll(new DocumentNpcSpawn(file).parse());
					break;
				
				case "barriers":
					barriers.addAll(new DocumentNpcSpawn(file).parse());
					break;
				
				case "manager":
					manager.addAll(new DocumentNpcSpawn(file).parse());
					break;
				
				case "shops":
					shops.addAll(new DocumentNpcSpawn(file).parse());
					break;
			}
		}
		
		defense.trimToSize();
		control.trimToSize();
		manager.trimToSize();
		barriers.trimToSize();
		shops.trimToSize();
		region.setControl(control.array());
		region.setDefense(defense.array());
		region.setManager(manager.array());
		region.setBarriers(barriers.array());
		region.setShops(shops.array());
	}
}