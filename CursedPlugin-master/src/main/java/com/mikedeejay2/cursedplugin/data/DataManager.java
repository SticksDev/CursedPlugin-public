package com.mikedeejay2.cursedplugin.data;

import com.mikedeejay2.cursedplugin.CursedPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DataManager
{
    private final CursedPlugin plugin;
    private HashMap<Player, PlayerData> data;

    public DataManager(CursedPlugin plugin)
    {
        this.plugin = plugin;
        this.data = new HashMap<>();

        for(Player player : Bukkit.getOnlinePlayers())
        {
            addPlayer(player);
        }
    }

    public void addPlayer(Player player)
    {
        data.put(player, new PlayerData(plugin, player));
    }

    public void removePlayer(Player player)
    {
        data.get(player).disable();
        data.remove(player);
    }
}
