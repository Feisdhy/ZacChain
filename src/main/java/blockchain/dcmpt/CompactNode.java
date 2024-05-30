package blockchain.dcmpt;

import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;

import java.io.Serializable;

public class CompactNode implements Node, Serializable {
    transient public String hash;

    public Segment[] segments;

    public CompactNode(String hash) {
        segments = new Segment[16];
        this.hash = hash;
    }

    @Override
    public String hash() {
        return hash;
    }
}