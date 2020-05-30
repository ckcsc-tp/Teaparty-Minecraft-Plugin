package ckcsc33rd.teapartyplugin.events;


import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class chat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
       Player p =e.getPlayer();
       Team team= p.getScoreboard().getPlayerTeam(p);
        assert team != null;
        if (team!=null) {
            p.setDisplayName((ChatColor.BLUE + "[" + team.getName() + "]")+ChatColor.WHITE+ p.getName());
        }
    }
}
