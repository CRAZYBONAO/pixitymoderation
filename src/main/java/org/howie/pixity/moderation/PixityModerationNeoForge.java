package org.howie.pixity.moderation;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.sql.Connection;

// CHAT
import org.howie.pixity.moderation.chat.*;
import org.howie.pixity.moderation.neoforge.announce.AnnouncementsCommands;
import org.howie.pixity.moderation.neoforge.announce.AnnouncementsConfigStore;
import org.howie.pixity.moderation.neoforge.announce.AnnouncementsService;
import org.howie.pixity.moderation.neoforge.announce.AnnouncementsTicker;
import org.howie.pixity.moderation.neoforge.auctionhouse.*;
import org.howie.pixity.moderation.neoforge.automod.*;
import org.howie.pixity.moderation.neoforge.chat.ChatListener;
import org.howie.pixity.moderation.neoforge.chat.NickCommands;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.*;
import org.howie.pixity.moderation.neoforge.chatcontrol.*;

// ALTS
import org.howie.pixity.moderation.neoforge.alts.*;
import org.howie.pixity.moderation.neoforge.alts.smart.*;

// CORE
import org.howie.pixity.moderation.neoforge.chatextras.*;
import org.howie.pixity.moderation.neoforge.chatgames.*;
import org.howie.pixity.moderation.neoforge.combat.CombatTagListener;
import org.howie.pixity.moderation.neoforge.combat.CombatTagService;
import org.howie.pixity.moderation.neoforge.crate.*;
import org.howie.pixity.moderation.neoforge.fishing.*;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryRewardManager;
import org.howie.pixity.moderation.neoforge.fishing.events.FishingEventManager;
import org.howie.pixity.moderation.neoforge.freeze.*;
import org.howie.pixity.moderation.neoforge.giveaway.*;
import org.howie.pixity.moderation.neoforge.inspect.*;
import org.howie.pixity.moderation.neoforge.invsee.InvSeeCommands;
import org.howie.pixity.moderation.neoforge.item.ItemMetaCommands;
import org.howie.pixity.moderation.neoforge.jail.*;
import org.howie.pixity.moderation.neoforge.joinleave.*;
import org.howie.pixity.moderation.neoforge.kits.KitManager;
import org.howie.pixity.moderation.neoforge.kits.KitsBootstrap;
import org.howie.pixity.moderation.neoforge.kits.KitsCommands;
import org.howie.pixity.moderation.neoforge.kits.firstjoin.FirstJoinListener;
import org.howie.pixity.moderation.neoforge.kits.firstjoin.FirstJoinService;
import org.howie.pixity.moderation.neoforge.kits.firstjoin.FirstJoinStore;
import org.howie.pixity.moderation.neoforge.mail.*;
import org.howie.pixity.moderation.neoforge.milestones.core.*;
import org.howie.pixity.moderation.neoforge.milestones.listeners.*;
import org.howie.pixity.moderation.neoforge.miningevents.MiningEventCommand;
import org.howie.pixity.moderation.neoforge.miningevents.MiningEventListener;
import org.howie.pixity.moderation.neoforge.miningevents.MiningEventTask;
import org.howie.pixity.moderation.neoforge.mod.ModCommands;
import org.howie.pixity.moderation.neoforge.mod.ModGui;
import org.howie.pixity.moderation.neoforge.msg.*;
import org.howie.pixity.moderation.neoforge.notes.*;
import org.howie.pixity.moderation.neoforge.playtime.*;
import org.howie.pixity.moderation.neoforge.playtime.gui.PlaytimeLevelsMenu;
import org.howie.pixity.moderation.neoforge.pokemon.*;
import org.howie.pixity.moderation.neoforge.pokemonhunt.PokemonHuntCommand;
import org.howie.pixity.moderation.neoforge.pokemonhunt.PokemonHuntJoinListener;
import org.howie.pixity.moderation.neoforge.pokemonhunt.PokemonHuntListener;
import org.howie.pixity.moderation.neoforge.pokemonhunt.PokemonHuntManager;
import org.howie.pixity.moderation.neoforge.profile.ProfileCommands;
import org.howie.pixity.moderation.neoforge.punish.*;
import org.howie.pixity.moderation.neoforge.punish.gui.PunishGuiCommands;
import org.howie.pixity.moderation.neoforge.queue.*;
import org.howie.pixity.moderation.neoforge.rank.RankHolder;
import org.howie.pixity.moderation.neoforge.rankup.RankupCommand;
import org.howie.pixity.moderation.neoforge.reports.*;
import org.howie.pixity.moderation.neoforge.rollback.*;
import org.howie.pixity.moderation.neoforge.rules.*;
import org.howie.pixity.moderation.neoforge.shop.*;
import org.howie.pixity.moderation.neoforge.skills.*;
import org.howie.pixity.moderation.neoforge.skills.command.PixitySkillsCommand;
import org.howie.pixity.moderation.neoforge.spawners.*;
import org.howie.pixity.moderation.neoforge.staff.StaffModeCommands;
import org.howie.pixity.moderation.neoforge.staff.StaffModeService;
import org.howie.pixity.moderation.neoforge.staff.StaffToolListener;
import org.howie.pixity.moderation.neoforge.staff.dashboard.StaffDashboardCommands;
import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;
import org.howie.pixity.moderation.neoforge.tp.*;
import org.howie.pixity.moderation.neoforge.tp.gui.*;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.jail.SQLiteJailStore;
import org.howie.pixity.moderation.neoforge.milestones.command.MilestonesCommand;

// TAB
import org.howie.pixity.moderation.neoforge.tab.TabListTicker;
import org.howie.pixity.moderation.neoforge.voucher.VoucherCommand;
import org.howie.pixity.moderation.neoforge.voucher.VoucherListener;
import org.howie.pixity.moderation.neoforge.voucher.VoucherManager;
import org.howie.pixity.moderation.neoforge.worldboss.*;
import org.howie.pixity.moderation.tab.TabListConfigManager;
import org.howie.pixity.moderation.neoforge.economy.*;
import org.howie.pixity.moderation.neoforge.fly.*;

// STATE
import org.howie.pixity.moderation.neoforge.state.*;

// AFK
import org.howie.pixity.moderation.neoforge.afk.*;

@Mod("pixitymoderation")
public final class PixityModerationNeoForge {

    public static PixityModerationNeoForge INSTANCE;

    private SQLiteSkillsStore skillsStore;
    private SkillService skillService;
    public static StatEngine STAT_ENGINE;
    public static AbilityEngine ABILITY_ENGINE;
    public static SkillService SKILL_SERVICE;
    public static ActiveAbilityManager ACTIVE_ABILITIES;
    public static EconomyService ECONOMY_SERVICE;
    public static MilestonePlayerService MILESTONE_PLAYERS;




    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {

        MinecraftServer server = event.getServer();

        NickHolder.INSTANCE =
                new NickManager(server.getServerDirectory());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {

        MinecraftServer server = event.getServer();

        org.howie.pixity.moderation.neoforge.voucher.VoucherManager.init(server);

        skillsStore = new SQLiteSkillsStore(
                LOGGER,
                "jdbc:sqlite:./config/pixity/skills.db"
        );

        skillService = new SkillService(LOGGER, skillsStore);

// CRITICAL FIX
        SKILL_SERVICE = skillService;

// engines
        STAT_ENGINE = new StatEngine(SKILL_SERVICE);
        ABILITY_ENGINE = new AbilityEngine(SKILL_SERVICE);
        ACTIVE_ABILITIES = new ActiveAbilityManager();

// register autosave
        NeoForge.EVENT_BUS.register(SKILL_SERVICE);

// XP system
        SkillXpListener.init(SKILL_SERVICE);

// listeners
        NeoForge.EVENT_BUS.register(CombatListener.class);
        new TrainerListener(SKILL_SERVICE);
        NeoForge.EVENT_BUS.register(new AbilityListener());
        new ProfessorListener(PixityModerationNeoForge.SKILL_SERVICE);
        new ProfessorCatchListener(SKILL_SERVICE);
        WorldBossBattleListener.register();
        WorldBossBattleHandler.register();
        WorldBossBattleDebug.register();
        WorldBossCatchListener.register();
        server.getAllLevels().forEach(level ->
                CrateHologramService.reloadAll(level)
        );

        NeoForge.EVENT_BUS.register(
                new WorldBossJoinListener()
        );

        WorldBossSpawnStorage.load(
                event.getServer()
        );
        WorldBossPersistence.load(server);

        WorldBossManager.findExistingBoss(server);

        NeoForge.EVENT_BUS.register(
                new WorldBossDropsListener()
        );



        // =========================
        // 🏆 POKEDEX HOLOGRAM REFRESH LOOP
        // =========================

        DeliveryRewardManager.init();

        ChatGamesConfig.load();
        ChatGamesDatabase.init();

        FishingEventManager.init(server, economy);

        AuctionConfig.load();

        PokedexDatabase.init();

        PokemonCatchListener.register();
        HologramManager.loadCache();

        AuctionDatabase.load();
        AuctionExpiryScheduler.start(server);


        PokemonHuntManager.generateNewHunt(
                server
        );


// also load existing listings into queue
        for (var l : AuctionDatabase.getActive()) {
            AuctionExpiryScheduler.schedule(server, l);
        }
    }

    @SubscribeEvent
    public void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post e) {

        // run every second
        if (e.getServer().getTickCount() % 20 != 0) return;

        FishingEventManager.tick();

        HologramManager.queueRefresh(e.getServer());

        ChatGameManager.tick(e.getServer().overworld());

        MiningEventTask.tick(
                e.getServer()
        );

        long now = System.currentTimeMillis();

        if (now - lastChatGame >= ChatGamesConfig.intervalSeconds * 1000L) {

            lastChatGame = now;

            if (!ChatGameManager.isActive()) {
                ChatGameManager.startRound(e.getServer().overworld());
            }
        }



    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppingEvent event) {
        AuctionDatabase.saveAsync(); // or saveSync if you want guaranteed write
    }

    public static PixityModerationNeoForge getInstance() {
        return INSTANCE;
    }

    private static boolean running = false;
    private long lastChatGame = 0;



    public static final Logger LOGGER = LogManager.getLogger();

    private final TpService tp;
    private final TpaService tpa;
    private final TeleportWarmupManager warmup;
    private final TpChatPromptService prompts;
    private final RankService ranks;


    private final AfkService afk;
    private final AltsService alts;
    private final SmartAltService smartAlts;
    private final PlaytimeService playtime;
    private final PunishmentManager punish;
    private final FreezeService freeze;
    private final JailService jail;
    private final MuteManager mutes;
    private final AnnouncementsService announce;
    private final AnnouncementsConfigStore announceStore;
    private final ChatControlService chatCtl;
    private final ChatControlConfigStore chatCtlStore;
    private final ChatExtrasService chatExtras;
    private final ChatExtrasConfigStore chatExtrasStore;
    private final MsgService msg;
    private final IgnoreManager ignores;
    private final SocialSpyManager spy;
    private final ReplyManager reply;
    private final RulesService rules;
    private final RulesConfigManager rulesCfg;
    private final SQLiteRulesSeenStore rulesStore;
    private final MailService mail;
    private final MailConfigStore mailCfgStore;
    private final MailConfig mailCfg;
    private final SQLiteMailStore mailStore;
    private final JoinLeaveService joinLeave;
    private final JoinLeaveConfigManager joinLeaveCfg;
    private final SQLiteJoinLeaveStore joinLeaveStore;
    private final InspectService inspect;
    private final InspectConfig inspectCfg;
    private final InspectConfigStore inspectCfgStore;
    private final SQLiteBlockLogStore inspectDb;
    private final RollbackService rollback;
    private final RollbackConfig rollbackCfg;
    private final RollbackConfigStore rollbackCfgStore;
    private final RollbackStore rollbackStore;
    private final StaffModeService staffMode;
    private final GiveawayService giveaway;
    private final KitsBootstrap kitsBootstrap;
    private final KitManager kits;
    private final FirstJoinService firstJoin;
    public final EconomyService economy;
    private final QueueService queue;
    private final QueueConfigStore queueCfgStore;
    private final QueueConfig queueCfg;
    private PlayerStateManager states;
    private PlayerStateService stateService;
    private SQLitePlayerStateStore stateStore;
    private CombatTagService combat;
    private AutoModService autoMod;
    private final AutoModConfig autoModCfg;
    private final AutoModConfigStore autoModStore;
    private final TabListTicker tab;
    private TabListConfigManager tabCfg;
    private final PunishGuiCommands punishGuiCommands;
    private final MsgCommands msgCommands;
    private final FlyTimeService fly;
    private NickManager nickMgr;
    private GiveawayChatPromptService giveawayPrompts;
    ChatCosmeticsConfig cosmeticsConfig = new ChatCosmeticsConfig();
    private CosmeticsGui cosmeticsGui;
    private CosmeticsCommands cosmeticsCommands;
    private ChatCosmeticsService cosmeticsService;
    private PlaytimeService playtimeService;
    private SQLitePlaytimeStore playtimeStore;
    private List<PlaytimeLevelsMenu.Level> levels;
    private SpawnerStackService stacks;
    private SpawnerConfig spawnerConfig;
    private SpawnerDatabase spawnerDb;
    private ShopService shopService;
    private EconomyBridge econ;
    private AuctionCommands auctionCommands;
    public static org.howie.pixity.moderation.neoforge.rank.RankService RANK_SERVICE;





