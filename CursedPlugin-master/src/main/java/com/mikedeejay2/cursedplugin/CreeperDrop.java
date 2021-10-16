package com.mikedeejay2.cursedplugin;

import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.mikedeejay2lib.util.raytrace.RayTracer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class CreeperDrop extends EnhancedRunnable
{
    private Player player;
    private Random random;

    public CreeperDrop(Player player)
    {
        this.player = player;
        this.random = new Random();
    }

    @Override
    public void onRun()
    {
        Location upLoc = player.getLocation();
        upLoc.setPitch(-90);
        Vector hitVec = RayTracer.rayTraceVector(upLoc, entity -> entity != player);
        World world = player.getWorld();
        if(hitVec != null) return;
        Location spawnLoc = player.getLocation();
        spawnLoc.setY(100);
        int num = random.nextInt(10000);
        if(num > 10) return;
        Creeper creeper = (Creeper) world.spawnEntity(spawnLoc, EntityType.CREEPER);
        creeper.setFallDistance(-1000);
    }
}
