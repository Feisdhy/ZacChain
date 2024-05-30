package blockchain.mpt;

import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;

import java.io.Serializable;

public class ExtensionNode implements Node, Serializable {
    transient public String hash;

    public String key;
    transient public Node child;

    public String childHash;

    public ExtensionNode(String key, String hash) {
        this.key = key;
        this.hash = hash;
    }

    @Override
    public String hash() {
        return hash;
    }
}
