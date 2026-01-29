package com.hieu.client.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeUsername extends Command {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ChangeUsername() {
        super("changeusername", "Change the username with an access token.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Missing Arguments: changeusername <accessToken> <newUsername>");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("accessToken", StringArgumentType.word())
            .then(argument("username", StringArgumentType.word()).executes(context -> {
                String accessToken = StringArgumentType.getString(context, "accessToken");
                String username = StringArgumentType.getString(context, "username");

                executor.execute(() -> {
                    try {
                        URL url = new URL("https://api.minecraftservices.com/minecraft/profile/name/" + username);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("PUT");
                        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setDoOutput(true);

                        int responseCode = connection.getResponseCode();
                        BufferedReader reader = null;
                        StringBuilder response = new StringBuilder();

                        if (responseCode >= 400) {
                            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        } else {
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        }
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        if (responseCode == 403) {
                            info("Name is already taken or Player is on username change delay.");
                        } else if (responseCode == 400) {
                            info("Invalid profile name.");
                        } else if (responseCode == 429) {
                            info("Too many requests.");
                        } else if (responseCode == 200) {
                            info("Username changed to " + username + ".");
                        } else {
                            info("Unexpected response code: " + responseCode + " | Response: " + response.toString());
                        }

                    } catch (IOException e) {
                        error("Couldn't change username: " + e.getMessage());
                    }
                });

                return SINGLE_SUCCESS;
            }))
        );
    }
}
