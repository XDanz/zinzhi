#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <mysql.h>
#include <unistd.h>

#include "gcf.h" 


#define MAXENTRIES 22500

void finish_with_error(MYSQL *con);

int
main (int argc, char** argv)
{
        
        char line[BUFSIZ];
        BDt *tradables[MAXENTRIES];
        BDUi *underlyings[MAXENTRIES];

        int i, j , c ,dryrun;
        i = 0, j = 0 , dryrun = 0;

        for (; i < MAXENTRIES; i++ ) {
                tradables[i] = (BDt *)calloc(MAXENTRIES, sizeof(BDt));
                underlyings[i] = (BDUi *)calloc(MAXENTRIES, sizeof(BDUi));
        }

        // Ddtprc:
        i = 0;
        j = 0;
        while ((c = getopt(argc, argv, "Ddtprc:")) != -1) {
                switch (c) {
                case 'd':
                        fprintf(stdout,"d option set , dry run! \n");
                        dryrun = 1;
                        break;
                case 'D':
                        fprintf(stdout,"D option set \n");
                        break;
                case 't':
                        fprintf(stdout,"t option set \n");
                        break;
                case 'p':
                        fprintf(stdout,"p option set \n");
                        break;
                case 'r':
                        fprintf(stdout,"p option set \n");
                        break;
                        /* case 'c': */
                /*         if ((p = strchr(optarg, '/')) != NULL) */
                /*                 *p++ = '\0'; */
                /*         else */
                /*                 p = confd_port; */
                /*         if (getaddrinfo(optarg, p, &hints, &addr) != 0) { */
                /*                 if (p != confd_port) { */
                /*                         *--p = '/'; */
                /*                         p = "/port"; */
                /*                 } else { */
                /*                         p = ""; */
                /*                 } */
                /*                 fprintf(stderr, "%s: Invalid address%s: %s\n", */
                /*                         argv[0], p, optarg); */
                /*                 exit(1); */
                /*         } */
                /*         break; */
                default:
                        fprintf(stderr,
                                "Usage: %s [-dtpr] [-c address[/port]]\n",
                                argv[0]);
                        exit(1);
                }
        }


        while ( fgets (line, BUFSIZ, stdin) != NULL )
        {
                if ( strstr (line, "BDt"))
                        read_BDt(line, tradables[i++]);
                if ( strstr (line, "BDUi")) {
                        read_BDUi( line ,  underlyings[j++]);
                }
        }

        
        fprintf(stdout, "Total entries BDt = %d, BDUi = %d  \n", i, j);

        if (!dryrun) {
                MYSQL *con = mysql_init(NULL);
                
                if (con == NULL) 
                {
                        fprintf(stderr, "%s\n", mysql_error(con));
                        exit(1);
                }
                
                if (mysql_real_connect(con, "localhost", "root", "root", 
                                       NULL, 0, NULL, 0) == NULL) 
                {
                        fprintf(stderr, "%s\n", mysql_error(con));
                        mysql_close(con);
                        exit(1);
                }
                
                if (mysql_select_db(con, "gcf") != 0) 
                {
                        fprintf(stderr, "%s\n", mysql_error(con));
                        mysql_close(con);
                        exit(1);
                }
                i = 0;
        }
        return 0;
}

void insert_BDUi(MYSQL *con,BDUi *underlyings[] ,int size)
{
        char buf[BUFSIZ];
        int i;
        for (i = 0; i < size ; i++) {
                snprintf( buf, BUFSIZ, "INSERT INTO BDUi VALUES(%ld,'%s',%d,%hd,%d,'%s',%d,%d,'%s','%s','%s');",
                          underlyings[i]->idCode, //i 1)
                          underlyings[i]->sourceId, //Si 2)
                          underlyings[i]->sourceSystem, //s 3)
                          underlyings[i]->updateCode, //u 4)
                          underlyings[i]->underlyingId, //ULi 5)
                          underlyings[i]->underlyingExternalId,   //UEi
                          underlyings[i]->underlyingExternalType , //ULt
                          underlyings[i]->underlyingInstrumentType , //SYm
                          underlyings[i]->isin,  //ISn
                          underlyings[i]->micCode, //CUi
                          underlyings[i]->tradingCurr //CUt
                        );
                
                if ( mysql_query(con , (const char *)buf) )
                {
                        fprintf(stderr,"error: %s \n", buf);
                        finish_with_error(con);
                }
        }
}

void insert_BDt(MYSQL *con, BDt *tradables[], int size )
{

        char buf[BUFSIZ];
        int i;
        for (i = 0; i < size ; i++) {
                snprintf( buf, BUFSIZ, "INSERT INTO BDt VALUES(%ld,'%s',%d,%hd,%d,%d,'%s','%s','%s','%s','%s','%s','%s','%ld','%hd');",
                          tradables[i]->idCode, //i
                          tradables[i]->sourceId, //Si
                          tradables[i]->sourceSystem, //s
                          tradables[i]->updateCode, //u
                          tradables[i]->exchangeId, //Ex
                          tradables[i]->marketId,   //Mk
                          tradables[i]->instrumentSourceId, //Ini
                          tradables[i]->symbol, //SYm
                          tradables[i]->name,   // NAm
                          tradables[i]->abbrevName, //SNm
                          tradables[i]->isin,  //ISn
                          tradables[i]->issueCurr, //CUi
                          tradables[i]->tradingCurr, //CUt
                          tradables[i]->instrSubType, //INt
                          tradables[i]->securityType); //STy
                if ( mysql_query(con , (const char *)buf) )
                {
                        fprintf(stderr,"error: %s \n", buf);
                        finish_with_error(con);
                }
        }
}

void finish_with_error(MYSQL *con)
{
        fprintf(stderr, "%s\n", mysql_error(con));
        mysql_close(con);
        exit(1);        
}
