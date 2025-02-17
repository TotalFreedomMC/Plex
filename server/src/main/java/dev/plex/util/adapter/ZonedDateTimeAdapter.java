package dev.plex.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.plex.Plex;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime>
{
    private static final String TIMEZONE = Plex.get().config.getString("server.timezone");

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toInstant().toEpochMilli());
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
        return ZonedDateTime.ofInstant(instant, ZoneId.of(TIMEZONE));
    }
}
