///
/// Created by danter on 2016-08-25.
///

#ifndef LEARNING_TREE_C_QUEUE_H
#define LEARNING_TREE_C_QUEUE_H

#define NULL_QUEUE 0

struct q_node {
    int data;
    struct q_node *next;
};

/**
 * Prints the queue to stdout
 */
void q_print(struct q_node *);

/**
 *
 * Add the int to the back of the queue
 *
 * struct q_node *my_q = 0;
 * q_add(&my_q, 1);
 *
 * @param p_hndl the handle adress
 * @param val value
 */
void q_add (struct q_node**, int);

/**
 *
 * Removed the head element from the queue and
 * returns its value.
 *
 * @param p_hndl the handle adress
 * @return val value
 */
int q_take(struct q_node**);
int q_len (struct q_node *);
int q_empty (struct q_node *);
void q_flush(struct q_node **);

#endif //LEARNING_TREE_C_QUEUE_H
