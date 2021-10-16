package com.mikedeejay2.cursedplugin;

import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.mikedeejay2lib.util.math.MathUtil;
import com.mikedeejay2.mikedeejay2lib.util.particle.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PigStep extends EnhancedRunnable
{
    private Player player;
    private boolean enabled;
    private long countDown;
    private List<LivingEntity> pigs;

    public PigStep(Player player)
    {
        this.player = player;
        this.enabled = false;
        this.countDown = 0;
        this.pigs = new ArrayList<>();
    }

    @Override
    public void onRun()
    {
        --countDown;
        if(countDown <= 0) disable();
        for(Entity entity : player.getNearbyEntities(20, 20, 20))
        {
            if(entity.getType() != EntityType.PIG) continue;
            processPig(entity);
        }
        for(LivingEntity pig : pigs)
        {
            if(pig.isDead())
            {
                countDown -= 2400;
                pigs.remove(pig);
                break;
            }
            Vector velocity = MathUtil.getFacingVector(player.getLocation(), pig.getLocation(), 0.1f);
            pig.setVelocity(velocity);
        }
        if(pigs.size() > 0)
        {
            Location pigLoc = pigs.get(0).getLocation();
            pigLoc.setY(pigLoc.getY() - 1);
            Location newPlayerLoc = player.getLocation();
            newPlayerLoc.setDirection(MathUtil.getFacingVector(pigLoc, player.getLocation(), 1));
            Vector velocity = MathUtil.getFacingVector(pigLoc, newPlayerLoc, 0.1f);
            player.teleport(newPlayerLoc);
            player.setVelocity(velocity);
            World world = pigs.get(0).getWorld();
            for(int x = (int) (pigLoc.getX() - 1); x <= pigLoc.getX() + 1; x++)
            {
                for(int y = (int) (pigLoc.getY() - 1); y <= pigLoc.getY() + 2; y++)
                {
                    for(int z = (int) (pigLoc.getZ() - 1); z <= pigLoc.getZ() + 1; z++)
                    {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
            if(newPlayerLoc.distance(pigLoc) < 2)
            {
                plugin.sendMessage(player, "&4&oPIG STEPPED ON YOU");
                world.spawnEntity(newPlayerLoc, EntityType.PRIMED_TNT);
                disable();
            }
        }
    }

    private void processPig(Entity pig)
    {
        if(pigs.contains(pig)) return;
        pigs.add((LivingEntity) pig);
        ParticleUtil.addParticleToEntity(plugin, pig, Particle.VILLAGER_ANGRY, 1000, 10, 1, 1, 1, 1, false);
        countDown += 2400;
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 1000, 1);
        if(enabled) return;
        enabled = true;
    }

    private void disable()
    {
        pigs.clear();
        enabled = false;
        player.stopSound(Sound.MUSIC_DISC_PIGSTEP);
    }
}
