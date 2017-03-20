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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



public final class SkyWalkerPlugin extends JavaPlugin implements Listener {

    Material    controllerMaterial = Material.REDSTONE_TORCH_ON;
    String      controllerName = "§e§lSkyWalker Controller";

    String adminPermission = "man10.skywaker.admin";
    String driverPermission = "man10.skywaker.drive";


    //      ユーザーをキーにしたアイテム情報
    HashMap<UUID,SkyWalker> map = new HashMap<UUID,SkyWalker>();
    //
    String  prefix = "[§bSkyWalker§f] ";

    void giveController(Player p,String type){

        if(p.hasPermission(adminPermission) == false){
            p.sendMessage("§cYou don't have permission:"+adminPermission);
            return;
        }
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH_ON,1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(controllerName);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§9Man10 Technology's latest drone 'SkyWalker's Controller'");
        lore.add("§b§lMan10テック社の最新ドローン'スカイウォーカー'のリモコン");
        lore.add("§b§l最新技術でつくられており、もはや魔法と区別がつかない");
        im.setLore(lore);
        item.setItemMeta(im);
        p.getInventory().addItem(item);
    }


    //     サーバーメッセージ
    void serverMessage(String text){
        Bukkit.getServer().broadcastMessage(prefix +  text);
    }
    //      　
    boolean isController(ItemStack item){
        if(item.getType() != controllerMaterial){
            return false;
        }
        String name = item.getItemMeta().getDisplayName();
        if(name == null){
            return false;
        }
        if(!controllerName.contentEquals(name)){
            return false;
        }
        return true;
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
        }, 0, 4);
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
            if(!isController(item)){
                return;
            }
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

        //      コントローラーの右クリ
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            Player p = e.getPlayer();
            if(isController(p.getInventory().getItemInMainHand())){
                e.setCancelled(true);
            }
            return;
        }

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
            Player p = e.getPlayer();
            //      リモコンでないなら
            if(!isController(p.getInventory().getItemInMainHand())) {
                return;
            }

            /////////////////////////////////////
            //      すでに登録済み
            /////////////////////////////////////
            if(map.containsKey(p.getUniqueId())){
                SkyWalker sw = map.get(p.getUniqueId());
                if(sw != null){
                    sw.delete();
                    map.remove(p.getUniqueId());
                    p.setWalkSpeed((float).2);

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
                p.setWalkSpeed((float)0.5);



                    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        public void run() {
                            if(sw.isClosed){
                                sw.isClosed = false;
                                p.sendMessage(prefix + "You called SkyWalker.");
                            }
                        }
                    }, 10);

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
        if(!map.containsKey(e.getPlayer().getUniqueId())){
            return;
        }

        SkyWalker sw = map.get(e.getPlayer().getUniqueId());
        if(sw.isClosed){
            return ;
        }
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
