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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class join implements Listener {

    TeapartyPlugin plugin;
    public static final HashMap<Player,Scoreboard> scoreboard = new HashMap<Player, Scoreboard>();
    public join(TeapartyPlugin teapartyPlugin ) {
        plugin=teapartyPlugin;

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Welcome!");
        scoreboard.put(p, Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getNewScoreboard());
        teamCreate();
        updateScoreboard("join");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Quit");
        scoreboard.remove(p);
        updateScoreboard("quit");
    }
    public void teamCreate(){
        ScoreboardManager manager =Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board = manager.getMainScoreboard();
        FindIterable<Document> party = TeapartyPlugin.team.find();
        for(Document partys:party){
            String name =partys.getString("name");
            if(board.getTeam(name)==null){
                Team t = board.registerNewTeam(name);
                t.setDisplayName(name);
                t.setAllowFriendlyFire(false);
            }
        }
        Team team;
        for(Document teams: party){
            List<String> playerList = (List<String>) teams.get("player");
            String teamname = teams.getString("name");
            for (String players :playerList){
                    team = board.getTeam(teamname);
                    assert team != null;
                    team.addPlayer(Bukkit.getOfflinePlayer(players));
                }
            }
        }

    public static void updateScoreboard(String status){
        Collection<?> onlinePlayer = Bukkit.getOnlinePlayers();
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;

        for (Player online : Bukkit.getOnlinePlayers()){

            Scoreboard board=scoreboard.get(online);
            if(board.getObjective("info")!= null) {
                Objects.requireNonNull(board.getObjective("info")).unregister();
            }
            int number= (status.equals("join")) ? onlinePlayer.size() : onlinePlayer.size()-1;
            Objective info= board.registerNewObjective("info","dummy");
            info.setDisplayName("資訊欄");
            info.setDisplaySlot(DisplaySlot.SIDEBAR);
            Objective playerClub= board.registerNewObjective("club","dummy");
            playerClub.setDisplaySlot(DisplaySlot.BELOW_NAME);
            playerClub.setDisplayName("社團");
            String thisPlayerTeam = "你還沒有隊伍";
            String thisPlayerClub = "你還沒有社團";
            //register all team
            FindIterable<Document> party = TeapartyPlugin.team.find();
            FindIterable<Document> club = TeapartyPlugin.clubs.find();
            for(Team playerTeams: board.getTeams()){
                playerTeams.unregister();
            }
            for(Document teams: party){
                List<String> playerList = (List<String>) teams.get("player");
                String teamname = teams.getString("name");
                Team playerTeam = board.registerNewTeam(teamname);
                playerTeam.setDisplayName(teamname);
                playerTeam.setPrefix(ChatColor.GREEN+"["+teamname+"]");
                for (String players :playerList){
                    playerTeam.addPlayer(Bukkit.getOfflinePlayer(players));
                }
                if(playerTeam.hasPlayer(online)){
                    thisPlayerTeam = playerTeam.getName();
                }
            }


            info.getScore(ChatColor.YELLOW+ "線上人數 ： " +number).setScore(0);
            info.getScore(ChatColor.GREEN+ "隊伍 ： " +thisPlayerTeam).setScore(0);

            online.setScoreboard(board);
        }
    }
}
