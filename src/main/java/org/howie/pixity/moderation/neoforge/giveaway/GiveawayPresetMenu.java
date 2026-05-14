package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public class GiveawayPresetMenu {

    public static void open(ServerPlayer p,
                            GiveawayService service,
                            GiveawayChatPromptService prompts) {

        SimpleContainer cont = new SimpleContainer(27);

        List<String> names =
                new ArrayList<>(service.getPresets().keySet());

        int slot = 10;

        for (String name : names) {

            ItemStack it = new ItemStack(Items.CHEST);
            it.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    Component.literal("§e" + name));

            cont.setItem(slot++, it);
        }

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        int index = slot - 10;
                        if (index < 0 || index >= names.size()) return;

                        String name = names.get(index);


                        if (button == 1 && type == ClickType.QUICK_MOVE) {
                            sp.closeContainer();
                            prompts.requestRename(sp, service, name);
                            return;
                        }


                        if (button == 0 && type == ClickType.QUICK_MOVE) {
                            service.deletePreset(name);

                            sp.sendSystemMessage(
                                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §cDeleted preset §e" + name)
                            );

                            open(sp, service, prompts);
                            return;
                        }


                        if (button == 1) {

                            service.loadPreset(name);

                            sp.sendSystemMessage(
                                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §eEditing preset §6" + name)
                            );

                            GiveawayGUI.open(sp, service, prompts);
                            return;
                        }


                        service.loadPreset(name);
                        sp.closeContainer();
                    }
                },
                Component.literal("§6Preset Selector")
        ));
    }
}