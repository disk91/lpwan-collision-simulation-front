package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

import java.util.ArrayList;

public class ModelRunner {

    protected static final long RUN_DURATION_S = 20;        // duration of the test in second
    protected static final long WINDOW_START_S = 4;         // start of the analysis windows
    protected static final long WINDOW_STOP_S  = 16;        // end of the analysis windows

    protected long totalFrames = 0;
    protected long totalCollisions = 0;

    public long getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(long totalFrames) {
        this.totalFrames = totalFrames;
    }

    public long getTotalCollisions() {
        return totalCollisions;
    }

    public void setTotalCollisions(long totalCollisions) {
        this.totalCollisions = totalCollisions;
    }


    // The idea is to simulate frame emission in a 10s windows to take into consideration
    // what happen on the message fired on the 1st second. The 10 seconds is larger than the
    // usual protocol transmission time for a single frame. The objective is to identify what's
    // going after the first second on a fired frame. The Message per second will be multiplied by 10
    // experience for this on this 10s window
    public void runStep(RadioModel r, int messagesPerSecond ) {

        // store the number of messages in every millisecond during experience test
        ArrayList<FrameModel>[] everyMsFrames = new ArrayList[(int)RUN_DURATION_S*1_000];
        for ( int i = 0 ; i < everyMsFrames.length ; i++ ) {
            everyMsFrames[i] = new ArrayList<FrameModel>();
        }

        // Store all frames
        ArrayList<FrameModel> allFrames = new ArrayList<>();

        // Generate 10x more frame per second on a 10s time range
        for ( int i = 0 ; i < messagesPerSecond * 10 ; i++ ) {
            // get a starting time in us
            long start = (long)Math.floor(Math.random()*RUN_DURATION_S*1_000_000);
            // Generate the frames
            ArrayList<FrameModel> fms = r.getFrameModel(start);
            for ( FrameModel fm : fms ) {
                // only keep what happen on the seconds 1st & 9th and for every ms link the frame
                // to this slot time.
                if ( fm.getUsEnd() < WINDOW_START_S*1_000_000 || fm.getUsStart() > WINDOW_STOP_S*1_000_000 ) continue;

                for ( long t = fm.getUsStart() / 1000 ; t <= fm.getUsEnd() / 1000 && t < everyMsFrames.length ; t++ ) {
                    everyMsFrames[(int)t].add(fm);
                }
                allFrames.add(fm);
            }
        }

        // Collect collision
        for ( int i = 0 ; i < RUN_DURATION_S*1_000 ; i++ ) {
            for (int j = 0 ; j < everyMsFrames[i].size() ; j++ ) {
                FrameModel fm1 = everyMsFrames[i].get(j);
                for ( int k = j+1 ; k < everyMsFrames[i].size() ; k++ ) {
                    FrameModel fm2 = everyMsFrames[i].get(k);
                    r.collisionTest(fm1,fm2);
                }
            }
        }


        // print parallel
        /*
        for ( int i = 0 ; i < 100 ; i++) {
            int parallel = 0;
            for ( int j = 0 ; j < 10 ; j++ ) {
                parallel += everyMsFrames[i*10+j].size();
            }
            System.out.println("in slot "+i+": "+parallel);
        }
        */


        // Collisions
        int collision = 0;
        int total = 0;
        for ( FrameModel f : allFrames ) {
            // only process header frame
            if ( f.isFirst() ) {
                FrameModel fl = f;
                while ( fl.getNext() != null ) fl = fl.getNext(); // get last sample
                // only consider frames fully in the observation windows
                // for the others, not all the collision are counted.
                if(    f.getUsStart() > WINDOW_START_S*1_000_000
                    && fl.getUsEnd() < WINDOW_STOP_S*1_000_000
                ) {
                    if (f.isLost()) collision++;
                    else {
                        if (!r.viabilityTest(f)) {
                            collision++;
                        }
                    }
                    total++;
                }
            }
        }
        this.totalFrames = total;
        this.totalCollisions = collision;

        // clean memory
        for (ArrayList<FrameModel> everyMsFrame : everyMsFrames) {
            everyMsFrame.clear();
        }
        allFrames.clear();

    }


}
