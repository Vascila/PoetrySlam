package server.mallet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.CharSequenceRemoveHTML;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;
import database.domain.Poem;

public class PipeLine {
	
	private static final String collectionDir = "C:\\Users\\Lukas\\git\\PoetrySlam\\OtherText\\testLine.txt";
	private static Writer output;
	
	public static void addToPoemCollection(Poem poem) {
		
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequenceRemoveHTML() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        
        InputStream stream = new ByteArrayInputStream(poem.getText().getBytes(StandardCharsets.UTF_8));
        Reader test = new InputStreamReader(stream);
        
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        
        instances.addThruPipe(new CsvIterator (test, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                               3, 2, 1)); // data, label, name fields

        //System.out.println(instances.getDataAlphabet().toString());
        
        try(PrintWriter output = new PrintWriter(new FileWriter(collectionDir,true))) 
        {
        	String endString = "";
        	for(Object token: instances.getAlphabet().toArray())	
        		endString += token + " ";
        	
            output.printf("%s\r\n", poem.getPoemID() + " en " + endString);
        } 
        catch (Exception e) {}
        
//        try {
//			output = new BufferedWriter(new FileWriter(collectionDir, true));
//			output.append(id + " " + instances.getDataAlphabet().toString());
//			System.out.println("Appended");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
//        Writer writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream(OUTPUT_DIR), "utf-8"));
//        writer.write(instances.getDataAlphabet().toString());
//        writer.close();
    }
	
}
