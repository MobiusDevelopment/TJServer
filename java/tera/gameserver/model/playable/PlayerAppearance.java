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

import java.lang.reflect.Field;

import rlib.logging.Loggers;
import rlib.util.ReflectionUtils;
import rlib.util.VarTable;
import rlib.util.array.Array;
import rlib.util.pools.Foldable;
import rlib.util.pools.FoldablePool;
import rlib.util.pools.Pools;

/**
 * @author Ronn
 */
public class PlayerAppearance implements Foldable, Cloneable
{
	private static final FoldablePool<PlayerAppearance> pool = Pools.newConcurrentFoldablePool(PlayerAppearance.class);
	
	/**
	 * Method getInstance.
	 * @param objectId int
	 * @return PlayerAppearance
	 */
	public static final PlayerAppearance getInstance(int objectId)
	{
		PlayerAppearance appearance = pool.take();
		
		if (appearance == null)
		{
			appearance = new PlayerAppearance();
		}
		
		appearance.setObjectId(objectId);
		return appearance;
	}
	
	private int objectId;
	private int faceColor;
	private int faceSkin;
	private int adormentsSkin;
	private int featuresSkin;
	private int featuresColor;
	private int voice;
	private int boneStructureBrow;
	private int boneStructureCheekbones;
	private int boneStructureJaw;
	private int boneStructureJawJut;
	private int earsRotation;
	private int earsExtension;
	private int earsTrim;
	private int earsSize;
	private int eyesWidth;
	private int eyesHeight;
	private int eyesSeparation;
	private int eyesAngle;
	private int eyesInnerBrow;
	private int eyesOuterBrow;
	private int noseExtension;
	private int noseSize;
	private int noseBridge;
	private int noseNostrilWidth;
	private int noseTipWidth;
	private int noseTip;
	private int noseNostrilFlare;
	private int mouthPucker;
	private int mouthPosition;
	private int mouthWidth;
	private int mouthLipThickness;
	private int mouthCorners;
	private int eyesShape;
	private int noseBend;
	private int boneStructureJawWidth;
	private int mothGape;
	
	/**
	 * Method getObjectId.
	 * @return int
	 */
	public final int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Method setObjectId.
	 * @param objectId int
	 */
	public final void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Method getFaceColor.
	 * @return int
	 */
	public final int getFaceColor()
	{
		return faceColor;
	}
	
	/**
	 * Method setFaceColor.
	 * @param faceColor int
	 */
	public final void setFaceColor(int faceColor)
	{
		this.faceColor = faceColor;
	}
	
	/**
	 * Method getFaceSkin.
	 * @return int
	 */
	public final int getFaceSkin()
	{
		return faceSkin;
	}
	
	/**
	 * Method setFaceSkin.
	 * @param faceSkin int
	 */
	public final void setFaceSkin(int faceSkin)
	{
		this.faceSkin = faceSkin;
	}
	
	/**
	 * Method getAdormentsSkin.
	 * @return int
	 */
	public final int getAdormentsSkin()
	{
		return adormentsSkin;
	}
	
	/**
	 * Method setAdormentsSkin.
	 * @param adormentsSkin int
	 */
	public final void setAdormentsSkin(int adormentsSkin)
	{
		this.adormentsSkin = adormentsSkin;
	}
	
	/**
	 * Method getFeaturesSkin.
	 * @return int
	 */
	public final int getFeaturesSkin()
	{
		return featuresSkin;
	}
	
	/**
	 * Method setFeaturesSkin.
	 * @param featuresSkin int
	 */
	public final void setFeaturesSkin(int featuresSkin)
	{
		this.featuresSkin = featuresSkin;
	}
	
	/**
	 * Method getFeaturesColor.
	 * @return int
	 */
	public final int getFeaturesColor()
	{
		return featuresColor;
	}
	
	/**
	 * Method setFeaturesColor.
	 * @param featuresColor int
	 */
	public final void setFeaturesColor(int featuresColor)
	{
		this.featuresColor = featuresColor;
	}
	
	/**
	 * Method getVoice.
	 * @return int
	 */
	public final int getVoice()
	{
		return voice;
	}
	
	/**
	 * Method setVoice.
	 * @param voice int
	 */
	public final void setVoice(int voice)
	{
		this.voice = voice;
	}
	
	/**
	 * Method getBoneStructureBrow.
	 * @return int
	 */
	public final int getBoneStructureBrow()
	{
		return boneStructureBrow;
	}
	
