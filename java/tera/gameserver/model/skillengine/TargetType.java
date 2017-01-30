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
package tera.gameserver.model.skillengine;

import tera.Config;
import tera.gameserver.manager.GeoManager;
import tera.gameserver.model.Character;
import tera.gameserver.model.World;
import tera.gameserver.model.skillengine.targethandler.AreOneTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AreaFractionTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AreaOwnerTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AreaPartyGuildTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AreaPartyTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AreaTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AuraFractionTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AuraOwnerTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AuraPartyTargetHandler;
import tera.gameserver.model.skillengine.targethandler.AuraTargetHandler;
import tera.gameserver.model.skillengine.targethandler.NoneTargetHandler;
import tera.gameserver.model.skillengine.targethandler.SelfTargetHandler;
import tera.gameserver.model.skillengine.targethandler.TargetHandler;
import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.util.LocalObjects;
import tera.util.Location;

import rlib.geom.Angles;
import rlib.geom.Coords;
import rlib.logging.Loggers;
import rlib.util.array.Array;

/**
 * @author Ronn
 */
public enum TargetType
{
	
	TARGET_NONE(new NoneTargetHandler()),
	
	TARGET_ONE(new NoneTargetHandler()),
	
	TARGET_FRACTION_AREA(new AreaFractionTargetHandler()),
	
	TARGET_FRACTION_AURA(new AuraFractionTargetHandler()),
	
	TARGET_ONE_AREA(new AreOneTargetHandler()),
	
	TARGET_SELF(new SelfTargetHandler()),
	
	TARGET_AREA(new AreaTargetHandler()),
	
	TARGET_AURA(new AuraTargetHandler()),
	
	TARGET_FRONT,
	
	TARGET_DEGREE_AURA,
	
	TARGET_RAIL,
	
	TARGET_BACK_RAIL,
	
	TARGET_PARTY(new AuraPartyTargetHandler()),
	
	TARGET_OWNER_AURA(new AuraOwnerTargetHandler()),
	
	TARGET_OWNER_AREA(new AreaOwnerTargetHandler()),
	
	LOCK_ON_WAR,
	
	LOCK_ON_FRIEND,
	
	TRAP_AURA,
	
	TARGET_AREA_PARTY(new AreaPartyTargetHandler()),
	
	TARGET_AREA_PARTY_GUILD(new AreaPartyGuildTargetHandler());
	
	private final TargetHandler handler;
	
	private TargetType()
	{
		handler = null;
	}
	
	/**
	 * Constructor for TargetType.
	 * @param handler TargetHandler
	 */
	private TargetType(TargetHandler handler)
	{
		this.handler = handler;
	}
	
	/**
	 * Method check.
	 * @param caster Character
	 * @param target Character
	 * @return boolean
	 */
	public boolean check(Character caster, Character target)
	{
		switch (this)
		{
			case LOCK_ON_FRIEND:
				return !caster.checkTarget(target);
			
			case LOCK_ON_WAR:
				return caster.checkTarget(target);
			
			default:
			{
				Loggers.warning(this, "incorrect check target type " + this);
				return false;
			}
		}
	}
	
