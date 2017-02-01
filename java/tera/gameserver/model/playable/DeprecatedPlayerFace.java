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
package tera.gameserver.model.playable;

import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public final class DeprecatedPlayerFace implements Foldable
{
	private static final FoldablePool<DeprecatedPlayerFace> pool = Pools.newConcurrentFoldablePool(DeprecatedPlayerFace.class);
	
	/**
	 * Method newInstance.
	 * @param objectId int
	 * @return DeprecatedPlayerFace
	 */
	public static DeprecatedPlayerFace newInstance(int objectId)
	{
		DeprecatedPlayerFace face = pool.take();
		
		if (face == null)
		{
			face = new DeprecatedPlayerFace(objectId);
		}
		else
		{
			face.objectId = objectId;
		}
		
		return face;
	}
	
	private int faceColor;
	private int hairColor;
	private int eyebrowsFirstVal;
	private int eyebrowsSecondVal;
	private int eyebrowsThridVal;
	private int eyeFirstVal;
	private int eyeSecondVal;
	private int eyeThridVal;
	private int eyePosVertical;
	private int eyeWidth;
	private int eyeHeight;
	private int chin;
	private int cheekbonePos;
	private int earsFirstVal;
	private int earsSecondVal;
	private int earsThridVal;
	private int earsFourthVal;
	private int noseFirstVal;
	private int noseSecondVal;
	private int noseThridVal;
	private int noseFourthVal;
	private int noseFifthVal;
	private int lipsFirstVal;
	private int lipsSecondVal;
	private int lipsThridVal;
	private int lipsFourthVal;
	private int lipsFifthVal;
	private int lipsSixthVal;
	private int cheeks;
	private int bridgeFirstVal;
	private int bridgeSecondVal;
	private int bridgeThridVal;
	private int objectId;
	public int[] tempVals;
	
	/**
	 * Constructor for DeprecatedPlayerFace.
	 * @param objectId int
	 */
	public DeprecatedPlayerFace(int objectId)
	{
		this.objectId = objectId;
		tempVals = new int[19];
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
		faceColor = 0;
		bridgeSecondVal = 0;
		bridgeFirstVal = 0;
		bridgeThridVal = 0;
		cheekbonePos = 0;
		cheeks = 0;
		chin = 0;
		earsFirstVal = 0;
		earsFourthVal = 0;
		earsSecondVal = 0;
		earsThridVal = 0;
		eyebrowsFirstVal = 0;
		eyebrowsSecondVal = 0;
		eyebrowsThridVal = 0;
		eyeFirstVal = 0;
		eyeHeight = 0;
		eyeWidth = 0;
		eyeThridVal = 0;
		faceColor = 0;
		hairColor = 0;
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method getBridgeFirstVal.
	 * @return int
	 */
	public int getBridgeFirstVal()
	{
		return bridgeFirstVal;
	}
	
	/**
	 * Method getBridgeSecondVal.
	 * @return int
	 */
	public int getBridgeSecondVal()
	{
		return bridgeSecondVal;
	}
	
	/**
	 * Method getBridgeThridVal.
	 * @return int
	 */
	public int getBridgeThridVal()
	{
		return bridgeThridVal;
	}
	
	/**
	 * Method getCheekbonePos.
	 * @return int
	 */
	public int getCheekbonePos()
	{
		return cheekbonePos;
	}
	
	/**
	 * Method getCheeks.
	 * @return int
	 */
	public int getCheeks()
	{
		return cheeks;
	}
	
	/**
	 * Method getChin.
	 * @return int
	 */
	public int getChin()
	{
		return chin;
	}
	
	/**
	 * Method getEarsFirstVal.
	 * @return int
	 */
	public int getEarsFirstVal()
	{
		return earsFirstVal;
	}
	
	/**
	 * Method getEarsFourthVal.
	 * @return int
	 */
	public int getEarsFourthVal()
	{
		return earsFourthVal;
	}
	
	/**
	 * Method getEarsSecondVal.
	 * @return int
	 */
	public int getEarsSecondVal()
	{
		return earsSecondVal;
	}
	
	/**
	 * Method getEarsThridVal.
	 * @return int
	 */
	public int getEarsThridVal()
	{
		return earsThridVal;
	}
	
	/**
	 * Method getEyebrowsFirstVal.
	 * @return int
	 */
	public int getEyebrowsFirstVal()
	{
		return eyebrowsFirstVal;
	}
	
	/**
	 * Method getEyebrowsSecondVal.
	 * @return int
	 */
	public int getEyebrowsSecondVal()
	{
		return eyebrowsSecondVal;
	}
	
	/**
	 * Method getEyebrowsThridVal.
	 * @return int
	 */
	public int getEyebrowsThridVal()
	{
		return eyebrowsThridVal;
	}
	
	/**
	 * Method getEyeFirstVal.
	 * @return int
	 */
	public int getEyeFirstVal()
	{
		return eyeFirstVal;
	}
	
	/**
	 * Method getEyeHeight.
	 * @return int
	 */
	public int getEyeHeight()
	{
		return eyeHeight;
	}
	
	/**
	 * Method getEyePosVertical.
	 * @return int
	 */
	public int getEyePosVertical()
	{
		return eyePosVertical;
	}
	
	/**
	 * Method getEyeSecondVal.
	 * @return int
	 */
	public int getEyeSecondVal()
	{
		return eyeSecondVal;
	}
	
	/**
	 * Method getEyeThridVal.
	 * @return int
	 */
	public int getEyeThridVal()
	{
		return eyeThridVal;
	}
	
	/**
	 * Method getEyeWidth.
	 * @return int
	 */
	public int getEyeWidth()
	{
		return eyeWidth;
	}
	
	/**
	 * Method getFaceColor.
	 * @return int
	 */
	public int getFaceColor()
	{
		return faceColor;
	}
	
	/**
	 * Method getHairColor.
	 * @return int
	 */
	public int getHairColor()
	{
		return hairColor;
	}
	
	/**
	 * Method getLipsFifthVal.
	 * @return int
	 */
	public int getLipsFifthVal()
	{
		return lipsFifthVal;
	}
	
	/**
	 * Method getLipsFirstVal.
	 * @return int
	 */
	public int getLipsFirstVal()
	{
		return lipsFirstVal;
	}
	
	/**
	 * Method getLipsFourthVal.
	 * @return int
	 */
	public int getLipsFourthVal()
	{
		return lipsFourthVal;
	}
	
	/**
	 * Method getLipsSecondVal.
	 * @return int
	 */
	public int getLipsSecondVal()
	{
		return lipsSecondVal;
	}
	
	/**
	 * Method getLipsSixthVal.
	 * @return int
	 */
	public int getLipsSixthVal()
	{
		return lipsSixthVal;
	}
	
	/**
	 * Method getLipsThridVal.
	 * @return int
	 */
	public int getLipsThridVal()
	{
		return lipsThridVal;
	}
	
	/**
	 * Method getNoseFifthVal.
	 * @return int
	 */
	public int getNoseFifthVal()
	{
		return noseFifthVal;
	}
	
	/**
	 * Method getNoseFirstVal.
	 * @return int
	 */
	public int getNoseFirstVal()
	{
		return noseFirstVal;
	}
	
	/**
	 * Method getNoseFourthVal.
	 * @return int
	 */
	public int getNoseFourthVal()
	{
		return noseFourthVal;
	}
	
	/**
	 * Method getNoseSecondVal.
	 * @return int
	 */
	public int getNoseSecondVal()
	{
		return noseSecondVal;
	}
	
	/**
	 * Method getNoseThridVal.
	 * @return int
	 */
	public int getNoseThridVal()
	{
		return noseThridVal;
	}
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	/**
	 * Method setBridgeFirstVal.
	 * @param bridgeFirstVal int
	 */
	public void setBridgeFirstVal(int bridgeFirstVal)
	{
		this.bridgeFirstVal = bridgeFirstVal;
	}
	
	/**
	 * Method setBridgeSecondVal.
	 * @param bridgeSecondVal int
	 */
	public void setBridgeSecondVal(int bridgeSecondVal)
	{
		this.bridgeSecondVal = bridgeSecondVal;
	}
	
	/**
	 * Method setBridgeThridVal.
	 * @param bridgeThridVal int
	 */
	public void setBridgeThridVal(int bridgeThridVal)
	{
		this.bridgeThridVal = bridgeThridVal;
	}
	
	/**
	 * Method setCheekbonePos.
	 * @param cheekbonePos int
	 */
	public void setCheekbonePos(int cheekbonePos)
	{
		this.cheekbonePos = cheekbonePos;
	}
	
	/**
	 * Method setCheeks.
	 * @param cheeks int
	 */
	public void setCheeks(int cheeks)
	{
		this.cheeks = cheeks;
	}
	
	/**
	 * Method setChin.
	 * @param chin int
	 */
	public void setChin(int chin)
	{
		this.chin = chin;
	}
	
	/**
	 * Method setEarsFirstVal.
	 * @param earsFirstVal int
	 */
	public void setEarsFirstVal(int earsFirstVal)
	{
		this.earsFirstVal = earsFirstVal;
	}
	
	/**
	 * Method setEarsFourthVal.
	 * @param earsFourthVal int
	 */
	public void setEarsFourthVal(int earsFourthVal)
	{
		this.earsFourthVal = earsFourthVal;
	}
	
	/**
	 * Method setEarsSecondVal.
	 * @param earsSecondVal int
	 */
	public void setEarsSecondVal(int earsSecondVal)
	{
		this.earsSecondVal = earsSecondVal;
	}
	
	/**
	 * Method setEarsThridVal.
	 * @param earsThridVal int
	 */
	public void setEarsThridVal(int earsThridVal)
	{
		this.earsThridVal = earsThridVal;
	}
	
	/**
	 * Method setEyebrowsFirstVal.
	 * @param eyebrowsFirstVal int
	 */
	public void setEyebrowsFirstVal(int eyebrowsFirstVal)
	{
		this.eyebrowsFirstVal = eyebrowsFirstVal;
	}
	
	/**
	 * Method setEyebrowsSecondVal.
	 * @param eyebrowsSecondVal int
	 */
	public void setEyebrowsSecondVal(int eyebrowsSecondVal)
	{
		this.eyebrowsSecondVal = eyebrowsSecondVal;
	}
	
	/**
	 * Method setEyebrowsThridVal.
	 * @param eyebrowsThridVal int
	 */
	public void setEyebrowsThridVal(int eyebrowsThridVal)
	{
		this.eyebrowsThridVal = eyebrowsThridVal;
	}
	
	/**
	 * Method setEyeFirstVal.
	 * @param eyeFirstVal int
	 */
	public void setEyeFirstVal(int eyeFirstVal)
	{
		this.eyeFirstVal = eyeFirstVal;
	}
	
	/**
	 * Method setEyeHeight.
	 * @param eyeHeight int
	 */
	public void setEyeHeight(int eyeHeight)
	{
		this.eyeHeight = eyeHeight;
	}
	
	/**
	 * Method setEyePosVertical.
	 * @param eyePosVertical int
	 */
	public void setEyePosVertical(int eyePosVertical)
	{
		this.eyePosVertical = eyePosVertical;
	}
	
	/**
	 * Method setEyeSecondVal.
	 * @param eyeSecondVal int
	 */
	public void setEyeSecondVal(int eyeSecondVal)
	{
		this.eyeSecondVal = eyeSecondVal;
	}
	
	/**
	 * Method setEyeThridVal.
	 * @param eyeThridVal int
	 */
	public void setEyeThridVal(int eyeThridVal)
	{
		this.eyeThridVal = eyeThridVal;
	}
	
	/**
	 * Method setEyeWidth.
	 * @param eyeWidth int
	 */
	public void setEyeWidth(int eyeWidth)
	{
		this.eyeWidth = eyeWidth;
	}
	
	/**
	 * Method setFaceColor.
	 * @param faceColor int
	 */
	public void setFaceColor(int faceColor)
	{
		this.faceColor = faceColor;
	}
	
	/**
	 * Method setHairColor.
	 * @param hairColor int
	 */
	public void setHairColor(int hairColor)
	{
		this.hairColor = hairColor;
	}
	
	/**
	 * Method setLipsFifthVal.
	 * @param lipsFifthVal int
	 */
	public void setLipsFifthVal(int lipsFifthVal)
	{
		this.lipsFifthVal = lipsFifthVal;
	}
	
	/**
	 * Method setLipsFirstVal.
	 * @param lipsFirstVal int
	 */
	public void setLipsFirstVal(int lipsFirstVal)
	{
		this.lipsFirstVal = lipsFirstVal;
	}
	
	/**
	 * Method setLipsFourthVal.
	 * @param lipsFourthVal int
	 */
	public void setLipsFourthVal(int lipsFourthVal)
	{
		this.lipsFourthVal = lipsFourthVal;
	}
	
	/**
	 * Method setLipsSecondVal.
	 * @param lipsSecondVal int
	 */
	public void setLipsSecondVal(int lipsSecondVal)
	{
		this.lipsSecondVal = lipsSecondVal;
	}
	
	/**
	 * Method setLipsSixthVal.
	 * @param lipsSixthVal int
	 */
	public void setLipsSixthVal(int lipsSixthVal)
	{
		this.lipsSixthVal = lipsSixthVal;
	}
	
	/**
	 * Method setLipsThridVal.
	 * @param lipsThridVal int
	 */
	public void setLipsThridVal(int lipsThridVal)
	{
		this.lipsThridVal = lipsThridVal;
	}
	
	/**
	 * Method setNoseFifthVal.
	 * @param noseFifthVal int
	 */
	public void setNoseFifthVal(int noseFifthVal)
	{
		this.noseFifthVal = noseFifthVal;
	}
	
	/**
	 * Method setNoseFirstVal.
	 * @param noseFirstVal int
	 */
	public void setNoseFirstVal(int noseFirstVal)
	{
		this.noseFirstVal = noseFirstVal;
	}
	
	/**
	 * Method setNoseFourthVal.
	 * @param noseFourthVal int
	 */
	public void setNoseFourthVal(int noseFourthVal)
	{
		this.noseFourthVal = noseFourthVal;
	}
	
	/**
	 * Method setNoseSecondVal.
	 * @param noseSecondVal int
	 */
	public void setNoseSecondVal(int noseSecondVal)
	{
		this.noseSecondVal = noseSecondVal;
	}
	
	/**
	 * Method setNoseThridVal.
	 * @param noseThridVal int
	 */
	public void setNoseThridVal(int noseThridVal)
	{
		this.noseThridVal = noseThridVal;
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method toAppearance.
	 * @return PlayerAppearance
	 */
	public PlayerAppearance toAppearance()
	{
		final PlayerAppearance appearance = PlayerAppearance.getInstance(objectId);
		appearance.setObjectId(objectId);
		appearance.setFaceColor(faceColor);
		appearance.setFaceSkin(tempVals[10]);
		appearance.setAdormentsSkin(tempVals[11]);
		appearance.setFeaturesSkin(tempVals[12]);
		appearance.setFeaturesColor(hairColor);
		appearance.setVoice(tempVals[13]);
		appearance.setBoneStructureBrow(eyebrowsFirstVal);
		appearance.setBoneStructureCheekbones(eyeFirstVal);
		appearance.setBoneStructureJaw(chin);
		appearance.setBoneStructureJawJut(cheekbonePos);
		appearance.setEarsRotation(earsFirstVal);
		appearance.setEarsExtension(earsSecondVal);
		appearance.setEarsTrim(earsThridVal);
		appearance.setEarsSize(earsFourthVal);
		appearance.setEyesWidth(eyeWidth);
		appearance.setEyesHeight(eyePosVertical);
		appearance.setEyesSeparation(eyeSecondVal);
		appearance.setEyesAngle(eyeThridVal);
		appearance.setEyesInnerBrow(eyebrowsSecondVal);
		appearance.setEyesOuterBrow(eyebrowsThridVal);
		appearance.setNoseExtension(noseFirstVal);
		appearance.setNoseSize(noseSecondVal);
		appearance.setNoseBridge(bridgeFirstVal);
		appearance.setNoseNostrilWidth(bridgeSecondVal);
		appearance.setNoseTipWidth(bridgeThridVal);
		appearance.setNoseTip(noseThridVal);
		appearance.setNoseNostrilFlare(noseFourthVal);
		appearance.setMouthPucker(lipsFirstVal);
		appearance.setMouthPosition(lipsSecondVal);
		appearance.setMouthWidth(lipsThridVal);
		appearance.setMouthLipThickness(lipsFourthVal);
		appearance.setMouthCorners(lipsFifthVal);
		appearance.setEyesShape(eyeHeight);
		appearance.setNoseBend(noseFifthVal);
		appearance.setBoneStructureJawWidth(cheeks);
		appearance.setMothGape(lipsSixthVal);
		return appearance;
	}
}