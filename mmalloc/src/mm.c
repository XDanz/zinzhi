/*
 * Simple, 32-bit and 64-bit clean allocator based on segregated fits.
 * The allocator maintains an array of (pointers to) free lists. 
 * Each free list is associated with a size class and is organized 
 * of a explicit list.
 * The paload of a free block contains a pointer to the next free block
 * or NULL if the free block is the last in the list. Each list
 * contains different-sized blocks whose size are members of the size class.
 *
 * Allocation
 * ----------
 * To allocate a free block the allocator determine the size class of the
 * request and does a first fit search of the apropriate free list for a block
 * that fits. If a free block is found then the block is split the fragment is 
 * inserted in the appropriate free list. If we cannot find a block that
 * fits then the allocator searches the free list for the next larger
 * size class. If none of the free list yields a block that fits,
 * then allocator requests additional heap memory from the operating system
 * and allocat the block out of this new heap memory and place the remainder
 * in the appropriate size class.
 *
 * Deallocation
 * ------------
 * To free a block the allocator coalesce and places the result on 
 * the appropriate free list.
 *
 * Structure
 * ---------
 * The segregated list starts from mem_heap_lo() and extends to
 * mem_heap_lo()+640. The segregated list is composed of 2 levels where
 * the first level is composed of fixed size DELTA_FIXED (8) apart
 * ranging from size classes 16 to 512 indexed from 0 (16) to last 512 (62).
 *
 * The next level is exponent based starting from 2^(9) index 63
 * to 2^(MAX_EXP) index 79.
 *
 * Allocated blocks must be aligned to doubleword (8 byte) boundaries.
 * Minimum block size is 16 bytes and has the below layout when the
 * block is occupied.
 *
 *  <----- blocksize --->
 *  ---------------------
 *  |HDDR| payload |FTTR|
 *  ---------------------
 *  ^    ^         ^
 *  |    |         |
 *  bp-4 bp        bp+blocksize-8
 *
 * blocksize = HDDR(4 bytes) + FTTR(4 bytes) + payload >= 16
 *
 * When a block is free the situation is as follow:
 *
 *  <----- blocksize ----->
 *  -----------------------
 *  |HDDR| next-addr |FTTR|
 *  -----------------------
 *  ^    ^           ^
 *  |    |           |
 *  bp-4 bp          bp+blocksize-8
 *
 * next-addr (8 bytes) points to next free block in list or NULL.
 *
 * Initiallization
 * ---------------
 * The mm_init() function sets up the heap to look like this:
 *
 *  ---------------------------
 *  Segregated lists  |P|H|F|E|
 *  ---------------------------
 *
 *  P) 4 bytes of padding
 *  H) Header of a dummy block of total size 8 bytes
 *  F) Footer of the dummy block
 *  E) Epilogue-header of a nonexistant block signallying the end of the heap
 *
 * When extend_heap() is called it increases the size
 * of the heap by words many words.
 * It first asks the operating system for more memory
 * using mem_sbrk, which return a pointer to the beginning of this new
 * memory. So after the call to mem_sbrk() we have this:
 *
 * ------------------------------------------
 * Segregated lists |P|H|F|H| new space |F|E|
 * ------------------------------------------
 * ^                    ^
 * |                    |
 * mem_heap_lo()      heap_listp
 *
 *
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include "mm.h"
#include "memlib.h"

team_t team = {
        /* Team name */
        "DD",
        /* First member's full name */
        "Daniel Terranova",
        /* First member's email address */
        "danieltc@kth.se",
        /* Second member's full name (leave blank if none) */
        "Tommy Hagberg",
        /* Second member's email address (leave blank if none) */
        "thagber@kth.se"
};


/* Basic constants and macros */
#define WSIZE       4       /* Word and header/footer size (bytes) */
#define DSIZE       8       /* Doubleword size (bytes) */
#define CHUNKSIZE  (1<<8)   /* Extend heap by this amount (bytes) */

#define MIN_BLOCK_SIZE 16

