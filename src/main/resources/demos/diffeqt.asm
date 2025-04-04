; 50th anniversary competition entry
; Parabola
; Magnus Olsson from Sweden wrote a program which: "uses the CRT as an X-Y display to plot a parabola by 
; setting bits in the unused upper half of lines 1-22. The parabola is computed as the solution of a 
; difference equation.
; One of very few programs to make active use of the unused bits on the right, rather than just having a
; picture there. Also a nice cross between "mathematical" and "graphical".
; source: https://curation.cs.manchester.ac.uk/computer50/www.computer50.org/mark1/prog98/prizewinners.html
; also: https://web.archive.org/web/19991008021142/http://www.cs.man.ac.uk/prog98/prizewinners.html

31
0001 LDN 29
0002 SUB 29
0003 STO 29
0004 CMP
0005 STP
0006 LDN 29
0007 STO 29
0008 LDN 22
0009 SUB 29
0010 STO 30
0011 LDN 30
0012 STO 22
0013 LDN 8
0014 SUB 28
0015 STO 30
0016 LDN 30
0017 STO 8
0018 SUB 27
0019 STO 12
0020 LDN 28
0021 SUB 26
0022 STO 28
0023 LDN 28
0024 STO 28
0025 JMP 31
0026 NUM 1
0027 NUM -8192
0028 NUM -6
0029 NUM 131072
0030 NUM 0
0031 NUM 0