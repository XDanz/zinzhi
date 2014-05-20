/*
 * mm-naive.c - The fastest, least memory-efficient malloc package.
 * 
 * In this naive approach, a block is allocated by simply incrementing
 * the brk pointer.  A block is pure payload. There are no headers or
 * footers.  Blocks are never coalesced or reused. Realloc is
 * implemented directly using mm_malloc and mm_free.
 *
 * NOTE TO STUDENTS: Replace this header comment with your own header
 * comment that gives a high level description of your solution.
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "mm.h"
#include "memlib.h"

/*********************************************************
 * NOTE TO STUDENTS: Before you do anything else, please
 * provide your team information in the following struct.
 ********************************************************/
team_t team = {
    /* Team name */
  "DD",
    /* First member's full name */
  "Daniel Terranova",
    /* First member's email address */
  "daniel.terranova@gmail.com",
    /* Second member's full name (leave blank if none) */
    "Tommy Hagberg",
    /* Second member's email address (leave blank if none) */
    "thagber@kth.se"
};


/* 
 * single word (4) or double word (8) alignment 
*/

/** $begin mallocmacros */
#define WSIZE 4            /* Word size (bytes)                     */
#define DSIZE 8            /* double word size (byte)               */
#define CHUNKSIZE (1<<12)  /* Extends the heap by this amount.      */


#define MAX(x,y) ((x) > (y) ? (x) : (y))

/* Pack a size and allocated bit into word */
#define PACK(size,alloc) ((size) | (alloc))

/* read and write a word at address p  */
#define GET(p)     (*(unsigned int *)(p))
#define PUT(p,val) (*(unsigned int *)(p) = (val))

/* Read the size and allocated fields from address p */
#define GET_SIZE(p)  (GET(p) & ~0x7)
#define GET_ALLOC(p) (GET(p) & 0x1)

/* Given block ptr bp, compute address of its header and footer */
#define HDRP(bp) ((char *) (bp) - WSIZE)
#define FTRP(bp) ((char *) (bp) + GET_SIZE(HDRP(bp)) - DSIZE)

/* Given block ptr bp, compute address of next and previous block */
#define NEXT_BLKP(bp) ((char *)(bp) + GET_SIZE(((char *)(bp) - WSIZE)))
#define PREV_BLKP(bp) ((char *)(bp) - GET_SIZE(((char *)(bp) - DSIZE)))
/** $end mallocmacros */



static char *heap_listp;
static void *place(void *bp,size_t asize);
static void *find_fit(size_t asize);
static void *coalesce(void *bp);
static void *extend_heap(size_t words);

#define SIZE_T_SIZE (ALIGN(sizeof(size_t)))


/* 
 * mm_init - initialize the malloc package.
 * calls mem_sbrk() to move the brk pointer.
 */

/* $begin mminit */
int mm_init(void)
{
  if((heap_listp = mem_sbrk(4*WSIZE)) == (void *)-1)
    return -1;

  PUT(heap_listp,0);                          /* Alignement padding */
  PUT(heap_listp+(1*WSIZE), PACK(DSIZE,1));   /* prologue header     */
  PUT(heap_listp+(2*WSIZE), PACK(DSIZE,1));   /* prologue footer     */ 
  PUT(heap_listp+(3*WSIZE),PACK(0,1));        /* epilogue header     */
  heap_listp += (2*WSIZE);
  /* $end mminit */
  
  /* $begin mminit */
  /* Extends the empty heap with a free block of CHUNKSIZE bytes */
  if(extend_heap(CHUNKSIZE/WSIZE) == NULL)
    return -1;

    return 0;
}


/* 
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
/**
void *mm_malloc(size_t size)
{
    int newsize = ALIGN(size + SIZE_T_SIZE);
    void *p = mem_sbrk(newsize);
    if ((int)p < 0)
	return NULL;
    else {
        *(size_t *)p = size;
        return (void *)((char *)p + SIZE_T_SIZE);
    }
    }**/

void *mm_malloc(size_t size){

  size_t asize; /*Adjusted block size*/
  size_t extendsize;
  char *bp;

  if(size <=0)
    return NULL;

  if(size <= DSIZE)
    asize = DSIZE + OVERHEAD;
  else
    asize = DSIZE * ( (size + (OVERHEAD) + (DSIZE-1)) / DSIZE);

  if((bp = find_fit(asize)) != NULL){
    place(bp,asize);
    return bp;
  }

  extendsize = MAX(asize,CHUNKSIZE);
  if((bp = extend_heap(extendsize/WSIZE)) == NULL)
    return NULL;
  place(bp,asize);
  return bp;

}

/*
 * mm_free - Freeing a block does nothing.
 */
void mm_free(void *ptr)
{
  size_t size = GET_SIZE(HDRP(ptr));
  PUT(HDRP(ptr), PACK(size,0));
  PUT(FTRP(ptr), PACK(size,0));

  coalesce(ptr);
}

