/******************************************************************************
* Runs the GO Slimmer program.                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/

package main;

public class Main
{
	//Get the GOSlimmer instance
	private static GOSlimmer ea = GOSlimmer.getInstance();
	
	private static String goFile = "data/go_sub.obo";
	private static String goSlimFile = "data/goslim_sub.obo";
	private static String annotFile = "data/annot_sub.txt";
	private static String slimAnnotFile = "data/annot_slim_sub.txt";
	
	public static void main(String[] args)
	{
		//Process the arguments
		processArgs(args);
		//Verify the arguments
		verifyArgs();
		
		ea.openOntology(goFile);
		ea.openSlimOntology(goSlimFile);
		ea.openAnnotationSet(annotFile);
		ea.runConversion();
		ea.saveSlimAnnotations(slimAnnotFile);
		ea.exit();
	}
	
	private static void exitHelp()
	{
		System.out.println("GOSlimmer converts a set of annotations from GO to a given GOslim version\n");
		System.out.println("Usage: 'java -jar GOSlimmer.jar OPTIONS'\n");
		System.out.println("Options:");
		System.out.println("-g, --go FILE_PATH\tPath to the full Gene Ontology OBO or OWL file");
		System.out.println("-s, --slim FILE_PATH\tPath to the GOslim OBO or OWL file");
		System.out.println("-a, --annotation FILE_PATH\tPath to the tabular annotation file (GAF, BLAST2GO or 2-column table format");
		System.out.println("-o, --output FILE_PATH\tPath to the output GOslim annotation file]");
		System.exit(0);		
	}

	private static void exitError()
	{
		System.err.println("Type 'java -jar GOEnrichment.jar -h' for details on how to run the program.");
		System.exit(1);		
	}
	
	private static void processArgs(String[] args)
	{
		//Process the arguments
		for(int i = 0; i < args.length; i++)
		{
			if((args[i].equalsIgnoreCase("-g") || args[i].equalsIgnoreCase("--go")) &&
					i < args.length-1)
			{
				goFile = args[++i];
			}
			else if((args[i].equalsIgnoreCase("-s") || args[i].equalsIgnoreCase("--slim")) &&
					i < args.length-1)
			{
				goSlimFile = args[++i];
			}
			else if((args[i].equalsIgnoreCase("-a") || args[i].equalsIgnoreCase("--annotation")) &&
					i < args.length-1)
			{
				annotFile = args[++i];
			}
			else if((args[i].equalsIgnoreCase("-o") || args[i].equalsIgnoreCase("--output")) &&
					i < args.length-1)
			{
				slimAnnotFile = args[++i];
			}
			else if(args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--help"))
			{
				exitHelp();
			}
		}
	}
	
	//Checks that all mandatory parameters were entered so that the program can proceed
	private static void verifyArgs()
	{
		if(goFile == null)
		{
			System.err.println("Error: you must specify an input GO file.");
			exitError();
		}
		if(annotFile == null)
		{
			System.err.println("Error: you must specify an input annotation file.");
			exitError();
		}
		if(goSlimFile == null)
		{
			System.err.println("Error: you must specify an input GOslim file.");
			exitError();
		}
		if(slimAnnotFile == null)
		{
			System.err.println("Error: you must specify an output file.");
			exitError();
		}
	}
}