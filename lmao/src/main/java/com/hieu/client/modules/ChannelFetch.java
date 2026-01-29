package com.hieu.client.modules;

import com.hieu.client.Main;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ChannelFetch extends Module {

    public ChannelFetch() {
        super(Main.CATEGORY, "Channel Fetcher", "gets the current open channels in the minecraft server");
    }

}
