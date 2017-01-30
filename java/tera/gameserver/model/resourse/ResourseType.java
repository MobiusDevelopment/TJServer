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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import tera.gameserver.templates.ResourseTemplate;

import rlib.logging.Loggers;

/**
 * @author Ronn
 */
public enum ResourseType
{
	QUEST(QuestResourse.class),
	PLANT(PlantResourse.class),
	MINING(MiningResourse.class),
	UNKNOWN(QuestResourse.class),
	ENERGY(EnergyResourse.class);
	private Constructor<? extends ResourseInstance> constructor;
	
	/**
	 * Constructor for ResourseType.
	 * @param type Class<? extends ResourseInstance>
	 */
	private ResourseType(Class<? extends ResourseInstance> type)
	{
		try
		{
			constructor = type.getConstructor(int.class, ResourseTemplate.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Loggers.warning(this, e);
		}
	}
	
	/**
	 * Method newInstance.
	 * @param template ResourseTemplate
	 * @return ResourseInstance
	 */
	public ResourseInstance newInstance(ResourseTemplate template)
	{
		try
		{
			return constructor.newInstance(0, template);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Loggers.warning(this, e);
		}
		
		return null;
	}
}