package magma.util.train_server.impl;

public enum PlayModeParameters {
    BeforeKickOff("BeforeKickOff"),
    KickOff_Left("KickOff_Left"),
    KickOff_Right("KickOff_Right"),
    PlayOn("PlayOn"),
    KickIn_Left("KickIn_Left"),
    KickIn_Right("KickIn_Right"),
    CORNER_KICK_LEFT("CORNER_KICK_LEFT"),
    CORNER_KICK_RIGHT("CORNER_KICK_RIGHT"),
    GOAL_KICK_LEFT("GOAL_KICK_LEFT"),
    GOAL_KICK_RIGHT("GOAL_KICK_RIGHT"),
    OFFSIDE_LEFT("OFFSIDE_LEFT"),
    OFFSIDE_RIGHT("OFFSIDE_RIGHT"),
    GameOver("GameOver"),
    Goal_Left("Goal_Left"),
    Goal_Right("Goal_Right"),
    FREE_KICK_LEFT("FREE_KICK_LEFT"),
    FREE_KICK_RIGHT("FREE_KICK_RIGHT"),

    NONE("NONE");

    private String name;

    PlayModeParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}