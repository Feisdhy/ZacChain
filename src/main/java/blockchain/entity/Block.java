package blockchain.entity;

import java.io.Serializable;
import java.util.List;

public class Block implements Serializable {
    public int n;
    public Transaction[] transactions;

    public Block(List<Transaction> list) {
        this.n = list.size();
        this.transactions = list.toArray(new Transaction[0]);
    }
}
