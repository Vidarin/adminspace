package com.vidarin.adminspace.mixin;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.DimensionInit;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCreeper.class)
public class MixinEntityCreeper extends EntityMob {
    public MixinEntityCreeper(World world) {
        super(world);
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void adminspace$onDeath(DamageSource source, CallbackInfo ci) {
        if (this.world.getGameRules().getBoolean("doMobLoot")) {
            if (this.world.provider.getDimensionType().equals(DimensionInit.DELTAQUEST)) {
                if (this.world.rand.nextInt(50) == 0) {
                    if (this.world.getBlockState(this.getPosition()) == Blocks.AIR.getDefaultState()) {
                        this.world.setBlockState(this.getPosition(), BlockInit.creeperHeart.getDefaultState(), 3);
                    }
                    else if (this.world.getBlockState(this.getPosition().up()) == Blocks.AIR.getDefaultState()) {
                        this.world.setBlockState(this.getPosition().up(), BlockInit.creeperHeart.getDefaultState(), 3);
                    }
                }
            }
        }
    }
}
