package io.github.Puzzlots.Proximity.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * An API class for creating deserializers with keyless binary objects.
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public interface IKeylessDeserializer {

    HashMap<Class<?>, IKeylessCustomSerializable<?>> KEYLESS_DESERIALIZER_MAP = new HashMap<>();

    /**
     * Registers a deserializer for non-user owned objects.
     *
     * @param deserializer The serializer being registered.
     */
    static void registerDeserializer(IKeylessCustomSerializable<?> deserializer) {
        if (deserializer.getSerializableType().isArray()) throw new RuntimeException("cannot register deserializer of array type, I recommend registering the component type instead.");
        KEYLESS_DESERIALIZER_MAP.put(deserializer.getSerializableType(), deserializer);
    }

    /**
     * Checks if a custom deserializer exist for a custom object class.
     *
     * @param clazz the class that may have a custom deserializer built for it.
     */
    static boolean hasDeserializer(Class<?> clazz) {
        return !KEYLESS_DESERIALIZER_MAP.containsKey(clazz);
    }

    /**
     * Checks if a custom deserializer exist for a custom object class.
     *
     * @param obj the object that may have a custom deserializer built for it.
     */
    static boolean hasDeserializer(Object obj) {
        return hasDeserializer(obj.getClass());
    }

    /**
     * Instances the default named deserializer implementation.
     * @param bytes The content bytes to deserialize.
     * @param isCompressed The option to allow decompression to the bytes.
     */
    static IKeylessDeserializer createDefault(byte[] bytes, boolean isCompressed) throws IOException {
        if (isCompressed)
            return new KeylessBinaryDeserializer(NativeArrayUtil.readNBytes(new GZIPInputStream(new ByteArrayInputStream(bytes)), Integer.MAX_VALUE));
        return new KeylessBinaryDeserializer(bytes);
    }

    /**
     * Instances the default named deserializer implementation.
     * @param bytes The content bytes to deserialize.
     * @param isCompressed The option to allow decompression to the bytes.
     */
    static IKeylessDeserializer createDefault(Byte[] bytes, boolean isCompressed) throws IOException {
        return createDefault(NativeArrayUtil.toNativeArray(bytes), isCompressed);
    }

    /**
     * Instances the default named deserializer implementation.
     * @param bytes The content bytes to deserialize.
     */
    default IKeylessDeserializer newInstance(byte[] bytes) throws IOException {
        return newInstance(bytes, false);
    }

    /**
     * Instances the default named deserializer implementation.
     * @param bytes The content bytes to deserialize.
     */
    default IKeylessDeserializer newInstance(Byte[] bytes) throws IOException {
        return newInstance(bytes, false);
    }

    /**
     * Creates new instances of the parent/current deserializer.
     * @param bytes The content bytes to deserialize.
     * @param isCompressed The option to allow decompression to the bytes.
     */
    IKeylessDeserializer newInstance(byte[] bytes, boolean isCompressed) throws IOException;
    default IKeylessDeserializer newInstance(Byte[] bytes, boolean isCompressed) throws IOException {
        return newInstance(NativeArrayUtil.toNativeArray(bytes), isCompressed);
    }

    /**
     * Reads a single byte.
     */
    byte readByte() throws IOException;

    /**
     * Reads a byte array.
     */
    Byte[] readByteArray() throws IOException;

    /**
     * Reads a native byte array.
     */
    default byte[] readByteArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readByteArray());
    }

    /**
     * Reads a list of bytes.
     */
    default List<Byte> readByteArrayAsList() throws IOException {
        return Arrays.asList(readByteArray());
    }

    /**
     * Reads a single short.
     */
    short readShort() throws IOException;

    /**
     * Reads a short array.
     */
    Short[] readShortArray() throws IOException;

    /**
     * Reads a native short array.
     */
    default short[] readShortArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readShortArray());
    }

    /**
     * Reads a list of shorts.
     */
    default List<Short> readShortArrayAsList() throws IOException {
        return Arrays.asList(readShortArray());
    }

    /**
     * Reads a single integer.
     */
    int readInt() throws IOException;

    /**
     * Reads an integer array.
     */
    Integer[] readIntArray() throws IOException;

    /**
     * Reads a native integer array.
     */
    default int[] readIntArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readIntArray());
    }

    /**
     * Reads a list of integers.
     */
    default List<Integer> readIntArrayAsList() throws IOException {
        return Arrays.asList(readIntArray());
    }

    /**
     * Reads a single long.
     */
    long readLong() throws IOException;

    /**
     * Reads a long array.
     */
    Long[] readLongArray() throws IOException;

    /**
     * Reads a native long array.
     */
    default long[] readLongArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readLongArray());
    }

    /**
     * Reads a list of longs.
     */
    default List<Long> readLongArrayAsList() throws IOException {
        return Arrays.asList(readLongArray());
    }

    /**
     * Reads a single float.
     */
    float readFloat() throws IOException;

    /**
     * Reads a float array.
     */
    Float[] readFloatArray() throws IOException;

    /**
     * Reads a native float array.
     */
    default float[] readFloatArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readFloatArray());
    }

    /**
     * Reads a list of floats.
     */
    default List<Float> readFloatArrayAsList() throws IOException {
        return Arrays.asList(readFloatArray());
    }

    /**
     * Reads a single double.
     */
    double readDouble() throws IOException;

    /**
     * Reads a double array.
     */
    Double[] readDoubleArray() throws IOException;

    /**
     * Reads a native double array.
     */
    default double[] readDoubleArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readDoubleArray());
    }

    /**
     * Reads a list of doubles.
     */
    default List<Double> readDoubleArrayAsList() throws IOException {
        return Arrays.asList(readDoubleArray());
    }

    /**
     * Reads a single boolean.
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads a boolean array.
     */
    Boolean[] readBooleanArray() throws IOException;

    /**
     * Reads a native boolean array.
     */
    default boolean[] readBooleanArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readBooleanArray());
    }

    /**
     * Reads a list of booleans.
     */
    default List<Boolean> readBooleanArrayAsList() throws IOException {
        return Arrays.asList(readBooleanArray());
    }

    /**
     * Reads a single character.
     */
    char readChar() throws IOException;

    /**
     * Reads a character array.
     */
    Character[] readCharArray() throws IOException;

    /**
     * Reads a native character array.
     */
    default char[] readCharArrayAsNative() throws IOException {
        return NativeArrayUtil.toNativeArray(readCharArray());
    }

    /**
     * Reads a list of characters.
     */
    default List<Character> readCharArrayAsList() throws IOException {
        return Arrays.asList(readCharArray());
    }

    /**
     * Reads a single string.
     */
    String readString() throws IOException;

    /**
     * Reads a string array.
     */
    String[] readStringArray() throws IOException;

    /**
     * Reads a list of strings.
     */
    default List<String> readStringArrayAsList() throws IOException {
        return Arrays.asList(readStringArray());
    }

    /**
     * Reads a single raw-object.
     */
    <T extends IDataStreamSerializable> T readRawObject(Class<T> type) throws IOException;

    /**
     * Reads a raw-object array.
     */
    <T extends IDataStreamSerializable> T[] readRawObjectArray(Class<T> type) throws IOException;

    /**
     * Reads a list of raw-objects.
     */
    default <T extends IDataStreamSerializable> List<T> readRawObjectArrayAsList(Class<T> type) throws IOException {
        return Arrays.asList(readRawObjectArray(type));
    }

    /**
     * Reads a single keyless-object.
     */
    <T extends IKeylessSerializable> T readKeylessObject(Class<T> type) throws IOException;

    /**
     * Reads a keyless-object array.
     */
    <T extends IKeylessSerializable> T[] readKeylessObjectArray(Class<T> type) throws IOException;

    /**
     * Reads a list of keyless-objects.
     */
    default <T extends IKeylessSerializable> List<T> readKeylessObjectArrayAsList(Class<T> type) throws IOException {
        return Arrays.asList(readKeylessObjectArray(type));
    }

    /**
     * Reads a single custom-object.
     */
    <T> T readCustomObject(Class<T> type) throws IOException;

    /**
     * Reads a custom-object array.
     */
    <T> T[] readCustomObjectArray(Class<T> type) throws IOException;

    /**
     * Reads a list of custom-objects.
     */
    default <T> List<T> readCustomObjectArrayAsList(Class<T> type) throws IOException {
        return Arrays.asList(readCustomObjectArray(type));
    }

}