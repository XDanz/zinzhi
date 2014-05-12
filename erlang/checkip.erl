-module (checkip).
-compile(export_all).

test({addr,Addr}) ->
    %% R = [{"vboxnet1",[{addr,{192,168,52,1}} ]},
    %%      {"vboxnet0",[{addr,{192,168,50,1}} ]},
    %%      {"en1",     [{addr,{192,168,0,14}} ]},
    %%      {"lo0",     [{addr,{127,0,0,1}}]}   
    %%     ],
    
    %% Hosts=[{n1,{addr,{192,168,50,2}},{port,5757}},
    %%        {n2,{addr,{192,168,50,3}},{port,5758}}],

    %% [{n1,N1Addr, N1Port}, {n2, N2Addr,N2Port}] = Hosts,
    %% io:format("N1Addr=~p , N2Addr=~p\n",[N1Addr,N2Addr]),
    %% {"vboxnet1",[{addr,{192,168,52,1}}]}
    %% Test = {addr,{127,0,0,1}},

    {ok,L} = inet:getifaddrs(),
    IPV4 = get_ipv4(L,[]),
    case lists:filter(
           fun({_,AddrL}) -> 
                   lists:member(Addr,AddrL)
           end, IPV4) of 
        [] ->
            false;
        _ ->
            true
    end.

    
get_ipv4([],Acc) ->
    Acc;

get_ipv4([{_IfName,Info}|T],Acc) ->
    %% {IfName,Info} = H,
    Inet4Addr = [Addr || {addr,Addr} <- Info , size(Addr) == 4],
    case Inet4Addr of
        [] ->
            get_ipv4 (T, Acc);
        _ ->
            get_ipv4 (T, [{_IfName,Inet4Addr} | Acc])
    end.

    
                          
