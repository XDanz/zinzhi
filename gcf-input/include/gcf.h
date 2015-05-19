struct BasicDataTradable {
        long idCode;  //i 
        char sourceId[20]; //Si
        int  sourceSystem; //s
        short updateCode; //u
        int exchangeId; //Ex
        int marketId; //Mk
        char instrumentSourceId[20]; //Ini
        char symbol[32]; // SYm
        char name[80]; // NAm
        char abbrevName[32]; //SNm
        char isin[12]; //ISn

        char issueCurr[4]; //CUi
        char tradingCurr[4]; //CUt
        
        long instrSubType; //INt
        short securityType; //STy
};

struct BasicDataUnderLyingInfo {
        long idCode;  //i 
        char sourceId[20]; //Si
        int  sourceSystem; //s
        short updateCode; //u
        int underlyingId; //ULi
        char underlyingExternalId[33]; //UEi
        int underlyingExternalType; //UEt
        int underlyingInstrumentType; //ULt
        char isin[13]; //ISn
        char micCode[5]; //MIc
        char tradingCurr[4]; //CUt
};

typedef struct BasicDataUnderLyingInfo BDUi;
typedef struct BasicDataTradable BDt;


void read_BDt(char *line, BDt* tradable);
void read_BDUi(char *line, BDUi* underlying);
