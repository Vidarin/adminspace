package com.vidarin.adminspace.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class ItemDismantler extends ItemBase {
    private final float efficiency;
    private final float attackDamage;
    private final float attackSpeed;

    public ItemDismantler() {
        super("dismantler", CreativeTabs.TOOLS);
        this.setMaxStackSize(1);
        this.setMaxDamage(1598);
        this.efficiency = 9.0F;
        this.attackDamage = 3.8F;
        this.attackSpeed = -2.8F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) entityIn;
            if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemDismantler) {
                if (!stack.hasTagCompound()) {
                    ItemStack newDismantler = new ItemStack(ItemInit.dismantler);
                    NBTTagCompound compound = new NBTTagCompound();

                    compound.setBoolean("Activated", false);
                    compound.setString("Activation_Code", getRandomCode(player));
                    compound.setString("Owner", player.getName());
                    compound.setLong("Last_Use", System.currentTimeMillis());
                    compound.setBoolean("In_Cooldown", false);

                    newDismantler.setTagCompound(compound);

                    player.setHeldItem(EnumHand.MAIN_HAND, newDismantler);
                    return;
                }
            }
        }
        if (worldIn.isRemote) {
            EntityPlayerSP player = (EntityPlayerSP) entityIn;
            if (stack.getItem() instanceof ItemDismantler) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = Objects.requireNonNull(stack.getTagCompound());
                    if (System.currentTimeMillis() - compound.getLong("Last_Use") > 1200 && compound.getBoolean("In_Cooldown")) {
                        player.playSound(SoundInit.DISMANTLER_RECHARGE, 0.8F, 1.0F);
                        compound.setBoolean("In_Cooldown", false);
                    }
                }
            }
        }
    }

    private String getRandomCode(EntityPlayer player) {
        Random rand = player.getRNG();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int ascii = rand.nextInt(74) + 48;
            char c = (char) ascii;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.getHeldItem(handIn).hasTagCompound()) {
            NBTTagCompound compound = playerIn.getHeldItem(handIn).getTagCompound();
            assert compound != null;

            if (playerIn.getName().equals(compound.getString("Owner"))) {
                if (compound.getBoolean("Activated")) {
                    if (System.currentTimeMillis() - compound.getLong("Last_Use") >= 1200) {
                        Vec3d velocity = playerIn.getLookVec().scale(1.6D);

                        playerIn.addVelocity(velocity.x, velocity.y, velocity.z);
                        playerIn.playSound(SoundInit.DISMANTLER_DASH, 1.5F, 1.0F);

                        compound.setLong("Last_Use", System.currentTimeMillis());
                        compound.setBoolean("In_Cooldown", true);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
                    }
                }
            }
            else {
                playerIn.attackEntityFrom(DamageSource.GENERIC, 8);
                if (playerIn.world.isRemote) playerIn.sendMessage(new TextComponentString(Fonts.Dark_Red + "This Dismantler is not bound to you!"));
                return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            }
        }

        return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            assert compound != null;
            if (!compound.getBoolean("Activated")) tooltip.add(Fonts.Gray + "Activation Code: " + compound.getString("Activation_Code"));
            else tooltip.add(Fonts.Blue + "ACTIVATED");
            tooltip.add(Fonts.Dark_Gray + "Bound to " + compound.getString("Owner"));
        } else
            tooltip.add(Fonts.Red + "UNBOUND");}

    @Override
    @Nullable
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound() : null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return efficiency;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canHarvestBlock(IBlockState blockIn) {
        return blockIn.getBlock().getHarvestLevel(blockIn) <= 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        stack.damageItem(2, attacker);

        assert stack.getTagCompound() != null;
        if (!attacker.getName().equals(stack.getTagCompound().getString("Owner"))) {
            attacker.attackEntityFrom(DamageSource.GENERIC, 8);
            if (attacker.world.isRemote) attacker.sendMessage(new TextComponentString(Fonts.Dark_Red + "This Dismantler is not bound to you!"));
        }

        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        if (worldIn.getBlockState(pos).getBlockHardness(worldIn, pos) >= 0.0D)
            stack.damageItem(1, entityLiving);

        assert stack.getTagCompound() != null;
        if (!entityLiving.getName().equals(stack.getTagCompound().getString("Owner"))) {
            entityLiving.attackEntityFrom(DamageSource.GENERIC, 8);
            if (entityLiving.world.isRemote) entityLiving.sendMessage(new TextComponentString(Fonts.Dark_Red + "This Dismantler is not bound to you!"));
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        ItemStack mat = new ItemStack(ItemInit.noiseGem);
        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this.attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", this.attackSpeed, 0));
        }

        return multimap;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable net.minecraft.entity.player.EntityPlayer player, @javax.annotation.Nullable IBlockState blockState)
    {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull Set<String> getToolClasses(ItemStack stack)
    {
        return ImmutableSet.of("pickaxe", "axe", "shovel");
    }
}
