package io.github.puzzlots.proximity.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

/**
 * The default & fast implementation of the IKeylessDeserializer.
 *
 * @see IKeylessDeserializer
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public class KeylessBinaryDeserializer implements IKeylessDeserializer {

    DataInputStream input;
    ByteArrayInputStream byteStream;

    /**
     * A helper method for creating a keyless deserializer.
     *
     * @param bytes the bytes to deserialize.
     */
    public static KeylessBinaryDeserializer fromBytes(byte[] bytes) {
        return new KeylessBinaryDeserializer(bytes);
    }

    /**
     * A helper method for creating a keyless binary deserializer.
     *
     * @param bytes the bytes to deserialize.
     */
    public static KeylessBinaryDeserializer fromBytes(Byte[] bytes) throws IOException {
        return fromBytes(NativeArrayUtil.toNativeArray(bytes));
    }

    /**
     * A helper method for creating a keyless binary deserializer.
     *
     * @param bytes the bytes to deserialize.
     * @param isCompressed the option to choose weather if the bytes are treated as compressed or decompressed bytes.
     */
    public static KeylessBinaryDeserializer fromBytes(byte[] bytes, boolean isCompressed) throws IOException {
        if (isCompressed) return new KeylessBinaryDeserializer(NativeArrayUtil.readNBytes(new GZIPInputStream(new ByteArrayInputStream(bytes)), Integer.MAX_VALUE));
        else return fromBytes(bytes);
    }

    /**
     * A helper method for creating a keyless binary deserializer.
     *
     * @param bytes the bytes to deserialize.
     * @param isCompressed the option to choose weather if the bytes are treated as compressed or decompressed bytes.
     */
    public static KeylessBinaryDeserializer fromBytes(Byte[] bytes, boolean isCompressed) throws IOException {
        return fromBytes(NativeArrayUtil.toNativeArray(bytes), isCompressed);
    }

    public KeylessBinaryDeserializer(byte[] bytes) {
        byteStream = new ByteArrayInputStream(bytes);
        input = new DataInputStream(byteStream);
    }

    @Override
    public IKeylessDeserializer newInstance(byte[] bytes, boolean isCompressed) throws IOException {
        return fromBytes(bytes, isCompressed);
    }

    private <T> T[] readArray(Function<Integer, T[]> arrayCreator, ThrowableSupplier<T> supplier) throws IOException {
        T[] array = arrayCreator.apply(input.readInt());
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
        }
        return array;
    }

    @Override
    public byte readByte() throws IOException {
        return input.readByte();
    }

    @Override
    public Byte[] readByteArray() throws IOException {
        return readArray(Byte[]::new, input::readByte);
    }

    @Override
    public short readShort() throws IOException {
        return input.readShort();
    }

    @Override
    public Short[] readShortArray() throws IOException {
        return readArray(Short[]::new, input::readShort);
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt();
    }

    @Override
    public Integer[] readIntArray() throws IOException {
        return readArray(Integer[]::new, input::readInt);
    }

    @Override
    public long readLong() throws IOException {
        return input.readLong();
    }

    @Override
    public Long[] readLongArray() throws IOException {
        return readArray(Long[]::new, input::readLong);
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public Float[] readFloatArray() throws IOException {
        return readArray(Float[]::new, input::readFloat);
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Override
    public Double[] readDoubleArray() throws IOException {
        return readArray(Double[]::new, input::readDouble);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    @Override
    public Boolean[] readBooleanArray() throws IOException {
        return readArray(Boolean[]::new, input::readBoolean);
    }

    @Override
    public char readChar() throws IOException {
        return input.readChar();
    }

    @Override
    public Character[] readCharArray() throws IOException {
        return readArray(Character[]::new, input::readChar);
    }

    @Override
    public String readString() throws IOException {
        return input.readUTF();
    }

    @Override
    public String[] readStringArray() throws IOException {
        return readArray(String[]::new, input::readUTF);
    }

    @Override
    public <T extends IDataStreamSerializable> T readRawObject(Class<T> type) {
        T obj = null;

        try {
            obj = type.getDeclaredConstructor().newInstance();
            obj.read(new DataInputStream(new ByteArrayInputStream(readByteArrayAsNative())));
            return obj;
        } catch (
                InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException
                | IOException e
        ) {
            if (e instanceof EOFException) return obj;
            return null;
        }
    }

    @Override
    public <T extends IDataStreamSerializable> T[] readRawObjectArray(Class<T> type) throws IOException {
        int length = readInt();
        //noinspection unchecked
        T[] t = (T[]) Array.newInstance(type, length);
        for (int i = 0; i < t.length; i++) {
            t[i] = readRawObject(type);
        }
        return t;
    }

    @Override
    public <T extends IKeylessSerializable> T readKeylessObject(Class<T> type) {

        T obj = null;
        try {
            obj = type.getDeclaredConstructor().newInstance();
            obj.read(newInstance(readByteArrayAsNative(), false));
            return obj;
        } catch (
                InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException
                | IOException e
        ) {
            if (e instanceof EOFException) return obj;
            return null;
        }
    }

    @Override
    public <T extends IKeylessSerializable> T[] readKeylessObjectArray(Class<T> type) throws IOException {
        int length = readInt();
        //noinspection unchecked
        T[] t = (T[]) Array.newInstance(type, length);
        for (int i = 0; i < t.length; i++) {
            t[i] = readKeylessObject(type);
        }
        return t;
    }

    @Override
    public <T> T readCustomObject(Class<T> type) throws IOException {
        if (!KEYLESS_DESERIALIZER_MAP.containsKey(type)) throw new RuntimeException("cannot deserialize class of type \"" + type.getName() + "\" due to it not having a registered deserializer.");

        try {
            //noinspection unchecked
            return (T) KEYLESS_DESERIALIZER_MAP.get(type).read(newInstance(readByteArrayAsNative()));
        } catch (
                IllegalArgumentException | SecurityException e
        ) {
            return null;
        }
    }

    @Override
    public <T> T[] readCustomObjectArray(Class<T> type) throws IOException {
        if (!KEYLESS_DESERIALIZER_MAP.containsKey(type)) throw new RuntimeException("cannot deserialize class of type \"" + type.getName() + "\" due to it not having a registered deserializer.");

        int length = readInt();
        //noinspection unchecked
        T[] t = (T[]) Array.newInstance(type, length);
        for (int i = 0; i < t.length; i++) {
            t[i] = readCustomObject(type);
        }
        return t;
    }

}