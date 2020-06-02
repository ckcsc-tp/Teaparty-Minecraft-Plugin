package ckcsc33rd.teapartyplugin.commands;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;


public class party implements CommandExecutor {

    TeapartyPlugin plugin;
    MongoCollection<Document> team;

    public party(TeapartyPlugin teapartyPlugin) {
        plugin = teapartyPlugin;
        team =TeapartyPlugin.team;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("lobby")){
            if(args.length==0) {
                try {
                    Lobby(sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
        if(command.getName().equalsIgnoreCase("teaparty")){
            if(args.length==0|| args[0].equals("help")) {
                sender.sendMessage("§a以下為茶會指令的列表");
                plugin.mg("party / p 一般隊伍指令", sender);
                plugin.mg("lobby 回到大廳 ", sender);
                if (sender.isOp()) {
                    plugin.mg("adminteam / at 管理隊伍指令", sender);
                }
                plugin.mg("永遠紀念MyKirito我的桐人，謝謝團長 By指令組", sender);
                return true;
            }
            return false;
        }
        if(command.getName().equalsIgnoreCase("party")) {
            if(args.length == 0|| args[0].equals("help")){
                sender.sendMessage("§a以下為party的使用方法");
                plugin.mg("/p help",sender);
                plugin.mg("/p list <隊伍>",sender);
                plugin.mg("/p chat <內容>",sender);
                return true;
            }
            if (args[0].equals("list")){
                if(args[1]==null) {
                    plugin.mg("請輸入隊伍名稱",sender);
                    return true;
                }
                teamPlayer(args[1],sender);
                return true;
            }
            if (args[0].equals("chat")) {
                if(args[1]==null){
                    plugin.mg("請輸入訊息",sender);
                }
                teamChat(args,sender.getName());
                return true;
            }
            return false;
        }
        return false;
    }
    public void teamPlayer(String teamname,CommandSender s){
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
        s.sendMessage(ChatColor.GREEN+"此隊伍的玩家有:");
        for(String playerConfig: playerList){
            plugin.mg(Objects.requireNonNull(playerConfig),s);
        }

    }
    public void teamChat(String[] content,String player){
        StringBuilder chatContent= new StringBuilder();
        for (int i=1;i<content.length;i++){
            chatContent.append(content[i]).append(" ");
        }
        Document party= team.find(in("player",player)).first();
        if(party!=null){
            List<String> teamPlayer = (List<String>) party.get("player");
            for(String p:teamPlayer){
                if (Bukkit.getPlayer(p)!=null){
                    Objects.requireNonNull(Bukkit.getPlayer(p))
                            .sendMessage("§6[隊伍]["+player+"]說 : "+chatContent);

                }
            }
        }
    }
    public void Lobby(CommandSender sender) throws IOException {
        if(Bukkit.getPlayer(sender.getName())!=null){
            Player p =Bukkit.getPlayer(sender.getName());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF("main");
            assert p != null;
            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        }
    }
}
