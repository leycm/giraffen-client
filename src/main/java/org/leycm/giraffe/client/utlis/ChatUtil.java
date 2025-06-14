package org.leycm.giraffe.client.utlis;


import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ChatUtil {

    public enum Type{
        ERROR, WARNING, INFO, IMPORTANT
    }

    public static void sendMessage(String message, @NotNull Type type) {
        String prefix = "";
        switch (type) {
            case ERROR -> prefix = "§4[§f⚀§4] §c";
            case WARNING -> prefix = "§6[§f⚀§6] §e";
            case INFO -> prefix = "§8[§f⚀§8] §7";
            case IMPORTANT -> prefix = "§8[§f⚀§8] §f";
        }

        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.literal(prefix + message), false);
    }

    public static void sendMessage(@NotNull Text text, Type type) {
        sendMessage(text.toString(), type);
    }


}
