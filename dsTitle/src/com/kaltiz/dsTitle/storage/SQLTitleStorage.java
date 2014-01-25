package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.Title;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.logging.Level;

public class SQLTitleStorage extends TitleStorage {

    protected DatabaseType driver;
    protected String url = "";
    protected String username = "";
    protected String password = "";

    private Connection conn = null;

    public SQLTitleStorage(DSTitle plugin, TitleManager manager) throws SQLException
    {
        super(plugin,manager);

        this.driver = DatabaseType.match(plugin.getConfig().getString("storage.database.driver"));
        this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url");
        this.username = plugin.getConfig().getString("storage.database.username");
        this.password = plugin.getConfig().getString("storage.database.password");

        if (!loadDriver()) {
            throw new SQLException("Couldn't load driver");
        }

        this.conn = getConnection();

        String qry = "CREATE TABLE IF NOT EXISTS `players` (`name` VARCHAR(16) NOT NULL PRIMARY KEY, `prefix` VARCHAR(128), `suffix` VARCHAR(128));";
        Statement stmt = this.conn.createStatement();
        stmt.execute(qry);
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

    private Connection getConnection() {
        if (conn != null) {
            // Make a dummy query to check the connection is alive.
            try {
                if (conn.isClosed()) {
                    conn = null;
                } else {
                    conn.prepareStatement("SELECT 1;").execute();
                }
            } catch (SQLException ex) {

            }
        }
        try {
            if (conn == null || conn.isClosed()) {
                conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
                return conn;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return conn;
    }

    @Override
    public void loadTitlesPlayer(OfflinePlayer target)
    {
        String prefix = "";
        String suffix = "";
        String qry = "SELECT * FROM `players` WHERE `name` = '" + target.getName() + "';";
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet result = stmt.executeQuery(qry);

            if(result.next())
            {
                plugin.getLogger().info("Hello1");
                prefix = result.getString("prefix");
                suffix = result.getString("suffix");
            }
        }
        catch (SQLException ex)
        {
            plugin.getLogger().log(Level.SEVERE,"Could not get Suffix", ex);
        }

        manager.setPlayerPrefix(prefix, target);
        manager.setPlayerSuffix(suffix, target);
    }

    @Override
    public void saveTitlesPlayer(OfflinePlayer target)
    {
        Title p = manager.getPlayerPrefix(target);
        Title s = manager.getPlayerSuffix(target);

        String prefix = p == null ? "" : p.name;
        String suffix = s == null ? "" : s.name;

        // Check if the Player has an Existing Row
        try{
            String existing;
            String qry = "SELECT `name` FROM `players` WHERE `name` = '" + target.getName() + "';";
            Statement stmt = this.conn.createStatement();
            ResultSet result = stmt.executeQuery(qry);

            existing = result.next() ? result.getString("name") : null;

            if(existing != null)
                qry = "UPDATE `players` SET `prefix` = '" + prefix + "', `suffix` = '" + suffix + "' WHERE `name` = '" + target.getName() + "';";
            else
                qry = "INSERT INTO `players` VALUES ('" + target.getName() + "','" + prefix + "','" + suffix + "');";

            stmt = this.conn.createStatement();
            stmt.execute(qry);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
