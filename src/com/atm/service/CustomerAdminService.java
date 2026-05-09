package com.atm.service;

import com.atm.dto.CustomerCreateRequest;
import com.atm.dto.CustomerUpdateRequest;

public interface CustomerAdminService {

    int registerCustomer(CustomerCreateRequest input);
    int updateCustomer(CustomerUpdateRequest input);
    void deleteCustomer(int customerId);

}
