package ckcsc33rd.teapartyplugin;

import ckcsc33rd.teapartyplugin.commands.adminteam;
import ckcsc33rd.teapartyplugin.commands.club;
import ckcsc33rd.teapartyplugin.commands.party;
import ckcsc33rd.teapartyplugin.commands.tabcomplete;
import ckcsc33rd.teapartyplugin.events.chat;
import ckcsc33rd.teapartyplugin.events.join;
import ckcsc33rd.teapartyplugin.events.kill;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class TeapartyPlugin extends JavaPlugin implements Listener {
    public static MongoCollection<Document> team;
    public static MongoCollection<Document> clubs;
    SimpleDateFormat format =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @Override
    public void onEnable() {
        // Plugin startup logic
        MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://ItisCaleb:c12345678@teaparty-gr0h8.mongodb.net/test?retryWrites=true&w=majority");
        MongoDatabase database = mongoClient.getDatabase("teaparty");
        team = database.getCollection("teams");
        clubs = database.getCollection("clubs");
        System.out.println("DB connect");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        setupCommands();
        setupEvents();
        new BukkitRunnable(){

            @Override
            public void run() {
                for(Player player:Bukkit.getOnlinePlayers()){
                    player.setPlayerListFooter("§b§lmc.ckcsc.net§r \n"+"§e現在時間：§f"+format.format(new Date()) );
                }
            }
        }.runTaskTimer(this,0,20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void setupEvents(){
        getServer().getPluginManager().registerEvents(new join(this), this);
        getServer().getPluginManager().registerEvents(new chat(),this);
        getServer().getPluginManager().registerEvents(new kill(this),this);
    }

    public void setupCommands(){
        Objects.requireNonNull(getCommand("party")).setExecutor(new party(this));
        Objects.requireNonNull(getCommand("lobby")).setExecutor(new party(this));
        Objects.requireNonNull(getCommand("teaparty")).setExecutor(new party(this));
        Objects.requireNonNull(getCommand("adminteam")).setExecutor(new adminteam(this));
        Objects.requireNonNull(getCommand("club")).setExecutor(new club(this));
        Objects.requireNonNull(getCommand("party")).setTabCompleter(new tabcomplete());
        Objects.requireNonNull(getCommand("adminteam")).setTabCompleter(new tabcomplete());
    }

    public void  mg(String m, CommandSender sender){
        sender.sendMessage(ChatColor.YELLOW+m);
    }




}
