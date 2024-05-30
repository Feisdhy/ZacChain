package blockchain.dcmpt;


import blockchain.dcmpt.slot.StateSlot;
import blockchain.global.Node;

import java.io.Serializable;

public class Segment implements Serializable {
    public int current, threshold;
    public StateSlot[] stateSlots;
    transient public Node trieSlot;
    public String trieSlotHash;

    public Segment() {
        current = 0;
        threshold = 1;
        stateSlots = new StateSlot[1];
    }

    public Segment(int threshold) {
        current = 0;
        this.threshold = threshold;
        stateSlots = new StateSlot[threshold];
    }

    public boolean hasSpace() {
        return current != threshold;
    }

    public void insertStateSlot(String restKey, String value) {
        stateSlots[current ++ ] = new StateSlot(restKey, value);
    }

    public String find(String restKey) {
        for (int i = 0; i < current; i ++ )
            if (restKey.equals(stateSlots[i].key)) return stateSlots[i].value;
        return null;
    }

    public int getIndex(String restKey) {
        for (int i = 0; i < current; i ++ )
            if (restKey.equals(stateSlots[i].key)) return i;
        return -1;
    }
}
