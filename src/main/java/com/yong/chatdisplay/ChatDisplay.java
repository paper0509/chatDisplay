package com.yong.chatdisplay;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

import java.awt.*;

public final class ChatDisplay extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("[ChatDisplay] start");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[ChatDisplay] stop");
    }

    @EventHandler
    public void onChat(PlayerChatEvent event){
        event.setCancelled(true);

        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Color color = Color.fromARGB(0, 0, 0, 0);
        String content = event.getMessage();
        float accent = accentCount(content) * 0.1f;

        player.getWorld().playSound(loc, Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1, 2);

        for (Entity passengers : player.getPassengers()){
            if (passengers instanceof TextDisplay oldChat) {
                addTransScale(oldChat, 0.3f + (accent * 0.3f), 0);
            }
        }

        TextDisplay nowChat = (TextDisplay) player.getWorld().spawnEntity(loc.add(0, 1.5, 0), EntityType.TEXT_DISPLAY);
        Transformation nowTrans = nowChat.getTransformation();

        Bukkit.getServer().getScheduler().runTaskLater(this, () -> {
            nowChat.remove();
        }, 40L);

        addTransScale(nowChat, 0.3f, accent);
        nowChat.addScoreboardTag("ChatDisplay");
        nowChat.setText(content);
        nowChat.setBackgroundColor(color);
        nowChat.setBillboard(Display.Billboard.CENTER);
        nowChat.setShadowed(true);

        player.addPassenger(nowChat);

    }
    public void addTransScale(TextDisplay chat, float height, float scale){
        Transformation trans = chat.getTransformation();
        trans.getTranslation().add(0, height, 0);
        trans.getScale().add(scale, scale, scale);
        chat.setTransformation(trans);
    }

    public int accentCount(String str){
        int count = 0;
        int consecutiveCount = 0;
        for (int i = 0; i < str.length(); i++){
            if (str.charAt(i) == '!'){
                count++;
            } else if (str.charAt(i) == '.') {
                consecutiveCount++;
                if(consecutiveCount > 1){
                    count--;
                }
            }
        }
        return count;
    }
}