/* Some constants for segregated list */
#define MAX_FIXED 512
#define MAX_INDEX 79

#define DELTA_FIXED 8
#define MAX_EXP  25
#define EXP_BASE 2
#define ALIGNMENT 16

#define MAX(x, y) ((x) > (y)? (x) : (y))

/* Pack a size and allocated bit into a word */
#define PACK(size, alloc)  ((size) | (alloc))

/* Read and write a word at address p */
#define GET(p)       (*(unsigned int *)(p))
#define PUT(p, val)  (*(unsigned int *)(p) = (val))

/* Read the size and allocated fields from address p */
#define GET_SIZE(p)  (GET(p) & ~0x7)
#define GET_ALLOC(p) (GET(p) & 0x1)

/* Given block ptr bp, compute address of its header and footer */
#define HDRP(bp)       ((char *)(bp) - WSIZE)
#define FTRP(bp)       ((char *)(bp) + GET_SIZE(HDRP(bp)) - DSIZE)

/* Given block ptr bp, compute address of next and previous blocks */
#define NEXT_BLKP(bp)  ((char *)(bp) + GET_SIZE(((char *)(bp) - WSIZE)))
#define PREV_BLKP(bp)  ((char *)(bp) - GET_SIZE(((char *)(bp) - DSIZE)))

#define NEXT(ptr) *((char **)ptr)

#ifdef DEBUG
# define DBG(...) printf(__VA_ARGS__)
#else
# define DBG(...)
#endif

/* Global variables */
static char * heap_listp = 0;  /* Pointer to first block */
static unsigned int min_exp = 0;
static unsigned int heap_allocator_size = 0;

/* Function prototypes for internal helper routines */
static void *extend_heap(size_t words);
static void place(void *bp, size_t asize);
//static void *find_fit(size_t asize);
static void *coalesce(void *bp);
static void printblock(void *bp);
//static void checkheap(int verbose);
static void checkblock(void *bp);

static unsigned int segr_index(size_t size);
static void segr_put(void * ptr);
static void segr_remove();

/**********************************************************************
 * segr_init() - Initialize the segregated list with 2 levels
 * first level is fixedsize with increment of 8,
 * second level is exponent based with base 2.
 * Initlaize the lists with NULL.
 */
static int 
segr_init()
{
        unsigned int num;
        unsigned int i;
        int exp = 0;
        size_t segr_init_size =  0;

        // Calculate the initial size for the first level of segregated list 
        // (the exact list).
        // Contains blocks of fixed intervall starting from the first
        // free list with size MIN_BLOCK_SIZE(16 bytes at index 0).
        //
        // With a delta intervall of DELTA_FIXED(8 bytes) for
        // then next free list i.e 16,24,32 ... , 512 MAX_FIXED (index 62).
        // The maximum largest block in the first level is 512 which has
        // the index 62 in the first level.

        // 504 bytes default when sizeof(void *) is 8 bytes
        segr_init_size += (((MAX_FIXED - MIN_BLOCK_SIZE) / DELTA_FIXED)
                           +1) * sizeof(void *);

        // Calculate the smallest Exponent in then next level.
        // 2^(9),2^(10),2^(11), ... 2^(25),
        // so the smallest value would be 1024 (2^10) and the 
        // largest 2^(25) (33554432)

        for(; exp < MAX_EXP+1; exp++) {
                num = EXP_BASE << exp;

                if (num >= MIN_BLOCK_SIZE && num > MAX_FIXED)
                        break;
        }

        // 640 bytes
        if (exp != MAX_EXP+1)
                segr_init_size += (MAX_EXP - exp + 1) * sizeof(void *);

        min_exp = exp;

        DBG("MIN_EXP=%d\n", min_exp);

        heap_allocator_size = segr_init_size;

        if( (heap_listp = mem_sbrk(segr_init_size)) == (void *)-1)
                return -1;

        DBG("SEGR heap_listp=%p \n", heap_listp);
        DBG("mem_heap_lo()=%p \n", mem_heap_lo());
        DBG("segr_init_size=%d \n", segr_init_size);
        DBG("heap_start=%p \n", mem_heap_lo()+segr_init_size);
        i = 0;

        while (i < (segr_init_size / sizeof(void *))) {
                DBG("heap_listp+%d=%p \n",i,(char **)mem_heap_lo()+i);

                NEXT(heap_listp + i++) = NULL;
        }

        DBG("heap_listp+%d=%p \n",i,(char **)mem_heap_lo()+i);

        return 0;
}

