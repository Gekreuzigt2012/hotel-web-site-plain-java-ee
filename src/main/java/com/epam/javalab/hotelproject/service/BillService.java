package com.epam.javalab.hotelproject.service;

import com.epam.javalab.hotelproject.model.Bill;

import java.util.List;

public interface BillService {
    List<Bill> findAll();
    Bill findByNumber();
    Bill createBill();
    boolean saveBill();
    boolean deleteBill();
}
