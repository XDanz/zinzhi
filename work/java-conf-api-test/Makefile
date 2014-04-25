# include /Users/dtc/dev/trunk2/support/include.mk
###-*-makefile-*-   ; force emacs to enter makefile-mode

ERL       = /Users/dtc/dev/trunk-darwin/otp/installed/bin/erl
ERLC      = /Users/dtc/dev/trunk-darwin/otp/installed/bin/erlc
ESCRIPT   = /Users/dtc/dev/trunk-darwin/otp/installed/bin/escript
DIALYZER  = /Users/dtc/dev/trunk-darwin/otp/installed/bin/dialyzer
SVNTOP_DIR = /Users/dtc/dev/trunk-darwin
NATIVE_SVNTOP_DIR = /Users/dtc/dev/trunk-darwin
ERL_DIR   = /Users/dtc/dev/trunk-darwin/otp/installed/lib/erlang
SKIP_JAVA ?= 
SKIP_LIBSMI ?= 
BUILDCONFM     = false
BITS64    = true
CONFD_LIB_VSN = 05020000
CONFD_LIB_PROTO_VSN = 50
CONFD_LIB_DOT_VSN = 5.2.0

CONFD_DIR = $(NATIVE_SVNTOP_DIR)/confd_dir
NCS_DIR   = $(NATIVE_SVNTOP_DIR)/ncs_dir

HRL2H     = $(SVNTOP_DIR)/support/hrl2h.sh
HRL2JAVA  = $(SVNTOP_DIR)/java/support/hrl2java.sh

## If you get irritated by the fail on warnings, set this variable
ifeq ($(shell echo $$ERL_NO_FAIL_ON_WARNING), true)
WERR=
else
WERR = -Werror
endif


# Cover forms may have internal warnings
# ignore them for the time beeing. Needs to be fixed.
ifeq ($(shell echo $$TAILF_COVER), true)
WERR=
COVER= +cover
else
COVER=
endif

ERLC_FLAGS+=$(WERR) $(COVER) -I $(SVNTOP_DIR)/lib
# confd/ebin is needed in order to find our files with -behaviour_info
ERLC_FLAGS+=-pa $(SVNTOP_DIR)/lib/confd/ebin
DIALYZER_FLAGS += -Wno_return -Wno_unused -Wno_improper_lists

ifeq ($(TAILF_RELEASE),true)
  ERLC_FLAGS += +slim +compressed -Drelease -Ddont_use_verbosity
endif

ifeq ($(TYPE), debug)
  ERLC_FLAGS+=+debug_info -Ddebug
endif

ifeq ($(TYPE), dialyzer)
  ERLC_FLAGS+=+debug_info -DNOTEST
endif

ifeq ($(TYPE), perf)
  ERLC_FLAGS+=-Dperf
endif

# janl: The ERL_INTERFACE_LIB location here is overridden in
# capi and js for cross builds. Needs to be the cross variant,
# not the native variant here.
ERL_INTERFACE_LIB     = /Users/dtc/dev/trunk-darwin/otp/installed/lib/erlang/lib/erl_interface-3.7.15/lib
ERL_INTERFACE_LIBS    = -lerl_interface -lei 
ERL_INTERFACE_INCLUDE = /Users/dtc/dev/trunk-darwin/otp/installed/lib/erlang/lib/erl_interface-3.7.15/include
XERL_INTERFACE_LIB    = $(SVNTOP_DIR)/otp/lib/erl_interface/obj/$(XHOST)

