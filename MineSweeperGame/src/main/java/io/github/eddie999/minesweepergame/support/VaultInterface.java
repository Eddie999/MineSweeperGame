package io.github.eddie999.minesweepergame.support;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public interface VaultInterface {

    public static Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null) return null;
        return rsp.getProvider();
    }
   
    public static String getCurrencyName() {
    	Economy econ = getEconomy();
        if(econ == null) return null;        
        return econ.currencyNameSingular();
    }
    
    public static boolean deposit(Player player, double amount) {
    	Economy econ = getEconomy();
        if(econ == null) return false;
        
    	EconomyResponse r = econ.depositPlayer(player, amount);
    	if(!r.transactionSuccess()) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Vault transaction failed: " + r.errorMessage);
    	return r.transactionSuccess();
    }

}
