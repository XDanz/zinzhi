//
// Created by danter on 2016-08-27.
//

#include "queue.h"
int
main (int argc, char ** arg) {
  struct q_node* my_q = 0;
  q_add (&my_q, 1);
  q_add (&my_q, 2);
  q_add (&my_q, 3);

  q_take (&my_q);
  q_print (my_q);

}