package com.hackru.ruwatch.pojos;
import com.google.gson.annotations.SerializedName;

import java.util.*;
/**
 * Created by shreyashirday on 10/11/14.
 */
public class User {


    @SerializedName("userId")
    public static String mId;

    @SerializedName("age")
    public static int mAge;

    @SerializedName("weight")
    public static int mWeight;

    @SerializedName("benchmark")
    public static int mBm;

    @SerializedName("wager")
    public static int mWager;

    @SerializedName("goal")
    public static int mGoal;



    public User(String id){
        mId = id;



    }

    public static void setAge(int age){
        mAge = age;
    }

    public static int getAge(){
        return mAge;
    }

    public static void setWeight(int weight){
        mWeight = weight;
    }

    public static int getWeight(){
        return  mWeight;
    }

    public static void setBenchmark(int b){
        mBm = b;
    }

    public static int getBenchmark(){
        return  mBm;
    }

    public static void setWager(int wager){
        mWager = wager;
    }

    public static int getWager(){
        return mWager;
    }

    public static void setGoal(int g){
        mGoal = g;
    }

    public static int getGoal(){
        return  mGoal;
    }
}
