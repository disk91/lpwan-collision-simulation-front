package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

import java.util.ArrayList;

import com.projetzz2.lpwan_colision_simulation.RandomGeneratorSimu;

public class LoRaWanModel extends RadioModel {

    // distribution of the different SF over the frame sent
    protected int[] sfDistribution = {
            20, // SF7
            20, // SF8
            20, // SF9
            20, // SF10
            10, // SF11
            10, // SF12
    };

    // time on air for a single repeat with 10 byte payload in Us
    protected long[] frameDurationBySFuS = {
            61700,      // SF7
            113200,     // SF8
            205800,     // SF9
            370700,     // SF10
            823300,     // SF11
            1482800,    // SF12
    };


    @Override
    public ArrayList<FrameModel> getFrameModel(long startUs) {
        ArrayList<FrameModel> r = new ArrayList<>();
        FrameModel previous = null;
        FrameModel head = null;

        // Calculate a SF for this frame
        int tof = (int) Math.floor(RandomGeneratorSimu.random() * 99.99); // %age to SF "type of frame"
        int sf = -1; // SF7
        while (tof >= 0 && sf < sfDistribution.length ) {
            sf++;
            tof -= sfDistribution[sf];
        }

        // 3 repeat, taking 10 bytes data with 2 seconds between repeats
        // no ack management to reduce the transmission on success
        for ( int i = 0 ; i < 3 ; i ++) {

            FrameModel fm = new FrameModel();
            // link the fragments
            if ( head == null ) head = fm;
            fm.setHead(head);
            fm.setFirst((i==0));
            fm.setNext(null);
            if ( previous != null ) previous.setNext(fm);
            // set the timings
            fm.setUsStart(startUs);
            // the duration of a 10 bytes based on SF
            fm.setUsEnd(startUs + frameDurationBySFuS[sf]);

            // 8 channels
            int channel = (int) Math.floor(RandomGeneratorSimu.random() * 7.99); // 8 values
            fm.setChannel(channel);
            // SF is used as multiple sf on same frequency does not collision
            fm.setGroup(sf);
            r.add(fm);
            previous = fm;
            startUs = startUs + frameDurationBySFuS[sf] + (2_000_000); // transmission time + 1000ms interframe
        }
        return r;
    }

    @Override
    public void collisionTest(FrameModel f1, FrameModel f2) {
        // same one, exiting
        if ( f1 == f2 ) return;
        if ( f1.isLost() && f2.isLost() ) return; // nothing more to compute

        // Not on the same frequency
        if ( f1.getChannel() != f2.getChannel() ) return;

        // no timing collision case
        if (
                f1.getUsEnd() < f2.getUsStart()
             || f1.getUsStart() > f2.getUsEnd()
        ) return;

        // Not the same SF - no collision
        if ( f1.getGroup() != f2.getGroup() ) return;

        // rest of the case we have a collision
        f1.setCollision(true);
        f2.setCollision(true);
    }

    @Override
    public boolean viabilityTest(FrameModel f) {
        FrameModel h = f.getHead();
        if ( h.isLost() ) return false; // not viable, already computed
        int col = 0;
        int frag = 0;
        do {
            if ( h.isCollision() ) col++;
            frag++;
            h = h.getNext();
        } while ( h != null );
        if ( col == frag ) {
            // all repeat collisionned
            f.markWholeFrameLostAndCollide();
            return false;
        }
        return true; // viable frame
    }
}
