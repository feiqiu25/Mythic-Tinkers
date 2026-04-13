package derekahedron.mythictinkers.mixin;

import derekahedron.mythictinkers.entity.LivingEntityDuck;
import derekahedron.mythictinkers.event.StyxEventHandler;
import derekahedron.mythictinkers.fluid.MTFluidTypes;
import derekahedron.mythictinkers.potion.MTEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityDuck {
    @Unique
    private static final String mythictinkers_$STYX_DAMAGE_KEY = "mythictinkers:STYX_DAMAGE";
    @Unique
    private static final String mythictinkers_$MARKED_FOR_DEATH_KEY = "mythictinkers:MARKED_FOR_DEATH";
    @Unique
    private static final String mythictinkers_$MARKED_FOR_DEATH_UUID_KEY = "UUID";
    @Unique
    private static final String mythictinkers_$MARKED_FOR_DEATH_TICKS_KEY = "Ticks";

    @Unique
    private boolean mythictinkers_$inStyx = false;
    @Unique
    private float mythictinkers_$styxDamage = 0.0F;
    @Unique
    private final Map<UUID, Integer> mythictinkers_$markedForDeathMap = new HashMap<>();

    @Shadow protected boolean jumping;
    @Shadow public float xxa;
    @Shadow public float yya;
    @Shadow public float zza;

    @Shadow
    protected abstract boolean isAffectedByFluids();

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;serverAiStep()V",
                    shift = At.Shift.AFTER))
    private void restrictMovement(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object) this;
        if (mythictinkers_$inStyx && isAffectedByFluids()) {
            float styxIntensity = StyxEventHandler.getStyxIntensity(mythictinkers_$styxDamage);
            if (jumping &&
                    (self.getFluidTypeHeight(MTFluidTypes.STYX.get()) > self.getFluidJumpThreshold()
                            || styxIntensity >= 1.0F)) {
                jumping = false;
            }
            xxa *= (1.0F - styxIntensity);
            yya *= (1.0F - styxIntensity);
            zza *= (1.0F - styxIntensity);
        }
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("RETURN")
    )
    private void saveCustomAttrs(CompoundTag compound, CallbackInfo ci) {
        if (mythictinkers_$styxDamage != 0.0F) {
            compound.putFloat(mythictinkers_$STYX_DAMAGE_KEY, mythictinkers_$styxDamage);
        }
        if (!mythictinkers_$markedForDeathMap.isEmpty()) {
            ListTag timers = new ListTag();
            mythictinkers_$markedForDeathMap.forEach((uuid, integer) -> {
                CompoundTag timerTag = new CompoundTag();
                timerTag.putUUID(mythictinkers_$MARKED_FOR_DEATH_UUID_KEY, uuid);
                timerTag.putInt(mythictinkers_$MARKED_FOR_DEATH_TICKS_KEY, integer);
                timers.add(timerTag);
            });
            compound.put(mythictinkers_$MARKED_FOR_DEATH_KEY, timers);
        }
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("RETURN")
    )
    private void loadCustomAttrs(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains(mythictinkers_$STYX_DAMAGE_KEY)) {
            mythictinkers_$styxDamage = compound.getFloat(mythictinkers_$STYX_DAMAGE_KEY);
        } else {
            mythictinkers_$styxDamage = 0.0F;
        }

        mythictinkers_$markedForDeathMap.clear();
        if (compound.contains(mythictinkers_$MARKED_FOR_DEATH_KEY)) {
            ListTag timers = compound.getList(mythictinkers_$MARKED_FOR_DEATH_KEY, 10);
            for (int i = 0; i < timers.size(); ++i) {
                CompoundTag timerTag = timers.getCompound(i);
                UUID uuid = timerTag.getUUID(mythictinkers_$MARKED_FOR_DEATH_UUID_KEY);
                int ticks = timerTag.getInt(mythictinkers_$MARKED_FOR_DEATH_TICKS_KEY);
                mythictinkers_$markedForDeathMap.put(uuid, ticks);
            }
        }
    }

    @Override
    public boolean mythictinkers_$isInStyx() {
        return mythictinkers_$inStyx;
    }

    @Override
    public void mythictinkers_$setInStyx(boolean inStyx) {
        mythictinkers_$inStyx = inStyx;
    }

    @Override
    public float mythictinkers_$getStyxDamage() {
        return mythictinkers_$styxDamage;
    }

    @Override
    public void mythictinkers_$setStyxDamage(float styxDamage) {
        this.mythictinkers_$styxDamage = styxDamage;
    }

    @Override
    public void mythictinkers_$markForDeath(LivingEntity attacker, int ticks) {
        LivingEntity self = (LivingEntity) (Object) this;
        self.addEffect(new MobEffectInstance(MTEffects.MARKED_FOR_DEATH.get(), ticks));

        UUID uuid = attacker.getUUID();
        if (ticks > 0) {
            ticks = Math.max(ticks, mythictinkers_$markedForDeathMap.getOrDefault(uuid, 0));
            mythictinkers_$markedForDeathMap.put(uuid, ticks);
        } else {
            mythictinkers_$markedForDeathMap.remove(uuid);
        }
    }

    @Override
    public boolean mythictinkers_$isMarkedForDeath(LivingEntity attacker) {
        LivingEntity self = (LivingEntity) (Object) this;
        return self.hasEffect(MTEffects.MARKED_FOR_DEATH.get())
                && mythictinkers_$markedForDeathMap.getOrDefault(attacker.getUUID(), 0) > 0;
    }

    @Override
    public void mythictinkers_$tickMarkedForDeath() {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.hasEffect(MTEffects.MARKED_FOR_DEATH.get())) {
            mythictinkers_$markedForDeathMap.clear();
        } else {
            mythictinkers_$markedForDeathMap.entrySet().removeIf(entry -> {
                entry.setValue(entry.getValue() - 1);
                return entry.getValue() == 0;
            });
        }
    }

    @Override
    public void mythictinkers_$unmarkForDeath() {
        LivingEntity self = (LivingEntity) (Object) this;
        self.removeEffect(MTEffects.MARKED_FOR_DEATH.get());
        mythictinkers_$markedForDeathMap.clear();
    }
}
