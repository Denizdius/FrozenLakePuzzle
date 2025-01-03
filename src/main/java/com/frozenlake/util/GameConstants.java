package com.frozenlake.util;

public final class GameConstants {
    // Cell Types
    public static final String EMPTY = "  ";
    public static final String ENTRANCE = "E ";
    public static final String CLIFF_EDGE = "CE";
    public static final String ICE_BLOCK = "IB";
    public static final String ICE_SPIKES = "IS";
    public static final String HOLE_IN_ICE = "HI";
    public static final String BRIDGE = "BR";
    public static final String CLIMBED = "CL";

    // Game Settings
    public static final int MAX_EQUIPMENT_PER_RESEARCHER = 3;
    public static final int INITIAL_SCORE = 100;
    public static final int FALL_PENALTY = 20;
    public static final int EQUIPMENT_USE_PENALTY = 5;
    public static final int EXPERIMENT_COMPLETION_BONUS = 10;

    // Success Rates
    public static final double CAMERA_SUCCESS_RATE = 0.8; // 80% success rate

    // Equipment Short Names
    public static final String TEMP_DETECTOR = "td";
    public static final String WIND_DETECTOR = "ws";
    public static final String CAMERA = "cm";
    public static final String CHISEL = "ch";
    public static final String CLIMBING = "cl";
    public static final String WOODEN_BOARD = "wb";

    private GameConstants() {
        // Prevent instantiation
    }
} 