package com.mikedeejay2.cursedplugin.listeners;

import com.mikedeejay2.cursedplugin.CursedPlugin;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.mikedeejay2lib.util.block.BlockIterator;
import com.mikedeejay2.mikedeejay2lib.util.block.FallingBlockUtil;
import com.mikedeejay2.mikedeejay2lib.util.chat.Colors;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class Listeners implements Listener
{
    private final CursedPlugin plugin;
    private Random random;

    public Listeners(CursedPlugin plugin)
    {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event)
    {
        plugin.getData().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event)
    {
        plugin.getData().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void walkEvent(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location playerLoc = player.getEyeLocation();
        World world = player.getWorld();
        int randomNum = random.nextInt(2000);
        if(randomNum < 2)
        {
            Block block = world.getBlockAt(playerLoc);
            block.setType(Material.LAVA);
            new EnhancedRunnable()
            {
                @Override
                public void onRun()
                {
                    block.setType(Material.AIR);
                }
            }.runTaskLater(plugin, 5 * 20);
        }
        Location blockLoc = player.getLocation();
        blockLoc.setY(blockLoc.getY()-1);
        Block blockfeet = world.getBlockAt(blockLoc);
        switch(blockfeet.getType())
        {
            case SAND:
                blockfeet.setType(Material.GLASS);
                break;
            case CACTUS:
                for(int y = blockLoc.getBlockY(); y < 256; y++)
                {
                    world.getBlockAt(blockLoc.getBlockX(), y, blockLoc.getBlockZ()).setType(Material.CACTUS);
                }
                break;
            case GRASS_BLOCK:
                blockfeet.setType(Material.DIRT);
                break;
            case STONE:
                blockfeet.setType(Material.COBBLESTONE);
                break;
            case DIAMOND_ORE:
                blockfeet.setType(Material.COAL);
                break;
            case DEAD_BUSH:
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, true, false, false));
                break;
        }

        BlockIterator.iterateBlocks(player.getLocation(), 10, 10, 10, (location, block) -> {
            Location blockBelow = location.clone();
            blockBelow.setY(blockBelow.getY() - 1);
            Block newBloc = world.getBlockAt(blockBelow);
            if(block.getType().toString().endsWith("AIR")) return;
            if(newBloc.getType().toString().endsWith("AIR"))
            {
                FallingBlockUtil.makeBlockFall(block);
            }
        }, plugin);
    }

    @EventHandler
    public void shootBowEvent(ProjectileLaunchEvent event)
    {
        Projectile proj = event.getEntity();
        float offX = ((random.nextFloat()) - 0.5f);
        float offY = ((random.nextFloat()) - 0.5f);
        float offZ = ((random.nextFloat()) - 0.5f);
        proj.setVelocity(proj.getVelocity().add(new Vector(offX, offY, offZ)));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        BlockIterator.iterateBlocksSphere(location, 5, (location1, block) -> FallingBlockUtil.makeBlockExplode(block, 2), plugin);
    }

    @EventHandler
    public void blockSpreadEvent(BlockSpreadEvent event)
    {
        Block block = event.getSource();
        BlockData blockData = block.getBlockData();
        Location location = block.getLocation();
        BlockIterator.iterateBlocks(location, 3, 3, 3, ((newLoc, newBlock) -> {
            if(newBlock.getType() != Material.AIR && newBlock.getType() != Material.FIRE)
            {
                return;
            }
            newBlock.setBlockData(blockData);
        }), plugin);
    }

    @EventHandler
    public void moistureChangeEvent(MoistureChangeEvent event)
    {
        Location location = event.getBlock().getLocation();
        World world = event.getBlock().getWorld();
        BlockIterator.iterateBlocks(location, 3, 3, 3, ((newLoc, newBlock) -> {
            world.getBlockAt(newLoc).setType(Material.OBSIDIAN);
        }), plugin);
    }

    @EventHandler
    public void furnaceBurnEvent(FurnaceBurnEvent event)
    {
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTimeTotal(1000);
        furnace.update();
    }

    @EventHandler
    public void furnaceSmeltEvent(FurnaceSmeltEvent event)
    {
        Furnace furnace = (Furnace) event.getBlock().getState();
        ItemStack result = furnace.getInventory().getResult();
        furnace.setCookTimeTotal(1000);
        furnace.update();
        Location location = furnace.getLocation();
        World world = location.getWorld();
        new EnhancedRunnable()
        {
            @Override
            public void onRun()
            {
                Item item = world.dropItemNaturally(location, result);
                item.setVelocity(new Vector(random.nextFloat()-0.5f, random.nextFloat()-0.5f, random.nextFloat()-0.5f).multiply(2));
                world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                for(int i = 0; i < 10; i++)
                {
                    Entity entity = world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
                    entity.setVelocity(new Vector(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f).multiply(2));
                }
                world.spawnParticle(Particle.CRIT, location, 10, 1, 1, 1);
            }
        }.runTaskTimerCounted(plugin, 1, 64);
    }

    @EventHandler
    public void spawnEvent(EntitySpawnEvent event)
    {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        World world = entity.getWorld();

        switch(entity.getType())
        {
            case BAT:
            {
                world.spawnEntity(location, EntityType.PRIMED_TNT);
                break;
            }
            case ZOMBIE:
            {
                Zombie zombie = (Zombie)entity;
                zombie.setBaby();
                zombie.setCustomName(Colors.format("&4&l&kWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWEWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWEWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWE"));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 100, false, false, false));
                zombie.getEquipment().setBoots(
                    ItemBuilder.of(Material.DIAMOND_BOOTS)
                        .setAmount(10)
                        .setName("&4&l&kWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWEWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWEWEOIFJOWIEJFOIEWJFOIWEJFJOWEIFJWEOJFOWIEJFOWEIJFJOWEIJOWEIJFOIWEJFOIWEJOIWEJOFIWEJFOIWEJOIFJWEOIFJWE")
                        .get());
                break;
            }
        }
    }
}
