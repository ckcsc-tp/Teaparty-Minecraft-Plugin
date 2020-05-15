package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class adminteam implements CommandExecutor {

    TeapartyPlugin plugin;
    MongoCollection<Document> team;
    public adminteam(TeapartyPlugin teapartyPlugin,MongoCollection<Document> collection) {
        plugin=teapartyPlugin;
        team=collection;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("adminteam")) {
            if (sender.isOp()) {
                if(args.length == 0)
                    return false;
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

            }
            else {
                sender.sendMessage(ChatColor.RED+"你沒有權限執行此命令");
                return true;
            }
        }
        return false;
    }


    public void TeamList(CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        s.sendMessage(ChatColor.GREEN+ "隊伍名單:");
        for(Team team: board.getTeams()){
            plugin.mg(team.getName(),s);
        }
    }
    public void createTeam(String name,CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team teamExist = board.getTeam(name);
        if(teamExist!=null) {
            s.sendMessage(ChatColor.RED+name+"此隊伍已被創建");
            return;
        }
        Team team1 = board.registerNewTeam(name);
        team1.setPrefix(ChatColor.RED+("["+name+"]"));
        team1.setDisplayName(name);
        team1.setAllowFriendlyFire(false);
        List<String> playerList = new ArrayList<>();
        Document teams= new Document("name",name)
                .append("player",playerList)
                .append("score",0);
        team.insertOne(teams);
        plugin.mg("隊伍"+team1.getName()+"創建成功",s);
    }

    public void addPlayer(String teamname,String player,CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Document name = new Document("name",teamname);

        Document team1=team.find(name).first();

        if(team1==null) {
            plugin.mg(ChatColor.RED+"此隊伍不存在",s);
            return;
        }
        List<String> playerList = (List<String>) team1.get("player");
        for (String playerConfig :playerList){
            assert player != null;
            if(player.equals(playerConfig)){
                plugin.mg("此玩家已加入隊伍",s);
                return;
            }
        }
        playerList.add(player);
        if(Bukkit.getPlayer(player) !=null){
            Objects.requireNonNull(Bukkit.getPlayer(player)).setScoreboard(board);
        }
        Document players = new Document("player",playerList);
        team.updateOne(name, players);
        s.sendMessage(ChatColor.BLUE+ (player +"成功加入"+teamname));
    }

    public void deletePlayer(String team,String player,CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team team1 = board.getTeam(team);
        List<String> playerList = (List<String>) plugin.getConfig().getList("teams."+team1.getName()+".players");
        assert playerList != null;

        for (String p:playerList) {
            if(player.equals(p)){
                team1.removeEntry(p);
                s.sendMessage(ChatColor.BLUE+ ("隊伍"+team1.getName()+"已移除"+player));
                playerList.remove(p);
                plugin.getConfig().set("teams."+team1.getName()+".players",playerList);
                plugin.saveConfig();
                return;
            }
        }
        plugin.mg("這玩家不存在於此隊伍",s);


    }
    public void deleteTeam(String team,CommandSender s){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        Team team1 = board.getTeam(team);
        if(team1==null){
            plugin.mg("此隊伍不存在",s);
            return;
        }
        plugin.getConfig().set("teams."+team1.getName(),null);
        plugin.saveConfig();
        s.sendMessage(ChatColor.BLUE+ ("隊伍"+team1.getName()+"已解散"));
        team1.unregister();
    }
}
