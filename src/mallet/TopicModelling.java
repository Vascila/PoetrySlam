package mallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class TopicModelling {

	private static final Logger log = Logger.getLogger( TopicModelling.class.getName() );
	/** the delimiter for csv files */
	public static final String CSV_DEL = ";"; 
	public static final String newline = "\n";
	public static final String DEFAULT_INPUT_DIR = "InputText/allPoems.txt"; 
	public static final String DEFAULT_OUTPUT_DIR = "output"; 
	public static final String DEFAULT_NUM_TOPICS = "15";
	
	public static final String[] DEFAULT_STOPWORDS = {"--remove-stopwords", "true"};
	public static final String[] DEFAULT_PRESERVE_CASE = {"--preserve-case", "false"};
	public static final String[] DEFAULT_NUM_ITER = {"--num-iterations", "200"};
	public static final String[] DEFAULT_NUM_TOP_WORDS = {"--num-top-words", "8"};
	public static final String[] DEFAULT_DOC_TOPICS_THRESH = {"--doc-topics-threshold", "0.00"};
	public static final String[] DEFAULT_STOPLIST_FILE = {"--stoplist-file", "import"};
	
	public static final String[] EXTRA_FILES = {"topic-input.mallet","output_topic_keys","output_state.gz",
			"output_doc_topics.txt","output_state"};
	
	
	public static void main(String[] args) {
		runMallet();
	}
	
	/**
	 * Method that runs MALLET
	 * Produces csv files within the output/output_csv directory
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void runMallet() {
		
		String collectionPath = new File(DEFAULT_OUTPUT_DIR, "topic-input.mallet").getPath();
        
        log.info("Importing and Training...this may take a few minutes depending on collection size.");
        log.info("Importing from: " + DEFAULT_INPUT_DIR + "." + newline);

        try {
        	// Reflect to MALLET class that holds main
			Class c = Class.forName("cc.mallet.classify.tui.Csv2Vectors");
        	
			// Block for setting all flags
        	ArrayList<String> imp = new ArrayList<String>();
           	ArrayList<String> trn = new ArrayList<String>();
           	Collections.addAll(imp, DEFAULT_STOPWORDS);
           	Collections.addAll(imp, DEFAULT_PRESERVE_CASE);
        	Collections.addAll(trn, DEFAULT_NUM_ITER);
        	Collections.addAll(trn, DEFAULT_NUM_TOP_WORDS);
        	Collections.addAll(trn, DEFAULT_DOC_TOPICS_THRESH);
            String[] temp = {"--input", DEFAULT_INPUT_DIR, "--output", collectionPath, "--keep-sequence"};
            Collections.addAll(imp, temp);
            String[] fullImportArgs = imp.toArray(new String[imp.size()]);
            
            
            // Now invoke the method.
            Class[] argTypes =  {fullImportArgs.getClass(),}; // array is Object!
			Method m = c.getMethod("main", argTypes);
            Object[] passedArgs =  {fullImportArgs};
            
            log.info("Importing...");
            m.invoke(null, passedArgs);
            
            
            // Prepare all directories
            String stateFile = DEFAULT_OUTPUT_DIR + File.separator + "output_state.gz";
            String outputDocTopicsFile = DEFAULT_OUTPUT_DIR + File.separator +"output_doc_topics.txt";
            String topicKeysFile = DEFAULT_OUTPUT_DIR + File.separator + "output_topic_keys";
            
            
            // Reflect to second MALLET class holding main
            c = Class.forName("cc.mallet.topics.tui.Vectors2Topics");
            		
            String[] temp2 = {"--input", collectionPath,"--num-topics", DEFAULT_NUM_TOPICS,
            		"--output-state",stateFile,"--output-topic-keys",topicKeysFile,"--output-doc-topics",outputDocTopicsFile};
            Collections.addAll(trn, temp2);
            String[] fullTrainArgs = trn.toArray(new String[trn.size()]);
            
            
            // Invoke main
            argTypes = new Class[]{fullTrainArgs.getClass(),}; // array is Object!
            m = c.getMethod("main", argTypes);
            passedArgs =  new  Object[]{fullTrainArgs};
            m.invoke(null, passedArgs);
            
            
            // Handle all output files 
            GunZipper g = new GunZipper(new File(stateFile));
            g.unzip(new File(DEFAULT_OUTPUT_DIR + File.separator + "output_state"));
            outputCsvFiles(DEFAULT_OUTPUT_DIR);
	     }
	     catch (Throwable e1) {
	            e1.printStackTrace();
	     }   
      
	 }
      
 	

	/**
	 * Converts MALLET output files into csv files
	 *
	 * @param directory
	 *            
	 * @throws FileNotFoundException
	 */
	private static void outputCsvFiles(String outputDir) throws FileNotFoundException
	{	
	 CsvBuilder cb = new CsvBuilder();
	 cb.createCsvFiles(Integer.parseInt(DEFAULT_NUM_TOPICS), outputDir);
	 clearExtrafiles(outputDir);
	}

	
	/**
	 * Cleans up all unnecessary MALLET files after work complete
	 * 
	 * @param directory
	 */
	private static void clearExtrafiles(String outputDir)
 	{
 		String[] fileNames = EXTRA_FILES;
 		for(String f:fileNames)
 		{
 			new File(outputDir,f).delete();
 		}
 	}
	
}
