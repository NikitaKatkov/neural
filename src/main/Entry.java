package main;

import java.util.List;

public class Entry {
    private List<Double> _value; // список признаков кластеризации - для общности, хотя в лабе нужен только 1 элемент
    private int _ID; // идентификатор чтобы различать записи - собственно, идентификатор из документа с сайта

    Entry(int ID, List<Double> value) {
        _ID = ID;
        _value = value;
    }


    public List<Double> get_value() {
        return _value;
    }

    public void set_value(List<Double> _value) {
        this._value = _value;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

}
