package server.mallet;

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
	public static final String DEFAULT_INPUT_DIR = "OtherText/testLine.txt"; 
	public static final String DEFAULT_OUTPUT_DIR = "output"; 
	public static final String DEFAULT_NUM_TOPICS = "5";
	
	public static final String[] DEFAULT_STOPWORDS = {"--remove-stopwords", "true"};
	public static final String[] DEFAULT_PRESERVE_CASE = {"--preserve-case", "false"};
	public static final String[] DEFAULT_NUM_ITER = {"--num-iterations", "200"};
	public static final String[] DEFAULT_NUM_TOP_WORDS = {"--num-top-words", "10"};
	public static final String[] DEFAULT_DOC_TOPICS_THRESH = {"--doc-topics-threshold", "0.05"};
	public static final String[] DEFAULT_STOPLIST_FILE = {"--stoplist-file", "import"};
	
	public static final String[] EXTRA_FILES = {"topic-input.mallet","output_topic_keys","output_state.gz",
			"output_doc_topics.txt","output_state"};
	
	public static void main(String[] args) {
		runMallet();
	}
	
	public static void runMallet() {
		long start = System.currentTimeMillis(); 
				
		String inputDir = DEFAULT_INPUT_DIR;
		String outputDir = DEFAULT_OUTPUT_DIR;
		
		String collectionPath = new File(outputDir,"topic-input.mallet").getPath();
        
        log.info("Importing and Training...this may take a few minutes depending on collection size.");
        log.info("Importing from: " + inputDir + "." + newline);

        ////////////////
        try {
        	
        	@SuppressWarnings("rawtypes")
			Class c = Class.forName("cc.mallet.classify.tui.Csv2Vectors");
        	       	
        	ArrayList<String> imp = new ArrayList<String>();
           	ArrayList<String> trn = new ArrayList<String>();
           	
           	Collections.addAll(imp, DEFAULT_STOPWORDS);
           	Collections.addAll(imp, DEFAULT_PRESERVE_CASE);
           	
        	Collections.addAll(trn, DEFAULT_NUM_ITER);
        	Collections.addAll(trn, DEFAULT_NUM_TOP_WORDS);
        	Collections.addAll(trn, DEFAULT_DOC_TOPICS_THRESH);
        	
            String[] temp1 = {"--input",inputDir,"--output",collectionPath,"--keep-sequence"};
            
            Collections.addAll(imp, temp1);
            String[] fullImportArgs = imp.toArray(new String[imp.size()]);
                   //System.out.println(passedArgs);
            // Now invoke the method.
            Class[] argTypes =  {fullImportArgs.getClass(),}; // array is Object!
			Method m = c.getMethod("main", argTypes);
            Object[] passedArgs =  {fullImportArgs};
            //System.out.println(Arrays.toString(fullImportArgs));
            
//    		log.info("Importing...");
            m.invoke(null, passedArgs);
            
            
            String stateFile = outputDir+File.separator+"output_state.gz";
            String outputDocTopicsFile = outputDir+File.separator+"output_doc_topics.txt";
            String topicKeysFile = outputDir+File.separator+"output_topic_keys";
            c = Class.forName("cc.mallet.topics.tui.Vectors2Topics");
            		
            String[] temp2 = {"--input", collectionPath,"--num-topics", DEFAULT_NUM_TOPICS,
            		"--output-state",stateFile,"--output-topic-keys",topicKeysFile,"--output-doc-topics",outputDocTopicsFile};
            Collections.addAll(trn, temp2);
            String[] fullTrainArgs = trn.toArray(new String[trn.size()]);
            //System.out.println(passedArgs);
            // Now invoke the method.
            argTypes = new Class[]{fullTrainArgs.getClass(),}; // array is Object!
			m = c.getMethod("main", argTypes);
            passedArgs =  new  Object[]{fullTrainArgs};
//            System.out.println(Arrays.toString(fullTrainArgs));
//            System.out.println("STOP!");
    		
    		
            m.invoke(null, passedArgs);
            
            GunZipper g = new GunZipper(new File(stateFile));
            g.unzip(new File(outputDir+File.separator+"output_state"));
            
            outputCsvFiles(outputDir,true);
            //outputHtmlFiles(outputDir);
            
            
//            System.out.println("Mallet Output files written in " + outputDir + " ---> " + stateFile + " , " +
//            		topicKeysFile + newline);
//            System.out.println("Csv Output files written in " + outputDir + File.separator+ "output_csv");
//            System.out.println("Html Output files written in " + outputDir + File.separator+ "output_html");
	     }
	     catch (Throwable e1) {
	            e1.printStackTrace();
	     }   
      
      long elapsedTimeMillis = System.currentTimeMillis()-start;

      // Get elapsed time in seconds
      float elapsedTimeSec = elapsedTimeMillis/1000F;
      System.out.println("Time :" + elapsedTimeSec);
	 }
      
 	

	/**
	 * Output csv files.
	 *
	 * @param outputDir the output directory
	 *            
	 * @param htmlOutputFlag print html output or not
	 *            
	 * @throws FileNotFoundException
	 */
	private static void outputCsvFiles(String outputDir,Boolean htmlOutputFlag) throws FileNotFoundException
	{	
	 CsvBuilder cb = new CsvBuilder();
	 cb.createCsvFiles(Integer.parseInt(DEFAULT_NUM_TOPICS), outputDir);
	 
	 if(htmlOutputFlag)
	 {
		 HtmlBuilder hb = new HtmlBuilder(cb.getNtd(),new File(DEFAULT_INPUT_DIR));
		 hb.createHtmlFiles(new File(outputDir));
	 }
	 clearExtrafiles(outputDir);
	}

	
	private static void clearExtrafiles(String outputDir)
 	{
 		String[] fileNames = EXTRA_FILES;
 		for(String f:fileNames)
 		{
 			if(!(new File(outputDir,f).canWrite()))
 			{
 	 			//System.out.println(f);
 	 		}
 			new File(outputDir,f).delete();
 		}
 	}
	
}
