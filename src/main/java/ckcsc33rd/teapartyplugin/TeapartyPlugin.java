package ckcsc33rd.teapartyplugin;

import ckcsc33rd.teapartyplugin.commands.adminteam;
import ckcsc33rd.teapartyplugin.commands.team;
import ckcsc33rd.teapartyplugin.events.join;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TeapartyPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
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
        getServer().getPluginManager().registerEvents(new join(this), this);
    }

    public void setupCommands(){
        Objects.requireNonNull(getCommand("team")).setExecutor(new team(this));
        Objects.requireNonNull(getCommand("adminteam")).setExecutor(new adminteam(this));
    }
    public void  mg(String m, CommandSender sender){
        sender.sendMessage(ChatColor.YELLOW+m);
    }
}
