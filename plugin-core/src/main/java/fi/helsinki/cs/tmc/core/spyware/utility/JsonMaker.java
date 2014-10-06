package fi.helsinki.cs.tmc.core.spyware.utility;

import com.google.gson.JsonObject;

/**
 * A convenient way to build ad-hoc JSON objects.
 */
public class JsonMaker {
    
    private final JsonObject toplevel;
    
    public JsonMaker(final JsonObject toplevel) {

        this.toplevel = toplevel;
    }
    
    public JsonMaker() {

        this(new JsonObject());
    }


    public static JsonMaker create() {

        return new JsonMaker();
    }

    public JsonMaker add(final String name, final String value) {

        toplevel.addProperty(name, value);
        return this;
    }

    public JsonMaker add(final String name, final long value) {

        toplevel.addProperty(name, value);
        return this;
    }

    public JsonMaker add(final String name, final boolean value) {

        toplevel.addProperty(name, value);
        return this;
    }

    @Override
    public String toString() {

        return toplevel.toString();
    }
}
