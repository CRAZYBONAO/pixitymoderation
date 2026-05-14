package org.howie.pixity.moderation.neoforge.notes;

import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class NotesService {


    public static final String PERM_NOTE_ADD = "pixity.notes.add";
    public static final String PERM_NOTE_VIEW = "pixity.notes.view";
    public static final String PERM_NOTE_REMOVE = "pixity.notes.remove";

    private final Logger logger;
    private final SQLiteNotesStore store;

    public NotesService(final Logger logger, final SQLiteNotesStore store) {
        this.logger = logger;
        this.store = store;
    }

    public List<NoteEntry> list(UUID target) {
        return store.get(target);
    }

    public NoteEntry add(UUID target, ServerPlayer staff, String text) {

        NoteEntry n = new NoteEntry();
        n.id = shortId();
        n.ts = System.currentTimeMillis();
        n.staffUuid = (staff == null) ? "CONSOLE" : staff.getUUID().toString();
        n.staffName = (staff == null) ? "CONSOLE" : staff.getGameProfile().getName();
        n.text = text;

        store.insert(target, n);
        return n;
    }

    public boolean remove(UUID target, String id) {
        return store.delete(target, id);
    }

    private static String shortId() {
        String s = UUID.randomUUID().toString().replace("-", "");
        return s.substring(0, 6).toUpperCase(Locale.ROOT);
    }


}
