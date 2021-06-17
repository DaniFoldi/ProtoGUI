package com.danifoldi.bungeegui.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateUtil {

    private static final @NotNull String GITHUB_URL = "https://api.github.com/repos/DaniFoldi/BungeeGui/releases/latest";
    private static final @NotNull String SPIGET_URL = "https://api.spiget.org/v2/resources/92209/versions/latest";

    public static CompletableFuture<String> getNewest() {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull HttpRequest ghRequest = HttpRequest
                    .newBuilder()
                    .uri(URI.create(GITHUB_URL))
                    .build();

            try {
                return HttpClient.newHttpClient()
                        .sendAsync(ghRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenApply(b -> new Gson().fromJson(b, JsonElement.class).getAsJsonObject())
                        .thenApply(o -> o.get("tag_name").getAsString())
                        .join();

            } catch (Exception e) {
                Logger.getAnonymousLogger().warning(e.getMessage());
                final @NotNull HttpRequest sgRequest = HttpRequest
                        .newBuilder()
                        .uri(URI.create(SPIGET_URL))
                        .build();

                try {
                    return HttpClient.newHttpClient()
                            .sendAsync(sgRequest, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::body)
                            .thenApply(b -> new Gson().fromJson(b, JsonElement.class).getAsJsonObject())
                            .thenApply(o -> o.get("name").getAsString())
                            .join();
                } catch (Exception e2) {
                    Logger.getAnonymousLogger().warning(e2.getMessage());
                    return "";
                }
            }
        });
    }

    private UpdateUtil() {
        throw new UnsupportedOperationException();
    }
}
