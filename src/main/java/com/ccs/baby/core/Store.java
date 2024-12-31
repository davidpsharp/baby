package com.ccs.baby.core;

import java.io.*;
import java.util.*;


public class Store
{
	
	// actual data in the store
	private int line[];
	
	private Control control;
	
	// array showing whether the line has changed since the display was last updated
	public boolean isLineAltered[];
	
	// values for the different types of file that might be detected
	public static final int UNACCEPTABLE = 0;
	public static final int SNAPSHOT = 1;
	public static final int ASSEMBLY = 2;
	
	public Store()
	{
		// set up default data
		line = new int[32];
		isLineAltered = new boolean[32];
		
		for(int x = 0; x<32; x++)
		{
			isLineAltered[x] = true;
		}
		
		
	}
	
	// set control since both objects mutually linked
	public void setControl(Control aControl)
	{
		control = aControl;
	}
	
	// adjust a line value
	public void setLine(int lineNumber, int value)
	{
		line[lineNumber] = value;
		isLineAltered[lineNumber] = true;
	}
		
	public int getLine(int lineNumber)
	{
		return line[lineNumber];
	}
	
	// get a single bit from a given line
	public int getBit(int lineNumber, int bitNumber)
	{
		return ((line[lineNumber] >> bitNumber) & 1);
	}
		
	// initialise store
	public void reset()
	{
		for(int i=0; i<32; i++)
		{
			line[i] = 0;
			isLineAltered[i] = true;
		}
	}
	
	public String toString()
	{
		String output = "";
		
		// for each line of the store
		for(int lineNum = 0; lineNum<32; lineNum++)
		{
			String lineOutput = getBinaryString(getLine(lineNum));
			
			// reverse line String for SSEM bit ordering
			lineOutput = reverseString(lineOutput);
						
			output += lineOutput + "  " + disassembleModern(getLine(lineNum)) + "\n";
		}
		
		return output;	
	}
	
	// save a snapshot file to the given filename
	public void saveSnapshot(String fileName) throws IOException
	{
		File snapshotFile = new File(fileName);
		
		FileWriter snapshotWriter = new FileWriter(snapshotFile);
		
		BufferedWriter out = new BufferedWriter(snapshotWriter);
		
		// count number of lines to output
		int numLines = 0;
		for(int line=0; line<32; line++)
		{
			if( getLine(line) != 0)
				numLines++;
		}

		out.write("" + numLines);
		out.newLine();
		
		for(int line=0; line<32; line++)
		{
			// don't output unused lines
			if( getLine(line) != 0)
			{
				// output line number padded with preceeding zeros
				out.write("00");
				if(line < 10)
					out.write("0");
				out.write("" + line + ":");
				
				// output line as reversed binary string
				out.write( reverseString( getBinaryString(getLine(line)) ) );
				
				// if not last line
				if(line<31)
					out.newLine();
			}
		}
	
		out.close();
	}
	
	// save an assembly file to the given filename
	public void saveAssembly(String fileName) throws IOException
	{
		File snapshotFile = new File(fileName);
		
		FileWriter snapshotWriter = new FileWriter(snapshotFile);
		
		BufferedWriter out = new BufferedWriter(snapshotWriter);
		
		// count number of lines to output
		int numLines = 0;
		for(int line=0; line<32; line++)
		{
			if( getLine(line) != 0)
				numLines++;
		}

		out.write("" + numLines);
		out.newLine();
		
		for(int lineNumber=0; lineNumber<32; lineNumber++)
		{
			if( getLine(lineNumber) != 0 )
			{
				String lineNumberS = "" + lineNumber;
							
				out.write( lineNumberS + "  " + disassembleModern( getLine(lineNumber) ) + "\n" );
			}
		}
		
		out.close();
	}
	
	
	// simple auto detection of the file type
	// UNACCEPTABLE - no number on first uncommented line
	// SNAPSHOT - colon on the first uncommented line after the initial number
	// ASSEMBLY - otherwise
	public int getFileType(String fileName) throws IOException, SnapshotException
	{
		// setup file
		File snapshotFile = new File(fileName);
		// snapshot reader
		FileReader snapshotReader;
		
		// open snapshot file
		try
		{
			snapshotReader = new FileReader(snapshotFile);
		}
		catch(FileNotFoundException e)
		{
			throw new SnapshotException(e.getMessage());
		}
      
		// create buffered reader from input stream
		BufferedReader in = new BufferedReader(snapshotReader);
		
		// check for a number of lines value on the first non-comment line
		
		int numberOfLines = -1;
		// while lines to read and we haven't yet read the number of lines value
		while( in.ready() && (numberOfLines == -1) )
		{		
			// read number of lines in snapshot
			// trim whitespace
			String numberOfLinesS = (in.readLine()).trim();
			
			// if the line isn't empty
			if( !numberOfLinesS.equals("") )
			{
				// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
				if(numberOfLinesS.charAt(0) != ';')
				{
					try
					{
						numberOfLines = Integer.parseInt(numberOfLinesS);
					}
					catch(NumberFormatException e)
					{
						return UNACCEPTABLE;
					}
				}
			}
		}
		if(numberOfLines == -1)
			return UNACCEPTABLE;
		
		String nextLine = in.readLine();
		if(nextLine.indexOf(':') == -1)
			return ASSEMBLY;
		else
			return SNAPSHOT;
	}
	
