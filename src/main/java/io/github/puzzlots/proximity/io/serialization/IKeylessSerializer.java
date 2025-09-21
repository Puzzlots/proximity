package io.github.puzzlots.proximity.io.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * An API class for creating serializers with only binary objects, similar to the DataOutputStream.
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public interface IKeylessSerializer {

    HashMap<Class<?>, IKeylessCustomSerializable<?>> KEYLESS_SERIALIZER_MAP = new HashMap<>();

    /**
     * Registers a serializer for non-user owned objects.
     *
     * @see IKeylessCustomSerializable
     *
     * @param serializer The serializer being registered.
     */
    static void registerSerializer(IKeylessCustomSerializable<?> serializer) {
        if (serializer.getSerializableType().isArray()) throw new RuntimeException("cannot register serializer of array type, I recommend registering the component type instead.");
        KEYLESS_SERIALIZER_MAP.put(serializer.getSerializableType(), serializer);
    }

    /**
     * Gets the serializer for a custom object class.
     *
     * @param clazz the class that may have a custom serializer built for it.
     */
    static <T> IKeylessCustomSerializable<T> getSerializer(Class<T> clazz) {
        //noinspection unchecked
        return (IKeylessCustomSerializable<T>) KEYLESS_SERIALIZER_MAP.get(clazz);
    }

    /**
     * Checks if a custom serializer exist for a custom object class.
     *
     * @param clazz the class that may have a custom serializer built for it.
     */
    static boolean hasSerializer(Class<?> clazz) {
        return !KEYLESS_SERIALIZER_MAP.containsKey(clazz);
    }

    /**
     * Checks if a custom serializer exist for a custom object.
     *
     * @param obj the object that may have a custom serializer built for it.
     */
    static boolean hasSerializer(Object obj) {
        return hasSerializer(obj.getClass());
    }

    /**
     * Instances the default keyless serializer implementation.
     */
    static IKeylessSerializer createDefault() {
        return new KeylessBinarySerializer();
    }

    IKeylessSerializer newInstance();

    /**
     * Writes a byte to the serializer.
     * @param i The byte to be written.
     */
    void writeByte(byte i) throws IOException;

    /**
     * Writes an array of bytes.
     * @param array The array to be written.
     */
    void writeByteArray(byte[] array) throws IOException;

    /**
     * Writes an array of bytes.
     * @param array The array to be written.
     */
    default void writeByteArray(Byte[] array) throws IOException {
        writeByteArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of bytes.
     * @param list The list to be written.
     */
    default void writeByteList(@NotNull List<Byte> list) throws IOException {
        writeByteArray(NativeArrayUtil.toNativeArray(list.toArray(new Byte[0])));
    }

    /**
     * Writes a short to the serializer.
     * @param i The short to be written.
     */
    void writeShort(short i) throws IOException;

    /**
     * Writes an array of shorts.
     * @param array The array to be written.
     */
    void writeShortArray(short[] array) throws IOException;

    /**
     * Writes an array of shorts.
     * @param array The array to be written.
     */
    default void writeShortArray(Short[] array) throws IOException {
        writeShortArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of shorts.
     * @param list The list to be written.
     */
    default void writeShortList(@NotNull List<Short> list) throws IOException {
        writeShortArray(NativeArrayUtil.toNativeArray(list.toArray(new Short[0])));
    }

    /**
     * Writes an integer to the serializer.
     * @param i The short to be written.
     */
    void writeInt(int i) throws IOException;

    /**
     * Writes an array of integers.
     * @param array The array to be written.
     */
    void writeIntArray(int[] array) throws IOException;

    /**
     * Writes an array of integers.
     * @param array The array to be written.
     */
    default void writeIntArray(Integer[] array) throws IOException {
        writeIntArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of integers.
     * @param list The list to be written.
     */
    default void writeIntList(@NotNull List<Integer> list) throws IOException {
        writeIntArray(NativeArrayUtil.toNativeArray(list.toArray(new Integer[0])));
    }

    /**
     * Writes a long to the serializer.
     * @param i The long to be written.
     */
    void writeLong(long i) throws IOException;

    /**
     * Writes an array of longs.
     * @param array The array to be written.
     */
    void writeLongArray(long[] array) throws IOException;

    /**
     * Writes an array of longs.
     * @param array The array to be written.
     */
    default void writeLongArray(Long[] array) throws IOException {
        writeLongArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of longs.
     * @param list The list to be written.
     */
    default void writeLongList(@NotNull List<Long> list) throws IOException {
        writeLongArray(NativeArrayUtil.toNativeArray(list.toArray(new Long[0])));
    }

    /**
     * Writes a float to the serializer.
     * @param i The float to be written.
     */
    void writeFloat(float i) throws IOException;

    /**
     * Writes an array of floats.
     * @param array The array to be written.
     */
    void writeFloatArray(float[] array) throws IOException;

    /**
     * Writes an array of floats.
     * @param array The array to be written.
     */
    default void writeFloatArray(Float[] array) throws IOException {
        writeFloatArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of floats.
     * @param list The list to be written.
     */
    default void writeFloatList(@NotNull List<Float> list) throws IOException {
        writeFloatArray(NativeArrayUtil.toNativeArray(list.toArray(new Float[0])));
    }

    /**
     * Writes a double to the serializer.
     * @param i The double to be written.
     */
    void writeDouble(double i) throws IOException;

    /**
     * Writes an array of doubles.
     * @param array The array to be written.
     */
    void writeDoubleArray(double[] array) throws IOException;

    /**
     * Writes an array of doubles.
     * @param array The array to be written.
     */
    default void writeDoubleArray(Double[] array) throws IOException {
        writeDoubleArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of doubles.
     * @param list The list to be written.
     */
    default void writeDoubleList(@NotNull List<Double> list) throws IOException {
        writeDoubleArray(NativeArrayUtil.toNativeArray(list.toArray(new Double[0])));
    }

    /**
     * Writes a boolean to the serializer.
     * @param b The boolean to be written.
     */
    void writeBoolean(boolean b) throws IOException;

    /**
     * Writes an array of booleans.
     * @param array The array to be written.
     */
    void writeBooleanArray(boolean[] array) throws IOException;

    /**
     * Writes an array of booleans.
     * @param array The array to be written.
     */
    default void writeBooleanArray(Boolean[] array) throws IOException {
        writeBooleanArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of booleans.
     * @param list The list to be written.
     */
    default void writeBooleanList(@NotNull List<Boolean> list) throws IOException {
        writeBooleanArray(NativeArrayUtil.toNativeArray(list.toArray(new Boolean[0])));
    }

    /**
     * Writes a character to the serializer.
     * @param c The character to be written.
     */
    void writeChar(char c) throws IOException;

    /**
     * Writes an array of characters.
     * @param array The array to be written.
     */
    void writeCharArray(char[] array) throws IOException;

    /**
     * Writes an array of characters.
     * @param array The array to be written.
     */
    default void writeCharArray(Character[] array) throws IOException {
        writeCharArray(NativeArrayUtil.toNativeArray(array));
    }

    /**
     * Writes a list of characters.
     * @param list The list to be written.
     */
    default void writeCharList(@NotNull List<Character> list) throws IOException {
        writeCharArray(NativeArrayUtil.toNativeArray(list.toArray(new Character[0])));
    }

    /**
     * Writes a double to the serializer.
     * @param v The string to be written.
     */
    void writeString(String v) throws IOException;

    /**
     * Writes an array of strings.
     * @param array The array to be written.
     */
    void writeStringArray(String[] array) throws IOException;

    /**
     * Writes a list of strings.
     * @param list The list to be written.
     */
    default void writeStringList(@NotNull List<String> list) throws IOException {
        writeStringArray(list.toArray(new String[0]));
    }

    /**
     * Writes a raw-object using a pre-registered serializer.
     * @param object The object to be written.
     */
    <T extends IDataStreamSerializable> void writeRawObject(T object) throws IOException;

    /**
     * Writes an array of raw-objects using a pre-registered serializer.
     * @param array The array to be written.
     */
    <T extends IDataStreamSerializable> void writeRawObjectArray(T[] array) throws IOException;

    /**
     * Writes an array of raw-objects using a pre-registered serializer.
     * @param list The list to be written.
     */
    default <T extends IDataStreamSerializable> void writeRawObjectList(@NotNull List<T> list) throws IOException {
        writeRawObjectArray(list.toArray(new IDataStreamSerializable[0]));
    }

    /**
     * Writes a keyless-object using a pre-registered serializer.
     * @param object The object to be written.
     */
    <T extends IKeylessSerializable> void writeKeylessObject(T object) throws IOException;

    /**
     * Writes an array of keyless-objects using a pre-registered serializer.
     * @param array The array to be written.
     */
    <T extends IKeylessSerializable> void writeKeylessObjectArray(T[] array) throws IOException;

    /**
     * Writes a list of keyless-objects using a pre-registered serializer.
     * @param list The list to be written.
     */
    default <T extends IKeylessSerializable> void writeKeylessObjectList(@NotNull List<T> list) throws IOException {
        writeKeylessObjectArray(list.toArray(new IKeylessSerializable[0]));
    }

    /**
     * Writes a custom object using a pre-registered serializer.
     * @param object The object to be written.
     */
    <T> void writeCustomObject(T object) throws IOException;

    /**
     * Writes an array of custom-objects using a pre-registered serializer.
     * @param array The array to be written.
     */
    <T> void writeCustomObjectArray(T[] array) throws IOException;

    /**
     * Writes a list of custom-objects using a pre-registered serializer.
     * @param list The list to be written.
     */
    default <T> void writeCustomObjectList(@NotNull List<T> list) throws IOException {
        writeCustomObjectArray(list.toArray(new Object[0]));
    }

    /**
     * Creates a primitive byte array from the serialized data.
     */
    byte[] toBytes() throws IOException;

    /**
     * Creates a gzip compressed bytes from the serialized data.
     */
    default byte[] toCompressedBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream stream1 = new GZIPOutputStream(stream);
        stream1.write(toBytes());
        stream1.close();
        return stream.toByteArray();
    }

    /**
     * Creates a base64 string from uncompressed bytes.
     */
    default String toBase64() throws IOException {
        return Base64.getEncoder().encodeToString(toBytes());
    }

    /**
     * Creates a base64 string from compressed bytes.
     */
    default String toCompressedBase64() throws IOException {
        return Base64.getEncoder().encodeToString(toCompressedBytes());
    }

}