    private final NotesService notes;
    private final ReportsService reports;

    public PixityModerationNeoForge() {

        INSTANCE = this;
        LOGGER.info("Starting PixityModeration...");

        Path dataDir = Path.of("config", "pixity");

        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data directory", e);
        }

        ranks = new RankService();
        RankHolder.INSTANCE = ranks;
        RANK_SERVICE = new org.howie.pixity.moderation.neoforge.rank.RankService();

        nickMgr = new NickManager(Path.of("config", "pixity"));
        NickHolder.INSTANCE = nickMgr;


        // =========================
// MAIL
// =========================



        mailCfgStore =
                new MailConfigStore(LOGGER, dataDir);

        mailCfg = mailCfgStore.loadOrCreate();

        mailStore =
                new SQLiteMailStore(
                        LOGGER,
                        dataDir
                );

        mail =
                new MailService(
                        LOGGER,
                        mailCfg,
                        mailStore
                );

        NeoForge.EVENT_BUS.register(
                new MailJoinListener(mail)
        );

        SQLiteFishingStore fishingStore =
                new SQLiteFishingStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("fishing.db")
                );


        MilestoneDefinitionRegistry.init();
        MilestoneRegistry.init();

        SQLiteMilestoneStore milestoneStore =
                new SQLiteMilestoneStore(
                        LOGGER,
                        dataDir.toFile()
                );

        MILESTONE_PLAYERS =
                new MilestonePlayerService(
                        LOGGER,
                        milestoneStore
                );

        NeoForge.EVENT_BUS.register(MILESTONE_PLAYERS);

        PlayerStatsDatabase.init(
                "jdbc:sqlite:config/pixity/player_stats.db"
        );

        NeoForge.EVENT_BUS.register(new FishingMobListener());

        NeoForge.EVENT_BUS.register(
                MiningMilestoneListener.class
        );

        NeoForge.EVENT_BUS.register(
                FarmingMilestoneListener.class
        );

        NeoForge.EVENT_BUS.register(
                MobMilestoneListener.class
        );

        NeoForge.EVENT_BUS.register(
                MiningEventListener.class
        );

        TrainerMilestoneListener.register();

        ProfessorMilestoneListener.register();
        // =========================
// RULES
// =========================

        rulesCfg =
                new RulesConfigManager(
                        LOGGER,
                        dataDir.resolve("rules.json")
                );

        rulesStore =
                new SQLiteRulesSeenStore(
                        LOGGER,
                        dataDir
                );

        rules =
                new RulesService(
                        rulesCfg,
                        rulesStore,
                        ranks
                );

        NeoForge.EVENT_BUS.register(
                new RulesJoinListener(rules)
        );






        // =========================
        // NOTES
        // =========================
        SQLiteNotesStore notesStore = new SQLiteNotesStore(LOGGER, dataDir);
        notes = new NotesService(LOGGER, notesStore);

        // =========================
        // REPORTS
        // =========================
        ReportsStore reportsStore = new ReportsStore(LOGGER, dataDir);
        reports = new ReportsService(LOGGER, reportsStore, ranks);

        // =========================
// PLAYTIME
// =========================

        playtimeStore =
                new SQLitePlaytimeStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("playtime.db")
                );

        playtime = new PlaytimeService(playtimeStore);

        NeoForge.EVENT_BUS.register(playtime);

        PlaytimeLevelsConfig levelsConfig =
                new PlaytimeLevelsConfig(dataDir);

        levels = levelsConfig.getLevels();





        // =========================
        // ECONOMY
        // =========================

        SQLiteEconomyStore econStore =
                new SQLiteEconomyStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("economy.db")
                );

        economy = new EconomyService(LOGGER, econStore);

        RewardExecutor.init(economy);

        // =========================
// SPAWNERS
// =========================
        PokePartyConfig.load();

        spawnerConfig =
                SpawnerConfig.load(
                        dataDir.resolve("spawners.json")
                );

        spawnerDb =
                new SpawnerDatabase(
                        "jdbc:sqlite:" + dataDir.resolve("spawners.db")
                );

        stacks =
                new SpawnerStackService(
                        spawnerDb,
                        spawnerConfig
                );

        NeoForge.EVENT_BUS.register(
                new SpawnerListener(ranks, stacks)
        );

        NeoForge.EVENT_BUS.register(
                new SpawnerSpawnListener(stacks)
        );

        NeoForge.EVENT_BUS.register(
                new SpawnerLoadListener(stacks)
        );

        NeoForge.EVENT_BUS.register(
                new MobStackMergeListener()
        );

        NeoForge.EVENT_BUS.register(
                new MobStackDeathListener()
        );

        NeoForge.EVENT_BUS.register(
                new MobStackHologramFollowListener()
        );

        PokedexDatabase.init();

        PokedexRewardConfig.load();

        NeoForge.EVENT_BUS.register(new ChatGamesListener());


        // =========================
        // FLY
        // =========================

        SQLiteFlyTimeStore flyStore =
                new SQLiteFlyTimeStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("flytime.db")
                );

        fly = new FlyTimeService(flyStore, ranks);

        NeoForge.EVENT_BUS.register(fly);
        NeoForge.EVENT_BUS.register(new FlyTimeListener(fly));


        // =========================
        // AFK
        // =========================

        AfkConfigStore afkConfigStore =
                new AfkConfigStore(LOGGER, dataDir);

        AfkConfig afkConfig = afkConfigStore.loadOrCreate();
        afkConfig.autoAfkMinutes = 15;

        SQLiteAfkStore afkStore =
                new SQLiteAfkStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("afk.db")
                );

        afk = new AfkService(afkConfig, ranks, afkStore);

        AfkHolder.INSTANCE = afk;

        NeoForge.EVENT_BUS.register(new AfkListener(afk));



        // =========================
        // TP
        // =========================

        SQLiteTpStore tpStore =
                new SQLiteTpStore(LOGGER, dataDir);

        tp = new TpService(tpStore, ranks);

        SQLiteBackStore backStore =
                new SQLiteBackStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("back.db")
                );

        BackService.init(LOGGER, backStore);

        //
        // GIVEAWAY PROMPTS
        //

        giveawayPrompts = new GiveawayChatPromptService();
        NeoForge.EVENT_BUS.register(
                new GiveawayChatListener(giveawayPrompts)
        );
        NeoForge.EVENT_BUS.register(new PokeGiveHook());

        // =========================
        // WARMUP
        // =========================

        WarmupConfigManager warmCfg =
                new WarmupConfigManager(
                        LOGGER,
                        dataDir.resolve("warmup.json")
                );

        CombatTagService combat = new CombatTagService(10);

        warmup = new TeleportWarmupManager(
                warmCfg,
                ranks,
                tp,
                combat
        );

        NeoForge.EVENT_BUS.register(new TeleportWarmupListener(warmup));


        // =========================
        // TPA
        // =========================

        TpaStore tpaStore =
                new TpaStore(LOGGER, dataDir.resolve("tptoggle.json"));

        tpa = new TpaService(
                ranks,
                tp,
                warmup,
                tpaStore,
                afk
        );
        //
         // FISHING
         //

        NeoForge.EVENT_BUS.register(new FishingListener());


        // =========================
// ALTS
// =========================

        SQLiteAltsStore altsStore =
                new SQLiteAltsStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("alts.db")
                );

        alts = new AltsService(LOGGER, altsStore);


// =========================
// SMART ALTS
// =========================

        SQLiteSmartAltStore smartStore =
                new SQLiteSmartAltStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("smart_alts.db")
                );

        smartAlts = new SmartAltService(LOGGER, smartStore);

// REGISTER LISTENER (IMPORTANT)
        NeoForge.EVENT_BUS.register(
                new SmartAltListener(smartAlts, ranks)
        );

        // =========================
        // CHAT CONTROL
        // =========================

        chatCtlStore =
                new ChatControlConfigStore(LOGGER, dataDir);

        chatCtl =
                new ChatControlService(
                        chatCtlStore.loadOrCreate()
                );

        // =========================
        // CHAT EXTRAS
        // =========================

        chatExtrasStore =
                new ChatExtrasConfigStore(LOGGER, dataDir);

        chatExtras =
                new ChatExtrasService(
                        chatExtrasStore.loadOrCreate(),
                        ranks
                );

        ChatConfigManager chatCfg =
                new ChatConfigManager(LOGGER, dataDir.resolve("chat.json"));

        mutes =
                new MuteManager(LOGGER, dataDir.resolve("mutes.json"));


        // =========================
        // MSG
        // =========================

        ignores =
                new IgnoreManager(
                        LOGGER,
                        dataDir.resolve("ignores.json")
                );

        spy = new SocialSpyManager();
        reply = new ReplyManager();

        msg =
                new MsgService(
                        ignores,
                        spy,
                        reply,
                        mutes,
                        ranks,
                        nickMgr
                );

        this.msgCommands =
                new MsgCommands(
                        LOGGER,
                        msg
                );



        // =========================
        // PUNISHMENTS
        // =========================

        SQLitePunishStore punishStore =
                new SQLitePunishStore(LOGGER, dataDir);

        punish = new PunishmentManager(
                LOGGER,
                punishStore,
                ranks
        );

        NeoForge.EVENT_BUS.register(
                new BanEnforcementListener(punish)
        );

        // =========================
        // FREEZE
        // =========================

        FreezeConfigStore freezeCfgStore =
                new FreezeConfigStore(LOGGER, dataDir);

        FreezeStore freezeStore =
                new FreezeStore(LOGGER, dataDir);

        freeze = new FreezeService(
                freezeStore,
                freezeCfgStore.loadOrCreate(),
                punish,
                ranks
        );



        // =========================
        // JAIL
        // =========================

        SQLiteJailStore jailStore =
                new SQLiteJailStore(
                        LOGGER,
                        dataDir.resolve("jail.db").toString()
                );

        JailConfigStore jailCfgStore =
                new JailConfigStore(
                        LOGGER,
                        dataDir.resolve("jail_config.json")
                );

        jail = new JailService(
                jailStore,
                jailCfgStore.loadOrCreateDefault(),
                tp,
                warmup,
                punish,
                ranks
        );

        this.punishGuiCommands =
                new PunishGuiCommands(
                        punish,
                        mutes,
                        freeze,
                        jail,
                        ranks
                );


        // =========================
// JOIN / LEAVE
// =========================

        joinLeaveCfg =
                new JoinLeaveConfigManager(
                        LOGGER,
                        dataDir.resolve("joinleave.json")
                );

        joinLeaveStore =
                new SQLiteJoinLeaveStore(
                        LOGGER,
                        dataDir
                );

        joinLeave =
                new JoinLeaveService(
                        joinLeaveCfg,
                        joinLeaveStore,
                        ranks,
                        nickMgr,
                        economy,
                        fly
                );

        NeoForge.EVENT_BUS.register(
                new JoinLeaveListener(joinLeave)
        );

        // =========================
