package red.man10;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by takatronix on 2017/03/19.
 */
public class SkyWalker {
    private final SkyWalkerPlugin plugin;
    Material    controllerMaterial = Material.REDSTONE_TORCH_ON;
    String      controllerName = "§e§lSkyWalker Controller";

    Boolean                 isClosed = true;
    Boolean                 isSneaking = false;
    Material                material =  Material.IRON_BLOCK;
    BlockPlace              pos = null;        //  現在地
    ArrayList<BlockPlace>   blocks = new ArrayList<BlockPlace>();

    SkyWalker(   SkyWalkerPlugin plugin){
        this.plugin = plugin;
    }
    void    setBlock(Player p,BlockPlace b){

        for(int i = 0;i < blocks.size();i++){
            BlockPlace bp = blocks.get(i);
            if(bp.isSamePos(b)){
                return ;
            }
        }
        if(b.getLocation().getBlock().getType() == Material.AIR ){
            b.getLocation().getBlock().setType(material);
            blocks.add(new BlockPlace(b));
        }
    }

    int     getBlockIndex(BlockPlace b){
        for(int i = 0;i < blocks.size();i++){
            BlockPlace bp =blocks.get(i);
            if(bp.isSamePos(b)){
                return i;
            }
        }
        return -1;
    }

    void    removeBlock(BlockPlace b){
        int index = getBlockIndex(b);
        if(index == -1){
            return;
        }
        BlockPlace bp = blocks.get(index);
        if(b.getLocation().getBlock().getType() == material ){
            b.getLocation().getBlock().setType(Material.AIR);
        }
        blocks.remove(index);
    }
    //     すべての管理ブロックを削除する
    void removeAllBlocks(){
        for(;;){
            if(blocks.size() ==0){
                break;
            }
            BlockPlace b = blocks.get(0);
            if(b.getLocation().getBlock().getType() == material ){
            }
            b.getLocation().getBlock().setType(Material.AIR);
            blocks.remove(0);
//            Bukkit.getServer().broadcastMessage("sakujo "+b.desc());
        }
    }
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


    void giveController(Player p,String type){
/*
        if(p.hasPermission(adminPermission) == false){
            p.sendMessage("§cYou don't have permission:"+adminPermission);
            return;
        }
*/
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH_ON,1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(controllerName);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§b§lMan10テック社の最新ドローン'スカイウォーカー'のリモコン");
        lore.add("§b最新技術でつくられており、もはや魔法と区別がつかない");
        im.setLore(lore);
        item.setItemMeta(im);
        p.getInventory().addItem(item);
    }

    void onPlayerMove(PlayerMoveEvent e){

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(isController(item) == false){
            return;
        }

      //  org.bukkit.util.Vector v = p.getLocation().getDirection();
       // p.sendMessage("v:"+v.getY());
     //   double d = v.getY() * 0.1;
     //   Location loc = p.getLocation();
      //  loc.subtract(0,0,d);
      //  p.teleport(loc);
        if(p.isFlying() == false){
            return;
        }

        Location l = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
        BlockPlace bp = new BlockPlace(l);

        if(pos == null){
            pos = bp;
            return;
        }
        if(!bp.isSamePos(pos)){
            int ret = onBaseChanged(p,bp);
            /*
            if(ret == 0){
                if(!isClosed ){
                    p.sendMessage(plugin.prefix+ "Your SkyWalker is stored.");
                    delete();
                    isClosed = true;
                }
            }*/
            pos = bp;
        }

    }

    int delete(){
        removeAllBlocks();


        return 0;
    }

    int onPlayerToggleSneakEvent(PlayerToggleSneakEvent event){

        isSneaking = event.isSneaking();

        //      リモコンでないなら
        if(!isController(event.getPlayer().getInventory().getItemInMainHand())) {
            return 0;
        }
    //    if(event.isSneaking()){
    //        delete();
     //   }

        return 0;
    }
    int     onBaseChanged(Player p,BlockPlace bp){
        //Location loc = bp.getLocation();

        removeAllBlocks();

        BlockPlace b = new BlockPlace(bp);
        setBlock(p,b);

        b = new BlockPlace(bp);
        b.x ++;
        setBlock(p,b);
        b = new BlockPlace(bp);
        b.x --;
        setBlock(p,b);
        b = new BlockPlace(bp);
        b.z --;
        setBlock(p,b);
        b = new BlockPlace(bp);
        b.z ++;
        setBlock(p,b);

        /*
        b = new BlockPlace(bp);
        b.z ++;
        b.x ++;
        setBlock(p,b);

        b = new BlockPlace(bp);
        b.z --;
        b.x --;
        setBlock(p,b);

        b = new BlockPlace(bp);
        b.z ++;
        b.x --;

        setBlock(p,b);
        b = new BlockPlace(bp);
        b.z --;
        b.x ++;
        setBlock(p,b);
*/
        if(false){
            int carpetSize = 1;
            for(int x = -1* carpetSize;x <carpetSize ;x++){
                for(int y = -1* carpetSize;y < carpetSize;y++){
                    b = new BlockPlace(bp);
                    b.x += x;
                    b.z += y;
                    setBlock(p,b);
                }
            }
        }

        return blocks.size();
    }


    boolean hitCheck(Location loc){
        BlockPlace bp = new BlockPlace(loc);

        int index = getBlockIndex(bp);
        if(index == -1){
            return false;
        }
        return true;
    }
    void playSound(){
        if(isClosed){
            return;
        }
        Location l = pos.getLocation();
      //  l.getWorld().playSound(l,Sound.ENTITY_ARMORSTAND_BREAK ,1, 0.2f);

    }

}
