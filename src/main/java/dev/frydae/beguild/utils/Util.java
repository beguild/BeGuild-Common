package dev.frydae.beguild.utils;

import com.google.common.collect.Table;
import com.google.errorprone.annotations.FormatString;
import dev.frydae.beguild.BeGuildCommon;
import dev.frydae.beguild.data.Caches;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import static dev.frydae.beguild.exceptions.ExceptionUtils.verifyFalse;
import static dev.frydae.beguild.exceptions.ExceptionUtils.verifyTrue;

public final class Util {
    /**
     * Converts {@literal &} characters in a string into section signs.
     * <p></p>
     * This conversion acts as a bypass to the new {@link net.minecraft.util.Formatting} system for message.
     * <p></p>
     * While using this method, an ampersand can be included in the final product by escaping it with a backslash.
     * <p></p>
     *
     * <pre>{@code
     * "§aThis is &a message" = color("&aThis is \&a message")
     * }</pre>
     *
     * @param message
     * @return
     */
    public static String color(String message) {
        Matcher matcher = Patterns.CHAT_COLOR_CODES.matcher(message);

        message = matcher.replaceAll("\u00A7");;
        message = message.replaceAll("\\\\&", "&");

        return message;
    }

    public static String formatMessage(@FormatString String message, Object... replacements) {
        return String.format(message, replacements);
    }

    public static void sendMessage(@NotNull ServerPlayerEntity player, @FormatString String message, Object... replacements) {
        message = color(message);
        message = formatMessage(message, replacements);

        player.sendMessage(Text.literal(message));
    }

    public static void sendMessage(@NotNull ServerPlayerEntity player, List<String> lines) {
        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(line).append("\n");
        }

        sendMessage(player, builder.toString());
    }

    public static void sendMessage(@NotNull ServerPlayerEntity player, @NotNull Text text) {
        player.sendMessage(text);
    }

    public static void sendTimeoutMessage(@NotNull ServerPlayerEntity player, @NotNull String key, @FormatString String message, Object... replacements) {
        Pair<UUID, String> cacheKey = Pair.of(player.getUuid(), key);

        if (Caches.timeoutMessageCache.getIfPresent(cacheKey) == null) {
            sendMessage(player, message, replacements);

            Caches.timeoutMessageCache.put(cacheKey, TimeUtil.timestamp());
        }
    }

    public static void sendTimeoutMessage(@NotNull ServerPlayerEntity player, @NotNull String key, @NotNull Text message) {
        Pair<UUID, String> cacheKey = Pair.of(player.getUuid(), key);

        if (Caches.timeoutMessageCache.getIfPresent(cacheKey) == null) {
            player.sendMessage(message);

            Caches.timeoutMessageCache.put(cacheKey, TimeUtil.timestamp());
        }
    }

    public static void broadcastMessage(String message, Object... replacements) {
        String newMessage = formatMessage(message, replacements);

        broadcastMessage(Text.literal(newMessage));
    }

    public static void broadcastMessage(Text text) {
        BeGuildCommon.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text));
    }

    public static String ucfirst(String input) {
        return ucfirst(input, false);
    }

    public static String ucfirst(String text, boolean allWords) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        if (allWords) {
            String[] words = Patterns.SPACE.split(text);

            StringBuilder builder = new StringBuilder();

            for (String word : words) {
                builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
            }

            return builder.toString().trim();
        } else {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }

    public static <R, C, V> void tableRelocate(Table<R, C, V> table, R row, C currentColumn, C newColumn) {
        verifyFalse(table.contains(row, newColumn), "Cannot relocate %s to %s because it already exists", currentColumn, newColumn);
        verifyTrue(table.contains(row, currentColumn), "Cannot relocate %s to %s because it does not exist", currentColumn, newColumn);

        V value = table.get(row, currentColumn);
        table.put(row, newColumn, value);
        table.remove(row, currentColumn);
    }
}
