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

/**
 * Mixin for {@link PlayerInventory} to track item stack changes and notify modded items.
 */
@Mixin( PlayerInventory.class )
public abstract class PlayerInventoryMixin implements Inventory {

    /**
     * Stores the previous item stack before it's replaced.
     * Used to compare old and new stacks when notifying modded items of inventory changes.
     */
    @Unique
    private ItemStack oldStack = ItemStack.EMPTY;

    /**
     * Captures the old item stack before it's replaced.
     * Injected at the HEAD of {@code setStack} to save the current stack.
     */
    @Inject( at = @At("HEAD"), method = "setStack(ILnet/minecraft/item/ItemStack;)V" )
    private void harmonySetOldStack( int slot, ItemStack newStack, CallbackInfo callback ) {

        oldStack = getStack(slot).copy();

    }

    /**
     * Notifies modded items when their stack changes in the inventory.
     * Injected at the TAIL of {@code setStack} after the stack has been replaced.
     */
    @Inject( at = @At("TAIL"), method = "setStack(ILnet/minecraft/item/ItemStack;)V" )
    private void harmonyOnStackSet( int slot, ItemStack newStack, CallbackInfo callback ) {

        Item item = newStack.getItem();         boolean isModded = item instanceof ModItem;

        if ( !isModded ) return;


        ModItem modItem = (ModItem) item;           modItem.onInventoryChange( oldStack, newStack );

        oldStack = ItemStack.EMPTY;

    }

}
