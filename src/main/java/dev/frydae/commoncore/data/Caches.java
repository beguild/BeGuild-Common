package dev.frydae.commoncore.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import dev.frydae.commoncore.user.RegisteredUser;
import dev.frydae.commoncore.user.UserManager;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Caches {
    private static final RemovalListener<Object, RegisteredUser> USER_REMOVAL_LISTENER = notification -> {
        RegisteredUser user = notification.getValue();

        switch (notification.getCause()) {
            case EXPIRED, SIZE:
                if (user != null && user.getPlayer() != null && !user.getPlayer().isDisconnected()) {
                    UserManager.refreshCaches(user);
                }
        }
    };

    public static Cache<UUID, RegisteredUser> userUuidCache =
            CacheBuilder.newBuilder()
                    .maximumSize(256)
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .removalListener(USER_REMOVAL_LISTENER)
                    .build();

    public static Cache<String, RegisteredUser> userNameCache =
            CacheBuilder.newBuilder()
                    .maximumSize(256)
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .removalListener(USER_REMOVAL_LISTENER)
                    .build();

    public static Cache<String, ServerWorld> worldNameCache =
            CacheBuilder.newBuilder()
                    .maximumSize(256)
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .build();
}
