package org.example.exmod.util;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class BufferUtil {

    public static byte[] toByteArray(ByteBuf byteBuf) {
        if (byteBuf.hasArray()) return byteBuf.array();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int size = byteBuf.readableBytes();
//        for (int i = 0; i < size; i++) {
//            bytes[i] = byteBuf.readByte();
//        }
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public static void printOutByteArray(byte[] bytes) {
        System.out.println("Size: " + bytes.length);
        for (byte bite: bytes) {
            System.out.printf("%02X ", bite);
        }
        System.out.println();
    }

    public static byte[] toByteArray(ByteBuffer buffer) {
        if (buffer.hasArray()) return buffer.array();
        byte[] bytes = new byte[buffer.remaining()];
        int size = buffer.remaining();
//        for (int i = 0; i < size; i++) {
//            bytes[i] = byteBuf.readByte();
//        }
        buffer.get(buffer.position(), bytes);
        return bytes;
    }
}
