package edu.neu.madcourse.fastit;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FastingSessionDao {

    @Query("SELECT * FROM fastingsession")
    List<FastingSession> getAllSessions();

    @Insert
    void insertAll(FastingSession... fastingSessions);

    @Delete
    void delete(FastingSession fastingSession);
}
