package org.leycm.giraffe.client;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import org.leycm.giraffe.client.command.commands.ModuleCommand;
import org.leycm.giraffe.client.command.commands.ScreenCommand;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.modules.cosmetics.CapeLoaderModule;
import org.leycm.giraffe.client.ui.ScreenHandler;
import org.leycm.storage.StorageRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ModInitializer {
	public static final String MOD_ID = "giraffenclient";
	public static MinecraftClient MC;
	private static boolean screenShown = false;

	public static final Logger LOGGER = LoggerFactory.getLogger("Giraffen");

    public static boolean isScreenShown() {
        return screenShown;
    }

    public static void setScreenShown(boolean screenShown) {
        Client.screenShown = screenShown;
    }

    @Override
	public void onInitialize() {
		StorageRegistry.setup("config/" + MOD_ID, java.util.logging.Logger.getLogger(LOGGER.getName()));


		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			MC = MinecraftClient.getInstance();

			Modules.startClient(64);

			ScreenHandler.startClient();
			ModuleCommand.register();
			ScreenCommand.register();
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			Modules.saveClient();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			Modules.saveClient();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ScreenHandler.run();
		});

	}
}