package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BlockModSlab extends BlockSlab {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockModSlab(String name, Material material) {
        this(name, null, material);
    }

    public BlockModSlab(String name, @Nullable CreativeTabs tab, Material material) {
        super(material);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);
        //noinspection DataFlowIssue
        this.setCreativeTab(tab);

        this.useNeighborBrightness = true;

        IBlockState baseState = this.blockState.getBaseState();

        if (!this.isDouble()) {
            baseState = baseState.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(baseState.withProperty(VARIANT, Variant.DEFAULT));

        BlockInit.BLOCKS.add(this);
    }

    public static BlockModSlab createSlabSet(String name, @Nullable CreativeTabs tab, Material material, SoundType soundType, float hardness, float resistance) {
        Half halfSlab = (Half) new Half(name, tab, material).setSoundType(soundType).setHardness(hardness).setResistance(resistance);
        Double doubleSlab = (Double) new Double(name + "_double", material).setSoundType(soundType).setHardness(hardness).setResistance(resistance);
        ItemInit.ITEMS.add(new ItemSlab(halfSlab, halfSlab, doubleSlab).setRegistryName(name));
        return halfSlab;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockInit.voidSlab);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(BlockInit.voidSlab);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);

        if (!this.isDouble()) state = state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);

        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;

        if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) meta |= 8;

        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, HALF, VARIANT);
    }

    @Override
    public String getTranslationKey(int meta) {
        return this.getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }

    public static class Half extends BlockModSlab {
        public Half(String name, @org.jetbrains.annotations.Nullable CreativeTabs tab, Material material) {
            super(name, tab, material);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static class Double extends BlockModSlab {
        public Double(String name, Material material) {
            super(name, material);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }

    public enum Variant implements IStringSerializable {
        DEFAULT;

        @Override
        public String getName() {
            return "default";
        }
    }
}