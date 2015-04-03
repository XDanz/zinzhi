include ./Make.defines

all:
	@echo "Nothing to make in this directory"
	@echo "Please read the README file"

clean:
	rm -f $(CLEANFILES)

tags:
	find . -name "*.[chCH]" -print | etags -
 
distclean:
	rm -f $(CLEANFILES) config.cache config.log config.status config.h Make.defines Makefile
