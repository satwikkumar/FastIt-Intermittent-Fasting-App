package edu.neu.madcourse.fastit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FastingSession {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "start_time")
    public long startTime;

    @ColumnInfo(name = "end_time")
    public long endTime;

    @ColumnInfo(name = "fast_cycle")
    public int fastCycle;

    @ColumnInfo(name = "progress_image_path")
    public String progressImagePath;

    @ColumnInfo
    public float weight;

    @ColumnInfo
    public boolean hasCompletedSession;
}
