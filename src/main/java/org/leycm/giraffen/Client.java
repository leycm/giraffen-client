package org.leycm.giraffen;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import org.leycm.giraffen.command.commands.ModuleCommand;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.impl.cosmetics.CapeLoaderModule;
import org.leycm.giraffen.ui.ScreenHandler;
import org.leycm.storage.StorageRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ModInitializer {
	public static final String MOD_ID = "giraffenclient";
	public static MinecraftClient MC;
	private static boolean screenShown = false;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Giraffen");

	@Override
	public void onInitialize() {
		StorageRegistry.setup("config/" + MOD_ID, java.util.logging.Logger.getLogger(LOGGER.getName()));

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			MC = MinecraftClient.getInstance();

			Modules.startClient(64);

			ScreenHandler.startClient();
			CapeLoaderModule.cashExternalCapeTextures();
			ModuleCommand.register();
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