package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;

import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.objects.Title;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class SQLTitleStorage extends TitleStorage {

    protected DatabaseType driver;
    protected String url = "";
    protected String username = "";
    protected String password = "";
    private DSTitle plugin;

    private Connection conn = null;

    public SQLTitleStorage(DSTitle plugin, TitleManager manager) throws SQLException
    {
        super(plugin,manager);
        this.plugin = plugin;
        this.driver = DatabaseType.match(plugin.getConfig().getString(("storage.database.driver")));
        if(this.driver!=null) {
        	if(this.driver.equals(DatabaseType.SQLITE)){
            	this.url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + plugin.getConfig().getString("storage.database.url");
            }else{
            	if(plugin.getConfig().getString("storage.database.autoReconnect").equalsIgnoreCase("true"))
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL") + "&autoReconnect=true";
            	else
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL");
            }
            this.username = plugin.getConfig().getString("storage.database.username");
            this.password = plugin.getConfig().getString("storage.database.password");

            if (!loadDriver()) {
                throw new SQLException("Couldn't load driver");
            }

            this.conn = getConnection();
            
            if (conn==null) {
                throw new SQLException("Couldn't connect to the database");
            }

            // Create table
            String qry = "CREATE TABLE IF NOT EXISTS `players` (`uuid` VARCHAR(64) NOT NULL PRIMARY KEY, `prefix` VARCHAR(32), `suffix` VARCHAR(32));";
            Statement stmt = this.conn.createStatement();
            stmt.execute(qry);
        }else {
        	plugin.getLogger().info("Database needs a type set. Possible values: H2, MYSQL, POSTGRE, SQLITE");
        }      
    }

    private boolean loadDriver()
    {
        try {
            this.getClass().getClassLoader().loadClass(this.driver.driver).newInstance();
            return true;
        } catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Connection getConnection() throws SQLException{
        /*if (conn != null) {
            // Make a dummy query to check the connection is alive.
            try {
                if (conn.isClosed()) {
                    conn = null;
                } else {
                    conn.prepareStatement("SELECT 1;").execute();
                }
            } catch (SQLException ex) {

            }
        }*/
        if (conn == null || conn.isClosed()) {
            conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
        }
        // The connection could be null here (!)
        return conn;
    }
    
    public void closeConnection() throws SQLException{
    	 if (conn != null && !conn.isClosed()) conn.close();
    }

    @Override
    public void loadTitlesPlayer(OfflinePlayer target)
    {
        String prefix = plugin.getTitleManager().titlesConfig.getDefaultPrefix();
        String suffix = plugin.getTitleManager().titlesConfig.getDefaultSuffix();
        String qry = "SELECT * FROM `players` WHERE `uuid` = ?;";
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement(qry);
            stmt.setString(1, target.getUniqueId().toString());
            ResultSet result = stmt.executeQuery();

            if(result.next())
            {
                prefix = result.getString("prefix");
                suffix = result.getString("suffix");
            }
        }
        catch (SQLException ex)
        {
            plugin.getLogger().log(Level.SEVERE,"Could not load titles of player " + target.getName());
            plugin.getLogger().log(Level.SEVERE,"Reason: " + ex.getMessage());
        }

        manager.setPlayerPrefix(prefix, target);
        manager.setPlayerSuffix(suffix, target);
    }

    @Override
    public void saveTitlesPlayer(OfflinePlayer target)
    {
        Title p = manager.getPlayerPrefix(target);
        Title s = manager.getPlayerSuffix(target);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveAsync(p, s, target.getUniqueId()));      
    }
    
    @SuppressWarnings("resource")
	private void saveAsync(Title p, Title s, UUID id) {
    	String prefix = p == null ? null : p.name;
        String suffix = s == null ? null : s.name;

        // Check if the Player has an Existing Row
        try{
            String existing;
            String qry = "SELECT `uuid` FROM `players` WHERE `uuid` = ?;";
            PreparedStatement stmt = this.conn.prepareStatement(qry);
            stmt.setString(1, id.toString());
            
            ResultSet result = stmt.executeQuery();
            existing = result.next() ? result.getString("uuid") : null;
            stmt.close();

            if(existing != null){
            	stmt = this.getConnection().prepareStatement("UPDATE `players` SET `prefix` = ?, `suffix` = ? WHERE `uuid` = ?;");
            	stmt.setString(1, prefix);
            	stmt.setString(2, suffix);
            	stmt.setString(3, id.toString());
            }else{
            	stmt = this.getConnection().prepareStatement("INSERT INTO `players` VALUES (?, ?, ?);");
            	stmt.setString(1, id.toString());
            	stmt.setString(2, prefix);
            	stmt.setString(3, suffix);
            }
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
        	plugin.getLogger().log(Level.SEVERE,"Could not save titles of player " + id.toString());
            plugin.getLogger().log(Level.SEVERE,"Reason: " + ex.getMessage());
        }
    }
}
