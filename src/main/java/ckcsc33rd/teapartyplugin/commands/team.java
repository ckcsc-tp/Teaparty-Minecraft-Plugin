package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;
import java.util.Set;


public class team implements CommandExecutor {
    TeapartyPlugin plugin;
    public team(TeapartyPlugin teapartyPlugin) {
        plugin = teapartyPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("team")) {
            if(args.length == 0){
                return false;
            }
            if (args[0].equals("list")){
                if(args[1].isEmpty()) {
                    plugin.mg("請輸入隊伍名稱",sender);
                    return true;
                }
                teamPlayer(args[1],sender);
                return true;
            }
            if (args[0].equals("chat")) {
                plugin.mg("講話",sender);
                return true;
            }
            if (args[0].equals("score")){
                plugin.mg("分數",sender);
                return true;
            }
            return false;
        }
        return false;
    }
    public void teamPlayer(String team,CommandSender s){
        List<String> p = (List<String>) plugin.getConfig().getList("teams."+team+".players");
        assert p != null;
        if (p.isEmpty()){
            plugin.mg("這個隊伍沒有玩家",s);
            return;
        }
        s.sendMessage(ChatColor.GREEN+"此隊伍的玩家有:");
        for(String player: p){
            plugin.mg(Objects.requireNonNull(player),s);
        }

    }
}
