package com.manchesterbaby.baby.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.manchesterbaby.baby.controller.CrtControlPanelController;
import com.manchesterbaby.baby.event.FileLoadedListener;
import com.manchesterbaby.baby.utils.AppSettings;
import com.manchesterbaby.baby.utils.MiscUtils;
import com.manchesterbaby.baby.utils.RecentFilesManager;

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
	
	private final List<FileLoadedListener> fileLoadedListeners = new ArrayList<>();

	/**
	 * Add a listener to be notified when a file is loaded
	 * @param listener the listener to add
	 */
	public void addFileLoadedListener(FileLoadedListener listener) {
		if (!fileLoadedListeners.contains(listener)) {
			fileLoadedListeners.add(listener);
		}
	}

	/**
	 * Remove a file loaded listener
	 * @param listener the listener to remove
	 */
	public void removeFileLoadedListener(FileLoadedListener listener) {
		fileLoadedListeners.remove(listener);
	}

	/**
	 * Notify all listeners that a file has been loaded
	 */
	private void notifyFileLoaded() {
		for (FileLoadedListener listener : fileLoadedListeners) {
			listener.onFileLoaded();
		}
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
	
	
	// load a snapshot format image into the store
	public void loadSnapshot(String fileName) throws IOException {
		doLoadAssemblySnapshot(fileName, "filePath", "snapshot");
    }

    /** used for loading built-in examples from the JAR */
    public void loadLocalSnapshot(String fileName) throws IOException {
		doLoadAssemblySnapshot(fileName, "URL", "snapshot");
    }

    private void processSnapshot(String fileName, String loadMethod) throws IOException {
		
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
      
        // reset the store to empty
        reset();

		// if already STP'd then reset stop/run switch to stop ready to start running next program
		if(control.getStopFlag())
		{
			// set to Stop
			crtControlPanelController.setStopRun(false);
		}	

		// resets Stop flag amongst other things
		control.reset();

        // Add to recent files
        recentFilesManager.addRecentFile(fileName, "snapshot:" + loadMethod);
           
        int lineCounter = 0;

		boolean firstNonBlankLine = true;
        
        // while more lines to read
        while(in.ready()) {
            lineCounter++;
            
            int lineNumber;
            int lineData;
            
            // read line from file, trailing spaces removed
            String fileLine = (in.readLine()).trim();
            

            // if line isn't blank or a comment
            if( !fileLine.equals("") && !(fileLine.charAt(0) == ';') ) {

				// handle first non-blank/non-comment line being number of lines in file
				if(firstNonBlankLine && fileLine.matches("\\d+"))
				{
					firstNonBlankLine = false;
					continue; // skip to next line if it's just the number of lines at start of the file, we don't need it anyway
				}

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
				if(AppSettings.getInstance().isInteractiveLoading())
					crtControlPanelController.setLine(lineNumber, lineData);
				else
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
        doLoadAssemblySnapshot(fileName, "filePath", "assembly");
    }

	public void loadLocalModernAssembly(String fileName) throws IOException {
		doLoadAssemblySnapshot(fileName, "URL", "assembly");
	}

	// handle loading files whether SNP or ASM from JAR or file system
    public void doLoadAssemblySnapshot(String fileName, String loadMethod, String fileType) throws IOException {

		// moved to background thread so that can experiment with interactively having CrtControlPanelController
		// press individual buttons to load the program

		// TODO: all the crtControlPanelController.* calls should be moved out of the Store class
		// maybe all the file IO stuff.

		if(AppSettings.getInstance().isInteractiveLoading())
		{
		    // while we're operating in this mode then switch to MAN so highlighted action line shows line being programmed
            crtControlPanelController.setManAuto(false);

			// set to write bits when typewriter pressed 
			crtControlPanelController.setWriteErase(false);

			new Thread(() -> {
				try {
					Thread.currentThread().setName("Interactive Loading");
					if(fileType.equals("assembly"))
						processModernAssembly(fileName, loadMethod);
					else if(fileType.equals("snapshot"))
						processSnapshot(fileName, loadMethod);
					else
						throw new IOException("File type not recognised: " + fileType);
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
				notifyFileLoaded();
			}).start();
		}
		else
		{
			try {
				if(fileType.equals("assembly"))
					processModernAssembly(fileName, loadMethod);
				else if(fileType.equals("snapshot"))
					processSnapshot(fileName, loadMethod);
				else
					throw new IOException("File type not recognised: " + fileType);
			} catch(IOException e) {
				System.err.println("Error loading assembly file: " + e.getMessage());
			}
			notifyFileLoaded();
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

		// if already STP'd then reset stop/run switch to stop ready to start running next program
		if(control.getStopFlag())
		{
			// set to Stop
			crtControlPanelController.setStopRun(false);
		}	

		// this resets Stop flag too
		control.reset();

        // Add to recent files
        recentFilesManager.addRecentFile(fileName, "assembly:" + loadMethod);
           
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
						
						try
						{
							operandValue = parseStringToNumber(operandValueS);
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

	private int parseStringToNumber(String numberString) throws NumberFormatException
	{
		// Example number formats this can parse:
		// 0b01011011011 : binary
		// L0b10010101 : LSB first binary (same as Baby CRT store)
		// 0xABC5 or ABC5h : hexadecimal
		// 1234 -1234 : decimal

		int number;

		// check for Least-Significant-Bit-first encoding (to match baby CRT Store bit order)
		if(numberString.startsWith("L0b"))
		{
			number = parseBinaryString(reverseString(numberString.substring(3)));
		}
		else if(numberString.startsWith("0x"))
		{ 
			// try hex (parseLong otherwise can't handle bit 31 being set)
			number = (int)Long.parseLong(numberString.substring(2), 16);
		}
		else if(numberString.startsWith("0b"))
		{
			// try binary
			number = parseBinaryString(numberString.substring(2));
		}
		// No prefixes found so check suffixes...
		else if(numberString.endsWith("h"))
		{
			// try hex (parseLong otherwise can't handle bit 31 being set)
			number = (int)Long.parseLong(numberString.substring(0, numberString.length()-1), 16);
		}
		else
		{
			// try decimal
			number = Integer.parseInt(numberString);
		}
		
		return number;
		
	}
	
	public String disassembleModern(int line)
	{
		return disassembleModern(line, false, true);
	}

	public String disassembleModern(int line, boolean isNextInstruction)
	{
		return disassembleModern(line, isNextInstruction, true);
	}

	private String formatNUMoperand(int line)
	{
		String format = AppSettings.getInstance().getNumDissFormat();

		String value;

		// pad binary formats with an extra space so long binary string separated from the semi colon that starts the comment

		switch(format) {
			case "bin" :
				value = "0b" + getBinaryString(line) + " ";
				break;
			case "hex" :
				value = "0x" + String.format("%08x", line);
				break;
			case "lsbbin" :
				value = "L0b" + reverseString(getBinaryString(line)) + " ";
				break;
			default : // also catch for "dec"
				value = Integer.toString(line);
		}

		return value;
	}

	// takes a line and returns the NUM value or the mdoern mnemonic
	// marks the line's comment if it's flagged as the next instruction to make it easy to spot on the disassembler
	public String disassembleModern(int line, boolean isNextInstruction, boolean useNUMpseudoInstruction)
	{	
		String output = "";
		
		// get integer representation of the line
		String lineValueDec = Integer.toString(line);
		
		// if any bits other than 0-4 and 13-15 are set then decide it's a NUM
		if( (line & (~0x0000E01F)) != 0 && useNUMpseudoInstruction)
		{
			output = "NUM " + formatNUMoperand(line);
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
		output += getHexString(line) + " : ";
		
		// if any bits other than 0-4 and 13-15 are set then decide it's a NUM
		if( (line & (~0x0000E01F)) != 0 && useNUMpseudoInstruction)
		{
			output += disassembleModernMnemonic(line);
		}
		else
		{
			output += lineValueDec;
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

	public static String getHexString(int value)
	{
		return String.format("%08x", value);
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
	

	/**
	 * Takes a program from a URL param and attempts to load it into the store
	 * @param program The program to load in whatever encoding we get
	 */
	public void loadFromURLparam(String program)
	{
		
		// TODO: add support for a run-lengh compressed encoding
		// start with a bit map with 1 bit for each line to denote whether for that line to store
		// only bits 0-4 and 13-15 (a v common pattern which avoids lots of zeroes and only takes 8 bits rather than 32 -  sadly 2 base64
		// characters unless can do a bitstream rather than a bytestream) OR whether all 32 bits should be represented 
		// for that line. Should reduce number of bits needed by 75% for probably 60% of program lines for many progs. If true would reduce from 1088 (inc CI & ACC)->631 bits.
		// actually may be able to just use byte buffer as will always write just 8 bits, or 32 bits and let base64 encoding do the rest.

		// assume base64url encoding for now
		this.fromCompressedBase64url(program);

	}

	public String saveToURLparam()
	{
		return this.toCompressedBase64url();
	}

	/**
	 * Encodes the store's line array into a compressed thenbase64url encoded string
	 * @return A base64url compressed encoded string representing the store's contents
	 */
	public String toCompressedBase64url() {
		// Create a ByteBuffer to hold all the store line integers (4 bytes each)
		// add 1*4 so can store Accumulator registers so programs can be copied mid-execution
		// add 1*4 so can store bitmap to represent line compression map across the store
		// add 1 byte at the end for the CI register that can only be 5 bits.

		ByteBuffer buffer = ByteBuffer.allocate(((line.length+1+1) * 4)+1);

		int bytesWritten = 0;
		
		// clear the map of store lines that can do 8 bit representation versus needing full 32 bit representation.
		int storeEncodingBitmap = 0;
		buffer.putInt(storeEncodingBitmap); // push this in at start to make sure can read it back first when decoding
		bytesWritten += 4;

		// Write all store lines to the buffer
		for(int lineCount=0; lineCount < line.length; lineCount++)
		{
			int value = line[lineCount];
			// if some bits other than 0-4 and 13-15 are set so store the whole 32 bit line
			if((value & ~0x0000E01F) != 0)
			{
				buffer.putInt(value);
				bytesWritten += 4;
				// don't need to update storeEncodingBitmap as 0 will represent uncompressed line.
			}
			else
			{
				// only store bits 0-4 and 13-15 in a single byte
				byte compressedLineValue = (byte)((value & 0x1F) | ((value & 0xE000) >> 8));
				buffer.put(compressedLineValue);
				bytesWritten += 1;
				storeEncodingBitmap |= 1 << lineCount; // set the bit map for this line to show compressed to 8 bits
			}
		}

		// now go back to the start of the buffer and poke the updated storeEncodingBitmap over those first 4 bytes
		// so it's available for decoding the store lines when reading back
		buffer.array()[0] = (byte)(storeEncodingBitmap >> 24);
		buffer.array()[1] = (byte)(storeEncodingBitmap >> 16);
		buffer.array()[2] = (byte)(storeEncodingBitmap >> 8);
		buffer.array()[3] = (byte)storeEncodingBitmap;
		// no need to increment bytesWritten as already done at start

		int accumulator = control.getAccumulator();

		// only need bits 0-4 in this byte for CI so use bit 5 to represent whether to compress the accumulator too
		byte compCI = (byte)control.getControlInstruction();
		
		// if some bits other than 0-4 and 13-15 are set so store the whole 32 bit accumulator
		if((accumulator & ~0x0000E01F) != 0)
		{
			// don't need to set bit in compCI as 0 will represent uncompressed accumulator
			buffer.put(compCI);
			bytesWritten += 1;

			buffer.putInt(accumulator);
			bytesWritten += 4;
		}
		else
		{
			compCI |= 0x20; // set bit 5 to show compressed accumulator
			buffer.put(compCI);
			bytesWritten += 1;

			byte compressedAccumulator = (byte)((accumulator & 0x1F) | ((accumulator & 0xE000) >> 8));
			buffer.put(compressedAccumulator);
			bytesWritten += 1;
		}		
		
		// can't resize ByteBuffer so create a new byte array of correct size
		byte[] byteArray = new byte[bytesWritten];
		((ByteBuffer)buffer).rewind(); // occasionally seeing NoSuchMethodError in dev on later than Java 8 JDKs so explicitly cast to for the right class support in Java 8
		buffer.get(byteArray);

		// Convert to base64url
		return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(byteArray);
	}

	/**
	 * Decodes a compressed base64url encoded string and loads it into the store's line array
	 * Sidenote, this method including comments was 100% generated by Codeium Windsurf just by prompting it to:
	 *  'create a fromCompressedBase64url method in store that takes a string and reverses the compression operations performed by toCompresedBase64url'
	 * AI is magical.
	 * @param base64 The base64url encoded string to decode
	 */
	public void fromCompressedBase64url(String base64) {
		// Decode base64url string to byte array
		byte[] bytes = java.util.Base64.getUrlDecoder().decode(base64);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		// Read the compression bitmap first (it's at the start of the buffer)
		int storeEncodingBitmap = buffer.getInt();
		
		// Read store lines using the bitmap to determine format
		for(int lineCount = 0; lineCount < line.length; lineCount++) {
			if((storeEncodingBitmap & (1 << lineCount)) != 0) {
				// This line is compressed to 8 bits
				byte compressedValue = buffer.get();
				
				// Extract bits 0-4 and 13-15 from compressed format
				int value = (compressedValue & 0x1F) | ((compressedValue & 0xE0) << 8);
				line[lineCount] = value;
			} else {
				// This line is stored as full 32 bits
				line[lineCount] = buffer.getInt();
			}
		}
		
		// Read CI register and check if accumulator is compressed
		byte compCI = buffer.get();
		control.setControlInstruction((byte)(compCI & 0x1F)); // Only use bits 0-4 for CI
		
		// Check bit 5 to see if accumulator is compressed
		if((compCI & 0x20) != 0) {
			// Accumulator is compressed to 8 bits
			byte compressedAccumulator = buffer.get();
			// Extract bits 0-4 and 13-15 from compressed format
			int accumulator = (compressedAccumulator & 0x1F) | ((compressedAccumulator & 0xE0) << 8);
			control.setAccumulator(accumulator);
		} else {
			// Accumulator is stored as full 32 bits
			control.setAccumulator(buffer.getInt());
		}
	}

	/**
	 * Encodes the store's line array into a base64url encoded string
	 * @return A base64url encoded string representing the store's contents
	 */
	public String toBase64url() {
		// Create a ByteBuffer to hold all the integers (4 bytes each)
		// add 1*4 so can store Accumulator registers so programs can be copied mid-execution
		// then add 1 byte at the end for the CI register that can only be 5 bits.

		ByteBuffer buffer = ByteBuffer.allocate(((line.length+1) * 4)+1);
		
		// Write all integers to the buffer
		for (int value : line) {
			buffer.putInt(value);
		}
		
		// Write Accumulator and CI registers
		buffer.putInt(control.getAccumulator());
		buffer.put((byte)control.getControlInstruction());
		
		// Convert to base64url
		return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
	}
	
	/**
	 * Decodes a base64url encoded string and loads it into the store's line array
	 * @param base64 The base64url encoded string to decode
	 * @throws IllegalArgumentException if the input string is invalid or doesn't contain the correct number of integers
	 */
	public void fromBase64url(String base64) {
		// Decode the base64url string
		byte[] bytes = java.util.Base64.getUrlDecoder().decode(base64);
		
		// Check if the byte array length is valid (must be multiple of 4 since each int is 4 bytes)
		// add 2 for Accumulator and CI registers
		if (bytes.length % 4 != 0 || bytes.length != (line.length + 2) * 4) {
			throw new IllegalArgumentException("Invalid base64url string length");
		}
		
		// Create a ByteBuffer to read the integers
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		// Read integers into the line array
		for (int i = 0; i < line.length; i++) {
			line[i] = buffer.getInt();
			isLineAltered[i] = true;
		}
		
		// Read Accumulator and CI registers
		control.setAccumulator(buffer.getInt());
		control.setControlInstruction(buffer.get());
	}

	
}
