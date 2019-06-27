package fi.helsinki.cs.tmc.core.old.spyware.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.helsinki.cs.tmc.core.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.spyware.utility.ByteArrayGsonSerializer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class EventStore {

    private static final Logger LOG = Logger.getLogger(EventStore.class.getName());

    private final FileIO configFile;

    public EventStore(final FileIO configFile) {

        this.configFile = configFile;
    }

    public void save(final LoggableEvent[] events) throws IOException {

        final String text = getGson().toJson(events);
        final Writer writer = configFile.getWriter();
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }

    public LoggableEvent[] load() throws IOException {

        final StringWriter writer = new StringWriter();
        final Reader reader = configFile.getReader();
        LoggableEvent[] result = new LoggableEvent[0];

        if (reader == null) {
            return result;
        }
        try {
            IOUtils.copy(reader, writer);
            result = getGson().fromJson(writer.toString(), LoggableEvent[].class);
            if (result == null) {
                return new LoggableEvent[0];
            }
        } finally {
            writer.close();
            reader.close();
        }

        return result;
    }

    private Gson getGson() {

        return new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayGsonSerializer()).create();
    }

    public void clear() throws IOException {

        final Writer writer = configFile.getWriter();
        try {
            writer.write("");
        } finally {
            writer.close();
        }
    }
}
