package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
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
                    plugin.mg("/club create <team name>", sender);
                    plugin.mg("/club delete <team name>", sender);
                    plugin.mg("/club add <team name> <player>", sender);
                    plugin.mg("/club kick <team name> <player>", sender);
                    plugin.mg("/club score <team name> <number>", sender);
                    plugin.mg("/club send <team name> <server>", sender);
                    return true;
                }
                if (args[0].equals("list")) {
                    ClubList(sender);
                    return true;
                }
                if (args[0].equals("create")) {
                    if (args[1].isEmpty()) {
                        plugin.mg("請輸入社團名稱", sender);
                        return true;
                    }
                    addClub(args[1], sender);
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
                if (args[0].equals("kick")) {
                    if (args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱", sender);
                        return true;
                    }
                    if (args[2].isEmpty()) {
                        plugin.mg("請輸入玩家名稱", sender);
                        return true;
                    }
                    deletePlayer(args[1], args[2], sender);
                    return true;
                }
                if (args[0].equals("add")) {
                    if (args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱", sender);
                        return true;
                    }
                    if (args[2].isEmpty()) {
                        plugin.mg("請輸入玩家名稱", sender);
                        return true;
                    }
                    addPlayer(args[1], args[2], sender);
                    return true;
                }
            }
            
        }

        return false;
    }
    public void ClubList(CommandSender s){
        s.sendMessage(ChatColor.GREEN+ "社團:");
        //find all teams and list them
        FindIterable<Document> club = clubs.find();
        for (Document document : club) {
            String name = document.getString("name");
            plugin.mg(name, s);
        }
    }
    public void addClub(String teamname,CommandSender s){

        //check if the team exist
        Document team1=clubs.find(eq("name",teamname)).first();
        if(team1!=null) {
            s.sendMessage(ChatColor.RED+teamname+"此社團已存在");
            return;
        }

        List<String> playerList = new ArrayList<>();

        //insert team's data to the database
        Document club= new Document("name",teamname)
                .append("player",playerList);
        clubs.insertOne(club);
        plugin.mg("社團"+teamname+"加入成功",s);
    }
    public void deleteClub(String teamname,CommandSender s){

        //check if the team exist
        Document team1=clubs.find(eq("name",teamname)).first();
        if(team1==null){
            plugin.mg("此社團不存在",s);
            return;
        }
        //delete the team
        clubs.deleteOne(eq("name",teamname));
        s.sendMessage(ChatColor.BLUE+ ("社團"+teamname+"已刪除"));
        //if the player is in the server then add the player to the team
        join.updateScoreboard("join");
    }
    public void addPlayer(String teamname,String player,CommandSender s){



        //check if the team exist
        Document team1=clubs.find(eq("name",teamname)).first();
        if(team1==null) {
            plugin.mg(ChatColor.RED+"此社團不存在",s);
            return;
        }

        //get all the player in the team
        List<String> playerList = (List<String>) team1.get("player");
        for (String playerConfig :playerList){
            assert player != null;
            if(player.equals(playerConfig)){
                plugin.mg("此玩家已加入社團",s);
                return;
            }
        }
        playerList.add(player);

        //update team data in database
        Document players = new Document("player",playerList);
        clubs.updateOne(eq("name",teamname), new Document("$set",players));
        //if the player is in the server then add the player to the team
        join.updateScoreboard("join");
        s.sendMessage(ChatColor.BLUE+ (player +"成功加入"+teamname));
    }
    public void deletePlayer(String teamname,String player,CommandSender s){

        //get the team in database
        Document team1=clubs.find(eq("name",teamname)).first();
        List<String> playerList = (List<String>) team1.get("player");
        assert playerList != null;

        //check if the player is in the team and kick it
        for (String playerConfig:playerList) {
            if(player.equals(playerConfig)){

                s.sendMessage(ChatColor.BLUE+ ("社團"+teamname+"已移除"+player));
                playerList.remove(playerConfig);
                Document players = new Document("player",playerList);
                clubs.updateOne(eq("name",teamname), new Document("$set",players));
                //if the player is in the server then add the player to the team
                join.updateScoreboard("join");
                return;
            }

        }

        plugin.mg("這玩家不存在於此社團",s);

    }





}