ERL_SOURCES  := $(wildcard *.erl)
YECC_SOURCES := $(wildcard *.yrl) 
ERL_HEADERS += $(shell (echo $(wildcard *.hrl) | sed 's/confd_cfg.hrl//g' \
	| sed 's/ncs_cfg.hrl//g')) \
	$(wildcard ../include/*.hrl) \
	$(SVNTOP_DIR)/lib/confd/include/*.hrl
ERL_OBJECTS := $(ERL_SOURCES:%.erl=../ebin/%.beam) \
	       $(YECC_SOURCES:%.yrl=../ebin/%.beam)
MODULES     := $(ERL_SOURCES:%.erl=%)

EXS_SOURCES := $(wildcard *.exs)
EXS_OBJECTS := $(EXS_SOURCES:%.exs=../priv/%.fxs)

APP_SOURCES := $(wildcard *.app.src)
APP_OBJECTS := $(APP_SOURCES:%.app.src=../ebin/%.app)

APPNAME := $(APP_SOURCES:%.app.src=%)
ifneq ($(APPNAME),)
ERLC_FLAGS += -DDBGTAG=$(APPNAME)
endif

APPSCRIPT = '$$vsn=shift; $$mods=""; while(@ARGV){ $$_=shift; s/^([A-Z].*)$$/\'\''$$1\'\''/; $$mods.=", " if $$mods; $$mods .= $$_; } while(<>) { s/%VSN%/$$vsn/; s/%MODULES%/$$mods/; print; }'

EDOC_OPTS=[{dir,"../doc/"},{todo,true}]

# dependencies for fxs/xso files
FXSDEPS=../../util/include/exs.hrl ../../util/src/exs_type.erl \
	../../util/src/exs_validator.erl ../../confdc/src/confd_tools.erl \
	../../util/src/confd_loading.erl ../../confd/include/cs.hrl \
	../../confdc/src/confd_emit_tools.erl


# ------------------------------------------------------------------------
# Java Programs


JARS = $(CONFD_DIR)/java/jar
CLASSPATH = $(JARS)/conf-api-$(CONFD_LIB_DOT_VSN).jar:$(JARS)/log4j-1.2.16.jar


# ------------------------------------------------------------------------
# C Programs

OSTYPE        = Darwin
CPU_VENDOR_OS = i386-apple-darwin13.1.0

# These programs will work and produce binaries for the machine on
#which we are doing the *build* (which, when not cross-compiling, is
#also the binaries for the target *host*)
PS2PDF		= pstopdf -i -o 
CC		= gcc
CFLAGS		= -Wall -O2
CSHARED_FLAGS	= -Wall -fPIC -fno-common -O2 -DDYNAMIC_DRIVER
CSHARED_FLAGS	= -Wall -fPIC -fno-common -O2 -DDYNAMIC_DRIVER
SSL_CFLAGS      = -I/usr/include
PAM_INCLUDE	= -I/usr/include/security
LD		= gcc
LDFLAGS 	= -lpthread
LDSHARED_FLAGS	= -dynamiclib
LDDRV_FLAGS	= -bundle -flat_namespace -undefined suppress
SSL_LDFLAGS     = -L/usr/lib
SHARED_LIBS	= -ldl
LIBDL		= -ldl
LIBCRYPT	= 
STRIP		= :
# This should be GNU tar
TAR             = tar
TAR_ZERO        = 
KILLALL         = killall

F_STACK_PROTECTOR_ALL_WORKS = yes

CROSS_COMPILING = false
# When CROSS_COMPILING is "true" use these defines as compilers that
#will produce binaries for target *host*
XHOST		= 
TOOLS_PREFIX	= 
XCC		= 
XCXX		= 
XCFLAGS		= 
XCSHARED_FLAGS	= 
XLD		= 
XLDFLAGS	= 
XLDSHARED_FLAGS	= 
XLDDRV_FLAGS	= 
XSHARED_LIBS	= 
XLIBDL		= 
XLIBCRYPT	= 
XLGCC		= 
XSTRIP		= 
XRANLIB		= 
XAR		= 
XAS		= 


# C targets

ifeq ($(XHOST),) # Native build
%.o:	%.c
	$(CC) $(CFLAGS) -c $<

../priv/%.so: %.o
	$(LD) $(LDDRV_FLAGS) -o $@ $< $(SHARED_LIBS)
../priv//%.so: %.o
	$(LD) $(LDDRV_FLAGS) -o $@ $< $(SHARED_LIBS)

%.so: %.o
	$(LD) $(LDDRV_FLAGS) -o $@ $< $(SHARED_LIBS)

else # Cross build
# C Cross targets

$(XHOST)/%.o:	%.c
	mkdir -p $(XHOST)
	$(XCC) $(XCFLAGS) $(CFLAGS) -o $@ -c $<

../priv/$(XHOST)/%.so: $(XHOST)/%.o
	mkdir -p ../priv/$(XHOST)
	$(XLD) $(XLDDRV_FLAGS) -o $@ $< $(SHARED_LIBS) $(XSHARED_LIBS)

$(XHOST)/%.so: $(XHOST)/%.o
	mkdir -p $(XHOST)
	$(XLD) $(XLDDRV_FLAGS) -o $@ $< $(SHARED_LIBS) $(XSHARED_LIBS)
endif # Cross build


# Erlang Targets

../ebin/%.app: %.app.src ../vsn.mk Makefile
	perl -e $(APPSCRIPT) "$(VSN)" $(MODULES) < $< > $@

../ebin/%.beam: %.erl
	$(ERLC) $(ERLC_FLAGS) -o ../ebin $<

%.erl: %.yrl
	$(ERLC) $<

# Confspecs

../priv/%.fxs: %.exs $(FXSDEPS)
	$(NATIVE_SVNTOP_DIR)/bin/confd -E -noshell -s confd_tools exs2fxs $< $@ true

# Include rules for man-pages only if we have defined MANSECTION
ifneq ($(MANSECTION),)

MANPAGES_in	= $(wildcard *.in)
MANPAGES_gen	= $(MANPAGES_in:.in=.$(MANSECTION))
MANPAGES	= $(wildcard *.$(MANSECTION)) $(MANPAGES_gen)
PDFPAGES  := $(MANPAGES:%.$(MANSECTION)=%.$(MANSECTION).pdf)
HTMLPAGES := $(MANPAGES:%.$(MANSECTION)=%.$(MANSECTION).html)

%.$(MANSECTION).pdf:	%.$(MANSECTION)
	man -t ./$< | $(PS2PDF) $<.pdf

%.$(MANSECTION).html:       %.$(MANSECTION)
ifeq ($(OSTYPE),Darwin)
	man ./$< | man2html -title "$* ($(MANSECTION))" -cgiurl ../index.html |\
		perl $(MANPREDIR)../../support/trim_html.perl > $@
else
	man2html -h ./$< | perl $(MANPREDIR)../../support/trim_html.perl > $@
endif

endif

CONFD_DIR ?= ../../../../confd_dir


SUBDIRS = maapi1 maapi1_yang  maapi3_candid maapi4_phase cdb1 cdb2_subscriptions \
          cdb3_bulk dp1 dp2_validate \
	  dp3_actions dp4_extern_candidate notif1 dp5_notifications \
	  dp6_rangeDataCb dp7_secondaryIndex dp8_lists_oref \
	  dp9_order_by dp_transform dp_hook ha1


all: 
ifneq ($(SKIP_JAVA), true)
	@set -e ; \
          for d in $(SUBDIRS) ; do \
            if [ -f $$d/Makefile ]; then \
              ( cd $$d &&  CONFD_DIR=$(CONFD_DIR) $(MAKE) $@ ) || exit 1 ; \
            fi ; \
          done
endif

clean:
ifneq ($(SKIP_JAVA), true) 
	@set -e ; \
          for d in $(SUBDIRS) ; do \
            if [ -f $$d/Makefile ]; then \
              ( cd $$d && CONFD_DIR=$(CONFD_DIR) $(MAKE) $@ ) || exit 1 ; \
            fi ; \
          done
endif



test: 	all
ifneq ($(SKIP_JAVA), true)
	@set -e ; \
          for d in $(SUBDIRS) ; do \
            if [ -f $$d/Makefile ]; then \
              ( cd $$d &&  CONFD_DIR=$(CONFD_DIR) $(MAKE) $@ ) || exit 1 ; \
            fi ; \
          done
endif
