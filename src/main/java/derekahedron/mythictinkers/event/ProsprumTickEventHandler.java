package derekahedron.mythictinkers.event;

import derekahedron.mythictinkers.MythicTinkers;
import derekahedron.mythictinkers.entity.LivingEntityDuck;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MythicTinkers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProsprumTickEventHandler {

    @SubscribeEvent
    public static void styxTick(LivingEvent.LivingTickEvent event) {
        ((LivingEntityDuck) event.getEntity()).mythictinkers_$tickMarkedForDeath();
    }
}
