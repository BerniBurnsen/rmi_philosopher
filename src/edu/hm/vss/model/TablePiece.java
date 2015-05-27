package edu.hm.vss.model;

import edu.hm.vss.interfaces.ITablePiece;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

/**
 * Created by Joncn on 13.05.2015.
 */
public class TablePiece implements Serializable, Remote
{
    private List<Plate> plates;
    public TablePiece(List<Plate> plates)
    {
        this.plates = plates;
    }
}
