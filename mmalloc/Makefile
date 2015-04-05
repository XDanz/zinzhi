DIRS=src driver_src test

all:
	for i in $(DIRS); do \
		$(MAKE) -C ./$${i} all || exit 1; \
	done



