package xyz.ashioto.ashioto.retrofitClasses;

/**
 * Created by geek on 23/9/16.
 * For defining a gate in the GatesList.
 */

class Gate {
    public String name; // Name of the gate
    public String count; // Latest count from the particular gate
    public String last_sync;  // Last sync time from the gate

    Gate(String name, String count, String last_sync) {
        this.name = name;
        this.count = count;
        this.last_sync = last_sync;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLast_sync() {
        return last_sync;
    }

    public void setLast_sync(String last_sync) {
        this.last_sync = last_sync;
    }
}