package io.github.puzzlots.proximity.io.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An API class for objects impls that allows custom serialization and deserialization to and from streams.
 *
 * @see DataOutputStream
 * @see DataInputStream
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public interface IDataStreamSerializable {

    /**
     * Deserializes data for use inside this serializable.
     * @param in The parent stream that contains all the data for the class being read.
     */
    void read(DataInputStream in) throws IOException;

    /**
     * Writes the data from this object to a serializer.
     * @param out The parent stream that is being written to.
     */
    void write(DataOutputStream out) throws IOException;
}