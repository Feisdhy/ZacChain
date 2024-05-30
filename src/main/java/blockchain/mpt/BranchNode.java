package blockchain.mpt;

import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;

import java.io.Serializable;

public class BranchNode implements Node, Serializable {
    transient public String hash;

    transient public Node[] children;

    public String[] childrenHash;

    public BranchNode(String hash) {
        children = new Node[16];
        childrenHash = new String[16];
        this.hash = hash;
    }

    @Override
    public String hash() {
        return hash;
    }
}