/*
 * mm_realloc - Implemented simply in terms of mm_malloc and mm_free
 */
void *mm_realloc(void *ptr, size_t size)
{
    void *oldptr = ptr;
    void *newptr;
    size_t copySize;
    
    newptr = mm_malloc(size);
    if (newptr == NULL)
      return NULL;
    copySize = *(size_t *)((char *)oldptr - SIZE_T_SIZE);
    if (size < copySize)
      copySize = size;
    memcpy(newptr, oldptr, copySize);
    mm_free(oldptr);
    return newptr;
}

static void * extend_heap(size_t words){

  char *bp;
  size_t size;
  /* Allocate an even number of words to maintain alignment */
  size = (words % 2) ? (words+1) * WSIZE : words * WSIZE;

  if((int)(bp = mem_sbrk(size)) < 0)
    return NULL;

  /*  initialize free block header/footer and the epilogue heder */
  PUT(HDRP(bp),PACK(size,0));
  PUT(FTRP(bp),PACK(size,0));
  PUT(HDRP(NEXT_BLKP(bp)),PACK(0,1));

  return coalesce(bp);
}

static void *coalesce(void *bp){

  size_t prev_alloc = GET_ALLOC(FTRP(PREV_BLKP(bp)));
  size_t next_alloc = GET_ALLOC(HDRP(NEXT_BLKP(bp)));
  size_t size = GET_SIZE(HDRP(bp));

  if(prev_alloc && next_alloc){
    return bp;

  }else if(prev_alloc && !next_alloc){  /* Case 2 .*/
    size += GET_SIZE(HDRP(NEXT_BLKP(bp)));

    PUT(HDRP(bp),            PACK(size,0));
    PUT(FTRP(NEXT_BLKP(bp)), PACK(size,0));

    return bp;

  }else if(!prev_alloc && next_alloc){ /* Case 3*/
    size += GET_SIZE(HDRP(PREV_BLKP(bp)));

    PUT(FTRP(bp),           PACK(size,0));
    PUT(HDRP(PREV_BLKP(bp)),PACK(size,0));
    return (PREV_BLKP(bp));

  }else {

    size += GET_SIZE( HDRP( PREV_BLKP(bp))) + GET_SIZE(FTRP(NEXT_BLKP(bp)));
    PUT(HDRP(PREV_BLKP(bp)),PACK(size,0));
    PUT(FTRP(NEXT_BLKP(bp)),PACK(size,0));
    return (PREV_BLKP(bp));

  }

}

static void *find_fit(size_t asize){
  void *bp;

  for(bp = heap_listp; GET_SIZE(HDRP(bp)) > 0; bp = NEXT_BLKP(bp)){
    printf("GET_ALLOC(bp)=%d,GET_SIZE(bp)=%d \n",GET_ALLOC(HDRP(bp)),GET_SIZE(HDRP(bp)));
    
    if(!GET_ALLOC(HDRP(bp)) && (asize <= GET_SIZE(HDRP(bp)) )){
      return bp;
    }
  }

}

static void *place(void *bp,size_t asize){
  
  size_t csize = GET_SIZE(HDRP(bp));

  if((csize-asize) >= (DSIZE + OVERHEAD)){
    PUT(HDRP(bp), PACK(asize,1));
    PUT(FTRP(bp), PACK(asize,1));
    bp = NEXT_BLKP(bp);
    PUT(HDRP(bp),PACK(csize-asize,0));
    PUT(FTRP(bp),PACK(csize-asize,0));
  }else{
    PUT(HDRP(bp),PACK(csize,1));
    PUT(FTRP(bp),PACK(csize,1));
  }
}

void heapcheck(){
  void *bp;
  int i;
  i= 0;
  printf("\n");
  printf("current heap_listp:0x%x\n",  heap_listp);
  printf("current heap:(0x%x: 0x%x)\n",mem_heap_lo(),  mem_heap_hi());
  printf("+------+----------+-----------+----------+-------------+-------+\n");
  printf("|index | header   | blockptr  | footer   | size(bytes) | Alloc |\n");
  printf("|------+----------+-----------+----------+-------------+-------|\n");

  for(bp = heap_listp; GET_SIZE(HDRP(bp)) != 0; bp = NEXT_BLKP(bp)){
    printf("|%5d | 0x%x | 0x%x  | 0x%x |%12d | %4d  |\n",i++,HDRP(bp),bp,FTRP(bp) ,GET_SIZE(HDRP(bp)),GET_ALLOC(HDRP(bp)) );
    if(FTRP(bp) > (char *)mem_heap_hi())
      printf("Fail! last ftr is is beyond last heap\n");
  }
  printf("bp=0x%x , HDRP(bp)=0x%x, SIZE=%d,alloc=%d \n",bp,HDRP(bp),GET_SIZE(HDRP(bp)),GET_ALLOC(HDRP(bp)) );
  //printf("New epilogue header size=%d,alloc=%d\n", 

}























