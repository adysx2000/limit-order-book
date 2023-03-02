package ro.satsoft.test.bento.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Order {
    private long id;
    private double price;
    private char side;
    private long size;
}
