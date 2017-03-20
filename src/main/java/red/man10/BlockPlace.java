package red.man10;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by takatronix on 2017/03/20.
 */
//      ブロック設置情報
public class BlockPlace {
    BlockPlace(BlockPlace bp) {
        this.world = bp.world;
        this.x = bp.x;
        this.y = bp.y;
        this.z = bp.z;

    }

    BlockPlace(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    BlockPlace(String world, double x, double y, double z) {
        this.world = world;
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
    }

    BlockPlace(Location loc) {
        this.world = loc.getWorld().getName().toString();
        this.x = (int) loc.getX();
        this.y = (int) loc.getY();
        this.z = (int) loc.getZ();
    }

    BlockPlace() {
        this.world = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    boolean isSamePos(BlockPlace bp) {
        if (bp.world.equalsIgnoreCase(world) && bp.x == x && bp.y == y && bp.z == z) {
            return true;
        }
        return false;
    }

    Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        return new Location(world, x, y, z);
    }

    String world;
    int x;
    int y;
    int z;

    String desc() {
        return new String("BP:" + world + "/x:" + x + "/y:" + y + "/z:" + z);
    }
}