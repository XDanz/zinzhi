package com.tailf.controller;

import java.net.Socket;

import com.tailf.ncs.NcsMain;


class Sock2Ncs {

    static Socket get () throws Exception {
        NcsMain ncsMain = NcsMain.getInstance();
        Socket sock = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());
        return sock;
    }

}
