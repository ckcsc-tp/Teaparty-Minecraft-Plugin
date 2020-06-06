package ckcsc33rd.teapartyplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class tabcomplete implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        List<String> list = new ArrayList<>();
        if(sender instanceof Player){
            if (command.getName().equalsIgnoreCase("party")){
                if (args.length==1){
                    list.add("list");
                    list.add("chat");
                    list.add("help");
                    return list;
                }else if (args.length==2&& args[0].equals("list")){
                    for(Team teams: Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeams()){
                        list.add(teams.getName());
                    }
                    return list;
                }

            }
            if (command.getName().equalsIgnoreCase("adminteam")){
                if (args.length==1){
                    list.add("create");
                    list.add("delete");
                    list.add("add");
                    list.add("kick");
                    list.add("score");
                    list.add("send");
                    list.add("show");
                    list.add("last");
                    list.add("win");
                    list.add("tnt");
                    return list;
                }else if (args.length==2&&
                        (args[0].equals("create")
                        ||args[0].equals("delete")
                        ||args[0].equals("add")
                        ||args[0].equals("send"))){
                    for(Team teams: Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeams()){
                        list.add(teams.getName());
                    }
                    return list;
                }else if (args.length==2&&(args[0].equals("score"))){
                    for (Player player:Bukkit.getOnlinePlayers()){
                        list.add(player.getName());
                    }
                    return list;
                }else if (args.length==2&&(args[0].equals("show"))){
                    list.add(String.valueOf(((Player) sender).getPlayer().getLocation().getBlockX()));
                    return list;
                }else if (args.length==3&&(args[0].equals("show"))){
                    list.add(String.valueOf(((Player) sender).getPlayer().getLocation().getBlockY()));
                    return list;
                }else if (args.length==4&&(args[0].equals("show"))){
                    list.add(String.valueOf(((Player) sender).getPlayer().getLocation().getBlockZ()));
                    return list;
                }
            }
        }

        return null;
    }

}
