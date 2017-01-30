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
package tera.gameserver.network.clientpackets;

import tera.gameserver.manager.PlayerManager;
import tera.gameserver.model.base.PlayerClass;
import tera.gameserver.model.base.Race;
import tera.gameserver.model.base.Sex;
import tera.gameserver.model.playable.PlayerAppearance;

/**
 * @author Ronn
 */
public class RequestCreatePlayer extends ClientPacket
{
	
	private String name;
	
	private PlayerAppearance appearance;
	
	private Sex sex;
	
	private Race race;
	
	private PlayerClass playerClass;
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		name = null;
		appearance = null;
	}
	
	/**
	 * Method isSynchronized.
	 * @return boolean
	 * @see rlib.network.packets.ReadeablePacket#isSynchronized()
	 */
	@Override
	public boolean isSynchronized()
	{
		return false;
	}
	
	@Override
	public void readImpl()
	{
		appearance = PlayerAppearance.getInstance(0);
		readInt();
		readShort();
		
		sex = Sex.valueOf(readByte());
		readByte();
		readByte();
		readByte();
		race = Race.valueOf(readByte(), sex);
		readByte();
		readByte();
		readByte();
		playerClass = PlayerClass.values()[readByte()];
		readByte();
		readByte();
		readByte();
		readByte();
		appearance.setFaceColor(readByte());
		appearance.setFaceSkin(readByte());
		appearance.setAdormentsSkin(readByte());
		appearance.setFeaturesSkin(readByte());
		appearance.setFeaturesColor(readByte());
		appearance.setVoice(readByte());
		readByte();
		readByte();
		
		name = readString();
		// final int pos = buffer.position();
		appearance.setBoneStructureBrow(readByte());
		appearance.setBoneStructureCheekbones(readByte());
		appearance.setBoneStructureJaw(readByte());
		appearance.setBoneStructureJawJut(readByte());
		appearance.setEarsRotation(readByte());
		appearance.setEarsExtension(readByte());
		appearance.setEarsTrim(readByte());
		appearance.setEarsSize(readByte());
		appearance.setEyesWidth(readByte());
		appearance.setEyesHeight(readByte());
		appearance.setEyesSeparation(readByte());
		readByte();
		appearance.setEyesAngle(readByte());
		appearance.setEyesInnerBrow(readByte());
		appearance.setEyesOuterBrow(readByte());
		readByte();
		appearance.setNoseExtension(readByte());
		appearance.setNoseSize(readByte());
		appearance.setNoseBridge(readByte());
		appearance.setNoseNostrilWidth(readByte());
		appearance.setNoseTipWidth(readByte());
		appearance.setNoseTip(readByte());
		appearance.setNoseNostrilFlare(readByte());
		appearance.setMouthPucker(readByte());
		appearance.setMouthPosition(readByte());
		appearance.setMouthWidth(readByte());
		appearance.setMouthLipThickness(readByte());
		appearance.setMouthCorners(readByte());
		appearance.setEyesShape(readByte());
		appearance.setNoseBend(readByte());
		appearance.setBoneStructureJawWidth(readByte());
		appearance.setMothGape(readByte());
	}
	
	@Override
	public void runImpl()
	{
		if (name == null)
		{
			return;
		}
		
		final PlayerManager playerManager = PlayerManager.getInstance();
		playerManager.createPlayer(getOwner(), appearance, name, playerClass, race, sex);
	}
}