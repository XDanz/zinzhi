
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/poll.h>

#include <confd.h>
#include <confd_maapi.h>

#include "math2.h"

/* FIXME - enum hashes are only in confspec .h (Trac #703) */
#define ncs_add m2_add
#define ncs_sub m2_sub
#define ncs_mul m2_mul
#define ncs_div m2_div
#define ncs_square m2_square

static int ctlsock, workersock;
static struct confd_daemon_ctx *dctx;

static char *expect_username = "admin";

#define XML(typeval, tagval)                                            \
    { .type = (typeval), .val = { .xmltag = { .tag = (tagval), .ns = NS }}}
#define BEGIN(tagval) XML(C_XMLBEGIN, (tagval))
#define END(tagval) XML(C_XMLEND, (tagval))
#define XTAG(tagval) XML(C_XMLTAG, (tagval))
#define INT32(valval) { .type = C_INT32, .val = { .i32 = (valval) }}
#define UINT16(valval) { .type = C_UINT16, .val = { .u16 = (valval) }}
#define INT16(valval) { .type = C_INT16, .val = { .i16 = (valval) }}
#define ENUM(valval) { .type = C_ENUM_HASH, .val = { .enumhash = (valval) }}
#define BUF(valval) { .type = C_BUF, .val = {                           \
            .buf = { .ptr = (unsigned char *)(valval),                  \
                     .size = sizeof(valval) - 1 }}}
#define LIST(valval) { .type = C_LIST, .val = {                         \
            .list = { .ptr = (valval),                  \
                      .size = sizeof(valval)/sizeof(valval[0]) }}}
#define NOEXISTS { .type = C_NOEXISTS }

#define TAG(tagval, val) { .tag = { .tag = (tagval), .ns = NS }, .v = val }


#define EXPECT_KP {                             \
        .v = {                                  \
            { BUF("fred"), NOEXISTS },          \
            { XTAG(m2_computer) },              \
            { XTAG(m2_system) },                \
            { NOEXISTS },                       \
        },                                      \
        .len = 3                                \
    }

#define EXPECT_PARAMS {                                 \
        TAG(m2_operation, BEGIN(m2_operation)),         \
        TAG(m2_number, INT32(13)),                      \
        TAG(m2_type, ENUM(m2_add)),                     \
        TAG(m2_operands, LIST(expect_list_add)),        \
        TAG(m2_operation, END(m2_operation)),           \
        TAG(m2_operation, BEGIN(m2_operation)),         \
        TAG(m2_number, INT32(1)),                       \
        TAG(m2_type, ENUM(m2_square)),                  \
        TAG(m2_operands, LIST(expect_list_square)),     \
        TAG(m2_operation, END(m2_operation)),           \
        TAG(m2_operation, BEGIN(m2_operation)),         \
        TAG(m2_number, INT32(42)),                      \
        TAG(m2_type, ENUM(m2_div)),                     \
        TAG(m2_operands, LIST(expect_list_div)),        \
        TAG(m2_operation, END(m2_operation))            \
    }

static confd_value_t expect_list_add[] = {
    INT16(13), INT16(25)
};
static confd_value_t expect_list_square[] = {
    INT16(7)
};
static confd_value_t expect_list_div[] = {
    INT16(25), INT16(4)
};

#define NS m2__ns
static struct xml_tag c_expect_name = { .tag = m2_math,
                                        .ns = NS };

static confd_hkeypath_t expect_kp = EXPECT_KP;

static confd_tag_value_t c_expect_params[] = EXPECT_PARAMS;

static confd_tag_value_t c_expect_values[] = {
        TAG(m2_result, BEGIN(m2_result)),
        TAG(m2_number, INT32(13)),
        TAG(m2_type, ENUM(m2_add)),
        TAG(m2_value, INT16(38)),
        TAG(m2_result, END(m2_result)),
        TAG(m2_result, BEGIN(m2_result)),
        TAG(m2_number, INT32(1)),
        TAG(m2_type, ENUM(m2_square)),
        TAG(m2_value, INT16(49)),
        TAG(m2_result, END(m2_result)),
        TAG(m2_result, BEGIN(m2_result)),
        TAG(m2_number, INT32(42)),
        TAG(m2_type, ENUM(m2_div)),
        TAG(m2_value, INT16(6)),
        TAG(m2_result, END(m2_result))
};

