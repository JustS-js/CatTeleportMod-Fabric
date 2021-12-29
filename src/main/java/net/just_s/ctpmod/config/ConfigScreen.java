package net.just_s.ctpmod.config;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import net.just_s.ctpmod.CTPMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;

public class ConfigScreen {
    public static ConfigCategory general;
    public static ConfigEntryBuilder entryBuilder;

    public static Screen buildScreen (Screen currentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(currentScreen)
                .setTitle(new LiteralText("Point Menu"))
                .setTransparentBackground(true)
                .setSavingRunnable(() -> {
                    // CTPMod.points = pass;
                    CTPMod.config.save(); // Сохранить CTP.points в json
                });


        for(int i=0; i < CTPMod.points.length; i++) {
            // general - Это одна точка
            general = builder.getOrCreateCategory(new LiteralText(CTPMod.points[i].getName()));
            // entryBuilder - это билдер значения в точке
            entryBuilder = builder.entryBuilder();
            createOption(i);
        }
        Screen newScreen = builder.build();

        return newScreen;
    }

    public static void createOption(int PointIndex) {
        general.addEntry(entryBuilder.startStrField(new LiteralText("Point Name"), CTPMod.points[PointIndex].getName())
                .setDefaultValue(CTPMod.points[PointIndex].getName()) // Recommended: Used when user click "Reset"
                .setTooltip(new LiteralText("The name of current Point")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setName(newValue)) // Сохранить точку в CTPMod.points
                .build());
        general.addEntry(entryBuilder.startIntField(new LiteralText("Start Period Time"), CTPMod.points[PointIndex].getStartPeriod())
                .setDefaultValue(CTPMod.points[PointIndex].getStartPeriod())
                .setTooltip(new LiteralText("Amount of seconds before rejoining server"))
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setStartPeriod(newValue))
                .build());
        general.addEntry(entryBuilder.startIntField(new LiteralText("End Period Time"), CTPMod.points[PointIndex].getEndPeriod())
                .setDefaultValue(CTPMod.points[PointIndex].getEndPeriod())
                .setTooltip(new LiteralText("Amount of seconds after rejoining should not be executed"))
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setEndPeriod(newValue))
                .build());
    }
}
