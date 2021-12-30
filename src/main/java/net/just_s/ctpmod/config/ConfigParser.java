package net.just_s.ctpmod.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.just_s.ctpmod.CTPMod;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

public class ConfigParser {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final JsonParser PARSER = new JsonParser();

    public static final ConfigParser INSTANCE = new ConfigParser(FabricLoader.getInstance().getConfigDir().resolve("ctpmod.json").toFile());
    static {
        INSTANCE.load();
    }

    public final File file;

    public ConfigParser(File file) {
        this.file = file;
    }

    public boolean deletePoint(String pointName) {
        //Удаление из json файла
        boolean isPointHere = false;
        int pointIndex = 0;
        Point[] pointsArr = CTPMod.points;
        for (int i = 0; i < pointsArr.length; i++) {
            if (Objects.equals(pointsArr[i].getName(), pointName)) {
                isPointHere = true;
                pointIndex = i;
                break;
            }
        }
        if (!isPointHere) {return false;}

        Point[] newPointsArr = new Point[pointsArr.length - 1];

        for (int i = 0, j = 0; i < pointsArr.length; i++) {
            if (i == pointIndex) {
                continue;
            }
            newPointsArr[j++] = pointsArr[i];
        }
        CTPMod.points = newPointsArr;
        save();
        return true;
    }

    public void addPoint(Point point) {
        //if (CTPMod.points == null) {CTPMod.points = new Point[]{};}
        Point[] pointsArr = CTPMod.points;
        Point[] newPointsArr = new Point[pointsArr.length + 1];
        for (int i = 0; i < pointsArr.length; i++) {newPointsArr[i] = pointsArr[i];}
        newPointsArr[pointsArr.length] = point;
        CTPMod.points = newPointsArr;
        save();
    }

    public void load() {
        CTPMod.LOGGER.debug("Loading config...");
        if (file.exists()) {
            try {
                String json_string = Files.readString(Path.of(file.toString()), StandardCharsets.US_ASCII);
                CTPMod.delta = deltaFromJson(json_string);
                CTPMod.points = pointsFromJson(json_string);
            } catch (Exception e) {
                CTPMod.LOGGER.error("Could not load config from file '" + file.getAbsolutePath() + "'", e);
            }
        }
        save();
    }

    public void save() {
        CTPMod.LOGGER.debug("Saving config...");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(toJson());
        } catch (Exception e) {
            CTPMod.LOGGER.error("Could not save config to file '" + file.getAbsolutePath() + "'", e);
        }
    }

    protected int deltaFromJson(String json_string) {
        //CTPMod.LOGGER.error("fromJson: " + json_string );
        JsonObject object = JsonParser.parseString(json_string).getAsJsonObject();
        return object.getAsJsonPrimitive("delta").getAsInt();
    }

    protected Point[] pointsFromJson(String json_string) {
        //CTPMod.LOGGER.error("fromJson: " + json_string );
        JsonObject object = JsonParser.parseString(json_string).getAsJsonObject();
        String newJSONString = object.getAsJsonArray("points").toString();
        return GSON.fromJson(newJSONString, Point[].class);
    }


    protected String toJson(){
        //CTPMod.LOGGER.error(Arrays.toString(CTPMod.points));
        Point[] points = CTPMod.points;
        String[] jsonReprOfPoints = new String[points.length];
        for(int i = 0; i < points.length; i++) {
            jsonReprOfPoints[i] = points[i].toJson();
        }
        return "{\"delta\":" + CTPMod.delta + ", \"points\":[" + String.join(",", jsonReprOfPoints)+ "]}";
    }
}
