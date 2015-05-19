#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "gcf.h"

static int read_CUt(char *tok, char *ret);
static int read_UEi(char *tok, char *ret) ;

void read_BDUi(char *line, BDUi* underlying)
{
        char * pch;
        fprintf(stdout,"%s\n", line);
        pch = strtok(line,";");

        while ( pch != NULL )
        {
                fprintf(stdout,"tok=\"%s\"\n", pch);
                if ( strstr(pch, "BDUi")) {
                        pch = strtok(NULL,";");
                        continue;
                }
                else if (pch[0] == 'i' ) {
                        if (sscanf(pch,"i%ld", &underlying->idCode) != 1) {
                                fprintf(stderr, "could not read idCode \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'S' && pch[1] == 'i' ) {
                        if (sscanf(pch,"Si%s", underlying->sourceId) != 1) {
                                fprintf(stderr, "could not read sourceId \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 's') {
                        if ( sscanf(pch,"s%d", &underlying->sourceSystem) != 1 ) {
                                fprintf(stderr, "could not read sourceSystem \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'u') {
                        if ( sscanf(pch,"u%hd", &underlying->updateCode) != 1) {
                                fprintf(stderr, "could not read updateCode \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'U' && pch[1] == 'L' && pch[2] == 'i' ) {
                        if ( sscanf(pch,"ULi%d", &underlying->underlyingId) != 1) {
                                fprintf(stderr, "could not read ULi \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'U' && pch[1] == 'E' && pch[2] == 'i') {
                        if (read_UEi(pch, underlying->underlyingExternalId) != 0) {
                                fprintf(stderr, "could not read UEi , line was \"%s\"\n", pch);
                                exit (2);
                        }
                }
                else if (pch[0] == 'U' && pch[1] == 'E' && pch[2] == 't') {
                        if (sscanf(pch,"UEt%d", &underlying->underlyingExternalType) != 1)  {
                                fprintf(stderr, "could not read UEt \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'U' && pch[1] == 'L' && pch[2] == 't') {
                        if (sscanf(pch,"ULt%d", &underlying->underlyingInstrumentType) != 1)  {
                                fprintf(stderr, "could not read ULt \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'M' && pch[1] == 'I' && pch[2] == 'c') {
                        if (sscanf(pch,"MIc%s", &underlying->micCode) != 1)  {
                          fprintf(stderr, "could not read MIc \n");
                          exit (2);
                  }

                }
                else if (pch[0] == 'C' && pch[1] == 'U' && pch[2] == 't') {
                        if (read_CUt(pch, underlying->tradingCurr) != 0) {
                                fprintf(stderr, "could not read CUt \n");
                                fprintf(stderr, "\"%s\" \n", line);
                                exit (2);
                        }
                }
                pch = strtok(NULL,";");
        }
}

static int read_CUt(char *tok, char *ret) 
{
        char *ptr = tok;
        char *res;
        if ( *ptr++ == 'C' && *ptr++ == 'U' && *ptr++ == 't')
        {
                res = strcpy(ret,ptr);
                return 0;
        }
        return 1;
}
static int read_UEi(char *tok, char *ret) 
{
        char *ptr = tok;
        char *res;
        if ( *ptr++ == 'U' && *ptr++ == 'E' && *ptr++ == 'i')
        {
                res = strcpy(ret,ptr);
                return 0;
        }
        return 1;
}

