package com.thepastimers.Mail;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 3/29/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailData extends Table {
    public static String table = "mail";

    int id;

    public MailData() {
        id = -1;
    }

    String player;
    String message;
    String sender;
    boolean read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static List<MailData> parseResult(ResultSet result) throws SQLException {
        List<MailData> ret = new ArrayList<MailData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            MailData p = new MailData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setMessage(result.getString("message"));
            p.setSender(result.getString("sender"));
            p.setRead(result.getBoolean("read"));

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
        return d.query("DELETE FROM " + table + " WHERE ID = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,message,sender,`read`)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(message) + "','" + d.makeSafe(sender)
                    +  "'," + read + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("message = '" + d.makeSafe(message) + "', ");
            query.append("sender = '" + d.makeSafe(sender) + "', ");
            query.append("`read` = " + read + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder ret = new StringBuilder();
        ret.append(table);
        ret.append(": int id,  string player, text message, string sender, boolean read");

        return ret.toString();
    }
}
