package edu.neu.madcourse.fastit;

import java.util.Objects;

public class FbFriend implements Comparable<FbFriend> {

    private String name;
    private int score;
    private String userId;

    public FbFriend(String name, int score, String userId) {
        this.name = name;
        this.score = score;
        this.userId = userId;
    }

    public FbFriend() { }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public int compareTo(FbFriend friend) {
        return friend.userId.compareTo(this.userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FbFriend friend = (FbFriend) o;
        return userId.equals(friend.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score, userId);
    }
}
