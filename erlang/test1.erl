-module (test1).
-compile(export_all).
%% -include ("~/dev/trunk2/lib/econfd/include/econfd.hrl").

-record(node ,{
          name :: n1 | n2,
          addr,
          port = 55555
         }).

-record(nodes ,{
          local :: #node{},
          remote ::#node{}
         }).


is_in_iflist(IP) ->
    Addr = {addr,IP},
    {ok, L} = inet:getifaddrs(),
    TList = 
        lists:keymap(fun(Prop) ->
                             lists:filter(fun({Prop0,SL}) ->
                                                  case Prop0 of 
                                                      addr when 
                                                            size(SL) == 4 ->
                                                          true;
                                                      _ ->
                                                          false
                                                  end
                                          end,
                                          Prop)
                     end,2,L),
    TTList = 
        lists:filter(fun({IfName,SL}) -> 
                             case SL of [] -> false;
                                 _ -> true
                             end
                     end, 
                     TList),
    
    lists:keymember([Addr],2,TTList).
















