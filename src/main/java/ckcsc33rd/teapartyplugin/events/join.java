package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class join implements Listener {

    TeapartyPlugin plugin;

    public join(TeapartyPlugin teapartyPlugin) {
        plugin=teapartyPlugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Welcome!");
        createScoreboard(p);
        updateScoreboard(p);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Quit");
        updateScoreboard(p);
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
        //plugin.getConfig().getConfigurationSection("clubs").getList("club");
        //Score clubName = objective.getScore("");
        for(Team team: board.getTeams()){
            for(OfflinePlayer player:team.getPlayers()){
                if(p.equals(player)){
                    team.addPlayer(p);
                }
            }
        }
        p.setScoreboard(board);
    }
    public void updateScoreboard(Player p){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();
        int onlinePlayer =  Bukkit.getOnlinePlayers().size();
        board.resetScores(ChatColor.YELLOW+ "Online ： " +(onlinePlayer-1));
        Score score= board.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.YELLOW+ "Online ： " +(onlinePlayer));
        score.setScore(0);
    }
}
