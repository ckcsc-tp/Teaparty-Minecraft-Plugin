package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class join implements Listener {

    TeapartyPlugin plugin;
    MongoCollection<Document> team;
    public join(TeapartyPlugin teapartyPlugin ,MongoCollection<Document> collection) {
        plugin=teapartyPlugin;
        team = collection;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Welcome!");
        createScoreboard(p);
        updateScoreboard("join");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Quit");
        updateScoreboard("quit");
    }
    public void createScoreboard(Player p){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getNewScoreboard();
        Objective objective= board.registerNewObjective("info","dummy");
        objective.setDisplayName("Info");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int onlinePlayer =  Bukkit.getOnlinePlayers().size();
        objective.getScore(ChatColor.YELLOW+ "Online ： " +onlinePlayer).setScore(0);
        objective.getScore(ChatColor.RED+ "Club:").setScore(0);
        FindIterable<Document> party = team.find();
        for(Document partys:party){
            String name =partys.getString("name");
            if(board.getTeam(name)==null){
                Team t = board.registerNewTeam(name);
                t.setPrefix(ChatColor.RED+("["+name+"]"));
                t.setDisplayName(name);
                t.setAllowFriendlyFire(false);
            }
        }
        Team team;
        for(Document teams: party){
            List<String> playerList = (List<String>) teams.get("player");
            String teamname = teams.getString("name");
            for (String players :playerList){
                if(p.getName().equals(players)){
                  team = board.getTeam(teamname);
                  assert team != null;
                  team.addPlayer(p);
                  System.out.println(team.getName());
                  System.out.println(p.getName());
                }
            }
        }
        p.setScoreboard(board);
    }
    public void updateScoreboard(String status){
        int oldPlayer= plugin.getConfig().getInt("player");
        Collection<?> onlinePlayer = Bukkit.getOnlinePlayers();
        for (Player online : Bukkit.getOnlinePlayers()){
            if(status.equals("join")) {
                Scoreboard board = online.getScoreboard();
                board.resetScores("Online : "+ oldPlayer);
                board.getObjective(DisplaySlot.SIDEBAR)
                        .getScore(ChatColor.YELLOW+ "Online ： " +onlinePlayer.size())
                        .setScore(0);
                plugin.getConfig().set("player",oldPlayer+1);
                System.out.println(plugin.getConfig().getInt("player"));
            }else if(status.equals("quit")) {
                Scoreboard board = online.getScoreboard();
                board.resetScores("Online : "+ oldPlayer);
                board.getObjective(DisplaySlot.SIDEBAR)
                        .getScore(ChatColor.YELLOW+ "Online ： " +onlinePlayer.size())
                        .setScore(0);
                plugin.getConfig().set("player",oldPlayer-1);
                System.out.println(plugin.getConfig().getInt("player"));
            }
        }
    }
}
