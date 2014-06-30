package com.jerome.rtp.protocol.data;

import java.nio.charset.Charset;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;


public class SdesChunkPrivItem extends SdesChunkItem {


    private final String prefix;


    protected SdesChunkPrivItem(String prefix, String value) {
        super(SdesChunkItem.Type.PRIV, value);
        this.prefix = prefix;
    }


    @Override
    public Buffer encode(MemoryManager<?> obtainMemory) {
    	byte[] prefixBytes;
        if (this.prefix != null) {
            // RFC section 6.5 mandates that this must be UTF8
            // http://tools.ietf.org/html/rfc3550#section-6.5
            prefixBytes = this.prefix.getBytes(Charset.forName("UTF-8"));
        } else {
            prefixBytes = new byte[]{};
        }

        byte[] valueBytes;
        if (this.value != null) {
            // RFC section 6.5 mandates that this must be UTF8
            // http://tools.ietf.org/html/rfc3550#section-6.5
            valueBytes = this.value.getBytes(Charset.forName("UTF-8"));
        } else {
            valueBytes = new byte[]{};
        }

        if ((prefixBytes.length + valueBytes.length) > 254) {
            throw new IllegalArgumentException("Content (prefix + text) can be no longer than 255 bytes and this has " +
                                               valueBytes.length);
        }

        // Type (1b), total item length (1b), prefix length (1b), prefix (xb), text (xb)
        Buffer buffer = obtainMemory.allocate(2 + 1 + prefixBytes.length + valueBytes.length);
        buffer.put(this.type.getByte());
        buffer.put((byte)(1 + prefixBytes.length + valueBytes.length));
        buffer.put((byte)prefixBytes.length);
        buffer.put(prefixBytes);
        buffer.put(valueBytes);
        buffer.position(0);// 缓存区归零.
        return buffer;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("SdesChunkPrivItem{")
                .append("prefix='").append(this.prefix).append('\'')
                .append(", value='").append(this.value).append('\'')
                .append('}').toString();
    }
}