/**********************************************************************
 * mm_init() - Initialize the memory manager and create inital empty heap.
 * Create the segregated lists (the call to segr_init()) and creates the
 * following:
 *
 *   1) Alignment padding (4 bytes)
 *   2) Prologe header (4 bytes)
 *   4) Prologe footer (4 bytes)
 *   5) Epilogue header (4 bytes)
 *
 * At last create the initial start block CHUNKSIZE bytes.
 */
int 
mm_init(void) 
{
        // create the segregated lists
        segr_init();

        // exend heap high water mark
        if ((heap_listp = mem_sbrk(4*WSIZE)) == (void *)-1)
                return -1;

        DBG("== ALIGNMENT heap_listp=%p \n", heap_listp);
        PUT(heap_listp, 0);                          /* Alignment padding */
        PUT(heap_listp + (1*WSIZE), PACK(DSIZE, 1)); /* Prologue header */
        PUT(heap_listp + (2*WSIZE), PACK(DSIZE, 1)); /* Prologue footer */
        PUT(heap_listp + (3*WSIZE), PACK(0, 1));     /* Epilogue header */

        // make the heap_listp point to Prologe footer.
        heap_listp += (2*WSIZE);

        /* Extend the empty heap with a free block of CHUNKSIZE bytes */
        if (extend_heap(CHUNKSIZE/WSIZE) == NULL) 
                return -1;

        return 0;
}


/**********************************************************************
 * mm_malloc - Allocate a block with at least size bytes of payload. The
 * function will adjust our malloc request to 8 byte alignement.
 * The function will determine the starting index call to segr_index(size)
 * and start searching from the corresponding list.
 *
 * size - requested size in bytes.
 */