// AUTOMOD
// =========================

        autoModStore =
                new AutoModConfigStore(
                        LOGGER,
                        dataDir
                );

        autoModCfg =
                autoModStore.loadOrCreate();

        autoMod =
                new AutoModService(
                        autoModCfg,
                        tp,
                        mutes,
                        punish
                );

        // =========================
        // ANNOUNCEMENTS
        // =========================

        announceStore =
                new AnnouncementsConfigStore(LOGGER, dataDir);

        announce =
                new AnnouncementsService(
                        announceStore.loadOrCreate(),
                        tp
                );

        NeoForge.EVENT_BUS.register(new AnnouncementsTicker(announce));

        prompts = new TpChatPromptService(tp, warmup);
        NeoForge.EVENT_BUS.register(new TpChatListener(prompts));

        // =========================
// COSMETICS
// =========================

        AnimatedChatManager animatedChat = new AnimatedChatManager();

        CosmeticsStorage cosmeticsStorage =
                new CosmeticsStorage(dataDir.resolve("cosmetics.db").toString());

        this.cosmeticsService =
                new ChatCosmeticsService(cosmeticsStorage, cosmeticsConfig, ranks);

        this.shopService = new ShopService();
        this.shopService.load();


        NeoForge.EVENT_BUS.register(new CrateAuraTicker());

        CrateManager.load();
        CrateBlockManager.load();
        CrateAuraManager.load();
        VoucherManager.load();

// ✅ FIRST load cosmetics into config
        loadCosmetics();

// ✅ THEN generate shops from it
        this.shopService.generateCosmetics(cosmeticsConfig);

        this.econ = new EconomyBridge(economy);

        cosmeticsGui =
                new CosmeticsGui(
                        cosmeticsService,
                        cosmeticsConfig,
                        economy,
                        ranks,
                        econ,
                        shopService
                );

        cosmeticsCommands = new CosmeticsCommands(
                cosmeticsService,
                cosmeticsGui
        );

        NeoForge.EVENT_BUS.register(
                new CosmeticsJoinListener(cosmeticsService)
        );









        NeoForge.EVENT_BUS.register(
                new ChatListener(
                        chatCfg,
                        mutes,
                        afk,
                        autoMod,
                        chatCtl,
                        chatExtras,
                        freeze,
                        jail,
                        economy,
                        fly,
                        playtime,
                        ignores,
                        ranks,
                        cosmeticsService
                )
        );

        // =========================
// INSPECT
// =========================

        inspectCfgStore =
                new InspectConfigStore(
                        LOGGER,
                        dataDir
                );

        inspectCfg =
                inspectCfgStore.loadOrCreate();

        inspectDb =
                new SQLiteBlockLogStore(
                        LOGGER,
                        dataDir
                );

        inspect =
                new InspectService(
                        LOGGER,
                        inspectCfg,
                        inspectDb,
                        ranks
                );

        NeoForge.EVENT_BUS.register(
                new InspectListener(inspect)
        );

        // =========================
// ROLLBACK
// =========================

        rollbackCfgStore =
                new RollbackConfigStore(
                        LOGGER,
                        dataDir
                );

        rollbackCfg =
                rollbackCfgStore.loadOrCreate();

        rollbackStore =
                new RollbackStore(
                        LOGGER,
                        dataDir
                );

        rollback =
                new RollbackService(
                        LOGGER,
                        rollbackCfg,
                        rollbackStore,
                        ranks
                );

        NeoForge.EVENT_BUS.register(
                new RollbackListener(rollback)
        );

        // =========================
// PLAYER STATES
// =========================

        stateStore =
                new SQLitePlayerStateStore(
                        LOGGER,
                        "jdbc:sqlite:" + dataDir.resolve("player_states.db")
                );

        states =
                new PlayerStateManager(
                        stateStore,
                        ranks
                );

        stateService =
                new PlayerStateService(
                        states,
                        freeze,
                        jail,
                        mutes,
                        afk
                );

        NeoForge.EVENT_BUS.register(
                new PlayerStateListener(states, ranks)
        );

        NeoForge.EVENT_BUS.register(
                new StateEnforcementListener(stateService)
        );

        // =========================
// STAFF MODE
// =========================

        staffMode =
                new StaffModeService(
                        states,
                        ranks
                );

        NeoForge.EVENT_BUS.register(
                new StaffToolListener(
                        staffMode,
                        freeze,
                        ranks
                )
        );

        // =========================
// GIVEAWAY
// =========================

        giveaway = new GiveawayService(dataDir);

        NeoForge.EVENT_BUS.register(
                new GiveawayTicker(giveaway)
        );



        // =========================
// KITS
// =========================

        kitsBootstrap =
                new KitsBootstrap(
                        LOGGER,
                        dataDir,
                        ranks,
                        economy
                );

        kits = kitsBootstrap.getManager();

        FirstJoinStore firstJoinStore =
                new FirstJoinStore(
                        LOGGER,
                        dataDir
                );

        firstJoin =
                new FirstJoinService(
                        kits,
                        ranks,
                        firstJoinStore
                );

        NeoForge.EVENT_BUS.register(
                new FirstJoinListener(firstJoin)
        );

        // =========================
// QUEUE
// =========================

        queueCfgStore =
                new QueueConfigStore(
                        LOGGER,
                        dataDir
                );

        queueCfg =
                queueCfgStore.loadOrCreate();

        queue =
                new QueueService(
                        queueCfg,
                        ranks
                );

        NeoForge.EVENT_BUS.register(
                new QueueListener(queue)
        );

        NeoForge.EVENT_BUS.register(
                new QueueDisconnectListener(queue)
        );

        //
         // COSMETICS
         //

        NeoForge.EVENT_BUS.register(new CosmeticListener());

        //
        // VOUCHER AND CRATE
        //

        NeoForge.EVENT_BUS.register(new VoucherListener());
        NeoForge.EVENT_BUS.register(new CrateListener());



        // =========================
// COMBAT TAG
// =========================

        combat =
                new CombatTagService(15);

        NeoForge.EVENT_BUS.register(
                new CombatTagListener(combat)
        );



        // =========================
