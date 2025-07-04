package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

import java.util.ArrayList;

import com.projetzz2.lpwan_colision_simulation.RandomGeneratorSimu;

public class MiotyModel extends RadioModel {

    /*
     * The carrier spacing step size  of the standard TSMA mode used for one 100 kHz channel shall be 2 380,371 Hz. The
     * carrier spacing accuracy shall be ±5,0 Hz across all radio-bursts within one radio-frame.
     * If two radio channels A and B are used, the frame transmissions shall be alternated between the two channels. The
     * channel to be used for transmission shall be derived from the least significant bit of the payload CRC according to
     * Table 6
     */
    public final static int SPACING_STEP = 2380; // Hz

    public static class MiotyPattern {
        int refChannel;     // reference channel
        long timeDiff;      // time diff from previous packet in ms

        public MiotyPattern(int r, long t) { this.refChannel = r; this.timeDiff = t;}
    }

    protected MiotyPattern[][] patterns=new MiotyPattern[8][24];

    private int[][] freqPattern = {{ 5, 21, 13,  6, 22, 14,  1, 17,  9,  0, 16,  8,  7, 23, 15,  4, 20, 12,  3, 19, 11,  2, 18, 10},
    { 4, 20, 12,  1, 17,  9,  0, 16,  8,  6, 22, 14,  7, 23, 15,  2, 18, 10,  5, 21, 13,  3, 19, 11},
    { 4, 20, 12,  3, 19, 11,  6, 22, 14,  7, 23, 15,  0, 16,  8,  5, 21, 13,  2, 18, 10,  1, 17,  9},
    { 6, 22, 14,  2, 18, 10,  7, 23, 15,  0, 16,  8,  1, 17,  9,  4, 20, 12,  5, 21, 13,  3, 19, 11},
    { 7, 23, 15,  4, 20, 12,  3, 19, 11,  2, 18, 10,  6, 22, 14,  0, 16,  8,  1, 17,  9,  5, 21, 13},
    { 3, 19, 11,  6, 22, 14,  2, 18, 10,  0, 16,  8,  7, 23, 15,  1, 17,  9,  4, 20, 12,  5, 21, 13},
    { 3, 19, 11,  1, 17,  9,  5, 21, 13,  7, 23, 15,  0, 16,  8,  2, 18, 10,  6, 22, 14,  4, 20, 12},
    { 0, 16,  8,  6, 22, 14,  3, 19, 11,  2, 18, 10,  4, 20, 12,  7, 23, 15,  5, 21, 13,  1, 17,  9}};

    private long [][] timePattern = {{388, 354, 356, 432, 352, 467, 620},
    {435, 409, 398, 370, 361, 472, 522},
    {356, 439, 413, 352, 485, 397, 444},
    {352, 382, 381, 365, 595, 604, 352},
    {380, 634, 360, 393, 352, 373, 490},
    {364, 375, 474, 355, 478, 464, 513},
    {472, 546, 501, 356, 359, 359, 364},
    {391, 468, 512, 543, 354, 391, 368}};

    public static final int MODE_EU0 = 0;  // 100KHz
    public static final int MODE_EU1 = 1;  // 2x100KHz
    private int mode;

public MiotyModel(int mode) {
        // just considering UPG1 (no repeat)
        for ( int i = 0 ; i < 8 ; i++ ) {
            long tot = 0;
            for ( int j = 0 ; j < 24 ; j++ ) {
                long t = 0;
                if ( j % 3 == 0 ) t = 330;
                else if ( j % 3 == 1 ) t = 387;
                else if ( j != 23 ) t = timePattern[i][j/3];
                tot += t;
                patterns[i][j] = new MiotyPattern(freqPattern[i][j],t);
            }
            System.out.println("Mioty total time on pattern "+i+" is "+tot+"ms");
        }
        this.mode = mode;
    }

    @Override
    public ArrayList<FrameModel> getFrameModel(long startUs) {
        // pattern selection and freq shift are based on CRC as a pseudo random generator
        // here we use a random value, should be equivalent
        int pattern = (int) Math.floor(RandomGeneratorSimu.random() * 7.99);          // random pattern selection
        int fOffset = (int) Math.floor(RandomGeneratorSimu.random() * 10.99) - 5;     // the best condition is +/-5 shift
        int bank =(RandomGeneratorSimu.random() > 0.5 && mode == MODE_EU1)?40:0;   // randomly select the bank for EU1 (2x100KHz)
        ArrayList<FrameModel> r = new ArrayList<>();
        long _startUs = startUs;
        FrameModel previous = null;
        FrameModel head = null;
        for ( int i = 0 ; i < 24 ; i++ ) {
            MiotyPattern p = patterns[pattern][i];
            FrameModel fm = new FrameModel();
            // link the fragments
            if ( head == null ) head = fm;
            fm.setHead(head);
            fm.setFirst((i==0));
            fm.setNext(null);
            if ( previous != null ) previous.setNext(fm);
            // set the timings
            fm.setUsStart(_startUs);
            // the duration of a split is 15.126ms (36x0.4201ms)
            fm.setUsEnd(_startUs + 15126);
            // the channel is basically 1 on 25 + 10 due to the frequency offset
            // add potentially 40 to channel for the second band in EU1 mode
            fm.setChannel(p.refChannel+fOffset+bank);
            fm.setGroup(pattern);
            r.add(fm);
            // next split will be later, based on pattern
            previous = fm;
            _startUs = _startUs + (p.timeDiff*1_000) - (15126); // timeDiff is middle to middle split
        }
        return r;
    }

    @Override
    public void collisionTest(FrameModel f1, FrameModel f2) {
        // same one, exiting
        if ( f1 == f2 ) return;
        //if ( f1.isLost() && f2.isLost() ) return; // nothing more to compute

        // Not on the same frequency
        if ( f1.getChannel() != f2.getChannel() ) return;

        // no timing collision case
        if (
                f1.getUsEnd() < f2.getUsStart()
             || f1.getUsStart() > f2.getUsEnd()
        ) return;

        // mioty pattern is the same ?
        if ( f1.getGroup() == f2.getGroup() ) {
            if ( f1.getHead().getChannel() == f2.getHead().getChannel() ) {
                // global collision happen, mark the whole frame lost
                f1.markWholeFrameLostAndCollide();
                f2.markWholeFrameLostAndCollide();
            }
        }
        
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
        if ( col >= (1 * frag)/2 ) {
            // >=2/3 burst in collision, impossible to reconstruct due to the triplication of data
            // but based on discussion with Mioty, with 50% of burst loss the concentrator will not
            // be able to detect the pattern
            f.markWholeFrameLost();
            return false;
        }
        return true; // viable frame
    }


}