static confd_tag_value_t optparam[] = {
        TAG(m2_maybe, INT32(13))
};

#undef NS

//#define NS ncs__ns
//static struct xml_tag n_expect_name = { .tag = cs_math, .ns = NS };
//static confd_tag_value_t n_expect_params[] = EXPECT_PARAMS;

static int expect_nparams = sizeof(c_expect_params)/sizeof(c_expect_params[0]);
static int expect_nvalues = sizeof(c_expect_values)/sizeof(c_expect_values[0]);


static int get_ctlsock(struct addrinfo *addr){
    int sock;
    if ((sock =
         socket(addr->ai_family, addr->ai_socktype, addr->ai_protocol)) < 0)
        return -1;

    if (confd_connect(dctx, sock, CONTROL_SOCKET,
                      addr->ai_addr, addr->ai_addrlen) != CONFD_OK) {
        close(sock);
        return -1;
    }
    return sock;
}

static int get_workersock(struct addrinfo *addr){
    int sock;

    if ((sock =
         socket(addr->ai_family, addr->ai_socktype, addr->ai_protocol)) < 0)
        return -1;
    if (confd_connect(dctx, sock, WORKER_SOCKET,
                      addr->ai_addr, addr->ai_addrlen) != CONFD_OK) {
        close(sock);
        return -1;
    }
    return sock;
}

static int get_maapisock(struct addrinfo *addr)
{
    int sock;

    if ((sock =
         socket(addr->ai_family, addr->ai_socktype, addr->ai_protocol)) < 0)
        return -1;
    if (maapi_connect(sock,
                      addr->ai_addr, addr->ai_addrlen) != CONFD_OK) {
        close(sock);
        return -1;
    }
    return sock;
}





//init() callback
static int init_action(struct confd_user_info *uinfo){
  confd_action_set_fd(uinfo, workersock);
  return CONFD_OK;
}

