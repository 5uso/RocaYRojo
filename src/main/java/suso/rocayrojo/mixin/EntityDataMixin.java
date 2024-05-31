package suso.rocayrojo.mixin;

import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(EntityDataObject.class)
public class EntityDataMixin {
    @Shadow @Final private Entity entity;

    @Unique @Nullable
    private static GameMode gameModeFromNbt(@Nullable NbtCompound nbt) {
        return nbt != null && nbt.contains("playerGameType", 99) ? GameMode.byId(nbt.getInt("playerGameType")) : null;
    }

    @Inject(
            method = "setNbt(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At(
                    value = "INVOKE",
                    target ="Lcom/mojang/brigadier/exceptions/SimpleCommandExceptionType;create()Lcom/mojang/brigadier/exceptions/CommandSyntaxException;",
                    remap = false
            ),
            cancellable = true
    )
    public void setPlayerNbt(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity p = (ServerPlayerEntity) this.entity;
        UUID uUID = p.getUuid();
        NbtCompound previous = p.writeNbt(new NbtCompound());
        p.readNbt(nbt); p.setUuid(uUID);

        if (!Objects.equals(nbt.get("Motion"), previous.get("Motion")))
            p.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(this.entity));

        if (!Objects.equals(nbt.get("Pos"), previous.get("Pos"))) {
            p.dismountVehicle();
            p.networkHandler.requestTeleport(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
        }

        if (!Objects.equals(nbt.get("Rotation"), previous.get("Rotation")))
            p.networkHandler.requestTeleport(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());

        if (!Objects.equals(nbt.get("PlayerGameType"), previous.get("PlayerGameType")))
            p.changeGameMode(gameModeFromNbt(nbt));

        if (!Objects.equals(nbt.get("SelectedItemSlot"), previous.get("SelectedItemSlot"))) {
            int s = nbt.getInt("SelectedItemSlot");
            p.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(s));
        }

        if (!Objects.equals(nbt.get("abilities"), previous.get("abilities")))
            p.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(p.getAbilities()));

        ci.cancel();
    }
}
