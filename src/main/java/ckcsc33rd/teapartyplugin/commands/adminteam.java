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
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.*;

public class adminteam implements CommandExecutor {

    TeapartyPlugin plugin;
    MongoCollection<Document> team;
    public adminteam(TeapartyPlugin teapartyPlugin) {
        //繼承plugin跟mongodb
        plugin=teapartyPlugin;
        team=TeapartyPlugin.team;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("adminteam")) {
            if (sender.isOp()) {
                if(args.length == 0 || args[0].equals("help")){
                    plugin.mg("/at create <team name>",sender);
                    plugin.mg("/at delete <team name>",sender);
                    plugin.mg("/at add <team name> <player>",sender);
                    plugin.mg("/at kick <team name> <player>",sender);
                    plugin.mg("/at score <team name> <number>",sender);
                    plugin.mg("/at send <team name> <server>",sender);
                    return true;
                }
                if(args[0].equals("list")){
                    TeamList(sender);
                    return true;
                }
                if(args[0].equals("create")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    createTeam(args[1],sender);
                    return true;
                }

                if(args[0].equals("delete")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    deleteTeam(args[1],sender);
                    return true;
                }
                if(args[0].equals("kick")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2].isEmpty()) {
                        plugin.mg("請輸入玩家名稱",sender);
                        return true;
                    }
                    deletePlayer(args[1],args[2],sender);
                    return true;
                }
                if(args[0].equals("add")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2].isEmpty()) {
                        plugin.mg("請輸入玩家名稱",sender);
                        return true;
                    }
                    addPlayer(args[1],args[2],sender);
                    return true;
                }
                if(args[0].equals("score")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2].isEmpty()) {
                        plugin.mg("請輸入分數",sender);
                        return true;
                    }
                    score(args[1],Integer.parseInt(args[2]),sender);
                    return true;
                }
                if(args[0].equals("send")){
                    if(args[1].isEmpty()) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2].isEmpty()) {
                        plugin.mg("請輸入伺服器",sender);
                        return true;
                    }
                    try {
                        playerSend(args[1],args[2],sender);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                if(args[0].equals("update")){
                    join.teamCreate();
                }


            }
            else {
                sender.sendMessage(ChatColor.RED+"你沒有權限執行此命令");
                return true;
            }
        }
        return false;
    }

    //list all teams
    public void TeamList(CommandSender s){
        s.sendMessage(ChatColor.GREEN+ "隊伍名單:");
        //find all teams and list them
        FindIterable<Document> teams = team.find();
        for (Document document : teams) {
            String name = document.getString("name");
            plugin.mg(name, s);
        }
    }

    //create team
    public void createTeam(String teamname,CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();

        //check if the team exist
        Document team1=team.find(eq("name",teamname)).first();
        if(team1!=null) {
            s.sendMessage(ChatColor.RED+teamname+"此隊伍已被創建");
            return;
        }

        //register team in the server
        if(board.getTeam(teamname)!=null){
            Objects.requireNonNull(board.getTeam(teamname)).unregister();
        }
        Team t= board.registerNewTeam(teamname);
        t.setDisplayName(teamname);
        t.setAllowFriendlyFire(false);
        List<String> playerList = new ArrayList<>();

        //insert team's data to the database
        Document teams= new Document("name",teamname)
                .append("player",playerList)
                .append("score",0);
        team.insertOne(teams);
        plugin.mg("隊伍"+teamname+"創建成功",s);
    }

    //add player to the team
    public void addPlayer(String teamname,String player,CommandSender s){

        //get team in the server
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team t = board.getTeam(teamname);
        //check if the team exist
        Document team1=team.find(eq("name",teamname)).first();
        if(team1==null) {
            plugin.mg(ChatColor.RED+"此隊伍不存在",s);
            return;
        }

        //get all the player in the team
        List<String> playerList = (List<String>) team1.get("player");
        for (String playerConfig :playerList){
            assert player != null;
            if(player.equals(playerConfig)){
                plugin.mg("此玩家已加入隊伍",s);
                return;
            }
        }
        playerList.add(player);
        assert t != null;
        t.addEntry(player);
        //update team data in database
        Document players = new Document("player",playerList);
        team.updateOne(eq("name",teamname), new Document("$set",players));
        //if the player is in the server then add the player to the team
        join.updateScoreboard();
        s.sendMessage(ChatColor.BLUE+ (player +"成功加入"+teamname));
    }

    //kick player in team
    public void deletePlayer(String teamname,String player,CommandSender s){

        //get the team
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team t = board.getTeam(teamname);

        //get the team in database
        Document team1=team.find(eq("name",teamname)).first();
        List<String> playerList = (List<String>) team1.get("player");
        assert playerList != null;

        //check if the player is in the team and kick it
        for (String playerConfig:playerList) {
            if(player.equals(playerConfig)){
                assert t != null;
                t.removeEntry(playerConfig);
                s.sendMessage(ChatColor.BLUE+ ("隊伍"+teamname+"已移除"+player));
                playerList.remove(playerConfig);
                Document players = new Document("player",playerList);
                team.updateOne(eq("name",teamname), new Document("$set",players));
                //if the player is in the server then add the player to the team
                join.updateScoreboard();
                return;
            }

        }

        plugin.mg("這玩家不存在於此隊伍",s);

    }

    //delete the team
    public void deleteTeam(String teamname,CommandSender s){

        //get the team
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team t = board.getTeam(teamname);

        //check if the team exist
        Document team1=team.find(eq("name",teamname)).first();
        if(team1==null){
            plugin.mg("此隊伍不存在",s);
            return;
        }

        //delete the team
        team.deleteOne(eq("name",teamname));
        s.sendMessage(ChatColor.BLUE+ ("隊伍"+teamname+"已解散"));
        assert t != null;
        t.unregister();
        //if the player is in the server then add the player to the team
        join.updateScoreboard();
    }

    //adds team score
    public void score(String player,int score,CommandSender s){
        Document team1=team.find(in("player",player)).first();
        if(team1==null){
            plugin.mg("此玩家不在隊伍裡",s);
            return;
        }
        if (score<0){
            plugin.mg("不得為小於0的數字",s);
            return;
        }
        String teamname=team1.getString("name");
        Document score1 = new Document("score",score);
        team.updateOne(eq("name",teamname),new Document("$set",score1));
    }

    //send player
    public void playerSend(String teamname,String server,CommandSender s) throws IOException {
        Document team1 = team.find(eq("name",teamname)).first();
        if(team1==null){
            plugin.mg("此隊伍不存在",s);
            return;
        }
        List<String> playerList = (List<String>) team1.get("player");
        if (playerList.isEmpty()){
            plugin.mg("這個隊伍沒有玩家",s);
            return;
        }
        for(String playerConfig: playerList){
            Player player =Bukkit.getPlayer(playerConfig);
            if(player!=null) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            }

        }

    }
}
