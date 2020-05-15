package ckcsc33rd.teapartyplugin;

import ckcsc33rd.teapartyplugin.commands.adminteam;
import ckcsc33rd.teapartyplugin.commands.party;
import ckcsc33rd.teapartyplugin.events.join;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Objects;

public final class TeapartyPlugin extends JavaPlugin implements Listener {
    public MongoCollection<Document> team;
    @Override
    public void onEnable() {
        // Plugin startup logic
        MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://ItisCaleb:c12345678@teaparty-gr0h8.mongodb.net/test?retryWrites=true&w=majority");
        MongoDatabase database = mongoClient.getDatabase("teaparty");
        team = database.getCollection("teams");
        System.out.println("DB connect");
        if(this.getConfig().get("player")==null)
            this.getConfig().set("player",0);
        setupCommands();
        setupEvents();
        getConfig().options().copyDefaults(true);
        saveConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void setupEvents(){
        getServer().getPluginManager().registerEvents(new join(this,team), this);
    }

    public void setupCommands(){
        Objects.requireNonNull(getCommand("party")).setExecutor(new party(this,team));
        Objects.requireNonNull(getCommand("adminteam")).setExecutor(new adminteam(this,team));
    }
    public void  mg(String m, CommandSender sender){
        sender.sendMessage(ChatColor.YELLOW+m);
    }




}
