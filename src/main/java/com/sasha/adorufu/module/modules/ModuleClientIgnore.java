/*
 * Copyright (c) Sasha Stevens 2018.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.adorufu.module.modules;

import com.sasha.adorufu.AdorufuMod;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.adorufu.events.ClientPacketRecieveEvent;
import com.sasha.adorufu.module.ModuleInfo;
import com.sasha.adorufu.module.AdorufuCategory;
import com.sasha.adorufu.module.AdorufuModule;
import net.minecraft.network.play.server.SPacketChat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ModuleInfo(description = "Ignore players on the client side.")
public class ModuleClientIgnore extends AdorufuModule implements SimpleListener {
    public static List<String> ignorelist = new ArrayList<>();
    public static List<String> filterList = new ArrayList<>();

    public ModuleClientIgnore() {
        super("ClientIgnore", AdorufuCategory.CHAT, false, true);
        this.addOption("Players", true);
        /*
         * "Words" setting will choose not to display messages that contain certain words.
         * Useful for muting group harassment or inappropriate language.
         */
        this.addOption("Words", false);
        AdorufuMod.scheduler.schedule(() -> ignorelist = AdorufuMod.DATA_MANAGER.loadIgnorelist(), 0, TimeUnit.MILLISECONDS);
        AdorufuMod.scheduler.schedule(() -> filterList = AdorufuMod.DATA_MANAGER.loadFilterList(), 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {
        this.setSuffix(this.getModuleOptionsMap());
        if (this.isEnabled()) {
            this.setSuffix(ignorelist.size() + ":" + filterList.size());
        }
    }

    @SimpleEventHandler
    public void onPckRx(ClientPacketRecieveEvent e) {
        if (this.isEnabled() && e.getRecievedPacket() instanceof SPacketChat) {
            String msg = ModuleAutoIgnore.stripColours(((SPacketChat) e.getRecievedPacket()).getChatComponent().getUnformattedText());
            if (this.getOption("Players")) {
                for (String s : ignorelist) {
                    if (msg.toLowerCase().startsWith("<" + s.toLowerCase() + ">")) {
                        e.setCancelled(true);
                    }
                }
            }
            if (this.getOption("Words")) {
                String[] msgs = msg.split(" ");
                for (String s : filterList) {
                    for (String msg1 : msgs) {
                        if (msg1.equalsIgnoreCase(s)) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
