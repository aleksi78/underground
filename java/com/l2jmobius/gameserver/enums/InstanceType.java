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
package com.l2jmobius.gameserver.enums;

public enum InstanceType
{
	L2Object(null),
	L2ItemInstance(L2Object),
	L2Character(L2Object),
	L2Npc(L2Character),
	L2Playable(L2Character),
	L2Summon(L2Playable),
	L2Decoy(L2Character),
	L2PcInstance(L2Playable),
	L2NpcInstance(L2Npc),
	L2MerchantInstance(L2NpcInstance),
	L2WarehouseInstance(L2NpcInstance),
	L2StaticObjectInstance(L2Character),
	L2DoorInstance(L2Character),
	L2TerrainObjectInstance(L2Npc),
	L2EffectPointInstance(L2Npc),
	CommissionManagerInstance(L2Npc),
	// Summons, Pets, Decoys and Traps
	L2ServitorInstance(L2Summon),
	L2PetInstance(L2Summon),
	L2DecoyInstance(L2Decoy),
	L2TrapInstance(L2Npc),
	// Attackable
	L2Attackable(L2Npc),
	L2GuardInstance(L2Attackable),
	L2QuestGuardInstance(L2GuardInstance),
	L2MonsterInstance(L2Attackable),
	L2BlockInstance(L2Attackable),
	L2ChestInstance(L2MonsterInstance),
	L2ControllableMobInstance(L2MonsterInstance),
	L2FeedableBeastInstance(L2MonsterInstance),
	L2TamedBeastInstance(L2FeedableBeastInstance),
	L2FriendlyMobInstance(L2Attackable),
	L2RaidBossInstance(L2MonsterInstance),
	L2GrandBossInstance(L2RaidBossInstance),
	FriendlyNpcInstance(L2Attackable),
	// FlyMobs
	L2FlyTerrainObjectInstance(L2Npc),
	// Vehicles
	L2Vehicle(L2Character),
	L2BoatInstance(L2Vehicle),
	L2AirShipInstance(L2Vehicle),
	L2ShuttleInstance(L2Vehicle),
	L2ControllableAirShipInstance(L2AirShipInstance),
	// Siege
	L2DefenderInstance(L2Attackable),
	L2ArtefactInstance(L2NpcInstance),
	L2ControlTowerInstance(L2Npc),
	L2FlameTowerInstance(L2Npc),
	L2SiegeFlagInstance(L2Npc),
	// Fort Siege
	L2FortCommanderInstance(L2DefenderInstance),
	// Fort NPCs
	L2FortLogisticsInstance(L2MerchantInstance),
	L2FortManagerInstance(L2MerchantInstance),
	// City NPCs
	L2FishermanInstance(L2MerchantInstance),
	L2ObservationInstance(L2Npc),
	L2OlympiadManagerInstance(L2Npc),
	L2PetManagerInstance(L2MerchantInstance),
	L2RaceManagerInstance(L2Npc),
	L2TeleporterInstance(L2Npc),
	L2VillageMasterInstance(L2NpcInstance),
	// Doormens
	L2DoormenInstance(L2NpcInstance),
	L2FortDoormenInstance(L2DoormenInstance),
	// Custom
	L2ClassMasterInstance(L2NpcInstance),
	L2EventMobInstance(L2Npc);
	
	private final InstanceType _parent;
	private final long _typeL;
	private final long _typeH;
	private final long _maskL;
	private final long _maskH;
	
	private InstanceType(InstanceType parent)
	{
		_parent = parent;
		
		final int high = ordinal() - (Long.SIZE - 1);
		if (high < 0)
		{
			_typeL = 1L << ordinal();
			_typeH = 0;
		}
		else
		{
			_typeL = 0;
			_typeH = 1L << high;
		}
		
		if ((_typeL < 0) || (_typeH < 0))
		{
			throw new Error("Too many instance types, failed to load " + name());
		}
		
		if (parent != null)
		{
			_maskL = _typeL | parent._maskL;
			_maskH = _typeH | parent._maskH;
		}
		else
		{
			_maskL = _typeL;
			_maskH = _typeH;
		}
	}
	
	public final InstanceType getParent()
	{
		return _parent;
	}
	
	public final boolean isType(InstanceType it)
	{
		return ((_maskL & it._typeL) > 0) || ((_maskH & it._typeH) > 0);
	}
	
	public final boolean isTypes(InstanceType... it)
	{
		for (InstanceType i : it)
		{
			if (isType(i))
			{
				return true;
			}
		}
		return false;
	}
}
