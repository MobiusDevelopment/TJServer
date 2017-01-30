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
package tera.gameserver.model.base;

import java.io.File;

import tera.Config;
import tera.gameserver.document.DocumentRaceAppearance;
import tera.gameserver.document.DocumentRaceStats;
import tera.gameserver.model.playable.PlayerAppearance;
import tera.gameserver.model.skillengine.funcs.Func;

import rlib.data.DocumentXML;
import rlib.logging.Logger;
import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
@SuppressWarnings("unused")
public enum Race
{
	
	HUMAN(0),
	
	ELF(1),
	
	AMAN(2),
	
	CASTANIC(3),
	
	POPORI(4),
	
	BARAKA(5),
	
	ELIN(4);
	
	private static final Logger log = Loggers.getLogger(Race.class);
	
	public static final Race[] VALUES = values();
	
	public static final int SIZE = VALUES.length;
	
	public static void init()
	{
		DocumentXML<Void> document = new DocumentRaceAppearance(new File(Config.SERVER_DIR + "/data/player_race_appearance.xml"));
		document.parse();
		document = new DocumentRaceStats(new File(Config.SERVER_DIR + "/data/player_race_stats.xml"));
		document.parse();
		log.info("race appearances initializable.");
	}
	
	/**
	 * Method valueOf.
	 * @param id int
	 * @param sex Sex
	 * @return Race
	 */
	public static Race valueOf(int id, Sex sex)
	{
		if ((id == ELIN.id) && (sex == Sex.FEMALE))
		{
			return ELIN;
		}
		
		return values()[id];
	}
	
	private PlayerAppearance male;
	
	private PlayerAppearance female;
	
	private Array<Func> funcs;
	
	private float regHp;
	
	private float regMp;
	
	private float powerFactor;
	
	private float defenseFactor;
	
	private float impactFactor;
	
	private float balanceFactor;
	
	private float atkSpd;
	
	private float runSpd;
	
	private float critRate;
	
	private float critRcpt;
	
	private int id;
	
	/**
	 * Constructor for Race.
	 * @param id int
	 */
	private Race(int id)
	{
		this.id = id;
	}
	
	/**
	 * Method setMale.
	 * @param male PlayerAppearance
	 */
	public void setMale(PlayerAppearance male)
	{
		this.male = male;
	}
	
	/**
	 * Method setFemale.
	 * @param female PlayerAppearance
	 */
	public void setFemale(PlayerAppearance female)
	{
		this.female = female;
	}
	
	/**
	 * Method getAppearance.
	 * @param sex Sex
	 * @return PlayerAppearance
	 */
	public PlayerAppearance getAppearance(Sex sex)
	{
		return sex == Sex.MALE ? male : female;
	}
	
	/**
	 * Method setFuncs.
	 * @param funcs Array<Func>
	 */
	public void setFuncs(Array<Func> funcs)
	{
		this.funcs = funcs;
	}
	
	/**
	 * Method getRegMp.
	 * @return float
	 */
	public float getRegMp()
	{
		return regMp;
	}
	
	/**
	 * Method getFuncs.
	 * @return Array<Func>
	 */
	public Array<Func> getFuncs()
	{
		return funcs;
	}
	
	/**
	 * Method getId.
	 * @return int
	 */
	public int getId()
	{
		return id;
	}
}
