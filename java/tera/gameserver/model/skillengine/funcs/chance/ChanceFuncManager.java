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
package tera.gameserver.model.skillengine.funcs.chance;

import rlib.util.array.Array;
import rlib.util.array.Arrays;

/**
 * @author Ronn
 */
public final class ChanceFuncManager
{
	private static ChanceFuncManager instance;
	
	/**
	 * Method getInstance.
	 * @return ChanceFuncManager
	 */
	public static ChanceFuncManager getInstance()
	{
		if (instance == null)
		{
			instance = new ChanceFuncManager();
		}
		
		return instance;
	}
	
	private final Array<ChanceFunc> funcs;
	
	private ChanceFuncManager()
	{
		funcs = Arrays.toArray(ChanceFunc.class);
	}
	
	/**
	 * Method add.
	 * @param func ChanceFunc
	 */
	public void add(ChanceFunc func)
	{
		funcs.add(func);
	}
	
	public void prepare()
	{
		for (ChanceFunc func : funcs)
		{
			func.prepare();
		}
	}
}