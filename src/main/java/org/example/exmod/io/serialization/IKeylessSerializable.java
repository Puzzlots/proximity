package org.example.exmod.io.serialization;

import java.io.IOException;

/**
 * An API class for objects impls that allows custom serialization and deserialization.
 *
 * @see IKeylessSerializer
 * @see IKeylessDeserializer
 *
 * @author Mr Zombii
 * @since 1.0.0
 */
public interface IKeylessSerializable {

    /**
     * Deserializes data for use inside this serializable.
     * @param in The parent deserializer that contains all the data for the class being deserialized.
     */
    void read(IKeylessDeserializer in) throws IOException;

    /**
     * Writes the data from this object to a serializer.
     * @param out The parent serializer that is being written to.
     */
    void write(IKeylessSerializer out) throws IOException;
}