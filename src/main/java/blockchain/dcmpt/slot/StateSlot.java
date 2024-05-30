package blockchain.dcmpt.slot;

import blockchain.global.Node;

import java.io.Serializable;

public class StateSlot implements Node, Slot, Serializable {
    public String key;
    public String value;

    public StateSlot() {

    }

    public StateSlot(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String hash() {
        return null;
    }
}
