/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.ShotType;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.effects.L2EffectType;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.skills.AbnormalType;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Formulas;
import com.l2jmobius.gameserver.model.stats.Stats;

/**
 * Fatal Blow effect implementation.
 * @author Adry_85
 */
public final class FatalBlow extends AbstractEffect
{
	private final double _power;
	private final double _chance;
	private final double _criticalChance;
	private final boolean _overHit;
	
	private final Set<AbnormalType> _abnormals;
	private final double _abnormalPower;
	
	public FatalBlow(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		_chance = params.getDouble("chance", 0);
		_criticalChance = params.getDouble("criticalChance", 0);
		_overHit = params.getBoolean("overHit", false);
		
		final String abnormals = params.getString("abnormalType", null);
		if ((abnormals != null) && !abnormals.isEmpty())
		{
			_abnormals = new HashSet<>();
			for (String slot : abnormals.split(";"))
			{
				_abnormals.add(AbnormalType.getAbnormalType(slot));
			}
		}
		else
		{
			_abnormals = Collections.<AbnormalType> emptySet();
		}
		_abnormalPower = params.getDouble("abnormalPower", 1);
	}
	
	@Override
	public boolean calcSuccess(L2Character effector, L2Character effected, Skill skill)
	{
		return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill) && Formulas.calcBlowSuccess(effector, effected, skill, _chance);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (effector.isAlikeDead())
		{
			return;
		}
		
		if (_overHit && effected.isAttackable())
		{
			((L2Attackable) effected).overhitEnabled(true);
		}
		
		double power = _power;
		
		// Check if we apply an abnormal modifier
		if (_abnormals.stream().anyMatch(effected::hasAbnormalType))
		{
			power += _abnormalPower;
		}
		
		final boolean ss = skill.useSoulShot() && effector.isChargedShot(ShotType.SOULSHOTS);
		final byte shld = Formulas.calcShldUse(effector, effected);
		double damage = Formulas.calcBlowDamage(effector, effected, skill, false, power, shld, ss);
		final boolean crit = Formulas.calcCrit(_criticalChance, effector, effected, skill);
		
		if (crit)
		{
			damage *= 2;
		}
		
		// Check if damage should be reflected
		Formulas.calcDamageReflected(effector, effected, skill, true);
		
		final double damageCap = effected.getStat().getValue(Stats.DAMAGE_LIMIT);
		if (damageCap > 0)
		{
			damage = Math.min(damage, damageCap);
		}
		
		effected.reduceCurrentHp(damage, effector, skill, false, false, true, false);
		
		// Manage attack or cast break of the target (calculating rate, sending message...)
		if (!effected.isRaid() && Formulas.calcAtkBreak(effected, damage))
		{
			effected.breakAttack();
			effected.breakCast();
		}
		
		effector.sendDamageMessage(effected, skill, (int) damage, true, false);
	}
}