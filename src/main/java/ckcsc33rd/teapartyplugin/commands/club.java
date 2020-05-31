package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import ckcsc33rd.teapartyplugin.events.chat;
import ckcsc33rd.teapartyplugin.events.join;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class club implements CommandExecutor {

    TeapartyPlugin plugin;
    MongoCollection<Document> clubs;
    public club(TeapartyPlugin teapartyPlugin) {
        //繼承plugin跟mongodb
        plugin=teapartyPlugin;
        clubs=TeapartyPlugin.clubs;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("club")){
            if (sender.isOp()) {
                if (args.length == 0 || args[0].equals("help")) {
                    plugin.mg("/club create <club name>", sender);
                    plugin.mg("/club delete <player name>", sender);
                    return true;
                }
                if (args[0].equals("create")) {
                    if (args[1].isEmpty()) {
                        plugin.mg("請輸入社團名稱", sender);
                        return true;
                    }
                    addClub(args[1],args[2], sender);
                    return true;
                }

                if (args[0].equals("delete")) {
                    if (args[1].isEmpty()) {
                        plugin.mg("請輸入社團名稱", sender);
                        return true;
                    }
                    deleteClub(args[1], sender);
                    return true;
                }
            }
            
        }

        return false;
    }
    public void addClub(String player,String teamname,CommandSender s){

        //check if the team exist
        Document data=clubs.find(eq("name",player)).first();
        if(data!=null) {
            s.sendMessage(ChatColor.RED+teamname+"此玩家已註冊");
            return;
        }

        //insert team's data to the database
        Document club= new Document("name",player)
                .append("club",teamname);
        clubs.insertOne(club);
        plugin.mg("玩家"+teamname+"註冊成功",s);
        chat.updateDisplay(Objects.requireNonNull(Bukkit.getPlayer(player)));
    }
    public void deleteClub(String player,CommandSender s){

        //check if the team exist
        Document team1=clubs.find(eq("name",player)).first();
        if(team1==null){
            plugin.mg("此玩家還未註冊",s);
            return;
        }
        //delete the team
        clubs.deleteOne(eq("name",player));
        s.sendMessage(ChatColor.BLUE+ ("玩家"+player+"已撤銷註冊"));
        //if the player is in the server then add the player to the team
        join.updateScoreboard();
        chat.updateDisplay(Objects.requireNonNull(Bukkit.getPlayer(player)));
    }






}