// TAB + SIDEBAR
// =========================

        tabCfg =
                new TabListConfigManager(
                        LOGGER,
                        dataDir.resolve("tablist.json")
                );

        tab =
                new TabListTicker(
                        tabCfg,      // TabListConfigManager
                        ranks,
                        states,
                        afk,
                        nickMgr,
                        economy,
                        fly,
                        cosmeticsService
                );



        NeoForge.EVENT_BUS.register(new FreezeListener(freeze));
        NeoForge.EVENT_BUS.register(new JailMovementListener(jail));





        NeoForge.EVENT_BUS.register(tab);
        NeoForge.EVENT_BUS.register(new ShinyBoostListener());
        NeoForge.EVENT_BUS.register(new LegendarySpawnListener());
        NeoForge.EVENT_BUS.register(new PlayerJoinListener());
        NeoForge.EVENT_BUS.register(
                new PokemonHuntJoinListener()
        );
        PokemonCatchListener.register();
        PokemonHuntListener.register();






        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent e) {

        var d = e.getDispatcher();

        new TpCommands(tp, warmup, ranks).register(d);
        new TpGuiCommands(tp, warmup, prompts, ranks).register(d);
        new TpaCommands(tpa, warmup, ranks).register(d);
        new PlaytimeCommands(playtime).register(d);
        FishingCommands.register(d);


        auctionCommands = new AuctionCommands(economy);
        auctionCommands.register(d);
        AuctionManager.ECON = economy;

        ChatGamesCommands.register(d);

        new NotesCommands(notes, ranks).register(d);
        new ReportsCommands(reports, ranks).register(d);

        new FreezeCommands(freeze, ranks).register(d);
        new JailCommands(jail, ranks).register(d);
        new JailAdminCommands(jail, ranks).register(d);
        new JailCheckCommands(jail, ranks).register(d);
        new PixitySkillsCommand(ranks).register(d);

        punishGuiCommands.register(d);

        new PunishCommands(
                punish,
                notes,
                reports,
                mutes,
                ranks,
                freeze,
                jail
        ).register(d);

        new PwarpCommands(tp, ranks).onRegister(e);
        new BackCommands(tp, ranks).onRegister(e);
        new AfkCommands(afk, ranks).onRegister(e);

        new AnnouncementsCommands(
                announce,
                tp,
                () -> announce.setConfig(announceStore.loadOrCreate())
        ).register(d);

        new ChatControlCommands(chatCtl, tp).register(d);

        msgCommands.register(d);

        new EmojiCommands(chatExtrasStore.loadOrCreate(), ranks).register(d);

        new ItemMetaCommands(ranks).register(d);

// COMMAND
        new ShopCommand(shopService, ranks, econ).register(d);

// SELL WAND
        SellWandCommand.register(d);

// GLOBAL BOOST
        GlobalBoostCommand.register(d);

// LISTENERS
        NeoForge.EVENT_BUS.register(
                new SellWandListener(shopService, econ, ranks)
        );

        NeoForge.EVENT_BUS.register(
                new ShopChatListener(shopService, econ, ranks)
        );

        RankupCommand.register(d, ranks, econ);

        VoucherCommand.register(d);
        CrateCommand.register(d);
        ShinyBoosterCommand.register(d);
        PokePartyCommand.register(d);

        new ProfileCommands(ranks, freeze, notes, reports, alts, smartAlts).register(d);
        new RulesCommands(rules, ranks).register(d);
        new MailCommands(mail, ranks).register(d);
        new JoinLeaveCommands(joinLeave, ranks).register(d);
        new InspectCommands(inspect).register(d);
        new RollbackCommands(rollback).register(d);
        new GiveawayCommands(giveaway, giveawayPrompts, ranks).register(d);
        new KitsCommands(kits, economy, ranks).register(d);
        new EconomyCommands(economy, ranks).register(d);
        new FlyTimeCommands(fly, economy, ranks).register(d);
        new NickCommands(ranks, cosmeticsService).register(d);
        new AltsCommands(alts, smartAlts, ranks).register(d);
        new InvSeeCommands(ranks).register(d);
        new CosmeticsCommands(
                cosmeticsService,
                cosmeticsGui
        ).register(d);




        new StaffModeCommands(staffMode, ranks).register(d);
        MilestonesCommand.register(d);
        MiningEventCommand.register(d);

        new StaffDashboardCommands(
                ranks,
                punish,
                reports,
                freeze,
                jail
        ).register(d);

        new PlaytimeLevelsCommand(
                playtime,
                playtimeStore,
                levels
        ).register(d);

        new SpawnerCommands(ranks).register(d);

        // =========================
// MOD GUI
// =========================
        ModGui modGui = new ModGui(
                stateService,
                freeze,
                jail,
                punish,
                ranks,
                mutes
        );

        new ModCommands(modGui, ranks).register(d);
        NeoForge.EVENT_BUS.register(jail);
        NeoForge.EVENT_BUS.register(
                new MuteTicker(mutes, punish)
        );

        new StateCommands(states, ranks).register(d);
        WorldBossCommand.register(d);
        PokedexCommand.register(d);
        PokemonHuntCommand.register(d);



        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Commands registered.");
    }

    @SubscribeEvent
    public void onShutdown(ServerStoppingEvent e) {

        playtime.saveAll();
        economy.saveAll();
    }


    private void loadCosmetics() {

        //
         // GYM BADGE GRADIENTS
         //

        cosmeticsConfig.gradients.put("gym_fire", grad(
                "Fire Badge",
                "#ff6a00",
                "#ff0000",
                "Fire type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.fire",
                0
        ));

        cosmeticsConfig.gradients.put("gym_water", grad(
                "Water Badge",
                "#1e90ff",
                "#00c6ff",
                "Water type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.water",
                0
        ));

        cosmeticsConfig.gradients.put("gym_grass", grad(
                "Grass Badge",
                "#39ff14",
                "#2ecc71",
                "Grass type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.grass",
                0
        ));

        cosmeticsConfig.gradients.put("gym_electric", grad(
                "Electric Badge",
                "#ffd000",
                "#fff700",
                "Electric type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.electric",
                0
        ));

        cosmeticsConfig.gradients.put("gym_psychic", grad(
                "Psychic Badge",
                "#ff4da6",
                "#ff66cc",
                "Psychic type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.psychic",
                0
        ));

        cosmeticsConfig.gradients.put("gym_ice", grad(
                "Ice Badge",
                "#8ee3f5",
                "#c7f9ff",
                "Ice type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.ice",
                0
        ));

        cosmeticsConfig.gradients.put("gym_dragon", grad(
                "Dragon Badge",
                "#7038f8",
                "#a05cff",
                "Dragon type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.dragon",
                0
        ));

        cosmeticsConfig.gradients.put("gym_dark", grad(
                "Dark Badge",
                "#000000",
                "#434343",
                "Dark type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.dark",
                0
        ));

        cosmeticsConfig.gradients.put("gym_fairy", grad(
                "Fairy Badge",
                "#ff9ed8",
                "#ffc8ec",
                "Fairy type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.fairy",
                0
        ));

        cosmeticsConfig.gradients.put("gym_steel", grad(
                "Steel Badge",
                "#b8b8d0",
                "#e0e0ff",
                "Steel type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.steel",
                0
        ));

        cosmeticsConfig.gradients.put("gym_ghost", grad(
                "Ghost Badge",
                "#705898",
                "#9b7fd1",
                "Ghost type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.ghost",
                0
        ));

        cosmeticsConfig.gradients.put("gym_poison", grad(
                "Poison Badge",
                "#a040a0",
                "#d45fd4",
                "Poison type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.poison",
                0
        ));

        cosmeticsConfig.gradients.put("gym_fighting", grad(
                "Fighting Badge",
                "#c03028",
                "#8b0000",
                "Fighting type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.fighting",
                0
        ));

        cosmeticsConfig.gradients.put("gym_flying", grad(
                "Flying Badge",
                "#a890f0",
                "#87ceeb",
                "Flying type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.flying",
                0
        ));

        cosmeticsConfig.gradients.put("gym_ground", grad(
                "Ground Badge",
                "#e0c068",
                "#8b5a2b",
                "Ground type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.ground",
                0
        ));

        cosmeticsConfig.gradients.put("gym_rock", grad(
                "Rock Badge",
                "#b8a038",
                "#6b4f1d",
                "Rock type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.rock",
                0
        ));

        cosmeticsConfig.gradients.put("gym_bug", grad(
                "Bug Badge",
                "#a8b820",
                "#4caf50",
                "Bug type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.bug",
                0
        ));

        cosmeticsConfig.gradients.put("gym_grass_alt", grad(
                "Grass Badge II",
                "#4caf50",
                "#1b5e20",
                "Grass type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.grass2",
                0
        ));

        cosmeticsConfig.gradients.put("gym_normal", grad(
                "Normal Badge",
                "#a8a878",
                "#6d6d4e",
                "Normal type gym badge",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.gym.normal",
                0
        ));

        //
        // ELITE 4 GRADS
        //

        cosmeticsConfig.gradients.put("elitefour_fire", grad(
                "Elite Four Fire",
                "#ff4500",
                "#ff0000",
                "Elite Four Fire Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.fire",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_water", grad(
                "Elite Four Water",
                "#00c6ff",
                "#0072ff",
                "Elite Four Water Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.water",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_grass", grad(
                "Elite Four Grass",
                "#39ff14",
                "#2ecc71",
                "Elite Four Grass Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.grass",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_electric", grad(
                "Elite Four Electric",
                "#ffd000",
                "#fff700",
                "Elite Four Electric Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.electric",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_psychic", grad(
                "Elite Four Psychic",
                "#ff4da6",
                "#ff66cc",
                "Elite Four Psychic Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.psychic",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_ice", grad(
                "Elite Four Ice",
                "#8ee3f5",
                "#c7f9ff",
                "Elite Four Ice Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.ice",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_dragon", grad(
                "Elite Four Dragon",
                "#7038f8",
                "#a05cff",
                "Elite Four Dragon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.dragon",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_dark", grad(
                "Elite Four Dark",
                "#000000",
                "#434343",
                "Elite Four Dark Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.dark",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_fairy", grad(
                "Elite Four Fairy",
                "#ff9ed8",
                "#ffc8ec",
                "Elite Four Fairy Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.fairy",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_steel", grad(
                "Elite Four Steel",
                "#b8b8d0",
                "#e0e0ff",
                "Elite Four Steel Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.steel",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_ghost", grad(
                "Elite Four Ghost",
                "#705898",
                "#9b7fd1",
                "Elite Four Ghost Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.ghost",
                0
        ));

        cosmeticsConfig.gradients.put("elitefour_poison", grad(
                "Elite Four Poison",
                "#a040a0",
                "#d45fd4",
                "Elite Four Poison Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.elitefour.poison",
                0
        ));

        cosmeticsConfig.gradients.put("champion_gold", grad(
                "Champion Gold",
                "#ffd700",
                "#fff4a3",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.gold",
                0
        ));

        cosmeticsConfig.gradients.put("champion_platinum", grad(
                "Champion Platinum",
                "#e5e4e2",
                "#cfcfcf",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.platinum",
                0
        ));

        cosmeticsConfig.gradients.put("champion_diamond", grad(
                "Champion Diamond",
                "#b9f2ff",
                "#00e5ff",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.diamond",
                0
        ));

        cosmeticsConfig.gradients.put("champion_rainbow", grad(
                "Champion Rainbow",
                "#ff0000",
                "#7a00ff",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.rainbow",
                0
        ));

        cosmeticsConfig.gradients.put("champion_master", grad(
                "Champion Master",
                "#000000",
                "#ffd700",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.master",
                0
        ));

        cosmeticsConfig.gradients.put("champion_legend", grad(
                "Champion Legend",
                "#ff6a00",
                "#ffd700",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.legend",
                0
        ));

        cosmeticsConfig.gradients.put("champion_shiny", grad(
                "Shiny Champion",
                "#00e5ff",
                "#ff00ff",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.shiny",
                0
        ));

        cosmeticsConfig.gradients.put("champion_shadow", grad(
                "Shadow Champion",
                "#000000",
                "#4a148c",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.shadow",
                0
        ));

        cosmeticsConfig.gradients.put("champion_ultimate", grad(
                "Ultimate Champion",
                "#ffd700",
                "#ff0000",
                "Pokemon Champion",
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.champion.ultimate",
                0
        ));

        //
         // EVENTS
         //

        cosmeticsConfig.gradients.put("event_halloween", grad(
                "Halloween",
                "#ff6a00",
                "#000000",
                "Limited Halloween Event",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_halloween_pumpkin", grad(
                "Pumpkin",
                "#ff6a00",
                "#000000",
                "Halloween pumpkin",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_halloween_spooky", grad(
                "Spooky",
                "#7a00ff",
                "#000000",
                "Spooky Halloween theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_halloween_candy", grad(
                "Candy",
                "#ff66cc",
                "#ff8800",
                "Halloween candy",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_stpatricks", grad(
                "St Patricks",
                "#00cc66",
                "#006400",
                "St Patrick's Day",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_stpatricks_gold", grad(
                "Pot of Gold",
                "#00cc66",
                "#ffd700",
                "St Patrick's gold",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_thanksgiving", grad(
                "Thanksgiving",
                "#8b5a2b",
                "#ff8800",
                "Thanksgiving event",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_thanksgiving_harvest", grad(
                "Harvest",
                "#c79081",
                "#dfa579",
                "Harvest season",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_thanksgiving_fall", grad(
                "Fall Feast",
                "#ff6a00",
                "#8b5a2b",
                "Autumn feast",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_christmas", grad(
                "Christmas",
                "#ff0000",
                "#00cc66",
                "Limited Christmas Event",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_christmas_gold", grad(
                "Christmas Gold",
                "#ff0000",
                "#ffd700",
                "Christmas event reward",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_christmas_ice", grad(
                "Christmas Ice",
                "#00e5ff",
                "#ffffff",
                "Christmas winter theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_christmas_tree", grad(
                "Christmas Tree",
                "#006400",
                "#00cc66",
                "Christmas tree theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_newyear", grad(
                "New Year",
                "#ffd700",
                "#ffffff",
                "Limited New Year Event",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_newyear_fireworks", grad(
                "New Year Fireworks",
                "#ffd700",
                "#ff00ff",
                "New Year celebration",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_newyear_midnight", grad(
                "Midnight",
                "#000000",
                "#ffd700",
                "New Year midnight",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_easter", grad(
                "Easter",
                "#ff9ed8",
                "#7ed957",
                "Limited Easter Event",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_easter_pastel", grad(
                "Easter Pastel",
                "#ff9ed8",
                "#7ed957",
                "Easter pastel theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_easter_egg", grad(
                "Easter Egg",
                "#ffd166",
                "#c77dff",
                "Easter egg colors",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_easter_spring", grad(
                "Easter Spring",
                "#39ff14",
                "#ff66cc",
                "Spring Easter theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_july4", grad(
                "4th of July",
                "#ff0000",
                "#0000ff",
                "Independence Day",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_july4_firework", grad(
                "Firework",
                "#ffffff",
                "#ff0000",
                "Firework celebration",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_july4_patriot", grad(
                "Patriot",
                "#0000ff",
                "#ffffff",
                "Patriotic theme",
                CosmeticCategory.EVENT,
                null,
                0
        ));

        cosmeticsConfig.gradients.put("event_anniversary", grad(
                "Anniversary",
                "#ff00ff",
                "#00e5ff",
                "Server Anniversary",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.anniversary",
                0
        ));

        cosmeticsConfig.gradients.put("event_launch", grad(
                "Server Launch",
                "#39ff14",
                "#00ccff",
                "Server Launch Event",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.launch",
                0
        ));

        cosmeticsConfig.gradients.put("event_beta", grad(
                "Beta Tester",
                "#ffaa00",
                "#ff0000",
                "Closed Beta Reward",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.beta",
                0
        ));

        cosmeticsConfig.gradients.put("event_founder", grad(
                "Founder",
                "#ffd700",
                "#ff6a00",
                "Server Founder Reward",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.founder",
                0
        ));

        cosmeticsConfig.gradients.put("event_tournament", grad(
                "Tournament Winner",
                "#ffd700",
                "#ffffff",
                "Tournament Champion",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.tournament",
                0
        ));

        cosmeticsConfig.gradients.put("event_top_player", grad(
                "Top Player",
                "#00e5ff",
                "#7a00ff",
                "Top Player Event",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.topplayer",
                0
        ));

        cosmeticsConfig.gradients.put("event_staff", grad(
                "Staff Event",
                "#ff0000",
                "#7a00ff",
                "Staff Event Reward",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.staff",
                0
        ));

        cosmeticsConfig.gradients.put("event_community", grad(
                "Community Event",
                "#00cc66",
                "#00e5ff",
                "Community Event Reward",
                CosmeticCategory.EVENT,
                "pixity.cosmetics.event.community",
                0
        ));

        //
         // BASE COLORS
         //

        cosmeticsConfig.colors.put("dark_red", color(
                "Dark Red",
                "&#8b0000",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("dark_blue", color(
                "Dark Blue",
                "&#00008b",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("dark_green", color(
                "Dark Green",
                "&#006400",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("dark_purple", color(
                "Dark Purple",
                "&#4b0082",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("dark_gray", color(
                "Dark Gray",
                "&#2f2f2f",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("light_red", color(
                "Light Red",
                "&#ff6b6b",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("light_blue", color(
                "Light Blue",
                "&#66b3ff",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("light_green", color(
                "Light Green",
                "&#66ff99",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("light_purple", color(
                "Light Purple",
                "&#c77dff",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("light_gray", color(
                "Light Gray",
                "&#d3d3d3",
                "Standout from other players using this color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_black", color(
                "MC Black",
                "&#000000",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_dark_blue", color(
                "MC Dark Blue",
                "&#0000aa",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_dark_green", color(
                "MC Dark Green",
                "&#00aa00",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_dark_aqua", color(
                "MC Dark Aqua",
                "&#00aaaa",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_dark_red", color(
                "MC Dark Red",
                "&#aa0000",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_gold", color(
                "MC Gold",
                "&#ffaa00",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_gray", color(
                "MC Gray",
                "&#aaaaaa",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_aqua", color(
                "MC Aqua",
                "&#55ffff",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_red", color(
                "MC Red",
                "&#ff5555",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));

        cosmeticsConfig.colors.put("mc_green", color(
                "MC Green",
                "&#55ff55",
                "Minecraft classic color",
                CosmeticCategory.COLORS,
                null,
                250
        ));


        //
         // STARTER POKEMON
         //

        cosmeticsConfig.gradients.put("bulbasaur", grad(
                "Bulbasaur",
                "#7ed957",
                "#4caf50",
                "Starter Bulbasaur gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("charmander", grad(
                "Charmander",
                "#ff7b00",
                "#ff3c00",
                "Starter Charmander gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("squirtle", grad(
                "Squirtle",
                "#5dade2",
                "#1e90ff",
                "Starter Squirtle gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("chikorita", grad(
                "Chikorita",
                "#9acd32",
                "#4caf50",
                "Starter Chikorita gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("cyndaquil", grad(
                "Cyndaquil",
                "#ff6a00",
                "#ff0000",
                "Starter Cyndaquil gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("totodile", grad(
                "Totodile",
                "#00c6ff",
                "#0072ff",
                "Starter Totodile gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("treecko", grad(
                "Treecko",
                "#39ff14",
                "#0d3b12",
                "Starter Treecko gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("torchic", grad(
                "Torchic",
                "#ff8800",
                "#ff0000",
                "Starter Torchic gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("mudkip", grad(
                "Mudkip",
                "#5dade2",
                "#00bbff",
                "Starter Mudkip gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("turtwig", grad(
                "Turtwig",
                "#7ed957",
                "#2e7d32",
                "Starter Turtwig gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("chimchar", grad(
                "Chimchar",
                "#ff7b00",
                "#ff0000",
                "Starter Chimchar gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("piplup", grad(
                "Piplup",
                "#5dade2",
                "#1e90ff",
                "Starter Piplup gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("snivy", grad(
                "Snivy",
                "#9acd32",
                "#2ecc71",
                "Starter Snivy gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("tepig", grad(
                "Tepig",
                "#ff4500",
                "#8b0000",
                "Starter Tepig gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("oshawott", grad(
                "Oshawott",
                "#00c6ff",
                "#0072ff",
                "Starter Oshawott gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("chespin", grad(
                "Chespin",
                "#4caf50",
                "#1b5e20",
                "Starter Chespin gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("fennekin", grad(
                "Fennekin",
                "#ff9f1c",
                "#ff3c00",
                "Starter Fennekin gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("froakie", grad(
                "Froakie",
                "#5dade2",
                "#00bbff",
                "Starter Froakie gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("rowlet", grad(
                "Rowlet",
                "#2ecc71",
                "#145a32",
                "Starter Rowlet gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("litten", grad(
                "Litten",
                "#ff0000",
                "#000000",
                "Starter Litten gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("popplio", grad(
                "Popplio",
                "#1e90ff",
                "#5dade2",
                "Starter Popplio gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("grookey", grad(
                "Grookey",
                "#39ff14",
                "#2e7d32",
                "Starter Grookey gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("scorbunny", grad(
                "Scorbunny",
                "#ff8800",
                "#ff0000",
                "Starter Scorbunny gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("sobble", grad(
                "Sobble",
                "#00c6ff",
                "#1e90ff",
                "Starter Sobble gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("sprigatito", grad(
                "Sprigatito",
                "#4caf50",
                "#7ed957",
                "Starter Sprigatito gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("fuecoco", grad(
                "Fuecoco",
                "#ff4500",
                "#ff0000",
                "Starter Fuecoco gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        cosmeticsConfig.gradients.put("quaxly", grad(
                "Quaxly",
                "#5dade2",
                "#00bbff",
                "Starter Quaxly gradient",
                CosmeticCategory.POKEMON,
                null,
                250
        ));

        //
        // SEASONAL
         //

        cosmeticsConfig.gradients.put("spring_bloom", grad(
                "Spring Bloom",
                "#ff9ed8",
                "#7ed957",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("spring_pastel", grad(
                "Spring Pastel",
                "#c77dff",
                "#7ec8e3",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("spring_flowers", grad(
                "Spring Flowers",
                "#ff66cc",
                "#39ff14",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("spring_cherry_blossom", grad(
                "Cherry Blossom",
                "#ffb7c5",
                "#ffc0cb",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("spring_rain", grad(
                "Spring Rain",
                "#7ec8e3",
                "#39ff14",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("spring_mint", grad(
                "Spring Mint",
                "#98ff98",
                "#00cc66",
                "Spring seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_heat", grad(
                "Summer Heat",
                "#ff6a00",
                "#ffd000",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_ocean", grad(
                "Summer Ocean",
                "#00c6ff",
                "#0072ff",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_sunset", grad(
                "Summer Sunset",
                "#ff7b00",
                "#ff3c00",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_heatwave", grad(
                "Heatwave",
                "#ff4500",
                "#ffd000",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_neon", grad(
                "Summer Neon",
                "#00e5ff",
                "#ff00ff",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("summer_tropical", grad(
                "Tropical",
                "#39ff14",
                "#00e5ff",
                "Summer seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("autumn_leaves", grad(
                "Autumn Leaves",
                "#ff8800",
                "#8b5a2b",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("autumn_harvest", grad(
                "Autumn Harvest",
                "#c79081",
                "#dfa579",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("autumn_forest", grad(
                "Autumn Forest",
                "#006400",
                "#ff6a00",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("autumn_gold", grad(
                "Autumn Gold",
                "#ffd700",
                "#ff8800",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                "pixity.cosmetics.seasonal.autumn.gold",
                750
        ));

        cosmeticsConfig.gradients.put("autumn_maple", grad(
                "Maple",
                "#ff0000",
                "#8b5a2b",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("autumn_cool", grad(
                "Cool Autumn",
                "#8b5a2b",
                "#006400",
                "Autumn seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_frost", grad(
                "Winter Frost",
                "#00e5ff",
                "#ffffff",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_ice", grad(
                "Winter Ice",
                "#7ec8e3",
                "#00bbff",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_blizzard", grad(
                "Blizzard",
                "#ffffff",
                "#5dade2",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_snowfall", grad(
                "Snowfall",
                "#ffffff",
                "#00e5ff",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_aurora", grad(
                "Aurora",
                "#00ff87",
                "#60efff",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("winter_midnight", grad(
                "Winter Midnight",
                "#000428",
                "#004e92",
                "Winter seasonal gradient",
                CosmeticCategory.SEASONAL,
                null,
                750
        ));



        //
        // PASTEL COLORS
        //

        cosmeticsConfig.colors.put("pastel_pink", color(
                "Pastel Pink",
                "&#ffb6c1",
                "Standout from other players using this color",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.colors.put("pastel_blue", color(
                "Pastel Blue",
                "&#aec6cf",
                "Standout from other players using this color",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.colors.put("pastel_green", color(
                "Pastel Green",
                "&#77dd77",
                "Standout from other players using this color",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.colors.put("pastel_yellow", color(
                "Pastel Yellow",
                "&#fdfd96",
                "Standout from other players using this color",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.colors.put("pastel_purple", color(
                "Pastel Purple",
                "&#c3b1e1",
                "Standout from other players using this color",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        //
        // PASTEL GRADS
        //

        cosmeticsConfig.gradients.put("soft_pink", grad(
                "Soft Pink",
                "#ffeef2",
                "#ffd6e0",
                "Soft pink gradient",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("blush", grad(
                "Blush",
                "#fce4ec",
                "#f8bbd0",
                "Blush tone gradient",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("lavender", grad(
                "Lavender",
                "#f3e5f5",
                "#e1bee7",
                "Soft lavender gradient",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        cosmeticsConfig.gradients.put("lilac", grad(
                "Lilac",
                "#ede7f6",
                "#d1c4e9",
                "Minimal lilac gradient",
                CosmeticCategory.PASTEL,
                null,
                750
        ));

        //
         // FOOD COLOR
         //

        cosmeticsConfig.colors.put("strawberry", color(
                "Strawberry",
                "&#ff4d6d",
                "Standout from other players using this color",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.colors.put("mango", color(
                "Mango",
                "&#ff9f1c",
                "Standout from other players using this color",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.colors.put("grape", color(
                "Grape",
                "&#7b2cbf",
                "Standout from other players using this color",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.colors.put("blue_raspberry", color(
                "Blue Raspberry",
                "&#3a86ff",
                "Standout from other players using this color",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.colors.put("watermelon", color(
                "Watermelon",
                "&#ff3b3b",
                "Standout from other players using this color",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        //
         // FOOD GRAD
         //

        cosmeticsConfig.gradients.put("cotton_candy", grad(
                "Cotton Candy",
                "#ffafcc",
                "#a2d2ff",
                "Cotton candy sweetness",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.gradients.put("lemonade", grad(
                "Lemonade",
                "#fff200",
                "#ffd60a",
                "Refreshing lemonade colors",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.gradients.put("orange_soda", grad(
                "Orange Soda",
                "#ff8800",
                "#ffaa33",
                "Orange soda gradient",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.gradients.put("mint_chocolate", grad(
                "Mint Chocolate",
                "#2ec4b6",
                "#5a3e36",
                "Mint chocolate colors",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        cosmeticsConfig.gradients.put("caramel", grad(
                "Caramel",
                "#c68e17",
                "#ffb347",
                "Sweet caramel colors",
                CosmeticCategory.FOOD,
                null,
                850
        ));

        //
         // NATURE COLOR
         //

        cosmeticsConfig.colors.put("forest_green", color(
                "Forest Green",
                "&#2e8b57",
                "Standout from other players using this color",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.colors.put("ocean_blue", color(
                "Ocean Blue",
                "&#0077be",
                "Standout from other players using this color",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.colors.put("sunset_orange", color(
                "Sunset Orange",
                "&#ff5e13",
                "Standout from other players using this color",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.colors.put("earth_brown", color(
                "Earth Brown",
                "&#8b5a2b",
                "Standout from other players using this color",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.colors.put("leaf", color(
                "Leaf",
                "&#4caf50",
                "Standout from other players using this color",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        //
         // NATURE GRAD
         //

        cosmeticsConfig.gradients.put("ocean_breeze", grad(
                "Ocean Breeze",
                "#2193b0",
                "#6dd5ed",
                "Cool ocean breeze colors",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.gradients.put("sunset_sky", grad(
                "Sunset Sky",
                "#ff512f",
                "#dd2476",
                "Warm sunset gradient",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.gradients.put("forest", grad(
                "Forest",
                "#134e5e",
                "#71b280",
                "Forest nature colors",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.gradients.put("glacier", grad(
                "Glacier",
                "#83a4d4",
                "#b6fbff",
                "Glacier ice gradient",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        cosmeticsConfig.gradients.put("desert", grad(
                "Desert",
                "#c79081",
                "#dfa579",
                "Desert sand colors",
                CosmeticCategory.NATURE,
                null,
                900
        ));

        //
        // NEON COLORS
        //

        cosmeticsConfig.colors.put("neon_red", color(
                "Neon Red",
                "&#ff003c",
                "Standout from other players using this color",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("neon_blue", color(
                "Neon Blue",
                "&#00e5ff",
                "Standout from other players using this color",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("neon_green", color(
                "Neon Green",
                "&#39ff14",
                "Standout from other players using this color",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("neon_pink", color(
                "Neon Pink",
                "&#ff2bd6",
                "Standout from other players using this color",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("neon_purple", color(
                "Neon Purple",
                "&#bc13fe",
                "Standout from other players using this color",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        //
        // NEON GRADS
        //

        cosmeticsConfig.gradients.put("neon_blue_glow", grad(
                "Neon Blue Glow",
                "#00e5ff",
                "#005eff",
                "Electric neon blue glow",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("neon_pink_glow", grad(
                "Neon Pink Glow",
                "#ff2bd6",
                "#ff006e",
                "Bright neon pink glow",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("neon_green_glow", grad(
                "Neon Green Glow",
                "#39ff14",
                "#00ff87",
                "Bright neon green glow",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("neon_cyan", grad(
                "Neon Cyan",
                "#00f5ff",
                "#00bbff",
                "Neon cyan gradient",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("neon_fire", grad(
                "Neon Fire",
                "#ff0055",
                "#ff8800",
                "Neon fire gradient",
                CosmeticCategory.NEON,
                null,
                1000
        ));

        //
         // DARK COLORS
         //

        cosmeticsConfig.colors.put("midnight", color(
                "Midnight",
                "&#0f2027",
                "Standout from other players using this color",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("shadow", color(
                "Shadow",
                "&#232526",
                "Standout from other players using this color",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("obsidian", color(
                "Obsidian",
                "&#141e30",
                "Standout from other players using this color",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("void", color(
                "Void",
                "&#000000",
                "Standout from other players using this color",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.colors.put("charcoal", color(
                "Charcoal",
                "&#2b2b2b",
                "Standout from other players using this color",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        //
         // DARK GRAD
         //

        cosmeticsConfig.gradients.put("midnight_dark", grad(
                "Midnight",
                "#0f2027",
                "#203a43",
                "Deep midnight gradient",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("shadow_dark", grad(
                "Shadow",
                "#232526",
                "#414345",
                "Dark shadow tones",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("obsidian_dark", grad(
                "Obsidian",
                "#141e30",
                "#243b55",
                "Obsidian glass gradient",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("void_dark", grad(
                "Void",
                "#000000",
                "#1a0026",
                "Dark void energy",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        cosmeticsConfig.gradients.put("eclipse_dark", grad(
                "Eclipse",
                "#000000",
                "#434343",
                "Eclipse gradient",
                CosmeticCategory.DARK,
                null,
                1000
        ));

        //
         // METALLIC COLOR
         //

        cosmeticsConfig.colors.put("gold", color(
                "Gold",
                "&#d4af37",
                "Standout from other players using this color",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.colors.put("silver", color(
                "Silver",
                "&#c0c0c0",
                "Standout from other players using this color",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.colors.put("platinum", color(
                "Platinum",
                "&#e5e4e2",
                "Standout from other players using this color",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.colors.put("bronze", color(
                "Bronze",
                "&#cd7f32",
                "Standout from other players using this color",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.colors.put("rose_gold", color(
                "Rose Gold",
                "&#b76e79",
                "Standout from other players using this color",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        //
         // METALLIC GRAD
         //

        cosmeticsConfig.gradients.put("gold_shine", grad(
                "Gold Shine",
                "#d4af37",
                "#ffd700",
                "Shimmering gold gradient",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("silver_chrome", grad(
                "Chrome",
                "#8e9eab",
                "#eef2f3",
                "Chrome metal shine",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("platinum_lux", grad(
                "Platinum",
                "#e5e4e2",
                "#cfcfcf",
                "Platinum luxury gradient",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("titanium", grad(
                "Titanium",
                "#283048",
                "#859398",
                "Titanium metallic tones",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("rose_gold_metal", grad(
                "Rose Gold",
                "#b76e79",
                "#f7cac9",
                "Rose gold metallic",
                CosmeticCategory.METAL,
                null,
                1250
        ));

        //
        // SHINY STARTER POKEMON
         //

        cosmeticsConfig.gradients.put("shiny_bulbasaur", grad(
                "Shiny Bulbasaur",
                "#8ddf8a",
                "#2ecc71",
                "Shiny Bulbasaur gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_charmander", grad(
                "Shiny Charmander",
                "#ffd166",
                "#ff8800",
                "Shiny Charmander gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_squirtle", grad(
                "Shiny Squirtle",
                "#7ec8e3",
                "#00bbff",
                "Shiny Squirtle gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_chikorita", grad(
                "Shiny Chikorita",
                "#b7ffb7",
                "#4caf50",
                "Shiny Chikorita gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_cyndaquil", grad(
                "Shiny Cyndaquil",
                "#ffd166",
                "#ff3c00",
                "Shiny Cyndaquil gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_totodile", grad(
                "Shiny Totodile",
                "#7ed6ff",
                "#0072ff",
                "Shiny Totodile gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_treecko", grad(
                "Shiny Treecko",
                "#aaffaa",
                "#00cc66",
                "Shiny Treecko gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_torchic", grad(
                "Shiny Torchic",
                "#ffd166",
                "#ff4500",
                "Shiny Torchic gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_mudkip", grad(
                "Shiny Mudkip",
                "#7ec8e3",
                "#1e90ff",
                "Shiny Mudkip gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_turtwig", grad(
                "Shiny Turtwig",
                "#b7ff7a",
                "#2ecc71",
                "Shiny Turtwig gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_chimchar", grad(
                "Shiny Chimchar",
                "#ffd166",
                "#ff0000",
                "Shiny Chimchar gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_piplup", grad(
                "Shiny Piplup",
                "#7ec8e3",
                "#5dade2",
                "Shiny Piplup gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_snivy", grad(
                "Shiny Snivy",
                "#a8ff78",
                "#78ffd6",
                "Shiny Snivy gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_tepig", grad(
                "Shiny Tepig",
                "#ffd166",
                "#ff3c00",
                "Shiny Tepig gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_oshawott", grad(
                "Shiny Oshawott",
                "#7ec8e3",
                "#00bbff",
                "Shiny Oshawott gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_chespin", grad(
                "Shiny Chespin",
                "#a8ff78",
                "#2ecc71",
                "Shiny Chespin gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_fennekin", grad(
                "Shiny Fennekin",
                "#ffd166",
                "#ff6a00",
                "Shiny Fennekin gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_froakie", grad(
                "Shiny Froakie",
                "#7ec8e3",
                "#1e90ff",
                "Shiny Froakie gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_rowlet", grad(
                "Shiny Rowlet",
                "#b7ff7a",
                "#2ecc71",
                "Shiny Rowlet gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_litten", grad(
                "Shiny Litten",
                "#ffd166",
                "#8b0000",
                "Shiny Litten gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_popplio", grad(
                "Shiny Popplio",
                "#7ec8e3",
                "#5dade2",
                "Shiny Popplio gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_grookey", grad(
                "Shiny Grookey",
                "#a8ff78",
                "#2ecc71",
                "Shiny Grookey gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_scorbunny", grad(
                "Shiny Scorbunny",
                "#ffd166",
                "#ff3c00",
                "Shiny Scorbunny gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_sobble", grad(
                "Shiny Sobble",
                "#7ec8e3",
                "#1e90ff",
                "Shiny Sobble gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_sprigatito", grad(
                "Shiny Sprigatito",
                "#a8ff78",
                "#4caf50",
                "Shiny Sprigatito gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_fuecoco", grad(
                "Shiny Fuecoco",
                "#ffd166",
                "#ff0000",
                "Shiny Fuecoco gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        cosmeticsConfig.gradients.put("shiny_quaxly", grad(
                "Shiny Quaxly",
                "#7ec8e3",
                "#00bbff",
                "Shiny Quaxly gradient",
                CosmeticCategory.POKEMON,
                null,
                1250
        ));

        //
         // POKEBALL COLORS
         //

        cosmeticsConfig.colors.put("pokeball_red", color(
                "Pokeball Red",
                "&#ff0000",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("greatball_blue", color(
                "Great Ball",
                "&#2a75ff",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("ultraball_yellow", color(
                "Ultra Ball",
                "&#ffd000",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("masterball_purple", color(
                "Master Ball",
                "&#7a00ff",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        //
         // POKEBALL GRADIENTS
         //

        cosmeticsConfig.gradients.put("pokeball", grad(
                "Pokeball",
                "#ff0000",
                "#ffffff",
                "Classic pokeball colors",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("greatball", grad(
                "Great Ball",
                "#2a75ff",
                "#d62828",
                "Great ball gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("ultraball", grad(
                "Ultra Ball",
                "#000000",
                "#ffd000",
                "Ultra ball gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("masterball", grad(
                "Master Ball",
                "#7a00ff",
                "#ff66ff",
                "Legendary master ball gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        //
         // POKEMON TYPES COLOR
         //

        cosmeticsConfig.colors.put("fire_type", color(
                "Fire Type",
                "&#ff6b00",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("water_type", color(
                "Water Type",
                "&#1e90ff",
                null,
                CosmeticCategory.POKEMON,
                "pixity.cosmetics.pokemon.water",
                1500
        ));

        cosmeticsConfig.colors.put("grass_type", color(
                "Grass Type",
                "&#4caf50",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("electric_type", color(
                "Electric Type",
                "&#ffd300",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("psychic_type", color(
                "Psychic Type",
                "&#ff4da6",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("ice_type", color(
                "Ice Type",
                "&#8ee3f5",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("dragon_type", color(
                "Dragon Type",
                "&#7038f8",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("dark_type", color(
                "Dark Type",
                "&#2e2e2e",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("fairy_type", color(
                "Fairy Type",
                "&#ff9ed8",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("steel_type", color(
                "Steel Type",
                "&#b8b8d0",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("ghost_type", color(
                "Ghost Type",
                "&#705898",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.colors.put("poison_type", color(
                "Poison Type",
                "&#a040a0",
                "Standout from other players using this color",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        //
         // POKEMON TYPE GRADIENTS
         //

        cosmeticsConfig.gradients.put("fire_type", grad(
                "Fire Type",
                "#ff6b00",
                "#ff0000",
                "Fire type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("water_type", grad(
                "Water Type",
                "#1e90ff",
                "#00c6ff",
                "Water type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("grass_type", grad(
                "Grass Type",
                "#4caf50",
                "#8bc34a",
                "Grass type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("electric_type", grad(
                "Electric Type",
                "#ffd300",
                "#fff700",
                "Electric type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("psychic_type", grad(
                "Psychic Type",
                "#ff4da6",
                "#ff66cc",
                "Psychic type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("ice_type", grad(
                "Ice Type",
                "#8ee3f5",
                "#c7f9ff",
                "Ice type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("dragon_type", grad(
                "Dragon Type",
                "#7038f8",
                "#a05cff",
                "Dragon type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("dark_type", grad(
                "Dark Type",
                "#2e2e2e",
                "#000000",
                "Dark type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("fairy_type", grad(
                "Fairy Type",
                "#ff9ed8",
                "#ffc8ec",
                "Fairy type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("steel_type", grad(
                "Steel Type",
                "#b8b8d0",
                "#e0e0ff",
                "Steel type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("ghost_type", grad(
                "Ghost Type",
                "#705898",
                "#9b7fd1",
                "Ghost type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        cosmeticsConfig.gradients.put("poison_type", grad(
                "Poison Type",
                "#a040a0",
                "#d45fd4",
                "Poison type gradient",
                CosmeticCategory.POKEMON,
                null,
                1500
        ));

        //
        // POKEMON LEGENDARYS GRADIENTS
         //

        cosmeticsConfig.gradients.put("articuno", grad(
                "Articuno",
                "#7ec8e3",
                "#dff6ff",
                "Legendary Articuno gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("zapdos", grad(
                "Zapdos",
                "#ffd000",
                "#fff700",
                "Legendary Zapdos gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("moltres", grad(
                "Moltres",
                "#ff6a00",
                "#ff0000",
                "Legendary Moltres gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("mewtwo", grad(
                "Mewtwo",
                "#9b59b6",
                "#6c3483",
                "Legendary Mewtwo gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("lugia", grad(
                "Lugia",
                "#4facfe",
                "#00f2fe",
                "Legendary Lugia gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("ho_oh", grad(
                "Ho-Oh",
                "#ff512f",
                "#f09819",
                "Legendary Ho-Oh gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("raikou", grad(
                "Raikou",
                "#ffd000",
                "#ffaa00",
                "Legendary Raikou gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("entei", grad(
                "Entei",
                "#ff0000",
                "#8b0000",
                "Legendary Entei gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("suicune", grad(
                "Suicune",
                "#4facfe",
                "#00c6ff",
                "Legendary Suicune gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("regirock", grad(
                "Regirock",
                "#c79081",
                "#dfa579",
                "Legendary Regirock gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("regice", grad(
                "Regice",
                "#83a4d4",
                "#b6fbff",
                "Legendary Regice gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("registeel", grad(
                "Registeel",
                "#757f9a",
                "#d7dde8",
                "Legendary Registeel gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("kyogre", grad(
                "Kyogre",
                "#0033cc",
                "#00aaff",
                "Legendary Kyogre gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("groudon", grad(
                "Groudon",
                "#cc0000",
                "#ff5500",
                "Legendary Groudon gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("rayquaza", grad(
                "Rayquaza",
                "#00cc66",
                "#003300",
                "Legendary Rayquaza gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("latios", grad(
                "Latios",
                "#2a75ff",
                "#00bbff",
                "Legendary Latios gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("latias", grad(
                "Latias",
                "#ff4d6d",
                "#ff758f",
                "Legendary Latias gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));


        cosmeticsConfig.gradients.put("dialga", grad(
                "Dialga",
                "#5aa9e6",
                "#1c3d5a",
                "Legendary Dialga gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("palkia", grad(
                "Palkia",
                "#ff66cc",
                "#9933ff",
                "Legendary Palkia gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("giratina", grad(
                "Giratina",
                "#000000",
                "#ffd700",
                "Legendary Giratina gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("uxie", grad(
                "Uxie",
                "#ffd166",
                "#f4a261",
                "Legendary Uxie gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("mesprit", grad(
                "Mesprit",
                "#ff6ec7",
                "#ff9a9e",
                "Legendary Mesprit gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("azelf", grad(
                "Azelf",
                "#1e90ff",
                "#00c6ff",
                "Legendary Azelf gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("heatran", grad(
                "Heatran",
                "#ff4500",
                "#8b0000",
                "Legendary Heatran gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("regigigas", grad(
                "Regigigas",
                "#c0c0c0",
                "#ffd700",
                "Legendary Regigigas gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("cresselia", grad(
                "Cresselia",
                "#ffc0cb",
                "#87cefa",
                "Legendary Cresselia gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));





        cosmeticsConfig.gradients.put("reshiram", grad(
                "Reshiram",
                "#ffffff",
                "#ffcc99",
                "Legendary Reshiram gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("zekrom", grad(
                "Zekrom",
                "#000000",
                "#434343",
                "Legendary Zekrom gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("kyurem", grad(
                "Kyurem",
                "#6dd5ed",
                "#2193b0",
                "Legendary Kyurem gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("tornadus", grad(
                "Tornadus",
                "#6dd5ed",
                "#00c6ff",
                "Legendary Tornadus gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("thundurus", grad(
                "Thundurus",
                "#ffd000",
                "#ff8c00",
                "Legendary Thundurus gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("landorus", grad(
                "Landorus",
                "#c79081",
                "#dfa579",
                "Legendary Landorus gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));





        cosmeticsConfig.gradients.put("xerneas", grad(
                "Xerneas",
                "#00c9ff",
                "#92fe9d",
                "Legendary Xerneas gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("yveltal", grad(
                "Yveltal",
                "#8b0000",
                "#000000",
                "Legendary Yveltal gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("zygarde", grad(
                "Zygarde",
                "#39ff14",
                "#003300",
                "Legendary Zygarde gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));







        cosmeticsConfig.gradients.put("solgaleo", grad(
                "Solgaleo",
                "#ffffff",
                "#ffcc00",
                "Legendary Solgaleo gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("lunala", grad(
                "Lunala",
                "#2b1055",
                "#7597de",
                "Legendary Lunala gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("necrozma", grad(
                "Necrozma",
                "#000000",
                "#4a4a4a",
                "Legendary Necrozma gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("tapu_koko", grad(
                "Tapu Koko",
                "#ffd000",
                "#ff8800",
                "Legendary Tapu Koko gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("tapu_lele", grad(
                "Tapu Lele",
                "#ff66cc",
                "#ff9ed8",
                "Legendary Tapu Lele gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("tapu_bulu", grad(
                "Tapu Bulu",
                "#2ecc71",
                "#145a32",
                "Legendary Tapu Bulu gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("tapu_fini", grad(
                "Tapu Fini",
                "#5dade2",
                "#bb8fce",
                "Legendary Tapu Fini gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("nihilego", grad(
                "Nihilego",
                "#d291ff",
                "#6c3483",
                "Ultra Beast Nihilego gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("buzzwole", grad(
                "Buzzwole",
                "#ff0000",
                "#8b0000",
                "Ultra Beast Buzzwole gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("pheromosa", grad(
                "Pheromosa",
                "#f8f9f9",
                "#fadbd8",
                "Ultra Beast Pheromosa gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("xurkitree", grad(
                "Xurkitree",
                "#ffff00",
                "#ffffff",
                "Ultra Beast Xurkitree gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("celesteela", grad(
                "Celesteela",
                "#7f8c8d",
                "#34495e",
                "Ultra Beast Celesteela gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("kartana", grad(
                "Kartana",
                "#ffffff",
                "#2ecc71",
                "Ultra Beast Kartana gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("guzzlord", grad(
                "Guzzlord",
                "#2e2e2e",
                "#8b0000",
                "Ultra Beast Guzzlord gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("zacian", grad(
                "Zacian",
                "#5dade2",
                "#1f618d",
                "Legendary Zacian gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("zamazenta", grad(
                "Zamazenta",
                "#e74c3c",
                "#78281f",
                "Legendary Zamazenta gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("eternatus", grad(
                "Eternatus",
                "#ff00ff",
                "#4a004a",
                "Legendary Eternatus gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("calyrex", grad(
                "Calyrex",
                "#2ecc71",
                "#145a32",
                "Legendary Calyrex gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("glastrier", grad(
                "Glastrier",
                "#85c1e9",
                "#1b4f72",
                "Legendary Glastrier gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("spectrier", grad(
                "Spectrier",
                "#7d3c98",
                "#1b0a2a",
                "Legendary Spectrier gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("galar_articuno", grad(
                "Galar Articuno",
                "#a569bd",
                "#512e5f",
                "Galarian Articuno gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("galar_zapdos", grad(
                "Galar Zapdos",
                "#f39c12",
                "#7d6608",
                "Galarian Zapdos gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("galar_moltres", grad(
                "Galar Moltres",
                "#8e44ad",
                "#000000",
                "Galarian Moltres gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("koraidon", grad(
                "Koraidon",
                "#ff4d4d",
                "#8b0000",
                "Legendary Koraidon gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("miraidon", grad(
                "Miraidon",
                "#7a00ff",
                "#00e5ff",
                "Legendary Miraidon gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("chien_pao", grad(
                "Chien-Pao",
                "#e0f7fa",
                "#00acc1",
                "Legendary Chien-Pao gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("chi_yu", grad(
                "Chi-Yu",
                "#ff6a00",
                "#ff0000",
                "Legendary Chi-Yu gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("ting_lu", grad(
                "Ting-Lu",
                "#8d6e63",
                "#3e2723",
                "Legendary Ting-Lu gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("wo_chien", grad(
                "Wo-Chien",
                "#2e7d32",
                "#0d3b12",
                "Legendary Wo-Chien gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("walking_wake", grad(
                "Walking Wake",
                "#2196f3",
                "#00e5ff",
                "Legendary Walking Wake gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("iron_leaves", grad(
                "Iron Leaves",
                "#00ff87",
                "#005f3f",
                "Legendary Iron Leaves gradient",
                CosmeticCategory.POKEMON,
                null,
                2000
        ));

        cosmeticsConfig.gradients.put("mew", grad(
                "Mew",
                "#ffb6c1",
                "#ffc0cb",
                "Mythical Mew gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("celebi", grad(
                "Celebi",
                "#7ed957",
                "#d4ffb2",
                "Mythical Celebi gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("jirachi", grad(
                "Jirachi",
                "#ffd700",
                "#fff4a3",
                "Mythical Jirachi gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("deoxys_mythical", grad(
                "Deoxys",
                "#ff6600",
                "#cc0000",
                "Mythical Deoxys gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("phione", grad(
                "Phione",
                "#5dade2",
                "#85c1e9",
                "Mythical Phione gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("manaphy", grad(
                "Manaphy",
                "#1e90ff",
                "#00bbff",
                "Mythical Manaphy gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("darkrai_mythical", grad(
                "Darkrai",
                "#000000",
                "#4a148c",
                "Mythical Darkrai gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("shaymin_mythical", grad(
                "Shaymin",
                "#7ed957",
                "#ffffff",
                "Mythical Shaymin gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("arceus_mythical", grad(
                "Arceus",
                "#ffffff",
                "#ffd700",
                "Mythical Arceus gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("victini_mythical", grad(
                "Victini",
                "#ff7b00",
                "#ffd000",
                "Mythical Victini gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("keldeo_mythical", grad(
                "Keldeo",
                "#1e90ff",
                "#00bbff",
                "Mythical Keldeo gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("meloetta_mythical", grad(
                "Meloetta",
                "#00cc99",
                "#ff66cc",
                "Mythical Meloetta gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("genesect_mythical", grad(
                "Genesect",
                "#6c757d",
                "#adb5bd",
                "Mythical Genesect gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("diancie_mythical", grad(
                "Diancie",
                "#ffb7c5",
                "#ffd700",
                "Mythical Diancie gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("hoopa_mythical", grad(
                "Hoopa",
                "#7a00ff",
                "#ffcc00",
                "Mythical Hoopa gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("volcanion_mythical", grad(
                "Volcanion",
                "#ff4500",
                "#8b0000",
                "Mythical Volcanion gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("magearna", grad(
                "Magearna",
                "#f8cdda",
                "#c0c0aa",
                "Mythical Magearna gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("marshadow", grad(
                "Marshadow",
                "#2e2e2e",
                "#00ff87",
                "Mythical Marshadow gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("zeraora", grad(
                "Zeraora",
                "#ffd000",
                "#ff8800",
                "Mythical Zeraora gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("meltan", grad(
                "Meltan",
                "#b8b8d0",
                "#ffd700",
                "Mythical Meltan gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("melmetal", grad(
                "Melmetal",
                "#757f9a",
                "#d7dde8",
                "Mythical Melmetal gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("zarude", grad(
                "Zarude",
                "#2e7d32",
                "#0d3b12",
                "Mythical Zarude gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        cosmeticsConfig.gradients.put("pecharunt", grad(
                "Pecharunt",
                "#a64dff",
                "#ff66cc",
                "Mythical Pecharunt gradient",
                CosmeticCategory.POKEMON,
                null,
                2250
        ));

        //
        // RARE COSMETICS
         //

        cosmeticsConfig.gradients.put("rare_galaxy", grad(
                "Galaxy",
                "#7a00ff",
                "#00e5ff",
                "Rare cosmic gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.galaxy",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_nebula", grad(
                "Nebula",
                "#ff00ff",
                "#00ff87",
                "Rare cosmic gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.nebula",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_cosmic", grad(
                "Cosmic",
                "#000428",
                "#004e92",
                "Rare cosmic gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.cosmic",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_prism", grad(
                "Prism",
                "#ff0000",
                "#00e5ff",
                "Rare prism gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.prism",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_holographic", grad(
                "Holographic",
                "#60efff",
                "#ff1b6b",
                "Rare holographic gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.holographic",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_rainbow_shift", grad(
                "Rainbow Shift",
                "#ff0000",
                "#7a00ff",
                "Rare rainbow gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.rainbowshift",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_void", grad(
                "Void",
                "#000000",
                "#4a148c",
                "Rare void gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.void",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_shadow", grad(
                "Shadow",
                "#000000",
                "#434343",
                "Rare shadow gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.shadow",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_abyss", grad(
                "Abyss",
                "#020024",
                "#090979",
                "Rare abyss gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.abyss",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_plasma", grad(
                "Plasma",
                "#ff00cc",
                "#3333ff",
                "Rare plasma gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.plasma",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_electric", grad(
                "Electric Surge",
                "#00e5ff",
                "#ffd000",
                "Rare electric gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.electric",
                2500
        ));

        cosmeticsConfig.gradients.put("rare_neon_core", grad(
                "Neon Core",
                "#39ff14",
                "#00e5ff",
                "Rare neon gradient",
                CosmeticCategory.RARE,
                "pixity.cosmetics.rare.neoncore",
                2500
        ));

        //
         // ANIMATED :D
         //

        cosmeticsConfig.animated.put("rainbow_cycle", animated(
                "Rainbow Cycle",
                new String[][]{
                        {"#ff0000","#ffff00"},
                        {"#ffff00","#00ff00"},
                        {"#00ff00","#00ffff"},
                        {"#00ffff","#0000ff"},
                        {"#0000ff","#ff00ff"}
                },
                "Animated rainbow gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("fire", animated(
                "Fire",
                new String[][]{
                        {"#ff0000","#ff6a00"},
                        {"#ff6a00","#ffd000"},
                        {"#ffd000","#ff0000"},
                        {"#ff8800","#ff0000"}
                },
                "Animated fire gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("ocean_wave", animated(
                "Ocean Wave",
                new String[][]{
                        {"#0072ff","#00c6ff"},
                        {"#00c6ff","#00e5ff"},
                        {"#00e5ff","#0072ff"},
                        {"#0072ff","#5dade2"}
                },
                "Animated ocean gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("neon_pulse", animated(
                "Neon Pulse",
                new String[][]{
                        {"#39ff14","#00e5ff"},
                        {"#00e5ff","#ff00ff"},
                        {"#ff00ff","#39ff14"},
                        {"#00ff87","#39ff14"}
                },
                "Animated neon gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("galaxy_scroll", animated(
                "Galaxy Scroll",
                new String[][]{
                        {"#7a00ff","#00e5ff"},
                        {"#00e5ff","#ff00ff"},
                        {"#ff00ff","#7a00ff"},
                        {"#000428","#004e92"}
                },
                "Animated galaxy gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("shiny_shift", animated(
                "Shiny Shift",
                new String[][]{
                        {"#ffd700","#ffffff"},
                        {"#ffffff","#00e5ff"},
                        {"#00e5ff","#ffd700"},
                        {"#ffd700","#ff66cc"}
                },
                "Animated shiny gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("rgb_wave", animated(
                "RGB Wave",
                new String[][]{
                        {"#ff0000","#00ff00"},
                        {"#00ff00","#0000ff"},
                        {"#0000ff","#ff0000"},
                        {"#ff0000","#0000ff"}
                },
                "Animated RGB gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("lava_flow", animated(
                "Lava Flow",
                new String[][]{
                        {"#ff0000","#8b0000"},
                        {"#ff4500","#ff0000"},
                        {"#ff6a00","#8b0000"},
                        {"#ff0000","#ff8800"}
                },
                "Animated lava gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("aurora", animated(
                "Aurora",
                new String[][]{
                        {"#00ff87","#60efff"},
                        {"#60efff","#00e5ff"},
                        {"#00e5ff","#00ff87"},
                        {"#7a00ff","#00ff87"}
                },
                "Animated aurora gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("glitch", animated(
                "Glitch",
                new String[][]{
                        {"#ff00ff","#00ffff"},
                        {"#00ffff","#ff0000"},
                        {"#ff0000","#000000"},
                        {"#000000","#ff00ff"}
                },
                "Glitchy animated gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("matrix", animated(
                "Matrix",
                new String[][]{
                        {"#00ff00","#003300"},
                        {"#003300","#00ff00"},
                        {"#00ff00","#001a00"},
                        {"#001a00","#00ff00"}
                },
                "Matrix style animation",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("cyberpunk", animated(
                "Cyberpunk",
                new String[][]{
                        {"#ff00ff","#00e5ff"},
                        {"#00e5ff","#ff0080"},
                        {"#ff0080","#00ffcc"},
                        {"#00ffcc","#ff00ff"}
                },
                "Cyberpunk neon animation",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.animated.put("electric_storm", animated(
                "Electric Storm",
                new String[][]{
                        {"#ffd000","#00e5ff"},
                        {"#00e5ff","#ffffff"},
                        {"#ffffff","#ffd000"},
                        {"#ffd000","#00bbff"}
                },
                "Electric animated gradient",
                CosmeticCategory.ANIMATED,
                null,
                3500
        ));

        cosmeticsConfig.glow.put("glow_white",
                glow("White Glow", "#ffffff", "cosmetics.glow.white", 1000, 1));

        cosmeticsConfig.glow.put("glow_gray",
                glow("Gray Glow", "#aaaaaa", "cosmetics.glow.gray", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_gray",
                glow("Dark Gray Glow", "#555555", "cosmetics.glow.dark_gray", 1000, 1));

        cosmeticsConfig.glow.put("glow_black",
                glow("Black Glow", "#000000", "cosmetics.glow.black", 1000, 1));

        cosmeticsConfig.glow.put("glow_red",
                glow("Red Glow", "#ff5555", "cosmetics.glow.red", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_red",
                glow("Dark Red Glow", "#aa0000", "cosmetics.glow.dark_red", 1000, 1));

        cosmeticsConfig.glow.put("glow_gold",
                glow("Gold Glow", "#ffaa00", "cosmetics.glow.gold", 1000, 1));

        cosmeticsConfig.glow.put("glow_yellow",
                glow("Yellow Glow", "#ffff55", "cosmetics.glow.yellow", 1000, 1));

        cosmeticsConfig.glow.put("glow_green",
                glow("Green Glow", "#55ff55", "cosmetics.glow.green", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_green",
                glow("Dark Green Glow", "#00aa00", "cosmetics.glow.dark_green", 1000, 1));

        cosmeticsConfig.glow.put("glow_aqua",
                glow("Aqua Glow", "#55ffff", "cosmetics.glow.aqua", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_aqua",
                glow("Dark Aqua Glow", "#00aaaa", "cosmetics.glow.dark_aqua", 1000, 1));

        cosmeticsConfig.glow.put("glow_blue",
                glow("Blue Glow", "#5555ff", "cosmetics.glow.blue", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_blue",
                glow("Dark Blue Glow", "#0000aa", "cosmetics.glow.dark_blue", 1000, 1));

        cosmeticsConfig.glow.put("glow_light_purple",
                glow("Light Purple Glow", "#ff55ff", "cosmetics.glow.light_purple", 1000, 1));

        cosmeticsConfig.glow.put("glow_dark_purple",
                glow("Dark Purple Glow", "#aa00aa", "cosmetics.glow.dark_purple", 1000, 1));

        cosmeticsConfig.glow.put("glow_white_bold",
                glow("White Glow (Bold)", "#ffffff", "cosmetics.glow.white.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_gray_bold",
                glow("Gray Glow (Bold)", "#aaaaaa", "cosmetics.glow.gray.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_gray_bold",
                glow("Dark Gray Glow (Bold)", "#555555", "cosmetics.glow.dark_gray.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_black_bold",
                glow("Black Glow (Bold)", "#000000", "cosmetics.glow.black.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_red_bold",
                glow("Red Glow (Bold)", "#ff5555", "cosmetics.glow.red.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_red_bold",
                glow("Dark Red Glow (Bold)", "#aa0000", "cosmetics.glow.dark_red.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_gold_bold",
                glow("Gold Glow (Bold)", "#ffaa00", "cosmetics.glow.gold.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_yellow_bold",
                glow("Yellow Glow (Bold)", "#ffff55", "cosmetics.glow.yellow.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_green_bold",
                glow("Green Glow (Bold)", "#55ff55", "cosmetics.glow.green.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_green_bold",
                glow("Dark Green Glow (Bold)", "#00aa00", "cosmetics.glow.dark_green.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_aqua_bold",
                glow("Aqua Glow (Bold)", "#55ffff", "cosmetics.glow.aqua.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_aqua_bold",
                glow("Dark Aqua Glow (Bold)", "#00aaaa", "cosmetics.glow.dark_aqua.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_blue_bold",
                glow("Blue Glow (Bold)", "#5555ff", "cosmetics.glow.blue.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_blue_bold",
                glow("Dark Blue Glow (Bold)", "#0000aa", "cosmetics.glow.dark_blue.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_light_purple_bold",
                glow("Light Purple Glow (Bold)", "#ff55ff", "cosmetics.glow.light_purple.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_dark_purple_bold",
                glow("Dark Purple Glow (Bold)", "#aa00aa", "cosmetics.glow.dark_purple.bold", 2000, 2));

        cosmeticsConfig.glow.put("glow_white_ultra",
                glow("White Glow (Ultra)", "#ffffff", "cosmetics.glow.white.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_gray_ultra",
                glow("Gray Glow (Ultra)", "#aaaaaa", "cosmetics.glow.gray.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_gray_ultra",
                glow("Dark Gray Glow (Ultra)", "#555555", "cosmetics.glow.dark_gray.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_black_ultra",
                glow("Black Glow (Ultra)", "#000000", "cosmetics.glow.black.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_red_ultra",
                glow("Red Glow (Ultra)", "#ff5555", "cosmetics.glow.red.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_red_ultra",
                glow("Dark Red Glow (Ultra)", "#aa0000", "cosmetics.glow.dark_red.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_gold_ultra",
                glow("Gold Glow (Ultra)", "#ffaa00", "cosmetics.glow.gold.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_yellow_ultra",
                glow("Yellow Glow (Ultra)", "#ffff55", "cosmetics.glow.yellow.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_green_ultra",
                glow("Green Glow (Ultra)", "#55ff55", "cosmetics.glow.green.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_green_ultra",
                glow("Dark Green Glow (Ultra)", "#00aa00", "cosmetics.glow.dark_green.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_aqua_ultra",
                glow("Aqua Glow (Ultra)", "#55ffff", "cosmetics.glow.aqua.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_aqua_ultra",
                glow("Dark Aqua Glow (Ultra)", "#00aaaa", "cosmetics.glow.dark_aqua.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_blue_ultra",
                glow("Blue Glow (Ultra)", "#5555ff", "cosmetics.glow.blue.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_blue_ultra",
                glow("Dark Blue Glow (Ultra)", "#0000aa", "cosmetics.glow.dark_blue.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_light_purple_ultra",
                glow("Light Purple Glow (Ultra)", "#ff55ff", "cosmetics.glow.light_purple.ultra", 3500, 3));

        cosmeticsConfig.glow.put("glow_dark_purple_ultra",
                glow("Dark Purple Glow (Ultra)", "#aa00aa", "cosmetics.glow.dark_purple.ultra", 3500, 3));








    }

    private ChatCosmeticsConfig.GlowOption glow(
            String name,
            String color,
            String perm,
            int price,
            int thickness
    ) {
        ChatCosmeticsConfig.GlowOption o = new ChatCosmeticsConfig.GlowOption();
        o.name = name;
        o.color = color;
        o.permission = perm;
        o.price = price;
        o.thickness = thickness;
        return o;
    }

    private ChatCosmeticsConfig.ColorOption color(
            String name,
            String code,
            String desc,
            CosmeticCategory category,
            String permission,
            double price) {

        var c = new ChatCosmeticsConfig.ColorOption();
        c.name = name;
        c.code = code;
        c.description = desc;
        c.category = category;
        c.permission = permission;
        c.price = price;
        return c;
    }

    private ChatCosmeticsConfig.GradientOption grad(
            String name,
            String start,
            String end,
            String desc,
            CosmeticCategory category,
            String permission,
            double price) {

        var g = new ChatCosmeticsConfig.GradientOption();
        g.name = name;
        g.start = start;
        g.end = end;
        g.description = desc;
        g.category = category;
        g.permission = permission;
        g.price = price;

        return g;
    }

    private ChatCosmeticsConfig.AnimatedOption animated(
            String name,
            String[][] frames,
            String description,
            CosmeticCategory category,
            String permission,
            int price
    ) {
        ChatCosmeticsConfig.AnimatedOption o =
                new ChatCosmeticsConfig.AnimatedOption();

        o.name = name;
        o.frames = frames;
        o.description = description;
        o.category = category;
        o.permission = permission;
        o.price = price;

        return o;
    }

}