	/**
	 * Method setBoneStructureBrow.
	 * @param boneStructureBrow int
	 */
	public final void setBoneStructureBrow(int boneStructureBrow)
	{
		this.boneStructureBrow = boneStructureBrow;
	}
	
	/**
	 * Method getBoneStructureCheekbones.
	 * @return int
	 */
	public final int getBoneStructureCheekbones()
	{
		return boneStructureCheekbones;
	}
	
	/**
	 * Method setBoneStructureCheekbones.
	 * @param boneStructureCheekbones int
	 */
	public final void setBoneStructureCheekbones(int boneStructureCheekbones)
	{
		this.boneStructureCheekbones = boneStructureCheekbones;
	}
	
	/**
	 * Method getBoneStructureJaw.
	 * @return int
	 */
	public final int getBoneStructureJaw()
	{
		return boneStructureJaw;
	}
	
	/**
	 * Method setBoneStructureJaw.
	 * @param boneStructureJaw int
	 */
	public final void setBoneStructureJaw(int boneStructureJaw)
	{
		this.boneStructureJaw = boneStructureJaw;
	}
	
	/**
	 * Method getBoneStructureJawJut.
	 * @return int
	 */
	public final int getBoneStructureJawJut()
	{
		return boneStructureJawJut;
	}
	
	/**
	 * Method setBoneStructureJawJut.
	 * @param boneStructureJawJut int
	 */
	public final void setBoneStructureJawJut(int boneStructureJawJut)
	{
		this.boneStructureJawJut = boneStructureJawJut;
	}
	
	/**
	 * Method getEarsRotation.
	 * @return int
	 */
	public final int getEarsRotation()
	{
		return earsRotation;
	}
	
	/**
	 * Method setEarsRotation.
	 * @param earsRotation int
	 */
	public final void setEarsRotation(int earsRotation)
	{
		this.earsRotation = earsRotation;
	}
	
	/**
	 * Method getEarsExtension.
	 * @return int
	 */
	public final int getEarsExtension()
	{
		return earsExtension;
	}
	
	/**
	 * Method setEarsExtension.
	 * @param earsExtension int
	 */
	public final void setEarsExtension(int earsExtension)
	{
		this.earsExtension = earsExtension;
	}
	
	/**
	 * Method getEarsTrim.
	 * @return int
	 */
	public final int getEarsTrim()
	{
		return earsTrim;
	}
	
	/**
	 * Method setEarsTrim.
	 * @param earsTrim int
	 */
	public final void setEarsTrim(int earsTrim)
	{
		this.earsTrim = earsTrim;
	}
	
	/**
	 * Method getEarsSize.
	 * @return int
	 */
	public final int getEarsSize()
	{
		return earsSize;
	}
	
	/**
	 * Method setEarsSize.
	 * @param earsSize int
	 */
	public final void setEarsSize(int earsSize)
	{
		this.earsSize = earsSize;
	}
	
	/**
	 * Method getEyesWidth.
	 * @return int
	 */
	public final int getEyesWidth()
	{
		return eyesWidth;
	}
	
	/**
	 * Method setEyesWidth.
	 * @param eyesWidth int
	 */
	public final void setEyesWidth(int eyesWidth)
	{
		this.eyesWidth = eyesWidth;
	}
	
	/**
	 * Method getEyesHeight.
	 * @return int
	 */
	public final int getEyesHeight()
	{
		return eyesHeight;
	}
	
	/**
	 * Method setEyesHeight.
	 * @param eyesHeight int
	 */
	public final void setEyesHeight(int eyesHeight)
	{
		this.eyesHeight = eyesHeight;
	}
	
	/**
	 * Method getEyesSeparation.
	 * @return int
	 */
	public final int getEyesSeparation()
	{
		return eyesSeparation;
	}
	
	/**
	 * Method setEyesSeparation.
	 * @param eyesSeparation int
	 */
	public final void setEyesSeparation(int eyesSeparation)
	{
		this.eyesSeparation = eyesSeparation;
	}
	
	/**
	 * Method getEyesAngle.
	 * @return int
	 */
	public final int getEyesAngle()
	{
		return eyesAngle;
	}
	
	/**
	 * Method setEyesAngle.
	 * @param eyesAngle int
	 */
	public final void setEyesAngle(int eyesAngle)
	{
		this.eyesAngle = eyesAngle;
	}
	
