package blockchain.mpt;

import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;

import java.io.Serializable;

public class LeafNode implements Node, Serializable {
    transient public String hash;

    public String key;
    public String value;

    public LeafNode(String key, String value, String hash) {
        this.key = key;
        this.value = value;
        this.hash = hash;
    }

    @Override
    public String hash() {
        return hash;
    }
}
