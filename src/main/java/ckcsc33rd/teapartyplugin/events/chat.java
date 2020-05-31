package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.Set;
import static com.mongodb.client.model.Filters.*;

public class chat implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player p =e.getPlayer();
        updateDisplay(p);
    }
    public static void updateDisplay(Player p){
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Set<Team> teamSet = board.getTeams();
        String chatTeam="";
        String thisClub="";
        Document playerClub=TeapartyPlugin.clubs.find(eq("name",p.getName())).first();
        if(playerClub!=null){
            thisClub=playerClub.getString("club");
        }
        for (Team team :teamSet){
            if(team.getPlayers().contains(p)){
                chatTeam=("§b["+team.getName()+"]§f");
            };
        }
        Objects.requireNonNull(p.getPlayer()).setDisplayName(chatTeam+ ChatColor.GREEN+thisClub+ChatColor.WHITE +p.getName());
        p.getPlayer().setPlayerListName(chatTeam+ChatColor.GREEN+thisClub+ChatColor.WHITE +p.getName());
    }
}
