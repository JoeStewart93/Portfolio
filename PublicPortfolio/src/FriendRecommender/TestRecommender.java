package FriendRecommender;
/**
 * Class to test the FriendRecommender class
 * @author Stuart Hansen
 * @version Marhc 27, 2017
 */
public class TestRecommender {
    /**
     * A simple test main
     * @param args
     */
    public static void main(String[] args) {
        FriendRecommender r = new FriendRecommender();
        r.readGraph("RomeoAndJuliet.txt");
        System.out.println(r.recommendFriendsOfFriends("Mercutio"));

        r = new FriendRecommender();
        r.readGraph("RomeoAndJuliet.txt");
        System.out.println((r.recommendByInfluence("Mercutio")));
    }
}
