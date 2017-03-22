package red.man10;

import com.sun.prism.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



public final class SkyWalkerPlugin extends JavaPlugin implements Listener {



    String adminPermission = "man10.skywaker.admin";
    String driverPermission = "man10.skywaker.drive";


    //      ユーザーをキーにしたアイテム情報
    HashMap<UUID,SkyWalker> map = new HashMap<UUID,SkyWalker>();
    //
    String  prefix = "[§bSkyWalker§f] ";



    //     サーバーメッセージ
    void serverMessage(String text){
        Bukkit.getServer().broadcastMessage(prefix +  text);
    }
    //      　

    void giveController(Player p,String type){
        SkyWalker sw = getObject(p);
        sw.giveController(p,type);
    }
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents (this,this);
        Bukkit.getServer().broadcastMessage(prefix +  "SkyWalker is loaded.");
        getCommand("sw").setExecutor(new SkyWalkerCommand(this));
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                onTickTimer();
            }
        }, 0, 1);
    }

    public void onTickTimer(){





        List<UUID> userList = new ArrayList<UUID>(map.keySet());
        for(UUID id : userList) {
            if (!map.containsKey(id)) {
                continue;
            }
            Player p = Bukkit.getPlayer(id);
            if(p == null){
                return ;
            }
            ItemStack item = p.getInventory().getItemInMainHand();

            SkyWalker sw = getObject(p);
            sw.playSound();

            if(!sw.isController(item)){
                return;
            }
            if(p.isSneaking()){
                sw.delete();
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

    SkyWalker getObject(Player p){

        /////////////////////////////////////
        //      すでに登録済み
        /////////////////////////////////////
        if(map.containsKey(p.getUniqueId())){
            SkyWalker sw = map.get(p.getUniqueId());
            /*
            if(sw != null){
                sw.delete();
                map.remove(p.getUniqueId());
                // p.setWalkSpeed((float).2);

            }*/
            return sw;
        }
        /////////////////////////////////////
        //      新規作成
        /////////////////////////////////////
        SkyWalker sw = new SkyWalker(this);
       // sw.pos = new BlockPlace(p.getLocation());
        map.put(p.getUniqueId(),sw);
        return sw;
    }
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
     //   e.getPlayer().sendMessage("toggle flgiht");
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {


        //      プレーヤのスカイウォーカー
        SkyWalker sw = getObject(e.getPlayer());

        //      コントローラーの右クリ
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            e.setCancelled(true);


        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR ) {

            Player p = e.getPlayer();
            if(sw.isController(p.getInventory().getItemInMainHand())){
                e.getPlayer().setFlying(true);

                if(p.isFlying()){
                    sw.delete();

                    Vector v = p.getLocation().getDirection();
                    v.multiply(1.5);
                    p.setVelocity(v);
                }else{

                }

            }
            return;
        }

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
            Player p = e.getPlayer();
            //      リモコンでないなら
            if(!sw.isController(p.getInventory().getItemInMainHand())) {
                return;
            }

            float walkSpeed = .2f;
            float flySpeed = .2f;






            if(p.isFlying()){
                p.setAllowFlight(false);

                p.setFlying(false);
                p.setWalkSpeed(walkSpeed);
                p.sendMessage("止まった");

                sw.delete();
                sw.isClosed = true;

            }else{
                p.setFlySpeed(flySpeed);
                 p.setVelocity(p.getVelocity().setY(1.2));

                getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                    public void run() {
                                     p.sendMessage("浮いた");
                                        p.setAllowFlight(true);
                                        p.setFlying(false);
                                        sw.isClosed = false;
                                    }
                                }, 10);

            }

/*
            /////////////////////////////////////
            //      すでに登録済み
            /////////////////////////////////////
            if(map.containsKey(p.getUniqueId())){
                SkyWalker sw = map.get(p.getUniqueId());
                if(sw != null){
                    sw.delete();
                    map.remove(p.getUniqueId());
                   // p.setWalkSpeed((float).2);

                }
                return;
            }
            /////////////////////////////////////
            //      新規作成
            /////////////////////////////////////
            SkyWalker sw = new SkyWalker(this);
            map.put(p.getUniqueId(),sw);
            p.setVelocity(p.getVelocity().setY(1));
            sw.pos = new BlockPlace(p.getLocation());
            if(sw.pos != null){
                Location l = sw.pos.getLocation();
                //sw.pos.getLocation().getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH ,1, 0);
                l.getWorld().playSound(l,Sound.BLOCK_CHORUS_FLOWER_GROW ,1, 0);
             //   p.setWalkSpeed((float)0.5);


                    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        public void run() {
                            if(sw.isClosed){
                                sw.isClosed = false;
                                p.sendMessage(prefix + "You called SkyWalker.");
                                p.setFlying(true);
                            }
                        }
                    }, 10);

            }

            */
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
                OfflinePlayer owner = Bukkit.getOfflinePlayer(id);
                p.sendMessage(prefix + "You can't break " +owner.getName()+ "'s SkyWalker.");
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

        SkyWalker sw = getObject(e.getPlayer());
        sw.onPlayerMove(e);
        if(!sw.isClosed){
            e.getPlayer().setFlying(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        SkyWalker sw = getObject(p);
        sw.onPlayerToggleSneakEvent(e);
    }
}
