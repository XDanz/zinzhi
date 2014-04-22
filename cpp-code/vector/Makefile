
DIRS=$(shell ls -d */)

tt:
	echo $(DIRS)
clean:
	for i in $(DIRS); do \
		echo $${i}; \
		$(MAKE) -C ./$${i} clean || exit 1; \
	done
