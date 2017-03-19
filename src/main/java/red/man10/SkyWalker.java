package red.man10;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Created by takatronix on 2017/03/19.
 */
public class SkyWalker {

    public enum SkyWalkerType {
        Floor,
    }


    //      ブロック設置情報
    public class BlockPlace {
        BlockPlace(BlockPlace bp){
            this.world = bp.world;
            this.x = bp.x;
            this.y = bp.y;
            this.z = bp.z;

        }
        BlockPlace(String world,int x,int y,int z){
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        BlockPlace(String world,double x,double y,double z){
            this.world = world;
            this.x = (int)x;
            this.y = (int)y;
            this.z = (int)z;
        }
        BlockPlace(Location loc){
            this.world = loc.getWorld().getName().toString();
            this.x = (int)loc.getX();
            this.y = (int)loc.getY();
            this.z = (int)loc.getZ();
        }
        BlockPlace(){
            this.world = "";
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }
        boolean isSamePos(BlockPlace bp){
            if(bp.world.contentEquals(world) && bp.x == x && bp.y == y && bp.z == y){
                return true;
            }
            return  false;
        }
        Location getLocation(){
            World world = Bukkit.getWorld(this.world);
            return new Location(world,x,y,z);
        }

        String      world;
        int         x;
        int         y;
        int         z;

        String desc(){
            return new String("BP:"+world+"/x:"+x+"/y:"+y+"/z:"+z);
        }
    }

    Material                material =  Material.EMERALD_BLOCK;
    BlockPlace              pos = null;        //  現在地
    SkyWalkerType           type;
    ArrayList<BlockPlace>   blocks = new ArrayList<BlockPlace>();

    void    setBlock(Player p,BlockPlace b){


        for(int i = 0;i < blocks.size();i++){
            BlockPlace bp = blocks.get(i);
            if(bp.isSamePos(b)){
               // p.sendMessage("同じなので登録しない"+b.desc());
                return ;
            }

        }

        if(b.getLocation().getBlock().getType() == Material.AIR ){
            b.getLocation().getBlock().setType(material);
            blocks.add(new BlockPlace(b));
           // p.sendMessage("保存した:"+b.desc()+":"+blocks.size());
        }else{
           // p.sendMessage("できなかった:"+b.desc());
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
    void removeAllBlocks(Player p){
        for(;;){
            if(blocks.size() ==0){
                break;
            }
            BlockPlace b = blocks.get(0);
            if(b.getLocation().getBlock().getType() == material ){
            }
            b.getLocation().getBlock().setType(Material.AIR);
           // p.sendMessage("removing:"+b.desc() +"size:"+blocks.size());
            blocks.remove(0);

        }

    }

    void onPlayerMove(PlayerMoveEvent e){

        Player p = e.getPlayer();
        if(p.getInventory().getItemInMainHand().getType() != Material.REDSTONE_TORCH_ON){
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

    int delete(Player p){

        removeAllBlocks(p);

        return 0;
    }

    int onPlayerToggleSneakEvent(PlayerToggleSneakEvent event){

        if(event.isSneaking()){
            delete(event.getPlayer());
        }


        return 0;
    }
    int     onBaseChanged(Player p,BlockPlace bp){
        Location loc = bp.getLocation();

      //  Location lastLoc = pos.getLocation();

        removeAllBlocks(p);

        int xs[] = { 0,0,0, 1,1,1,-1,-1,-1};
        int ys[] = { 0,1,-1, 0,1,-1,0,1,-1};

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
        for(int x = -3;x < 3;x++){

            for(int y = -3;y < 3;y++){
                b = new BlockPlace(bp);
                b.x += x;
                b.z += y;
                setBlock(p,b);

            }
        }
        */

      //  loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 2003);

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
