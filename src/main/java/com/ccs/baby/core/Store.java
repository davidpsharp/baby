package com.ccs.baby.core;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ccs.baby.utils.AppSettings;
import com.ccs.baby.utils.MiscUtils;
import com.ccs.baby.utils.RecentFilesManager;
import com.ccs.baby.controller.CrtControlPanelController;

public class Store
{
	

	// actual data in the store
	private int line[];

	private Control control;
	
	private CrtControlPanelController crtControlPanelController; // the code that interacts with this for loading should probably be moved out to a separate class
	
	// array showing whether the line has changed since the display was last updated
	public boolean isLineAltered[];
	
	// values for the different types of file that might be detected
	public static final int UNACCEPTABLE = 0;
	public static final int SNAPSHOT = 1;
	public static final int ASSEMBLY = 2;
	
	// Manager for tracking recently loaded files
    private RecentFilesManager recentFilesManager;

    public RecentFilesManager getRecentFilesManager() {
        return recentFilesManager;
    }
	
	public Store()
	{
		// set up default data
		line = new int[32];
		isLineAltered = new boolean[32];
		
		for(int x = 0; x<32; x++)
		{
			isLineAltered[x] = true;
		}
		
		// setup recent files manager
        recentFilesManager = RecentFilesManager.getInstance();
	}

	public void setControl(Control aControl)
	{
		control = aControl;
	}

