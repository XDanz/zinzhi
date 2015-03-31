(setq-default indent-tabs-mode nil)
(set-default-font "Ubuntu mono 12")
(add-to-list 'default-frame-alist '(height . 43))
(add-to-list 'default-frame-alist '(width . 80))
(add-to-list 'default-frame-alist '(font . "Ubuntu mono 12"))

(modify-frame-parameters nil '((wait-for-wm . nil)))
(require 'iso-transl)

(if window-system
      (set-frame-size (selected-frame) 80 43))
;; (load "~/dev/trunk/devel_support/lib/emacs/tail-f")
(setq mac-option-modifier nil 
      mac-command-modifier 'meta
      x-select-enable-clipboard t)

;;hide the toolbar 
(tool-bar-mode -1) 
;;hide the menubar
(menu-bar-mode -1)

(require 'package)
(add-to-list 'package-archives
             '("melpa" . "http://melpa.milkbox.net/packages/") t)
(package-initialize)

(unless (package-installed-p 'smex)
  (package-refresh-contents) (package-install 'smex))

(unless (package-installed-p 'ido-vertical-mode)
  (package-refresh-contents) (package-install 'ido-vertical-mode))

(show-paren-mode 1)


(setq ispell-program-name "/opt/local/bin/ispell")

(require 'yang-mode nil t)

;; (require 'lux-mode)

(setq make-backup-files nil)

;; (defun my-yang-mode-hook ()
;;   "Configuration for YANG Mode. Add this to `yang-mode-hook'."
;;   (if window-system
;;       (progn
;;         (c-set-style "BSD")
;;         (setq indent-tabs-mode nil)
;;         (setq c-basic-offset 2)
;;         (setq font-lock-maximum-decoration t)
;;         (font-lock-mode 3))))

;; (add-hook 'yang-mode-hook 'my-yang-mode-hook)
(setq blink-matching-paren-distance nil)

(defconst sort-of-yang-identifier-regexp "[-a-zA-Z0-9_\\.:]*")

(add-hook
 'yang-mode-hook
 '(lambda ()
    (outline-minor-mode)
    (setq outline-regexp
          (concat "^ *" sort-of-yang-identifier-regexp " *"
                  sort-of-yang-identifier-regexp
                  " *{")))) 



;;;
;;; Erlang mode configuration
;;;

;;; Set up for automatic mode selection
;; (let ((a '("\\.erl$" . erlang-mode))
;;       (c '("\\.rdt$" . erlang-mode))
;;       (b '("\\.hrl$" . erlang-mode)))
;;   (or (assoc (car a) auto-mode-alist)
;;       (setq auto-mode-alist (cons a auto-mode-alist)))

;;   (or (assoc (car c) auto-mode-alist)
;;       (setq auto-mode-alist (cons c auto-mode-alist)))

;;   (or (assoc (car b) auto-mode-alist)
;;       (setq auto-mode-alist (cons b auto-mode-alist))))


;; ;;; Autoload the Erlang mode when needed.
;; (autoload 'erlang-mode "erlang" "Major mode for editing Erlang code." t)
;; (autoload 'run-erlang "erlang" "Run an inferior Erlang shell." t)

;; ;;; Files usually not interesting to view in Emacs.
;; (setq completion-ignored-extensions
;;       (append '(".jam" ".vee" ".beam") completion-ignored-extensions))

;; 					; Fontify my Erlang buffers
;; ;;(setq font-lock-maximum-decoration t)
;; (add-hook 'erlang-mode-hook 'turn-on-font-lock)


;;; This is how I want my new Erlang files to look:
(add-hook 'erlang-new-file-hook
	  (lambda () 
	    ;; work-around for Distel, erlang-mode calls this hook in
	    ;; attach buffers when it sees that they are empty. remove
	    ;; this after a proper fix in distel or erlang.el
	    (when buffer-file-name
	      'tempo-template-erlang-large-header)))

(setq erlang-skel-mail-address "dtc@tail-f.com")

;;
;; End erlang configuration
;;


;;yasnippet
;; (yas-global-mode 1)

;;;  Autocomplete config start 

;;; set the trigger key so that it can work together with yasnippet on tab key,
;;; if the word exists in yasnippet, pressing tab will cause yasnippet to
;;; activate, otherwise, auto-complete will

;; (require 'auto-complete-config)
;; (add-to-list 'ac-dictionary-directories "/Users/dtc/.emacs.d/ac-dict")

;; (set-default 'ac-sources
;;              '(ac-source-abbrev
;;                ac-source-dictionary
;;                ac-source-yasnippet
;;                ac-source-words-in-buffer
;;                ac-source-words-in-same-mode-buffers
;;                ac-source-semantic))
;; (dolist (m '(c-mode c++-mode java-mode))
;;   (add-to-list 'ac-modes m))
;; (ac-config-default)
;;;  Autocomplete config start 

;; (global-auto-complete-mode t)
;; (ac-set-trigger-key "TAB")
;; (ac-set-trigger-key "<tab>")


(setq interprogram-paste-function 'x-selection-value)




;;; yasnippet
;;; should be loaded before auto complete so that they can work together
;; (require 'yasnippet)
;; (yas-global-mode 1)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;  eclim config start -------------------------

;; (add-to-list 'load-path "~/.emacs.d/elpa/emacs-eclim-20140505.743")
;; (require 'eclim)

;; ;; Variables
;; (setq eclim-auto-save t
;;       ;;      eclim-executable "/opt/eclipse/eclim"
;;       ;;      eclimd-executable "/opt/eclipse/eclimd"
;;       eclimd-wait-for-process nil
;;       eclimd-default-workspace "~/Document/workspace"
;;       eclim-use-yasnippet nil
;;       help-at-pt-display-when-idle t
;;       help-at-pt-timer-delay 0.1
;;       )

;; (custom-set-variables
;;  '(eclim-eclipse-dirs '("~/Downloads/eclipse"))
;;  '(eclim-executable "~/Downloads/eclipse/eclim")
;;  '(eclimd-default-workspace "~/Document/workspace")
;;  )

;; ;; add the emacs-eclim source
;; (require 'ac-emacs-eclim-source)
;; (ac-emacs-eclim-config)
;; (require 'eclimd)
;; (load "~/.emacs.d/tjk-java.el")
;; (load "~/.emacs.d/dtc-eclim-config.el")
;; (global-eclime-mode)
;; 

(load "~/emacs-themes/deviant-theme.el")
(require 'ido)
(ido-mode t)
(require 'compile)
(add-hook 'c-mode-hook
          (lambda ()
            (unless (file-exists-p "Makefile")
              (set (make-local-variable 'compile-command)
                   ;; emulate make's .c.o implicit pattern rule, but with
                   ;; different defaults for the CC, CPPFLAGS, and CFLAGS
                   ;; variables:
                   ;; $(CC) -c -o $@ $(CPPFLAGS) $(CFLAGS) $<
                   (let ((file (file-name-nondirectory buffer-file-name)))
                     (format "%s -c -o %s.o %s %s %s"
                             (or (getenv "CC") "gcc")
                             (file-name
                              -sans-extension file)
                             (or (getenv "CPPFLAGS") "-DDEBUG=9")
                             (or (getenv "CFLAGS") "-ansi -pedantic -Wall -g")
                             file))))))
(global-set-key "\C-x\C-m" 'compile)

(defun ant-compile ()
  "Traveling up the path, find build.xml file and run compile."
  (interactive)
  (with-temp-buffer
    (while (and (not (file-exists-p "build.xml"))
                (not (equal "/" default-directory)))
      (cd ".."))
    (call-interactively 'compile)))

;; (add-hook 'java-mode-hook
(global-set-key "\C-x\C-a" 'ant-compile)


;; (require 'auto-complete-clang)
;; (define-key c++-mode-map (kbd "C-S-<return>") 'ac-complete-clang)
;; replace C-S-<return> with a key binding that you want

;; (add-hook 'c++-mode-hook
;;       (lambda()
;;             (semantic-mode 1)
;;             (define-key c++-mode-map (kbd "C-z") 'c++-auto-complete)))


;; (defun c++-auto-complete ()
;;   (interactive)
;;   (let ((ac-sources
;;          `(ac-source-semantic
;;            ,@ac-sources)))
;;   (auto-complete)))
(autoload 'smex "smex"
  "Smex is a M-x enhancement for Emacs, it provides a convenient interface to
your recently and most frequently used commands.")

(defun find-alternative-file-with-sudo ()
  (interactive)
  (when buffer-file-name
    (find-alternate-file
     (concat "/sudo:root@localhost:"
	     buffer-file-name))))
(global-set-key (kbd "C-x C-r") 'find-alternative-file-with-sudo)

(global-set-key (kbd "M-x") 'smex)
(require 'ido-vertical-mode)
(ido-mode 1)
(ido-vertical-mode 1)
(server-start)
