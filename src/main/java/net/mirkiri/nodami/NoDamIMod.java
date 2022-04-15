package net.mirkiri.nodami;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mirkiri.nodami.config.NodamiConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NoDamIMod.MODID)
public class NoDamIMod
{
    public static final String MODID = "nodami";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static IEventBus bus;

    public NoDamIMod()
    {
        bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NodamiConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("NodamI: Serverside operations started.");
        NodamiConfig.cacheValues();
        bus.register(new NodamiConfig());
    }


}
