package com.dnyferguson.voteplugin.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface FindResultCallback {
    public void onQueryDone(ResultSet result) throws SQLException;
}