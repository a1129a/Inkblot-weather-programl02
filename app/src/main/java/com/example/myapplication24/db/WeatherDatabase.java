package com.example.myapplication24.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication24.model.City;

/**
 * 天气应用数据库
 * 用于管理城市数据的持久化存储
 */
@Database(entities = { City.class }, version = 2, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "weather_db";
    private static WeatherDatabase instance;

    /**
     * 数据库迁移：从版本1升级到版本2
     * 添加province、city、district字段
     */
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 添加新的列，默认值为空字符串
            database.execSQL("ALTER TABLE cities ADD COLUMN province TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE cities ADD COLUMN city TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE cities ADD COLUMN district TEXT DEFAULT ''");
        }
    };

    /**
     * 获取城市数据访问对象
     */
    public abstract CityDao cityDao();

    /**
     * 获取数据库实例（单例模式）
     */
    public static synchronized WeatherDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    WeatherDatabase.class,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2) // 添加迁移策略
                    .build();
        }
        return instance;
    }
}