static int math(struct confd_user_info *uinfo,
                struct xml_tag *name,
                struct xml_tag *expect_name,
                confd_tag_value_t *params,
                confd_tag_value_t *expect_params,
                int nparams, int ns){
  int i, j;
  int type;
  int num_ops = 0;

  assert(strcmp(uinfo->username, expect_username) == 0);
  assert(memcmp(name, expect_name, sizeof(*name)) == 0);

  /* test response handling with "magic number" */
  if ((i = CONFD_GET_INT32(CONFD_GET_TAG_VALUE(&params[1]))) <= 0) {
    switch (i) {
    case 0:
      return CONFD_OK;
    case -1:
      return CONFD_ERR;
    case -2:
      return -17;               /* bogus */
    case -3:
      confd_action_seterr(uinfo, "Number %d is invalid", i);
      return CONFD_ERR;
    case -4:
      confd_action_seterr(uinfo, "Number %d is invalid", i);
      return CONFD_OK;  /* bogus but should just ignore err msg */
    case -5: {
      /* bogus - data & error */
      confd_tag_value_t reply[5];

      j = 0;
      CONFD_SET_TAG_XMLBEGIN(&reply[j],m2_result, ns); j++;
      CONFD_SET_TAG_INT32(&reply[j], m2_number, i); j++;
      type = CONFD_GET_ENUM_HASH(CONFD_GET_TAG_VALUE(&params[2]));
      CONFD_SET_TAG_ENUM_HASH(&reply[j], m2_type, type); j++;
      CONFD_SET_TAG_INT16(&reply[j], m2_value, 14); j++;
      CONFD_SET_TAG_XMLEND(&reply[j],m2_result, ns); j++;
      confd_action_reply_values(uinfo, reply, j);
      return CONFD_ERR;
    }
    }
  }

  //assert(nparams == expect_nparams);
    for (i = 0; i < expect_nparams; i++) {
      //assert(memcmp(&params[i].tag, &expect_params[i].tag,
      //sizeof(expect_params[0].tag)) == 0);
      //assert(confd_val_eq(&params[i].v, &expect_params[i].v) > 0);

      if (params[i].tag.tag == m2_operation &&    params[i].v.type == C_XMLBEGIN)
        num_ops++;
    }

    {
        int op;
        confd_tag_value_t reply[5*num_ops];
        confd_value_t *oplist;
        int op1, op2, result;
        printf("num_ops:%d\n",num_ops);
        i = 0; j = 0;
        for (op = 0; op < num_ops; op++) {
          //                                                              m2_number(1)
          //Cmd=["request","system","computer","apa","math","operation","{","number","1",
          //"type","add","operands","[","1","2","]","}"]
          //m2_type(2)     (3)
            CONFD_SET_TAG_XMLBEGIN(&reply[j],m2_result, ns);
            j++;
            i++;/*i=1*/         /* skip operation begin */
            CONFD_SET_TAG_INT32(&reply[j], m2_number,CONFD_GET_INT32(   CONFD_GET_TAG_VALUE(&params[i])));
            i++; j++;/*i=2*/
            type = CONFD_GET_ENUM_HASH(CONFD_GET_TAG_VALUE(&params[i]));
            i++;/*i=3*/
            CONFD_SET_TAG_ENUM_HASH(&reply[j], m2_type, type);
            j++;
            oplist = CONFD_GET_LIST(CONFD_GET_TAG_VALUE(&params[i]));
            i++; /*i=4*/
            op1 = CONFD_GET_INT16(&oplist[0]);
            switch (type) {
            case m2_add:
              op2 = CONFD_GET_INT16(&oplist[1]);
              result = op1 + op2;
              break;
            case m2_sub:
              op2 = CONFD_GET_INT16(&oplist[1]);
              result = op1 + op2;
              break;
            case m2_mul:
              op2 = CONFD_GET_INT16(&oplist[1]);
              result = op1 * op2;
              break;
            case m2_div:
              op2 = CONFD_GET_INT16(&oplist[1]);
              result = op1 / op2;
              break;
            case m2_square:
              result = op1 * op1;
                break;
            }
            i++;                /* skip operation end */
            CONFD_SET_TAG_INT16(&reply[j], m2_value, result); j++;
            CONFD_SET_TAG_XMLEND(&reply[j], m2_result, ns); j++;
        }
        confd_action_reply_values(uinfo, reply, j);
    }

    return CONFD_OK;
}

/*
static int math_nc(struct confd_user_info *uinfo,
                   struct xml_tag *name,
                   confd_hkeypath_t *kp,
                   confd_tag_value_t *params,
                   int nparams)
{
    fprintf(stderr, "** ---> math_nc action called\n");
    assert(kp == NULL);
    return math(uinfo, name, &n_expect_name, params, n_expect_params,
    nparams, ncs__ns);
    }*/

 //Action callback.
static int math_cs(struct confd_user_info *uinfo,
                   struct xml_tag *name,
                   confd_hkeypath_t *kp,
                   confd_tag_value_t *params,
                   int nparams){
  int i = 0, j;

  fprintf(stderr, "** ---> math_m2 action called\n");
  assert(kp != NULL);
  assert(kp->len == expect_kp.len);
  do {
    //assert(confd_val_eq(&kp->v[i][0], &expect_kp.v[i][0]) > 0);
    if (expect_kp.v[i][0].type != C_XMLTAG &&
        expect_kp.v[i][0].type != C_NOEXISTS) {
      j = 1;
      do {
        //  assert(confd_val_eq(&kp->v[i][j], &expect_kp.v[i][j]) > 0);
      } while (expect_kp.v[i][j++].type != C_NOEXISTS);
    }
  } while (expect_kp.v[i++][0].type != C_NOEXISTS);

  return math(uinfo, name, &c_expect_name, params, c_expect_params,
              nparams, m2__ns);
}

#define OKTEST() do { \
        assert(maapi_request_action(sock, params, nparams,              \
                                    &values, &nvalues, m2__ns,          \
                                    "/system/computer{fred}/math") ==   \
               CONFD_OK);                                               \
        assert(nvalues == expect_nvalues);                              \
        for (i = 0; i < expect_nvalues; i++) {                          \
            assert(memcmp(&values[i].tag, &c_expect_values[i].tag,      \
                          sizeof(c_expect_values[0].tag)) == 0);        \
            assert(confd_val_eq(&values[i].v, &c_expect_values[i].v) > 0); \
        }                                                               \
        for (i = 0; i < nvalues; i++)                                   \
            confd_free_value(CONFD_GET_TAG_VALUE(&values[i]));          \
        if (nvalues > 0)                                                \
            free(values);                                               \
        nvalues = 0;                                                    \
    } while (0)

