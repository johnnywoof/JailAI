package me.johnnywoof;

import java.util.logging.Level;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Jail extends JavaPlugin{

	public void onEnable(){
		
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
 
		//Register your trait with Citizens.        
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(JailAI.class).withName("JailAI"));	
		
	}
	
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] inargs) {
	
	    NPCRegistry registry = CitizensAPI.getNPCRegistry();
	    NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.RED + "Guard");
	    npc.addTrait(JailAI.class);
	    npc.spawn(((Player) sender).getLocation());
		
		return true;
		
	}
	
}
