package com.vidarin.adminspace.util;

import com.vidarin.adminspace.block.BlockTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


public class TerminalCommandHandler {
    private Logger logger;

    private EntityPlayer player;
    private World world;
    private BlockTerminal terminal;

    public void sendCommandParams(EntityPlayer player, World world, BlockTerminal terminal) {
        this.player = player;
        this.world = world;
        this.terminal = terminal;
    }

    public void runCommand(String command) {
        switch (command) {
            case "test":
                logger.log(Level.DEBUG, "this is a test message. i made it this long so it will be easy to find in the super bloated minecraft log, if this appears in a release version, you should open a github issue.");
            case "iamadataminerandilovetotrystupidthingsifindinthecode":
                logger.log(Level.INFO, "ok");
        }
    }
}
