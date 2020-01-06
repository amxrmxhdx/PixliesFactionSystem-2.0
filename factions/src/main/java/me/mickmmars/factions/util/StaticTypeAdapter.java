package me.mickmmars.factions.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.gson.stream.JsonToken.END_OBJECT;
import static java.lang.reflect.Modifier.isStatic;

final class StaticTypeAdapter<T>
        extends TypeAdapter<Class<T>> {

    private static final String TARGET_CLASS_PROPERTY = "___class";

    private final Gson gson;

    private StaticTypeAdapter(final Gson gson) {
        this.gson = gson;
    }

    static <T> TypeAdapter<Class<T>> getStaticTypeAdapter(final Gson gson) {
        return new StaticTypeAdapter<>(gson);
    }

    @Override
    @SuppressWarnings("resource")
    public void write(final JsonWriter out, final Class<T> value)
            throws IOException {
        try {
            final Iterator<Field> iterator = Stream.of(value.getFields())
                    .filter(f -> isStatic(f.getModifiers()))
                    .iterator();
            out.beginObject();
            while ( iterator.hasNext() ) {
                final Field field = iterator.next();
                out.name(field.getName());
                field.setAccessible(true);
                final Object fieldValue = field.get(null);
                @SuppressWarnings({ "unchecked", "rawtypes" })
                final TypeAdapter<Object> adapter = (TypeAdapter) gson.getAdapter(field.getType());
                adapter.write(out, fieldValue);
            }
            out.name(TARGET_CLASS_PROPERTY);
            out.value(value.getName());
            out.endObject();
        } catch ( final IllegalAccessException ex ) {
            throw new IOException(ex);
        }
    }

    @Override
    public Class<T> read(final JsonReader in)
            throws IOException {
        try {
            Class<?> type = null;
            in.beginObject();
            final Map<String, JsonElement> buffer = new HashMap<>();
            while ( in.peek() != END_OBJECT ) {
                final String property = in.nextName();
                switch ( property ) {
                    case TARGET_CLASS_PROPERTY:
                        type = Class.forName(in.nextString());
                        break;
                    default:
                        // buffer until the target class name is known
                        if ( type == null ) {
                            final TypeAdapter<JsonElement> adapter = gson.getAdapter(JsonElement.class);
                            final JsonElement jsonElement = adapter.read(in);
                            buffer.put(property, jsonElement);
                        } else {
                            // flush the buffer
                            if ( !buffer.isEmpty() ) {
                                for ( final Map.Entry<String, JsonElement> e : buffer.entrySet() ) {
                                    final Field field = type.getField(e.getKey());
                                    final Object value = gson.getAdapter(field.getType()).read(in);
                                    field.set(null, value);
                                }
                                buffer.clear();
                            }
                            final Field field = type.getField(property);
                            if ( isStatic(field.getModifiers()) ) {
                                final TypeAdapter<?> adapter = gson.getAdapter(field.getType());
                                final Object value = adapter.read(in);
                                field.set(null, value);
                            }
                        }
                        break;
                }
            }
            in.endObject();
            // flush the buffer
            if ( type != null && !buffer.isEmpty() ) {
                for ( final Map.Entry<String, JsonElement> e : buffer.entrySet() ) {
                    final Field field = type.getField(e.getKey());
                    final Object value = gson.fromJson(e.getValue(), field.getType());
                    field.set(null, value);
                }
                buffer.clear();
            }
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final Class<T> castType = (Class) type;
            return castType;
        } catch ( final ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex ) {
            throw new IOException(ex);
        }
    }

}
