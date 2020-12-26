package fr.liven.ultimawslinker;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	
	public void onEnable() {
		
		plugin = this;
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[UltimaWSLinker] " + ChatColor.GOLD + "Plugin has started succefully.");
	}
	
	public void onDisable() {
		
		plugin = null;
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[UltimaWSLinker] " + ChatColor.GOLD + "Disabling UltimaWSLinker");
	}
	
	public boolean loadConfig() {
		if(!new File(getDataFolder() + File.separator + "config.yml").exists()) {
			saveDefaultConfig();
		}
		try {
			new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[UltimaWSLinker] " + ChatColor.RED + "Error while loading config.yml");
			Bukkit.getConsoleSender().sendMessage("Please check error dump below.");
			Bukkit.getConsoleSender().sendMessage(" ");
		    e.printStackTrace();
		    Bukkit.getPluginManager().disablePlugin(this);
		      
		    return false;
		}
		reloadConfig();
		return true;
	}
	
	public File getFile() {
		return super.getFile();
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       if(label.equalsIgnoreCase("reqmclink")) {
    	   if(sender instanceof ConsoleCommandSender) {
    		   if(args.length == 0) {
    			   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Missing arguments on /reqmclink (Command executed: \"/reqmclink\")");
    		   } else {
    			   if(args.length == 1) {
    				   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Missing arguments on /reqmclink (Command executed: \"/reqmclink " + args[0] + "\")");
    			   }
    			   else {
    				   if(args.length > 2) {
    					   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Too many arguments on /reqmclink.");
    				   }
    				   else {
    					   Player player = Bukkit.getPlayerExact(args[0]);
    					   if(player == null) {
    						   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Player \"" + args[0] + "\" is not connected (/reqmclink command).");
    					   }
    					   else {

    						   String p_uuid = player.getUniqueId().toString().replaceAll("-", "");
    						   FileConfiguration.createPath(getConfig().getConfigurationSection("SentReqs"), p_uuid);
    						   getConfig().set("SentReqs." + p_uuid + ".username", player.getName());
    						   getConfig().set("SentReqs." + p_uuid + ".wsacc", args[1]);
    						   getConfig().set("SentReqs." + p_uuid + ".req", "link");
    						   saveConfig();
    						   reloadConfig();
    						   
    						   sender.sendMessage(ChatColor.DARK_GREEN + "[UltimaWSLinker] Sent link request to link MC: \"" + args[0] + "\" to WS: \"" + args[1] + "\" (/reqmclink command).");
    						   player.sendMessage(" ");
    						   player.sendMessage(ChatColor.DARK_GRAY + "\\_______________________________________________/");
    						   player.sendMessage(" ");
    						   player.sendMessage(ChatColor.GOLD + "Une requête pour lier votre compte avec le compte Ultima \"" + args[1] + "\" a été reçue!");
    						   TextComponent msg = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Cliquez ici pour accepter et lier vos comptes]");
    						   msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptlinkreq"));
    						   player.spigot().sendMessage(msg);
    						   player.sendMessage(" ");
    						   player.sendMessage(ChatColor.RED + "(Si cette demande ne viens pas de vous, veuillez ignorer ce message.)");
    						   player.sendMessage(" ");
    						   player.sendMessage(ChatColor.DARK_GRAY + "\\_______________________________________________/");
    						   player.sendMessage(" ");
    					   }
    				   }
    			   }
    		   }
    	   }
    	   else {
    		   sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
    	   }
       }
       else if(label.equalsIgnoreCase("acceptlinkreq")) {
    	   if(sender instanceof Player) {
    		   String p_uuid = ((Player) sender).getUniqueId().toString().replaceAll("-", "");
    		   if(getConfig().contains("SentReqs." + p_uuid)) {
				   FileConfiguration.createPath(getConfig().getConfigurationSection("AcceptedReqs"), p_uuid);
				   getConfig().set("AcceptedReqs." + p_uuid + ".username", sender.getName());
				   getConfig().set("AcceptedReqs." + p_uuid + ".wsacc", getConfig().getString("SentReqs." + p_uuid + ".wsacc"));
				   getConfig().set("AcceptedReqs." + p_uuid + ".req", "link");
				   getConfig().set("SentReqs." + p_uuid, null);
				   saveConfig();
				   reloadConfig();
				   
				   sender.sendMessage(" ");
				   sender.sendMessage(ChatColor.YELLOW + "" + "Vous avez accepté de lier vos comptes Minecraft et Ultima ensemble, veuillez retourner sur le site et cliquer sur le bouton \"j'ai accepté\".");

    		   }
    		   else {
    			   sender.sendMessage(ChatColor.RED + "Aucune demande de lien n'a été trouvée, veuillez ré-essayer de lier vos comptes sur le site.");
    		   }
    	   }
    	   else {
    		   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Console cannot execute /acceptlinkreq (Only players can).");
    	   }
       }
       else if(label.equalsIgnoreCase("tellreqaccepted")) {
    	   if(sender instanceof ConsoleCommandSender) {
    		   if(args.length == 0) {
    			   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Missing arguments on /tellreqaccepted (Command executed: \"/tellreqaccepted\")");
    		   }
    		   else if(args.length == 1) {
    			   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Missing arguments on /tellreqaccepted (Command executed: \"/tellreqaccepted " + args[0] + "\")");
    		   }
    		   else if(args.length > 2) {
    			   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Too many arguments on /tellreqaccepted.");
    		   }
    		   else {
    			   Player player = Bukkit.getPlayerExact(args[0]);
    			   if(player == null) {
    				   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Player \"" + args[0] + "\" is not connected (/tellreqaccepted command).");
    			   }
    			   else {
    				   String p_uuid = player.getUniqueId().toString().replaceAll("-", "");
    				   getConfig().set("AcceptedReqs." + p_uuid, null);
    				   getConfig().set("SentReqs." + p_uuid, null);
    				   saveConfig();
    				   reloadConfig();
    				   player.sendMessage(" ");
    				   player.sendMessage(ChatColor.GREEN + "Votre compte Minecraft \"" + args[0] + "\" a été lié avec le compte Ultima \"" + args[1] + "\" avec succès!" + ChatColor.GRAY + " (Si vous n'avez pas fait exprès, veuillez délier vos comptes dans les paramètres au hub.)");
    				   player.sendMessage(" ");
    				   sender.sendMessage(ChatColor.DARK_GREEN + "[UltimaWSLinker] Told \"" + args[0] + "\" that his account is now linked with UltimaWS account \"" + args[1] + "\" (/tellreqaccepted).");
    				   sender.sendMessage(ChatColor.DARK_GREEN + "[UltimaWSLinker] Also cleared all his request on .yml");
    			   }
    		   }
    	   }
    	   else {
    		   sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
    	   }
       }
       else if(label.equalsIgnoreCase("unlinkwsmc")) {
    	   if(sender instanceof Player) {
    		   // SEND WEBHOOK COMMAND THAT SAYS IF THIS MC ACC IS LINKED TO AN WS ACC, UNLINK IT AND SEND JSONAPI TO REMOVE RANK OF MC ACCOUNT
    		   sender.sendMessage(ChatColor.YELLOW + "Si votre compte Minecraft est lié à un compte site, il a été délié et votre grade n'a été conservé que sur le site.");
    		   Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[UltimaWSLinker] Player \"" + sender.getName() + "\" requested to unlink his MC account and his WS account (At this point we do not know if his MC account is linked to a WS account.)");
    	   }
    	   else {
    		   sender.sendMessage(ChatColor.DARK_RED + "[UltimaWSLinker] Console cannot execute /unlinkwsmc (Only players can).");
    	   }
       }
        return false;
    }
}
