package Dao.Employee;

import Model.Customer;

import java.util.ArrayList;

public class CustomerDao implements DaoInterface<Customer>{
    @Override
    public boolean insert(Customer customer) {
        return false;
    }

    @Override
    public boolean update(Customer customer) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public ArrayList<Customer> selectAll() {
        return null;
    }
}
