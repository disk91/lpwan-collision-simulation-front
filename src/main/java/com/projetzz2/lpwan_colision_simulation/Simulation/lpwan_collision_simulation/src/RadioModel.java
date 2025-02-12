package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

import java.util.ArrayList;

public abstract class RadioModel {

    // Create a chain of Frame splits
    public abstract ArrayList<FrameModel> getFrameModel(long startUs);

    // Test the collision between two fragments
    public abstract void collisionTest(FrameModel f1, FrameModel f2);

    // Verify if a Frame can be received after partial collision on certain part of the frame
    public abstract boolean viabilityTest(FrameModel f);

}
