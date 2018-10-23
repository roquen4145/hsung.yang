package com.gmail.roquen4145.khdd;

import java.io.Serializable;
import java.util.ArrayList;

public class AnnotClass implements Serializable
{
    private static final long serialVersionUID = 1209L;



    public int numPara;
    public int para_align;
    public int para_padding;
    public ArrayList<String> para_text;
    public ArrayList<Integer> para_pos_x;
    public ArrayList<Integer> para_text_size;

    public AnnotClass()
    {
        super();
        this.numPara = 0;
        this.para_align = 0;
        this.para_padding = 0;
        this.para_text = new ArrayList<String>();
        this.para_pos_x = new ArrayList<Integer>();
        this.para_text_size = new ArrayList<Integer>();
    }
}