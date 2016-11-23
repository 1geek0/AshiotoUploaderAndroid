package xyz.ashioto.ashioto.retrofitClasses;

/**
 * Created by geek on 29/9/16.
 * For defining a gate in the GatesList.
 */

class GatesListGate {
    public String name;

    GatesListGate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
