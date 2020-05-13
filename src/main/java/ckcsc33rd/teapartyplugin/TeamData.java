package ckcsc33rd.teapartyplugin;


import java.util.ArrayList;

public class TeamData {
    private String team;
    private ArrayList<String> player;
    private int score;

    public String getTeam(){
        return team;
    }
    public ArrayList<String> getPlayer(){
        return player;
    }
    public int getScore(){
        return score;
    }
    public void setTeam(String team){
        this.team= team;
    }
    public void setPlayer(ArrayList<String> player){
        this.player=player;
    }
    public void setScore(int score){
        this.score=score;
    }


}
