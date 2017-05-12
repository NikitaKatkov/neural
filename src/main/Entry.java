package main;

import java.util.List;

public class Entry {
    private List<Double> _value;
    private int _ID;

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
