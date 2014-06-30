package com.jerome.rtp.protocol.data;

import java.util.Arrays;
import java.util.List;

import org.glassfish.grizzly.Buffer;
/**
 * 
 * @author Will.jingmiao
 *
 */
public class CompoundControlPacket {
	

    private final List<ControlPacket> controlPackets;
    private Buffer packet;

    public CompoundControlPacket(Buffer packet,ControlPacket... controlPackets) {
        if (controlPackets.length == 0) {
            throw new IllegalArgumentException("At least one RTCP packet must be provided");
        }
        this.controlPackets = Arrays.asList(controlPackets);
        this.packet=packet;
    }

    public CompoundControlPacket(Buffer packet,List<ControlPacket> controlPackets) {
        if ((controlPackets == null) || controlPackets.isEmpty()) {
            throw new IllegalArgumentException("ControlPacket list cannot be null or empty");
        }
        this.controlPackets = controlPackets;
        this.packet=packet;
    }

    public int getPacketCount() {
        return this.controlPackets.size();
    }

    public List<ControlPacket> getControlPackets() {
        return this.controlPackets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CompoundControlPacket{\n");
        for (ControlPacket packet : this.controlPackets) {
            builder.append("  ").append(packet.toString()).append('\n');
        }
        return builder.append('}').toString();
    }

	public Buffer getPacket() {
		return packet;
	}

}