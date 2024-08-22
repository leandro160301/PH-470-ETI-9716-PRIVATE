package com.service.Balanzas.Interfaz;

public class modbus{
    private int baud;
    private int databit;
    private int stopbit;
    private int parity;
    private int slave;
    private int portselected;

    // Getters
    public  int getBaud() { return baud; }
    public int getDatabit() { return databit; }
    public int getStopbit() { return stopbit; }
    public int getParity() { return parity; }
    public int getSlave() { return slave; }
    public int getPortselected() { return portselected; }

    // Setters
    public void setBaud(int baud) {this.baud = baud; }
    public void setDatabit(int databit) { this.databit = databit; }
    public void setStopbit(int stopbit) { this.stopbit = stopbit; }
    public void setParity(int parity) { this.parity = parity; }
    public void setSlave(int slave) { this.slave = slave; }
    public void setPortselected(int portselected) { this.portselected = portselected; }
}