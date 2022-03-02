package cubingserver.things.events;

import cubing.utils.Reflections;
import net.minecraft.server.v1_8_R3.WorldData;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.lang.reflect.Field;

public class WeatherChange implements Listener {
    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent e) {
        if (e.getWorld().hasStorm()) {
            WorldData worldData = ((CraftWorld) e.getWorld()).getHandle().worldData;
            worldData.setWeatherDuration(0);
            Reflections.setField(worldData,"q",false);
        } else e.setCancelled(true);
    }
}