#define ERRTEST(errno, errstr) do {                                     \
        assert(maapi_request_action(sock, params, nparams,              \
                                    &values, &nvalues, m2__ns,          \
                                    "/system/computer{fred}/%s", name) == \
               CONFD_ERR);                                              \
        assert(confd_errno == (errno));                                 \
        if ((errstr)[0] != '\0')                                        \
            assert(strstr(confd_lasterr(), (errstr)) != NULL);          \
        else                                                            \
            assert((confd_lasterr())[0] == '\0');                       \
        assert(nvalues == 0);                                           \
    } while(0)


static void request_action(struct addrinfo *addr){
  int sock;
  const char *groups[1];
  struct confd_ip ip;
  int nparams = expect_nparams;
  int nparams2 = 4 * expect_nparams / 3;
  confd_tag_value_t params[nparams2];
  char *name = "math";
  confd_tag_value_t *values;
  int nvalues = 0, i;
  int th;

  assert((sock = get_maapisock(addr)) >= 0);
  ip.af = AF_INET;
  inet_pton(AF_INET, "10.0.0.33", &ip.ip.v4);
  assert(maapi_start_user_session(sock, "admin", "cli", groups, 0,
                                  &ip, CONFD_PROTO_SSH) == CONFD_OK);

  for (i = 0; i < nparams; i++)
    params[i] = c_expect_params[i];
  for ( ; i < nparams2; i++)
    params[i] = c_expect_params[i - expect_nparams];

  /* normal request */
  OKTEST();

  /* ditto using th */
  assert((th = maapi_start_trans(sock, CONFD_RUNNING, CONFD_READ)) >= 0);

  assert(maapi_set_namespace(sock, th, m2__ns) == CONFD_OK);
  assert(maapi_cd(sock, th, "/system/computer/{fred}") == CONFD_OK);
  assert(maapi_request_action_th(sock, th, params, nparams,
                                 &values, &nvalues, "math") == CONFD_OK);
  assert(maapi_finish_trans(sock, th) == CONFD_OK);
  assert(nvalues == expect_nvalues);
  for (i = 0; i < expect_nvalues; i++) {
    assert(memcmp(&values[i].tag, &c_expect_values[i].tag,
                  sizeof(c_expect_values[0].tag)) == 0);
    assert(confd_val_eq(&values[i].v, &c_expect_values[i].v) > 0);
  }
  for (i = 0; i < nvalues; i++)
    confd_free_value(CONFD_GET_TAG_VALUE(&values[i]));
  if (nvalues > 0)
    free(values);

  /* argument validation */

  nvalues = 0;

  /* request action that doesn't exist - path is wrong */
  name = "max";
  ERRTEST(CONFD_ERR_BADPATH, "Bad path element");

  /* request action that doesn't exist - path is OK but it's not an action */
  name = "name";
  ERRTEST(CONFD_ERR_NOEXISTS, "No such action");
  name = "math";

  /* wrong tag in params */
  CONFD_SET_TAG_INT32(&params[11], m2_value, 42);
  ERRTEST(CONFD_ERR_BADTYPE, "Missing value");
  CONFD_SET_TAG_INT32(&params[11], m2_number, 42);

  /* wrong type in params */
  CONFD_SET_TAG_INT32(&params[7], m2_type, m2_square);
  ERRTEST(CONFD_ERR_BADTYPE, "expected type");
  CONFD_SET_TAG_ENUM_HASH(&params[7], m2_type, m2_square);

  /* minOccurs violation, no values */
  nparams = 0;
  ERRTEST(CONFD_ERR_TOO_FEW_ELEMS, "/operation");

  /* minOccurs violation, one (optional) value but no "dynamic" elems */
  params[0] = optparam[0];
  nparams = 1;
  ERRTEST(CONFD_ERR_TOO_FEW_ELEMS, "/operation");
  params[0] = c_expect_params[0];

  /* maxOccurs violation */
  nparams = nparams2;
  ERRTEST(CONFD_ERR_TOO_MANY_ELEMS, "/operation");

  /* remaining (i.e. non-matched) values at end */
  CONFD_SET_TAG_INT32(&params[expect_nparams], m2_value, 42);
  ERRTEST(CONFD_ERR_BADTYPE, "Too many");

  /* wrong value for start tag (silly) */
  CONFD_SET_INT32(CONFD_GET_TAG_VALUE(&params[5]), 42);
  ERRTEST(CONFD_ERR_BADTYPE, "value for start tag");
  CONFD_SET_TAG_XMLBEGIN(&params[5], m2_operation, m2__ns);

  /* missing end tag */
  nparams = expect_nparams - 1;
  ERRTEST(CONFD_ERR_BADTYPE, "Missing end");
  nparams = expect_nparams;

  /* request alternate callback behaviour with "magic number" */

  /* OK return w/o values */
  CONFD_SET_TAG_INT32(&params[1], m2_number, 0);
  assert(maapi_request_action(sock, params, nparams,
                              &values, &nvalues, m2__ns,
                              "/system/computer{fred}/math") == CONFD_OK);
  assert(nvalues == 0);

  /* ERR return w/o msg */
  CONFD_SET_TAG_INT32(&params[1], m2_number, -1);
  ERRTEST(CONFD_ERR_EXTERNAL, "");

  /* bogus err return */
  CONFD_SET_TAG_INT32(&params[1], m2_number, -2);
  ERRTEST(CONFD_ERR_EXTERNAL, "Bad return value: -17");

  /* ERR return w msg */
  CONFD_SET_TAG_INT32(&params[1], m2_number, -3);
  ERRTEST(CONFD_ERR_EXTERNAL, "Number -3 is invalid");

  /* OK return, no values, msg thrown away */
  CONFD_SET_TAG_INT32(&params[1], m2_number, -4);
  assert(maapi_request_action(sock, params, nparams,
                              &values, &nvalues, m2__ns,
                              "/system/computer{fred}/math") == CONFD_OK);
  assert(nvalues == 0);

  /* bogus: data + ERR return (ignored) */
  CONFD_SET_TAG_INT32(&params[1], m2_number, -5);
  assert(maapi_request_action(sock, params, nparams,
                              &values, &nvalues, m2__ns,
                              "/system/computer{fred}/math") == CONFD_OK);
  assert(nvalues == 5);
  for (i = 0; i < nvalues; i++)
    confd_free_value(CONFD_GET_TAG_VALUE(&values[i]));
  if (nvalues > 0)
    free(values);
  nvalues = 0;
  CONFD_SET_TAG_INT32(&params[1], m2_number, 13);

  /* normal request ns in only in XML* tags */
  for (i = 0; i < nparams; i++) {
    switch (CONFD_GET_TAG_VALUE(&params[i])->type) {
    case C_XMLTAG:
    case C_XMLBEGIN:
    case C_XMLEND:
      break;
    default:
      CONFD_SET_TAG_NS(&params[i], 0);
    }
  }
  OKTEST();

  /* normal request w no ns in tags */
  for (i = 0; i < nparams; i++) {
    CONFD_SET_TAG_NS(&params[i], 0);
  }
  OKTEST();

  /* kill callback with unexpected value */
  fprintf(stderr, "* Expect assertion failure\n");
  CONFD_SET_TAG_INT32(&params[1], m2_number, 47);
  ERRTEST(CONFD_ERR_EXTERNAL, "");

  /* request w/o callback registered */
  ERRTEST(CONFD_ERR_EXTERNAL, "No registration");
}


