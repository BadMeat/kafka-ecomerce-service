package com.zarta.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kode", length = 20)
    private String kode;

    @Column(name = "qty")
    private int qty;

    @Column(name = "price")
    private int price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
