package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.MongoCollection;
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
        Scoreboard board= manager.getMainScoreboard();
        Objective objective= board.registerNewObjective("info","dummy");
        objective.setDisplayName("Info");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int onlinePlayer =  Bukkit.getOnlinePlayers().size();
        Score online = objective.getScore(ChatColor.YELLOW+ "Online ： " +onlinePlayer);
        online.setScore(0);
        Score club = objective.getScore(ChatColor.RED+ "Club:");
        club.setScore(0);
        List<String> party = (List<String>) plugin.getConfig().getList("teams");
        for(String partys:party){
            if(board.getTeam(partys)==null){
                board.registerNewTeam(partys);
            }
        }
        for(Team team: board.getTeams()){
            for(OfflinePlayer player:team.getPlayers()){
                if(p.equals(player)){
                    team.addPlayer(p);
                }
            }
        }
        p.setScoreboard(board);
    }
    public void updateScoreboard(String status){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        int onlinePlayer =  Bukkit.getOnlinePlayers().size();
        if ((status.equals("join"))) {
            board.resetScores(ChatColor.YELLOW + "Online ： " + (onlinePlayer - 1));
        } else {
            board.resetScores(ChatColor.YELLOW + "Online ： " + (onlinePlayer + 1));
        }
        Score score= board.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW+ "Online ： " +(onlinePlayer));
        score.setScore(0);
    }
}
