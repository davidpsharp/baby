; experimental new assembly format version of diffeqt.asm program
; as of 3 Jan 2025 format is not settled yet and not supported by the simulator
; also doesn't include any examples of data bits set alongside instructions

	NUM 0				; line 0 unused as execution starts at line 1.

start:
	; 2X the pixel mask number to do a logical shift left
	; i.e. xpixelplot<<=1, to plot next pixel along
	; do the 2X by doing a=-1*((-1*a)-a) [a starts & ends +ve]

	LDN xpixelplot
	SUB xpixelplot
	STO xpixelplot

	; eventually shifts the bit out, xpixelplot becomes 0 so stop
	CMP
	STP

	; ldn/sto to invert sign ready for next loop (leaving it negative ready for LDN)
	ldn xpixelplot
	sto xpixelplot

get_line_to_plot_on:
	LDN 22	      				; get the instruction at next line to plot so can
								; apply pixel blit [this line is modded each loop]
	SUB xpixelplot
	STO blitbuf
	LDN blitbuf
save_line_plotted:
	STO 22      				; write instruction & pixel from buffer [this line is modded each loop]

	; mod instructions to index different line on the next loop
	LDN get_line_to_plot_on		; get instr with current line number to draw at
	SUB y_delta 				; adjust line num in instruction by y_delta
	STO blitbuf					; sign adjust so ready to blit over existing instr
	LDN blitbuf

	STO get_line_to_plot_on 	; mod code line num to load before drawing
	SUB loadstoreflipmask   	; switch instruction code from ldn to sto
	STO save_line_plotted	 	; mode code line num to store when writing pixel

	; decrement y_delta by 1 so change in pixel height 1 less/more next loop
	LDN y_delta
	SUB one		
	STO y_delta

	; ldn/sto to invert sign ready for next loop		
	LDN y_delta
	STO y_delta

	JMP zero					; loop back to start

one: 							; line 26
	JMP 1               		;  00000001 : 1
loadstoreflipmask:
	NUM -8192           		;  bit mask to flip a load in struction to a sto
y_delta:
	NUM -6              		; number of lines to adjust by for next x coor
    							; (goes negative)
xpixelplot:
	NUM 131072          		;  bit mask for the pixel we want to plot each loop
blitbuf:
	JMP 0        				; used to stage write pixels & stage self mod instructions
zero:
	ADR start          			; REMEMBER have to resolve ADR to be label-1 ! 
				        		; equivalent to NUL start-1 
   								; TODO: is ADR the right mnemonic for this?


