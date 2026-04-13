package derekahedron.mythictinkers.entity;

import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityDuck {
    boolean mythictinkers_$isInStyx();
    void mythictinkers_$setInStyx(boolean inStyx);
    float mythictinkers_$getStyxDamage();
    void mythictinkers_$setStyxDamage(float styxDamage);
    void mythictinkers_$markForDeath(LivingEntity attacker, int ticks);
    boolean mythictinkers_$isMarkedForDeath(LivingEntity attacker);
    void mythictinkers_$tickMarkedForDeath();
    void mythictinkers_$unmarkForDeath();
}
