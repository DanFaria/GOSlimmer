# GOSlimmer

<b>GOSlimmer is a tool for converting a set of annotations from GO to a given GO slim.</b>

<b>It requires as input:</b>

1) A full Gene Ontology file in either OBO or OWL

2) A slim Gene Ontology file in either OBO or OWL (any GOSlim version)

3) An Annotation file, which can be in GAF format (from the Gene Ontology website),
   BLAST2GO format, or in tabular format (with gene ids in the first column and GO term ids in the second one)

<b>It produces as output:</b>

1) A tabular Slim Annotation file listing all non-redundant GO Slim annotations of gene products in the original Annotation file

<b>The JAR and XML files are setup for the Galaxy platform</b> (https://usegalaxy.org/)
