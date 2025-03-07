
# Quick Start Guide for the Manchester Baby Simulator

Visit [manchesterbaby.com](https://manchesterbaby.com) and wait a few seconds for it to load. Per the video below...

1. Select a program from the Examples menu to be loaded.
2. Click on **KC** or press F10 on your keyboard to repeatedly step through one instruction at a time.
3. Flick the **Stop / Run** switch to Run or press F5 on your keyboard to run the program until the Stop lamp comes on.

Congratulations, you just learned how to run programs on the world's first stored-program computer from 1948!

https://github.com/user-attachments/assets/6170ac36-9b82-4353-8d0a-30ec6b2b64e3

## What can it do

Despite only have 32 lines each of 32 bits (for 128 bytes of total memory), all visible on it's Cathode Ray Tube monitor. The Manchester Small Scale Experimental Machine or 'Baby' as it was nicknamed could run some impressive programs including animations.

https://github.com/user-attachments/assets/5bee8e28-9e64-421c-967b-f34361fcd4cf

## Real-life replica

In the run up to the 50th anniversary of the original machine, a team at Manchester rebuilt a replica of the original which is now on display and operational at the [Science and Industry Museum, Manchester](https://www.scienceandindustrymuseum.org.uk/whats-on/meet-baby), England. The team of volunteers at the museum uses this simulator to practice operating the real machine and you can learn how to as well.

![replica photo](https://github.com/user-attachments/assets/830f7233-8df1-445b-b688-4a8d6ae18202)

## How to understand what it is doing

From the View menu select the Disassembler to show the program instructions as text to follow what is going on line by line. 

<img width="406" alt="disassembly" src="https://github.com/user-attachments/assets/0f556e10-345a-4830-82af-17a4b662be0b" />

If you hover the mouse over a particular line on the monitor it will display a tooltip showing the instruction that line represents.

<img width="354" alt="monitor tooltip" src="https://github.com/user-attachments/assets/67a8a74e-6332-4955-81a4-24bc3f7d851f" />

## How to program the Baby

The machine only has 6 instructions and you can either program it very slowly, exactly as the original machine was, using the typewriter buttons and switches on the control panel.

Alternatively you can write a program to run on the simulator in modern-style assembly language in any text editor or in the included disassembler and these programs can also be run on the real replica!

Source code for the example programs included with the simulator is available [here](https://github.com/davidpsharp/baby/tree/main/src/main/resources/demos).

See the [Introduction to Programming the Baby Computer](intro-to-programming-the-baby.md) for details.

https://github.com/user-attachments/assets/0ca0b138-82f5-4c3f-b1c6-e3c3020eb196

## Running the simulator as a desktop application

The simulator is written in Java meaning it can be run on any computer or architecture; Windows, Mac or Linux, including Raspberry Pi and doesn't require a web browser or Internet access.

Download the [latest release](https://github.com/davidpsharp/baby/releases) to run offline on your computer. You may need to install a Java Runtime if you don't already have one installed, version 8 or later is required.

The version at [manchesterbaby.com](https://manchesterbaby.com) is exactly the same simulator, just running on a browser-based java runtime called cheerpj. The simulator has a few minor chnages in behaviour when running in that environment.

Full [source code](https://github.com/davidpsharp/baby) for the simulator is available on github and contributions are welcome.