	public void loadLocalSnapshot(String fileName) throws SnapshotException, IOException
	{
		// setup file
		// snapshot reader
		
		InputStream snapshotReader;
		
		// open snapshot file
		try
		{
			snapshotReader = openFile(fileName);
		}
		catch(FileNotFoundException e)
		{
			throw new SnapshotException(e.getMessage());
		}
      
		// create buffered reader from input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(snapshotReader));
		
		// read number of lines in snapshot
		String numberOfLinesS = (in.readLine()).trim();
		try
		{
			int numberOfLines = Integer.parseInt(numberOfLinesS);
		}
		catch(NumberFormatException e)
		{
			throw new SnapshotException("File " + fileName + " is of an unrecognised format");
		}
		
		// reset the store to empty
		reset();
		control.reset();
		
		int lineCounter = 0;
		
		
		// while more lines to read
		while( in.ready() )
		{
			
			lineCounter++;
			
			int lineNumber;
			int lineData;
			
			// read line from file, trailing spaces removed
			String fileLine = ( in.readLine() ).trim();
			
			// if line isn't blank
			if( !fileLine.equals("") )
			{
				// create a new tokenizer, tokenizing on colons
				StringTokenizer tokenizer = new StringTokenizer(fileLine, ":");
				
				// get store line number
				if(tokenizer.hasMoreElements())
				{
					// get the first token on the line
					String lineNumberS = tokenizer.nextToken();
				
					// convert string to int
					try
					{
						lineNumber = Integer.parseInt(lineNumberS);
					}
					catch(NumberFormatException e)
					{
						throw new SnapshotException("File " + fileName + " has a bad store line number at line " + lineCounter + " in the file");
					}
				}
				else
				{
					throw new SnapshotException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
				}
				
				// get binary information for store line
				if(tokenizer.hasMoreElements())
				{
					// get the second token on the line
					String lineDataS = tokenizer.nextToken();
					
					// reverse binary string (for SSEM's reverse binary representation)
					lineDataS = reverseString(lineDataS);
					
					// convert string to int
					try
					{
						// radix (i.e. base) 2 or binary
						lineData = parseBinaryString(lineDataS);
						
					}
					catch(NumberFormatException e)
					{
						throw new SnapshotException("File " + fileName + " has bad store line data at line " + lineCounter + " in the file");
					}					
					
				}
				else
				{
					throw new SnapshotException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
				}
								
				// put data for this line into the store
				setLine(lineNumber, lineData);
			
			}				
		}		
	}
	