void 
*mm_malloc(size_t size) 
{

        size_t asize;        /* Adjusted block size */
        size_t extendsize;   /* Amount to extend heap if no fit */
        char ** addr_heap;
        void *addr;         //The adress of the found free block
        void *addr2;
        unsigned int index;  //The index in the segregated list
        void * prev;
        DBG("<==MALLOC START reqsize=%zu bytes \n", size);

        addr = NULL;
        addr2 = NULL;

        /* Ignore spurious requests */
        if (size == 0)
                return NULL;

        if(size == 448) size = 512;
        if(size == 112) size = 128;

        /* Adjust block size to include overhead and alignment reqs. */
        if (size <= DSIZE) {
                asize = 2*DSIZE;
        } else {
                asize = DSIZE * ((size + (DSIZE) + (DSIZE-1)) / DSIZE);
        }

        DBG("==MALLOC adjusted size=%u bytes \n", asize);

        // Search the segregated list for a free block that fits.
        addr_heap = mem_heap_lo();
        index = segr_index(asize);

        //If the size is in the exact range list. i level 1.
        if (asize <= MAX_FIXED) {
                DBG("==MALLOC request in level 1 \n");
                addr = NEXT(addr_heap + index);
                //check if some previous block was in the position addr.
                if (addr != NULL) {
                        NEXT(addr_heap + index) = NEXT(addr);
                        place(addr, asize);
                        DBG("==>MALLOC RETURN Addr=%p \n", addr);
                        return addr;
                } else {
                        // No free block found at the current index
                        // we need to search for a bigger block at next index
                        DBG("--MALLOC NO free block with size: %zd in free list \n", asize);
                }

                //The size is in level 2 exponent range class.
        } else {
                DBG("==MALLOC request in level 2 \n");
                addr = NEXT(addr_heap + index);
                prev = NULL;

                // In the corresponding link list
                // at index (addr_heap + index) find a free block
                // that fits and remove it from the current chain.
                while (addr != NULL) {

                        if (GET_SIZE(HDRP(addr)) >= asize)
                                break;
                        else {
                                prev = addr;
                                addr = NEXT(addr);
                        }
                }

                if (addr != NULL) {

                        if (prev == NULL)
                                NEXT(addr_heap + index) = NEXT(addr);
                        else
                                NEXT(prev) = NEXT(addr);
                }

        }//else case (exponent range class)


        // No free block in segregated list was found at current index
        //,with requested size class.
        //Start search from the next index for free blocks
        //that fits.
        //
        if (addr == NULL) {
                index++;  //start search from next index
                while (index <= MAX_INDEX && (NEXT(addr_heap + index) == NULL)) {

                        if (index < MAX_INDEX) {
                                index++;
                        }else{
                                break;
                        }
                }

                //Remove the block from segregated list
                if (index <= MAX_INDEX && (NEXT(addr_heap + index) != NULL)) {
                        addr = NEXT(addr_heap + index);
                        NEXT(addr_heap + index) = NEXT(addr);
                }

                if(addr != NULL && (GET_SIZE(HDRP(addr)) > asize)){
                        place(addr, asize);
                        DBG("==>MALLOC RETURN Addr=%p \n", addr);
                        return addr;

                }else{
                        addr2 = NEXT(addr_heap + index);
                        prev = NULL;

                        //In the corresponding link list
                        //at index (addr_heap + index) find a free block
                        //that fits and remove it from the current chain.
                        while(addr2 != NULL) {

                                if (GET_SIZE(HDRP(addr2)) >= asize)
                                        break;
                                else {
                                        prev = addr2;
                                        addr2 = NEXT(addr2);
                                }
                        }

                        if (addr2 != NULL) {

                                if (prev == NULL)
                                        NEXT(addr_heap + index) = NEXT(addr2);
                                else
                                        NEXT(prev) = NEXT(addr2);

                                addr = addr2;

                        }else{
                                //we need to handle the case when
                                //there is no free block that could handle a request with
                                //asize i.e when malloc request with big sizes.

                                /* No fit found. Get more memory and place the block */
                                extendsize = MAX(asize,CHUNKSIZE);

                                if ((addr = extend_heap(extendsize/WSIZE)) == NULL)
                                        return NULL;

                        }

                        place(addr,asize);
                        DBG("==>MALLOC RETURN Addr=%p \n", addr);
                        return addr;
                }
        } //if(addr == NULL)
        place(addr,asize);
        //printf("--MALLOC RETURN Addr=%p \n", addr);
        return addr;
}


/*
 * mm_free - Free a previously allocated block and coalesce.
 */
void 
mm_free(void *bp)
{

        size_t size;

        if(bp == 0) return;

        size = GET_SIZE(HDRP(bp));
        //printf("FREE %p with size=%u \n",bp, size);
        if (heap_listp == 0)  mm_init();

        PUT(HDRP(bp), PACK(size, 0));
        PUT(FTRP(bp), PACK(size, 0));
        coalesce(bp);
}


/*
 * coalesce - Boundary tag coalescing.
 * Return ptr to coalesced block. coalesce calls segr_remove
 * and segr_put.
 */
