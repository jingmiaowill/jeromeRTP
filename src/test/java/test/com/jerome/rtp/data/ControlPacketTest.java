package test.com.jerome.rtp.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.memory.BuffersBuffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Test;

import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;
import com.jerome.rtp.protocol.utils.ByteUtils;

public class ControlPacketTest {

	@Test
    public void testDecodeCompoundPacket() throws Exception {
        // wireshark capture, 3 packets (SR, SDES, BYE), from X-lite
        byte[] firstPacketBytes = ByteUtils
                .convertHexStringToByteArray("80c80006e6aa996ed01f8460ea7ef9db001eb9b4000006e30004a084");
        byte[] secondPacketBytes = ByteUtils
                .convertHexStringToByteArray("81ca001ee6aa996e013d383232433634303536464438344539414231324438333442463" +
                                             "836303931354140756e697175652e7a333644423331373042303744344333302e6f7267" +
                                             "083110782d7274702d73657373696f6e2d6964363539413238344341443842344436313" +
                                             "83641324643304336383039363137300000");
        byte[] thirdPacketBytes = ByteUtils
                .convertHexStringToByteArray("81cb0001e6aa996e");

        ByteBufferManager mm = new ByteBufferManager();
        BuffersBuffer buffers = BuffersBuffer.create(mm);
        buffers.append(HeapBuffer.wrap(firstPacketBytes));
        buffers.append(HeapBuffer.wrap(secondPacketBytes));
        buffers.append(HeapBuffer.wrap(thirdPacketBytes));

        List<ControlPacket> controlPackets = new ArrayList<ControlPacket>(3);
        while (BufferUtils.readableBytes(buffers) > 0) {
        	System.out.println(BufferUtils.readableBytes(buffers));
            controlPackets.add(ControlPacket.decode(buffers));
        }

        assertEquals(3, controlPackets.size());

        assertEquals(ControlPacket.Type.SENDER_REPORT, controlPackets.get(0).getType());
        assertEquals(ControlPacket.Type.SOURCE_DESCRIPTION, controlPackets.get(1).getType());
        assertEquals(ControlPacket.Type.BYE, controlPackets.get(2).getType());

        // No more tests needed as there is plenty of unit testing for each of those packets individually.
    }
//    @Test
//    public void test2(){
//    	byte[] test = ByteUtils.convertHexStringToByteArray("80c900011acc413e81cc00031acc413e454e42520002524c");
//    	Buffer buffer = HeapBuffer.wrap(test);
//    	List<ControlPacket> controlPackets = new ArrayList<ControlPacket>(3);
//    	while (BufferUtils.readableBytes(buffer) > 0) {
//			try {
//				controlPackets.add(SimpleControlPacket.decode(buffer));
//			} catch (IllegalArgumentException e) {
//				break;
//			}
//		}
//       
//        CompoundControlPacket ccp= new CompoundControlPacket(buffer, controlPackets);
//    }
}