package net.hpclab.beans;

import net.hpclab.database.DataBaseManager;
import net.hpclab.entities.RegType;

public class test {

    public static void main(String args[]) {
        DataBaseManager<RegType> dbm = new DataBaseManager<RegType>();
        RegType regType = dbm.find(RegType.class, new Integer(1));
        System.out.println(regType);
    }
}
