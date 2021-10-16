package com.mikedeejay2.cursedplugin;

import com.mikedeejay2.cursedplugin.data.DataManager;
import com.mikedeejay2.cursedplugin.listeners.Listeners;
import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;

public final class CursedPlugin extends BukkitPlugin
{
    private DataManager data;

    @Override
    public void onEnable()
    {
        super.onEnable();

        this.registerEvent(new Listeners(this));

        this.data = new DataManager(this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    public DataManager getData()
    {
        return data;
    }
}
