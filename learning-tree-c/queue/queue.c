////
//// Created by danter on 2016-08-25.
////
#include "queue.h"

#include <stdio.h>
#include <stdlib.h>

int q_len (struct q_node *pq)
{
  int count;

  for (count = 0; pq != NULL_QUEUE; pq = pq->next)
    count++;
  return count;
}

int q_empty (struct q_node *pq)
{
  return pq == NULL_QUEUE;
}

void q_flush (struct q_node **p_hndl)
{
  while(!q_empty (*p_hndl))
    q_take (p_hndl);
}

void q_print (struct q_node *pq)
{
  printf ("Queue contains: ");

  for (;pq != NULL_QUEUE; pq = pq->next)
    printf ("%d ", pq->data);
  putchar ('\n');
}

/**
 *
 * Takes a handle adress and appends to the end of the list
 *
 * struct q_node *my_q = 0;
 * q_add(&my_q, 1);
 *
 * @param p_hndl the handle adress
 * @param val value
 */
void q_add (struct q_node **p_hndl, int val)
{
  struct q_node *p_new , *loop_hndl;

  if ((p_new = malloc (sizeof (struct q_node))))
    {
      p_new->data = val;
      p_new->next = 0;
      if (*p_hndl == 0)
        *p_hndl = p_new;
      else {
          for (loop_hndl = *p_hndl; loop_hndl->next != 0; loop_hndl = loop_hndl->next);

          loop_hndl->next = p_new;
        }
    }
}

/***
 * q_take takes a handle address and return the value at the front
 * of the queue. The node that contained the value is removed from the queue and freed.
 *
 * @param p_hndl queue
 * @return the value at the front of the queue
 */
int q_take (struct q_node **p_hndl)
{
  struct q_node *ptmp;
  int ret;

  if (*p_hndl == NULL_QUEUE) {
      puts("q_take on a empty queue");
    }

  ptmp = *p_hndl;
  *p_hndl = ptmp->next;
  ret = ptmp->data;
  free (ptmp);
  return ret;
}


