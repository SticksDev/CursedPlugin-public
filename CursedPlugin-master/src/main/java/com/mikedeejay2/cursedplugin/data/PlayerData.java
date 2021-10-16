package com.mikedeejay2.cursedplugin.data;

import com.mikedeejay2.cursedplugin.CreeperDrop;
import com.mikedeejay2.cursedplugin.CursedPlugin;
import com.mikedeejay2.cursedplugin.PigStep;
import org.bukkit.entity.Player;

public class PlayerData
{
    private final CursedPlugin plugin;
    private Player player;
    private PigStep pigStep;
    private CreeperDrop creeperDrop;

    public PlayerData(CursedPlugin plugin, Player player)
    {
        this.plugin = plugin;
        this.player = player;
        this.pigStep = new PigStep(player);
        pigStep.runTaskTimer(plugin);
        this.creeperDrop = new CreeperDrop(player);
        creeperDrop.runTaskTimer(plugin);
    }

    public void disable()
    {
        pigStep.cancel();
        creeperDrop.cancel();
    }
}
