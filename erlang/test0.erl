-module (test0).
-compile(export_all).
%% -include ("~/dev/trunk2/lib/econfd/include/econfd.hrl").

-record(node ,{
          name :: n1 | n2,
          addr,
          port
         }).

-record(nodes ,{
          local :: #node{},
          remote ::#node{}
         }).

test()->
    {ok,CDB} = econfd_cdb:connect(),
    {ok,SESS} = econfd_cdb:new_session(CDB,1),
    IKP =  ['nodes' ,  ['http://tail-f.com/ns/ha-fw-lite' | 'ha-fw-lite']],
    {ok, [IKPN1, N1IP,{_,N1PORT}, IKPN2, N2IP,{_,N2PORT}]} 
        =   econfd_cdb:get_object(SESS, IKP),
    %% io:format("Res=~p \n",[Res]),
    %% [IKPN1, N1IP,{_,N1PORT}, IKPN2, N2IP,{_,N2PORT}] = Res
    Hosts = [{n1,{addr,N1IP},{port,N1PORT}},{n2,{addr,N2IP},{port,N2PORT}}],
    Match = [{addr,N1IP},{addr,{N2IP}}],

    io:format("HOSTS=~p \n", [Hosts]),
    {ok, L} = inet:getifaddrs(),

    case inet:getifaddrs() of
        {ok,L} ->
            %% R = get_ipv4(L,[]),
            %% Hosts=[{n1,{addr,{192,168,50,2}},{port,5757}},
            %%        {n2,{addr,{192,168,50,3}},{port,5758}}],

            [{n1,N1Addr, N1Port}, {n2, N2Addr,N2Port}] = Hosts,

            Conn = case lists:filter(fun({IfName,AddrL}) ->
                                             lists:member(N1Addr,AddrL)
                                     end, get_ipv4(L,[])) of
                       [] ->
                           N2Addr;
                       _  ->
                           N1Addr
                   end,

            io:format("Connecting =~p \n", [Conn])
    end,
    {ok,CDB} = econfd_cdb:end_session(SESS).

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