	public void setCrtControlPanelController(CrtControlPanelController controller)
	{
		crtControlPanelController = controller;
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
	public int getFileType(String fileName) throws IOException {
		File snapshotFile = new File(fileName);
		
		try (FileReader snapshotReader = new FileReader(snapshotFile);
			 BufferedReader in = new BufferedReader(snapshotReader)) {
			
			// check for a number of lines value on the first non-comment line
			int numberOfLines = -1;
			// while lines to read and we haven't yet read the number of lines value
			while (in.ready() && (numberOfLines == -1)) {
				// read number of lines in snapshot
				// trim whitespace
				String numberOfLinesS = (in.readLine()).trim();
				
				// if the line isn't empty
				if (!numberOfLinesS.equals("")) {
					// if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
					if (numberOfLinesS.charAt(0) != ';') {
						try {
							numberOfLines = Integer.parseInt(numberOfLinesS);
						} catch (NumberFormatException e) {
							return UNACCEPTABLE;
						}
					}
				}
			}
			if (numberOfLines == -1)
				return UNACCEPTABLE;
			
			String nextLine = in.readLine();
			if (nextLine.indexOf(':') == -1)
				return ASSEMBLY;
			else
				return SNAPSHOT;
		} catch (FileNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	// load a snapshot format image into the store
	public void loadSnapshot(String fileName) throws IOException {
        // setup file
        File snapshotFile = new File(fileName);
        
        // open snapshot file and process it
        try (FileReader snapshotReader = new FileReader(snapshotFile)) {
            processSnapshot(new BufferedReader(snapshotReader), fileName, "loadSnapshot");
        } catch(FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** used for loading built-in examples from the JAR */
    public void loadLocalSnapshot(String fileName) throws IOException {
        // open snapshot file and process it
        try (InputStream snapshotReader = openFile(fileName)) {
            processSnapshot(new BufferedReader(new InputStreamReader(snapshotReader)), fileName, "loadLocalSnapshot");
        } catch(FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void processSnapshot(BufferedReader in, String fileName, String loadMethod) throws IOException {
        // read number of lines in snapshot
        String numberOfLinesS = (in.readLine()).trim();
        try {
            int numberOfLines = Integer.parseInt(numberOfLinesS);
        } catch(NumberFormatException e) {
            throw new IOException("File " + fileName + " is of an unrecognised format");
        }
        
        // reset the store to empty
        reset();
		control.reset();
        
        // Add to recent files
        recentFilesManager.addRecentFile(fileName, loadMethod);
           
        int lineCounter = 0;
        
        // while more lines to read
        while(in.ready()) {
            lineCounter++;
            
            int lineNumber;
            int lineData;
            
            // read line from file, trailing spaces removed
            String fileLine = (in.readLine()).trim();
            
            // if line isn't blank
            if(!fileLine.equals("")) {
                // create a new tokenizer, tokenizing on colons
                StringTokenizer tokenizer = new StringTokenizer(fileLine, ":");
                
                // get store line number
                if(tokenizer.hasMoreElements()) {
                    // get the first token on the line
                    String lineNumberS = tokenizer.nextToken();
                
                    // convert string to int
                    try {
                        lineNumber = Integer.parseInt(lineNumberS);
                    } catch(NumberFormatException e) {
                        throw new IOException("File " + fileName + " has a bad store line number at line " + lineCounter + " in the file");
                    }
                } else {
                    throw new IOException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
                }
                
                // get binary information for store line
                if(tokenizer.hasMoreElements()) {
                    // get the second token on the line
                    String lineDataS = tokenizer.nextToken();
                    
                    // reverse binary string (for SSEM's reverse binary representation)
                    lineDataS = reverseString(lineDataS);
                    
                    // convert string to int
                    try {
                        // radix (i.e. base) 2 or binary
                        lineData = parseBinaryString(lineDataS);
                    } catch(NumberFormatException e) {
                        throw new IOException("File " + fileName + " has bad store line data at line " + lineCounter + " in the file");
                    }                    
                } else {
                    throw new IOException("File " + fileName + " is of an unrecognised format at line " + lineCounter + " in the file");
                }
                                
                // put data for this line into the store
                setLine(lineNumber, lineData);
            }                
        }        
    }
	
	/**
	 * Gets an InputStream for various flavours of URI e.g.
	 * JAR resources: jar:file:/path/to/app.jar!/demos/program.asm
	 * ZIP files: jar:file:/path/to/baby_programs.zip!/folder/program.asm
	 * Regular files: file:/path/to/program.asm or just /path/to/program.asm
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public InputStream openFile(String fileName) throws IOException {
		try {
			URI uri = new URI(fileName);
			String scheme = uri.getScheme();
			
			// If it's a resource in a JAR file
			if ("jar".equals(scheme) && fileName.contains(".jar!")) {
				return Store.class.getResourceAsStream(
					fileName.substring(fileName.indexOf("!") + 1)
				);
			}
			// If it's a file in a ZIP
			else if ("jar".equals(scheme) && fileName.contains(".zip!")) {
				// Format should be: jar:file:/path/to/file.zip!/path/inside/zip
				String[] parts = uri.getSchemeSpecificPart().split("!");
				if (parts.length != 2) {
					throw new IOException("Invalid ZIP URI format: " + fileName);
				}
				
				// Get the path to the ZIP file
				Path zipPath = Paths.get(URI.create(parts[0]));
				String entryPath = parts[1];
				if (entryPath.startsWith("/")) {
					entryPath = entryPath.substring(1); // Remove leading slash
				}
				
				// Open the ZIP file and get the entry
				ZipFile zipFile = new ZipFile(zipPath.toFile());
				ZipEntry entry = zipFile.getEntry(entryPath);
				if (entry == null) {
					throw new IOException("ZIP entry not found: " + entryPath);
				}
				
				// Return the input stream for the entry
				return zipFile.getInputStream(entry);
			}
			// Regular file URI
			else if ("file".equals(scheme) || scheme == null) {
				Path path = Paths.get(uri);
				return Files.newInputStream(path);
			}
			// Unknown URI scheme
			else {
				throw new IOException("Unsupported URI scheme: " + scheme);
			}
		}
		catch (URISyntaxException ex) {
			// If it's not a valid URI, try as a direct file path
			System.err.println("Not a valid URI, trying as direct file path: " + ex.toString());
			return new FileInputStream(fileName);
		}
	}



	// load a modern assembly file into the store, mainly file input issues
	public void loadModernAssembly(String fileName) throws IOException {
        doLoadModernAssembly(fileName, "filePath");
    }

	public void loadLocalModernAssembly(String fileName) throws IOException {
		doLoadModernAssembly(fileName, "URL");
	}

	/** used for loading built-in examples from the JAR */
    public void doLoadModernAssembly(String fileName, String loadMethod) throws IOException {

		// moved to background thread so that can experiment with interactively having CrtControlPanelController
		// press individual buttons to load the program

		if(AppSettings.getInstance().isInteractiveLoading())
		{
		    // while we're operating in this mode then switch to MAN so highlighted action line shows line being programmed
            crtControlPanelController.setManAuto(false);

			new Thread(() -> {
				try {
					Thread.currentThread().setName("Interactive Loading");
					processModernAssembly(fileName, loadMethod);
				}
				catch(IOException e) {
					System.err.println("Error loading assembly file: " + e.getMessage());
				}
				catch(Exception e) {
					System.err.println("Error unhandled: " + MiscUtils.getStackTrace(e));
				}
				
				// revert to auto mode ready to run
				crtControlPanelController.setManAuto(true);

				// if interactive loading was turned off while the thread was running then need to...
				if(!AppSettings.getInstance().isInteractiveLoading())
				{
					// make sure the line switches are all set down otherwise program unlikely to run, will have been
					// left in some hodgepodge state part way through interactive loading
					crtControlPanelController.setAllLineSwitchesDown();

					// and redraw the CRT panel as the panel will need redrawing
					crtControlPanelController.redrawCrtPanel();
				}
			}).start();
		}
		else
		{
			try {
				processModernAssembly(fileName, loadMethod);
			} catch(IOException e) {
				System.err.println("Error loading assembly file: " + e.getMessage());
			}
		}
    }

    private void processModernAssembly(String fileName, String loadMethod) throws IOException {
        int numberOfLines = -1;
		BufferedReader in;

		if(loadMethod == "URL")
		{
			// load URL from JAR, Zip etc. (typically an Example program)
			InputStream assemblyReader = openFile(fileName);
			in = new BufferedReader(new InputStreamReader(assemblyReader));
		}
		else
		{
			// load plain file path (loadModernAssembly)
			File assemblyFile = new File(fileName);
			FileReader assemblyReader = new FileReader(assemblyFile);
			in = new BufferedReader(assemblyReader);
		}
        
        // while lines to read and we haven't yet read the number of lines value
        while (in.ready() && (numberOfLines == -1)) {        
            // read number of lines in snapshot
            String numberOfLinesS = (in.readLine()).trim();
            
            // if the line isn't empty
            if (!numberOfLinesS.equals("")) {
                // if the line doesn't start with a semi-colon (comment) then it must be the number of lines value
                if (numberOfLinesS.charAt(0) != ';') {
                    try {
                        numberOfLines = Integer.parseInt(numberOfLinesS);
                    } catch(NumberFormatException e) {
                        throw new IOException("File " + fileName + " is of an unrecognised format");
                    }
                }
            }
        }
        
        // reset the store to empty
        reset();
		control.reset();
        
        // Add to recent files
        recentFilesManager.addRecentFile(fileName, loadMethod);
           
        int lineCounter = 1;
                
        // while more lines to read
        while (in.ready()) {
            lineCounter++;
            
            // read line from file, trailing spaces removed
            String fileLine = (in.readLine()).trim();
            
            assembleModernToStore(fileLine, lineCounter);
        }
    }
	
	// actually assemble an individual line of text
	public void assembleModernToStore(String fileLine, int lineCounter) throws IOException
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
					throw new IOException("Bad store line number at line " + lineCounter);
				}
			}
			else
			{
				throw new IOException("Unrecognised format at line " + lineCounter);
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
							throw new IOException("Bad operand at line " + lineCounter);
						}
					}
					else
					{
						throw new IOException("Unrecognised format at line " + lineCounter);
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
						throw new IOException("Bad store line number at line " + lineCounter);
					}
					
					lineData = operandValue;
					lineData |= (functionNumber << 13);
				}
				
				

				if(AppSettings.getInstance().isInteractiveLoading())
					crtControlPanelController.setLine(lineNumber, lineData);
				else
					setLine(lineNumber, lineData);

			// if there was no mnemonic on the line	
			}
			else
			{
				throw new IOException("Unrecognised format at line " + lineCounter);
			}
		
		
		}
	}
	
	public String disassembleModern(int line)
	{
		return disassembleModern(line, false);
	}

	// takes a line and returns the NUM value or the mdoern mnemonic
	// marks the line's comment if it's flagged as the next instruction to make it easy to spot on the disassembler
	public String disassembleModern(int line, boolean isNextInstruction)
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
		if(isNextInstruction)
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

		// append value of the line being read from if an instruction that reads a line value
		int functionNum = Control.getFunctionNumber(line);
		switch(functionNum) {
			case Control.FUNC_JMP :
			case Control.FUNC_JRP :
			case Control.FUNC_LDN :
			case Control.FUNC_SUB :
			case Control.FUNC_SUB5 :
				int actionLine=Control.getLineNumber(line);
				output += " (" + getLine(actionLine) + ")";  // drop through to here
			default :
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

		/*
		// old logic kept temporarily
		
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
		*/

		// Integer.parseInt() can't cope with negatives but parseLong() cast to an int does
		int result = (int)Long.parseLong(s, 2);

		return result;
	}	
}
