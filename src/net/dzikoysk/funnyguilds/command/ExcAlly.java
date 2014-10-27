package net.dzikoysk.funnyguilds.command;

import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcAlly extends Exc {
	
	public ExcAlly(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
	    if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excAlly)) return false;

	    if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
	    
	    Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User lp = User.get(p);
		
	    if(!lp.hasGuild()){
	    	p.sendMessage(m.getMessage("allyHasNotGuild"));
	    	return true;
	    }
	    
	    if(!lp.isOwner()){
	    	p.sendMessage(m.getMessage("allyIsNotOwner"));
	    	return true;
	    }
	    
	    Guild guild = lp.getGuild();
	    
	    if(args.length < 1){

	    	if(InvitationsList.get(guild, 1).getLS().isEmpty()){
				p.sendMessage(m.getMessage("allyHasNotInvitation"));
				return true;
			}
	    	
	    	List<String> list = m.getList("allyInvitationList");
			String[] msgs =  list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(InvitationsList.get(guild, 1).getLS(), true);
			for(int i = 0; i < msgs.length; i++)
				p.sendMessage(msgs[i]
					.replace("{GUILDS}", iss)
				);
			return true;
	    }
	    
	    String tag = args[0];
	    
	    if(!GuildUtils.tagExists(tag)){
	    	p.sendMessage(StringUtils
	    		.replace(m.getMessage("allyGuildExists"), "{TAG}", tag));
	    	return true;
		}
	    
	    Guild inv = GuildUtils.byTag(tag);
	    
	    if(guild.equals(inv)){
	    	p.sendMessage(m.getMessage("allySame"));
	    	return true;
	    }
	    
	    if(guild.getAllies().contains(inv)){
	    	p.sendMessage(m.getMessage("allyAlly"));
	    	return true;
	    }
	    
	    if(InvitationsList.get(guild, 1).contains(inv.getName())){
	    	InvitationsList.get(guild, 1).remove(inv.getName());
	    	
	    	guild.addAlly(inv);
	    	inv.addAlly(guild);
	    	
	    	p.sendMessage(StringUtils
		    	.replace(m.getMessage("allyDone"), "{GUILD}", inv.getName())
	    	);
	    	
	    	OfflinePlayer of = Bukkit.getOfflinePlayer(inv.getOwner().getName());
		    if(of.isOnline()) of.getPlayer().sendMessage(
		    	m.getMessage("allyIDone")
		    	.replace("{GUILD}", guild.getName())
			);
		    
		    for(User u : guild.getMembers())
		    	IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, inv);
			for(User u : inv.getMembers())
				IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
			
	    	return true;
	    }
	    
	    if(InvitationsList.get(inv, 1).getLS().contains(guild.getName())){
	    	InvitationsList.get(inv, 1).remove(guild.getName());
	    	p.sendMessage(
	    		m.getMessage("allyReturn")
	    		.replace("{GUILD}", inv.getName())
	    	);
	    	
	    	OfflinePlayer of = Bukkit.getOfflinePlayer(inv.getOwner().getName());
		    if(of.isOnline()) of.getPlayer().sendMessage(m.getMessage("allyIReturn")
		    	.replace("{GUILD}", guild.getName())
			);
		    
	    	return true;
	    }
	    
	    InvitationsList.get(inv, 1).add(guild.getName());
	    
	    p.sendMessage(m.getMessage("allyInviteDone")
	    	.replace("{GUILD}", inv.getName())
	    );
	    
	    OfflinePlayer of = Bukkit.getOfflinePlayer(inv.getOwner().getName());
	    if(of.isOnline()) of.getPlayer().sendMessage(m.getMessage("allyToInvited")
	    	.replace("{GUILD}", guild.getName())
		);
	    return true;
	}
}
