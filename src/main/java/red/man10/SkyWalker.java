package red.man10;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by takatronix on 2017/03/19.
 */
public class SkyWalker {
    private final SkyWalkerPlugin plugin;

    Boolean                 isSneaking = false;
    Material                material =  Material.DIAMOND_BLOCK;
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

        }

    }

    void onPlayerMove(PlayerMoveEvent e){

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(plugin.isController(item) == false){
            return;
        }

        Location loc = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
        BlockPlace bp = new BlockPlace(loc);

        if(pos == null){
            pos = bp;
            return;
        }
        if(!bp.isSamePos(pos)){
            onBaseChanged(p,bp);
            pos = bp;
        }

    }

    int delete(){
        removeAllBlocks();

        if(pos != null){
            Location l = pos.getLocation();
           // l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 5);
            l.getWorld().playSound(l,Sound.ENTITY_ARROW_HIT ,1, 0);
        }

        return 0;
    }

    int onPlayerToggleSneakEvent(PlayerToggleSneakEvent event){

        isSneaking = event.isSneaking();

        //      リモコンでないなら
        if(!plugin.isController(event.getPlayer().getInventory().getItemInMainHand())) {
            return 0;
        }

        if(event.isSneaking()){
            delete();
        }


        return 0;
    }
    int     onBaseChanged(Player p,BlockPlace bp){
        Location loc = bp.getLocation();

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

        return 1;
    }


    boolean hitCheck(Location loc){
        BlockPlace bp = new BlockPlace(loc);

        int index = getBlockIndex(bp);
        if(index == -1){
            return false;
        }
        return true;
    }


}
