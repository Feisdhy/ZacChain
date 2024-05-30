package blockchain.entity;

import java.io.Serializable;

public class Transaction implements Serializable {
    public String from;
    public String to;

    public Transaction(String from, String to) {
        this.from = from;
        this.to = to;
    }
}
