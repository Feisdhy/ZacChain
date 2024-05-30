package blockchain.dcmpt.slot;

import blockchain.global.Node;

import java.io.Serializable;

public class TrieSlot implements Node, Slot, Serializable {
    transient public Node child;
    public String childHash;

    public TrieSlot() {

    }

    @Override
    public String hash() {
        return null;
    }
}
