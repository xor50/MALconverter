# MAL converter

This is a small program to convert *Micro Assembly Language* lines to binary microinstruction code (used by the *Mic-1*)
and to hex for use in [Logisim](https://github.com/logisim-evolution/logisim-evolution) ROMs.

*MAL* and the *Mic-1* both come from the book *Structured Computer Organization*
by [Andrew S. Tanenbaum](https://en.wikipedia.org/wiki/Andrew_S._Tanenbaum).

## Notes

### How to use:
1) Start and use via command line.  
2) Just enter correct MAL like ``MDR = TOS = MDR + H; wr; goto Main1``. Don't forget or leave out ``NEXT_ADDRESS`` (
   hex, ``Main1``, or ``MBR``).  
3) When you're done leave with ``exit``.

### Known issues:
maybe some tiny things, but mainly

* possible crashes if you don't enter correct MAL (or ``exit`` command)

## License
This project is under MIT license.  
Nonetheless, if you do anything cool with it I'd love if you could reference the original project (this repo). Thanks!
