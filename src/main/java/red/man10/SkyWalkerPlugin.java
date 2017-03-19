package red.man10;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
    String  prefix = "[SkyWalker]";

    //     サーバーメッセージ
    void serverMessage(String text){
        Bukkit.getServer().broadcastMessage(prefix +  text);
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents (this,this);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!map.containsKey(p.getUniqueId())) {
            return;
        }
        SkyWalker sw = map.get(p.getUniqueId());
        sw.delete(p);
        serverMessage(p.getName().toString()+"'s SkyWalker is stored");
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic

        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList) {
            if (!map.containsKey(id)) {
                continue;
            }
            Player p = Bukkit.getPlayer(id);
            serverMessage(p.getName().toString()+"'s SkyWalker is stored.");
            SkyWalker sw = map.get(id);
            sw.delete(p);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH_ON) {
                Player p = e.getPlayer();
                if(map.containsKey(p.getUniqueId())){
                    SkyWalker sw = map.get(p.getUniqueId());
                    sw.delete(p);
                    p.sendMessage("You stored SkyWalker.");
                    map.remove(p.getUniqueId());
                }else{
                    SkyWalker sw = new SkyWalker();
                    map.put(p.getUniqueId(),sw);
                    p.sendMessage("You called SkyWalker.");
                    p.setVelocity(p.getVelocity().setY(1));
                }

            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        //SkyWalker.BlockPlace bp = new SkyWalker.BlockPlace();

        Player p = e.getPlayer();
        p.sendMessage("block break");
        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList){
            if(!map.containsKey(id)){
                continue;
            }
            p.sendMessage("UUID:"+id);

            SkyWalker sw = map.get(id);
            if(sw.hitCheck(e.getBlock().getLocation())){
                p.sendMessage("hit");
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