	// load a snapshot format image into the store
	public void loadSnapshot(String fileName) throws SnapshotException, IOException
	{
		// setup file
		File snapshotFile = new File(fileName);
		// snapshot reader
		FileReader snapshotReader;
		
		// open snapshot file
		try
		{
			snapshotReader = new FileReader(snapshotFile);
		}
		catch(FileNotFoundException e)
		{
			throw new SnapshotException(e.getMessage());
		}
      
		// create buffered reader from input stream
		BufferedReader in = new BufferedReader(snapshotReader);
		
		// read number of lines in snapshot
		String numberOfLinesS = (in.readLine()).trim();
		try
		{
			int numberOfLines = Integer.parseInt(numberOfLinesS);
		}
		catch(NumberFormatException e)
		{
			throw new SnapshotException("File " + fileName + " is of an unrecognised format");
		}
		
		// reset the store to empty
		reset();
		control.reset();
		
		int lineCounter = 0;
		
		
		// while more lines to read
		while( in.ready() )
		{
			
			lineCounter++;
			
			int lineNumber;
			int lineData;
			
			// read line from file, trailing spaces removed
			String fileLine = ( in.readLine() ).trim();
			
			// if line isn't blank
			if( !fileLine.equals("") )
			{
				// create a new tokenizer, tokenizing on colons
				StringTokenizer tokenizer = new StringTokenizer(fileLine, ":");
				
				// get store line number
				if(tokenizer.hasMoreElements())
				{
					// get the first token on the line
					String lineNumberS = tokenizer.nextToken();
				
					// convert string to int
					try
					{
						lineNumber = Integer.parseInt(lineNumberS);
					}
					catch(NumberFormatException e)
					{
						throw new SnapshotException("File " + fileName + " has a bad store line number at line " + lineCounter + " in the file");
					}
				}
				else
				{
					throw new SnapshotException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
				}
				
				// get binary information for store line
				if(tokenizer.hasMoreElements())
				{
					// get the second token on the line
					String lineDataS = tokenizer.nextToken();
					
					// reverse binary string (for SSEM's reverse binary representation)
					lineDataS = reverseString(lineDataS);
					
					// convert string to int
					try
					{
						// radix (i.e. base) 2 or binary
						lineData = parseBinaryString(lineDataS);
						
					}
					catch(NumberFormatException e)
					{
						throw new SnapshotException("File " + fileName + " has bad store line data at line " + lineCounter + " in the file");
					}					
					
				}
				else
				{
					throw new SnapshotException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
				}
								
				// put data for this line into the store
				setLine(lineNumber, lineData);
			
			}				
		}		
	}
	
	
	public InputStream openFile(String fileName) throws IOException
    {
      java.net.URL url = getClass().getClassLoader().getResource(fileName);
      if(url == null)
        throw new IOException("File not found: " + fileName);
      return url.openStream();
    }
	
	// load a modern assembly file into the store, mainly file input issues
	public void loadModernAssembly(String fileName) throws AssemblyException, IOException
	{
		
		// setup file
		File assemblyFile = new File(fileName);
		
		// assembly reader
		FileReader assemblyReader;
		
		// open assembly file
		try
		{
			assemblyReader = new FileReader(assemblyFile);
		}
		catch(FileNotFoundException e)
		{
			throw new AssemblyException(e.getMessage());
		}
      
		// create buffered reader from input stream
		BufferedReader in = new BufferedReader(assemblyReader);
		
		int numberOfLines = -1;
		
		// while lines to read and we haven't yet read the number of lines value
		while( in.ready() && (numberOfLines == -1) )
		{		
			// read number of lines in snapshot
			String numberOfLinesS = (in.readLine()).trim();
			
			// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
			// if the line isn't empty
			if( !numberOfLinesS.equals("") )
			{
				// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
				if(numberOfLinesS.charAt(0) != ';')
				{
					try
					{
						numberOfLines = Integer.parseInt(numberOfLinesS);
					}
					catch(NumberFormatException e)
					{
						throw new AssemblyException("File " + fileName + " is of an unrecognised format");
					}
				}
			}
		}
		
		// reset the store to empty
		reset();
		control.reset();
		
		int lineCounter = 1;
				
		// while more lines to read
		while( in.ready() )
		{
		
			lineCounter++;
			
			// read line from file, trailing spaces removed
			String fileLine = ( in.readLine() ).trim();
			
			assembleModernToStore(fileLine, lineCounter);
							
		}
	
	}
	

	// TODO: de-duplicate the logic that was copied here when put into a JAR
	
