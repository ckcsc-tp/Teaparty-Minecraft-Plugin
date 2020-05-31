package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class join implements Listener {

    TeapartyPlugin plugin;
    public static final HashMap<Player,Scoreboard> scoreboard = new HashMap<Player, Scoreboard>();
    public static final HashMap<String,Integer> onlinePlayer  = new HashMap<String, Integer>();
    public join(TeapartyPlugin teapartyPlugin ) {
        plugin=teapartyPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Welcome!");
        scoreboard.put(p, Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getNewScoreboard());
        updateScoreboard();
        chat.updateDisplay(p);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.GREEN+p.getName()+ChatColor.YELLOW+" Quit");
        scoreboard.remove(p);
        if(p.getGameMode().equals(GameMode.SPECTATOR)){
            updatePeople("spectateQuit");
        }else {
            updatePeople("quit");
        }
    }
    @EventHandler
    public void onModeChange(PlayerGameModeChangeEvent e){
        GameMode gameMode= e.getNewGameMode();
        if(gameMode.equals(GameMode.SPECTATOR)) {
            updatePeople("notAlive");
        }else if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)){
            updatePeople("fromSpectator");
        }else {updatePeople("join");}
    }

    public static void teamCreate(){
        ScoreboardManager manager =Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board = manager.getMainScoreboard();
        FindIterable<Document> party = TeapartyPlugin.team.find();
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
                    team = board.getTeam(teamname);
                    assert team != null;
                    team.addPlayer(Bukkit.getOfflinePlayer(players));
                }
            }
        }
    public static void updatePeople(String status){
        int Alive=0;
        if(onlinePlayer.get("online")==null){
            onlinePlayer.put("online",Bukkit.getOnlinePlayers().size());
        }
        for (Player online : Bukkit.getOnlinePlayers()){
            GameMode gameMode =online.getGameMode();
            if(gameMode.equals(GameMode.SURVIVAL)||gameMode.equals(GameMode.ADVENTURE)||gameMode.equals(GameMode.CREATIVE)) {
                Alive++;
            }

        }
        if(status.equals("quit")||status.equals("notAlive")){
            Alive--;
        }
        if(status.equals("fromSpectator")) Alive++;
        onlinePlayer.putIfAbsent("spectate", Alive);

        int number= (status.equals("join")||status.equals("notAlive")||status.equals("fromSpectator")) ? Bukkit.getOnlinePlayers().size() : Bukkit.getOnlinePlayers().size()-1;

        for (Player online : Bukkit.getOnlinePlayers()){
            Scoreboard board = scoreboard.get(online);
            board.resetScores("§6人數 §f： "+onlinePlayer.get("spectate")+"/"+onlinePlayer.get("online"));
            board.getObjective("info")
                    .getScore("§6人數 §f： "+Alive+"/"+number)
                    .setScore(4);
        }
        onlinePlayer.put("online",number);
        onlinePlayer.put("spectate",Alive);

    }
    public static void updateScoreboard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;

        for (Player online : Bukkit.getOnlinePlayers()){

            Scoreboard board=scoreboard.get(online);
            if(board.getObjective("info")!= null) {
                Objects.requireNonNull(board.getObjective("info")).unregister();
            }
            if(board.getObjective("club")!= null) {
                Objects.requireNonNull(board.getObjective("club")).unregister();
            }

            Objective info= board.registerNewObjective("info","dummy");
            info.setDisplayName("§6§lResplendent §r§fX §9§lUltramarine");
            info.setDisplaySlot(DisplaySlot.SIDEBAR);

            String thisPlayerTeam = "你還沒有隊伍";
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
            info.getScore("§8-------------------------").setScore(5);
            info.getScore("§a隊伍 §f： " +thisPlayerTeam).setScore(3);
            info.getScore("§8----------§cmc.ckcsc.net§8---------").setScore(2);
            online.setScoreboard(board);
            join.updatePeople("join");
        }
    }
}
