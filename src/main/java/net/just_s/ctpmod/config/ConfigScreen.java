package net.just_s.ctpmod.config;

import me.shedaniel.clothconfig2.api.*;
import net.just_s.ctpmod.CTPMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;


public class ConfigScreen {
    public static ConfigCategory mainCategory;
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

        // mainCategory - Это экран
        mainCategory = builder.getOrCreateCategory(new LiteralText("Null"));
        // entryBuilder - это билдер значения в точке
        entryBuilder = builder.entryBuilder();
        for(int i=0; i < CTPMod.points.length; i++) {
            createOption(i);
        }
        // Это потом позволит делать кейбинды
        // mainCategory.addEntry(entryBuilder.startModifierKeyCodeField().build());
        Screen screen = builder.build();

        return screen;
    }

    public static void createOption(int PointIndex) {
        List<AbstractConfigListEntry> listOfEntries = new ArrayList<>();
        listOfEntries.add(entryBuilder.startStrField(new LiteralText("Point Name"), CTPMod.points[PointIndex].getName())
                .setDefaultValue(CTPMod.points[PointIndex].getName()) // Recommended: Used when user click "Reset"
                .setTooltip(new LiteralText("The name of current Point")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setName(newValue)) // Сохранить точку в CTPMod.points
                .build());
        listOfEntries.add(entryBuilder.startIntField(new LiteralText("Start Period Time"), CTPMod.points[PointIndex].getStartPeriod())
                .setDefaultValue(CTPMod.points[PointIndex].getStartPeriod())
                .setTooltip(new LiteralText("Amount of seconds before rejoining server"))
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setStartPeriod(newValue))
                .build());
        listOfEntries.add(entryBuilder.startIntField(new LiteralText("End Period Time"), CTPMod.points[PointIndex].getEndPeriod())
                .setDefaultValue(CTPMod.points[PointIndex].getEndPeriod())
                .setTooltip(new LiteralText("Amount of seconds after rejoining should not be executed"))
                .setSaveConsumer(newValue -> CTPMod.points[PointIndex].setEndPeriod(newValue))
                .build());
        // startSubCategory - одна точка
        mainCategory.addEntry(entryBuilder.startSubCategory(new LiteralText(CTPMod.points[PointIndex].getName()), listOfEntries).build());
    }
}
