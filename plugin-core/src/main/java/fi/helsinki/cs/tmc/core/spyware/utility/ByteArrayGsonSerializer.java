package fi.helsinki.cs.tmc.core.spyware.utility;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

/**
 * Converts byte[] to/from base64 in JSON.
 */
public class ByteArrayGsonSerializer implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

    @Override
    public JsonElement serialize(final byte[] data, final Type type, final JsonSerializationContext jsc) {

        if (data == null) {
            return JsonNull.INSTANCE;
        } else {
            return new JsonPrimitive(Base64.encodeBase64String(data));
        }
    }

    @Override
    public byte[] deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) {

        if (je.isJsonPrimitive() && ((JsonPrimitive) je).isString()) {
            return Base64.decodeBase64(je.getAsString());
        } else if (je.isJsonNull()) {
            return null;
        } else {
            throw new JsonParseException("Not a base64 string.");
        }
    }
}
