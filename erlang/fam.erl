-module (fam).
-compile(export_all).

test(Test) -> 
    R = [{"vboxnet1",[{addr,{192,168,52,1}} ]},
         {"vboxnet0",[{addr,{192,168,50,1}} ]},
         {"en1",     [{addr,{192,168,0,14}} ]},
         {"lo0",     [{addr,{127,0,0,1}}]}   
        ],

    Hosts=[{n1,{addr,{192,168,50,2}},{port,5757}},
           {n2,{addr,{192,168,50,3}},{port,5758}}],

    [{n1,N1Addr, N1Port}, {n2, N2Addr,N2Port}] = Hosts,
    io:format("N1Addr=~p , N2Addr=~p\n",[N1Addr,N2Addr]),
    %% {"vboxnet1",[{addr,{192,168,52,1}}]}
    case lists:filter(fun({IfName,AddrL}) -> 
                                     lists:member(Test,AddrL)
                             end, R) of 
        [] ->
            io:format("Conn to Test \n");
        _ ->
            io:format("Conn To My Buddy \n")
    end,
        
    true.
    
                          