	public void loadLocalModernAssembly(String fileName) throws AssemblyException, IOException
	{
		
		// setup file
		//File assemblyFile = new File(fileName);
		
		// assembly reader
		InputStream assemblyReader;
		
		// open assembly file
		try
		{
			assemblyReader = openFile(fileName);
		}
		catch(FileNotFoundException e)
		{
			throw new AssemblyException(e.getMessage());
		}
      
		// create buffered reader from input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(assemblyReader));
		
		int numberOfLines = -1;
		
		// while lines to read and we haven't yet read the number of lines value
		while( in.ready() && (numberOfLines == -1) )
		{		
			// read number of lines in snapshot
			String numberOfLinesS = (in.readLine()).trim();
			
			// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
			// if the line isn't empty
			if( !numberOfLinesS.equals("") )
			{
				// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
				if(numberOfLinesS.charAt(0) != ';')
				{
					try
					{
						numberOfLines = Integer.parseInt(numberOfLinesS);
					}
					catch(NumberFormatException e)
					{
						throw new AssemblyException("File " + fileName + " is of an unrecognised format");
					}
				}
			}
		}
		
		// reset the store to empty
		reset();
		control.reset();
		
		int lineCounter = 1;
				
		// while more lines to read
		while( in.ready() )
		{
		
			lineCounter++;
			
			// read line from file, trailing spaces removed
			String fileLine = ( in.readLine() ).trim();
			
			assembleModernToStore(fileLine, lineCounter);
							
		}
	
	}
	
	
	// actually assemble an individual line of text
	public void assembleModernToStore(String fileLine, int lineCounter) throws AssemblyException
	{
		int lineNumber = 0;
			
		// if line isn't blank and doesn't start with a semi-colon (comment)
		if( !fileLine.equals("") && !(fileLine.charAt(0) == ';') )
		{
			// create a new tokenizer, tokenizing on spaces
			StringTokenizer tokenizer = new StringTokenizer(fileLine, " ");
			
			// get store line number
			if(tokenizer.hasMoreElements())
			{
				// get the first token on the line
				String lineNumberS = tokenizer.nextToken();
				
				// convert string to int
				try
				{
					lineNumber = Integer.parseInt(lineNumberS);
				}
				catch(NumberFormatException e)
				{
					throw new AssemblyException("Bad store line number at line " + lineCounter);
				}
			}
			else
			{
				throw new AssemblyException("Unrecognised format at line " + lineCounter);
			}
		
			boolean recognised = false;
			boolean operand = false;
			int functionNumber = 0;
			int operandValue = 0;
		
			// get assembly mnemonic information for store line
			if(tokenizer.hasMoreElements())
			{
				// get the second token on the line
				String lineMnemonic = tokenizer.nextToken();
				
				lineMnemonic.toUpperCase();
									
				if(lineMnemonic.equals("JMP"))
				{
					recognised = true;
					operand = true;
					functionNumber = 0;
				}
				
				if(lineMnemonic.equals("JRP"))
				{
					recognised = true;
					operand = true;
					functionNumber = 1;
				}
				
				if(lineMnemonic.equals("LDN"))
				{
					recognised = true;
					operand = true;
					functionNumber = 2;
				}
				
				if(lineMnemonic.equals("STO"))
				{
					recognised = true;
					operand = true;
					functionNumber = 3;
				}
				
				if(lineMnemonic.equals("SUB"))
				{
					recognised = true;
					operand = true;
					functionNumber = 4;
				}
				
				if(lineMnemonic.equals("CMP"))
				{
					recognised = true;
					operand = false;
					functionNumber = 6;
				}
				
				if(lineMnemonic.equals("STP"))
				{
					recognised = true;
					operand = false;
					functionNumber = 7;
				}
				
				if(lineMnemonic.equals("NUM"))
				{
					recognised = true;
					operand = true;
					functionNumber = 8;
				}					
				
				if(operand)
				{
					// get operand
					if(tokenizer.hasMoreElements())
					{
						// get the third token on the line
						String operandValueS = tokenizer.nextToken();
						
						// convert string to int
						try
						{
							operandValue = Integer.parseInt(operandValueS);
						}
						catch(NumberFormatException e)
						{
							throw new AssemblyException("Bad operand at line " + lineCounter);
						}
					}
					else
					{
						throw new AssemblyException("Unrecognised format at line " + lineCounter);
					}
				}
				
				int lineData = 0;
				// now have all the data needed
				if(functionNumber == 8)
				{
					// if NUM pseudo-instruction
					lineData = operandValue;
				}
				else
				{
					// all other 'real' instructions
					if(operandValue > 31)
					{
						throw new AssemblyException("Bad store line number at line " + lineCounter);
					}
					
					lineData = operandValue;
					lineData |= (functionNumber << 13);
				}
					
				setLine(lineNumber, lineData);	
				
			// if there was no mnemonic on the line	
			}
			else
			{
				throw new AssemblyException("Unrecognised format at line " + lineCounter);
			}
		
		
		}
	}
	
