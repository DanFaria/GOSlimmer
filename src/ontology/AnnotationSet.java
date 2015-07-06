/******************************************************************************
* A set of gene product <-> GO term annotations, read from an input file and  *
* represented as an 'indexed table'.                                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/

package ontology;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

import main.GOSlimmer;
import util.Table2Set;

public class AnnotationSet
{
	//The annotation map of gene accs -> GO terms
	private Table2Set<String,Integer> annotations;
	private GeneOntology o;
	
	/**
	 * Constructs an empty AnnotationSet
	 */
	public AnnotationSet(GeneOntology o)
	{
		annotations = new Table2Set<String,Integer>();
		this.o = o;
	}
	
	/**
	 * Constructs an AnnotationSet by reading a set of annotations from a file
	 * in one of the recognized "AnnotationFileFormat"s, then extending it
	 * for transitive closure 
	 * @param annotFile: the path to the file containing the annotations
	 * @throws IOException if it cannot open or read the input file
	 */
	public AnnotationSet(String annotFile, GeneOntology o) throws IOException
	{
		annotations = new Table2Set<String,Integer>();
		this.o = o;
		readAnnotationFile(annotFile);
		extendAnnotations();
	}
	
	public void add(String gene, int term)
	{
		annotations.add(gene, term);
	}
	
	public int size()
	{
		return annotations.size();
	}
	
	public void save(String file) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		out.println("Gene\tGOSlim Term");
		for(String gene : annotations.keySet())
			for(int term : annotations.get(gene))
				if(!containsDescendent(gene,term))
					out.println(gene + "\t" + o.getLocalName(term));
		out.close();
	}

	public AnnotationSet slimIt()
	{
		GOSlimmer gs = GOSlimmer.getInstance();
		GeneOntology goSlim = gs.getSlimOntology();
		AnnotationSet slimAnnotations = new AnnotationSet(gs.getSlimOntology());
		for(String gene : annotations.keySet())
		{
			for(int term : annotations.get(gene))
			{
				String name = o.getLocalName(term);
				
				if(goSlim.containsName(name))
					slimAnnotations.add(gene, goSlim.getIndexName(name));
			}
		}
		return slimAnnotations;
	}
	
	private boolean containsDescendent(String gene, int term)
	{
		for(int desc : o.getSubClasses(term, false))
			if(annotations.contains(gene, desc))
				return true;
		return false;
	}
	
	//Extends the AnnotationSet for transitive closure
	private void extendAnnotations()
	{
		//We must store the new annotations in a temporary table in order
		//to avoid concurrent modifications
		Table2Set<String,Integer> tempAnnotations = new Table2Set<String,Integer>();
		for(String gene : annotations.keySet())
		{		
			for(int go : annotations.get(gene))
				for(int ancestor : o.getSuperClasses(go, false))
					tempAnnotations.add(gene, ancestor);
		}
		//Once we have all the new annotations, we can add them to the
		//AnnotationSet tables
		for(String gene : tempAnnotations.keySet())
		{
			for(int go : tempAnnotations.get(gene))
			{
				annotations.add(gene,go);
			}
		}
	}

	//Reads the set of annotations listed in an input file
	private void readAnnotationFile(String annotFile) throws IOException
	{
		//Open the input file or die
		BufferedReader in;
		if(annotFile.endsWith(".gz"))
			in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(annotFile))));
		else
			in = new BufferedReader(new FileReader(annotFile));
		String line = in.readLine();
		//Detect the annotation file format
		AnnotationFileFormat f;
		if(line.startsWith("!"))
		{
			//A GO annotation file should start with a commented section
			//with '!' being the comment sign
			f = AnnotationFileFormat.GAF;
			while(line != null && line.startsWith("!"))
				line = in.readLine();
		}
		else if(line.startsWith("("))
		{
			//A BINGO file should start with an info line which contains
			//information within parenthesis
			f = AnnotationFileFormat.BINGO;
			while(line != null && line.startsWith("("))
				line = in.readLine();
		}
		else
		{
			//Otherwise, we assume we have a tabular file, which may or may
			//not include header information, so we skip lines until a GO
			//term is found
			f = AnnotationFileFormat.TABULAR;
			while(line != null && !line.contains("GO:"))
				line = in.readLine();
		}
		while(line != null)
		{
			String[] values;
			String gene = null, go = null;
			if(f.equals(AnnotationFileFormat.BINGO))
			{
				values = line.split(" = ");
				gene = values[0];
				go = ("GO:" + values[1]);
			}
			else
			{
				values = line.split("\t");
				if(f.equals(AnnotationFileFormat.GAF))
				{
					if(values[3].equalsIgnoreCase("NOT"))
					{
						line = in.readLine();
						continue;
					}
					gene = values[2];
					go = values[4];					
				}
				else
				{
					gene = values[0].trim();
					for(int i = 1; i < values.length; i++)
					{
						if(values[i].trim().startsWith("GO:"))
						{
							go = values[i].trim();
							break;
						}
					}
				}
			}
			line = in.readLine();
			if(!o.containsName(go))
				continue;
			int index = o.getIndexName(go);
			annotations.add(gene,index);
		}
		in.close();
	}
}