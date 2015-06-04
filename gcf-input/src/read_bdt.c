#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "gcf.h"

static int read_abbrev_name(char *tok , char *ret);
static int read_name(char *tok, char *ret);


void read_BDt(char *line, BDt* tradable)
{
        char * pch;
        pch = strtok(line,";");
        while ( pch != NULL )
        {
                /* fprintf ( stdout , "tok = \"%s\" \n", pch ); */
                if ( strstr(pch, "BDt")) {
                        pch = strtok(NULL,";");
                        continue;
                }
                else if (pch[0] == 'i' ) {
                        if (sscanf(pch,"i%ld", &tradable->idCode) != 1) {
                                fprintf(stderr, "could not read idCode \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'S' && pch[1] == 'i' ) {
                        if (sscanf(pch,"Si%s",tradable->sourceId) != 1) {
                                fprintf(stderr, "could not read sourceId \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 's') {
                        if ( sscanf(pch,"s%d",&tradable->sourceSystem) != 1 ) {
                                fprintf(stderr, "could not read sourceSystem \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'u') {
                        if ( sscanf(pch,"u%hd",&tradable->updateCode) != 1) {
                                fprintf(stderr, "could not read updateCode \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'E' && pch[1] == 'x') {
                        if ( sscanf(pch,"Ex%d",&tradable->exchangeId) != 1) {
                                fprintf(stderr, "could not read exchangeId \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'M' && pch[1] == 'k') {
                        if (sscanf(pch,"Mk%d",&tradable->marketId) != 1) {
                                fprintf(stderr, "could not read marketId , line was \"%s\"\n", pch);
                                exit (2);
                        }
                }
                else if (pch[0] == 'I' && pch[1] == 'N' && pch[2] == 'i') {
                        if (sscanf(pch,"INi%s",tradable->instrumentSourceId) != 1)  {
                                fprintf(stderr, "could not read Ini \n");
                                exit (2);
                        }
                }
                else if (pch[0] == 'S' && pch[1] == 'Y' && pch[2] == 'm') {
                        if (sscanf(pch,"SYm%s",tradable->symbol) != 1)  {
                                fprintf(stderr, "could not read SYm \n");
                                exit (2);
                        }

                }
                else if (pch[0] == 'N' && pch[1] == 'A' && pch[2] == 'm') {
                        if (read_name(pch, tradable->name) != 0) {
                                fprintf(stderr, "could not read NAm \n");
                                fprintf(stderr, "\"%s\" \n", line);
                                exit (2);
                        }

                }
                else if (pch[0] == 'S' && pch[1] == 'N' && pch[2] == 'm') {
                        if (read_abbrev_name(pch,tradable->abbrevName) != 0)  {
                                fprintf(stderr, "could not read SNm \n");
                                fprintf(stderr, "\"%s\" \n", pch);
                                exit (2);
                        }
                                        
                }
                else if (pch[0] == 'I' && pch[1] == 'S' && pch[2] == 'n') {
                        if (sscanf(pch,"ISn%s",tradable->isin) != 1)  {
                                fprintf(stderr, "could not read ISn \n");
                                exit (2);
                        }
                }
                // Issuer currency 
                else if (pch[0] == 'C' && pch[1] == 'U' && pch[2] == 'i') {
                        if (sscanf(pch,"CUi%s", tradable->issueCurr) != 1)  {
                                fprintf(stderr, "could not read CUin \n");
                                exit (2);
                        }

                }
                // Trading currency 
                else if (pch[0] == 'C' && pch[1] == 'U' && pch[2] == 't') {
                        if (sscanf(pch,"CUt%s", tradable->tradingCurr) != 1)  {
                                fprintf(stderr, "could not read CUtn \n");
                                exit (2);
                        }
                }
                // Instrument sub type 
                else if (pch[0] == 'I' && pch[1] == 'N' && pch[2] == 't') {
                        if (sscanf(pch,"INt%ld", &tradable->instrSubType) != 1)  {
                                fprintf(stderr, "could not read INtn \n");
                                exit (2);
                        }
                }
                // Security type
                else if (pch[0] == 'S' && pch[1] == 'T' && pch[2] == 'y') {
                        if (sscanf(pch,"STy%hd", &tradable->securityType) != 1)  {
                                fprintf(stderr, "could not read STyn \n");
                                exit (2);
                        }
                }
                pch = strtok(NULL,";");
        }
}

static int read_name(char *tok, char *ret)
{
        char *ptr = tok;
        if ( *ptr++ == 'N' && *ptr++ == 'A' && *ptr++ == 'm')
        {
                strcpy(ret,ptr);
                return 0;
        }
        return 1;
}

static int read_abbrev_name(char *tok , char *ret)
{

        char *ptr = tok;
        if ( *ptr++ == 'S' && *ptr++ == 'N' && *ptr++ == 'm')
        {
                strcpy(ret, ptr);
                return 0;
        }
        return 1;
}

