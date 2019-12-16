package me.mickmmars.factions.config;

import com.google.gson.JsonObject;
import me.mickmmars.factions.Factions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonConfig {

    private final File file;

    private JsonObject defaults = new JsonObject();
    private JsonObject object;

    public JsonConfig(final File file) {
        this.file = file;
        reloadFromFile();
    }

    public boolean saveConfig() {
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (final IOException e) {
                return false;
            }
        }
        try (final FileWriter writer = new FileWriter(this.file)) {
            writer.write(Factions.getInstance().getPrettyGson().toJson(this.object));
        } finally {
            return true;
        }
    }

    public boolean reloadFromFile() {
        if (!this.file.exists()) {
            if (this.object == null) this.object = this.defaults;
            return false;
        }
        try (final FileReader reader = new FileReader(this.file)) {
            this.object = Factions.getInstance().getParser().parse(reader).getAsJsonObject();
        } catch (final IOException e) {
            return false;
        } finally {
            return true;
        }
    }

    public <T> T get(final String key, final Class<T> type) {
        if (!this.object.has(key)) return null;
        return Factions.getInstance().getGson().fromJson(this.object.get(key), type);
    }

    public void set(final String key, final Object object) {
        this.object.add(key, Factions.getInstance().getGson().toJsonTree(object));
    }

    public String toString() {
        return this.object.toString();
    }

    public void fromString(final String string) {
        this.object = Factions.getInstance().getParser().parse(string).getAsJsonObject();
    }

}
