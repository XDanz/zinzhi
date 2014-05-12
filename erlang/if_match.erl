-module (if_match).
-compile(export_all).

-record(node ,{
          name :: n1 | n2,
          addr,
          port
         }).

-record(nodes ,{
          local :: #node{},
          remote ::#node{}
         }).

in_ifconfig({addr, Addr}, L) ->
    %% Addr = {addr,IP}, for example {addr,{127,0,0,1}}

    case lists:zf(fun({IfName, Props}) ->
                          case lists:keysearch(addr, 1, Props) of
                              {value, {addr, Addr}} ->
                                  {true, IfName};
                              _ ->
                                  false
                          end
                  end, L) of
        [Ifname | _] ->
            {true , Ifname};
        _ ->
            false
    end.


test(A) ->
    {ok,L} = inet:getifaddrs(),
    in_ifconfig ( A, L).