	/**
	 * Method getEyesInnerBrow.
	 * @return int
	 */
	public final int getEyesInnerBrow()
	{
		return eyesInnerBrow;
	}
	
	/**
	 * Method setEyesInnerBrow.
	 * @param eyesInnerBrow int
	 */
	public final void setEyesInnerBrow(int eyesInnerBrow)
	{
		this.eyesInnerBrow = eyesInnerBrow;
	}
	
	/**
	 * Method getEyesOuterBrow.
	 * @return int
	 */
	public final int getEyesOuterBrow()
	{
		return eyesOuterBrow;
	}
	
	/**
	 * Method setEyesOuterBrow.
	 * @param eyesOuterBrow int
	 */
	public final void setEyesOuterBrow(int eyesOuterBrow)
	{
		this.eyesOuterBrow = eyesOuterBrow;
	}
	
	/**
	 * Method getNoseExtension.
	 * @return int
	 */
	public final int getNoseExtension()
	{
		return noseExtension;
	}
	
	/**
	 * Method setNoseExtension.
	 * @param noseExtension int
	 */
	public final void setNoseExtension(int noseExtension)
	{
		this.noseExtension = noseExtension;
	}
	
	/**
	 * Method getNoseSize.
	 * @return int
	 */
	public final int getNoseSize()
	{
		return noseSize;
	}
	
	/**
	 * Method setNoseSize.
	 * @param noseSize int
	 */
	public final void setNoseSize(int noseSize)
	{
		this.noseSize = noseSize;
	}
	
	/**
	 * Method getNoseBridge.
	 * @return int
	 */
	public final int getNoseBridge()
	{
		return noseBridge;
	}
	
	/**
	 * Method setNoseBridge.
	 * @param noseBridge int
	 */
	public final void setNoseBridge(int noseBridge)
	{
		this.noseBridge = noseBridge;
	}
	
	/**
	 * Method getNoseNostrilWidth.
	 * @return int
	 */
	public final int getNoseNostrilWidth()
	{
		return noseNostrilWidth;
	}
	
	/**
	 * Method setNoseNostrilWidth.
	 * @param noseNostrilWidth int
	 */
	public final void setNoseNostrilWidth(int noseNostrilWidth)
	{
		this.noseNostrilWidth = noseNostrilWidth;
	}
	
	/**
	 * Method getNoseTipWidth.
	 * @return int
	 */
	public final int getNoseTipWidth()
	{
		return noseTipWidth;
	}
	
	/**
	 * Method setNoseTipWidth.
	 * @param noseTipWidth int
	 */
	public final void setNoseTipWidth(int noseTipWidth)
	{
		this.noseTipWidth = noseTipWidth;
	}
	
	/**
	 * Method getNoseTip.
	 * @return int
	 */
	public final int getNoseTip()
	{
		return noseTip;
	}
	
	/**
	 * Method setNoseTip.
	 * @param noseTip int
	 */
	public final void setNoseTip(int noseTip)
	{
		this.noseTip = noseTip;
	}
	
	/**
	 * Method getNoseNostrilFlare.
	 * @return int
	 */
	public final int getNoseNostrilFlare()
	{
		return noseNostrilFlare;
	}
	
	/**
	 * Method setNoseNostrilFlare.
	 * @param noseNostrilFlare int
	 */
	public final void setNoseNostrilFlare(int noseNostrilFlare)
	{
		this.noseNostrilFlare = noseNostrilFlare;
	}
	
	/**
	 * Method getMouthPucker.
	 * @return int
	 */
	public final int getMouthPucker()
	{
		return mouthPucker;
	}
	
	/**
	 * Method setMouthPucker.
	 * @param mouthPucker int
	 */
	public final void setMouthPucker(int mouthPucker)
	{
		this.mouthPucker = mouthPucker;
	}
	
	/**
	 * Method getMouthPosition.
	 * @return int
	 */
	public final int getMouthPosition()
	{
		return mouthPosition;
	}
	
	/**
	 * Method setMouthPosition.
	 * @param mouthPosition int
	 */
	public final void setMouthPosition(int mouthPosition)
	{
		this.mouthPosition = mouthPosition;
	}
	
	/**
	 * Method getMouthWidth.
	 * @return int
	 */
	public final int getMouthWidth()
	{
		return mouthWidth;
	}
	
	/**
	 * Method setMouthWidth.
	 * @param mouthWidth int
	 */
	public final void setMouthWidth(int mouthWidth)
	{
		this.mouthWidth = mouthWidth;
	}
	
