package com.hackru.ruwatch.pojos;
import com.google.gson.annotations.SerializedName;

import java.util.*;
/**
 * Created by shreyashirday on 10/11/14.
 */
public class User {


    @SerializedName("id")
    public String mId;

    @SerializedName("age")
    private int mAge;

    @SerializedName("weight")
    public int mWeight;

    @SerializedName("benchmark")
    public int mBm;

    @SerializedName("wager")
    public int mWager;

    @SerializedName("goal")
    public int mGoal;

    @SerializedName("burned")
    public  int mBurned;

    @SerializedName("donated")
    public int mDonated;

    @SerializedName("goalCompleted")
    public boolean mGoalCompleted;

    @SerializedName("start")
    public boolean mStart;

    @SerializedName("initialDate")
    public Date mInitialDate;

    @SerializedName("finalDate")
    public Date mFinalDate;



    public User() {

    }

    public User(String id, int age, int weight, int benchmark, int wager, int goal){
        this.mId = id;
        this.mAge = age;
        this.mWeight = weight;
        this.mBm = benchmark;
        this.mWager = wager;
        this.mGoal = goal;
    }

    public void setId(String i){
        mId = i;
    }

    public String getId(){
        return mId;
    }

    public  void setBurned(int bur){
        mBurned = bur;
    }

    public int getBurned(){
        return  mBurned;
    }

    public void setDonated(int don){
        mDonated = don;
    }

    public  int getDonated(){
        return  mDonated;
    }

    public void setAge(int age){
        mAge = age;
    }

    public int getAge(){
        return mAge;
    }

    public void setWeight(int weight){
        mWeight = weight;
    }

    public int getWeight(){
        return  mWeight;
    }

    public void setBenchmark(int b){
        mBm = b;
    }

    public int getBenchmark(){
        return  mBm;
    }

    public void setWager(int wager){
        mWager = wager;
    }

    public int getWager(){
        return mWager;
    }

    public void setGoal(int g){
        mGoal = g;
    }

    public int getGoal(){
        return  mGoal;
    }

    public void setGoalCompleted(boolean gC) { mGoalCompleted = gC; }

    public boolean getGoalCompleted() { return mGoalCompleted; }

    public void setStart(boolean s) { mStart = s; }

    public boolean getStart() { return mStart; }

    public void setInitialDate(Date iD) { mInitialDate = iD; }

    public Date getInitialDate() { return mInitialDate; }

    public void setFinalDate(Date iD) { mFinalDate = iD; }

    public Date setFinalDate() { return mFinalDate; }


}
