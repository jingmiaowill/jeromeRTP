package com.jerome.rtp.protocol.data;

import java.nio.charset.Charset;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

public class SdesChunkItems {

    public static final SdesChunkItem NULL_ITEM = new SdesChunkItem(SdesChunkItem.Type.NULL, null);


    public static SdesChunkItem createNullItem() {
        return NULL_ITEM;
    }

    public static SdesChunkItem createCnameItem(String cname) {
        return new SdesChunkItem(SdesChunkItem.Type.CNAME, cname);
    }

    public static SdesChunkItem createNameItem(String name) {
        return new SdesChunkItem(SdesChunkItem.Type.NAME, name);
    }

    public static SdesChunkItem createEmailItem(String email) {
        return new SdesChunkItem(SdesChunkItem.Type.EMAIL, email);
    }

    public static SdesChunkItem createPhoneItem(String phone) {
        return new SdesChunkItem(SdesChunkItem.Type.PHONE, phone);
    }

    public static SdesChunkItem createLocationItem(String location) {
        return new SdesChunkItem(SdesChunkItem.Type.LOCATION, location);
    }

    public static SdesChunkItem createToolItem(String tool) {
        return new SdesChunkItem(SdesChunkItem.Type.TOOL, tool);
    }

    public static SdesChunkItem createNoteItem(String note) {
        return new SdesChunkItem(SdesChunkItem.Type.NOTE, note);
    }

    public static SdesChunkPrivItem createPrivItem(String prefix, String value) {
        return new SdesChunkPrivItem(prefix, value);
    }

    public static SdesChunkItem decode(Buffer buffer) {
    	SdesChunkItem.Type type = SdesChunkItem.Type.fromByte(buffer.get());
        switch (type) {
            case NULL:
                return NULL_ITEM;
            case CNAME:
            case NAME:
            case EMAIL:
            case PHONE:
            case LOCATION:
            case TOOL:
            case NOTE:
                byte[] value = new byte[BufferUtils.getOneUnsignedByte(buffer)];
                buffer.get(value);
                return new SdesChunkItem(type, new String(value, Charset.forName("UTF-8")));
            case PRIV:
                short valueLength = BufferUtils.getOneUnsignedByte(buffer);
                short prefixLength = BufferUtils.getOneUnsignedByte(buffer);
                value = new byte[valueLength - prefixLength - 1];
                byte[] prefix = new byte[prefixLength];
                buffer.get(prefix);
                buffer.get(value);
                return new SdesChunkPrivItem(new String(prefix, Charset.forName("UTF-8")),
                                             new String(value, Charset.forName("UTF-8")));
            default:
                throw new IllegalArgumentException("Unknown type of SDES chunk: " + type);
        }
    }

    public static Buffer encode(SdesChunkItem item,MemoryManager<?> obtainMemory) {
        return item.encode(obtainMemory);
    }
}
