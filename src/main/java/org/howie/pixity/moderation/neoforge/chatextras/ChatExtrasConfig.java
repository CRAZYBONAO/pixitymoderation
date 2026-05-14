package org.howie.pixity.moderation.neoforge.chatextras;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ChatExtrasConfig {

    public boolean enabled = true;


    public boolean emojiEnabled = true;


    public int emojiMaxPerMessage = 15;


    public Map<String, String> emojis = new LinkedHashMap<>();


    public boolean mentionsEnabled = true;


    public String mentionColor = "&b";


    public boolean mentionActionbar = true;


    public String mentionActionbarText = "&eYou were mentioned by &f{SENDER}&e!";


    public boolean mentionSound = true;


    public String mentionBypassPermission = "pixity.mention.bypass";

    public ChatExtrasConfig() {

        emojis.put(":heart:", "❤");
        emojis.put(":star:", "⭐");
        emojis.put(":fire:", "🔥");
        emojis.put(":skull:", "💀");
        emojis.put(":sob:", "😭");
        emojis.put(":joy:", "😂");
        emojis.put(":shrug:", "¯\\_(ツ)_/¯");
        emojis.put(":tableflip:", "(╯°□°）╯︵ ┻━┻");
        emojis.put(":check:", "✔");
        emojis.put(":x:", "✖");
        emojis.put(":sparkles:", "✨");
        emojis.put(":100:", "💯");
        emojis.put(":pog:", "😮");
    }
}