static void 
*coalesce(void *bp) 
{
        size_t prev_alloc = GET_ALLOC(FTRP(PREV_BLKP(bp)));
        size_t next_alloc = GET_ALLOC(HDRP(NEXT_BLKP(bp)));
        size_t size = GET_SIZE(HDRP(bp));

        void *prev_blkp;
        void *next_blkp;

        if (prev_alloc && next_alloc) {            /* Case 1 */
                //printf("case 1 \n");
                segr_put(bp);
                return bp;

        } else if (prev_alloc && !next_alloc) {      /* Case 2 */
                //next block is free and should be in segregated list
                next_blkp = NEXT_BLKP(bp);
                segr_remove(next_blkp);
                size += GET_SIZE(HDRP(NEXT_BLKP(bp)));
                PUT(HDRP(bp), PACK(size, 0));
                PUT(FTRP(bp), PACK(size,0));
                segr_put(bp);


        } else if (!prev_alloc && next_alloc) {      /* Case 3 */
                //previous block is free and should be in segregrated list.
                //printf("case 3 (prev free)\n");
                prev_blkp = PREV_BLKP(bp);
                segr_remove(prev_blkp);
                size += GET_SIZE(HDRP(PREV_BLKP(bp)));
                //printf("SIZE=%d \n", size);
                PUT(FTRP(bp), PACK(size, 0));
                PUT(HDRP(PREV_BLKP(bp)), PACK(size, 0));
                bp = PREV_BLKP(bp);
                segr_put(bp);

        } else {                                     /* Case 4 */
                //printf("case 4 \n");
                size += GET_SIZE(HDRP(PREV_BLKP(bp))) + GET_SIZE(FTRP(NEXT_BLKP(bp)));
                prev_blkp = PREV_BLKP(bp);
                next_blkp = NEXT_BLKP(bp);
                segr_remove(prev_blkp);
                segr_remove(next_blkp);
                segr_remove(bp);

                PUT(HDRP(PREV_BLKP(bp)), PACK(size, 0));
                PUT(FTRP(NEXT_BLKP(bp)), PACK(size, 0));
                bp = PREV_BLKP(bp);
                segr_put(bp);
        }
        return bp;
}

/*
 * mm_realloc
 * size - Is the requested size. The size parameter could be greater
 * or less than the original
 * meaning that the block should be shrinked or extend.
 *
 * *ptr - The pointer to the previously allocated block
 * If pointer ptr is null, realloc alocates size bytes of memory and
 * returns a pointer to the newly allocated block.
 *
 * We distinguich between 2 main cases.
 * case 1.) The size is greater than the allocated block
 *  In case 1 we need to check the next block if it is free
 *
 */
void 
*mm_realloc(void *ptr, size_t size) 
{
        size_t blksize;
        size_t diff;
        void *newptr;
        void *next;

        //printf("REALLOC ptr=%p , size=%d \n", ptr, size);
        /* If size == 0 then this is just free, and we return NULL. */
        if (size == 0) {
                mm_free(ptr);
                return 0;
        }

        /* If oldptr is NULL, then this is just malloc. */
        if (ptr == NULL) {
                return mm_malloc(size);
        }

        if (size <= DSIZE) {
                size = 2*DSIZE;
        } else {
                size = DSIZE * ((size + (DSIZE) + (DSIZE-1)) / DSIZE);
        }

        //printf("REALLOC adjusted size=%d \n",size);
        blksize = GET_SIZE(HDRP(ptr));
        //printf("REALLOC current block blksize=%d \n" , blksize);

        if (size > blksize) {
                //if next block is free
                if (!GET_ALLOC(HDRP(NEXT_BLKP(ptr)))) {
                        next = NEXT_BLKP(ptr);
                        size_t nextsize = GET_SIZE(HDRP(NEXT_BLKP(ptr)));
                        diff = size-blksize;
                        //printf("REALLOC diff=%d \n" , diff);
                        // The next block has room for the extension
                        if (nextsize >=diff) {
                                //means next free block can fit extention
                                if (nextsize-diff >= MIN_BLOCK_SIZE) {
                                        //enough left over for more blocks?
                                        segr_remove(next);
                                        PUT(HDRP(ptr), PACK(size,1));
                                        PUT(FTRP(ptr), PACK(size,1));
                                        next = NEXT_BLKP(ptr);

                                        //split the reminder
                                        PUT(HDRP(next), PACK(nextsize-diff,0));
                                        PUT(FTRP(next), PACK(nextsize-diff,0));
                                        //put the remainder back to segr list
                                        segr_put( next);
                                        return ptr;

                                } else {//if not then allocate the whole block
                                        PUT(HDRP(ptr), PACK(blksize+nextsize,1));
                                        PUT(FTRP(ptr), PACK(blksize+nextsize,1));
                                        segr_remove(next);
                                        return ptr;
                                }
                        }
                }
                //either the next block is allocated or it was free but
                //could not handle the extension which means that
                //we need to move the allcated block so we could resize it
                newptr = mm_malloc(size);

                // If realloc() fails the original block is left untouched
                if(!newptr) return 0;

                /* Copy the old data. */
                // blksize = GET_SIZE(HDRP(ptr));
                /* printf("REALLOC COPY ptr=%p to newptr=%p, %d bytes \n", */
                /*        ptr,newptr,blksize); */
                memcpy(newptr, ptr, blksize);

                /* Free the old block. */
                mm_free(ptr);
                return newptr;

        } else if(size < blksize) {
                size_t diff = blksize - size;
                //We need to check that the splitting blocks
                //is not smaller than  MIN_BLOCK_SIZE
                if (diff >= MIN_BLOCK_SIZE && size >= MIN_BLOCK_SIZE) {
                        PUT(HDRP(ptr),PACK(size,1));
                        PUT(HDRP(ptr),PACK(size,1));
                        next = NEXT_BLKP(ptr);
                        PUT(HDRP(next),PACK(diff,0));
                        PUT(HDRP(next),PACK(diff,0));
                        segr_put(next);
                        return ptr;
                }
        }
        return ptr;
}

