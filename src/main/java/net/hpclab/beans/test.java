package net.hpclab.beans;

import net.hpclab.services.DataBaseService;
import net.hpclab.entities.RegType;

public class test {

    public static void main(String args[]) {
        DataBaseService<RegType> dbm = new DataBaseService<RegType>();
        RegType regType = dbm.find(RegType.class, new Integer(1));
        System.out.println(regType);
    }
}
