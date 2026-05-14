package org.howie.pixity.moderation.neoforge.invsee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.level.ServerPlayer;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


public final class InvAuditLogger {

    private static final Gson GSON = new GsonBuilder().create();
    private static volatile InvAuditLogger INSTANCE;

    private final Logger logger;
    private final Path auditJsonl;

    private InvAuditLogger(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.auditJsonl = dataDir.resolve("audit.jsonl");
    }

    public static void init(final Logger logger, final Path dataDir) {
        INSTANCE = new InvAuditLogger(logger, dataDir);
    }

    public static InvAuditLogger get() {
        return INSTANCE;
    }

    public void logInvEdit(final ServerPlayer staff,
                           final UUID targetUuid,
                           final String targetName,
                           final boolean enderChest,
                           final Map<String, Integer> removed,
                           final Map<String, Integer> added) {
        try {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("ts", Instant.now().toString());
            obj.put("action", enderChest ? "ENDERSEE_EDIT" : "INVSEE_EDIT");
            obj.put("staffUuid", staff == null ? "CONSOLE" : staff.getUUID().toString());
            obj.put("staffName", staff == null ? "CONSOLE" : staff.getGameProfile().getName());
            obj.put("targetUuid", targetUuid.toString());
            obj.put("targetName", targetName);
            obj.put("removed", removed);
            obj.put("added", added);

            String json = GSON.toJson(obj);

            Files.createDirectories(auditJsonl.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(auditJsonl, StandardCharsets.UTF_8,
                    Files.exists(auditJsonl)
                            ? java.nio.file.StandardOpenOption.APPEND
                            : java.nio.file.StandardOpenOption.CREATE)) {
                w.write(json);
                w.newLine();
            }
        } catch (Exception e) {
            if (logger != null) logger.error("[PixityModeration] Failed to append invsee audit.jsonl", e);
        }
    }
}
