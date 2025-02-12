package com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src;

public class FrameModel {

    // channel is the frequency used by the frame,
    // we don't need to use frequency in Hz, it's a representation of it like 1,2..
    protected int channel;

    // In the case of Mioty we can have same channel reused by multiple group
    protected int group;

    // frame start and stop
    protected long usStart;
    protected long usEnd;

    // local (one fragment) collision
    protected boolean collision;

    // global collision
    protected boolean lost;

    // chain the framemodel
    protected FrameModel next;
    // link to head
    protected FrameModel head;
    // true when first in chain
    protected boolean first;

    // ---

    public void markWholeFrameLost() {
        FrameModel f = this.getHead();
        do {
            f.setCollision(true);
            f.setLost(true);
            f = f.getNext();
        } while ( f != null );
    }


    // ---


    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getUsStart() {
        return usStart;
    }

    public void setUsStart(long usStart) {
        this.usStart = usStart;
    }

    public long getUsEnd() {
        return usEnd;
    }

    public void setUsEnd(long usEnd) {
        this.usEnd = usEnd;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public FrameModel getNext() {
        return next;
    }

    public void setNext(FrameModel next) {
        this.next = next;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public FrameModel getHead() {
        return head;
    }

    public void setHead(FrameModel head) {
        this.head = head;
    }
}
