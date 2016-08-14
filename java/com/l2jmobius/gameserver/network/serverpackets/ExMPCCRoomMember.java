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
package com.l2jmobius.gameserver.network.serverpackets;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.enums.MatchingMemberType;
import com.l2jmobius.gameserver.instancemanager.MapRegionManager;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.matching.CommandChannelMatchingRoom;
import com.l2jmobius.gameserver.network.client.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExMPCCRoomMember implements IClientOutgoingPacket
{
	private final CommandChannelMatchingRoom _room;
	private final MatchingMemberType _type;
	
	public ExMPCCRoomMember(L2PcInstance player, CommandChannelMatchingRoom room)
	{
		_room = room;
		_type = room.getMemberType(player);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MPCC_ROOM_MEMBER.writeId(packet);
		
		packet.writeD(_type.ordinal());
		packet.writeD(_room.getMembersCount());
		for (L2PcInstance member : _room.getMembers())
		{
			packet.writeD(member.getObjectId());
			packet.writeS(member.getName());
			packet.writeD(member.getLevel());
			packet.writeD(member.getClassId().getId());
			packet.writeD(MapRegionManager.getInstance().getBBs(member.getLocation()));
			packet.writeD(_room.getMemberType(member).ordinal());
		}
		return true;
	}
}
