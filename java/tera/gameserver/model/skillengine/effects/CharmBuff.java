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
package tera.gameserver.model.skillengine.effects;

import tera.gameserver.model.Character;
import tera.gameserver.model.EffectList;
import tera.gameserver.model.Party;
import tera.gameserver.model.skillengine.EffectState;
import tera.gameserver.network.serverpackets.AppledCharmEffect;
import tera.gameserver.network.serverpackets.CancelCharmEffect;
import tera.gameserver.taskmanager.EffectTaskManager;
import tera.gameserver.templates.EffectTemplate;
import tera.gameserver.templates.SkillTemplate;

/**
 * @author Ronn
 */
public class CharmBuff extends AbstractEffect
{
	/**
	 * Constructor for CharmBuff.
	 * @param template EffectTemplate
	 * @param effector Character
	 * @param effected Character
	 * @param skill SkillTemplate
	 */
	public CharmBuff(EffectTemplate template, Character effector, Character effected, SkillTemplate skill)
	{
		super(template, effector, effected, skill);
	}
	
	/**
	 * Method scheduleEffect.
	 * @see tera.gameserver.model.skillengine.Effect#scheduleEffect()
	 */
	@Override
	public void scheduleEffect()
	{
		final Character effected = getEffected();
		final Character effector = getEffector();
		
		if (effected == null)
		{
			LOGGER.warning(this, new Exception("not found effected"));
			return;
		}
		
		if (effector == null)
		{
			LOGGER.warning(this, new Exception("not found effector"));
			return;
		}
		
		final EffectList effectList = getEffectList();
		
		if (effectList == null)
		{
			LOGGER.warning(this, new Exception("not found effect list."));
			return;
		}
		
		effectList.lock();
		
		try
		{
			switch (getState())
			{
				case CREATED:
				{
					onStart();
					setState(EffectState.ACTING);
					effected.broadcastPacket(AppledCharmEffect.getInstance(effected, this));
					final Party party = effected.getParty();
					
					if (party != null)
					{
						party.updateEffects(effected.getPlayer());
					}
					
					final EffectTaskManager effectManager = EffectTaskManager.getInstance();
					effectManager.addTask(this, period);
					break;
				}
				
				case ACTING:
				{
					if (count > 0)
					{
						count--;
						
						if (onActionTime() && (count > 0))
						{
							break;
						}
					}
					
					setState(EffectState.FINISHING);
					break;
				}
				
				case FINISHING:
				{
					setState(EffectState.FINISHED);
					setInUse(false);
					onExit();
					effected.removeEffect(this);
					effected.sendPacket(CancelCharmEffect.getInstance(getEffectId()), true);
					final Party party = effected.getParty();
					
					if (party != null)
					{
						party.updateEffects(effected.getPlayer());
					}
					
					break;
				}
				
				default:
					LOGGER.warning(this, new Exception("incorrect effect state " + state));
			}
		}
		
		finally
		{
			effectList.unlock();
		}
	}
}
