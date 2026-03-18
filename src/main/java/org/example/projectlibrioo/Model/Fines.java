package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Fines {
    @Id
    private int fineId;
    private String category;
    private int max_loans;
    private double rate;

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMax_loans() {
        return max_loans;
    }

    public void setMax_loans(int max_loans) {
        this.max_loans = max_loans;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
