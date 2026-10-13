/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.event;

import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BukkitEventProvider extends EventProvider {

    default void runSync(@NotNull Runnable runnable) {
        if (getPlugin().getMorePaperLib() != null) {
            if (getPlugin().getMorePaperLib().scheduling().isOnGlobalRegionThread()) {
                runnable.run();
            } else {
                getPlugin().getMorePaperLib().scheduling().globalRegionalScheduler().run(runnable);
            }
        } else if (getPlugin().getServer().isPrimaryThread()) {
            runnable.run();
        } else {
            getPlugin().getServer().getScheduler().runTask(getPlugin(), runnable);
        }
    }

    @Override
    default CompletableFuture<ChatMessageEvent> fireChatMessageEvent(@NotNull OnlineUser player,
                                                                    @NotNull String message,
                                                                    @NotNull String channelId) {
        final CompletableFuture<ChatMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitChatMessageEvent event = new BukkitChatMessageEvent(player, message, channelId);
        runSync(() -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<PrivateMessageEvent> firePrivateMessageEvent(@NotNull OnlineUser sender,
                                                                          @NotNull List<OnlineUser> receivers,
                                                                          @NotNull String message) {
        final CompletableFuture<PrivateMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitPrivateMessageEvent event = new BukkitPrivateMessageEvent(sender, receivers, message);
        runSync(() -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    @Override
    default CompletableFuture<BroadcastMessageEvent> fireBroadcastMessageEvent(@NotNull OnlineUser sender,
                                                                              @NotNull String message) {
        final CompletableFuture<BroadcastMessageEvent> completableFuture = new CompletableFuture<>();
        final BukkitBroadcastMessageEvent event = new BukkitBroadcastMessageEvent(sender, message);
        runSync(() -> {
            getPlugin().getServer().getPluginManager().callEvent(event);
            completableFuture.complete(event);
        });
        return completableFuture;
    }

    BukkitHuskChat getPlugin();

}
