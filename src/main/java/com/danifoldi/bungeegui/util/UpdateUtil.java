package com.danifoldi.bungeegui.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UpdateUtil {

    private static final String QUERY_URL = "https://api.github.com/repos/DaniFoldi/BungeeGui/releases/latest";

    public static CompletableFuture<String> getNewest() {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(QUERY_URL))
                    .build();

            try {

                return HttpClient.newHttpClient()
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenApply(b -> new Gson().fromJson(b, JsonElement.class).getAsJsonObject())
                        .thenApply(o -> o.get("tag_name").getAsString())
                        .join();

            } catch (Exception e) {
                return "";
            }
        });
    }

    private UpdateUtil() {
        throw new UnsupportedOperationException();
    }
}
