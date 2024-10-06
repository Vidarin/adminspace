package com.vidarin.adminspace.entity;

import com.vidarin.adminspace.main.PacketHandler;
import com.vidarin.adminspace.packets.BreakBlockPacket;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityIntegrity extends EntityMob {
    private EntityPlayer TARGET;

    private int ticksUntilBlockBreak = 0;
    private int prevDistance = 0;

    public EntityIntegrity(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 3.0f);
        this.stepHeight = 1.5f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(200.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.2D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
    }

    @Override
    public void onLivingUpdate() {
        this.world.getChunkFromBlockCoords(new BlockPos(this.posX, this.posY, this.posZ)).markDirty();

        if (this.TARGET == null || this.TARGET.getDistance(this) > 100)
            this.TARGET = this.world.getNearestAttackablePlayer(this, 40.0D, 20.0D);
        if (this.TARGET != null) {
            this.TARGET.sendMessage(new TextComponentString("YOU'RE THE TARGET! dist:" +this.TARGET.getDistance(this) + " ticksUntilBlockBreak: " +this.ticksUntilBlockBreak));
            this.moveEntity();
        }
    }

    private void moveEntity() {
        double dx = this.TARGET.posX - this.posX;
        double dy = this.TARGET.posY - this.posY;
        double dz = this.TARGET.posZ - this.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double vecX = dx / distance;
        double vecY = dy / distance;
        double vecZ = dz / distance;

        if (distance < 1.5) {
            if ((this.onGround || this.world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)).isFullBlock()) || vecY <= 0)
                this.move(MoverType.SELF, 0, vecY / 3, 0);
            else if (!(this.onGround || this.world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)).isFullBlock()) && vecY > 0)
                this.move(MoverType.SELF, 0, -vecY / 3, 0);
        } else {
            BlockPos targetPos = new BlockPos(this.TARGET.posX, this.TARGET.posY, this.TARGET.posZ);

            List<BlockPos> peskyBlocksInTheWay = getBlocksInWay(this.posX, this.posY + 2, this.posZ, targetPos.getX(), targetPos.getY(), targetPos.getZ());

            if (!peskyBlocksInTheWay.isEmpty() && ticksUntilBlockBreak >= 30) {
                BlockPos blockToBreak = peskyBlocksInTheWay.get(0);
                if (this.world.getBlockState(blockToBreak).getBlock().canEntityDestroy(this.world.getBlockState(blockToBreak), this.world, blockToBreak, this)) {
                    if ((blockToBreak.getX() < this.posX + 4 && blockToBreak.getX() > this.posX - 4) && (blockToBreak.getY() < this.posY + 6 && blockToBreak.getY() > this.posY - 2) && (blockToBreak.getZ() < this.posZ + 4 && blockToBreak.getZ() > this.posZ - 4)) {
                        this.world.destroyBlock(blockToBreak, true);
                        PacketHandler.INSTANCE.sendToServer(new BreakBlockPacket(blockToBreak));
                        peskyBlocksInTheWay.remove(blockToBreak);
                    }
                    ticksUntilBlockBreak = 0;
                }
            } else {
                if (this.onGround && this.posY < targetPos.getY()) {
                    this.move(MoverType.SELF, vecX / 3, 0.5D, vecZ / 3);
                } else if (!this.onGround) {
                    this.move(MoverType.SELF, vecX / 3, -0.5D, vecZ / 3);
                } else {
                    this.move(MoverType.SELF, vecX / 3, 0, vecZ / 3);
                }
            }

            int currentDistance = Math.round(this.TARGET.getDistance(this));
            if (currentDistance == prevDistance) {
                ticksUntilBlockBreak += 1;

            } else {
                ticksUntilBlockBreak = 0;
                prevDistance = currentDistance;
            }
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

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, 0);
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
        return SoundEvents.ENTITY_BLAZE_DEATH; //Placeholder sound
    }
}