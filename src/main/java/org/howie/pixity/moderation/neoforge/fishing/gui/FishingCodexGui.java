package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class FishingCodexGui {

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(27);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x3, id, inv, cont, 3
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

                filler.set(
                        DataComponents.CUSTOM_NAME,
                        Component.literal("")
                );


                for (int i = 0; i <= 8; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                for (int i = 18; i <= 26; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                this.getSlot(9).set(filler.copy());
                this.getSlot(17).set(filler.copy());




                ItemStack back = new ItemStack(Items.BARRIER);

                back.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>")
                );

                this.getSlot(22).set(back);

                if (this.getSlot(10).hasItem()) return;

                setTier(10, FishTier.BRONZE,
                        "<gray>The most common fish on the server</gray>");

                setTier(11, FishTier.SILVER,
                        "<gray>Harder to catch than</gray> &#cd7f32&lBRONZE <gray>fish, but</gray> <green>worth more</green><gray>.</gray>");

                setTier(12, FishTier.GOLD,
                        "<gray>Much rarer than </gray>&#bdbdbd&lSILVER <gray>tiered fish, but</gray> <green>worth more</green> <gray>as well.</gray>");

                setTier(14, FishTier.DIAMOND,
                        "<gray>Rarer than</gray> &#f5f788&lGOLD <gray>tiered fish, but</gray> <green>worth more</green><gray> as well.</gray>");

                setTier(15, FishTier.PLATINUM,
                        "<gray>Significantly</gray> <red>rarer</red> <gray>than</gray> &#57f2e8&lDIAMOND <gray>tiered fish.</gray>");

                setTier(16, FishTier.MYTHICAL,
                        "<gray>The rarest fish of them all, </gray><red>exceptionally valuable.</red>");
            }

            private void setTier(int slot, FishTier tier, String description) {

                ItemStack item = getBall(tier);

                String color = tier.getColor();

                item.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(color + "&l" + tier.name() + " FISH"));

                List<Component> lore = new ArrayList<>();

                lore.add(TextFormatter.parse(description));
                lore.add(Component.empty());

                long total = FishingManager.FISH.stream()
                        .filter(f -> f.tier == tier)
                        .count();

                lore.add(TextFormatter.parse(
                        "&7Total " +
                                color + "&l" + tier.name() +
                                " &r&7Fish: <yellow>" + total + "</yellow>"
                ));

                lore.add(Component.empty());


                lore.add(Component.empty());

                String rarity = switch (tier) {
                    case BRONZE -> "<gray>Common</gray>";
                    case SILVER -> "<white>Uncommon</white>";
                    case GOLD -> "<yellow>Rare</yellow>";
                    case DIAMOND -> "&bEpic";
                    case PLATINUM -> "&dLegendary";
                    case MYTHICAL -> "&c&lMythical";
                };

                lore.add(TextFormatter.parse("<gray>Rarity:</gray> " + rarity));

                lore.add(TextFormatter.parse("<yellow>Click to view fish</yellow>"));

                item.set(DataComponents.LORE, new ItemLore(lore));

                this.getSlot(slot).set(item);
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {



                if (type != ClickType.PICKUP) return;

                if (!(p instanceof ServerPlayer sp)) return;

                if (slot >= 27) return;




                if (slot == 22) {

                    FishingMainMenu.open(sp);
                    return;
                }

                FishTier tier = switch (slot) {
                    case 10 -> FishTier.BRONZE;
                    case 11 -> FishTier.SILVER;
                    case 12 -> FishTier.GOLD;
                    case 14 -> FishTier.DIAMOND;
                    case 15 -> FishTier.PLATINUM;
                    case 16 -> FishTier.MYTHICAL;
                    default -> null;
                };

                if (tier != null) {
                    FishingCodexTierGui.open(sp, tier);
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Codex</gold>")
        ));
    }

    private static ItemStack getBall(FishTier tier) {

        String id = switch (tier) {
            case BRONZE -> "cobblemon:poke_ball";
            case SILVER -> "cobblemon:great_ball";
            case GOLD -> "cobblemon:ultra_ball";
            case DIAMOND -> "cobblemon:beast_ball";
            case PLATINUM -> "cobblemon:master_ball";
            case MYTHICAL -> "cobblemon:ancient_origin_ball";
        };

        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));

        if (item == null) {
            return new ItemStack(Items.BARRIER);
        }

        return new ItemStack(item);
    }
}
