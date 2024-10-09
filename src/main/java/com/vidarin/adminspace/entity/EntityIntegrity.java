package com.vidarin.adminspace.entity;

import com.vidarin.adminspace.init.SoundInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityIntegrity extends EntityMob {
    private EntityPlayer TARGET;

    private int ticksSinceBlockBreak = 0;
    private int prevDistance = 0;

    public EntityIntegrity(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.0f);
        this.stepHeight = 1.5f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(200.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
    }

    @Override
    public void onLivingUpdate() {
        this.world.getChunkFromBlockCoords(new BlockPos(this.posX, this.posY, this.posZ)).markDirty();

        if (this.TARGET == null || this.TARGET.getDistance(this) > 100) {
            this.setFramerate(120);
            this.TARGET = this.world.getNearestAttackablePlayer(this, 40.0D, 20.0D);
        }
        if (this.TARGET != null) {
            this.TARGET.sendMessage(new TextComponentString("dist:" + this.TARGET.getDistance(this) + " ticksSinceBlockBreak: " + this.ticksSinceBlockBreak));

            this.moveEntity();

            if (this.TARGET.getDistance(this) < 1.5) {
                this.TARGET.sendMessage(new TextComponentString("you deaded..."));
                this.setDead();
            }
            this.setFramerate(5);
        }
    }

    private void moveEntity() {
        double dx = this.TARGET.posX - this.posX;
        double dy = this.TARGET.posY - this.posY;
        double dz = this.TARGET.posZ - this.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double vecX = dx / distance;
        double vecZ = dz / distance;

        BlockPos targetPos = new BlockPos(this.TARGET.posX, this.TARGET.posY, this.TARGET.posZ);

        List<BlockPos> peskyBlocksInTheWay = getBlocksInWay(this.posX, this.posY + 1, this.posZ, targetPos.getX(), targetPos.getY(), targetPos.getZ());

        if (!peskyBlocksInTheWay.isEmpty() && ticksSinceBlockBreak >= 30) {
            BlockPos blockToBreak = peskyBlocksInTheWay.get(0);
            if (this.world.getBlockState(blockToBreak).getBlock().canEntityDestroy(this.world.getBlockState(blockToBreak), this.world, blockToBreak, this)) {
                if ((blockToBreak.getX() < this.posX + 4 && blockToBreak.getX() > this.posX - 4) && (blockToBreak.getY() < this.posY + 6 && blockToBreak.getY() > this.posY - 2) && (blockToBreak.getZ() < this.posZ + 4 && blockToBreak.getZ() > this.posZ - 4)) {
                    this.world.destroyBlock(blockToBreak, true);
                    this.world.setBlockState(blockToBreak, Blocks.AIR.getDefaultState(), 3);
                    this.world.notifyNeighborsOfStateChange(blockToBreak, this.world.getBlockState(blockToBreak).getBlock(), true);
                    peskyBlocksInTheWay.remove(blockToBreak);
                }
                ticksSinceBlockBreak = 0;
            }
        } else {
            if (this.onGround && this.posY < targetPos.getY() && this.canJump()) {
                this.move(MoverType.SELF, vecX / 4, 0.5D, vecZ / 4);
            } else if (!this.onGround) {
                this.move(MoverType.SELF, vecX / 4, -0.5D, vecZ / 4);
            } else {
                this.move(MoverType.SELF, vecX / 4, 0, vecZ / 4);
            }
        }

        int currentDistance = Math.round(this.TARGET.getDistance(this));
        if (currentDistance == prevDistance) {
            ticksSinceBlockBreak += 1;

        } else {
            ticksSinceBlockBreak = 0;
            prevDistance = currentDistance;
        }
    }

    private List<BlockPos> getBlocksInWay(double x1, double y1, double z1, double x2, double y2, double z2) {
        List<BlockPos> blocksInWay = new ArrayList<>();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double vecX = dx / distance;
        double vecY = dy / distance;
        double vecZ = dz / distance;

        for (int i = 1; i <= 10; i++) {
            BlockPos pos = new BlockPos(Math.round(x1 + vecX * i), Math.round(y1 + vecY * i), Math.round(z1 + vecZ * i));
            if (this.world.getBlockState(pos).isFullBlock()) {
                blocksInWay.add(pos);
            }
        }
        return blocksInWay;
    }

    private boolean canJump() {
        IBlockState blockFront = this.world.getBlockState(new BlockPos(this.posX + 1, this.posY, this.posZ));
        IBlockState blockBack = this.world.getBlockState(new BlockPos(this.posX - 1, this.posY, this.posZ));
        IBlockState blockLeft = this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ + 1));
        IBlockState blockRight = this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ - 1));

        IBlockState blockFrontUp = this.world.getBlockState(new BlockPos(this.posX + 1, this.posY + 1, this.posZ));
        IBlockState blockBackUp = this.world.getBlockState(new BlockPos(this.posX - 1, this.posY + 1, this.posZ));
        IBlockState blockLeftUp = this.world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ + 1));
        IBlockState blockRightUp = this.world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ - 1));

        return (blockFront.isFullBlock() && !blockFrontUp.isFullBlock()) || (blockBack.isFullBlock() && !blockBackUp.isFullBlock()) || (blockLeft.isFullBlock() && !blockLeftUp.isFullBlock()) || (blockRight.isFullBlock() && !blockRightUp.isFullBlock());
    }

    @SideOnly(Side.CLIENT)
    private void setFramerate(int framerate) {
        if (this.TARGET != null && this.TARGET == Minecraft.getMinecraft().player) {
            Minecraft.getMinecraft().gameSettings.limitFramerate = framerate;
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, 0);
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        this.setFramerate(120);
    }

    @Override
    public void setDead() {
        super.setDead();
        this.setFramerate(120);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT; //Placeholder sound
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_BLAZE_HURT; //Placeholder sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.DEATH_EASTER_EGG; //Should be a 1/10000 chance
    }
}