	/**
	 * Method getTargets.
	 * @param targets Array<Character>
	 * @param skill Skill
	 * @param targetX float
	 * @param targetY float
	 * @param targetZ float
	 * @param attacker Character
	 */
	public void getTargets(Array<Character> targets, Skill skill, float targetX, float targetY, float targetZ, Character attacker)
	{
		skill.setImpactX(attacker.getX());
		skill.setImpactY(attacker.getY());
		skill.setImpactZ(attacker.getZ());
		
		if (handler != null)
		{
			handler.addTargetsTo(targets, attacker, skill, targetX, targetY, targetZ);
		}
		else
		{
			final ItemTable itemTable = ItemTable.getInstance();
			final GeoManager geoManager = GeoManager.getInstance();
			
			switch (this)
			{
				case LOCK_ON_FRIEND:
				{
					final Array<Character> prepare = attacker.getLockOnTargets();
					skill.setImpactX(attacker.getX());
					skill.setImpactY(attacker.getY());
					skill.setImpactZ(attacker.getZ());
					prepare.readLock();
					
					try
					{
						final Character[] array = prepare.array();
						
						for (int i = 0, length = prepare.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (!attacker.checkTarget(target))
							{
								targets.add(target);
							}
						}
					}
					
					finally
					{
						prepare.readUnlock();
					}
					prepare.clear();
					break;
				}
				
				case LOCK_ON_WAR:
				{
					final Array<Character> prepare = attacker.getLockOnTargets();
					skill.setImpactX(attacker.getX());
					skill.setImpactY(attacker.getY());
					skill.setImpactZ(attacker.getZ());
					prepare.readLock();
					
					try
					{
						final Character[] array = prepare.array();
						
						for (int i = 0, length = prepare.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (attacker.checkTarget(target) && attacker.isInRange(target, skill.getRange()))
							{
								targets.add(target);
							}
						}
					}
					
					finally
					{
						prepare.readUnlock();
					}
					prepare.clear();
					break;
				}
				
				case TARGET_DEGREE_AURA:
				{
					final int degree = skill.getDegree();
					final int width = skill.getWidth();
					int min = degree - width;
					
					if (min < 0)
					{
						min += 360;
					}
					
					int max = degree + width;
					
					if (max < 0)
					{
						max += 360;
					}
					
					final float radius = skill.getRadius();
					World.getAround(Character.class, targets, attacker, radius);
					
					if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
					{
						final Location[] locs = Coords.arcCoords(Location.class, attacker, (int) radius, 10, degree, width);
						final ItemTemplate template = itemTable.getItem(8007);
						
						for (int i = 0; i < 10; i++)
						{
							final Location loc = locs[i];
							loc.setContinentId(attacker.getContinentId());
							template.newInstance().spawnMe(loc);
						}
					}
					
					final float x = attacker.getX();
					final float y = attacker.getY();
					final float z = attacker.getZ();
					
					if (!targets.isEmpty())
					{
						final Character[] array = targets.array();
						
						for (int i = 0, length = targets.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (!attacker.checkTarget(target))
							{
								targets.fastRemove(i--);
								length--;
								continue;
							}
							
							if (!target.isHit(x, y, z, 100, radius))
							{
								targets.fastRemove(i--);
								length--;
								continue;
							}
							
							if (!attacker.isInDegree(target, degree, width))
							{
								targets.fastRemove(i--);
								length--;
							}
						}
					}
					
					break;
				}
				
				case TARGET_RAIL:
				{
					final float radius = skill.getRadius();
					final float range = skill.getRange();
					targets = World.getAround(Character.class, targets, attacker, range);
					final float x = attacker.getX();
					final float y = attacker.getY();
					final float z = attacker.getZ();
					final float radians = Angles.headingToRadians(attacker.getHeading() + skill.getHeading());
					targetX = Coords.calcX(x, skill.getRange(), radians);
					targetY = Coords.calcY(y, skill.getRange(), radians);
					targetZ = geoManager.getHeight(attacker.getContinentId(), targetX, targetY, z);
					
					if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
					{
						final Location[] locs = Coords.circularCoords(Location.class, targetX, targetY, z, (int) radius, 10);
						final ItemTemplate template = itemTable.getItem(8007);
						
						for (int i = 0; i < 10; i++)
						{
							final Location loc = locs[i];
							loc.setContinentId(attacker.getContinentId());
							template.newInstance().spawnMe(loc);
						}
						
						template.newInstance().spawnMe(attacker.getLoc());
						template.newInstance().spawnMe(new Location(targetX, targetY, z, 0, attacker.getContinentId()));
					}
					
					if (!targets.isEmpty())
					{
						final Character[] array = targets.array();
						
						for (int i = 0, length = targets.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (!attacker.checkTarget(target))
							{
								targets.fastRemove(i--);
								length--;
								continue;
							}
							
							if (!target.isHit(x, y, z, targetX, targetY, targetZ, radius, false))
							{
								targets.fastRemove(i--);
								length--;
							}
						}
					}
					
					break;
				}
				
				case TARGET_BACK_RAIL:
				{
					final float radius = skill.getRadius();
					final float range = skill.getRange();
					targets = World.getAround(Character.class, targets, attacker, range);
					final float x = attacker.getX();
					final float y = attacker.getY();
					final float z = attacker.getZ();
					final float radians = Angles.degreeToRadians(Angles.headingToDegree(attacker.getHeading() + skill.getHeading()) - 180);
					targetX = Coords.calcX(x, skill.getRange(), radians);
					targetY = Coords.calcY(y, skill.getRange(), radians);
					targetZ = geoManager.getHeight(attacker.getContinentId(), targetX, targetY, z);
					
					if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
					{
						final Location[] locs = Coords.circularCoords(Location.class, targetX, targetY, z, (int) radius, 10);
						final ItemTemplate template = itemTable.getItem(8007);
						
						for (int i = 0; i < 10; i++)
						{
							final Location loc = locs[i];
							loc.setContinentId(attacker.getContinentId());
							template.newInstance().spawnMe(loc);
						}
						
						template.newInstance().spawnMe(attacker.getLoc());
						template.newInstance().spawnMe(new Location(targetX, targetY, z, 0, attacker.getContinentId()));
					}
					
					if (!targets.isEmpty())
					{
						final Character[] array = targets.array();
						
						for (int i = 0, length = targets.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (!attacker.checkTarget(target))
							{
								targets.fastRemove(i--);
								length--;
								continue;
							}
							
							if (!target.isHit(x, y, z, targetX, targetY, targetZ, radius, false))
							{
								targets.fastRemove(i--);
								length--;
							}
						}
					}
					
					break;
				}
				
				case TRAP_AURA:
				{
					final float radius = skill.getRadius();
					skill.setImpactX(targetX);
					skill.setImpactY(targetY);
					skill.setImpactZ(targetZ);
					
					if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
					{
						final Location[] locs = Coords.circularCoords(Location.class, targetX, targetY, targetZ, (int) radius, 10);
						final ItemTemplate template = itemTable.getItem(8007);
						
						for (int i = 0; i < 10; i++)
						{
							final Location loc = locs[i];
							loc.setContinentId(attacker.getContinentId());
							template.newInstance().spawnMe(loc);
						}
						
						template.newInstance().spawnMe(new Location(targetX, targetY, targetZ));
					}
					
					targets = World.getAround(Character.class, targets, attacker.getContinentId(), targetX, targetY, attacker.getZ(), attacker.getObjectId(), attacker.getSubId(), radius);
					
					if (!targets.isEmpty())
					{
						final Character[] array = targets.array();
						
						for (int i = 0, length = targets.size(); i < length; i++)
						{
							final Character target = array[i];
							
							if (!attacker.checkTarget(target))
							{
								targets.fastRemove(i--);
								length--;
								continue;
							}
							
							if (!target.isHit(targetX, targetY, targetZ, 100, radius))
							{
								targets.fastRemove(i--);
								length--;
							}
						}
					}
					
					break;
				}
				
				default:
					break;
			}
		}
		
		if (skill.isNoCaster())
		{
			targets.fastRemove(attacker);
		}
		
		if (!skill.isShieldIgnore() && (targets.size() > 1))
		{
			final LocalObjects local = LocalObjects.get();
			final Array<Character> removed = local.getNextCharList();
			Character[] array = targets.array();
			final float x = skill.getImpactX();
			final float y = skill.getImpactY();
			final float z = skill.getImpactZ();
			float radius = 0F;
			int angle = 0;
			int degree = 0;
			final ItemTable itemTable = ItemTable.getInstance();
			final Formulas formulas = Formulas.getInstance();
			
			for (int i = 0, length = targets.size(); i < length; i++)
			{
				final Character target = array[i];
				
				if (target.isDefenseStance() && formulas.calcDamageSkill(local.getNextAttackInfo(), skill, attacker, target).isBlocked() && !removed.contains(target))
				{
					final float distance = target.getGeomDistance(x, y);
					final float width = target.getGeomRadius();
					final float half = width / 2F;
					final float hip = (float) Math.sqrt((distance * distance) + (half * half));
					final float square = half * (float) Math.sqrt((hip + half) * (hip - half));
					radius = (hip * hip * width) / (4F * square);
					angle = (int) Math.toDegrees(Math.asin(width / (2 * radius))) / 2;
					degree = Angles.calcHeading(x, y, target.getX(), target.getY());
					
					if (Config.DEVELOPER_DEBUG_TARGET_TYPE)
					{
						final Location[] locs = Coords.arcCoords(Location.class, x, y, z, Angles.calcHeading(x, y, target.getX(), target.getY()), skill.getRadius(), 10, 0, angle * 2);
						final ItemTemplate template = itemTable.getItem(10001);
						
						for (int g = 0; g < 10; i++)
						{
							final Location loc = locs[g];
							loc.setContinentId(attacker.getContinentId());
							template.newInstance().spawnMe(loc);
						}
					}
					
					radius = skill.getRadius();
					
					for (int j = 0; j < length; j++)
					{
						final Character covered = array[j];
						
						if ((covered == target) || removed.contains(covered) || (covered.getGeomDistance(x, y) < distance))
						{
							continue;
						}
						
						if (!Angles.isInDegree(x, y, degree, covered.getX(), covered.getY(), angle))
						{
							continue;
						}
						
						removed.add(covered);
					}
				}
			}
			
			if (!removed.isEmpty())
			{
				array = removed.array();
				
				for (int i = 0, length = removed.size(); i < length; i++)
				{
					targets.fastRemove(array[i]);
				}
			}
		}
		
		if (skill.getMaxTargets() > 0)
		{
			final int diff = targets.size() - skill.getMaxTargets();
			
			if (diff > 0)
			{
				for (int i = diff; i > 0; i--)
				{
					targets.pop();
				}
			}
		}
	}
}