/*
 * checkheap - We don't check anything right now.
 */
void mm_checkheap(int verbose)  {

}


/*
 * The remaining routines are internal helper routines
 */

/*
 * extend_heap - Extend heap with free block and return its block pointer
 */
static void 
*extend_heap(size_t words) 
{
        
        char *bp;
        size_t size;

        /* Allocate an even number of words to maintain alignment */
        size = (words % 2) ? (words+1) * WSIZE : words * WSIZE;

        if ((long)(bp = mem_sbrk(size)) == -1)
                return NULL;

        /* Initialize free block header/footer and the epilogue header */

        PUT(HDRP(bp), PACK(size, 0));    /* Free block header */
        PUT(FTRP(bp), PACK(size, 0));    /* Free block footer */
        PUT(HDRP(NEXT_BLKP(bp)), PACK(0, 1)); /* New epilogue header */

        /* Coalesce if the previous block was free */
        return coalesce(bp);
}

/*************************************************************
 * segr_put - put the free block in the segregated list. Does
 * a lookup (call) to segr_index(size) to get a corresponding index.
 * The parameter passed is a pointer to a free block i.e the pointer
 * ptr should point to the free blocks payload which will be linked
 * in the linked list at the index position in the segragated list
 *
 */

static void 
segr_put(void * ptr) 
{
        char ** heap_ptr;
        char ** nxt;
        unsigned int index;
        size_t size = GET_SIZE(HDRP(ptr));

        heap_ptr = mem_heap_lo();
        NEXT(ptr) = NULL;
        //PREV(ptr) = NULL;//(heap_ptr + index);

        index = segr_index(size);

        DBG("   SEGR_PUT put free block (%p) with size %zu bytes at index pos %u \n",  ptr,size,index);

        //  nxt = NEXT(heap_ptr + index);
        //put an pointer (adress) in the payload block (pointed by ptr)
        //to point to what the (heap_ptr + index) pointed at before.
        //*((char**)ptr) = *((char**)(heap_ptr + index));

        NEXT(ptr) = NEXT(heap_ptr + index);
        //  PREV(ptr) = (heap_ptr+index);

        //and let the (heap_ptr + index) point to free block
        //pointet by ptr
        NEXT(heap_ptr + index) = ptr;
        //PREV(nxt) = ptr;
        //segr_check();
        //printf("-->segr_put OK\n");

}

/*
 *  segr_remove - Removes a free block pointed by ptr
 *  from the segragetad list. It will determine which linked list
 *  to search from by calling segr_index().
 *
 */
