package crazypants.enderio.machine.obelisk.aversion;

import java.util.HashMap;
import java.util.Map;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AversionObeliskController {

  public static AversionObeliskController instance = new AversionObeliskController();
  
  static {
    MinecraftForge.EVENT_BUS.register(AversionObeliskController.instance);
  }

  private Map<Integer, Map<BlockCoord, TileAversionObelisk>> perWorldGuards = new HashMap<Integer, Map<BlockCoord, TileAversionObelisk>>();

  private AversionObeliskController() {
  }

  public void registerGuard(TileAversionObelisk guard) {
    if(guard == null) {
      return;
    }    
    Map<BlockCoord, TileAversionObelisk> chargers = getGuardsForWorld(guard.getWorld());
    chargers.put(guard.getLocation(), guard);
  }

  public void deregisterGuard(TileAversionObelisk guard) {
    if(guard == null) {
      return;
    }
    Map<BlockCoord, TileAversionObelisk> chargers = getGuardsForWorld(guard.getWorld());
    chargers.remove(guard.getLocation());
  }
  
  @SubscribeEvent
  public void onEntitySpawn(LivingSpawnEvent evt) {
    
    if(Config.spawnGuardStopAllSlimesDebug && evt.entity instanceof EntitySlime) {
      evt.setResult(Result.DENY);
      return;
    }
    if(Config.spawnGuardStopAllSquidSpawning && evt.entity.getClass() == EntitySquid.class) {
      evt.setResult(Result.DENY);
      return;
    }
    
    
    Map<BlockCoord, TileAversionObelisk> guards = getGuardsForWorld(evt.world);
    for(TileAversionObelisk guard : guards.values()) {
      if(guard.isSpawnPrevented(evt.entityLiving)) {   
        evt.setResult(Result.DENY);
        return;
      }
    }    
  }
  
  private Map<BlockCoord, TileAversionObelisk> getGuardsForWorld(World world) {
    Map<BlockCoord, TileAversionObelisk> res = perWorldGuards.get(world.provider.getDimensionId());
    if(res == null) {
      res = new HashMap<BlockCoord, TileAversionObelisk>();
      perWorldGuards.put(world.provider.getDimensionId(), res);
    }
    return res;
  }
  
}
