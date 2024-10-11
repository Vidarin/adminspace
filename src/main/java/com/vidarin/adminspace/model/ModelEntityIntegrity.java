package com.vidarin.adminspace.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


import javax.annotation.Nonnull;

public class ModelEntityIntegrity extends ModelBase {
	private final ModelRenderer legs;
    private final ModelRenderer arms;
    private final ModelRenderer head;
	private final ModelRenderer bb_main;

    public ModelEntityIntegrity() {
		textureWidth = 64;
		textureHeight = 64;

		legs = new ModelRenderer(this);
		legs.setRotationPoint(0.0F, 11.0F, 0.0F);


        ModelRenderer leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(0.0F, 0.0F, 0.0F);
		legs.addChild(leftleg);
		leftleg.cubeList.add(new ModelBox(leftleg, 28, 0, 2.0F, -5.0F, 0.0F, 4, 18, 0, 0.0F, false));

        ModelRenderer rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(0.0F, 0.0F, 0.0F);
		legs.addChild(rightleg);
		rightleg.cubeList.add(new ModelBox(rightleg, 16, 32, -8.0F, -5.0F, 0.0F, 4, 18, 0, 0.0F, false));

		arms = new ModelRenderer(this);
		arms.setRotationPoint(0.0F, 13.0F, 0.0F);


        ModelRenderer leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(0.0F, 0.0F, 0.0F);
		arms.addChild(leftarm);


        ModelRenderer cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-9.0F, -23.0F, 0.0F);
		leftarm.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.1309F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 8, 23, -4.0F, -7.0F, 0.0F, 4, 26, 0, 0.0F, false));

        ModelRenderer cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(9.0F, -23.0F, 0.0F);
		leftarm.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -0.1309F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 23, -2.0F, -7.0F, 0.0F, 4, 26, 0, 0.0F, false));

        ModelRenderer rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(0.0F, 0.0F, 0.0F);
		arms.addChild(rightarm);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -3.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 16, 23, -6.0F, -23.0F, 0.0F, 10, 9, 0, 0.0F, false));

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -8.0F, -41.0F, 0.0F, 14, 23, 0, 0.0F, false));
	}

	@Override
	public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		legs.render(f5);
		arms.render(f5);
		head.render(f5);
		bb_main.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, @Nonnull Entity entityIn) {
		this.head.rotateAngleY = netHeadYaw * 0.017453292f;
		this.head.rotateAngleX = headPitch * 0.017453292f;
	}
}