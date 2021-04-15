package edu.neu.madcourse.fastit;

public class FbFriend {

    private String name;
    private int score;
    private String userId;

    public FbFriend(String name, int score) {
        this.name = name;
        this.score = score;
    }

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
}
