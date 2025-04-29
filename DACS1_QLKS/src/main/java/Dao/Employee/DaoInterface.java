package Dao.Employee;

import java.util.ArrayList;
public interface DaoInterface<T> {
    public boolean insert(T t);
    public boolean update(T t);
    public boolean delete(String id);
    public ArrayList<T> selectAll();
}
