package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

import java.util.ArrayList;

import com.projetzz2.lpwan_colision_simulation.RandomGeneratorSimu;

public class SigfoxModel extends RadioModel {
    @Override
    public ArrayList<FrameModel> getFrameModel(long startUs) {
        ArrayList<FrameModel> r = new ArrayList<>();
        FrameModel previous = null;
        FrameModel head = null;

        // 3 Frames, taking 12 bytes data with interframe
        for ( int i = 0 ; i < 3 ; i ++) {
            // 192KHz <-> 1920 possible channels
            // the reality is a random freq calculated by the device in the 192KHz
            // the collision is detected with 300Hz margin so it's basically equivalent
            // to 1920 different channel and collision on the adjacent channels.
            int channel = (int) Math.floor(RandomGeneratorSimu.random() * 1920);
            FrameModel fm = new FrameModel();
            // link the fragments
            if ( head == null ) head = fm;
            fm.setHead(head);
            fm.setFirst((i==0));
            fm.setNext(null);
            if ( previous != null ) previous.setNext(fm);
            // set the timings
            fm.setUsStart(startUs);
            // the duration of a 12 byte transmission is 200bits @ 100bps all included
            fm.setUsEnd(startUs + 2_000_000);
            // the channel is basically 1 on 25 + 10 due to the frequency offset
            fm.setChannel(channel);
            // no group notion
            fm.setGroup(0);
            r.add(fm);
            previous = fm;
            startUs = startUs + (2_000_000 + 500_000); // transmission time + 500ms interframe
        }
        return r;
    }

    @Override
    public void collisionTest(FrameModel f1, FrameModel f2) {
        // same one, exiting
        if ( f1 == f2 ) return;
        if ( f1.isLost() && f2.isLost() ) return; // nothing more to compute

        // Not on the same frequency ( one channel margin required )
        if (    f1.getChannel() < f2.getChannel() - 1
             || f1.getChannel() > f2.getChannel() + 1
        ) return;

        // no timing collision case
        if (
                f1.getUsEnd() < f2.getUsStart()
             || f1.getUsStart() > f2.getUsEnd()
        ) return;

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
            // all repeat collisioned
            f.markWholeFrameLostAndCollide();
            return false;
        }
        return true; // viable frame
    }
}