	/**
	 * Method getMouthLipThickness.
	 * @return int
	 */
	public final int getMouthLipThickness()
	{
		return mouthLipThickness;
	}
	
	/**
	 * Method setMouthLipThickness.
	 * @param mouthLipThickness int
	 */
	public final void setMouthLipThickness(int mouthLipThickness)
	{
		this.mouthLipThickness = mouthLipThickness;
	}
	
	/**
	 * Method getMouthCorners.
	 * @return int
	 */
	public final int getMouthCorners()
	{
		return mouthCorners;
	}
	
	/**
	 * Method setMouthCorners.
	 * @param mouthCorners int
	 */
	public final void setMouthCorners(int mouthCorners)
	{
		this.mouthCorners = mouthCorners;
	}
	
	/**
	 * Method getEyesShape.
	 * @return int
	 */
	public final int getEyesShape()
	{
		return eyesShape;
	}
	
	/**
	 * Method setEyesShape.
	 * @param eyesShape int
	 */
	public final void setEyesShape(int eyesShape)
	{
		this.eyesShape = eyesShape;
	}
	
	/**
	 * Method getNoseBend.
	 * @return int
	 */
	public final int getNoseBend()
	{
		return noseBend;
	}
	
	/**
	 * Method setNoseBend.
	 * @param noseBend int
	 */
	public final void setNoseBend(int noseBend)
	{
		this.noseBend = noseBend;
	}
	
	/**
	 * Method getBoneStructureJawWidth.
	 * @return int
	 */
	public final int getBoneStructureJawWidth()
	{
		return boneStructureJawWidth;
	}
	
	/**
	 * Method setBoneStructureJawWidth.
	 * @param boneStructureJawWidth int
	 */
	public final void setBoneStructureJawWidth(int boneStructureJawWidth)
	{
		this.boneStructureJawWidth = boneStructureJawWidth;
	}
	
	/**
	 * Method getMothGape.
	 * @return int
	 */
	public final int getMothGape()
	{
		return mothGape;
	}
	
	/**
	 * Method setMothGape.
	 * @param mothGape int
	 */
	public final void setMothGape(int mothGape)
	{
		this.mothGape = mothGape;
	}
	
	/**
	 * Method finalyze.
	 * @see rlib.util.pools.Foldable#finalyze()
	 */
	@Override
	public void finalyze()
	{
	}
	
	/**
	 * Method reinit.
	 * @see rlib.util.pools.Foldable#reinit()
	 */
	@Override
	public void reinit()
	{
	}
	
	public void fold()
	{
		pool.put(this);
	}
	
	/**
	 * Method toXML.
	 * @param appearance PlayerAppearance
	 * @param id String
	 * @return String
	 */
	public static String toXML(PlayerAppearance appearance, String id)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("<appearance id=\"").append(id).append("\" >\n");
		final Array<Field> fields = ReflectionUtils.getAllFields(appearance.getClass(), Object.class, true, "pool", "objectId");
		
		try
		{
			for (Field field : fields)
			{
				final String name = field.getName();
				final boolean old = field.isAccessible();
				field.setAccessible(true);
				final String value = String.valueOf(field.get(appearance));
				builder.append("	<set name=\"").append(name).append("\" value=\"").append(value).append("\" />").append("\n");
				field.setAccessible(old);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			Loggers.warning(appearance.getClass(), e);
		}
		
		builder.append("</appearance>");
		return builder.toString();
	}
	
	/**
	 * Method fromXML.
	 * @param <T>
	 * @param appearance T
	 * @param vars VarTable
	 * @return T
	 */
	public static <T extends PlayerAppearance> T fromXML(T appearance, VarTable vars)
	{
		final Array<Field> fields = ReflectionUtils.getAllFields(appearance.getClass(), Object.class, true, "pool", "objectId");
		
		try
		{
			for (Field field : fields)
			{
				final boolean old = field.isAccessible();
				field.setAccessible(true);
				field.setInt(appearance, vars.getInteger(field.getName(), field.getInt(appearance)));
				field.setAccessible(old);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			Loggers.warning(appearance.getClass(), e);
		}
		
		return appearance;
	}
	
	/**
	 * Method copy.
	 * @return PlayerAppearance
	 */
	public PlayerAppearance copy()
	{
		try
		{
			return (PlayerAppearance) clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}