package com.vidarin.adminspace.worldgen;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenStructurePlacer extends WorldGenerator {
    public final PlacementSettings settings;
    public final String structureName;

    @SuppressWarnings("DataFlowIssue")
    public WorldGenStructurePlacer(String name, @Nullable ChunkPos pos) {
        structureName = name;
        settings = new PlacementSettings().setChunk(pos).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, @Nullable Random rand, BlockPos position) {
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
        return switch (rotation) {
            case CLOCKWISE_90 -> pos.add(endPos.getZ() - 1, 0, 0);
            case CLOCKWISE_180 -> pos.add(endPos.getX() - 1, 0, endPos.getZ() - 1);
            case COUNTERCLOCKWISE_90 -> pos.add(0, 0, endPos.getX() - 1);
            default -> pos;
        };
    }

    @Nullable
    private Template loadTemplate(final World world) {
        if (world instanceof WorldServer worldServer) {
            final MinecraftServer mcServer = world.getMinecraftServer();
            final TemplateManager manager = worldServer.getStructureTemplateManager();
            final ResourceLocation location = new ResourceLocation(Adminspace.MODID, structureName);
            return manager.get(mcServer, location);
        } throw new IllegalArgumentException("Tried to generate structure on client!");
    }
}