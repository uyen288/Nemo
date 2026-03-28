package com.wildfire.nemo.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wildfire.nemo.data.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nemo_app.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id"; // Added user_id
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_MEANING_EN = "meaning_en";
    private static final String COLUMN_MEANING_VI = "meaning_vi";
    private static final String COLUMN_EXAMPLE_ES = "example_es";
    private static final String COLUMN_EXAMPLE_EN = "example_en";
    private static final String COLUMN_EXAMPLE_VI = "example_vi";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_AUDIO = "audio";
    private static final String COLUMN_TOPIC_ID = "topic_id";
    private static final String COLUMN_LEVEL = "level";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + "internal_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ID + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_WORD + " TEXT,"
                + COLUMN_MEANING_EN + " TEXT,"
                + COLUMN_MEANING_VI + " TEXT,"
                + COLUMN_EXAMPLE_ES + " TEXT,"
                + COLUMN_EXAMPLE_EN + " TEXT,"
                + COLUMN_EXAMPLE_VI + " TEXT,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_AUDIO + " TEXT,"
                + COLUMN_TOPIC_ID + " INTEGER,"
                + COLUMN_LEVEL + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
            onCreate(db);
        }
    }

    public void addFavorite(Vocabulary vocab, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, vocab.getId());
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_WORD, vocab.getWord());
        values.put(COLUMN_MEANING_EN, vocab.getMeaningEn());
        values.put(COLUMN_MEANING_VI, vocab.getMeaningVi());
        values.put(COLUMN_EXAMPLE_ES, vocab.getExampleEs());
        values.put(COLUMN_EXAMPLE_EN, vocab.getExampleEn());
        values.put(COLUMN_EXAMPLE_VI, vocab.getExampleVi());
        values.put(COLUMN_IMAGE, vocab.getImage());
        values.put(COLUMN_AUDIO, vocab.getAudio());
        values.put(COLUMN_TOPIC_ID, vocab.getTopicId());
        values.put(COLUMN_LEVEL, vocab.getLevel());

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public void removeFavorite(int vocabId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_ID + " = ? AND " + COLUMN_USER_ID + " = ?", 
                new String[]{String.valueOf(vocabId), String.valueOf(userId)});
        db.close();
    }

    public boolean isFavorite(int vocabId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{COLUMN_ID}, 
                COLUMN_ID + "=? AND " + COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(vocabId), String.valueOf(userId)}, null, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    @SuppressLint("Range")
    public List<Vocabulary> getAllFavorites(int userId) {
        List<Vocabulary> favoriteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_ID + " = " + userId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Vocabulary vocab = new Vocabulary();
                vocab.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                vocab.setWord(cursor.getString(cursor.getColumnIndex(COLUMN_WORD)));
                vocab.setMeaningEn(cursor.getString(cursor.getColumnIndex(COLUMN_MEANING_EN)));
                vocab.setMeaningVi(cursor.getString(cursor.getColumnIndex(COLUMN_MEANING_VI)));
                vocab.setExampleEs(cursor.getString(cursor.getColumnIndex(COLUMN_EXAMPLE_ES)));
                vocab.setExampleEn(cursor.getString(cursor.getColumnIndex(COLUMN_EXAMPLE_EN)));
                vocab.setExampleVi(cursor.getString(cursor.getColumnIndex(COLUMN_EXAMPLE_VI)));
                vocab.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                vocab.setAudio(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO)));
                vocab.setTopicId(cursor.getInt(cursor.getColumnIndex(COLUMN_TOPIC_ID)));
                vocab.setLevel(cursor.getString(cursor.getColumnIndex(COLUMN_LEVEL)));
                vocab.setFavorite(true);
                favoriteList.add(vocab);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}
