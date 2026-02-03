package com.vidarin.adminspace.util;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TerminalCommandHandler {

    private EntityPlayer player;
    private World world;
    private TileEntityTerminal terminal;

    private String commandStored = "";

    private int permLevel;

    public void sendCommandParams(EntityPlayer player, World world, TileEntityTerminal terminal) {
        this.player = player;
        this.world = world;
        this.terminal = terminal;
        this.permLevel = terminal.permLevel;
    }

    public void runCommand(String command) {
        String path = "";
        String commandArgs = "";
        try {
            path = command.split(";")[0];
            commandArgs = command.replaceAll(path + ";", "");
        }
        catch (ArrayIndexOutOfBoundsException ignored) {}

        String classPath = "com.vidarin.adminspace.util.terminalcommands." + path;

        Class<?> cls;
        try {
            cls = Class.forName(classPath);
        }
        catch (ClassNotFoundException e) {
            cls = TermUtil.class;
        }

        try {
            Method method = cls.getMethod("execute", TerminalCommandHandler.class, String[].class);
            method.invoke(null, this, commandArgs.split("/"));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        setCommandStored("");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("Command", this.commandStored);

        return compound;
    }

    public void readDataFromNBT(NBTTagCompound data) {
        this.commandStored = data.getString("Command");
    }

    public String getCommandStored() {
        return this.commandStored;
    }

    public void setCommandStored(String command) {
        this.commandStored = command;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public TileEntityTerminal getTerminal() {
        return this.terminal;
    }

    public int getPermLevel() {
        return this.permLevel;
    }
}
