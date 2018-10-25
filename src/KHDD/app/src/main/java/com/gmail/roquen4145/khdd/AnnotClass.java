package com.gmail.roquen4145.khdd;

import java.io.Serializable;
import java.util.ArrayList;

public class AnnotClass implements Serializable
{
    private static final long serialVersionUID = 1209L;



    public int numPara;
    public ArrayList<ParaStruct> Paras;


    public AnnotClass()
    {
        super();
        this.numPara = 0;
        this.Paras = new ArrayList<ParaStruct>();
    }
}