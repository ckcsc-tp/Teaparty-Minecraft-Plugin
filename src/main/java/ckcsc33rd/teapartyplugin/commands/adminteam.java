package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import ckcsc33rd.teapartyplugin.events.chat;
import ckcsc33rd.teapartyplugin.events.join;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class adminteam implements CommandExecutor {

    public static TeapartyPlugin plugin;
    public static MongoCollection<Document> team;
    public static final HashMap<String,Integer> scoreTeam = new HashMap<String, Integer>();
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
                    sender.sendMessage("§a以下為adminteam的使用方法");
                    plugin.mg("/at create <隊伍名稱>",sender);
                    plugin.mg("/at delete <隊伍名稱>",sender);
                    plugin.mg("/at add <隊伍名稱> <玩家>",sender);
                    plugin.mg("/at kick <玩家>",sender);
                    plugin.mg("/at score <隊伍名稱> <整數>",sender);
                    plugin.mg("/at send <隊伍名稱> <伺服器>",sender);
                    plugin.mg("/at show x y z",sender);
                    plugin.mg("/at last //最後存活的隊伍的分數為lastTeam，存活隊伍數為teamNumber",sender);
                    plugin.mg("/at tnt",sender);
                    plugin.mg("/at win",sender);
                    return true;
                }
                if(args[0].equals("list")){
                    TeamList(sender);
                    return true;
                }
                if(args[0].equals("create")){
                    if(args[1]==null) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    createTeam(args[1],sender);
                    return true;
                }

                if(args[0].equals("delete")){
                    if(args[1]==null) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    deleteTeam(args[1],sender);
                    return true;
                }
                if(args[0].equals("kick")){
                    if(args[1]==null) {
                        plugin.mg("請輸入玩家名稱",sender);
                        return true;
                    }
                    deletePlayer(args[1],sender);
                    return true;
                }
                if(args[0].equals("add")){
                    if(args[1]==null) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2]==null) {
                        plugin.mg("請輸入玩家名稱",sender);
                        return true;
                    }
                    addPlayer(args[1],args[2],sender);
                    return true;
                }
                if(args[0].equals("score")){
                    if(args[1]==null) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2]==null) {
                        plugin.mg("請輸入分數",sender);
                        return true;
                    }
                    score(args[1],Integer.parseInt(args[2]));
                    return true;
                }
                if(args[0].equals("send")){
                    if(args[1]==null) {
                        plugin.mg("請輸入隊伍名稱",sender);
                        return true;
                    }
                    if(args[2]==null) {
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

                if(args[0].equals("show")){
                    if (args[1]==null||args[2]==null||args[3]==null){
                        plugin.mg("請輸入正確的座標",sender);
                        return true;
                    }
                    showScore(Double.parseDouble(args[1]) ,Double.parseDouble(args[2]),Double.parseDouble(args[3]),sender);
                    return true;
                }
                if(args[0].equals("last")){
                    getLastTeam(sender);
                    return true;
                }
                if(args[0].equals("tnt")){
                    if(args[1].equals("true") || args[1].equals("false")) {
                        plugin.getConfig().set("tnt",args[1]);
                        return true;
                    }else plugin.mg("請輸入true或是false",sender);
                    return true;
                }
                if (args[0].equals("win")){
                    teamWin();
                    return true;
                }
                if(args[0].equals("update")){
                    join.teamCreate();
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
        if(Bukkit.getPlayer(player)!=null) {
            chat.updateDisplay(Objects.requireNonNull(Bukkit.getPlayer(player)));
        }
        s.sendMessage(ChatColor.BLUE+ (player +"成功加入"+teamname));
    }

    //kick player in team
    public void deletePlayer(String player,CommandSender s){

        //get the team
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board= manager.getMainScoreboard();

        //get the team in database
        Document team1=team.find(in("player",player)).first();
        if(team1==null){
            plugin.mg("此玩家並沒有隊伍",s);
            return;
        }
        List<String> playerList = (List<String>) team1.get("player");
        String teamName = team1.getString("name");
        Team t = board.getTeam(teamName);
        assert playerList != null;

        //check if the player is in the team and kick it
        assert t != null;
        t.removeEntry(player);
        s.sendMessage(ChatColor.BLUE+ ("隊伍"+teamName+"已移除"+player));
        playerList.remove(player);
        Document players = new Document("player",playerList);
        team.updateOne(eq("name",teamName), new Document("$set",players));
        //if the player is in the server then add the player to the team
        join.updateScoreboard();
        if(Bukkit.getPlayer(player)!=null) {
            chat.updateDisplay(Objects.requireNonNull(Bukkit.getPlayer(player)));
        }
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
    public static void score(String player,int score){
        Document team1=team.find(in("player",player)).first();
        if(team1==null){
            return;
        }

        String teamname=team1.getString("name");
        Document score1 = new Document("score",score+team1.getInteger("score"));
        team.updateOne(eq("name",teamname),new Document("$set",score1));
        List<String> teamPlayer = (List<String>) team1.get("player");
        for (String players:teamPlayer){
            if (Bukkit.getPlayer(players)!=null){
                Player p =Bukkit.getPlayer(players);

                p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,50,10) ;
                p.sendMessage("§d你們隊伍"+teamname+"獲得了"+score+"分");
            }
        }
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

    //summon a armor stand that shows teams score
    public void showScore(double x,double y,double z, CommandSender s){
        World world =s.getServer().getWorld("world");

        Double Y=y;
        assert false;
        FindIterable<Document> cursor=team.find().sort(new BasicDBObject("score",1));
        MongoCursor<Document> teams =cursor.iterator();
        for (MongoCursor<Document> it = teams; it.hasNext(); ) {
            Document Score = it.next();
            Location location1=new Location(world,x,Y,z);
            ArmorStand a =(ArmorStand)world.spawnEntity(location1, EntityType.ARMOR_STAND);
            a.setInvulnerable(true);
            a.setCollidable(false);
            a.setGravity(false);
            a.setVisible(false);
            a.setCustomNameVisible(true);
            a.setMarker(true);
            a.setCustomName("§b"+Score.getString("name")+" §f: "+Score.getInteger("score"));
            Y+=0.3;
        }
        Location location = new Location(world,x,Y,z);
        ArmorStand armorStand =(ArmorStand)world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName("§c隊伍分數\n");
        armorStand.setMarker(true);
        plugin.mg("已在座標 "+x+" "+y+" "+z+" "+"生成隊伍分數板",s);
    }
    public void getLastTeam(CommandSender sender){
        scoreTeam.putIfAbsent("Java",0);
        scoreTeam.putIfAbsent("Python",1);
        scoreTeam.putIfAbsent("PHP",2);
        scoreTeam.putIfAbsent("JavaScript",3);
        scoreTeam.putIfAbsent("Swift",4);
        scoreTeam.putIfAbsent("Kotlin",5);
        scoreTeam.putIfAbsent("Go",6);
        scoreTeam.putIfAbsent("C",7);
        scoreTeam.putIfAbsent("Basic",8);
        scoreTeam.putIfAbsent("HTML",9);
        scoreTeam.putIfAbsent("Aincrad",10);
        scoreTeam.putIfAbsent("Perl",11);
        scoreTeam.putIfAbsent("SQL",12);
        scoreTeam.putIfAbsent("C++",13);
        scoreTeam.putIfAbsent("Fortran",14);
        scoreTeam.putIfAbsent("Ruby",15);
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Set<Team> lastTeam = new HashSet<Team>();
        for (Player player: Bukkit.getOnlinePlayers()){
            if(!player.getGameMode().equals(GameMode.SPECTATOR) && board.getEntryTeam(player.getName()) !=null) {
                lastTeam.add(board.getEntryTeam(player.getName()));
            }
        }
        if(board.getObjective("info")==null){
            board.registerNewObjective("info","dummy");
        }
        board.getObjective("info").getScore("teamNumber").setScore(lastTeam.size());
        if(lastTeam.size()==1) {
            for (Team team : lastTeam) {
                board.getObjective("info").getScore("lastTeam").setScore(scoreTeam.get(team.getName()));
                plugin.mg("已剩下"+team.getName(),sender);
                return;
            }
        }
        plugin.mg("還剩下"+lastTeam.size()+"隊",sender);
    }
    public void teamWin(){
        for(Player player: Bukkit.getOnlinePlayers()){
            if (!player.getGameMode().equals(GameMode.SPECTATOR)){
                score(player.getName(),50);
                Bukkit.broadcastMessage("§a§l恭喜"+ Objects.requireNonNull(Objects.requireNonNull(Bukkit
                        .getScoreboardManager())
                        .getMainScoreboard()
                        .getEntryTeam(player.getName())).getName()+"贏得了本場遊戲的勝利!");
                return;
            }
        }
    }


    public static void tntScore(){
       for (Player player:Bukkit.getOnlinePlayers()){
           if (!player.getGameMode().equals(GameMode.SPECTATOR)){
               score(player.getName(),3);
           }
       }
    }
}
