package me.johnnywoof;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
public class JailAI extends Trait {

	Jail plugin = null;
	
	public JailAI() {
		super("JailAI");
		plugin = (Jail) Bukkit.getServer().getPluginManager().getPlugin("Jail");
	}
	
	private int tc = 0;
	private Player cn = null;
	private boolean wasclose = false;
	private boolean hasarmor = false;
	private int sc = 5;

	//Here you should load up any values you have previously saved. 
	//This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
	//This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
	//This is called BEFORE onSpawn so do not try to access npc.getBukkitEntity(). It will be null.
	public void load(DataKey key) {
		
	}

	//Save settings for this NPC. These values will be added to the citizens saves.yml under this NPC.
	public void save(DataKey key) {
		
	}

	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
		
		if(event.getNPC().getId() == this.npc.getId()){
			
			event.getClicker().sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "Why hello there " + event.getClicker().getName());
			
		}

	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event){
		
		if(cn != null){
		
			if(event.getPlayer().getUniqueId().equals(cn.getUniqueId())){
				
				Material m = event.getItemDrop().getItemStack().getType();
				
				if(m == Material.DIAMOND_SWORD || m == Material.IRON_SWORD){
					
					event.getItemDrop().remove();
					
					cn.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "Thank you, have a nice day.");
    				
    				this.sc = 5;
    				
    				cn = null;
    				
    				wasclose = false;
					
				}
				
			}
		
		}

	}


	//Run code when your trait is attached to a NPC. 
	//This is called BEFORE onSpawn so do not try to access npc.getBukkitEntity(). It will be null.
	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info(npc.getName() + "has been assigned JailAI!");
		
		//This will send a empty key to the Load method, forcing it to load the config.yml defaults.
		//Load will get called again with a real key if this NPC has previously been saved
		load(new net.citizensnpcs.api.util.MemoryDataKey());
	}
	
	// Called every tick
    @SuppressWarnings("deprecation")
	@Override
    public void run() {
    	
    	this.tc++;
    	
    	if(this.tc >= 20){
    		
    		this.onTickSecond();
    		
    		this.tc = 0;
    		
    	}
    	
    	if(this.npc.isSpawned()){
    		
    		if(!hasarmor){
    			
    			this.npc.getBukkitEntity().getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    			this.npc.getBukkitEntity().getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
    			this.npc.getBukkitEntity().getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    			this.npc.getBukkitEntity().getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
    			this.npc.getBukkitEntity().getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
    			
    			
    			hasarmor = true;
    			
    		}
    		
    		if(cn != null){
    			
    			this.npc.faceLocation(cn.getLocation());
    			
    			double d = this.npc.getBukkitEntity().getLocation().distanceSquared(cn.getLocation());
    			
    			if(d <= 4){
    				
    				if(this.npc.getNavigator().isNavigating()){
    					
    					this.npc.faceLocation(cn.getLocation());
    					
    					this.npc.getNavigator().cancelNavigation();
    					
    				}
    				
    			}
    			
    			if(d > 15 && wasclose){
    				
    				cn.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "You tried to escape!");
    				
    				this.sc = 5;
    				
    				cn = null;
    				
    				wasclose = false;
    				
    			}else if(d <= 15){
    				
    				wasclose = true;
    				
    			}
    			
    		}
    		
    		if(!this.npc.getNavigator().isNavigating()){
    		
    			if(cn == null){
    			
		    		for(Player p : plugin.getServer().getOnlinePlayers()){
		    			
		    			double dis = this.npc.getBukkitEntity().getLocation().distanceSquared(p.getLocation());
		    			
		    			if(dis <= 140 && dis > 3){
		    				
		    				if(p.getItemInHand() != null){
		    				
		    					if(p.getItemInHand().getType() == Material.IRON_SWORD || p.getItemInHand().getType() == Material.DIAMOND_SWORD){
		    					
				    				if(this.npc.getBukkitEntity().hasLineOfSight(p)){
				    					
				    					this.npc.getNavigator().setTarget(p.getLocation());
				    					
				    					this.npc.faceLocation(p.getLocation());
				    					
				    					wasclose = false;
				    					
				    					cn = p;
			    						
			    						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "Sword please " + p.getDisplayName());
				    					
				    					break;
				    					
				    				}
			    				
		    					}
		    				
		    				}
		    				
		    			}
		    			
		    		}
	    			
	    		}
    		
    		}
    		
    	}
    	
    }
    
    //called on every 20 tickets
    
    public void onTickSecond(){
    	
    	if(this.npc.isSpawned()){
    		
    		if(cn != null){
    			
    			if(sc <= 0){
    				
    				cn.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "Jailed");
    				
    				cn.getInventory().remove(Material.DIAMOND_SWORD);
    				
    				this.sc = 5;
    				
    				cn = null;
    				
    				return;
    				
    			}
    			
    			cn.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Guard" + ChatColor.GOLD + " -> me] " + ChatColor.WHITE + "" + sc);
    			
    			sc = sc - 1;
    			
    		}
    		
    	}
    	
    }

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	@SuppressWarnings("deprecation")
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {

		
		
	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

}