int main(int argc, char **argv){
  char confd_port[16];
  struct addrinfo hints;
  struct addrinfo *addr = NULL;
  int debuglevel = CONFD_SILENT;
  int requestor = 0;
  int c;
  char *p, *dname;
  int i;
  struct confd_action_cbs acb;
  struct pollfd set[3];
  int fds;
  int daemon_mode = 0;
  int ret;

  snprintf(confd_port, sizeof(confd_port), "%d", CONFD_PORT);
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = PF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;

  while ((c = getopt(argc, argv, "Ddtprc:")) != -1) {
    switch (c) {
    case 'd':
      debuglevel = CONFD_DEBUG;
      break;
    case 'D':
      daemon_mode = 1;
      break;
    case 't':
      debuglevel = CONFD_TRACE;
      break;
    case 'p':
      debuglevel = CONFD_PROTO_TRACE;
      break;
    case 'r':
      requestor = 1;
      break;
    case 'c':
      if ((p = strchr(optarg, '/')) != NULL)
        *p++ = '\0';
      else
        p = confd_port;
      if (getaddrinfo(optarg, p, &hints, &addr) != 0) {
        if (p != confd_port) {
          *--p = '/';
          p = "/port";
        } else {
          p = "";
        }
        fprintf(stderr, "%s: Invalid address%s: %s\n",
                argv[0], p, optarg);
        exit(1);
      }
      break;
    default:
      fprintf(stderr,
              "Usage: %s [-dtpr] [-c address[/port]]\n",
              argv[0]);
      exit(1);
    }
  }

  if (addr == NULL &&
      getaddrinfo("127.0.0.1", confd_port, &hints, &addr) != 0)
    /* "Can't happen" */
    confd_fatal("%s: Failed to get address for ConfD: %s\n",
                argv[0], gai_strerror(i));
  if ((dname = strrchr(argv[0], '/')) != NULL)
    dname++;
    else
      dname = argv[0];
    /* Init library */
    confd_init(dname, stderr, debuglevel);
    /*cs_init();*/

    if (requestor) {
      request_action(addr);
      exit(0);
    }

    if ((dctx = confd_init_daemon(dname)) == NULL)
      confd_fatal("Failed to initialize ConfD\n");
    if ((ctlsock = get_ctlsock(addr)) < 0)
      confd_fatal("Failed to connect to ConfD\n");
    if ((workersock = get_workersock(addr)) < 0)
      confd_fatal("Failed to connect to ConfD\n");

    memset(&acb, 0, sizeof(acb));

    //strcpy(acb.actionpoint, "math_nc");
    /*
      acb.init = init_action;
      acb.action = math_nc;
      if (confd_register_action_cbs(dctx, &acb) != CONFD_OK)
      confd_fatal("Couldn't register action callbacks\n");
    */

    strcpy(acb.actionpoint, "math_m2");
    acb.init = init_action;
    acb.action = math_cs;//m2__actionpointid_math_m2;//math_m2;

    if (confd_register_action_cbs(dctx, &acb) != CONFD_OK)
      confd_fatal("Couldn't register action callbacks\n");

    confd_register_done(dctx);

    printf("action started\n");
    fflush(stdout);

    if (daemon_mode)
      fds = 2;
    else
      fds = 3;



    while (1) {

      set[0].fd = ctlsock;
      set[0].events = POLLIN;
      set[0].revents = 0;

      set[1].fd = workersock;
      set[1].events = POLLIN;
      set[1].revents = 0;

      set[2].fd = 0;
      set[2].events = POLLIN;
      set[2].revents = 0;

      if (poll(set, fds, -1) < 0)
        confd_fatal("Poll failed\n");

      /* Check for I/O */

      if (set[0].revents & POLLIN) { /* ctlsock */
        if ((ret = confd_fd_ready(dctx, ctlsock)) == CONFD_EOF) {
          confd_fatal("Control socket closed\n");
        } else if (ret == CONFD_ERR && confd_errno != CONFD_ERR_EXTERNAL) {
          confd_fatal("Error on control socket request: %s (%d): %s\n",
                      confd_strerror(confd_errno), confd_errno, confd_lasterr());
        }
      }

      if (set[1].revents & POLLIN) { /* workersock */
        if ((ret = confd_fd_ready(dctx, workersock)) == CONFD_EOF) {
          confd_fatal("Worker socket closed\n");
        } else if (ret == CONFD_ERR && confd_errno != CONFD_ERR_EXTERNAL) {
          confd_fatal("Error on worker socket request: %s (%d): %s\n",
                      confd_strerror(confd_errno), confd_errno, confd_lasterr());
        }
      }

      if (set[2].revents & (POLLIN|POLLHUP)) { /* stdin */
        exit(0);
      }

    }
}
