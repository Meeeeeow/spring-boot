package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="gateway_table")
public class Gateway {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="gateway_id")
    private Long gatewayId;

    @Column(name="merchant_name", nullable=false)
    private String merchantName;

    @Column(name="merchant_gateway_id", nullable=false, unique=true)
    private String merchantGatewayId;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false, referencedColumnName="vendor_id")
    private Vendor vendor;
}
