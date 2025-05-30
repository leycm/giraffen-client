package org.leycm.giraffen;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.leycm.giraffen.commands.ModuleCommand;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;
import org.leycm.giraffen.module.modules.cosmetics.CapeLoaderModule;
import org.leycm.lang.TranslationHandler;
import org.leycm.storage.StorageRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiraffenClient implements ModInitializer {
	public static final String MOD_ID = "giraffen-client";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Giraffen");

	@Override
	public void onInitialize() {
		StorageRegistry.setup("config/" + MOD_ID, java.util.logging.Logger.getLogger(LOGGER.getName()));

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			Modules.startClient();
			CapeLoaderModule.cashExternalCapeTextures();
			ModuleCommand.register();
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			Modules.stopClient();
		});
	}
}