package org.howie.pixity.moderation.chat;

import java.util.*;

public final class ChatConfig {

    public boolean enabled = true;

    public String defaultNameHex = "#C8C8C8";
    public String defaultMessageHex = "#C8C8C8";

    public boolean useLuckPermsPrefixSuffix = true;
    public boolean useLuckPermsPrimaryGroupForRank = true;

    public String format = "{PREFIX}{NAME}{SUFFIX} » {MESSAGE}";
    public String rankFormat;

    public Map<String, String> rankColors = new HashMap<>();

    public Map<String, Gradient> nameGradients = new HashMap<>();
    public Map<String, Gradient> messageGradients = new HashMap<>();

    public List<String> rainbowGroups = new ArrayList<>();

    public boolean godRainbow = true;

    public static class Gradient {
        public String start;
        public String end;

        public Gradient() {}

        public Gradient(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
}