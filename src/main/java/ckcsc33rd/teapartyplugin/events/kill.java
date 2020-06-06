package ckcsc33rd.teapartyplugin.events;

import ckcsc33rd.teapartyplugin.TeapartyPlugin;
import ckcsc33rd.teapartyplugin.commands.adminteam;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class kill implements Listener {

    TeapartyPlugin plugin;
    public kill(TeapartyPlugin teapartyPlugin){
        plugin=teapartyPlugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e){
        Player player =e.getEntity();
        if(Objects.equals(plugin.getConfig().getString("tnt"), "true")){
            e.setDeathMessage("§c玩家"+player.getName()+"從艾恩葛朗特摔落了");
            player.setGameMode(GameMode.SPECTATOR);
            adminteam.tntScore();
        }else {
            Player killer = Objects.requireNonNull(e.getEntity().getKiller());
            ItemStack weapon = killer.getInventory().getItemInMainHand();
            String weaponName;
            if (weapon.getType().isAir()){
                weaponName="空手";
            }else if(Objects.requireNonNull(weapon.getItemMeta()).hasDisplayName()) {
                weaponName = Objects.requireNonNull(weapon.getItemMeta()).getDisplayName();
            }else weaponName=weapon.getType().name().replace("_", " ").toLowerCase();
            e.setDeathMessage("§c玩家"+killer.getName()+"使用了"+weaponName+"§c對"+player.getName()+"§l按下了我要殺死你");
            if(weaponName.equals("空手")){
                player.sendMessage("§c就憑你這菜b8 笑死");
            }
            adminteam.score(killer.getName(),10);
        }

    }
}
