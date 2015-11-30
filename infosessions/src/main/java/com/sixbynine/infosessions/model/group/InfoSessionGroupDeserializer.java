package com.sixbynine.infosessions.model.group;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.sixbynine.infosessions.model.programs.Faculty;
import com.sixbynine.infosessions.model.programs.Program;

import java.lang.reflect.Type;

/**
 * Created by stevenkideckel on 14-12-31.
 */
public class InfoSessionGroupDeserializer implements JsonDeserializer<InfoSessionGroup> {

    @Override
    public InfoSessionGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int type = obj.get("type").getAsInt();
        switch (type) {
            case InfoSessionGroup.CONSTANT:
                int id = obj.get("id").getAsInt();
                return InfoSessionGroup.fromId(id);
            case InfoSessionGroup.FACULTY:
                String facultyName = obj.get("faculty").getAsString();
                return InfoSessionGroup.createGroupForFaculty(Faculty.fromName(facultyName));
            case InfoSessionGroup.PROGRAM:
                String programName = obj.get("program").getAsString();
                return InfoSessionGroup.createGroupForProgram(Program.fromName(programName));
        }
        throw new RuntimeException("unexpected type while deserializing InfoSessionGroup: " + type);
    }
}
