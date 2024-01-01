package com.example.demo.model;



import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionKey implements Serializable {
	 @Column(name="card_number", nullable=false)
	    private String cardNumber;

	 @Column(name="transaction_id", nullable=false)
	 private String transactionId;
    
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        TransactionKey that = (TransactionKey) o;
//        return Objects.equals(cardNumber, that.cardNumber) &&
//               Objects.equals(transactionId, that.transactionId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(cardNumber, transactionId);
//    }
}
