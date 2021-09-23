package com.extclp.fabric.message.color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.*;

public class CustomMessageColorMod implements ModInitializer {

    public static Formatting color;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("message-color.json");

    @Override
    public void onInitialize() {
        if(Files.exists(CONFIG_PATH)){
            try {
                JsonObject data = new JsonParser().parse(Files.readString(CONFIG_PATH)).getAsJsonObject();
                color = Formatting.byName(data.getAsJsonPrimitive("color").getAsString());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        DISPATCHER.register(literal("message-color")
                        .executes(context -> resetMessageColor(context.getSource()))
            .then(argument("color", ColorArgumentType.color())
                .executes(context ->
                    execute(context.getSource(), context.getArgument("color", Formatting.class))
                )
            )
        );
    }

    public static int resetMessageColor(FabricClientCommandSource source){
       try {
           Files.deleteIfExists(CONFIG_PATH);
           CustomMessageColorMod.color = null;
           source.sendFeedback(new LiteralText("已重置私信字体的颜色"));
           return 1;
       }catch (IOException e){
           e.printStackTrace();
           return 0;
       }
    }

    public static int execute(FabricClientCommandSource source, Formatting color) {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(Map.of("color", color.getName())), StandardCharsets.UTF_8);
            CustomMessageColorMod.color = color;
            source.sendFeedback(new LiteralText("已修改私信字体的颜色"));
            return 1;
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void deepForEach(Text text, Consumer<Text> action){
        action.accept(text);
        for (Text sibling : text.getSiblings()) {
            deepForEach(sibling, action);
        }
    }
}
