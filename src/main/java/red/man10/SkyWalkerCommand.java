package red.man10;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by takatronix on 2017/03/20.
 */
public class SkyWalkerCommand  implements CommandExecutor {
    private final SkyWalkerPlugin plugin;

    public SkyWalkerCommand(SkyWalkerPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            CommandSender p = sender;
            p.sendMessage("§e============== §d●§f●§a●§eMan10 SkyWalker§d●§f●§a● §e===============");
            p.sendMessage("§c/sw get [type:0-10] - get SkyWalker's controller");
            p.sendMessage("§c/sw restore [name] - restore [name]'s SkyWalker");
            p.sendMessage("§c/sw restoreall  - restore all SkyWalker");
            p.sendMessage("§e============== §d●§f●§a●§eMan10 SkyWalker§d●§f●§a● §e===============");
            p.sendMessage("§ehttp://man10.red Minecraft Man10 Server");
            p.sendMessage("§ecreated by takatronix http://takatronix.com");
            p.sendMessage("§ecreated by takatronix http://twitter.com/takatronix");
            return false;
        }

        if(args[0].equalsIgnoreCase("get")){
            if(args.length != 2){
                sender.sendMessage("/sw get [dronetype:0-10]");
                return false;
            }
            plugin.giveController((Player)sender,args[1]);
            return true;
        }




        return true;
    }
}
