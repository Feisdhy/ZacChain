package blockchain.memory;

public class ExtensionNode extends Node {
    public String key;
    public Node child;

    public ExtensionNode() {

    }

    public ExtensionNode(String key) {
        this.key = key;
    }
}