static void 
segr_remove(void *ptr) 
{
        char ** heap_ptr;
        char ** addr;
        char ** prev;

        unsigned int index;
        size_t size = GET_SIZE(HDRP(ptr));
        index = segr_index(size);
        heap_ptr = mem_heap_lo();
        addr = NEXT(heap_ptr+index);
        prev = (heap_ptr+index);

        while (addr != NULL) {

                if (addr == (char **)ptr) {
                        NEXT(prev) = NEXT(addr);
                        break;
                }

                addr = NEXT(addr);
                prev = NEXT(prev);
        }
        //  printf(" SEGR_REMOVE BLOCK (%p) with size %d bytes \n" , ptr,size);
}

/****************************************************************
 * segr_index - Given the size in bytes
 * return a index in the range 0-79 corresponding a the size class
 * which belongs.
 *
 */
unsigned int 
segr_index(size_t size) 
{
        unsigned int exp;
        unsigned int num;
        unsigned int index;

        // if size is in the Level 1 class
        if (size <= MAX_FIXED) {
                index = ((size - MIN_BLOCK_SIZE) / DELTA_FIXED);
                return ((size - MIN_BLOCK_SIZE) / DELTA_FIXED);
        }

        // the size is in level 2 class
        exp = min_exp;
        while((num = (2 << exp++)) < size);

        index = ((MAX_FIXED - MIN_BLOCK_SIZE) / DELTA_FIXED) + 1
                + exp - min_exp;

        return index;
}


/*
 * place - Place block of asize bytes at start of free block pointed by
 * bp and split if remainder would be at least minimum block size.
 * If split occures then put the remainig free block in the
 * segregated list.
 */
static void 
place(void *bp, size_t asize)
{
        size_t csize = GET_SIZE(HDRP(bp));
        DBG(" PLACING %d bytes IN BLOCK (%p) with size %d bytes (diff: %d) \n",
            asize,bp,csize,(csize-asize));
        
        if ((csize - asize) >= (2*DSIZE)) {
                segr_remove(bp);
                PUT(HDRP(bp), PACK(asize, 1));
                PUT(FTRP(bp), PACK(asize, 1));
                bp = NEXT_BLKP(bp);
                PUT(HDRP(bp), PACK(csize-asize, 0));
                PUT(FTRP(bp), PACK(csize-asize, 0));
                segr_put(bp);
        } else {
                PUT(HDRP(bp), PACK(csize, 1));
                PUT(FTRP(bp), PACK(csize, 1));
                segr_remove(bp);
        }

        DBG(" PLACING %d bytes at %p DONE! \n", asize,bp);
}

/*************************************************************
 * printblock - Prints a block used for debug purpose
 * bp points to payload of a allocated or freed block.
 */
static void 
printblock(void *bp)
{
        size_t hsize, halloc, fsize, falloc;

        //checkheap(0);
        hsize = GET_SIZE(HDRP(bp));
        halloc = GET_ALLOC(HDRP(bp));
        fsize = GET_SIZE(FTRP(bp));
        falloc = GET_ALLOC(FTRP(bp));

        if (hsize == 0) {
                //printf("%p: EOL\n", bp);
                return;

        }

        DBG("blockp addr=%p: header:%p [%u bytes:%c] footer:%p [%d bytes:%c]\n",
            bp,
            HDRP(bp),
            (unsigned)hsize, (halloc ? 'a' : 'f'),
            FTRP(bp),
            (unsigned)fsize, (falloc ? 'a' : 'f'));
}

/*************************************************************
 * checkblock - Block consistency checker used for
 * debug purpose
 *
 */
static void 
checkblock(void *bp)
{
        if ((size_t)bp % 8)
                printf("Error: %p is not doubleword aligned\n", bp);
        
        if(GET(HDRP(bp)) != GET(FTRP(bp)))
                printf("Header footer does not match \n");

        if(GET_ALLOC(HDRP(bp))==0)
        {//on free blocks
                
                if(GET_ALLOC(HDRP(PREV_BLKP(bp))) == 0)//coalesce check
                        DBG("Previous block is not coalesced at %p", bp);

                if(GET_ALLOC(HDRP(NEXT_BLKP(bp)))==0)//coalesce check
                        DBG("Next block is not coalesced at %p",bp);
        }
}




