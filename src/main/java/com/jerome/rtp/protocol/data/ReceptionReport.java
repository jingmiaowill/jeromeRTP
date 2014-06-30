package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

public class ReceptionReport {
	
	private long ssrc;
	private short fractionLost;
	private int cumulativeNumberOfPacketsLost;
	private long extendedHighestSequenceNumberReceived;
	private long interArrivalJitter;
	private long lastSenderReport;
	private long delaySinceLastSenderReport;

	public ReceptionReport() {
	}

	public static Buffer encode(ReceptionReport block, MemoryManager<?> obtainMemory) {
		Buffer buffer = obtainMemory.allocate(24); // 4 + 1 + 3 + 4 + 4 + 4 + 4
		buffer.putInt((int) block.ssrc);
		buffer.put((byte)block.fractionLost);
		//buffer.putShort((short) block.cumulativeNumberOfPacketsLost);
		Object[] medium = BufferUtils.toMedium(block.cumulativeNumberOfPacketsLost);
		buffer.put((Byte)medium[0]);
		buffer.putShort((Short)medium[1]);
		
		buffer.putInt((int) block.extendedHighestSequenceNumberReceived);
		buffer.putInt((int) block.interArrivalJitter);
		buffer.putInt((int) block.lastSenderReport);
		buffer.putInt((int) block.delaySinceLastSenderReport);
		buffer.position(0);
		return buffer;
	}

	public static ReceptionReport decode(Buffer buffer) {
		ReceptionReport block = new ReceptionReport();
		block.setSsrc(BufferUtils.getOneUnsignedInt(buffer));
		block.setFractionLost(BufferUtils.getOneUnsignedByte(buffer));
		block.setCumulativeNumberOfPacketsLost(BufferUtils.getMedium(buffer));
		block.setExtendedHighestSequenceNumberReceived(BufferUtils.getOneUnsignedInt(buffer));
		block.setInterArrivalJitter(BufferUtils.getOneUnsignedInt(buffer));
		block.setLastSenderReport(BufferUtils.getOneUnsignedInt(buffer));
		block.setDelaySinceLastSenderReport(BufferUtils.getOneUnsignedInt(buffer));
		return block;
	}

	public Buffer encode(MemoryManager<?> obtainMemory) {
		return encode(this, obtainMemory);
	}

	public long getSsrc() {
		return ssrc;
	}

	public void setSsrc(long ssrc) {
		if ((ssrc < 0) || (ssrc > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for SSRC is [0;0xffffffff]");
		}
		this.ssrc = ssrc;
	}

	public short getFractionLost() {
		return fractionLost;
	}

	public void setFractionLost(short fractionLost) {
		if ((fractionLost < 0) || (fractionLost > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for Fraction Lost is [0;0xff]");
		}
		this.fractionLost = fractionLost;
	}

	public int getCumulativeNumberOfPacketsLost() {
		return cumulativeNumberOfPacketsLost;
	}

	public void setCumulativeNumberOfPacketsLost(int cumulativeNumberOfPacketsLost) {
		if ((cumulativeNumberOfPacketsLost < 0) || (cumulativeNumberOfPacketsLost > 0x00ffffff)) {
			throw new IllegalArgumentException("Valid range for Cumulative Number of Packets Lost is [0;0x00ffffff]");
		}
		this.cumulativeNumberOfPacketsLost = cumulativeNumberOfPacketsLost;
	}

	public long getExtendedHighestSequenceNumberReceived() {
		return extendedHighestSequenceNumberReceived;
	}

	public void setExtendedHighestSequenceNumberReceived(long extendedHighestSequenceNumberReceived) {
		if ((extendedHighestSequenceNumberReceived < 0) || (extendedHighestSequenceNumberReceived > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for Extended Highest SeqNumber Received is [0;0xffffffff]");
		}
		this.extendedHighestSequenceNumberReceived = extendedHighestSequenceNumberReceived;
	}

	public long getInterArrivalJitter() {
		return interArrivalJitter;
	}

	public void setInterArrivalJitter(long interArrivalJitter) {
		if ((interArrivalJitter < 0) || (interArrivalJitter > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for Interarrival Jitter is [0;0xffffffff]");
		}
		this.interArrivalJitter = interArrivalJitter;
	}

	public long getLastSenderReport() {
		return lastSenderReport;
	}

	public void setLastSenderReport(long lastSenderReport) {
		if ((lastSenderReport < 0) || (lastSenderReport > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for Last Sender Report is [0;0xffffffff]");
		}
		this.lastSenderReport = lastSenderReport;
	}

	public long getDelaySinceLastSenderReport() {
		return delaySinceLastSenderReport;
	}

	public void setDelaySinceLastSenderReport(long delaySinceLastSenderReport) {
		if ((delaySinceLastSenderReport < 0) || (delaySinceLastSenderReport > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for Delay Since Last Sender Report is [0;0xffffffff]");
		}
		this.delaySinceLastSenderReport = delaySinceLastSenderReport;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("ReceptionReport{").append("ssrc=").append(this.ssrc)
				.append(", fractionLost=").append(this.fractionLost).append(", cumulativeNumberOfPacketsLost=")
				.append(this.cumulativeNumberOfPacketsLost).append(", extendedHighestSequenceNumberReceived=")
				.append(this.extendedHighestSequenceNumberReceived).append(", interArrivalJitter=")
				.append(this.interArrivalJitter).append(", lastSenderReport=").append(this.lastSenderReport)
				.append(", delaySinceLastSenderReport=").append(this.delaySinceLastSenderReport).append('}').toString();
	}
}
