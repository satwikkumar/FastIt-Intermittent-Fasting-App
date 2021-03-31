package edu.neu.madcourse.fastit;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FastingSession.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FastingSessionDao fastingSessionDao();
}