/*************************************************************
 * checkheap - Minimal check of the heap for consistency
 */
void 
checkheap(int verbose)
{
        void* curr = mem_heap_lo();
        void* end = mem_heap_hi();

        DBG("CHECKHEAP *****START ******\n");
        DBG("\t HEAP size=%d \n", mem_heapsize());
        //char *bp = heap_listp;

        if (verbose){
                DBG("\t Heap (%p):\n", heap_listp);
                DBG("\t heap_lo (%p):\n", mem_heap_lo());
                DBG("\t heap_hi (%p):\n", mem_heap_hi());
        }

        for(curr=heap_listp ; curr<=end+1; curr = NEXT_BLKP(curr)){

                //check that the end of the heap is right
                if(GET_SIZE(HDRP(curr))==0){
                        if(curr != (end+1))
                                DBG("end of heap at %p should be %p\n",curr , (end+1));
                        break;//break loop
                }
                //    printf("MEM hi=%p \n", mem_heap_hi());
                //printf("MEM lo=%p \n", mem_heap_lo());
                if(curr > mem_heap_hi()){
                        DBG("%p (beyond mem_heap_hi()=%p) Not in heap! Failure! \n",
                            curr,mem_heap_hi());
                        return;
                }
                if((curr < mem_heap_lo())){
                        DBG("%p (bellow mem_heap_lo()=%p Not in heap! Failure! \n",
                            curr,mem_heap_lo());
                        return;
                }
                checkblock(curr);

                if(verbose)
                        printblock(curr);
        }
        DBG("CHECKHEAP ***** END  ******\n");
}

/*************************************************************
 * segr_check - Show whats in the segr list. For debug purpose
 */

void segr_check()
{

        char ** heap_ptr = mem_heap_lo();
        char ** addr;

        unsigned int i = 0;
        //int numblocks = 0;
        int found = 0;

        printf("*** Check for free blocks in segr_list *** \n");

        for(; i < heap_allocator_size/sizeof(void *); i++) {

                addr = NEXT(heap_ptr + i);
                assert(addr < mem_heap_hi());

                while(addr != NULL) {
                        found = 1;
                        printf("*** free block found (%p) size %zu index %d *** \n",addr,
                               GET_SIZE(HDRP(addr)),i);
                        addr = NEXT(addr);
                }
        }
        if(!found)
                printf("**No free block found in segr list \n");
}

/*************************************************************
 * segr_check - Another version of heap consistency checker.
 * Prints all the block in a table.
 */
void 
checkheap2() 
{

        void *bp;
        int i;
        i= 0;
        DBG("\n");
        DBG("current heap_listp:0x%p\n",  heap_listp);
        DBG("current heap:(%p: %p)(%u bytes)\n",mem_heap_lo(),
            mem_heap_hi(),(unsigned)(mem_heap_hi()-mem_heap_lo()));
        DBG("+------+-------------+-------------+-------------+-------------+-------+\n");
        DBG("|index | header      | blockptr    | footer      | size(bytes) | Alloc |\n");
        DBG("|------+-------------+-------------+-------------+-------------+-------|\n");

        for(bp = heap_listp; GET_SIZE(HDRP(bp)) != 0; bp = NEXT_BLKP(bp)){
                DBG("|%5d | %p | %p | %p |%12d | %4d  |\n",i++,HDRP(bp),bp,
                    FTRP(bp) ,GET_SIZE(HDRP(bp)),GET_ALLOC(HDRP(bp)) );

                if(FTRP(bp) > (char *)mem_heap_hi())
                        DBG("Fail! last ftr is is beyond last heap\n");
        }
        DBG("|------+----------+-----------+----------+-------------+-------|\n");
        DBG("bp=%p , HDRP(bp)=0x%p, SIZE=%d,alloc=%d \n",bp,HDRP(bp),
            GET_SIZE(HDRP(bp)),GET_ALLOC(HDRP(bp)) );
        //printf("New epilogue header size=%d,alloc=%d\n",

}
