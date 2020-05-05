/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.weather.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Toast;

import java.util.Objects;

import de.baumann.weather.R;


public class DbAdapter_Bookmarks {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "bookmarks_DB_v03.db";
    private static final String dbTable = "bookmarks";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, bookmarks_title, bookmarks_content, UNIQUE(bookmarks_content))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
            onCreate(db);
        }
    }

    //establish connection with SQLiteDataBase
    private final Context c;
    private SQLiteDatabase sqlDb;

    public DbAdapter_Bookmarks(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String bookmarks_title, String bookmarks_content) {
        if(!isExist(bookmarks_title)) {
            sqlDb.execSQL("INSERT INTO bookmarks (bookmarks_title, bookmarks_content) VALUES('" + bookmarks_title + "','" + bookmarks_content + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String bookmarks_content){
        try {
            String query = "SELECT bookmarks_content FROM bookmarks WHERE bookmarks_content='"+bookmarks_content+"' LIMIT 1";
            @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
            return row.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //edit data
    public void update(int id,String bookmarks_title,String bookmarks_content) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET bookmarks_title='"+bookmarks_title+"', bookmarks_content='"+bookmarks_content+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData() {
        String[] columns = new String[]{"_id", "bookmarks_title", "bookmarks_content"};
        String orderBy = "bookmarks_title" + " COLLATE NOCASE ASC;";
        return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
    }
}