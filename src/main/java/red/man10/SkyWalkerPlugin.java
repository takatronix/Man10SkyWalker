package red.man10;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



public final class SkyWalkerPlugin extends JavaPlugin implements Listener {


    //      ユーザーをキーにしたアイテム情報
    HashMap<UUID,SkyWalker> map = new HashMap<UUID,SkyWalker>();

    //
    String  prefix = "[§bSkyWalker§f] ";

    //     サーバーメッセージ
    void serverMessage(String text){
        Bukkit.getServer().broadcastMessage(prefix +  text);
    }

    @Override
    public void onEnable() {
        // Plugin startup logicParti
        getServer().getPluginManager().registerEvents (this,this);
        Bukkit.getServer().broadcastMessage(prefix +  "SkyWalker is loaded.");

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                onTickTimer();
            }
        }, 0, 4);

    }

    public void onTickTimer(){
        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList) {
            if (!map.containsKey(id)) {
                continue;
            }
            Player p = Bukkit.getPlayer(id);
            SkyWalker sw = map.get(id);
            if(sw.pos != null ){
                Location l = sw.pos.getLocation();
                l.getWorld().playSound(l,Sound.ENTITY_ARROW_HIT ,1, 0);
                if(sw.isSneaking){
                    sw.delete();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!map.containsKey(p.getUniqueId())) {
            return;
        }
        SkyWalker sw = map.get(p.getUniqueId());
        sw.delete();
    }
    @Override
    public void onDisable() {
        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList) {
            if (!map.containsKey(id)) {
                continue;
            }
            Player p = Bukkit.getPlayer(id);
            SkyWalker sw = map.get(id);
            sw.delete();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
            if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH_ON ||
                    e.getPlayer().getInventory().getItemInOffHand().getType() == Material.REDSTONE_TORCH_ON
                    ) {
                Player p = e.getPlayer();
                if(map.containsKey(p.getUniqueId())){
                    SkyWalker sw = map.get(p.getUniqueId());
                    if(sw != null){
                        sw.delete();
                        //  p.sendMessage(prefix+"You stored SkyWalker.");
                        map.remove(p.getUniqueId());
                        p.setWalkSpeed((float).2);
                    }
                }else{
                    SkyWalker sw = new SkyWalker();
                    map.put(p.getUniqueId(),sw);
                   // p.sendMessage(prefix+"You called SkyWalker.");
                    p.setVelocity(p.getVelocity().setY(1));
                    if(sw.pos != null){
                        Location l = sw.pos.getLocation();
                        //sw.pos.getLocation().getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH ,1, 0);
                        l.getWorld().playSound(l,Sound.BLOCK_NOTE_HARP ,1, 0);
                        p.setWalkSpeed((float)1.0);

                    }
                }

            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList){
            if(!map.containsKey(id)){
                continue;
            }
            SkyWalker sw = map.get(id);
            if(sw.hitCheck(e.getBlock().getLocation())){
                p.sendMessage(prefix + "You can't break the SkyWalker.");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonEvent(BlockPistonExtendEvent e){

        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList){
            if(!map.containsKey(id)){
                continue;
            }
            SkyWalker sw = map.get(id);
            Location l = e.getBlock().getLocation();
            if(l.distance(sw.pos.getLocation()) < 4){
                sw.delete();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if(!map.containsKey(e.getPlayer().getUniqueId())){
            return;
        }
        SkyWalker sw = map.get(e.getPlayer().getUniqueId());
        sw.onPlayerMove(e);
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if(!map.containsKey(e.getPlayer().getUniqueId())){
            return;
        }
        SkyWalker sw = map.get(e.getPlayer().getUniqueId());
        sw.onPlayerToggleSneakEvent(e);
    }
}
