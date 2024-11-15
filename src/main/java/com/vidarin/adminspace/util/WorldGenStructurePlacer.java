package com.vidarin.adminspace.util;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class WorldGenStructurePlacer extends WorldGenerator
{
    public static final WorldServer worldServer = Objects.requireNonNull(FMLCommonHandler.instance().getMinecraftServerInstance().getServer()).getWorld(0);
    public static final PlacementSettings settings = new PlacementSettings().setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);

    public static String structureName;

    public WorldGenStructurePlacer(String name) {
        structureName = name;
    }

    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        this.generateStructure(worldIn, position, Rotation.NONE);
        return true;
    }

    public void generateWithRotation(World worldIn, BlockPos position, Rotation rotation) {
        final BlockPos newPosition = this.posByRotation(position, rotation, this.templateSize(worldIn));
        this.generateStructure(worldIn, newPosition, rotation);
    }

    public void generateStructure(World world, BlockPos pos, Rotation rot) {
        final Template template = this.loadTemplate(world);
        if (template != null) {
            settings.setRotation(rot);
            template.addBlocksToWorldChunk(world, pos, settings);
        }
    }

    public BlockPos templateSize(World world) {
        final Template template = this.loadTemplate(world);
        if (template != null) {
            return template.getSize();
        }
        return null;
    }

    private BlockPos posByRotation(BlockPos pos, Rotation rotation, BlockPos endPos) {
        switch (rotation) {
            case CLOCKWISE_90: {
                return pos.add(endPos.getZ() - 1, 0, 0);
            }
            case CLOCKWISE_180: {
                return pos.add(endPos.getX() - 1, 0, endPos.getZ() - 1);
            }
            case COUNTERCLOCKWISE_90: {
                return pos.add(0, 0, endPos.getX() - 1);
            }
            default: {
                return pos;
            }
        }
    }

    @Nullable
    private Template loadTemplate(final World world) {
        final MinecraftServer mcServer = world.getMinecraftServer();
        final TemplateManager manager = worldServer.getStructureTemplateManager();
        final ResourceLocation location = new ResourceLocation(Adminspace.MODID, structureName);
        return manager.get(mcServer, location);
    }
}
