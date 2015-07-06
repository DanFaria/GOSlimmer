/******************************************************************************
* Singleton class that controls the state of the GOSlimmer program,           *
* by recording all options and holding links to all data structures.          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/

package main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ontology.AnnotationSet;
import ontology.GeneOntology;

public class GOSlimmer
{
	//Singleton pattern: unique instance
	private static GOSlimmer ea = new GOSlimmer();
	
	//- Date format
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//Data Structures:
	//- The Gene Ontologies (full and slim)
	private GeneOntology go;
	private GeneOntology goSlim;
	//- The sets of annotations (full and slim)
	private AnnotationSet as;
	private AnnotationSet slimAs;
	
	private GOSlimmer(){}
	
	public void exit()
	{
		System.exit(0);
	}
	
	public static GOSlimmer getInstance()
	{
		return ea;
	}
	
	public AnnotationSet getAnnotationSet()
	{
		return as;
	}
	
	public GeneOntology getOntology()
	{
		return go;
	}
	
	public GeneOntology getSlimOntology()
	{
		return goSlim;
	}
	
	public void openAnnotationSet(String file)
	{
		try
		{
			System.out.println(df.format(new Date()) + " - Reading annotations from '" + file + "'");
			as = new AnnotationSet(file,go);
			System.out.println(df.format(new Date()) + " - Read " + as.size() + " annotations");
		}
		catch(IOException e)
		{
			System.err.println(df.format(new Date()) + " - Error: could not read annotation set '" + file + "'!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void openOntology(String file)
	{
		try
		{
			System.out.println(df.format(new Date()) + " - Reading ontology from '" + file + "'");
			go = new GeneOntology(file);
			System.out.println(df.format(new Date()) + " - Finished");
		}
		catch(OWLOntologyCreationException e)
		{
			System.err.println(df.format(new Date()) + " - Error: could not read ontology '" + file + "'!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void openSlimOntology(String file)
	{
		try
		{
			System.out.println(df.format(new Date()) + " - Reading ontology from '" + file + "'");
			goSlim = new GeneOntology(file);
			System.out.println(df.format(new Date()) + " - Finished");
		}
		catch(OWLOntologyCreationException e)
		{
			System.err.println(df.format(new Date()) + " - Error: could not read ontology '" + file + "'!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void runConversion()
	{
		System.out.println(df.format(new Date()) + " - Slimming annotations");
		slimAs = as.slimIt();
		System.out.println(df.format(new Date()) + " - Finished");
	}
	
	public void saveSlimAnnotations(String file)
	{
		try
		{
			System.out.println(df.format(new Date()) + " - Saving result file '" + file + "'");
			slimAs.save(file);
			System.out.println(df.format(new Date()) + " - Finished");
		}
		catch(IOException e)
		{
			System.err.println("Error: could not write result file '" + file + "'!");
			e.printStackTrace();
			System.exit(1);			
		}
	}
}