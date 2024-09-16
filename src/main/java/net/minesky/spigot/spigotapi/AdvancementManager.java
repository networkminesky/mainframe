package net.minesky.spigot.spigotapi;

import net.minesky.spigot.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class AdvancementManager {

    private static void awardCriteria(Player p, Advancement adv, String crit) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.getAdvancementProgress(adv).awardCriteria(crit);
            }
        }.runTask(SpigotMain.getInstance());
    }

    public static NamespacedKey fromDefaultDatapack(String key) {
        return new NamespacedKey("minesky", key);
    }

    //                   namespaced key | cache salvo do advancement
    public static HashMap<String, Advancement> cache = new HashMap<>();

    public static void awardAdvancement(Player p, NamespacedKey namespacedKey, String firstCriteria) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String key = namespacedKey.getKey();

                Advancement a = cache.getOrDefault( key, iterateAdvancement(namespacedKey) );

                if(a != null)
                    awardCriteria(p, a, firstCriteria);
            }
        }.runTaskAsynchronously(SpigotMain.getInstance());
    }

    @Nullable
    private static Advancement iterateAdvancement(NamespacedKey namespacedKey) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            Advancement a = it.next();
            if (namespacedKey.equals(a.getKey())) {
                return a;
            }
        }
        return null;
    }

    public static void multiAwardAdvancement(Collection<Player> p, NamespacedKey id, String firstCriteria) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Advancement n = null;
                Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
                while (it.hasNext()) {
                    Advancement a = it.next();
                    if (a.getKey().equals(id)) {
                        n = a;
                    }
                }

                if(n==null)
                    return;

                for(Player d : p) {
                    awardCriteria(d, n, firstCriteria);
                }
            }
        }.runTaskAsynchronously(SpigotMain.getInstance());
    }
}