	public static String disassembleModern(int line)
	{
		return disassembleModern(line, false);
	}

	// takes a line and returns the NUM value or the mdoern mnemonic
	// marks the line's comment if it's flagged as the current instruction to make it easy to spot on the disassembler
	public static String disassembleModern(int line, boolean isCurrentInstruction)
	{	
		String output = "";
		
		// get integer representation of the line
		String lineValue = Integer.toString(line);
		
		// if any bits other than 0-4 and 13-15 are set then decide it's a NUM
		if( (line & (~0x0000E01F)) != 0)
		{
			output = "NUM " + lineValue;
		}
		else
		{
			output = disassembleModernMnemonic(line); 
		}
		
		// format of line is
		// mmm nn             ; alternative 
		// so pad with spaces to 20 chars long
		while(output.length() < 20)
		{
			output += " ";
		}
		
		// add alternative value
		if(isCurrentInstruction)
		{
			output += ";* ";
		}
		else
		{
			output += ";  ";
		}

		// add hex value of the line
		// Useful as can often see the raw instruction in the 3 bits 13-15 (i.e. 4th nibble)
		// and the line the instruction references in bits 0-4 (1st & 2nd nibble)
		// and teaches the hex-binary relationship and emphasizes that the bit order on the store's
		// display is unusual in being least signiticant bit on the left.
		output += String.format("%08x", line) + " : ";
		
		// if any bits other than 0-4 and 13-15 are set then decide it's a NUM
		if( (line & (~0x0000E01F)) != 0)
		{
			output += disassembleModernMnemonic(line);
		}
		else
		{
			output += lineValue;
		}
		
		return output;
	}
	
	// takes a line value and returns the modern disassembled representation
	public static String disassembleModernMnemonic(int line)
	{
				
		// get function name
		String functionName = "";
		switch( (line >> 13) & 0x07 )
		{
			case 0 : functionName = "JMP"; break;
			case 1 : functionName = "JRP"; break;
			case 2 : functionName = "LDN"; break;
			case 3 : functionName = "STO"; break;
			case 4 : functionName = "SUB"; break;
			case 5 : functionName = "SUB"; break;
			case 6 : functionName = "CMP"; break;
			case 7 : functionName = "STP"; break;
		}
		
		// get line acted upon by function
		String actionLine = Integer.toString(line & 0x1f);
				
		// add function name
		String output = functionName + " ";
		
		// add action line if sensible (i.e. CMP and STP do not act on a line)
		if(!functionName.equals("CMP") && !functionName.equals("STP"))
		{
			output += actionLine;
		}
		
		return output;
	}
		
	public static String reverseString(String str)
	{
		// create string buffer of input string
		StringBuffer strBuffer = new StringBuffer(str);
		// reverse string buffer
		strBuffer.reverse();
		// back to String again
		return strBuffer.toString();
	}
	
	// convert an int into a normal (LSB on the right) binary string
	public static String getBinaryString(int value)
	{
		String output = "";
		// for each bit in that number
		for(int bit = 31; bit>=0; bit--)
		{
			// get ms-bit first 
			output += "" + ((value >> bit) & 1);
		}
		return output;
	}
	
	// as Integer.parseInt(x, 2) can't cope with negatives need this method
	// in normal (LSB on the right) format
	private static int parseBinaryString(String s) throws NumberFormatException
	{
		int result = 0;
		
		if(s.length() > 32)
			throw new NumberFormatException("Binary string longer than 32 bits");
		
		// for each char from the ls-bit on the far right
		for(int i = s.length()-1; i>=0; i--)
		{
			char bitChar = s.charAt(i);
			
			if(bitChar == '1')
			{
				// get value of this bit
				int bitValue = 1 << ((s.length() - 1) - i);
				// if on first bit
				if(i == 0)
				{
					result -= bitValue;
				}
				else
				{
					result += bitValue;
				}
			}
			else
			{
				if(bitChar != '0')
				{
					throw new NumberFormatException("Non-binary digit in string");
				}
			}	
		}
		return result;
	}	
}


class SnapshotException extends Exception
{
	public SnapshotException()
	{
		super();
	}
	
	public SnapshotException(String str)
	{
		super( str );
	}
}

class AssemblyException extends Exception
{
	public AssemblyException()
	{
		super();
	}
	
	public AssemblyException(String str)
	{
		super( str );
	}
}