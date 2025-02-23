; 50th anniversary competition entry
; Prime numbers
; Calculate the sequence of prime numbers, next prime displayed in line 21. Stop, KC and Run after each calculation.
; Bas Wijnen from Holland wrote a prime number generator, which was judged to be the best of the mathematical programs, a difficult choice among many possibilities.
; This is about the most complex calculation which can be performed in the 32 instructions (the First Program was effectively a subcomponent of this program). When the
; program stops having found a prime, it can be restarted and will go on to the next one.
; source: https://curation.cs.manchester.ac.uk/computer50/www.computer50.org/mark1/prog98/prizewinners.html
; also: https://web.archive.org/web/19991008021142/http://www.cs.man.ac.uk/prog98/prizewinners.html
30
0000 JMP 24
0001 LDN 21
0002 STO 21
0003 LDN 21
0004 SUB 15
0005 STO 21
0006 LDN 15
0007 STO 22
0008 LDN 22
0009 STO 22
0010 LDN 22
0011 SUB 15
0012 STO 22
0013 SUB 21
0014 CMP
0015 NUM -1
0016 LDN 21
0017 STO 23
0018 LDN 23
0019 SUB 22
0020 JMP 0
0021 NUM 1
0024 NUM 7
0025 CMP
0026 JRP 0
0027 STO 23
0028 LDN 23
0029 SUB 22
0030 CMP
0031 JMP 20