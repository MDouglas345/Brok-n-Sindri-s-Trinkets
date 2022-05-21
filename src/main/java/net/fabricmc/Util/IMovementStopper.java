package net.fabricmc.Util;

public interface IMovementStopper {
    public void toggleShouldMove();

    public void setShouldMove(boolean move);
}
