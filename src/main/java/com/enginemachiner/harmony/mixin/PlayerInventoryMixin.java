package com.enginemachiner.harmony.mixin;

import com.enginemachiner.harmony.ModItem;
import net.minecraft.entity.player.PlayerInventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( PlayerInventory.class )
public abstract class PlayerInventoryMixin implements Inventory {

    @Unique
    private ItemStack oldStack = ItemStack.EMPTY;

    @Inject( at = @At("HEAD"), method = "setStack(ILnet/minecraft/item/ItemStack;)V" )
    private void harmonySetOldStack( int slot, ItemStack newStack, CallbackInfo callback ) {

        oldStack = getStack(slot).copy();

    }

    @Inject( at = @At("TAIL"), method = "setStack(ILnet/minecraft/item/ItemStack;)V" )
    private void harmonyOnStackSet( int slot, ItemStack newStack, CallbackInfo callback ) {

        Item item = newStack.getItem();         boolean isModded = item instanceof ModItem;

        if ( !isModded ) return;


        ModItem modItem = (ModItem) item;           modItem.onInventoryChange( oldStack, newStack );

        oldStack = ItemStack.EMPTY;

    }

}
