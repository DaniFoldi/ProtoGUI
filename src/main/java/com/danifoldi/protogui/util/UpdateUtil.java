package com.danifoldi.protogui.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class UpdateUtil {

    private static final @NotNull String GITHUB_URL = "https://api.github.com/repos/DaniFoldi/ProtoGui/releases/latest";
    private static final @NotNull String SPIGET_URL = "https://api.spiget.org/v2/resources/92209/versions/latest";

    public static CompletableFuture<String> getNewest(ExecutorService threadPool) {
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
        }, threadPool);
    }

    public static boolean isNewer(String current, String available) {
        String[] currentS = current.split("-")[0].split("\\.");
        String[] availableS = available.split("-")[0].split("\\.");
        int[] currentI = new int[currentS.length];
        int[] availableI = new int[availableS.length];
        for (int i = 0; i < currentS.length; i++) {
            try {
                currentI[i] = Integer.parseInt(currentS[i]);
            } catch (NumberFormatException ignored) {

            }
        }
        for (int i = 0; i < availableS.length; i++) {
            try {
                availableI[i] = Integer.parseInt(availableS[i]);
            } catch (NumberFormatException ignored) {

            }
        }
        final int maxLength = Math.max(currentI.length, availableI.length);
        for (int i = 0; i < maxLength; i++) {
            final int left = i < currentI.length ? currentI[i] : 0;
            final int right = i < availableI.length ? availableI[i] : 0;
            if (left != right) {
                return left >= right;
            }
        }
        return false;
    }

    private UpdateUtil() {
        throw new UnsupportedOperationException();
    }
}
