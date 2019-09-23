package com.tqrg.cleaner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ParserConfiguration;


import java.util.regex.*;
import java.io.*;

public class Cleaner 
{   
    
    public static void main( String[] args )
    {
        //@TODO: Verify if the .jar exists in the dir
        if (args.length != 2)
        {
            System.err.println("USAGE: java -cp java-parser-comments-remover-1.0-SNAPSHOT-jar-with-dependecies.jar <class_file> <output_file>");
            System.exit(-1);
        }

        if(!validateClass((args[0]).toString())){
            System.err.println("<class_file> should be a .java class");
            System.exit(-1);
        }

        try {
            // @TODO: Check if there is a way of not using 2 FileInputStreams for the same file
            FileInputStream in = new FileInputStream(args[0]);
            CompilationUnit cu = JavaParser.parse(in);
            FileInputStream in2 = new FileInputStream(args[0]);
            File file = new File(args[1]);
			
            BufferedReader br = new BufferedReader(new InputStreamReader(in2));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	        String line = null;
            boolean initialComments = true;
	        
            while ((line = br.readLine()) != null) {
				if(line.toString().trim().contains(cu.getPackageDeclaration().get().toString().trim()) && initialComments)
                    initialComments = false;
                								
                if(initialComments)
                    writer.write("\n");
                
                /**
                    @TODO: This is a workaround for https://github.com/javaparser/javaparser/issues/1574. Try to solve it, and then refactor it.
                 */
                if(!initialComments)
                    if((cu.getComments().toString().trim()).contains(line.toString().trim()) && !line.toString().trim().matches("^([ ]*)\\}")){
                        writer.write("\n");
                    }
                    else if(line.toString().trim().matches("\\/\\*\\*(.*)\\*\\/")) {
                        writer.write("\n");                    }
                    else
                    {
                        writer.write(line+"\n");
                    }
	        }
            writer.close();
	        br.close();
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateClass(String className){
        String regex = "(.*)\\.java";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(className);
        return matcher.find();
    }
}

