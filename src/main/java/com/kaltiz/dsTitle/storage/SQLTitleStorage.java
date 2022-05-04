package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.objects.Title;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

import javax.sql.DataSource;

public class SQLTitleStorage extends TitleStorage {

	protected DataSource dataSource;
    protected DatabaseType driver;
    private HikariConfig config;
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
            	this.username = plugin.getConfig().getString("storage.database.username");
                this.password = plugin.getConfig().getString("storage.database.password");
                try
            	{
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
            	}catch (Exception e) {
            		try
                    {
                            conn.rollback();
                    }
                    catch (SQLException e1)
                    {
                            e1.printStackTrace();
                    }
                    e.printStackTrace();
            	}
            }else{
            	this.username = plugin.getConfig().getString("storage.database.username");
                this.password = plugin.getConfig().getString("storage.database.password");
            	
            	if(plugin.getConfig().getString("storage.database.autoReconnect").equalsIgnoreCase("true"))
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL") + "&autoReconnect=true";
            	else
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL");
            	config = new HikariConfig();
            	config.setJdbcUrl(this.url);
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.setMaximumPoolSize(10);
                config.setMaxLifetime(28740000);
                config.setDriverClassName(this.driver.driver);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
                try
            	{
            		this.conn = dataSource.getConnection();
                    if (conn==null) {
                        throw new SQLException("Couldn't connect to the database");
                    }
                    String qry = "CREATE TABLE IF NOT EXISTS `players` (`uuid` VARCHAR(64) NOT NULL PRIMARY KEY, `prefix` VARCHAR(32), `suffix` VARCHAR(32));";
                    Statement stmt = this.conn.createStatement();
                    stmt.execute(qry);
            	}catch (Exception e) {
            		try
                    {
                            conn.rollback();
                    }
                    catch (SQLException e1)
                    {
                            e1.printStackTrace();
                    }
                    e.printStackTrace();
            	}
            }  	
        }else {
        	plugin.getLogger().info("Database needs a type set. Possible values: H2, MYSQL, POSTGRE, SQLITE");
        }      
    }
    
    public void closeConnection() throws SQLException{
    	if(dataSource instanceof HikariDataSource) {
    		if(!((HikariDataSource) dataSource).isClosed()) {
    			((HikariDataSource) dataSource).close();
    		}
    	}else {
    		if (conn != null && !conn.isClosed()) conn.close();
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

        if (conn == null || conn.isClosed()) {
        	if(dataSource instanceof HikariDataSource) {
                this.conn = dataSource.getConnection();
        	}else {
        		conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
        	}           
        }
        // The connection could be null here (!)
        return conn;
    }

    @Override
    public void loadTitlesPlayer(OfflinePlayer target)
    {
        String prefix = plugin.getTitleManager().titlesConfig.getDefaultPrefix();
        String suffix = plugin.getTitleManager().titlesConfig.getDefaultSuffix();
        String qry = "SELECT * FROM `players` WHERE `uuid` = ?;";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(qry);
            stmt.setString(1, target.getUniqueId().toString());
            ResultSet result = stmt.executeQuery();

            if(result.next())
            {
                prefix = result.getString("prefix");
                suffix = result.getString("suffix");
            }
            stmt.close();
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
            PreparedStatement stmt = getConnection().prepareStatement(qry);
            stmt.setString(1, id.toString());
            
            ResultSet result = stmt.executeQuery();
            existing = result.next() ? result.getString("uuid") : null;
            stmt.close();
            
            if(existing != null){
            	stmt = getConnection().prepareStatement("UPDATE `players` SET `prefix` = ?, `suffix` = ? WHERE `uuid` = ?;");
            	stmt.setString(1, prefix);
            	stmt.setString(2, suffix);
            	stmt.setString(3, id.toString());
            }else{
            	stmt = getConnection().prepareStatement("INSERT INTO `players` VALUES (?, ?, ?);");
            	stmt.setString(1, id.toString());
            	stmt.setString(2, prefix);
            	stmt.setString(3, suffix);
            }
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
        	System.out.println("[dsTitles] ERROR");
        	System.out.println("[dsTitles] Could not save titles of player " + id.toString());
        	System.out.println("Reason: " + ex.getMessage());
        }
    }
}
