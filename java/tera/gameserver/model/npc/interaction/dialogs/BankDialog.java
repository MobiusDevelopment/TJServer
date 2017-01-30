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

/**
 * @author Ronn
 */
public interface BankDialog extends Dialog
{
	/**
	 * Method addItem.
	 * @param index int
	 * @param itemId int
	 * @param count int
	 */
	public void addItem(int index, int itemId, int count);
	
	/**
	 * Method addMoney.
	 * @param money int
	 */
	public void addMoney(int money);
	
	/**
	 * Method getItem.
	 * @param index int
	 * @param itemId int
	 * @param count int
	 */
	public void getItem(int index, int itemId, int count);
	
	/**
	 * Method getMoney.
	 * @param money int
	 */
	public void getMoney(int money);
	
	/**
	 * Method movingItem.
	 * @param oldCell int
	 * @param newCell int
	 */
	public void movingItem(int oldCell, int newCell);
	
	/**
	 * Method setStartCell.
	 * @param startCell int
	 */
	public void setStartCell(int startCell);
	
	public void sort();
}