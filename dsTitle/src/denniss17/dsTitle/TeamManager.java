package denniss17.dsTitle;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import denniss17.dsTitle.Title;

public class TeamManager {	
	private DS_Title plugin;
	
	public TeamManager(DS_Title plugin) {
		this.plugin = plugin;
	}

	/**
	 * Returns a Team with the right prefix and suffix
	 * @param title The title to get prefix and suffix from
	 * @return the Team
	 * @ensure result.getPrefix().equals(title.headprefix) && result.getSuffix().equals(title.headsuffix)
	 */
	public Team getTeam(Title title){
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		Team team = scoreboard.getTeam("dt_" + (title.name.length()>13 ? title.name.substring(0, 13) : title.name));
		
		if(team==null){
			team = scoreboard.registerNewTeam("dt_" + (title.name.length()>13 ? title.name.substring(0, 13) : title.name));
			if(title.headprefix != null){
				String prefix = ChatStyler.setTotalStyle(title.headprefix);
				if(prefix.length()>16) prefix = prefix.substring(0, 16);
				team.setPrefix(prefix);
			}
			if(title.headsuffix != null){
				String suffix = ChatStyler.setTotalStyle(title.headsuffix);
				if(suffix.length()>16) suffix = suffix.substring(0, 16);
				team.setSuffix(suffix);
			}
			// Set options to same as if not in team
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(false);
		}
		
		return team;
	}

	/**
	 * Remove the player from the team corresponding to the given title
	 * @param player
	 * @param title
	 * @require title!=null
	 */
	public void removePlayerFromTeam(Player player, Title title) {
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		Team team = scoreboard.getTeam("dt_" + (title.name.length()>13 ? title.name.substring(0, 13) : title.name));
		if(team!=null){
			team.removePlayer(player);
			// Cleanup
			if(team.getSize()==0){
				team.unregister();
			}
		}
	}

	/**
	 * Reload the prefixes and suffixes of the teams
	 */
	public void reloadTags() {
		Set<Title> titles = plugin.getTitles();
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
		Team team;
		
		for(Title title : titles){
			team = scoreboard.getTeam("dt_" + (title.name.length()>13 ? title.name.substring(0, 13) : title.name));
			if(team!=null){
				if(title.headprefix != null) team.setPrefix(ChatStyler.setTotalStyle(title.headprefix));
				if(title.headsuffix != null) team.setSuffix(ChatStyler.setTotalStyle(title.headsuffix));
			}
		}	
	}
	
	/**
	 * Clean up unused teams
	 * @param force Remove all teams, even if there are still players in it.
	 */
	public void cleanUpTeams(boolean force){
		Set<Title> titles = plugin.getTitles();
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		for(Team team : scoreboard.getTeams()){
			if(team.getName().startsWith("dt_")){
				if(team.getSize()==0 || force){
					// Nobody in team, or everything should be removed
					team.unregister();
				}else{
					// There is somebody in the team, but maybe it is not a valid team,
					// because the title could be renamed or removed
					
					// Find corresponding title
					String name = team.getName().substring(8);
					boolean found = false;
					for(Title title : titles){
						if(title.name.equals(name)) found = true;
					}
					if(!found){
						// Title is removed, but team still exists
						team.unregister();
					}
				}
			}
		}		
	}
}
