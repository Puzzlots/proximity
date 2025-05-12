package io.github.Puzzlots.Proximity.io.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * The default & fast implementation of the IKeylessSerializer.
 *
 * @see IKeylessSerializer
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public class KeylessBinarySerializer implements IKeylessSerializer {

    final DataOutputStream output;
    final ByteArrayOutputStream byteStream;

    public KeylessBinarySerializer() {
        byteStream = new ByteArrayOutputStream();
        output = new DataOutputStream(byteStream);
    }

    @Override
    public IKeylessSerializer newInstance() {
        return new KeylessBinarySerializer();
    }

    public void writeByte(byte i) throws IOException {
        output.writeByte(i);
    }

    public void writeByteArray(byte[] array) throws IOException {
        output.writeInt(array.length);
        for (byte i : array) output.writeByte(i);
    }

    public void writeShort(short i) throws IOException {
        output.writeShort(i);
    }

    public void writeShortArray(short[] array) throws IOException {
        output.writeInt(array.length);
        for (short i : array) output.writeShort(i);
    }

    public void writeInt(int i) throws IOException {
        output.writeInt(i);
    }

    public void writeIntArray(int[] array) throws IOException {
        output.writeInt(array.length);
        for (int i : array) output.writeInt(i);
    }

    public void writeLong(long i) throws IOException {
        output.writeLong(i);
    }

    public void writeLongArray(long[] array) throws IOException {
        output.writeInt(array.length);
        for (long i : array) output.writeLong(i);
    }

    public void writeFloat(float i) throws IOException {
        output.writeFloat(i);
    }

    public void writeFloatArray(float[] array) throws IOException {
        output.writeInt(array.length);
        for (float i : array) output.writeFloat(i);
    }

    public void writeDouble(double i) throws IOException {
        output.writeDouble(i);
    }

    public void writeDoubleArray(double[] array) throws IOException {
        output.writeInt(array.length);
        for (double i : array) output.writeDouble(i);
    }

    public void writeBoolean(boolean b) throws IOException {
        output.writeBoolean(b);
    }

    public void writeBooleanArray(boolean[] array) throws IOException {
        output.writeInt(array.length);
        for (boolean b : array) output.writeBoolean(b);
    }

    public void writeChar(char c) throws IOException {
        output.writeChar(c);
    }

    public void writeCharArray(char[] array) throws IOException {
        output.writeInt(array.length);
        for (char c : array) output.writeChar(c);
    }

    public void writeString(String v) throws IOException {
        output.writeUTF(v);
    }

    public void writeStringArray(String[] array) throws IOException {
        output.writeInt(array.length);
        for (String s : array) output.writeUTF(s);
    }

    public <T extends IDataStreamSerializable> void writeRawObject(T object) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteOut);
        object.write(stream);

        writeByteArray(byteOut.toByteArray());
    }

    public <T extends IDataStreamSerializable> void writeRawObjectArray(T[] array) throws IOException {
        output.writeInt(array.length);
        for (T obj : array) {
            writeRawObject(obj);
        }
    }

    public <T extends IKeylessSerializable> void writeKeylessObject(T object) throws IOException {
        IKeylessSerializer miniSerializer = newInstance();
        object.write(miniSerializer);

        writeByteArray(miniSerializer.toBytes());
    }

    public <T extends IKeylessSerializable> void writeKeylessObjectArray(T[] array) throws IOException {
        output.writeInt(array.length);
        for (T obj : array) {
            writeKeylessObject(obj);
        }
    }

    public <T> void writeCustomObject(T object) throws IOException {
        if (!KEYLESS_SERIALIZER_MAP.containsKey(object.getClass())) throw new RuntimeException("cannot serialize class of type \"" + object.getClass().getName() + "\" due to it not having a registered serializer.");

        IKeylessSerializer miniSerializer = newInstance();
        //noinspection unchecked
        IKeylessCustomSerializable<T> serializer = IKeylessSerializer.getSerializer((Class<T>) object.getClass());
        serializer.write(miniSerializer, object);

        writeByteArray(miniSerializer.toBytes());
    }

    public <T> void writeCustomObjectArray(T[] array) throws IOException {
        output.writeInt(array.length);
        for (T obj : array) {
            writeCustomObject(obj);
        }
    }

    public byte[] toBytes() {
        return byteStream.toByteArray();
    }

    public byte[] toCompressedBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream stream1 = new GZIPOutputStream(stream);
        stream1.write(byteStream.toByteArray());
        stream1.close();
        return stream.toByteArray();
    }

    public String toBase64() {
        return Base64.getEncoder().encodeToString(toBytes());
    }

    public String toCompressedBase64() throws IOException {
        return Base64.getEncoder().encodeToString(toCompressedBytes());
    }

}