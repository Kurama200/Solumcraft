package com.thepastimers.UserMap;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rwijtman on 3/7/14.
 */
public class UserMapping extends Table {
    public static String table = "user_mapping";

    int id;

    private static Map<Integer,UserMapping> dataMap;

    public UserMapping() {
        id = -1;
    }

    String userName;
    int uuid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public static List<UserMapping> parseResult(ResultSet result) throws SQLException {
        List<UserMapping> ret = new ArrayList<UserMapping>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            UserMapping p = new UserMapping();

            p.setId(result.getInt("id"));
            p.setUserName(result.getString("userName"));
            p.setUuid(result.getInt("uuid"));

            ret.add(p);
        }

        return ret;
    }

    public boolean delete(Database d) {
        if (id == -1) {
            return true;
        }
        if (d == null) {
            return false;
        }
        boolean result = d.query("DELETE FROM " + table + " WHERE ID = " + id);

        if (result) {
            dataMap.remove(id);
        }

        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(userName,uuid)";
            String values = "('" + d.makeSafe(userName) + "'," + uuid + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                    }
                } catch (SQLException e) {
                    // fallback method
                    refreshCache(d,null);
                }
            }
            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("userName = '" + d.makeSafe(userName) + "'" + ", ");
            query.append("uuid = " + uuid + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string userName, int uuid");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<UserMapping> UserMappingList = (List<UserMapping>)d.select(UserMapping.class,"");

        dataMap = new HashMap<Integer, UserMapping>();

        if (UserMappingList == null) {
            return;
        }

        for (UserMapping pd : UserMappingList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static UserMapping getMappingForPlayer(Player p) {
        int id = p.getUniqueId().hashCode();
        for (Integer i : dataMap.keySet()) {
            UserMapping um = dataMap.get(i);
            if (um.getUuid() == id) {
                return um;
            }
        }
        return null;
    }

    public static UserMapping getMappingForPlayer(String p) {
        for (Integer i : dataMap.keySet()) {
            UserMapping um = dataMap.get(i);
            if (um.getUserName().equalsIgnoreCase(p)) {
                return um;
            }
        }
        return null;
    }
}
